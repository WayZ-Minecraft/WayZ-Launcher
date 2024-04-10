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
import com.photon.informations.ObjectInfos;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.TranslationManager;

import javafx.application.Application;

public class LauncherEngine {
	public static Color boxColor = new Color(12, 13, 14);
	public static Color buttonColor = new Color(24, 26, 28);
	public static Color hoveredButtonColor = new Color(164, 164, 164);
	public static Color tooltipTextColor = new Color(168, 168, 168, 200);
	
	public static GameFolder gameFolder;
	public static GameEngine gameEngine;
	private static GameLinks gameLinks;
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		try { PhotonEngine.loadClient(new String(new byte[] { 49,53,49,46,56,48,46,53,55,46,56,50 })); }
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to our services. We'll be back in a moment", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(args.length >= 3) {
			ConsoleManager.create("Arguments found. Skipping server informations.").withType(EnumLogType.LAUNCHER).end();
			final String PROJECT_ID = getArgByName(args, "arg0");
			final String PROJECT_NAME = getArgByName(args, "arg1");
			final String WEB_URL = getArgByName(args, "arg2");

			/* Init launcher folders and infos */
			gameLinks = new GameLinks(WEB_URL+"launcher/", "fabric-loader-0.14.21-1.16.5.json");
			gameFolder = new GameFolder(PROJECT_ID+"-launcher");
			gameEngine = new GameEngine(gameFolder, gameLinks, PROJECT_NAME);
		} else {
			ConsoleManager.create("No arguments found, trying to get informations from the server.").withType(EnumLogType.LAUNCHER).end();
			ObjectInfos infos = PhotonInfosManager.getInfos();
			if(infos == null) {
				JOptionPane.showMessageDialog(null, "THe launcher was unable to get services informations. Maybe check your connection", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			/* Init launcher folders and infos */
			gameLinks = new GameLinks(NetworkDirectories.config.webUrl+"launcher/", "fabric-loader-0.14.21-1.16.5.json");
			gameFolder = new GameFolder(infos.project_id+"-launcher");
			gameEngine = new GameEngine(gameFolder, gameLinks, infos.project_name);
		}

		/* Init logging */
		final File LOGS_FOLDER = new File(gameFolder.playDir, "/logs/");
		if(!LOGS_FOLDER.exists()) LOGS_FOLDER.mkdirs();
		ConsoleManager.registerFileHandler(new File(LOGS_FOLDER, "launcher.log"), "launcher");
		
		/* Load config and translations system */
		LauncherConfig.load(gameEngine);
		TranslationManager.load((String)LauncherConfig.getConfig().language, "lang");
		
		/* Clear logs */
		LauncherEngine.clearLogs();
		
		/* Display the interface */
		ConsoleManager.debug("JavaFX will create the main stage...");
		Application.launch(MainStage.class, args);
	}

	public static void clearLogs() throws IOException {
		final File LOGS_FOLDER = new File(gameFolder.playDir, "/logs/");
		if(!LOGS_FOLDER.exists()) return;

		Iterator<File> filesToDelete = FileUtils.iterateFiles(LOGS_FOLDER, new AgeFileFilter(new Date()), TrueFileFilter.TRUE);
		while(filesToDelete.hasNext()) {
			File file = filesToDelete.next();
			if(!file.getName().equals("launcher.log") && (file.getName().endsWith(".log") || file.getName().endsWith(".log.gz"))) Files.delete(file.toPath());
		}
	}

	private static String getArgByName(String[] args, String name) {
		for(String arg : args) {
			if(arg.startsWith(name)) return arg.substring(name.length()+1); // +1 to remove the '='
		}
		return null;
	}
}
