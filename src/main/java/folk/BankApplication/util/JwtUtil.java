package folk.BankApplication.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "my-super-secret-key-which-is-long-enough-123456";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateJwtToken(Long userId) {

        long expirationTime = 1000 * 60 * 10;  // 1 минута
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())  // Время создания токена
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .claim("user_id", userId)
                .signWith(key)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
