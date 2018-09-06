import java.awt.*;
import java.util.Arrays;

public class Hilbert {

    private static Color[] pts;
    private static int k = 0;

    private static void pointsGeneration(int n, int density) {
        pts = new Color[(int)(Math.pow(n,3)/ Math.pow(density, 3))];

        //generated all the possible colors in hilbert path order
        hilbert(n, density, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    /**
     * @param range       dimension of the cube (must be power of 2)
     * @param density indicates the minimum distance between two points (set high values for better performances)
     */
    public static void sort(int range, Color[] colors, int density){

        pointsGeneration(range, density);

        customColor[] customColors=new customColor[colors.length];

        int best;
        double bestDist;

        for(int i=0; i<colors.length; i++){
            best=-1;
            bestDist=Float.MAX_VALUE;
            for(int j=0; j<pts.length; j++) {
                if (isSuitable(colors[i], pts[j], density)) {
                    double dist = EDistance(colors[i], pts[j]);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = j;
                    }
                }
            }
            customColors[i] = new customColor(colors[i]);
            customColors[i].closest=best;
            System.out.println(i + " : " + bestDist);
        }

        Arrays.sort(customColors);

        System.arraycopy(customColors, 0, colors, 0, colors.length);

    }

    private static boolean isSuitable(Color color, Color pt, int maxDist) {
        if(Math.abs(color.getRed()-pt.getRed())>maxDist) return false;
        if(Math.abs(color.getGreen()-pt.getGreen())>maxDist) return false;
        if(Math.abs(color.getBlue()-pt.getBlue())>maxDist) return false;

        return true;
    }

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

    static private double EDistance(Color color1, Color color2){
        double x= (Math.pow(color1.getRed()-color2.getRed(), 2));
        double y= (Math.pow(color1.getGreen()-color2.getGreen(), 2));
        double z= (Math.pow(color1.getBlue()-color2.getBlue(), 2));

        return Math.sqrt(x+y+z);
    }

    private static void main(String args[]) {

        Hilbert.pointsGeneration(256, 16);
        

    }


    private static class customColor extends Color implements Comparable<customColor>{

        int closest;

        public customColor(int r, int g, int b){
            super(r,g,b);
        }

        public customColor(Color color){
            super(color.getRGB());
        }

        @Override
        public int compareTo(customColor o) {
            return -(this.closest-o.closest);
        }
    }
}


