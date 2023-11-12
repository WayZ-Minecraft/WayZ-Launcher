package com.launcher.utils.file;

import java.io.File;
import java.util.ArrayList;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.minecraft.json.MinecraftLibrary;
import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.photon.util.os.OperatingSystem;

public class GameUtils {

	/**
	 * Get the working directory for the game
	 * @param workDir : The working directory name
	 * @return The working directory
	 */
	public static File getWorkingDirectory(String workDir) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (OperatingSystem.getCurrent()) {
		case LINUX:
			workingDirectory = new File(userHome + "/." + workDir);
		case WINDOWS:
			workingDirectory = new File(userHome + "\\AppData\\Roaming\\." + workDir);
			break;
		case OSX:
			workingDirectory = new File(userHome + "/Library/Application Support/" + workDir);
			break;
		default:
			workingDirectory = new File(userHome + "/." + workDir);
			break;
		}
		return workingDirectory;
	}

	/**
	 * Construct the classpath for the game
	 * @param engine : The game engine
	 * @return The classpath
	 */
	public static String constructClasspath(GameEngine engine) {
		GameFolder workDir = engine.getGameFolder();
		String result = "";
		ArrayList<File> libs = getMinecraftLibraries(engine.getMinecraftVersion(), engine);
		String separator = System.getProperty("path.separator");
		for (File lib : libs) {
			if (lib.getAbsolutePath().contains("-srg")) {}
			else if (lib.getAbsolutePath().contains("-extra")) {}
			else if (lib.getAbsolutePath().contains("mclanguage")) {}
			else if (lib.getAbsolutePath().contains("-universal")) {}
			else result += lib.getAbsolutePath() + separator;
		}
		final File versionFolder = new File(workDir.getVersionsDir(), engine.getMinecraftVersion().getId());
		result += new File(workDir.getLibsDir(), "opus/opus-jni-java-1.0.3.jar").getAbsolutePath()+separator;
		result += new File(workDir.getLibsDir(), "com/photon/api.jar").getAbsolutePath()+separator;
		for(File file : new File(workDir.getLibsDir(), "additional").listFiles()) 
			result += new File(workDir.getLibsDir(), "additional/"+file.getName()).getAbsolutePath()+separator;
		result += new File(versionFolder, engine.getMinecraftVersion().getId() + ".jar").getAbsolutePath();
		return result;
	}

	private static ArrayList<File> getMinecraftLibraries(MinecraftVersion minecraftVersion, GameEngine engine) {
		ArrayList<File> theLibraries = new ArrayList<File>();
		GameFolder workDir = engine.getGameFolder();
		for (MinecraftLibrary lib : minecraftVersion.getLibraries()) {
			if (lib.appliesToCurrentEnvironment()) {
				File libPath = new File(workDir.getLibsDir(), lib.getArtifactPath());
				theLibraries.add(libPath);
			}
		}
		return theLibraries;
	}
	
	public static ArrayList<File> list(File folder) {
		ArrayList<File> files = new ArrayList<File>();
		if (!folder.isDirectory()) return files;
		File[] folderFiles = folder.listFiles();
		if (folderFiles != null) {
			for (File f : folderFiles)
				if (f.isDirectory()) files.addAll(list(f));
				else files.add(f);
		}
		return files;
	}
}
