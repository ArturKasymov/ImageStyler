package model.repositories.PythonGeneration;

import server.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Scanner;

public class PySqueezeNet {

    public static BufferedImage generate(BufferedImage contentImage, BufferedImage styleImage){
        URL url = Main.class.getResource("/PyGenerationRepo/Main.py");
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", url.toString().substring(6));
            Process p = pb.start();
            // PASS INPUT IMAGES
            BufferedWriter inputImagesStream = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            // CONTENT IMAGE
            int contHeight = contentImage.getHeight();
            int contWidth = contentImage.getWidth();
            System.out.println("Content shape - (" + contHeight + ", " + contWidth + ")");
            int[] contPixelsBytes = ((DataBufferInt) contentImage.getRaster().getDataBuffer()).getData();
            inputImagesStream.write(contHeight + " " + contWidth);
            inputImagesStream.write("\n");
            /*for (int j = 0; j < contHeight; j++) {
                for (int i = 0; i < contWidth; i++) {
                    System.out.println(contPixelsBytes[i+j*contWidth]);
                    inputImagesStream.write(String.valueOf(contPixelsBytes[i+j*contWidth]));
                    inputImagesStream.write("\n");
                }
            }
            // STYLE IMAGE
            int styleHeight = styleImage.getHeight();
            int styleWidth = styleImage.getWidth();
            int[] stylePixelsBytes = ((DataBufferInt) styleImage.getRaster().getDataBuffer()).getData();
            inputImagesStream.write(styleHeight + " " + styleWidth);
            System.out.println("Style shape - (" + styleHeight + ", " + styleWidth + ")");
            inputImagesStream.write("\n");
            for (int j = 0; j < styleHeight; j++) {
                for (int i = 0; i < styleWidth; i++) {
                    inputImagesStream.write(String.valueOf(stylePixelsBytes[i+j*styleWidth]));
                    inputImagesStream.write("\n");
                }
            }*/
            inputImagesStream.close();
            // GET GENERATED IMAGE
            InputStream generatedImageStream = p.getInputStream();
            Scanner imgSc = new Scanner(generatedImageStream);
            while (true) {
                while (!imgSc.hasNext());
                System.out.println(imgSc.nextLine());
            }
            /*BufferedImage img;
            while (true) {
                while (!imgSc.hasNext());
                String line = imgSc.nextLine();
                if (line.charAt(0)!='I') {
                    System.out.println(line);
                    String[] spl = line.split(",");
                    int height = Integer.parseInt(spl[0].substring(1));
                    int width = Integer.parseInt(spl[1].substring(1));
                    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            spl = imgSc.nextLine().split(" ");
                            int red = Integer.parseInt(spl[0]);
                            int green = Integer.parseInt(spl[1]);
                            int blue = Integer.parseInt(spl[2]);

                            img.setRGB(j, i, new Color(red, green, blue).getRGB());
                        }
                    }
                    break;
                }
            }
            p.destroy();
            return img;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentImage;
    }
}
