package com.bootstrap;

import java.awt.Color;

import javax.swing.JProgressBar;

public class ColoredProgressBar extends JProgressBar {
        public ColoredProgressBar() {
            this.setOpaque(false);
            this.setBackground(new Color(12, 13, 14, 255));
            this.setForeground(Color.white);
            this.setStringPainted(false);
            this.setBorderPainted(false);
            this.setUI(new ProgressCircleUI(this));
        }
    }