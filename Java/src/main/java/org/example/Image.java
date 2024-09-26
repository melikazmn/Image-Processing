package org.example;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Image {
    public BufferedImage bufferedImage;
    public int[][] matrix;

    public void loadImage(String filePath) {
        try {
            bufferedImage = ImageIO.read(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImage(String filePath) {
        int height = matrix.length;
        int width = matrix[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = matrix[y][x];
                bufferedImage.setRGB(x, y, value);
            }
        }
        try {
            File outputFile = new File(filePath);
            ImageIO.write(bufferedImage, "jpg", outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getImageMatrix() {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        matrix = new int[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                matrix[y][x] = bufferedImage.getRGB(x, y);
            }
        }
    }
}