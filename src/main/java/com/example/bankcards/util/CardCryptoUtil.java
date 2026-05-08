package com.example.bankcards.util;

import com.example.bankcards.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CardCryptoUtil {

    private final SecretKeySpec keySpec;

    public CardCryptoUtil(@Value("${app.crypto.secret}") String base64Secret) {
        byte[] key = Base64.getDecoder().decode(base64Secret);
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new BusinessException("Unable to encrypt card number");
        }
    }
}
