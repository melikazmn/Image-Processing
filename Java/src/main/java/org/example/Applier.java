package org.example;

import java.util.concurrent.locks.Lock;

public class Applier implements Runnable {
    public final int[][] mainMatrix;
    private final int startX, endX, startY, endY;
    private final Lock lock;
    private final double[][] gaussianKernel;
    private final String filterType;

    int[][] Gx = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    int[][] Gy = {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
    };

    public Applier(int[][] matrix, int startX, int endX, int startY, int endY, Lock lock, double[][] gaussianKernel, String filterType) {
        this.mainMatrix = matrix;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.lock = lock;
        this.gaussianKernel = gaussianKernel;
        this.filterType = filterType;
    }

    @Override
    public void run() {
        if ("Sobel".equals(filterType)) {
            applySobelFilter();
        } else if ("Gaussian".equals(filterType)) {
            applyGaussianFilter();
        } else if ("Sepia".equals(filterType)) {
            applySepiaFilter();
        }
    }
    private void applySobelFilter() {
        // Apply gray filter for each chunk
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                lock.lock();
                try {
                    int rgb = mainMatrix[y][x];
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    int gray = (int) (0.2989 * red + 0.5870 * green + 0.1140 * blue);
                    mainMatrix[y][x] = (gray << 16) | (gray << 8) | gray; // Store grayscale value
                } finally {
                    lock.unlock();
                }
            }
        }

        // Apply Sobel filter
        int regionWidth = endX - startX;
        int regionHeight = endY - startY;
        int sum = 0;
        int count = 0;
        if (regionHeight >= 2 && regionWidth >= 2) {
            int[][] result = new int[regionHeight][regionWidth];

            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    int sumX = 0;
                    int sumY = 0;

                    // Apply Gx and Gy
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (y == 0 && i == -1)
                                break;
                            if (y == mainMatrix.length - 1 && i == +1)
                                break;
                            if (x == 0 && j == -1)
                                j++;
                            if (x == mainMatrix[0].length - 1 && j == +1)
                                break;
                            lock.lock();
                            try {
                                int gray = (mainMatrix[y + i][x + j] & 0xFF); // Extract grayscale value
                                sumX += Gx[i + 1][j + 1] * gray;
                                sumY += Gy[i + 1][j + 1] * gray;
                            } finally {
                                lock.unlock();
                            }
                        }
                    }

                    // Calculate the gradient magnitude
                    int magnitude = (int) Math.sqrt(sumX * sumX + sumY * sumY);
                    result[y - startY][x - startX] = magnitude;
                    sum += magnitude;
                    count++;
                }
            }
            // Calculate the mean value of the result matrix
            int mean = sum / count;

            // Update the main matrix with the result
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    lock.lock();
                    try {
                        if (result[y - startY][x - startX] > mean) { // Set to black
                            mainMatrix[y][x] = 0x000000;
                        } else {
                            mainMatrix[y][x] = 0xFFFFFF; // Set to white
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } else {
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    mainMatrix[y][x] = 0xFFFFFF; // Set to white
                }
            }
        }
    }

    private void applyGaussianFilter() {
        int kernelSize = gaussianKernel.length;
        int kernelHalfSize = kernelSize / 2;

        int regionWidth = endX - startX;
        int regionHeight = endY - startY;

        int[][] result = new int[regionHeight][regionWidth];

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                double sum = 0.0;

                for (int ky = -kernelHalfSize; ky <= kernelHalfSize; ky++) {
                    for (int kx = -kernelHalfSize; kx <= kernelHalfSize; kx++) {
                        int pixelY = Math.min(Math.max(y + ky, 0), mainMatrix.length - 1);
                        int pixelX = Math.min(Math.max(x + kx, 0), mainMatrix[0].length - 1);

                        lock.lock();
                        try {
                            int gray = (mainMatrix[pixelY][pixelX] & 0xFF); // Extract grayscale value
                            sum += gray * gaussianKernel[ky + kernelHalfSize][kx + kernelHalfSize];
                        } finally {
                            lock.unlock();
                        }
                    }
                }

                int grayscaleValue = (int) Math.min(Math.max(sum, 0), 255);
                result[y - startY][x - startX] = (grayscaleValue << 16) | (grayscaleValue << 8) | grayscaleValue;
            }
        }

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                lock.lock();
                try {
                    mainMatrix[y][x] = result[y - startY][x - startX];
                } finally {
                    lock.unlock();
                }
            }
        }
    }
    private void applySepiaFilter() {
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                lock.lock();
                try {
                    int rgb = mainMatrix[y][x];
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    int tr = (int)(0.393 * red + 0.769 * green + 0.189 * blue);
                    int tg = (int)(0.349 * red + 0.686 * green + 0.168 * blue);
                    int tb = (int)(0.272 * red + 0.534 * green + 0.131 * blue);

                    // Clamp values to be within [0, 255]
                    if (tr > 255) tr = 255;
                    if (tg > 255) tg = 255;
                    if (tb > 255) tb = 255;

                    mainMatrix[y][x] = (tr << 16) | (tg << 8) | tb;
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}