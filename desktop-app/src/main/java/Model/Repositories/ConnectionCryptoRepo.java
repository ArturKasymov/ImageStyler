package Model.Repositories;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static Utils.ServerCommand.INIT;

public class ConnectionCryptoRepo {
    private KeyPairGenerator keyPairGenerator;
    private final Cipher cipherRSA;
    private final Cipher cipherAES;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;
    private GCMParameterSpec parameterSpec;

    private PublicKey serverPublicKey;

    public ConnectionCryptoRepo(int keyGenLength) throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        cipherRSA = Cipher.getInstance("RSA");
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        publicKey= keyPair.getPublic();
        privateKey=keyPair.getPrivate();
        cipherAES = Cipher.getInstance("AES/GCM/NoPadding");
    }

    public void setAES(DataInputStream dis, int AESsize){
        try {
            byte [] settingsBytes =new byte[AESsize];
            dis.readFully(settingsBytes,0,AESsize);
            this.cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
            byte[]temp=cipherAES.doFinal(settingsBytes);
            byte[] key=new byte[16];
            byte[] iv=new byte[12];
            for(int i=0;i<16;i++)key[i]=temp[i];
            for(int i=16;i<28;i++)iv[i-16]=temp[i];
            secretKey = new SecretKeySpec(key, "AES");
            parameterSpec = new GCMParameterSpec(128, iv);
        }catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public void setServerPublicKey(DataInputStream dis){
        try {
            byte[] keyArraySize = new byte[4];
            dis.read(keyArraySize);
            int size = ByteBuffer.wrap(keyArraySize).asIntBuffer().get();
            byte[] keyArray = new byte[size];
            dis.readFully(keyArray, 0, size);
            serverPublicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyArray));
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
        this.cipherRSA.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        return Base64.encodeBase64String(cipherRSA.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptMessage(String msg)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipherRSA.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    public byte[] encryptImage(byte[] input)
            throws GeneralSecurityException {
        cipherAES.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        return this.cipherAES.doFinal(input);
    }

    public BufferedImage decryptImage(DataInputStream dis, int encryptImageSize) throws BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte [] encryptImage =new byte[encryptImageSize];
        dis.readFully(encryptImage,0,encryptImageSize);
        cipherAES.init(Cipher.DECRYPT_MODE,secretKey,parameterSpec);
        return ImageIO.read(new ByteArrayInputStream( this.cipherAES.doFinal(encryptImage)));
    }

}
