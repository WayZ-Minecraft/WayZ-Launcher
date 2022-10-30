package com.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.launcher.utils.file.FileUtil;
import com.photon.util.ProtectorManager;

public class GameVerifier {
	
	public GameEngine engine;

	public static List<String> allowedFiles = new ArrayList<String>();
	public List<File> filesList;
	public List<String> ignoreList = new ArrayList<String>();
	public List<String> ignoreListFolder = new ArrayList<String>();
	public List<String> deleteList = new ArrayList<String>();
	
	public GameVerifier(GameEngine gameEngine) { this.engine = gameEngine; }

	public void verify() {
		for (File file : this.engine.getGameFolder().getGameDir().listFiles()) {			
			if (file.getAbsolutePath().endsWith(engine.getGameLinks().getJsonName())) continue;
			if (file.getAbsolutePath().endsWith("downloads.xml")) continue;
			if (existInDeleteList(file.getAbsolutePath().replace(this.engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) FileUtil.deleteSomething(file.getAbsolutePath());
			if (!existInAllowedFiles(file.getAbsolutePath().replace(this.engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) {
				if (existInIgnoreListFolder(file.getParent().replace(this.engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) continue;
				else if (existInIgnoreList(file.getAbsolutePath().replace(this.engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) continue;
				else FileUtil.deleteSomething(file.getAbsolutePath());
			}
		}
	}
	
	public static void addToFileList(String allowed) { allowedFiles.add(allowed); }
	
	public boolean existInIgnoreList(String search) {
		for(String str: this.ignoreList) {
		    if(str.trim().contains(search)) return true;
		}
		return false;
	}

	public boolean existInIgnoreListFolder(String search) {
		String newSearch = search + "\\";
		for(String str: this.ignoreListFolder) {
		    if(newSearch.contains(str)) return true;
		}
		return false;
	}
	
	public boolean existInAllowedFiles(String search) {
		for (String str : allowedFiles) {
			if (str.trim().contains(search)) return true;
		}
		return false;
	}
	
	public boolean existInDeleteList(String search) {
		for(String str: this.deleteList) {
		    if(str.trim().contains(search)) return true;
		}
		return false;
	}

	public void getIgnoreList() {
		URLConnection url = null;
		BufferedReader read = null;
		try {
			url = new URL(this.engine.getGameLinks().getIgnoreListUrl()).openConnection();
			ProtectorManager.addProperties(url);
			url.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			read = new BufferedReader(new InputStreamReader(url.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String i;
		try {
			while ((i = read.readLine()) != null) {
				String correctName = i.replace('/', File.separatorChar);
				if (correctName.endsWith("\\") || correctName.endsWith("/")) {
					this.ignoreListFolder.add(correctName);
				}
				else {
					this.ignoreList.add("" + this.engine.getGameFolder().getGameDir() + File.separatorChar + correctName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getDeleteList() {
		URLConnection url = null;
		BufferedReader read = null;
		try {
			url = new URL(this.engine.getGameLinks().getDeleteListUrl()).openConnection();
			ProtectorManager.addProperties(url);
			url.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			read = new BufferedReader(new InputStreamReader(url.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String i;
		try {
			while ((i = read.readLine()) != null) {
				String correctName = i.replace('/', File.separatorChar);
				this.deleteList.add("" + this.engine.getGameFolder().getGameDir() + File.separatorChar + correctName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
