import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;


public class PixelSorting {

    private BufferedImage img;
    private JFrame frame;
    private int[][] pixels;
    private int x, y;
    private int xBorder = 30, yBorder = 30;

    private boolean setupDone = false;

    public JFrame setup(String path) throws IOException {

        File imgFile = new File(path);
        img = ImageIO.read(imgFile);

        x = img.getWidth();
        y = img.getHeight();

        frame = new JFrame("PixelSorting");
        frame.setSize(x + 2 * xBorder, y + 2 * yBorder);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        frame.add(label);
        //frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setVisible(true);

        pixels = this.getPixels();

        //this.getColors(pixels);

        this.setupDone = true;
        return frame;
    }

    public int[][] getPixels() {

        int[][] result = new int[y][x];
        byte[] bytePixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();    //I read the pixels byte by byte
        boolean hasAlpha = (img.getAlphaRaster() != null);

        if (hasAlpha) {
            final int pixelLenght = 4;

            for (int pixel = 0, row = 0, col = 0; pixel < bytePixels.length; pixel += pixelLenght) {
                int argb = 0;
                argb += (((int) bytePixels[pixel] & 0xff) << 24);       // alpha
                argb += ((int) bytePixels[pixel + 1] & 0xff);           // blue
                argb += (((int) bytePixels[pixel + 2] & 0xff) << 8);    // green
                argb += (((int) bytePixels[pixel + 3] & 0xff) << 16);   // red

                result[row][col] = argb;

                col++;
                if (col >= x) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLenght = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < bytePixels.length; pixel += pixelLenght) {
                int argb = 0;
                argb += -16777216;                                      // 255 alpha
                argb += ((int) bytePixels[pixel] & 0xff);               // blue
                argb += (((int) bytePixels[pixel + 1] & 0xff) << 8);    // green
                argb += (((int) bytePixels[pixel + 2] & 0xff) << 16);   // red

                result[row][col] = argb;

                col++;
                if (col >= x) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }


    private Color[][] getColors(int[][] pixels) {

        int x = pixels[0].length;
        int y = pixels.length;

        Color[][] colorMatrix = new Color[y][x];

        for (int row = 0, col = 0; row < y; ) {

            colorMatrix[row][col] = new Color(pixels[row][col]);

            col++;
            if (col >= x) {
                col = 0;
                row++;
            }
        }
        return colorMatrix;
    }


    public void colorSwap(Color sourceColor, Color newColor, int tolerance) {

        int source = sourceColor.getRGB();
        int newC = newColor.getRGB();

        for (int row = 0, col = 0; row < y; ) {

            /*if (pixels[row][col] < source + tolerance && pixels[row][col] > source - tolerance) {
                pixels[row][col] = newC;
            }*/

            Color current = new Color(pixels[row][col]);
            if (current.getBlue() < sourceColor.getBlue() + tolerance && current.getBlue() > sourceColor.getBlue() - tolerance &&
                    current.getRed() < sourceColor.getRed() + tolerance && current.getRed() > sourceColor.getRed() - tolerance &&
                    current.getGreen() < sourceColor.getGreen() + tolerance && current.getGreen() > sourceColor.getGreen() - tolerance)
                pixels[row][col] = newC;

            col++;
            if (col >= x) {
                col = 0;
                row++;
            }
        }

        img.setRGB(0, 0, x, y, matrixToArray(pixels), 0, x);
        frame.repaint();
    }

    public int[] matrixToArray(int[][] pixelMatrix) {
        int x = pixelMatrix[0].length;
        int y = pixelMatrix.length;

        int[] result = new int[x * y];

        for (int row = 0, col = 0, i = 0; row < y; i++) {
            result[i] = pixelMatrix[row][col];

            col++;
            if (col >= x) {
                col = 0;
                row++;
            }
        }
        return result;
    }


    public static void main(String[] args) {

        PixelSorting instance = new PixelSorting();
        try {
            instance.setup("superga.jpg");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load image \n");
            return;
        }

        for (int i = 0; i < 1000; i++) {
            instance.colorSwap(new Color(207, 207, 207, 255), Color.green, i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
