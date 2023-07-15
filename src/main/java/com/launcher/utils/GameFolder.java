package com.launcher.utils;

import java.io.File;

import com.photon.util.os.FileLocation;

public class GameFolder {
	
	public File gameDir;
	public File cacheDir;
	public File binDir;
	public File playDir;
	public File gameJar;
	public File libsDir;
	public File assetsDir;
	public File nativesDir;
	public File nativesCacheDir;
	public File runtimeDir;
	public File versionsDir;

	public GameFolder(String location) {
		this.gameDir = FileLocation.getWorkingDirectory(location);
		this.binDir = new File(this.gameDir, "bin");
		this.playDir = new File(this.gameDir, "bin" + File.separator + "game");
		this.gameJar = new File(this.gameDir, "bin" + File.separator + "minecraft.jar");
		this.libsDir = new File(this.gameDir, "libraries");
		this.cacheDir = new File(this.gameDir, "cache");
		this.nativesCacheDir = new File(this.gameDir, "cache"+ File.separator + "natives");
		this.assetsDir = new File(this.gameDir, "assets");
		this.nativesDir = new File(this.gameDir, "natives");
		this.runtimeDir = new File(this.gameDir, "runtime");
		this.versionsDir = new File(this.gameDir, "versions");
		
		/* Create missing folders */
		this.getLibsDir().mkdirs();
		this.getCacheDir().mkdirs();
		this.getAssetsDir().mkdirs();
		this.getBinDir().mkdirs();
		this.getGameDir().mkdirs();
		this.getNativesDir().mkdirs();
		this.getNativesCacheDir().mkdirs();
		this.getPlayDir().mkdirs();
		this.getRuntimeDir().mkdirs();
		this.getVersionsDir().mkdirs();
	}

	public File getVersionsDir() {
		return this.versionsDir;
	}

	public File getRuntimeDir() {
		return this.runtimeDir;
	}

	public File getGameDir() {
		return this.gameDir;
	}

	public File getCacheDir() {
		return this.cacheDir;
	}
	
	public File getBinDir() {
		return this.binDir;
	}
	
	public File getPlayDir() {
		return this.playDir;
	}

	public File getGameJar() {
		return this.gameJar;
	}

	public File getLibsDir() {
		return this.libsDir;
	}

	public File getAssetsDir() {
		return this.assetsDir;
	}

	public File getNativesDir() {
		return this.nativesDir;
	}

	public File getNativesCacheDir() {
		return this.nativesCacheDir;
	}
}
