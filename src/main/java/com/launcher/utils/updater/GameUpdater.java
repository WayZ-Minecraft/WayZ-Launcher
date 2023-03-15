package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameVerifier;
import com.launcher.utils.assets.AssetIndex;
import com.launcher.utils.assets.AssetObject;
import com.launcher.utils.file.FileUtil;
import com.launcher.utils.file.JsonUtil;
import com.launcher.utils.file.LauncherFile;
import com.launcher.utils.minecraft.CompatibilityRule;
import com.launcher.utils.minecraft.CompatibilityRule.Action;
import com.launcher.utils.minecraft.json.MinecraftLibrary;
import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.photon.informations.PhotonInfosManager;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.ProtectorManager;
import com.photon.util.auth.GameAuth;
import com.photon.util.os.Arch;
import com.photon.util.os.FileLocation;
import com.photon.util.os.MultiThreadWorker;

public class GameUpdater extends Thread {

	public HashMap<String, LauncherFile> files = new HashMap<String, LauncherFile>();

	private static final String ASSETS_URL = "http://resources.download.minecraft.net/";

	private String HOST = "http://www.google.com";

	public static MinecraftVersion minecraftVersion;

	public static MinecraftVersion minecraftLocalVersion;

	public boolean hasCustomJar = false;
	public boolean hasModJar = false;

	public AssetIndex assetsList;

	private JFrame frameToHide;
	
	public GameEngine engine;

	private GameVerifier verifier;

	private ExecutorService assetsExecutor = Executors.newFixedThreadPool(5);

	private ExecutorService customJarsExecutor = Executors.newFixedThreadPool(5);

	private ExecutorService jarsExecutor = Executors.newFixedThreadPool(5);

	private String currentFile = "";

	public int downloadedFiles = 0;
	public long downloadedMbDownload = 0;

	public int filesToDownload = 0;
	public long filesMbDownload = 0;
	
	public String currentInfoText = "";
	
	public void reg(GameEngine gameEngine) { this.engine = gameEngine; }

	public void setCurrentInfoText(String name) { this.currentInfoText = name; }
	
	public int addMB(int b) {
		if(b > 0) this.downloadedMbDownload += b;
		return b;
	}
	
	@Override
	public void run() {
		HOST = engine.getGameLinks().getBaseUrl();
		if (this.isOnline()) {
			ConsoleManager.print(EnumLogType.LAUNCHER, "=============UPDATING GAME==============");
			this.setCurrentInfoText("updater.start");
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Updating Local Minecraft Version.");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.downloadVersion();

			this.verifier = new GameVerifier(this.engine);
			ConsoleManager.print(EnumLogType.LAUNCHER, "Getting ignore/delete list   [Step 1/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.lists");
			this.verifier.getIgnoreList();
			this.verifier.getDeleteList();
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Indexing version              [Step 2/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.indexing.version");
			this.indexVersion();			
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Indexing assets               [Step 3/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.indexing.assets");
			this.indexAssets();
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Indexing custom jars          [Step 3/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.indexing.jars");
			GameParser.getFilesToDownload(this.engine);
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Updating assets               [Step 4/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.downloading.assets");
			this.updateAssets();
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Updating jars/libraries       [Step 5/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.downloading.jars");
			this.updateJars();
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Updating custom jars         [Step 5/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.updateCustomJars();
			
			this.customJarsExecutor.shutdown();
			try { this.customJarsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
			ConsoleManager.print(EnumLogType.LAUNCHER, "Cleaning installation         [Step 6/6]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.cleaning");

			this.verifier.verify();
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|      Update Finished. Launching.     |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|            Version " + minecraftVersion.getId() + "            |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|          Runtime: " + System.getProperty("java.version") + "          |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			ConsoleManager.print(EnumLogType.LAUNCHER, "\n\n");
			ConsoleManager.print(EnumLogType.LAUNCHER, "==============GAME OUTPUT===============");
			this.setCurrentInfoText("updater.downloading.complete");
			
			GameRunner gameRunner = new GameRunner(this.engine, GameAuth.getSession());
			try { gameRunner.launch(); } catch (Exception e) { e.printStackTrace(); }
		}
		else {
			ConsoleManager.print(EnumLogType.LAUNCHER, "\n\n");
			ConsoleManager.print(EnumLogType.LAUNCHER, "=========UPDATING GAME OFFLINE==========");
			this.setCurrentInfoText("updater.start");
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Indexing local version         [Step 1/1]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.indexing.version");
			this.indexLocalVersion();
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "Indexing custom local jars   [Extra Step]");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			this.setCurrentInfoText("updater.indexing.jars");
			GameParser.getFilesToDownloadOffline(engine);
			
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|      Update Finished. Launching.     |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|            Version " + minecraftLocalVersion.getId() + "            |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "|          Runtime: " + System.getProperty("java.version") + "          |");
			ConsoleManager.print(EnumLogType.LAUNCHER, "========================================");
			ConsoleManager.print(EnumLogType.LAUNCHER, "\n\n");
			ConsoleManager.print(EnumLogType.LAUNCHER, "==============GAME OUTPUT===============");
			this.setCurrentInfoText("updater.downloading.complete");
			
			GameRunner gameRunner = new GameRunner(this.engine, GameAuth.getSession());
			try { gameRunner.launch(); } catch (Exception e) { e.printStackTrace(); }
		}
	}

	public void downloadVersion() {
		File theFile = new File(engine.getGameFolder().getCacheDir(), engine.getGameLinks().getJsonName());
		GameVerifier.addToFileList(theFile.getAbsolutePath().replace(engine.getGameFolder().getCacheDir().getAbsolutePath(), "").replace('/', File.separatorChar));
		try {
			URL url = new URL(this.engine.getGameLinks().getJsonUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			ProtectorManager.addProperties(connection);
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

	public static String generateLot() {
		String lot = "";
		SimpleDateFormat year = new SimpleDateFormat("YY");
		SimpleDateFormat hour = new SimpleDateFormat("HHmmss");
		Date date = new Date();
		int julianDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		lot = "L" + year.format(date) + julianDay + "/" + hour.format(date);
		return lot;
	}

	public static String constructClasspath(GameEngine engine) {
		String result = "";
		String separator = System.getProperty("path.separator");
		for (MinecraftLibrary lib : minecraftVersion.getLibraries()) {
			File libPath = new File(engine.getGameFolder().getLibsDir(), lib.getArtifactPath());
			result += libPath + separator;
		}
		result += engine.getGameFolder().getGameJar().getAbsolutePath();
		return result;
	}

	public void writeJar(MinecraftLibrary lib) {
		File libPath = new File(engine.getGameFolder().getLibsDir(), lib.getArtifactPath());
		GameVerifier.addToFileList(libPath.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
		if (lib.getCompatibilityRules() != null) {
			for (final CompatibilityRule rule : lib.getCompatibilityRules()) {
				if (rule.getOs() != null && rule.getAction() != null) {
					if (lib.appliesToCurrentEnvironment()) {
						if (rule.getAction().equals(Action.disallow)) lib.setSkipped(true);
						else lib.setSkipped(false);
					} else {
						if (rule.getAction().equals(Action.allow)) lib.setSkipped(false);
						else lib.setSkipped(true);
					}
				}
			}
		}

		if (!lib.isSkipped()) {
			if (lib.getDownloads().getArtifact() != null) {
				final Downloader downloadTask = new Downloader(libPath,
						lib.getDownloads().getArtifact().getUrl().toString(),
						lib.getDownloads().getArtifact().getSha1(), engine);
				if (downloadTask.requireUpdate()) {
					if (!verifier.existInDeleteList(libPath.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) {
						filesToDownload++;
						jarsExecutor.submit(downloadTask);
					}
				}
			}

			if (lib.hasNatives()) {
				for (final String osName : lib.getNatives().values()) {
					String realOsName = osName.replace("${arch}", Arch.CURRENT.getBit());
					if (lib.getDownloads().getClassifiers().get(realOsName) != null) {
						final File nativePath = new File(engine.getGameFolder().getNativesCacheDir(),
								lib.getArtifactNatives(realOsName));
						GameVerifier.addToFileList(nativePath.getAbsolutePath()
								.replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "")
								.replace('/', File.separatorChar));
						final Downloader downloadTask8 = new Downloader(nativePath,
								lib.getDownloads().getClassifiers().get(realOsName).getUrl().toString(),
								lib.getDownloads().getClassifiers().get(realOsName).getSha1(), engine);
						if (downloadTask8.requireUpdate()) {
							if (!verifier.existInDeleteList(nativePath.getAbsolutePath()
									.replace(engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) {
								filesToDownload++;
								jarsExecutor.submit(downloadTask8);
							}
						}
					}
				}
			}
		}
	}

	private static int filesAnalysed = 0;
	public void updateJars() {
		final Iterator<MinecraftLibrary> jarsIterator = minecraftVersion.getLibraries().iterator();
		final long start = System.nanoTime();
		new MultiThreadWorker() {
			@Override
			protected boolean work() {
				synchronized (jarsIterator) {
					final MinecraftLibrary lib = jarsIterator.next();
					if (jarsIterator.hasNext()) {
						synchronized(lib) {
							filesAnalysed++;
							writeJar(lib);
							return true;
						}
					}
				}
				return false;
			}
		};

		final File minecraftJarFile = new File(engine.getGameFolder().getBinDir(), "minecraft.jar");
		final Downloader downloadTask3 = new Downloader(minecraftJarFile, minecraftVersion.getDownloads().getClient().getUrl().toString(), minecraftVersion.getDownloads().getClient().getSha1(), engine);
		GameVerifier.addToFileList(minecraftJarFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
		
		if (downloadTask3.requireUpdate()) {
			if (!this.hasCustomJar) {
				this.jarsExecutor.submit(downloadTask3);
				this.filesToDownload++;
				filesAnalysed++;
			}
		}

		final String modFileName = PhotonInfosManager.getInfos().project_id+"-"+PhotonInfosManager.getLatestModUpdate()+".jar";
		final File modFile = new File(engine.getGameFolder().getBinDir(), "game/mods/"+modFileName);

		for(File mod : modFile.getParentFile().listFiles()) {
			if(mod.getName().contains(PhotonInfosManager.getInfos().project_id) && !mod.getName().equalsIgnoreCase(modFileName) && !mod.getName().contains(PhotonInfosManager.getLatestModUpdate())) {
				this.verifier.deleteList.add(mod.getAbsolutePath().replace('/', File.separatorChar));
				mod.delete();
			}
		}

		final Downloader downloadModTask = new Downloader(modFile, PhotonInfosManager.getLatestModURL(), PhotonInfosManager.getLatestModSHA1(), engine);
		GameVerifier.addToFileList(modFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
				
		if (downloadModTask.requireUpdate()) {
			if (!this.hasModJar) {
				this.jarsExecutor.submit(downloadModTask);
				this.filesToDownload++;
				filesAnalysed++;
			}
		}
		
		this.jarsExecutor.shutdown();
		try { this.jarsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }

		final long end = System.nanoTime();
		final long delta = end - start;
		ConsoleManager.print(EnumLogType.LAUNCHER, "Time (delta) to update jars: " + delta / 1000000L + " ms");
		ConsoleManager.print(EnumLogType.LAUNCHER, "For : " + filesAnalysed + " files analysed");
	}

	public void updateAssets() {
		String json = null;
		String assetUrl = minecraftVersion.getAssetIndex().getUrl().toString();
		AssetIndex assetsList;
		try { json = JsonUtil.loadJSON(assetUrl); }
		catch (IOException e) { e.printStackTrace(); }
		finally { assetsList = (AssetIndex) JsonUtil.getGson().fromJson(json, AssetIndex.class); }
		Map<String, AssetObject> objects = assetsList.getObjects();
		for (String assetKey : objects.keySet()) {
			AssetObject asset = (AssetObject) objects.get(assetKey);
			File mc = getAssetInMcFolder(asset.getHash());
			File local = getAsset(asset.getHash());

			GameVerifier.addToFileList(
					local.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "")
							.replace('/', File.separatorChar));

			local.getParentFile().mkdirs();
			if ((!local.exists()) || (!FileUtil.matchSHA1(local, asset.getHash()))) {
				if ((!local.exists()) && (mc.exists()) && (FileUtil.matchSHA1(mc, asset.getHash()))) {
					this.assetsExecutor.submit(new Duplicator(mc, local));
					ConsoleManager.print(EnumLogType.LAUNCHER, "Copying asset " + local.getName());
				} else {
					Downloader downloadTask = new Downloader(local, toURL(asset.getHash()), asset.getHash(), engine);
					if (downloadTask.requireUpdate()) {
						this.assetsExecutor.submit(downloadTask);
						this.filesToDownload++;
						ConsoleManager.print(EnumLogType.LAUNCHER, "Downloading asset " + local.getName());
					}
				}
			}
		}
		this.assetsExecutor.shutdown();
		File indexes = new File(engine.getGameFolder().getAssetsDir(), "indexes");
		indexes.mkdirs();
		File index = new File(indexes, minecraftVersion.getAssets() + ".json");

		GameVerifier.addToFileList(index.getAbsolutePath()
				.replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));

		if (!index.exists()) {
			try {
				index.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(index));
				writer.write(JsonUtil.getGson().toJson(assetsList));
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.assetsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}

	public void indexVersion() {
		String json = null;
		try {
			json = JsonUtil.loadJSON(engine.getGameLinks().getJsonUrl());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			minecraftVersion = (MinecraftVersion) JsonUtil.getGson().fromJson(json, MinecraftVersion.class);
			engine.reg(minecraftVersion);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void indexLocalVersion() {
		File f = new File(engine.getGameFolder().getCacheDir(), engine.getGameLinks().getJsonName());
		String json = null;
		try {
			json = JsonUtil.loadJSON(f.toURL().toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			minecraftLocalVersion = (MinecraftVersion) JsonUtil.getGson().fromJson(json, MinecraftVersion.class);
			engine.reg(minecraftLocalVersion);
		}
	}

	public void indexAssets() {
		String json = null;
		String assetUrl = minecraftVersion.getAssetIndex().getUrl().toString();
		try {
			json = JsonUtil.loadJSON(assetUrl);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			assetsList = (AssetIndex) JsonUtil.getGson().fromJson(json, AssetIndex.class);
		}
	}

	public AssetIndex getAssetsList() {
		return assetsList;
	}

	private String toURL(String hash) {
		return ASSETS_URL + hash.substring(0, 2) + "/" + hash;
	}

	private void updateCustomJars() {
		for (String name : this.files.keySet()) {
			String fileDest = name.replace(engine.getGameLinks().getCustomFilesUrl(), "");
			String fileName = fileDest;
			int index = fileName.lastIndexOf("\\");
			String dirLocation = fileName.substring(index + 1);

			File libPath = new File(engine.getGameFolder().getGameDir() + File.separator + dirLocation);
			String url = engine.getGameLinks().getCustomFilesUrl() + name;

			final Downloader customDownloadTask = new Downloader(libPath, url, null, engine);
			if (!verifier.existInDeleteList(libPath.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), ""))) {
				this.customJarsExecutor.submit(customDownloadTask);
			}
		}
	}

	private File getAsset(String hash) {
		File assetsDir = this.engine.getGameFolder().getAssetsDir();
		File mcObjectsDir = new File(assetsDir, "objects");
		File hex = new File(mcObjectsDir, hash.substring(0, 2));
		return new File(hex, hash);
	}

	private File getAssetInMcFolder(String hash) {
		File minecraftAssetsDir = new File(FileLocation.getWorkingDirectory("minecraft"), "assets");
		File minecraftObjectsDir = new File(minecraftAssetsDir, "objects");
		File hex = new File(minecraftObjectsDir, hash.substring(0, 2));
		return new File(hex, hash);
	}

	public GameEngine getEngine() {
		return engine;
	}

	public String getCurrentFile() {
		return this.currentFile;
	}

	public void setCurrentFile(String name) {
		this.currentFile = name;
	}

	public boolean isOnline() {
		try {
			URLConnection connection = new URL(HOST).openConnection();
			ProtectorManager.addProperties(connection);
			connection.connect();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public void setFrameToHide(JFrame f) { this.frameToHide = f; }
		
	public JFrame getFrameToHide() { return this.frameToHide; }
}
