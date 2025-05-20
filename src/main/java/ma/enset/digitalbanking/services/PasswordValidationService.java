package ma.enset.digitalbanking.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidationService {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    
    // Password strength patterns
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&].*");
    
    /**
     * Validates password strength and returns validation result
     */
    public PasswordValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password cannot be empty");
            return new PasswordValidationResult(false, 0, errors, suggestions);
        }
        
        // Length validation
        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
            suggestions.add("Add more characters to reach minimum length");
        }
        
        if (password.length() > MAX_LENGTH) {
            errors.add("Password must not exceed " + MAX_LENGTH + " characters");
        }
        
        // Character type validation
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one lowercase letter");
            suggestions.add("Add lowercase letters (a-z)");
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter");
            suggestions.add("Add uppercase letters (A-Z)");
        }
        
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one number");
            suggestions.add("Add numbers (0-9)");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one special character (@$!%*?&)");
            suggestions.add("Add special characters (@$!%*?&)");
        }
        
        // Calculate password strength score
        int strength = calculatePasswordStrength(password);
        
        boolean isValid = errors.isEmpty();
        
        return new PasswordValidationResult(isValid, strength, errors, suggestions);
    }
    
    /**
     * Calculates password strength score (0-100)
     */
    private int calculatePasswordStrength(String password) {
        int score = 0;
        
        // Length score (max 25 points)
        if (password.length() >= MIN_LENGTH) {
            score += Math.min(25, password.length() * 2);
        }
        
        // Character variety score (max 75 points)
        if (LOWERCASE_PATTERN.matcher(password).matches()) score += 15;
        if (UPPERCASE_PATTERN.matcher(password).matches()) score += 15;
        if (DIGIT_PATTERN.matcher(password).matches()) score += 15;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) score += 15;
        
        // Bonus for length > 12
        if (password.length() > 12) score += 15;
        
        return Math.min(100, score);
    }
    
    /**
     * Gets password strength description
     */
    public String getPasswordStrengthDescription(int strength) {
        if (strength < 30) return "Very Weak";
        if (strength < 50) return "Weak";
        if (strength < 70) return "Fair";
        if (strength < 85) return "Good";
        return "Strong";
    }
    
    /**
     * Password validation result class
     */
    public static class PasswordValidationResult {
        private final boolean valid;
        private final int strength;
        private final List<String> errors;
        private final List<String> suggestions;
        
        public PasswordValidationResult(boolean valid, int strength, List<String> errors, List<String> suggestions) {
            this.valid = valid;
            this.strength = strength;
            this.errors = errors;
            this.suggestions = suggestions;
        }
        
        public boolean isValid() { return valid; }
        public int getStrength() { return strength; }
        public List<String> getErrors() { return errors; }
        public List<String> getSuggestions() { return suggestions; }
    }
}
