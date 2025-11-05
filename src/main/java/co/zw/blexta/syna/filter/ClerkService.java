package co.zw.blexta.syna.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.text.ParseException;

@Slf4j
@Service
public class ClerkService {

    private JWKSource<SecurityContext> jwkSource;
    @Value("CLERK_API_KEY")
    private String apiKey;

    @PostConstruct
    private void init() {
        try {


            ResourceRetriever retriever = new DefaultResourceRetriever() {
                @Override
                public Resource retrieveResource(URL url) throws java.io.IOException {
                    java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                    con.setRequestProperty("Authorization", "Bearer " + apiKey);
                    con.setConnectTimeout(getConnectTimeout());
                    con.setReadTimeout(getReadTimeout());
                    return new Resource(new String(con.getInputStream().readAllBytes()), con.getContentType());

                }
            };

            jwkSource = new RemoteJWKSet<>(new URL("https://promoted-weevil-88.clerk.accounts.dev/.well-known/jwks.json"), retriever);


        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Clerk JWKS", e);
        }
    }

    /**
     * Verify the Clerk JWT token and return the userId (sub)
     */
    public String verifyTokenAndGetUserId(String bearerToken) {
        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid Authorization header");
            }

            String token = bearerToken.substring(7);
            SignedJWT jwt = SignedJWT.parse(token);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(jwt.getHeader().getAlgorithm(), jwkSource);
            jwtProcessor.setJWSKeySelector(keySelector);

            jwtProcessor.process(jwt, null);

            String clerkUserId = jwt.getJWTClaimsSet().getStringClaim("sub");

            if (clerkUserId == null) {
                throw new IllegalArgumentException("Clerk userId not found in token");
            }

            return clerkUserId;

        } catch (ParseException e) {
            log.error("Token parsing failed", e);
            throw new IllegalArgumentException("Invalid token", e);
        } catch (BadJOSEException | JOSEException e) {
            log.error("Token verification failed", e);
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
}
