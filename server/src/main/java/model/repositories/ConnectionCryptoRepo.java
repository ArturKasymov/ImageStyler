package model.repositories;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static util.ServerCommand.INIT;

public class ConnectionCryptoRepo {
    private KeyPairGenerator keyPairGenerator;
    private Cipher cipher;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private PublicKey clientPublicKey;

    public ConnectionCryptoRepo(int keyGenLength) throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        cipher = Cipher.getInstance("RSA");
        keyPairGenerator.initialize(keyGenLength);
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        publicKey= keyPair.getPublic();
        privateKey=keyPair.getPrivate();
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
        this.cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptMessage(String msg)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    public byte[] encryptImage(byte[] input)
            throws GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        return this.cipher.doFinal(input);
    }

    public BufferedImage decryptImage(DataInputStream dis, int encryptImageSize) throws BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeyException {
        byte [] encryptImage =new byte[encryptImageSize];
        dis.readFully(encryptImage,0,encryptImageSize);
        this.cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return ImageIO.read(new ByteArrayInputStream( this.cipher.doFinal(encryptImage)));
    }

}
