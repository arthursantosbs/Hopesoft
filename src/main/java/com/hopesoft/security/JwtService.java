package com.hopesoft.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final byte[] secretKey;
    private final long expirationMs;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.objectMapper = objectMapper;
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String generateToken(HopesoftUserDetails userDetails) {
        long nowEpochSeconds = Instant.now().getEpochSecond();
        long expEpochSeconds = Instant.now().plusMillis(expirationMs).getEpochSecond();

        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", userDetails.getUsername());
        payload.put("uid", userDetails.getId());
        payload.put("empresaId", userDetails.getEmpresaId());
        payload.put("nome", userDetails.getNome());
        payload.put("role", userDetails.getPerfil());
        payload.put("iat", nowEpochSeconds);
        payload.put("exp", expEpochSeconds);

        try {
            String encodedHeader = encodeJson(header);
            String encodedPayload = encodeJson(payload);
            String content = encodedHeader + "." + encodedPayload;
            return content + "." + sign(content);
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel gerar o token JWT", exception);
        }
    }

    public String extractUsername(String token) {
        Object subject = parseClaims(token).get("sub");
        if (subject == null) {
            throw new BadCredentialsException("Token JWT sem subject");
        }
        return subject.toString();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Map<String, Object> claims = parseClaims(token);
        Object subject = claims.get("sub");
        if (subject == null || !userDetails.getUsername().equals(subject.toString())) {
            return false;
        }

        Number expiration = asNumber(claims.get("exp"));
        return expiration != null && expiration.longValue() > Instant.now().getEpochSecond();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    private Map<String, Object> parseClaims(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BadCredentialsException("Token JWT malformado");
        }

        String content = parts[0] + "." + parts[1];
        String expectedSignature = sign(content);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8)
        )) {
            throw new BadCredentialsException("Assinatura do token JWT invalida");
        }

        try {
            String payloadJson = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> claims = objectMapper.readValue(payloadJson, MAP_TYPE);
            Number expiration = asNumber(claims.get("exp"));
            if (expiration == null) {
                throw new BadCredentialsException("Token JWT sem expiracao");
            }
            if (expiration.longValue() <= Instant.now().getEpochSecond()) {
                throw new CredentialsExpiredException("Token JWT expirado");
            }
            return claims;
        } catch (BadCredentialsException | CredentialsExpiredException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadCredentialsException("Nao foi possivel ler o token JWT", exception);
        }
    }

    private Number asNumber(Object value) {
        return value instanceof Number number ? number : null;
    }

    private String encodeJson(Object value) throws Exception {
        String json = objectMapper.writeValueAsString(value);
        return BASE64_URL_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] signature = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return BASE64_URL_ENCODER.encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel assinar o token JWT", exception);
        }
    }
}
