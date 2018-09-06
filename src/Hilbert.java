import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class Hilbert {

    private static Color[] pts;
    private static int k = 0;

    private static void pointsGeneration(int n, int density) {
        pts = new Color[(int) (Math.pow(n, 3) / Math.pow(density, 3))];

        long tic = System.currentTimeMillis();
        //generated all the possible colors in hilbert path order
        hilbert(n, density, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        long toc = System.currentTimeMillis();
        System.out.println("Generated " + pts.length + " points, elapsed time: " + (toc - tic) + " milliseconds");
    }

    /**
     * @param range   dimension of the cube (must be power of 2)
     * @param density indicates the minimum distance between two points (set high values for better performances)
     */
    public static int[] sort(int range, Color[] colors, int density) {

        pointsGeneration(range, density);

        customColor[] customColors = new customColor[colors.length];

        int best, sum = 0;
        double bestDist;

        long tic = System.currentTimeMillis();

        for (int i = 0; i < colors.length; i++) {
            best = -1;
            bestDist = Float.MAX_VALUE;
            for (int j = 0; j < pts.length; j++) {
                if (isSuitable(colors[i], pts[j], density)) {
                    double dist = EDistance(colors[i], pts[j]);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = j;
                    }
                }
            }
            customColors[i] = new customColor(colors[i]);
            customColors[i].closest = best;
            customColors[i].position = i;

            sum += bestDist;
            //System.out.println(i + " : " + bestDist);
        }

        long toc = System.currentTimeMillis();

        System.out.println("Found the nearest point for " + colors.length + " points");
        System.out.println("Average distance: " + sum / colors.length + ", elapsed time: " + (toc - tic) + " milliseconds");

        Arrays.sort(customColors);

        System.arraycopy(customColors, 0, colors, 0, colors.length);

        int[] prevPos = new int[colors.length];
        for (int i = 0; i < prevPos.length; i++) {
            prevPos[i] = customColors[i].position;
        }

        return prevPos;
    }

    /**
     * Used to avoid unnecessary calculation of distances
     * The max value the distance can assume is equal to the density used above
     * So I prune every point that can only be farther than that
     */
    private static boolean isSuitable(Color color, Color pt, int maxDist) {
        if (Math.abs(color.getRed() - pt.getRed()) > maxDist) return false;
        if (Math.abs(color.getGreen() - pt.getGreen()) > maxDist) return false;
        if (Math.abs(color.getBlue() - pt.getBlue()) > maxDist) return false;

        return true;
    }

    /**
     * This function is used to calculate the points of the hilbert 3d curve across a 3d nxn cube
     * Credits: https://stackoverflow.com/users/677578/kylefinn
     *
     * @param n       dimension of the cube
     * @param density density of final points of the curve
     * @param x       starting x value
     * @param y       starting y value
     * @param z       starting z value
     * @param dx      x value of the first axis vector
     * @param dy      y value of the first axis vector
     * @param dz      z value of the first axis vector
     * @param dx2     x value of the second axis vector
     * @param dy2     y value of the second axis vector
     * @param dz2     z value of the second axis vector
     * @param dx3     x value of the third axis vector
     * @param dy3     y value of the third axis vector
     * @param dz3     z value of the third axis vector
     */
    static private void hilbert(int n, int density, int x, int y, int z, int dx, int dy, int dz, int dx2, int dy2, int dz2, int dx3, int dy3, int dz3) {
        if (n == density) {
            pts[k] = new Color(x, y, z);
            //System.out.println("added point n. " + k + " " + x + " " + y + " " + z);
            k++;
        } else {
            n /= 2;
            if (dx < 0) x -= n * dx;
            if (dy < 0) y -= n * dy;
            if (dz < 0) z -= n * dz;
            if (dx2 < 0) x -= n * dx2;
            if (dy2 < 0) y -= n * dy2;
            if (dz2 < 0) z -= n * dz2;
            if (dx3 < 0) x -= n * dx3;
            if (dy3 < 0) y -= n * dy3;
            if (dz3 < 0) z -= n * dz3;
            hilbert(n, density, x, y, z, dx2, dy2, dz2, dx3, dy3, dz3, dx, dy, dz);
            hilbert(n, density, x + n * dx, y + n * dy, z + n * dz, dx3, dy3, dz3, dx, dy, dz, dx2, dy2, dz2);
            hilbert(n, density, x + n * dx + n * dx2, y + n * dy + n * dy2, z + n * dz + n * dz2, dx3, dy3, dz3, dx, dy, dz, dx2, dy2, dz2);
            hilbert(n, density, x + n * dx2, y + n * dy2, z + n * dz2, -dx, -dy, -dz, -dx2, -dy2, -dz2, dx3, dy3, dz3);
            hilbert(n, density, x + n * dx2 + n * dx3, y + n * dy2 + n * dy3, z + n * dz2 + n * dz3, -dx, -dy, -dz, -dx2, -dy2, -dz2, dx3, dy3, dz3);
            hilbert(n, density, x + n * dx + n * dx2 + n * dx3, y + n * dy + n * dy2 + n * dy3, z + n * dz + n * dz2 + n * dz3, -dx3, -dy3, -dz3, dx, dy, dz, -dx2, -dy2, -dz2);
            hilbert(n, density, x + n * dx + n * dx3, y + n * dy + n * dy3, z + n * dz + n * dz3, -dx3, -dy3, -dz3, dx, dy, dz, -dx2, -dy2, -dz2);
            hilbert(n, density, x + n * dx3, y + n * dy3, z + n * dz3, dx2, dy2, dz2, -dx3, -dy3, -dz3, -dx, -dy, -dz);
        }

    }

    static private double EDistance(Color color1, Color color2) {
        double x = (Math.pow(color1.getRed() - color2.getRed(), 2));
        double y = (Math.pow(color1.getGreen() - color2.getGreen(), 2));
        double z = (Math.pow(color1.getBlue() - color2.getBlue(), 2));

        return Math.sqrt(x + y + z);
    }

    public static void main(String args[]) {

        //Hilbert.pointsGeneration(256, 16);
        Random r = new Random();
        int n = 1000;
        Color[] colors = new Color[n];

        for (int i = 0; i < n; i++) {
            colors[i] = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        }

        JFrame frame = new JFrame("Test Hilber Color Sorting");

        frame.setSize(1200, 300);

        BufferedImage img1 = new BufferedImage(n, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(n, 100, BufferedImage.TYPE_INT_RGB);

        ImageIcon icon1 = new ImageIcon(img1);
        ImageIcon icon2 = new ImageIcon(img2);
        JLabel label1 = new JLabel(icon1);
        JLabel label2 = new JLabel(icon2);

        frame.add(label1);
        frame.add(label2);

        frame.setLayout(new FlowLayout());
        //frame.getContentPane().add(new JLabel(new ImageIcon(img1)));*/
        frame.setVisible(true);

        for (int i = 0; i < n; i++) {
            int[] color = new int[100];
            Arrays.fill(color, colors[i].getRGB());
            img1.setRGB(i, 0, 1, 100, color, 0, 1);
        }

        sort(256, colors, 16);
        //colors=ColorSorting.sort(colors);

        for (int i = 0; i < n; i++) {
            int[] color = new int[100];
            Arrays.fill(color, colors[i].getRGB());
            img2.setRGB(i, 0, 1, 100, color, 0, 1);
        }

        frame.repaint();
    }


    private static class customColor extends Color implements Comparable<customColor> {

        int closest;
        int position;

        public customColor(int r, int g, int b) {
            super(r, g, b);
        }

        public customColor(Color color) {
            super(color.getRGB());
        }

        @Override
        public int compareTo(customColor o) {
            return -(this.closest - o.closest);
        }
    }
}


