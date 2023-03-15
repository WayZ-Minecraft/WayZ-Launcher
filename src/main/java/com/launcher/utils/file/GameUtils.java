package com.launcher.utils.file;

import java.io.File;
import java.util.ArrayList;

import com.launcher.utils.GameEngine;

public class GameUtils {

	public static String constructClasspath(GameEngine engine) {
		String result = "";
		ArrayList<File> libs = list(engine.getGameFolder().getLibsDir());
		String separator = System.getProperty("path.separator");
		for (File lib : libs) {
			result += lib.getAbsolutePath() + separator;
		}
		result += engine.getGameFolder().getGameJar().getAbsolutePath();
		return result;
	}

	public static ArrayList<File> list(File folder) {
		ArrayList<File> files = new ArrayList<File>();
		if (!folder.isDirectory())
			return files;

		File[] folderFiles = folder.listFiles();
		if (folderFiles != null)
			for (File f : folderFiles)
				if (f.isDirectory())
					files.addAll(list(f));
				else
					files.add(f);

		return files;
	}
}
