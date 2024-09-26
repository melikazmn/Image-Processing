package org.example;

public class GaussianKernel {
    public static double[][] generateKernel(int size, double sigma) {
        double[][] kernel = new double[size][size];
        double sum = 0.0;

        int halfSize = size / 2;
        double sigma2 = 2 * sigma * sigma;

        for (int y = -halfSize; y <= halfSize; y++) {
            for (int x = -halfSize; x <= halfSize; x++) {
                double value = Math.exp(-(x * x + y * y) / sigma2) / (Math.PI * sigma2);
                kernel[y + halfSize][x + halfSize] = value;
                sum += value;
            }
        }

        // Normalize the kernel
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }

        return kernel;
    }
}
