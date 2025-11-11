package co.zw.blexta.syna.filter;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/clerk")
public class ClerkController {

    private final ClerkService clerkService;

    public ClerkController(ClerkService clerkService) {
        this.clerkService = clerkService;
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Missing Authorization header"
            ));
        }

        try {
            String userId = clerkService.verifyTokenAndGetUserId(bearerToken);

            // Optional: parse the token to return more claims if needed
            SignedJWT jwt = SignedJWT.parse(bearerToken.substring(7));
            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "userId", userId,
                    "claims", claims
            ));

        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to parse token"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
