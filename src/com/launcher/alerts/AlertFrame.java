package com.launcher.alerts;

import com.launcher.LauncherEngine;
import com.photon.informations.PhotonInfosManager;
import com.photon.ui.base.Frame;
import com.photon.util.TranslationManager;

@SuppressWarnings({ "serial", "deprecation" })
public class AlertFrame extends Frame {

	private Frame parent;
	private AlertPanel panel;
	
	public AlertFrame(Frame parent, String desc, EnumAlertType type) { this(parent, "", desc, type); }
	
	public AlertFrame(Frame parent, String subTitle, String desc, EnumAlertType type) { this(parent, -1, subTitle, desc, type); }
	
	public AlertFrame(Frame parent, int id, String desc, EnumAlertType type) { this(parent, id, "", desc, type); }
		
	public AlertFrame(Frame parent, int id, String subTitle, String desc, EnumAlertType type) {
		super();
		this.parent = parent;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(TranslationManager.format(type.getName()));
		this.setSize(415, 280);
		this.setIconImage(PhotonInfosManager.getGameLogo());
		this.setFrameUILookAndFeel(12, 12);
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
		parent.disable();
		this.setBackground(LauncherEngine.boxColor);
		this.setContentPane(this.panel = new AlertPanel(this, id, subTitle, desc, type));
		this.setVisible(true);
	}
	
	public void setRunnableCallback(Runnable callback) { this.panel.callback = callback; }
	
	public void closeAlert() {
		this.parent.enable();
		this.dispose();
	}
}
