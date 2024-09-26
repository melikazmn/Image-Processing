package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainFrame extends JFrame {
    private ImagePanel imagePanel;
    private Image image;

    public MainFrame() {
        super("Image Processing Application");

        imagePanel = new ImagePanel();
        ButtonPanel buttonPanel = new ButtonPanel(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(imagePanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(1200, 800); // Make the window bigger
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void loadImage(File file) {
        image = new Image();
        image.loadImage(file.getAbsolutePath());
        image.getImageMatrix();
        imagePanel.setImage(image);
    }

    public void applyFilter(String filterType) {
        int height = image.matrix.length;
        int width = image.matrix[0].length;

        int numThreads = Math.max(9, (width * height) / (200 * 200));
        int chunkSize;
        if (numThreads == 9) {
            chunkSize = (int) Math.ceil(Math.sqrt((width * height) / numThreads));
        } else {
            chunkSize = 200;
        }

        ArrayList<Thread> threads = new ArrayList<>();
        Lock lock = new ReentrantLock();
        int heightChunkNum = (int) Math.ceil(height / (double) chunkSize) + 1;
        int widthChunkNum = (int) Math.ceil(width / (double) chunkSize) + 1;

        double[][] gaussianKernel = GaussianKernel.generateKernel(15, 3.0);

        for (int i = 0; i < heightChunkNum; i++) {
            for (int j = 0; j < widthChunkNum; j++) {
                int startX = j * chunkSize;
                int startY = i * chunkSize;
                int endX = Math.min((j + 1) * chunkSize, width);
                int endY = Math.min((i + 1) * chunkSize, height);
                if (startX < width && startY < endY) {
                    Applier applier = new Applier(image.matrix, startX, endX, startY, endY, lock, gaussianKernel, filterType);
                    Thread thread = new Thread(applier);
                    threads.add(thread);
                    thread.start();
                }
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        image.saveImage("output.jpg");
        imagePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
