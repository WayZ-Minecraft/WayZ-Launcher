package com.launcher.utils;

import java.io.File;

import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.launcher.utils.updater.GameUpdater;
import com.photon.util.os.OperatingSystem;

public class GameEngine {

	private GameFolder gameFolder;
	private String name;
	private GameLinks gameLinks;
	private MinecraftVersion minecraftVersion;
	public Runnable startRunnable;
	public Runnable crashRunnable;
	public Runnable exitRunnable;
	
	public GameEngine(GameFolder folder, GameLinks links, String name) {
		this.gameFolder = folder;
		this.gameLinks = links;
		this.name = name;
	}
	
	public void reg(MinecraftVersion version) {
		this.minecraftVersion = version;
	}
	
	public void reg(GameLinks links) { this.gameLinks = links; }
	
	public String getName() { return this.name; }

	public GameFolder getGameFolder() { return this.gameFolder; }

	public GameLinks getGameLinks() { return this.gameLinks; }

	public MinecraftVersion getMinecraftVersion() { return this.minecraftVersion; }

	/**
	 * @return The Java Path Installed
	 */
	public static String getJavaPath(MinecraftVersion mcVersion, GameEngine engine) {
		if(GameUpdater.checkJavaInstallation(mcVersion)) return OperatingSystem.getJavaPath();

		final String component = mcVersion.getJavaVersion().getComponent();
		final File javaPath = new File(engine.getGameFolder().getRuntimeDir(), component);
		if (OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS && new File(javaPath + "/bin/javaw.exe").isFile()) return javaPath + "/bin/javaw.exe";
		return javaPath + "/bin/java";
	}
}
