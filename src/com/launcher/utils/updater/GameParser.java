package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameVerifier;
import com.launcher.utils.file.FileUtil;
import com.launcher.utils.file.LauncherFile;
import com.photon.informations.PhotonInfosManager;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.ProtectorManager;

public class GameParser {

	public static void getFilesToDownload(GameEngine engine) {
		downloadXMLFile(engine);
		try {
			final URLConnection resourceUrl = new URL(engine.getGameLinks().getCustomFilesUrl()).openConnection();
			ProtectorManager.addProperties(resourceUrl);
			resourceUrl.connect();
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.parse(resourceUrl.getInputStream());
			final NodeList nodeLst = doc.getElementsByTagName("Contents");

			final long start = System.nanoTime();
			for (int i = 0; i < nodeLst.getLength(); i++) {
				final Node node = nodeLst.item(i);
				if (node.getNodeType() == 1) {
	                  final Element element = (Element) node;
	                  final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue().replace("\n", "");
	                  String etag = element.getElementsByTagName("ETag") != null ? element.getElementsByTagName("ETag").item(0).getChildNodes().item(0).getNodeValue() : "-";
	                  final long size = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
	                  
					File localFile = new File(engine.getGameFolder().getGameDir(), key);
					GameVerifier.addToFileList(localFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
					if (key.contains("minecraft.jar")) { engine.getGameUpdater().hasCustomJar = true; }
					if (key.contains(PhotonInfosManager.getInfos().project_name) && key.endsWith(".jar")) { engine.getGameUpdater().hasModJar = true; }
					if (!localFile.isDirectory()) {
						if (etag.length() > 1) {
							etag = FileUtil.getEtag(etag);
							if (localFile.exists()) {
								if (localFile.isFile() && localFile.length() == size) {
									final String localMd5 = FileUtil.getMD5(localFile);
									if (!localMd5.equals(etag)) {
										if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
												engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
												engine.getGameUpdater().filesToDownload++;
												engine.getGameUpdater().filesMbDownload += size;
										}
									}
								} else {
									if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
										engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
										engine.getGameUpdater().filesToDownload++;
										engine.getGameUpdater().filesMbDownload += size;
									}
								}
							} else {
								if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
									engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
									engine.getGameUpdater().filesToDownload++;
									engine.getGameUpdater().filesMbDownload += size;
								}
							}

						}
					} else {
						localFile.mkdir();
						localFile.mkdirs();
					}
				}
			}
			final long end = System.nanoTime();
			final long delta = end - start;
			ConsoleManager.print(EnumLogType.LAUNCHER, "Time (delta) to compare resources: " + delta / 1000000L + " ms");
			ConsoleManager.print(EnumLogType.LAUNCHER, "From: " + resourceUrl);
		} catch (final Exception ex) { ex.printStackTrace(); }
	}

	public static void getFilesToDownloadOffline(GameEngine engine) {
		try {
			File f = new File(engine.getGameFolder().getCacheDir(), "downloads.xml");
			final URL resourceUrl = new URL(f.toURI().toString());
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.parse(resourceUrl.openConnection(Proxy.NO_PROXY).getInputStream());
			final NodeList nodeLst = doc.getElementsByTagName("Contents");

			final long start = System.nanoTime();
			for (int i = 0; i < nodeLst.getLength(); i++) {
				final Node node = nodeLst.item(i);
				if (node.getNodeType() == 1) {
                  final Element element = (Element) node;
                  final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue().replace("\n", "");
                  String etag = element.getElementsByTagName("ETag") != null ? element.getElementsByTagName("ETag").item(0).getChildNodes().item(0).getNodeValue() : "-";
                  final long size = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
                  
                  File localFile = new File(engine.getGameFolder().getGameDir(), key);
					GameVerifier.addToFileList(localFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
					if (key.contains("minecraft.jar")) { engine.getGameUpdater().hasCustomJar = true; }
					if (key.contains(PhotonInfosManager.getInfos().project_name) && key.endsWith(".jar")) { engine.getGameUpdater().hasModJar = true; }
					if (!localFile.isDirectory()) {
						if (etag.length() > 1) {
							etag = FileUtil.getEtag(etag);
							if (localFile.exists()) {
								if (localFile.isFile() && localFile.length() == size) {
									final String localMd5 = FileUtil.getMD5(localFile);
									if (!localMd5.equals(etag)) {
										if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
												engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
												engine.getGameUpdater().filesToDownload++;
												engine.getGameUpdater().filesMbDownload += size;
										}
									}
								} else {
									if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
										engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
										engine.getGameUpdater().filesToDownload++;
										engine.getGameUpdater().filesMbDownload += size;
									}
								}
							} else {
								if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
									engine.getGameUpdater().files.put(key, new LauncherFile(size, engine.getGameLinks().getCustomFilesUrl() + key, localFile.getAbsolutePath()));
									engine.getGameUpdater().filesToDownload++;
									engine.getGameUpdater().filesMbDownload += size;
								}
							}
	
						}
					} else {
						localFile.mkdir();
						localFile.mkdirs();
					}
				}
			}
			final long end = System.nanoTime();
			final long delta = end - start;
			ConsoleManager.print(EnumLogType.LAUNCHER, "Time (delta) to compare resources: " + delta / 1000000L + " ms");
			ConsoleManager.print(EnumLogType.LAUNCHER, "From: " + resourceUrl);
		} catch (final Exception ex) { ex.printStackTrace(); }
	}

	public static void downloadXMLFile(GameEngine engine) {
		File theFile = new File(engine.getGameFolder().getCacheDir(), "downloads.xml");
		GameVerifier.addToFileList(theFile.getAbsolutePath().replace(engine.getGameFolder().getCacheDir().getAbsolutePath(), "").replace('/', File.separatorChar));
		try {
			URL url = new URL(engine.getGameLinks().getCustomFilesUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			ProtectorManager.addProperties(connection);
			connection.connect();
			float totalDataRead = 0;
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(theFile);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int i = 0;
			while ((i = in.read(data, 0, 1024)) >= 0) {
				totalDataRead = totalDataRead + i;
				bout.write(data, 0, i);
			}
			bout.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}