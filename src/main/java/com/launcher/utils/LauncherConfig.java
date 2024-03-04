package com.launcher.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LauncherConfig {

	public static GameEngine gameEngine;
	private static ConfigVersion config;
	private final static Gson gson = new GsonBuilder().create();
	private static File file;
	
    public static void load(GameEngine engine) {
    	try {
    		file = new File(engine.getGameFolder().getBinDir(), "launcher_config.json");
    		if(!file.exists()) {
    			file.createNewFile();
    			config = new ConfigVersion();
    			saveConfig();
    		} else config = gson.fromJson(new FileReader(file), ConfigVersion.class);
		} catch (Exception e) { e.printStackTrace(); }
    }
    
    public static ConfigVersion getConfig() { return config; }

    public static boolean isSaved() {
		try {
			if(!file.exists()) return false;
    		final ConfigVersion cfg = gson.fromJson(new FileReader(file), ConfigVersion.class);
			return getConfig().equals(cfg);
		} catch (Exception e) { e.printStackTrace(); }
		return false;
	}

    public static void resetConfig() { config = new ConfigVersion(); }
    
    public static void saveConfig() {
		try {
			String cfg = gson.toJson(config);
			FileWriter writer = new FileWriter(file);
			writer.write(cfg);
			writer.close();
			config = gson.fromJson(new FileReader(file), ConfigVersion.class);
		} catch (Exception e) { e.printStackTrace(); }
    }
}
