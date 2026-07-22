package com.finpay.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.jwt.referesh-expiration-milliseconds}")
    private long jwtRefreshExpirationDate;



    /**
     * Converts your String secret from application.properties into a cryptographic SecretKey
     */
    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }



    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Generates a short-lived Access Token with custom claims
     */

    /**
     The .compact() method converts your configured JWT builder object into its final, usable string format.It performs three critical steps in the background to assemble the final token:
     1. Serializes to JSONIt takes your token metadata (the header) and your custom claims (the payload) and converts them from Java objects into raw JSON strings.
     2. Encodes to Base64URLIt converts the JSON header and JSON payload into Base64URL-encoded strings. This removes unsafe characters so the token can be safely sent over the internet in HTTP headers or URLs.
     3. Computes the Cryptographic SignatureIt takes the encoded header and payload, signs them using the secret key and algorithm you provided (via .signWith()), and encodes that signature into Base64URL.
     4. Joins with DotsIt joins the three encoded pieces together using periods (.) as delimiters. The final output is the standard three-part string you see in production: header.payload.signature.
     */
    public String generateAccessToken(String email, UUID userId, String role)
    {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(email)
                .claim("userId",userId.toString())
                .claim("role", role)
                .claim("tokenType", "ACCESS")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String generateRefereshToken(String email, UUID userId){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .claim("tokenType", "REFERESH")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    /**
     The Jwts.parser() method creates a parser instance used to read, verify, and decode a JSON Web Token (JWT) string back into its original Java objects.It reverses the .compact() process through three main steps:
     1. Cryptographic VerificationIt reads the token's header and payload, cryptographically recomputes the signature using your provided signing key, and compares it to the token's signature. If the token was altered or tampered with, it throws a SignatureException.
     2. Validation ChecksIt automatically checks the standard claims in the payload against the current time. If the token's expiration time (exp) has passed, or if its "not before" time (nbf) hasn't arrived yet, it throws an ExpiredJwtException or PrematureJwtException.
     3. Base64URL Decoding & ParsingOnce verified, it decodes the Base64URL string segments and deserializes the raw JSON header and payload back into readable Java structures, specifically a Claims object.
     */
    public String getTokenType(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("tokenType", String.class);
    }
    /**
     * Verifies that the token hasn't been tampered with and hasn't expired
     */
    public boolean validateToken(String token)
    {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        }
        return false;

    }
}
