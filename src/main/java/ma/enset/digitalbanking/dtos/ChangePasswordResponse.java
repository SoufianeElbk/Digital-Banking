package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordResponse {
    
    private boolean success;
    private String message;
    private String username;
    private LocalDateTime timestamp;
    
    public static ChangePasswordResponse success(String username) {
        return new ChangePasswordResponse(
            true, 
            "Password changed successfully. Please log in again with your new password.", 
            username, 
            LocalDateTime.now()
        );
    }
    
    public static ChangePasswordResponse failure(String message) {
        return new ChangePasswordResponse(
            false, 
            message, 
            null, 
            LocalDateTime.now()
        );
    }
}
