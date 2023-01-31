package com.launcher;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import com.launcher.SplashScreen.SplashPanel;
import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.GameLinks;
import com.launcher.utils.LauncherConfig;
import com.photon.PhotonEngine;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.TranslationManager;
import com.photon.util.os.ApplicationUtils;
import com.photon.util.os.FileLocation;

public class LauncherEngine
{	
	public static Color boxColor = new Color(12, 13, 14);
	public static Color buttonColor = new Color(24, 26, 28);
	public static Color hoveredButtonColor = new Color(164, 164, 164);
	public static Color tooltipTextColor = new Color(168, 168, 168, 200);
	
	private static String VERSION = "1.0.5";
	public static GameFolder gameFolder;
	private static GameLinks gameLinks;
	public static GameEngine gameEngine;
	
    public static Font getFont(int size) { return FileLocation.loadFont("fonts/axia_bold.otf", "Myriad", size); }
    
    public static Font getFontBOLD(int size) { return FileLocation.loadFont("fonts/axia_bold.otf", "Myriad", size).deriveFont(Font.BOLD); }
	
	public static void main(String[] args) throws URISyntaxException {
		boolean debugMode = false;
		for(String arg : args) {
			debugMode = arg.equalsIgnoreCase("debug");
			if(arg.contains("delfile-")) {
				final File toDelete = new File(arg.replace("delfile-", ""));
				if(toDelete.exists()) toDelete.delete();
			}
		}
    	
		try {
			PhotonEngine.loadClient("62.210.168.207");
			Thread.sleep(1000);
		} catch (IOException | InterruptedException e) {
    		JOptionPane.showMessageDialog(null, "For some connections reason we can't start the launcher", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
		if(PhotonInfosManager.getInfos() == null) { JOptionPane.showMessageDialog(null, "For some connections reason we can't start the launcher", "Error", JOptionPane.ERROR_MESSAGE); return; }

		gameFolder = new GameFolder(PhotonInfosManager.getInfos().project_id);
    	gameLinks = new GameLinks(NetworkDirectories.config.webUrl + "launcher/", "1.12.2.json");
    	gameEngine = new GameEngine(gameFolder, gameLinks, PhotonInfosManager.getInfos().project_name);
    	gameEngine.setDebugMode(debugMode);

    	final File currentExecutionFile = new File(LauncherEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	final File currentExecutionFolder = currentExecutionFile.getParentFile();
    	if(PhotonInfosManager.hasLauncherUpdate(LauncherEngine.VERSION)) {
    		final SplashScreen splash = new SplashScreen(PhotonInfosManager.getInfos().project_name, PhotonInfosManager.getGameLogo(), 250, 250);
    		final SplashPanel panel = (SplashPanel)splash.getContentPane();
    		PhotonInfosManager.updateLauncherFromDir(currentExecutionFolder);
    		while(!PhotonInfosManager.updateFinished) {
    			if(PhotonInfosManager.updateSize > 0 && PhotonInfosManager.updateSizeDownloaded >= PhotonInfosManager.updateSize) ApplicationUtils.launch(new File(currentExecutionFolder, "/launcher-" + PhotonInfosManager.getLatestLauncherUpdate() + ".jar"), new String[] { "delfile-"+currentExecutionFile.getPath() }, true);
    			panel.progressBar.setValue((int)PhotonInfosManager.updateSizeDownloaded);
    			panel.progressBar.setMaximum((int)PhotonInfosManager.updateSize);
    		}
    		return;
    	}
    	    	
    	LauncherConfig.load(gameEngine);
    	TranslationManager.load((String)LauncherConfig.getConfig().language, "lang");
    	new MainFrame();
    	System.gc();
	}
}
