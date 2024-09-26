package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ButtonPanel extends JPanel {
    private MainFrame mainFrame;

    public ButtonPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        JButton uploadButton = new JButton("Upload Image");
        JButton applySobelFilterButton = new JButton("Apply Sobel Filter");
        JButton applyGaussianFilterButton = new JButton("Apply Gaussian Filter");
        JButton applySepiaFilterButton = new JButton("Apply Sepia Filter");

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    mainFrame.loadImage(selectedFile);
                }
            }
        });

        applySobelFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.applyFilter("Sobel");
            }
        });

        applyGaussianFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.applyFilter("Gaussian");
            }
        });

        applySepiaFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.applyFilter("Sepia");
            }
        });

        add(uploadButton);
        add(applySobelFilterButton);
        add(applyGaussianFilterButton);
        add(applySepiaFilterButton);
    }
}
