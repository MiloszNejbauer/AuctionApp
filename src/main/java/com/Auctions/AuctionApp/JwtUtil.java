package com.Auctions.AuctionApp;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET = "mojTajnySekretnyKluczJwt123456789012345"; // 32+ znaki!
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private final long expiration = 1000 * 60 * 60 * 24; // 24h

    // 🔐 Generowanie tokenu
    public String generateToken(String userEmail) {
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // 🔍 Ekstrakcja emaila (czyli username)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🧠 Pomocnicze metody
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // usuwa "Bearer "
            }

            String cleanedToken = token.replaceAll("\\s+", "");
            System.out.println("🔍 Token przed parsowaniem: '" + cleanedToken + "'");

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(cleanedToken)
                    .getBody();

        } catch (Exception e) {
            System.err.println("❌ Błąd parsowania tokena: '" + token + "'");
            e.printStackTrace();
            throw e;
        }
    }



    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
        return expiration.before(new Date());
    }

}
