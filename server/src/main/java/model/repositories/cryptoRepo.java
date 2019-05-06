package model.repositories;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class cryptoRepo {
    private static int iterations = 2000;
    private static int saltLen = 32;
    private static int desiredKeyLen = 256;

    public static String getSaltedHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        /*TODO fix hash*/
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);//*/
       // return password;
    }

    public static boolean checkPassword(String password, String stored) throws NoSuchAlgorithmException, InvalidKeySpecException{
        String[] salt_hash = stored.split("\\$");
        if (salt_hash.length != 2) {
            throw new IllegalStateException("Wrong form. Should be 'salt$hash'.");
        }
        String hashedInput = hash(password, Base64.decodeBase64(salt_hash[0]));
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
