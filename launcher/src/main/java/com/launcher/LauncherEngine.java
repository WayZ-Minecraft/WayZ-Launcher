package com.launcher;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.GameLinks;
import com.launcher.utils.LauncherConfig;
import com.photon.PhotonEngine;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.TranslationManager;

import javafx.application.Application;

public class LauncherEngine {
	public static Color boxColor = new Color(12, 13, 14);
	public static Color buttonColor = new Color(24, 26, 28);
	public static Color hoveredButtonColor = new Color(164, 164, 164);
	public static Color tooltipTextColor = new Color(168, 168, 168, 200);
	
	public static GameFolder gameFolder;
	private static GameLinks gameLinks;
	public static GameEngine gameEngine;
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		try { PhotonEngine.loadClient(new String(new byte[] { 49,53,49,46,56,48,46,53,55,46,56,50 })); }
		catch (IOException e) {
    		JOptionPane.showMessageDialog(null, "Unable to connect to our services. We'll be back in a moment", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
		if(PhotonInfosManager.getInfos() == null) {
			JOptionPane.showMessageDialog(null, "Unable to get services informations. Maybe check your connection", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		/* Init launcher folders and infos */
		gameLinks = new GameLinks(NetworkDirectories.config.webUrl+"launcher/", "fabric-loader-0.14.21-1.16.5.json");
		gameFolder = new GameFolder(PhotonInfosManager.getInfos().project_id+"-launcher");
    	gameEngine = new GameEngine(gameFolder, gameLinks, PhotonInfosManager.getInfos().project_name);
		
		/* Init logging */
    	final File logsFolder = new File(gameFolder.playDir, "/logs/");
    	if(!logsFolder.exists()) logsFolder.mkdirs();
    	ConsoleManager.registerFileHandler(new File(logsFolder, "launcher.log"), "launcher");
		
		/* Load config and translations system */
    	LauncherConfig.load(gameEngine);
    	TranslationManager.load((String)LauncherConfig.getConfig().language, "lang");
		
		LauncherEngine.clearLogs();
		/* Display the interface */
		Application.launch(MainStage.class, args);
	}

	public static void clearLogs() throws IOException {
		final File logsFolder = new File(gameFolder.playDir, "/logs/");
		if(!logsFolder.exists()) return;

		Iterator<File> filesToDelete = FileUtils.iterateFiles(logsFolder, new AgeFileFilter(new Date()), TrueFileFilter.TRUE);
		while(filesToDelete.hasNext()) {
			File file = filesToDelete.next();
			if(!file.getName().equals("launcher.log") && (file.getName().endsWith(".log") || file.getName().endsWith(".log.gz"))) Files.delete(file.toPath());
		}
	}
}
