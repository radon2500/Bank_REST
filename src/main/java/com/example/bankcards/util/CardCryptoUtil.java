package com.example.bankcards.util;

import com.example.bankcards.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.SecureRandom;

@Component
public class CardCryptoUtil {
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecretKeySpec keySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public CardCryptoUtil(@Value("${app.crypto.secret}") String base64Secret) {
        byte[] key = Base64.getDecoder().decode(base64Secret);
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new BusinessException("CARD_CRYPTO_SECRET must be 16/24/32 bytes in Base64");
        }
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String value) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            String ivEncoded = Base64.getEncoder().encodeToString(iv);
            String payloadEncoded = Base64.getEncoder().encodeToString(ciphertext);
            return ivEncoded + ":" + payloadEncoded;
        } catch (Exception ex) {
            throw new BusinessException("Unable to encrypt card number");
        }
    }
}
