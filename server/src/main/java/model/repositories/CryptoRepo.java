package model.repositories;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class CryptoRepo {
    private static final int iterations = 500;
    private static final int  saltLen = 32;
    private static final int desiredKeyLen = 256;

    public static String getSaltedHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("start hash");
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        System.out.println(salt);
        //TODO fix hash
        return password;
        //return Base64.encodeBase64String(salt) + "$" + hash(password, salt);//*/
    }

    public static boolean checkPassword(String password, String stored){
        String[] salt_hash = stored.split("\\$");
        if (salt_hash.length != 2) {
            throw new IllegalStateException("Wrong form. Should be 'salt$hash'.");
        }
        String hashedInput;
        try {
            hashedInput = hash(password, Base64.decodeBase64(salt_hash[0]));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
        return hashedInput.equals(salt_hash[1]);
    }

    public static String hash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password==null || password.length()==0) {
            throw new IllegalArgumentException("Empty password!");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return Base64.encodeBase64String(key.getEncoded());
    }
}
