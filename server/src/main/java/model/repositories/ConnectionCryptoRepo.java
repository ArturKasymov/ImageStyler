package model.repositories;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static util.ServerCommand.AES;
import static util.ServerCommand.INIT;

public class ConnectionCryptoRepo {
    private KeyPairGenerator keyPairGenerator;
    private final Cipher cipherRSA;
    private final Cipher cipherAES;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private PublicKey clientPublicKey;
    private SecureRandom secureRandom=new SecureRandom();
    private byte[] keyAES=new byte[16];

    public ConnectionCryptoRepo(int keyGenLength) throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        cipherRSA = Cipher.getInstance("RSA");
        keyPairGenerator.initialize(keyGenLength);
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        publicKey= keyPair.getPublic();
        privateKey=keyPair.getPrivate();

        secureRandom.nextBytes(keyAES);
        cipherAES = Cipher.getInstance("AES/GCM/NoPadding");
    }

    public void sendAESsettings(DataOutputStream dos){
        synchronized (dos){
            try {
                this.cipherRSA.init(Cipher.ENCRYPT_MODE, clientPublicKey);
                byte input[]=cipherRSA.doFinal(keyAES);
                dos.writeUTF(encryptMessage(AES+ " "+input.length));
                dos.write(input);
                dos.flush();
            } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClientPublicKey(DataInputStream dis){
        try {
            byte[] keyArraySize = new byte[4];
            dis.read(keyArraySize);
            int size = ByteBuffer.wrap(keyArraySize).asIntBuffer().get();
            byte[] keyArray = new byte[size];
            dis.readFully(keyArray, 0, size);
            clientPublicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyArray));
        }catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicKey(DataOutputStream outputStream){
        synchronized (outputStream){
            try {
                outputStream.writeUTF(INIT);
                byte[] userImageSize = ByteBuffer.allocate(4).putInt(publicKey.getEncoded().length).array();
                outputStream.write(userImageSize);
                outputStream.write(publicKey.getEncoded());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String encryptMessage(String msg) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.cipherRSA.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        return Base64.encodeBase64String(cipherRSA.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptMessage(String msg)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipherRSA.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    public byte[] encryptBytes(byte[] input)
            throws GeneralSecurityException {
        byte[] iv = null;
        byte[] encrypted = null;
        try {
            iv = new byte[12];
            secureRandom.nextBytes(iv);
            cipherAES.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyAES,"AES"), new GCMParameterSpec(128, iv));
            encrypted = cipherAES.doFinal(input);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encrypted.length);
            byteBuffer.put((byte) iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    public byte[] decryptBytes(DataInputStream dis, int encryptBytesSize) throws BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte [] encryptBytes =new byte[encryptBytesSize];
        dis.readFully(encryptBytes,0,encryptBytesSize);
        byte[] iv = null;
        byte[] encrypted = null;
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptBytes);
            int ivLength = byteBuffer.get();
            iv = new byte[ivLength];
            byteBuffer.get(iv);
            encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);
            cipherAES.init(Cipher.DECRYPT_MODE,new SecretKeySpec(keyAES, "AES"),new GCMParameterSpec(128, iv));
            return cipherAES.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptBytes;
    }

}
