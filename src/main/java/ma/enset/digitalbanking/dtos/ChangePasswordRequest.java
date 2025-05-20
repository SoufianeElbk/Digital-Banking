package ma.enset.digitalbanking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)"
    )
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    /**
     * Custom validation to ensure new password and confirmation match
     */
    public boolean isPasswordConfirmationValid() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
    
    /**
     * Check if new password is different from current password
     */
    public boolean isNewPasswordDifferent() {
        return currentPassword != null && newPassword != null && !currentPassword.equals(newPassword);
    }
}
