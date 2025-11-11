package co.zw.blexta.syna.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.BadJWTException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.text.ParseException;

@Slf4j
@Service
public class ClerkService {

    private JWKSource<SecurityContext> jwkSource;

    @PostConstruct
    private void init() {
        try {
            jwkSource = new RemoteJWKSet<>(new URL("https://promoted-weevil-88.clerk.accounts.dev/.well-known/jwks.json"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Clerk JWKS", e);
        }
    }

    public String verifyTokenAndGetUserId(String bearerToken) throws Exception {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = bearerToken.substring(7);
        SignedJWT jwt = SignedJWT.parse(token);

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(jwt.getHeader().getAlgorithm(), jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // Custom claims verifier
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
            if (claims.getSubject() == null) {
                throw new BadJWTException("Missing 'sub' claim in token");
            }
            // optional: ignore expiration for testing
            // throw BadJWTException if you want to enforce exp check
        });

        try {
            jwtProcessor.process(jwt, null);
            return jwt.getJWTClaimsSet().getStringClaim("sub");

        } catch (BadJOSEException | JOSEException e) {
            log.error("Token verification failed", e);
            throw new IllegalArgumentException("Invalid token signature");
        } catch (ParseException e) {
            log.error("Token parsing failed", e);
            throw new IllegalArgumentException("Invalid token format");
        }
    }
}
