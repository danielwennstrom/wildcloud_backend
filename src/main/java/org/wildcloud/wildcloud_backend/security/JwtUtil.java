package org.wildcloud.wildcloud_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "FaszTudja123!@#FaszTudja123!@#FaszTudja123!@#";
    private static final long EXPIRATION_Time = 1000 * 60 * 15;

    private final static Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_Time))
                .signWith(key)
                .compact();  // what does compact do?
    }

    public static String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // why JWS
                .getBody()
                .getSubject();
    }

    public static boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !tokenIsExpired(token));
    }

    private static boolean tokenIsExpired(String token) {
        Date ExirationDate = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return ExirationDate.before(new Date());
    }
}
