package com.bootstrap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.photon.informations.PhotonUpdaterManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.ProtectorManager;
import com.photon.util.ConsoleManager.EnumLogType;

public class JFXUpdateManager {

    public static String url = NetworkDirectories.config.webUrl;
    private static ExecutorService downloader = Executors.newFixedThreadPool(5);

    public static String getSHA1(JFXFileType type) {
        try {
			final URL url = new URL(NetworkDirectories.config.webUrl+"services_updates/jfx/the-sha.php?type=javafx."+type.name().toLowerCase());
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			ProtectorManager.addProperties(conn);
			conn.setRequestMethod("GET");

			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String sha1 = reader.readLine();
			reader.close();

			return sha1.contains("-") ? "UNKNOWN" : sha1;
		} catch (IOException e) { e.printStackTrace(); }
		return "UNKNOWN";
    }

    public static boolean hasUpdate(JFXFileType type, final File file) {
        final String sha1 = getSHA1(type);
        if(!file.exists()) return true;
        if(!sha1.equalsIgnoreCase("UNKNOWN") && !PhotonUpdaterManager.getDigest(file, "SHA", 40).equals(sha1)) return true;
        return false;
    }

    public static boolean update(JFXFileType type, File file) {
        boolean hasFinished = false;
        if(hasUpdate(type, file)) hasFinished = download(type, file);
        if(hasFinished) downloader = Executors.newFixedThreadPool(5);
        return hasFinished;
    }

    public static String getURL(JFXFileType type) { return url+"services_updates/jfx/javafx."+type.name().toLowerCase()+".jar"; }

    private static boolean download(JFXFileType type, File file) {
        try {
            downloader.submit(new UpdateDownloader(file, getURL(type)));
            downloader.shutdown();
            downloader.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            return true;
        } catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private static class UpdateDownloader extends Thread {
        private final File file;
        private final String url;

        public UpdateDownloader(final File file, final String url) {
            this.file = file;
            this.url = url;
        }

        @Override
        public void run() {
            System.out.println("Acquiring file '" + file.getName() + "'");
            try {
                BufferedInputStream bufferedInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    URL downloadUrl = new URL(url.replace(" ", "%20"));
                    URLConnection urlConnection = downloadUrl.openConnection();
                    ProtectorManager.addProperties(urlConnection);
                    bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
                    fileOutputStream = new FileOutputStream(file);
                    
                    final int size = 1024;
                    byte[] data = new byte[size];
                    int read;
                    while ((read = bufferedInputStream.read(data, 0, size)) != -1) fileOutputStream.write(data, 0, read);
                } finally {
                    if (bufferedInputStream != null) bufferedInputStream.close();
                    if (fileOutputStream != null) fileOutputStream.close();
                }
            } catch (IOException e) { ConsoleManager.create("Unable to find file !").withType(EnumLogType.LAUNCHER).error().end(); }
        }
    }
}
