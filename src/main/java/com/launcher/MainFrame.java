package com.launcher;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.photon.informations.PhotonInfosManager;
import com.photon.ui.PhotonInterfaceUtils;
import com.photon.ui.base.Frame;

@SuppressWarnings("serial")
public class MainFrame extends Frame {

	public MainFrame() {
		super();
		this.setTitle(PhotonInfosManager.getInfos().project_name);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int width = (int)(screenSize.getWidth()/3.5);
		final int height = (int)screenSize.getHeight()/3;
		this.setSize(width, height);
		this.setIconImage(PhotonInfosManager.getGameLogo());
		this.setContentPane(new MainPanel(this));
		this.setMover(this);
		this.setFrameUILookAndFeel(12, 12);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setBackground(PhotonInterfaceUtils.TRANSPARENT);
	}
}
