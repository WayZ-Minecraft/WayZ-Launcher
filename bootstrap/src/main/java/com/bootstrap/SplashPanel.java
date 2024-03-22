package com.bootstrap;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SplashPanel extends JPanel {
    public ColoredProgressBar progressBar;
    private Image image;
    private int x;
    private int y;
    
    public SplashPanel(final JFrame frame, final Image image, int x, int y) {
        this.setLayout(null);
        this.x = x;
        this.y = y;
        this.image = image;
        this.progressBar = new ColoredProgressBar();
        this.progressBar.setSize(this.x, this.y);
        this.progressBar.setLocation(this.getWidth()/2, this.getHeight()/2);
        this.progressBar.setValue(0);
        this.progressBar.setMaximum(10);
        this.add(this.progressBar);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(this.image, this.getWidth()/5, this.getHeight()/5, 155, 155, this);
    }

    public void setProgressBarVisibility(boolean visible) {
        this.setProgressBarVisibility(visible, 0, 0);
    }

    public void setProgressBarVisibility(boolean visible, int progress, int maxProgress) {
        this.progressBar.setVisible(visible);
        this.progressBar.setValue(progress);
        this.progressBar.setMaximum(maxProgress);
    }
}
