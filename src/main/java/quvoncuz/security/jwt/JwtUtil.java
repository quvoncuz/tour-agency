package quvoncuz.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import quvoncuz.enums.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${spring.security.jwt.access-key}")
    private String secretKey;
    private final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000;


    public String encodeAccessToken(String username, Role role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", role.name());
        return Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSignInKey())
                .compact();
    }

    public JwtDTO decodeAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        return JwtDTO
                .builder()
                .username(username)
                .role(Role.valueOf(role))
                .build();
    }

    private SecretKey getSignInKey() {
        byte[] decode = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decode);
    }
}
