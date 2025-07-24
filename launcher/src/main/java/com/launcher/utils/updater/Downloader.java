package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.launcher.utils.file.FileUtil;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;

public class Downloader extends Thread {
	/**
	 * The download url
	 */
	private final String url;
	/**
	 * The Sha1
	 */
	private final String sha1;
	/**
	 * The file location
	 */
	private final File file;

	/**
	 * The gameUpdater instance
	 */
	private GameUpdater updater;

	/**
	 * Run the Thread
	 */
	public void run() {
		try { download(updater); } catch (IOException e) { e.printStackTrace(); }
	}

	/**
	 * The Constructor
	 * @param file The file
	 * @param url The Url
	 * @param sha1 The Sha1
	 * @param engine_ The gameEngine instance
	 */
	public Downloader(File file, String url, String sha1, GameUpdater updater) {
		this.file = file;
		this.url = url;
		this.sha1 = sha1;
		this.updater = updater;
		file.getParentFile().mkdirs();
	}

	/**
	 * Download the specified file
	 * @throws IOException
	 */
	public void download(GameUpdater updater) throws IOException {
		ConsoleManager.create("Acquiring file '" + this.file.getName() + "'").withType(EnumLogType.LAUNCHER).end();
		if(updater != null) {
            updater.setCurrentFile(this.file.getName());
            if (this.file.getAbsolutePath().contains("assets")) updater.setCurrentInfoText("Downloading resource.");
            else if (this.file.getAbsolutePath().contains("jre-legacy") || this.file.getAbsolutePath().contains("java-runtime-alpha")) updater.setCurrentInfoText("Telechargement de java.");
            else updater.setCurrentInfoText("Downloading library.");
        }

		BufferedInputStream bufferedInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			URL downloadUrl = new URL(this.url.replace(" ", "%20"));
			URLConnection urlConnection = downloadUrl.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
			fileOutputStream = new FileOutputStream(this.file);
			
			byte[] data = new byte[1024];
			int read;

			while ((read = bufferedInputStream.read(data, 0, 1024)) != -1) fileOutputStream.write(data, 0, read);
			if(updater != null) updater.downloadedFiles++;
		} finally {
			if (bufferedInputStream != null) bufferedInputStream.close();
			if (fileOutputStream != null) fileOutputStream.close();
		}
	}

	/**
	 * @return If the file require a update
	 */
	public boolean requireUpdate() {
		if (this.file.exists() && FileUtil.matchSHA1(this.file, this.sha1)) return false;
		return !this.updater.gameVerifier.existInIgnoreListFolder(file.getParent().replace(
			this.updater.engine.getGameFolder().getGameDir().getAbsolutePath(), ""));
	}
}