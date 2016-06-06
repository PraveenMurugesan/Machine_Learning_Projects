//package ml_hw3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class KMeans {

    public void compressImage(String inputImageFile, String outputFolder, int n) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(inputImageFile)); 
            Scanner s = new Scanner(System.in);
            for (int i = 0; i < n; i++) {
                System.out.println("Enter K value: ");
                int k = s.nextInt();
                BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
                ImageIO.write(kmeansJpg, "jpg", new File(outputFolder + "\\K=" + k + "_Koala.jpg"));
                kmeansJpg.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    private static int calculateDistance(int rgbValue, int centroidValue) {

        Color point = new Color(rgbValue);
        Color centroid = new Color(centroidValue);
        double blue = Math.pow((point.getBlue() - centroid.getBlue()), 2);
        double red = Math.pow((point.getRed() - centroid.getRed()), 2);
        double green = Math.pow((point.getGreen() - centroid.getGreen()), 2);
        return (int) Math.sqrt((blue + red + green));
    }

    private static void computeCentroid(int rgb[], int[] mu,int clusterNumberForPoints[], int k) {
        for (int j = 0; j < k; j++) {
            int[] s = new int[3];
            int numberOfPointsInCluster = 0;
            for (int i = 0; i < rgb.length; i++) {
                if (clusterNumberForPoints[i] == j) { 
                    int colorValue = rgb[i];
                    Color color = new Color(colorValue);
                    s[0] = s[0] + (color.getRed());
                    s[1] = s[1] + (color.getBlue());
                    s[2] = s[2] + (color.getGreen());
                    numberOfPointsInCluster++;
                }
            }

            if (numberOfPointsInCluster != 0) {
                Color color = new Color(s[0] / numberOfPointsInCluster, s[1] / numberOfPointsInCluster, s[2] / numberOfPointsInCluster);
                mu[j] = color.getRGB();
            }
        }

    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k) {

        int mu[] = new int[k];
        Random random = new Random();
        boolean dummyHash[] = new boolean[rgb.length];

        for (int i = 0; i < k;) {
            int r = random.nextInt(rgb.length);
            if (!dummyHash[r]) {
                mu[i] = rgb[r];
                dummyHash[r] = true;
                i++;
            }
        }

        int clusterNumberForPoints[] = new int[rgb.length];
        for (int iterator = 0; iterator < 100; iterator++) {

            for (int i = 0; i < rgb.length; i++) {
                int min = Integer.MAX_VALUE;
                int clusterNumber = -1;
                for (int j = 0; j < k; j++) {
                    int temp = calculateDistance(rgb[i], mu[j]);
                    if (temp < min) {
                        min = temp;
                        clusterNumber = j;
                    }
                }
                clusterNumberForPoints[i] = clusterNumber;
            }
            computeCentroid(rgb, mu, clusterNumberForPoints, k);
        }
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = mu[clusterNumberForPoints[i]];
        }
    }
}
