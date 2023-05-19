package com.osce.eprocurementmonitorbackend.security.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class Utils {

    private static final String ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 256;

    public static String encrypt(String secretKey, String valueToEncrypt) throws Exception {
        // Generate SHA-512 hash of key
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] keyHash = sha512.digest(secretKey.getBytes(StandardCharsets.UTF_8));
        // Use first 256 bits of hash as AES key
        byte[] aesKey = Arrays.copyOf(keyHash, AES_KEY_SIZE/8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedValueBytes = cipher.doFinal(valueToEncrypt.getBytes());
        return Base64.encodeBase64String(encryptedValueBytes);
    }

    public static String decrypt(String secretKey, String encryptedValue) throws Exception {
        // Generate SHA-512 hash of key
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] keyHash = sha512.digest(secretKey.getBytes(StandardCharsets.UTF_8));
        // Use first 256 bits of hash as AES key
        byte[] aesKey = Arrays.copyOf(keyHash, AES_KEY_SIZE/8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedValueBytes = cipher.doFinal(Base64.decodeBase64(encryptedValue));
        return new String(decryptedValueBytes);
    }

    public static String getPDFHash(InputStream fis) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        // Create a buffer to read the file contents in chunks
        byte[] buffer = new byte[1024];
        int bytesRead;

        // Read the file contents and update the MessageDigest with each chunk
        while ((bytesRead = fis.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
        }

        // Get the hash value as an array of bytes
        byte[] hashBytes = messageDigest.digest();

        // Convert the hash bytes to a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

}
