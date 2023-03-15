package com.launcher.utils;

import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.launcher.utils.updater.GameUpdater;

public class GameEngine {
	
	private GameFolder gameFolder;
	private String name;
	private GameLinks gameLinks;
	private GameUpdater gameUpdater;
	private JVMArguments jvmArgs;
	private MinecraftVersion minecraftVersion;
	private boolean debugMode;
	
	public GameEngine(GameFolder folder, GameLinks links, String name) {
		this.gameFolder = folder;
		this.gameLinks = links;
		this.name = name;
	}

	public void reg(JVMArguments jvArgs) {
		this.jvmArgs = jvArgs;
	}
	
	public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
	
	public boolean isDebugMode() { return this.debugMode; }
	
	public void reg(MinecraftVersion version) {
		this.minecraftVersion = version;
	}
	
	public void reg(GameLinks links) {
		this.gameLinks = links;
	}

	public void reg(GameUpdater updater) {
		this.gameUpdater = updater;
	}
	
	public String getName() {
		return this.name;
	}

	public GameFolder getGameFolder() {
		return this.gameFolder;
	}

	public GameLinks getGameLinks() {
		return this.gameLinks;
	}

	public GameUpdater getGameUpdater() {
		return this.gameUpdater;
	}

	public MinecraftVersion getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public JVMArguments getJVMArguments() {
		return jvmArgs;
	}
}
