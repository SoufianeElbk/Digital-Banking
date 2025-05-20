package ma.enset.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String username;
    private List<String> authorities;
    private boolean authenticated;
}
