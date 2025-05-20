package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private List<String> authorities;
    private long expiresIn;
    
    public LoginResponse(String accessToken, String username, List<String> authorities, long expiresIn) {
        this.accessToken = accessToken;
        this.username = username;
        this.authorities = authorities;
        this.expiresIn = expiresIn;
    }
}
