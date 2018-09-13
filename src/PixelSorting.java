import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class PixelSorting {

    private BufferedImage img1, img2;
    private JFrame frame;
    private int[][] pixels;
    private int x, y;
    private JLabel label1;
    private JLabel label2;
    int yBorder = 30;
    int xBorder = 15;
    private boolean setupDone = false;

    private JFrame setup(String path) throws IOException {

        File imgFile = new File(path);
        img1 = ImageIO.read(imgFile);
        x = img1.getWidth();
        y = img1.getHeight();
        img2 = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        fill(img2, Color.white);

        frame = new JFrame("PixelSorting: " + path);
        frame.setSize(x * 2 + 2 * xBorder, y + 2 * yBorder);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ImageIcon icon1 = new ImageIcon(img1);
        ImageIcon icon2 = new ImageIcon(img2);
        label1 = new JLabel(icon1);
        label2 = new JLabel(icon2);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(label1);
        panel.add(label2);

        frame.add(panel);


        //frame.setLayout(new FlowLayout());
        //frame.getContentPane().add(new JLabel(new ImageIcon(img1)));
        frame.setVisible(true);

        pixels = this.getPixels();

        //this.getColors(pixels);

        this.setupDone = true;
        return frame;
    }

    private void fill(BufferedImage img2, Color color) {
        int[] pixels = new int[img2.getHeight() * img2.getWidth()];
        Arrays.fill(pixels, color.getRGB());

        img2.setRGB(0, 0, img2.getWidth(), img2.getHeight(), pixels, 0, img2.getWidth());
    }

    private int[][] getPixels() {

        int[][] result = new int[y][x];
        byte[] bytePixels = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();    //I read the pixels byte by byte
        boolean hasAlpha = (img1.getAlphaRaster() != null);

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

    private Color[] getColors(int[] pixels) {
        int x = pixels.length;
        Color[] result = new Color[x];

        for (int i = 0; i < x; i++) {
            result[i] = new Color(pixels[i]);
        }

        return result;
    }

    public void colorSwap(Color sourceColor, Color newColor, int tolerance) {

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

        img1.setRGB(0, 0, x, y, matrixToArray(pixels), 0, x);
        frame.repaint();
    }

    private void pixelSort() {

        int[] pixelArray = this.matrixToArray(pixels);

        Color[] colorArray = getColors(pixelArray);
        //Color[] oldColors = new Color[colorArray.length];
        //System.arraycopy(colorArray, 0, oldColors, 0, oldColors.length);

        //colorArray = ColorSorting.sort(colorArray);
        int[] prevPos = Hilbert.sort(256, colorArray, 8);

        long tic = System.currentTimeMillis();

        for (int i = 0; i < colorArray.length; i++) {
            pixelArray[i] = colorArray[i].getRGB();
        }

        while (true) {
            for (int i = 0; i < pixelArray.length; i++) {
                img1.setRGB(prevPos[i] % x, prevPos[i] / x, Color.WHITE.getRGB());
                img2.setRGB(i % x, i / x, pixelArray[i]);
                frame.repaint();


                if (i % 4 == 0) {
                    drawLine(prevPos[i], i, x, frame, label1, label2, pixelArray[i]);
                }

                //sleep     (better than TimeUnit.NANOSECONDS.sleep)
                //long before=System.nanoTime();
                //while(before+10000>System.nanoTime());

            }
            long toc = System.currentTimeMillis();
            System.out.println(pixelArray.length + " points moved, elapsed time: " + (toc - tic) + " milliseconds");

            for (int i = pixelArray.length - 1; i >= 0; i--) {
                img2.setRGB(i % x, i / x, Color.WHITE.getRGB());
                img1.setRGB(prevPos[i] % x, prevPos[i] / x, pixelArray[i]);
                frame.repaint();


                if (i % 4 == 0) {
                    drawLine(prevPos[i], i, x, frame, label1, label2, pixelArray[i]);
                }

                //long before=System.nanoTime();
                //while(before+10000>System.nanoTime());
            }
            //frame.repaint();
        }
        //SLOW ALTERNATIVE
        /*for (int i = 0; i < pixelArray.length; i++) {

            int toTake=pixelArray[i];
            int j=0;
            while(oldColors[j].getRGB()!=toTake)
                j++;
            img1.setRGB(j%x, j/x, Color.WHITE.getRGB());
            img2.setRGB(i%x, i/x, pixelArray[i]);
            oldColors[j]=Color.WHITE;
            System.out.println(i + " pixels moved");
            frame.repaint();
        }*/

        //img2.setRGB(0, 0, x, y, pixelArray, 0, x);
    }

    private int[] matrixToArray(int[][] pixelMatrix) {
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

    private void drawLine(int startIndex, int endIndex, int width, JFrame frame, JLabel label1, JLabel label2, int color) {


        int yOffset = 37;

        Graphics g = frame.getGraphics();
        int x1 = label1.getX() + startIndex % width;
        int y1 = label1.getY() + startIndex / width + yOffset;
        int x2 = label2.getX() + endIndex % width;
        int y2 = label2.getY() + endIndex / width + yOffset;

        Color lineColor = new Color(color);

        g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 128));
        g.drawLine(x1, y1, x2, y2);

    }

    public static void main(String[] args) {

        PixelSorting instance = new PixelSorting();
        String path="superga.jpg";
        try {
            instance.setup(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load image \n");
            JOptionPane.showMessageDialog(null, path + " not found in this folder, please rename the image",
                    "Failed to load image", JOptionPane.ERROR_MESSAGE);
            return;
        }

       /* for (int i = 0; i < 1000; i++) {
            instance.colorSwap(new Color(207, 207, 207, 255), new Color(67, 255, 90, 255), i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        instance.pixelSort();
    }
}