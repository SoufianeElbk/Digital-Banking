package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.ChangePasswordRequest;
import ma.enset.digitalbanking.dtos.ChangePasswordResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Slf4j
public class UserManagementService {
    
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    
    // Rate limiting for password changes (username -> attempt count)
    private final ConcurrentHashMap<String, AttemptInfo> passwordChangeAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS_PER_HOUR = 5;
    
    /**
     * Changes user password with validation and security checks
     */
    public ChangePasswordResponse changePassword(String username, ChangePasswordRequest request) {
        try {
            log.info("Password change attempt for user: {}", username);
            
            // Rate limiting check
            if (isRateLimited(username)) {
                log.warn("Rate limit exceeded for password change attempt by user: {}", username);
                return ChangePasswordResponse.failure(
                    "Too many password change attempts. Please try again later."
                );
            }
            
            // Validate request
            if (!request.isPasswordConfirmationValid()) {
                recordFailedAttempt(username);
                return ChangePasswordResponse.failure("New password and confirmation do not match");
            }
            
            if (!request.isNewPasswordDifferent()) {
                recordFailedAttempt(username);
                return ChangePasswordResponse.failure("New password must be different from current password");
            }
            
            // Validate new password strength
            PasswordValidationService.PasswordValidationResult validationResult = 
                passwordValidationService.validatePassword(request.getNewPassword());
            
            if (!validationResult.isValid()) {
                recordFailedAttempt(username);
                String errorMessage = "Password does not meet requirements: " + 
                    String.join(", ", validationResult.getErrors());
                return ChangePasswordResponse.failure(errorMessage);
            }
            
            // Verify current password
            UserDetails userDetails;
            try {
                userDetails = userDetailsManager.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                log.error("User not found during password change: {}", username);
                recordFailedAttempt(username);
                return ChangePasswordResponse.failure("User not found");
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
                log.warn("Invalid current password provided by user: {}", username);
                recordFailedAttempt(username);
                return ChangePasswordResponse.failure("Current password is incorrect");
            }
            
            // Update password
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
            
            // Create updated user with new password
            UserDetails updatedUser = User.withUsername(username)
                .password(encodedNewPassword)
                .authorities(userDetails.getAuthorities())
                .build();
            
            // Update user in memory store
            userDetailsManager.updateUser(updatedUser);
            
            // Clear failed attempts on successful change
            passwordChangeAttempts.remove(username);
            
            log.info("Password successfully changed for user: {}", username);
            
            return ChangePasswordResponse.success(username);
            
        } catch (Exception e) {
            log.error("Error changing password for user: {}", username, e);
            recordFailedAttempt(username);
            return ChangePasswordResponse.failure("An error occurred while changing password. Please try again.");
        }
    }
    
    /**
     * Checks if user is rate limited for password changes
     */
    private boolean isRateLimited(String username) {
        AttemptInfo attemptInfo = passwordChangeAttempts.get(username);
        if (attemptInfo == null) {
            return false;
        }
        
        // Reset counter if more than an hour has passed
        if (attemptInfo.getLastAttempt().isBefore(LocalDateTime.now().minusHours(1))) {
            passwordChangeAttempts.remove(username);
            return false;
        }
        
        return attemptInfo.getCount().get() >= MAX_ATTEMPTS_PER_HOUR;
    }
    
    /**
     * Records a failed password change attempt
     */
    private void recordFailedAttempt(String username) {
        passwordChangeAttempts.compute(username, (key, attemptInfo) -> {
            if (attemptInfo == null) {
                return new AttemptInfo(new AtomicInteger(1), LocalDateTime.now());
            } else {
                attemptInfo.getCount().incrementAndGet();
                attemptInfo.setLastAttempt(LocalDateTime.now());
                return attemptInfo;
            }
        });
    }
    
    /**
     * Gets password validation result for frontend
     */
    public PasswordValidationService.PasswordValidationResult validatePasswordStrength(String password) {
        return passwordValidationService.validatePassword(password);
    }
    
    /**
     * Inner class to track password change attempts
     */
    private static class AttemptInfo {
        private final AtomicInteger count;
        private LocalDateTime lastAttempt;
        
        public AttemptInfo(AtomicInteger count, LocalDateTime lastAttempt) {
            this.count = count;
            this.lastAttempt = lastAttempt;
        }
        
        public AtomicInteger getCount() { return count; }
        public LocalDateTime getLastAttempt() { return lastAttempt; }
        public void setLastAttempt(LocalDateTime lastAttempt) { this.lastAttempt = lastAttempt; }
    }
}
