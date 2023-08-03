package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.launcher.utils.GameEngine;
import com.launcher.utils.file.FileUtil;
import com.photon.informations.PhotonInfosManager;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.ProtectorManager;
import com.photon.util.os.MultiThreadWorker;

public class GameParser {

	private static Node[] convertToArray(NodeList list) {
    	int length = list.getLength();
      	final Node[] copy = new Node[length];
      	for (int n = 0; n < length; ++n) copy[n] = list.item(n);
      	return copy;
  	}

	private static int filesAnalysed = 0;
	public static void getFilesToDownload(GameEngine engine, GameUpdater updater) {
		downloadXMLFile(engine);
		try {
			final URLConnection resourceUrl = new URL(engine.getGameLinks().getCustomFilesUrl()).openConnection();
			ProtectorManager.addProperties(resourceUrl);
			resourceUrl.connect();
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.parse(resourceUrl.getInputStream());
			final NodeList nodeLst = doc.getElementsByTagName("Contents");
			final List<Node> nodes = Arrays.asList(convertToArray(nodeLst));
			final Iterator<Node> itr = nodes.iterator();

			final long start = System.nanoTime();
			new MultiThreadWorker() {
				@Override protected boolean work() {
					if(itr == null) return false;
					if(itr.hasNext()) {
						final Node node = itr.next();
						synchronized(node) {
							filesAnalysed++;
							writeFile(engine, updater, node);
							return true;
						}
					}
					return false;
				}
			}.run();
			final long delta = System.nanoTime() - start;
			ConsoleManager.create("Time (delta) to compare resources: " + delta / 1000000L + " ms").withType(EnumLogType.LAUNCHER).end();;
			ConsoleManager.create(filesAnalysed + " files analysed").withType(EnumLogType.LAUNCHER).end();
		} catch (final Exception ex) { ex.printStackTrace(); }
	}

	private static void writeFile(GameEngine engine, GameUpdater updater, Node node) {
		if (node.getNodeType() == 1) {
			final Element element = (Element) node;
			final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue().replace("\n", "");
			final long size = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
			String etag = element.getElementsByTagName("ETag") != null ? element.getElementsByTagName("ETag").item(0).getChildNodes().item(0).getNodeValue() : "-";
			File localFile = new File(engine.getGameFolder().getGameDir(), key);
			
			GameVerifier.addToFileList(localFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
			if (key.toLowerCase().contains(PhotonInfosManager.getInfos().project_id) && key.endsWith(".jar")) updater.hasModJar = true;
			if (!localFile.isDirectory()) {
				if (etag.length() > 1) {
					etag = FileUtil.getEtag(etag);
					if (localFile.exists()) {
						if (localFile.isFile() && localFile.length() == size) {
							final String localMd5 = FileUtil.getMD5(localFile);
							if (!localMd5.equals(etag)) {
								if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
									updater.files.add(key);
									updater.filesToDownload++;
								}
							}
						} else {
							if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
								updater.files.add(key);
								updater.filesToDownload++;
							}
						}
					} else {
						if (!(engine.getGameLinks().getCustomFilesUrl() + key).endsWith("/")) {
							updater.files.add(key);
							updater.filesToDownload++;
						}
					}

				}
			}
		}
	}

	private static void downloadXMLFile(GameEngine engine) {
		final long start = System.nanoTime();

		final File theFile = new File(engine.getGameFolder().getCacheDir(), "downloads.xml");
		GameVerifier.addToFileList(theFile.getAbsolutePath().replace(engine.getGameFolder().getCacheDir().getAbsolutePath(), "").replace('/', File.separatorChar));
		try {
			URL url = new URL(engine.getGameLinks().getCustomFilesUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			ProtectorManager.addProperties(connection);
			connection.connect();
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(theFile));
			byte[] data = new byte[connection.getContentLength() > 0? connection.getContentLength() : 1024];
			int i = 0;
			while ((i = in.read(data)) >= 0) bout.write(data, 0, i);
			bout.close();
			in.close();
		} catch (Exception e) { e.printStackTrace(); }

		final long delta = System.nanoTime() - start;
		ConsoleManager.create("Time (delta) to get XML file: " + delta / 1000000L + " ms").withType(EnumLogType.LAUNCHER).end();
	}
}