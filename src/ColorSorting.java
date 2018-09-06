import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class ColorSorting {

    private myColor[] colors;

    public ColorSorting(int n) {

        Random r = new Random();
        colors = new myColor[n];


        for (int i = 0; i < n; i++) {
            colors[i] = new myColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        }
    }

    static public Color[] sort(Color[] colors) {
        myColor[] ary = new myColor[colors.length];

        for (int i = 0; i < colors.length; i++) {
            ary[i] = new myColor(colors[i]);
        }

        return sort(ary);

    }

    static public Color[] sort(myColor[] colors) {
        int n = colors.length;

        int[] redSort = new int[n];
        int[] greenSort = new int[n];
        int[] blueSort = new int[n];

        int[] redArray = new int[n];
        int[] greenArray = new int[n];
        int[] blueArray = new int[n];

        for (int i = 0; i < n; i++) {
            redSort[i] = i;
            greenSort[i] = i;
            blueSort[i] = i;

            redArray[i] = colors[i].getRed();
            greenArray[i] = colors[i].getGreen();
            blueArray[i] = colors[i].getBlue();
        }

        mergeSort(redArray, 0, n - 1, redSort);
        mergeSort(greenArray, 0, n - 1, greenSort);
        mergeSort(blueArray, 0, n - 1, blueSort);

        for (int i = 0; i < n; i++) {
            colors[redSort[i]].rank += i;
            colors[greenSort[i]].rank += i;
            colors[blueSort[i]].rank += i;
        }

        Arrays.sort(colors);
        return colors;
    }

    public Color[] sort(){
        return ColorSorting.sort(this.colors);
    }


    static private void mergeSort(int[] colors, int left, int right, int[] res) {
        int center = (left + right) / 2;
        if (left < right) {
            mergeSort(colors, left, center, res);
            mergeSort(colors, center + 1, right, res);
            merge(colors, left, center, right, res);
        }
    }

    static private void merge(int[] colors, int left, int center, int right, int[] res) {
        int i = left;
        int j = center + 1;
        int k = 0;
        int[] b = new int[right - left + 1];

        while (i <= center && j <= right) {
            if (colors[res[i]] <= colors[res[j]]) {
                b[k] = res[i];
                i++;
            } else {
                b[k] = res[j];
                j++;
            }
            k++;
        }

        while (i <= center) {
            b[k] = res[i];
            i++;
            k++;
        }

        while (j <= right) {
            b[k] = res[j];
            j++;
            k++;
        }

        for (k = left; k <= right; k++) {
            res[k] = b[k - left];
        }
    }


    private static void main(String args[]) {

        ColorSorting instance = new ColorSorting(100);
        sort(instance.colors);

    }

    private static class myColor extends Color implements Comparable<myColor> {

        int rank = -1;

        myColor(int r, int g, int b) {
            super(r, g, b);
        }

        myColor(int rgb) {
            super(rgb);
        }
        myColor(Color color) {
            super(color.getRGB());
        }


        @Override
        public int compareTo(myColor o) {
            return (this.rank - o.rank);
        }
    }

}

