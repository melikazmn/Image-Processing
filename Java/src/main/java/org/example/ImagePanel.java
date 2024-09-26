package org.example;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image image;


    public void setImage(Image image) {
        this.image = image;
        setPreferredSize(new Dimension(image.bufferedImage.getWidth(), image.bufferedImage.getHeight()));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null && image.bufferedImage != null) {
            g.drawImage(image.bufferedImage, 0, 0, this);
        }
    }
}