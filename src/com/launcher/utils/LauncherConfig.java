package com.launcher.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LauncherConfig {

	public static GameEngine gameEngine;
	private static ConfigVersion config;
	private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static File file;
	
    public static void load(GameEngine engine) {
    	try {
    		file = new File(engine.getGameFolder().getBinDir(), "launcher_config.json");
    		if(!file.exists()) {
    			file.createNewFile();
    			config = new ConfigVersion();
    			saveConfig(config.allocatedram, config.vmarguments);
    		} else config = gson.fromJson(new FileReader(file), ConfigVersion.class);
		} catch (Exception e) { e.printStackTrace(); }
    }
    
    public static ConfigVersion getConfig() { return config; }
    
    public static void saveConfig(String allocatedram, String vmarguments) {
		try {
			config.allocatedram = allocatedram;
			config.vmarguments = vmarguments;
			String cfg = gson.toJson(config);
			FileWriter writer = new FileWriter(file);
			writer.write(cfg);
			writer.close();
			
			config = gson.fromJson(new FileReader(file), ConfigVersion.class);
		} catch (Exception e) { e.printStackTrace(); }
    }
}
