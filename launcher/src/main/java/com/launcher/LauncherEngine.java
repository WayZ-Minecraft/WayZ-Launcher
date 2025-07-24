package com.launcher;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.GameLinks;
import com.launcher.utils.LauncherConfig;
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
		ConsoleManager.create("No arguments found, trying to get informations from the server.").withType(EnumLogType.LAUNCHER).end();

		/* Init launcher folders and infos */
		//TODO : Use your own URL like us before with https://files.wayz.fr/launcher/
		// E.g : http://pathToLauncherFolder/launcher/
		gameLinks = new GameLinks("", "fabric-loader-0.14.21-1.16.5.json");
		gameFolder = new GameFolder("wayz-launcher");
		gameEngine = new GameEngine(gameFolder, gameLinks, "WayZ");

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
		ConsoleManager.create("JavaFX will create the main stage...").withType(EnumLogType.LAUNCHER).end();
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
}
