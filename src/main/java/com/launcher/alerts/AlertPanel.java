package com.launcher.alerts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.launcher.LauncherEngine;
import com.photon.ui.PhotonInterfaceUtils;
import com.photon.ui.base.EventListener;
import com.photon.ui.base.Panel;
import com.photon.ui.components.buttons.ColoredButton;
import com.photon.ui.components.utils.AbstractButton;
import com.photon.util.TranslationManager;

@SuppressWarnings("serial")
public class AlertPanel extends Panel implements EventListener {

	private EnumAlertType type;
	private String subTitle;
	private String desc;
	private ColoredButton closeButton = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
	private ColoredButton yesButton = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
	private ColoredButton cancelButton = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
	private final String name;
	public Runnable callback;
		
	public AlertPanel(AlertFrame parent, int id, String subTitle, String desc, EnumAlertType type) { this(parent, id, subTitle, desc, type, null); }
	
	public AlertPanel(AlertFrame parent, int id, String subTitle, String desc, EnumAlertType type, final Runnable cb) {
		super(parent);
		this.setBackground(LauncherEngine.boxColor);
		this.setLayout(null);
		this.callback = cb;
		this.type = type;
		this.subTitle = subTitle;
		this.desc = desc;
		this.name = TranslationManager.format(type.getName());
		
		if(id == 0) {
			this.yesButton.setTextColor(EnumAlertType.SUCCESS.color);
			this.yesButton.setSize(125, 55);
			this.yesButton.setLocation(75, this.frame.getHeight() - 90);
			this.yesButton.setText(TranslationManager.format("alert.yes.text"));
			this.addButton(this.yesButton);
			
			this.cancelButton.setTextColor(EnumAlertType.ERROR.color);
			this.cancelButton.setSize(125, 55);
			this.cancelButton.setLocation(this.frame.getWidth() - 200, this.frame.getHeight() - 90);
			this.cancelButton.setText(TranslationManager.format("alert.cancel.text"));
			this.addButton(this.cancelButton);
		} else {
			this.closeButton.setTextColor(Color.white);
			this.closeButton.setSize(220, 55);
			this.closeButton.setLocation(105, this.frame.getHeight() - 90);
			this.closeButton.setText(TranslationManager.format("alert.close.text"));
			this.addButton(this.closeButton);
		}
	}

	public void addButton(AbstractButton btn) {
		btn.setClickSound("sounds/click_btn");
		btn.setHoverSound("sounds/hover_btn");
		btn.setArcSize(10, 10);
		btn.addEventListener(this);
		btn.setFont(LauncherEngine.getFont(20));
		this.addComponent(btn);
	}
	
	@Override
	public void drawPanel(Graphics g) {
		PhotonInterfaceUtils.activateAntialias(g);
		PhotonInterfaceUtils.drawTextCenteredX(g, name, this.getWidth(), 60, this.type.color, LauncherEngine.getFont(30));
		PhotonInterfaceUtils.drawTextCenteredX(g, this.subTitle, this.getWidth(), 92, Color.white, LauncherEngine.getFont(18));
		PhotonInterfaceUtils.drawCenteredParagraph((Graphics2D)g, this.desc, this.frame.getBounds(), 340.0, Color.white, LauncherEngine.getFont(15));
	}

	@Override
	public void onEvent(PhotonEvent event) {
		if(event.getSource() == this.closeButton) {
			((AlertFrame)this.frame).closeAlert();
			if (this.callback != null) this.callback.run();
		}
		if(event.getSource() == this.yesButton) {
			((AlertFrame)this.frame).closeAlert();
			if (this.callback != null) this.callback.run();
		}
		if(event.getSource() == this.cancelButton) ((AlertFrame)this.frame).closeAlert();
	}
}
