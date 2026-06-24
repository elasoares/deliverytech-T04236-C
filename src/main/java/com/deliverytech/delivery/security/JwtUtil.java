package com.deliverytech.delivery.security;

import com.deliverytech.delivery.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtUtil {
    private static final String SECRET_KEY = "F$7v9B#2pX!kL9wQ@4zR1tY*6mN3bV5cZ8jA0xH2nC4vB6m";

    private static  final long EXPIRATION = 24 * 60 * 60 * 1000L;

    private Key getSignKey (){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(Usuario usuario){
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("UserId", usuario.getId())
                .claim("role", usuario.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        Date expiration = extractClaim(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, String email){
        try{
            Claims claims = extractClaim(token);
            return claims.getSubject().equals(email) && !isTokenExpired(token);
        }catch (Exception e){
            return false;
        }

    }

    public String extractRole (String token){
        return extractClaim(token).get("role", String.class);
    }

    public Claims extractClaim(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }
}
