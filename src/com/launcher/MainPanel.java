package com.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.Timer;

import com.launcher.alerts.AlertFrame;
import com.launcher.alerts.EnumAlertType;
import com.launcher.utils.GameEngine;
import com.launcher.utils.JVMArguments;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.updater.GameUpdater;
import com.photon.informations.PhotonInfosManager;
import com.photon.ui.PhotonInterfaceUtils;
import com.photon.ui.base.EventListener;
import com.photon.ui.base.Frame;
import com.photon.ui.base.Panel;
import com.photon.ui.components.buttons.ColoredButton;
import com.photon.ui.components.buttons.SemiTexturedButton;
import com.photon.ui.components.fields.ColoredTextField;
import com.photon.ui.components.progressbar.ColoredProgressbar;
import com.photon.ui.components.utils.AbstractButton;
import com.photon.ui.components.utils.AbstractProgressbar;
import com.photon.util.TranslationManager;
import com.photon.util.os.ApplicationUtils;
import com.photon.util.os.FileLocation;

public class MainPanel extends Panel implements EventListener {
			
	private SemiTexturedButton frenchButton;
	private SemiTexturedButton englishButton;
	
	private SemiTexturedButton closeButton;
	private SemiTexturedButton hideButton;
	private SemiTexturedButton settingsButton;
	private SemiTexturedButton deleteButton;
	
	private ColoredTextField jmvArgsField;
	private ColoredTextField ramField;
	
	private Thread updateThread;
	private GameUpdater updater = new GameUpdater();
	private Timer updaterTimer;
	private Timer launcingTimer;
	
	private JLabel progressBarLabel;
	private ColoredProgressbar progressBar;
	private ColoredButton playBtn;
	private ColoredButton saveBtn;
	private ColoredButton cancelBtn;
		
	public MainPanel(Frame parent) {
		super(parent);
		this.setLayout(null);
		final boolean french = LauncherConfig.getConfig().language.equals("fr");
		
		this.frenchButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/language_french.png"), TRANSPARENT, LITTLE_TRANSPARENT_WHITE);
		this.frenchButton.setSize(40, 40);
		this.frenchButton.setLocation(30, 110);
		this.frenchButton.setSelectable();
		this.frenchButton.setSelectedColor(LauncherEngine.tooltipTextColor);
		this.frenchButton.setSelected(french);
		this.frenchButton.setVisible(false);
		this.addButton(this.frenchButton);
		
		this.englishButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/language_english.png"), TRANSPARENT, LITTLE_TRANSPARENT_WHITE);
		this.englishButton.setSize(40, 40);
		this.englishButton.setLocation(75, 110);
		this.englishButton.setSelectable();
		this.englishButton.setSelectedColor(LauncherEngine.tooltipTextColor);
		this.englishButton.setSelected(!french);
		this.englishButton.setVisible(false);
		this.addButton(this.englishButton);
		
		this.ramField = new ColoredTextField(LauncherEngine.boxColor, Color.white, LauncherEngine.hoveredButtonColor);
		this.ramField.setSize(60, 30);
		this.ramField.setLocation((this.frame.getWidth() - 110)/2, 115);
		this.ramField.setVisible(false);
		this.ramField.setText(LauncherConfig.getConfig().allocatedram);
		this.ramField.setArcSize(15, 15);
		this.ramField.setFont(LauncherEngine.getFont(14));
		this.addComponent(this.ramField);
		
		this.jmvArgsField = new ColoredTextField(LauncherEngine.boxColor, Color.white, LauncherEngine.hoveredButtonColor);
		this.jmvArgsField.setSize(250, 30);
		this.jmvArgsField.setLocation(30, 215);
		this.jmvArgsField.setArcSize(15, 15);
		this.jmvArgsField.setVisible(false);
		this.jmvArgsField.setText(LauncherConfig.getConfig().vmarguments);
		this.jmvArgsField.setFont(LauncherEngine.getFont(14));
		this.jmvArgsField.setKeyTypedSound("sounds/key_typing");
//		btn.setHoverSound("sounds/hover_btn");
		this.addComponent(this.jmvArgsField);
		
		this.closeButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/close_btn.png"), Color.white, LauncherEngine.hoveredButtonColor);
		this.closeButton.setSize(20, 20);
		this.closeButton.setLocation(this.frame.getWidth()-35, 15);
		this.addButton(this.closeButton);
		
		this.hideButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/hide_btn.png"), Color.white, LauncherEngine.hoveredButtonColor);
		this.hideButton.setSize(20, 20);
		this.hideButton.setLocation(this.frame.getWidth()-65, 15);
		this.addButton(this.hideButton);
		
		this.settingsButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/settings_btn.png"), Color.white, LauncherEngine.hoveredButtonColor);
		this.settingsButton.setSize(25, 25);
		this.settingsButton.setLocation(65, 12);
		this.addButton(this.settingsButton);
		
		this.deleteButton = new SemiTexturedButton(FileLocation.loadSmoothedImage("icons/delete_btn.png"), Color.white, LauncherEngine.hoveredButtonColor);
		this.deleteButton.setSize(25, 25);
		this.deleteButton.setLocation(this.frame.getWidth() - 90, 110);
		this.deleteButton.setVisible(false);
		this.addButton(this.deleteButton);
		
		this.playBtn = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
		this.playBtn.setFont(LauncherEngine.getFont(20));
		this.playBtn.setText(TranslationManager.format("mainPanel.play.text"));
		this.playBtn.setTextColor(Color.white);
		this.playBtn.setSize(160, 55);
		this.playBtn.setLocation((this.frame.getWidth() - 160)/2, this.frame.getHeight()-125);
		this.addButton(this.playBtn);
		
		this.saveBtn = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
		this.saveBtn.setFont(LauncherEngine.getFont(20));
		this.saveBtn.setText(TranslationManager.format("mainPanel.save.text"));
		this.saveBtn.setTextColor(EnumAlertType.SUCCESS.color);
		this.saveBtn.setSize(160, 55);
		this.saveBtn.setLocation((this.frame.getWidth() - 160)/2-100, this.frame.getHeight()-80);
		this.saveBtn.setVisible(false);
		this.addButton(this.saveBtn);
		
		this.cancelBtn = new ColoredButton(LauncherEngine.buttonColor, LauncherEngine.hoveredButtonColor);
		this.cancelBtn.setFont(LauncherEngine.getFont(20));
		this.cancelBtn.setText(TranslationManager.format("alert.cancel.text"));
		this.cancelBtn.setTextColor(EnumAlertType.ERROR.color);
		this.cancelBtn.setSize(160, 55);
		this.cancelBtn.setLocation((this.frame.getWidth() - 160)/2+100, this.frame.getHeight()-80);
		this.cancelBtn.setVisible(false);
		this.addButton(this.cancelBtn);
		
		this.progressBarLabel = new JLabel();
		this.progressBarLabel.setSize(this.frame.getWidth()-80, 15);
		this.progressBarLabel.setLocation((this.frame.getWidth() - (this.frame.getWidth()-80))/2, this.frame.getHeight()-55);
		this.progressBarLabel.setHorizontalAlignment(JLabel.CENTER);
		this.progressBarLabel.setForeground(Color.white);
		this.progressBarLabel.setFont(LauncherEngine.getFont(14));
		this.progressBarLabel.setText(TranslationManager.format("mainPanel.pressPlay"));
		this.progressBarLabel.setVisible(false);
		this.addComponent(this.progressBarLabel);
		
    	this.progressBar = new ColoredProgressbar(LauncherEngine.boxColor, Color.white);
    	this.progressBar.setSize(this.frame.getWidth()-80, 15);
    	this.progressBar.setLocation((this.frame.getWidth() - (this.frame.getWidth()-80))/2, this.frame.getHeight()-35);
    	this.progressBar.setArcSize(15, 15);
    	this.progressBar.setVisible(false);
    	this.addComponent(this.progressBar);
	}
	
	protected void addButton(AbstractButton btn) {
		btn.setClickSound("sounds/click_btn");
		btn.setHoverSound("sounds/hover_btn");
		btn.setArcSize(25, 25);
		btn.addEventListener(this);
		this.addComponent(btn);
	}
		
	@Override
	public void onEvent(PhotonEvent event) {
		if(event.getSource() == this.closeButton) {
			new AlertFrame(this.frame, 0, TranslationManager.format("alert.launcher.close"), EnumAlertType.WARNING).setRunnableCallback(new Runnable() {
				@Override
				public void run() { System.exit(0); }
			});
		} else if(event.getSource() == this.hideButton) this.frame.setState(Frame.ICONIFIED);
		else if(event.getSource() == this.deleteButton) {			
			new AlertFrame(this.frame, 0, TranslationManager.format("alert.warning.deletefiles"), EnumAlertType.WARNING).setRunnableCallback(new Runnable() {
				@Override
				public void run() {
					clearFolder(LauncherEngine.gameFolder.gameDir);
					ApplicationUtils.exitProperly();
				}
			});
		}
		else if(event.getSource() == this.settingsButton || event.getSource() == this.saveBtn || event.getSource() == this.cancelBtn) {
			frenchButton.setVisible(!frenchButton.isVisible());
			englishButton.setVisible(!englishButton.isVisible());
			ramField.setVisible(!ramField.isVisible());
			jmvArgsField.setVisible(!jmvArgsField.isVisible());
			deleteButton.setVisible(!deleteButton.isVisible());
			saveBtn.setVisible(!saveBtn.isVisible());
			cancelBtn.setVisible(!cancelBtn.isVisible());
			playBtn.setVisible(!playBtn.isVisible());
			repaint();
			if(event.getSource() != this.cancelBtn) LauncherConfig.saveConfig(ramField.getText(), jmvArgsField.getText());
		} else if(event.getSource() == this.frenchButton || event.getSource() == this.englishButton) {
			LauncherConfig.getConfig().language = event.getSource() == this.englishButton ? TranslationManager.locale_en.getLanguage() : TranslationManager.locale_fr.getLanguage();
			final boolean french = LauncherConfig.getConfig().language.equals("fr");
			this.frenchButton.setSelected(french);
			this.englishButton.setSelected(!french);
			new AlertFrame(this.frame, 0, TranslationManager.format("alert.warning.language.restart"), EnumAlertType.WARNING).setRunnableCallback(new Runnable() {
				@Override
				public void run() {
					ApplicationUtils.restart(LauncherEngine.class, new String[] {});
					LauncherConfig.saveConfig(ramField.getText(), jmvArgsField.getText());
				}
			});
			repaint();
		} else if(event.getSource() == this.playBtn) {
			this.playBtn.setEnabled(false);
			this.settingsButton.setEnabled(false);
			this.progressBar.setVisible(true);
			this.progressBarLabel.setVisible(true);
			this.updater.reg(LauncherEngine.gameEngine);
			this.updater.setFrameToHide(this.frame);
			final String vmArgs = (String) LauncherConfig.getConfig().vmarguments;
			String[] s = null;
			if (vmArgs.length() > 3) s = vmArgs.split(" ");
			final JVMArguments arguments = new JVMArguments(s);
			LauncherEngine.gameEngine.reg(arguments);
			LauncherEngine.gameEngine.reg(this.updater);
			
			this.updateThread = new Thread() {
				@Override
				public void run() { LauncherEngine.gameEngine.getGameUpdater().run(); }
			};
			this.updateThread.start();
			this.updaterTimer = new Timer(1000, new DownloadTick(LauncherEngine.gameEngine, this.progressBar, this.progressBarLabel));
			this.updaterTimer.start();
			this.launcingTimer = new Timer(350, new LaucnhingTick(this.playBtn));
			this.launcingTimer.start();
		}
	}
	
	final Image gameLogo = PhotonInfosManager.getGameLogo();
	final Image background = FileLocation.loadImage("background.png");
	
	public static void clearFolder(File file) {
    	if(!file.exists()) return;
    	for (File subFile : file.listFiles()) {
    		if(subFile.isDirectory()) clearFolder(subFile);
    		else subFile.delete();
    	}
    	file.delete();
    }
	
	@Override
	public void drawPanel(Graphics g) {
		PhotonInterfaceUtils.drawRoundedRect(g, 0, 0, this.frame.getWidth(), 50, 40, 40, LauncherEngine.boxColor);
		PhotonInterfaceUtils.drawRoundedRect(g, 0, 60, this.frame.getWidth(), this.frame.getHeight()-60, 35, 35, LauncherEngine.boxColor);
		PhotonInterfaceUtils.drawRoundedRect(g, 15, 60+15, this.frame.getWidth()-30, this.frame.getHeight()-(85*2)-(!this.frenchButton.isVisible() ? 45 : 105), 35, 35, LauncherEngine.buttonColor);
		PhotonInterfaceUtils.drawImage(g, this.gameLogo, 15, 2, 45, 45);
		PhotonInterfaceUtils.drawTextCenteredX(g, PhotonInfosManager.getInfos().project_name.toUpperCase() + " V" + PhotonInfosManager.getInfos().mod_version, this.frame.getWidth(), 30, Color.white, LauncherEngine.getFont(22));
//		PhotonInterfaceUtils.drawImage(g, this.background, 0, 0, this.getWidth(), this.getHeight());
				
		if(!this.frenchButton.isVisible()) return;
		PhotonInterfaceUtils.drawRoundedRect(g, 15, this.frame.getHeight()-(85*2)-15, this.frame.getWidth()-30, this.frame.getHeight()-(85*2)-105, 35, 35, LauncherEngine.buttonColor);
//		PhotonInterfaceUtils.drawRoundedRect(g, 15, 160+15, this.frame.getWidth()-30, this.frame.getHeight()-(85*2)-105, 35, 35, LauncherEngine.buttonColor);
		PhotonInterfaceUtils.drawRoundedRect(g, this.ramField.getX()+50, this.ramField.getY(), 50, 30, 15, 15, Color.DARK_GRAY);
		PhotonInterfaceUtils.drawText(g, "Gb", this.ramField.getX()+70, this.ramField.getY()+20, Color.white, LauncherEngine.getFont(15));
		PhotonInterfaceUtils.drawText(g, TranslationManager.format("mainPanel.settings.lang"), 35, 100, Color.white, LauncherEngine.getFont(15));
		PhotonInterfaceUtils.drawTextCenteredX(g, TranslationManager.format("mainPanel.settings.ram"), this.frame.getWidth(), 100, Color.white, LauncherEngine.getFont(15));
		PhotonInterfaceUtils.drawTextAlignedRight(g, TranslationManager.format("mainPanel.settings.deletegame"), this.frame.getWidth()-25, 100, Color.white, LauncherEngine.getFont(15));
		PhotonInterfaceUtils.drawText(g, TranslationManager.format("mainPanel.settings.jvmargs"), 35, 200, Color.white, LauncherEngine.getFont(15));
	}
	
	private static class DownloadTick implements ActionListener {
		
		private final GameEngine engine;
		private final AbstractProgressbar bar;
		private final JLabel label;
		
		public DownloadTick(GameEngine engine, AbstractProgressbar bar, JLabel label) {
			this.engine = engine;
			bar.setMaximum(100);
			this.bar = bar;
			this.label = label;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final String currentFileText = engine.getGameUpdater().downloadedFiles < engine.getGameUpdater().filesToDownload ? " ("+ getPercent(engine.getGameUpdater().downloadedFiles, engine.getGameUpdater().filesToDownload) + "%)" : "";
			this.label.setText(TranslationManager.format(engine.getGameUpdater().currentInfoText) + currentFileText);
			if(engine.getGameUpdater().filesToDownload > 0) {
				if(!this.bar.isVisible()) this.bar.setVisible(true);
				this.bar.setMaximum(engine.getGameUpdater().filesToDownload);
				this.bar.setValue(engine.getGameUpdater().downloadedFiles);
			}
		}
		
		public static int getPercent(int val, int max) { return val * 100 / max; }
	}
	
	private static class LaucnhingTick implements ActionListener {

		private final AbstractButton btn;
		private int count = 0;
		
		public LaucnhingTick(AbstractButton btn) { this.btn = btn; }
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.count++;
			this.btn.setTextColor(Color.gray);
			this.btn.setText(TranslationManager.format("mainPanel.launching")+" " + (this.count == 1 ? "." : this.count == 2 ? ".." : this.count == 3 ? "..." : ""));
			if(this.count > 3) this.count = 0;
		}
	}
}
