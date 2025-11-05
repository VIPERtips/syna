package co.zw.blexta.syna.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.net.URL;
import java.text.ParseException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkAuthenticationFilter extends OncePerRequestFilter {

    private final JWKSource<SecurityContext> jwkSource = initJwkSource();

    private static JWKSource<SecurityContext> initJwkSource() {
        try {
            return new RemoteJWKSet<>(new URL("https://api.clerk.dev/v1/jwks"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Clerk JWKS", e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(jwt.getHeader().getAlgorithm(), jwkSource);
            jwtProcessor.setJWSKeySelector(keySelector);

            jwtProcessor.process(jwt, null);

            String clerkUserId = jwt.getJWTClaimsSet().getStringClaim("sub");

            if (clerkUserId != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(clerkUserId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (ParseException e) {
            System.out.println("Token parsing failed: " + e.getMessage());
        } catch (BadJOSEException e) {
            System.out.println("Bad JOSE token: " + e.getMessage());
        } catch (JOSEException e) {
            System.out.println("JOSE verification error: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}
