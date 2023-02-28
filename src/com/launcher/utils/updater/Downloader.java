package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameVerifier;
import com.launcher.utils.file.FileUtil;
import com.photon.util.ProtectorManager;

public class Downloader extends Thread {

	private final String url;

	private final String sha1;

	private final File file;

	private GameEngine engine;

	public void run() {
		try { download(); }
		catch (IOException e) { e.printStackTrace(); }
	}

	public Downloader(File file, String url, String sha1, GameEngine engine_) {
		this.file = file;
		this.url = url;
		this.sha1 = sha1;
		this.engine = engine_;
		GameVerifier.addToFileList(file.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace("\\", "/"));
		file.getParentFile().mkdirs();
	}

	public void download() throws IOException {
		engine.getGameUpdater().setCurrentFile(this.file.getName());
		BufferedInputStream bufferedInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			URL downloadUrl = new URL(this.url.replace(" ", "%20"));
			URLConnection urlConnection = downloadUrl.openConnection();
			ProtectorManager.addProperties(urlConnection, "mojang");
			bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
			fileOutputStream = new FileOutputStream(this.file);

			int len = 8*1024;

			byte[] data = new byte[len];
			int read;

			while (engine.getGameUpdater().addMB(read = bufferedInputStream.read(data, 0, len)) != -1) fileOutputStream.write(data, 0, read);
			engine.getGameUpdater().downloadedFiles++;
		} finally {
			if (bufferedInputStream != null) bufferedInputStream.close();
			if (fileOutputStream != null) fileOutputStream.close();
		}
	}

	public boolean requireUpdate() {
		if ((this.file.exists()) && (FileUtil.matchSHA1(this.file, this.sha1))) return false;
		return true;
	}
}