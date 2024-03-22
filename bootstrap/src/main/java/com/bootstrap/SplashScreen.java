package com.bootstrap;

import java.awt.Color;
import java.awt.Image;

import javax.swing.JFrame;

public class SplashScreen extends JFrame {
    public SplashScreen(final String title, final Image image, final int x, final int y) {
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(0);
        this.setTitle(title);
        this.setSize(x, y);
        this.setIconImage(image);
        this.setContentPane(new SplashPanel(this, image, x, y));
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
