package com.bootstrap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.ProtectorManager;

public class JVMUpdateManager {

    public static String url = NetworkDirectories.config.webUrl;
    private static ExecutorService downloader = Executors.newFixedThreadPool(5);

    public static void update(File file) { download(file); }

    public static String getURL() { return url+"services_updates/aJVM-17.zip"; }

    private static boolean download(File file) {
        try {
            if(file.exists()) return false;
            downloader.submit(new UpdateDownloader(file, getURL()));
            downloader.shutdown();
            downloader.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            byte[] buffer = new byte[1024];
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    File newFile = newFile(file.getParentFile(), zipEntry);
                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) throw new IOException("Failed to create directory " + newFile);
                    } else {
                        // fix for Windows-created archives
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Failed to create directory " + parent);
                
                        // write file content
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                        fos.close();
                    }
                    zipEntry = zis.getNextEntry();
                }

                zis.closeEntry();
                zis.close();
            }
            return true;
        } catch (InterruptedException | IOException e) { e.printStackTrace(); }
        return false;
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return destFile;
    }

    private static class UpdateDownloader extends Thread {
        private final String url;
        private final File file;

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
