package com.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import com.photon.ui.PhotonInterfaceUtils;
import com.photon.ui.base.Frame;
import com.photon.ui.base.Panel;
import com.photon.ui.components.progressbar.ColoredProgressbar;

@SuppressWarnings("serial")
public class SplashScreen extends Frame
{
    public SplashScreen(final String title, final Image image, final int x, final int y) {
        this.setTitle(title);
        this.setSize(x, y);
        this.setIconImage(image);
        this.setContentPane(new SplashPanel(this, image));
        this.setFrameUILookAndFeel(0, 0);
        this.setBackground(PhotonInterfaceUtils.TRANSPARENT);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public static class SplashPanel extends Panel {
    	public ColoredProgressbar progressBar;
        private Image image;
        
        public SplashPanel(final Frame frame, final Image image) {
        	super(frame);
        	this.setLayout(null);
        	this.image = image;
        	this.progressBar = new ColoredProgressbar(LauncherEngine.boxColor, Color.white);
        	this.progressBar.setArcSize(12, 12);
        	this.progressBar.setSize(250, 10);
        	this.progressBar.setLocation(0, 240);
        	this.addComponent(this.progressBar);
        }
        
		@Override
		public void drawPanel(Graphics g) { g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this); }
    }
}
