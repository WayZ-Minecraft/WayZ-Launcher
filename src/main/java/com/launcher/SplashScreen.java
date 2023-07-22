package com.launcher;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

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
    
    public static class SplashPanel extends JPanel {
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
    }

    protected static class ColoredProgressBar extends JProgressBar {
        public ColoredProgressBar() {
            this.setOpaque(false);
            this.setBackground(LauncherEngine.buttonColor);
            this.setForeground(Color.white);
            this.setStringPainted(false);
            this.setBorderPainted(false);
            this.setUI(new ProgressCircleUI(this));
        }
    }

    public static class ProgressCircleUI extends BasicProgressBarUI {

        private BufferedImage imageColor;
        private final ColoredProgressBar pro;

        public ProgressCircleUI(ColoredProgressBar pro) {
            this.pro = pro;
            pro.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent ce) { createImage(); }
            });
        }

        private void createImage() {
            int width = pro.getWidth();
            int height = pro.getHeight();
            imageColor = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imageColor.createGraphics();
            int w = Math.min(width, height);
            int x = (width - w) / 2;
            int y = (height - w) / 2;
            for (int i = 1; i <= 360; i++) {
                g2.setColor(Color.white);
                g2.fillArc(x - 2, y - 2, w + 4, w + 4, i, 1);
            }
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            Dimension d = super.getPreferredSize(c);
            int v = Math.max(d.width, d.height);
            d.setSize(v, v);
            return d;
        }

        private Area createCircle(double percentComplete) {
            Insets b = progressBar.getInsets(); // area for border
            int barRectWidth = progressBar.getWidth() - b.right - b.left;
            int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
            if (barRectWidth <= 0 || barRectHeight <= 0) {
                return null;
            }
            double degree = 360 * percentComplete;
            double sz = Math.min(barRectWidth, barRectHeight);
            double cx = b.left + barRectWidth * .5;
            double cy = b.top + barRectHeight * .5;
            double or = sz * .5;
            double ir = or * .85; //or - 20;
            Shape inner = new Ellipse2D.Double(cx - ir, cy - ir, ir * 2, ir * 2);
            Shape outer = new Arc2D.Double(
                    cx - or, cy - or, sz, sz, 90 - degree, degree, Arc2D.PIE);
            Area area = new Area(outer);
            area.subtract(new Area(inner));
            return area;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            int width = c.getWidth();
            int height = c.getHeight();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(progressBar.getForeground());
            g2.fill(createCircle(progressBar.getPercentComplete()));
            g2.setComposite(AlphaComposite.SrcIn);
            if (imageColor == null) createImage();
            g2.drawImage(imageColor, 0, 0, null);
            g2.dispose();
            Graphics2D gg2 = (Graphics2D) g;
            gg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gg2.setColor(pro.getBackground());
            gg2.fill(createCircle(100));
            g.drawImage(img, 0, 0, null);
            if (progressBar.isStringPainted()) paintString(g);
        }

        private void paintString(Graphics g) {
            Insets b = progressBar.getInsets(); // area for border
            int barRectWidth = progressBar.getWidth() - b.right - b.left;
            int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
            g.setColor(pro.getForeground());
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, 0, b);
        }
    }
}
