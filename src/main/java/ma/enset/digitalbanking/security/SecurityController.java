package ma.enset.digitalbanking.security;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.dtos.ChangePasswordRequest;
import ma.enset.digitalbanking.dtos.ChangePasswordResponse;
import ma.enset.digitalbanking.dtos.LoginRequest;
import ma.enset.digitalbanking.dtos.LoginResponse;
import ma.enset.digitalbanking.dtos.UserProfile;
import ma.enset.digitalbanking.services.UserManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin("*")
public class SecurityController {
    private JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserManagementService userManagementService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            UserProfile userProfile = new UserProfile(
                    authentication.getName(),
                    authorities,
                    true
            );
            return ResponseEntity.ok(userProfile);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new UserProfile(null, null, false));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            Instant instant = Instant.now();
            long expirationTime = 3600; // 1 hour in seconds

            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String authoritiesString = String.join(" ", authorities);

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(loginRequest.getUsername())
                    .issuedAt(instant)
                    .expiresAt(instant.plusSeconds(expirationTime))
                    .claim("authorities", authoritiesString)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            Jwt jwt = jwtEncoder.encode(jwtEncoderParameters);

            LoginResponse loginResponse = new LoginResponse(
                    jwt.getTokenValue(),
                    loginRequest.getUsername(),
                    authorities,
                    expirationTime
            );

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Since we're using stateless JWT tokens, logout is handled on the client side
        // by removing the token from storage
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ChangePasswordResponse.failure("Authentication required"));
        }

        String username = authentication.getName();
        ChangePasswordResponse response = userManagementService.changePassword(username, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
