package com.launcher.utils.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.assets.AssetIndex;
import com.launcher.utils.assets.AssetObject;
import com.launcher.utils.file.FileUtil;
import com.launcher.utils.file.JsonUtil;
import com.launcher.utils.minecraft.CompatibilityRule;
import com.launcher.utils.minecraft.CompatibilityRule.Action;
import com.launcher.utils.minecraft.java.JVMFile;
import com.launcher.utils.minecraft.java.JVMManifest;
import com.launcher.utils.minecraft.java.JavaManifest;
import com.launcher.utils.minecraft.java.JavaRuntime;
import com.launcher.utils.minecraft.json.MinecraftLibrary;
import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.photon.informations.PhotonUpdaterManager;
import com.photon.informations.PhotonUpdaterManager.UpdateFileType;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.os.Arch;
import com.photon.util.os.OperatingSystem;

public class GameUpdater {
	
	private GameFolder workDir;
	/**
	 * The libraries Executor
	 */
	private ExecutorService jarsExecutor = Executors.newFixedThreadPool(5);
	/**
	 * The assets Executor
	 */
	private ExecutorService assetsExecutor = Executors.newFixedThreadPool(5);
	/**
	 * The java Executor
	 */
	private ExecutorService javaExecutor = Executors.newFixedThreadPool(5);
	/**
	 * The custom files Executor
	 */
	private ExecutorService filesExecutor = Executors.newFixedThreadPool(5);

	/**
	 * The custom files
	 */
	protected ArrayList<String> files = new ArrayList<>();
	
	/**
	 * The Minecraft JVM manifest
	 */
	public JVMManifest jvmManifest;
	/**
	 * The Minecraft Java Manifest
	 */
	public JavaManifest javaManifest;
	/**
	 * The Java style
	 */
	public String javaStyle;
	/**
	 * The current Info text of the progressbar
	 */
	private String currentInfoText = "";
	/**
	 * The current file of the progressbar
	 */
	private String currentFile = "";
	/**
	 * The AssetIndex
	 */
	public AssetIndex assetsList;
	private MinecraftVersion minecraftVersion;
	protected GameEngine engine;
	/**
	 * The downloaded custom files
	 */
	public int downloadedFiles = 0;
	/**
	 * The custom files to download
	 */
	public int filesToDownload = 0;
	/**
	 * The Assets Url
	 */
	private static final String ASSETS_URL = "https://resources.download.minecraft.net/";

	/**
	 * The game files verifier
	 */
	protected final GameVerifier gameVerifier;
	
	public GameUpdater(MinecraftVersion mcVersion, GameEngine engine) {
		this.minecraftVersion = mcVersion;
		this.engine = engine;
		this.engine.reg(mcVersion);
		this.workDir = engine.getGameFolder();
		this.gameVerifier = new GameVerifier(engine);
	}
	
	/**
	 * Reset updater executors, files, ... (Game has crashed)
	 */
	public void reset() {
		jarsExecutor = Executors.newFixedThreadPool(5);
		assetsExecutor = Executors.newFixedThreadPool(5);
		javaExecutor = Executors.newFixedThreadPool(5);
		filesExecutor = Executors.newFixedThreadPool(5);
		this.downloadedFiles = 0;
		this.filesToDownload = 0;
	}

	/**
	 * @param session
	 */
	public void downloadGameAndRun(Thread pb) {
		/* Getting infos */
		ConsoleManager.create("Start Download Files").withType(EnumLogType.LAUNCHER).end();
		GameParser.getFilesToDownload(this.engine, this);
		
		ConsoleManager.create("getting files to ignore").withType(EnumLogType.LAUNCHER).end();
		pb.start();
		this.gameVerifier.getIgnoreList();

		ConsoleManager.create("getting file to delete").withType(EnumLogType.LAUNCHER).end();
		this.gameVerifier.getDeleteList();

		/* Updating */
		ConsoleManager.create("start assets updating").withType(EnumLogType.LAUNCHER).end();
		this.updateAssets();

		ConsoleManager.create("start jars updating").withType(EnumLogType.LAUNCHER).end();
		this.updateJars();

		ConsoleManager.create("start other files updating").withType(EnumLogType.LAUNCHER).end();
		this.updateCustomFiles();

		ConsoleManager.create("start java download").withType(EnumLogType.LAUNCHER).end();
		this.downloadJavaManifest();

		/* Verify files before launching */
		ConsoleManager.create("verify all change").withType(EnumLogType.LAUNCHER).end();
		this.gameVerifier.verify();

		/* Start the game if all files are downloaded */
		ConsoleManager.create("start game").withType(EnumLogType.LAUNCHER).end();
		this.runGame();
	}
	
	/**
	 * Update minecraft assets
	 */
	public void updateAssets() {
		String json = null;
		String assetUrl = minecraftVersion.getAssetIndex().getUrl().toString();
		AssetIndex assetsList;
		try { json = JsonUtil.loadJSON(assetUrl); } catch (IOException e) { e.printStackTrace(); }
		finally { assetsList = (AssetIndex) JsonUtil.getGson().fromJson(json, AssetIndex.class); }
		Map<String, AssetObject> objects = assetsList.getObjects();
		for (String assetKey : objects.keySet()) {
			AssetObject asset = (AssetObject) objects.get(assetKey);
			File local = getAsset(asset.getHash());

			local.getParentFile().mkdirs();
			if ((!local.exists()) || (!FileUtil.matchSHA1(local, asset.getHash()))) {
				Downloader downloadTask = new Downloader(local, toURL(asset.getHash()), asset.getHash(), this);
				if (downloadTask.requireUpdate()) {
					this.assetsExecutor.submit(downloadTask);
					this.filesToDownload++;
					ConsoleManager.create("Downloading asset " + local.getName()).withType(EnumLogType.LAUNCHER).end();
				}
			}
		}
		this.assetsExecutor.shutdown();
		File indexes = new File(workDir.getAssetsDir(), "indexes");
		indexes.mkdirs();
		File index = new File(indexes, minecraftVersion.getAssets() + ".json");
		if (!index.exists()) {
			try {
				index.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(index));
				writer.write(JsonUtil.getGson().toJson(assetsList));
				writer.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
		try { this.assetsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * @return The assetsList
	 */
	protected AssetIndex getAssetsList() { return assetsList; }
	
	/**
	 * @param hash The hash
	 * @return The asset File
	 */
	private File getAsset(String hash) {
		File assetsDir = workDir.getAssetsDir();
		File mcObjectsDir = new File(assetsDir, "objects");
		File hex = new File(mcObjectsDir, hash.substring(0, 2));
		return new File(hex, hash);
	}
	
	/**
	 * @param hash The hash
	 * @return The hash url of the assets
	 */
	private String toURL(String hash) { return ASSETS_URL + hash.substring(0, 2) + "/" + hash; }
	
	/**
	 * Update custom files
	 */
	public void updateCustomFiles() {
		for (String name : this.files) {
			String fileDest = name.replace(engine.getGameLinks().getCustomFilesUrl(), "");
			String fileName = fileDest;
			int index = fileName.lastIndexOf("\\");
			String dirLocation = fileName.substring(index + 1);

			File libPath = new File(engine.getGameFolder().getGameDir() + File.separator + dirLocation);
			String url = engine.getGameLinks().getCustomFilesUrl() + name;

			final Downloader customDownloadTask = new Downloader(libPath, url, null, this);
			GameVerifier.addToFileList(libPath.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
			if (customDownloadTask.requireUpdate()) {
				this.filesExecutor.submit(customDownloadTask);
				this.filesToDownload++;
			}
		}
		filesExecutor.shutdown();
		try { filesExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Update minecraft libraries
	 */
	@SuppressWarnings({ "unused" })
	public void updateJars() {
		FileUtil.deleteFolder(workDir.getNativesCacheDir());
		for (MinecraftLibrary lib : minecraftVersion.getLibraries()) {
			final File libPath = new File(workDir.getLibsDir(), lib.getArtifactPath());
			if (lib.getCompatibilityRules() != null) {
				for (final CompatibilityRule rule : lib.getCompatibilityRules()) {
					if (rule.getOs() != null && rule.getAction() != null) {
						for (final String os : rule.getOs().getName().getAliases()) {
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
			}

			if (!lib.isSkipped()) {
				if (lib.appliesToCurrentEnvironment()) {
					if (lib.getArtifact() != null) {
						final Downloader downloadTask = new Downloader(libPath, lib.getArtifact().getUrl().toString(), lib.getArtifact().getSha1(), this);
						if (downloadTask.requireUpdate()) {
							this.jarsExecutor.submit(downloadTask);
							this.filesToDownload++;
						}
					}
					if (lib.getClassifiers() != null) {
					final Map<OperatingSystem, String> natives = lib.getNatives();
					if (natives != null && natives.containsKey(OperatingSystem.getCurrent())) {
						String nativesName = natives.get(OperatingSystem.getCurrent()).replace("natives-", "");
						final File nativePath = new File(workDir.getNativesCacheDir(), lib.getArtifactNatives(nativesName));
						final Downloader downloadTask8 = new Downloader(nativePath, lib.getClassifiers().get(nativesName).getUrl().toString(), lib.getClassifiers().get(nativesName).getSha1(), this);
						if (downloadTask8.requireUpdate()) {
							this.jarsExecutor.submit(downloadTask8);
							this.filesToDownload++;
						}
					}
				}
					if (lib.getDownloads() != null) {
						if (lib.getDownloads().getArtifact() != null) {
							final Downloader downloadTask = new Downloader(libPath, lib.getDownloads().getArtifact().getUrl().toString(), lib.getDownloads().getArtifact().getSha1(), this);
							if (downloadTask.requireUpdate()) {
								this.jarsExecutor.submit(downloadTask);
								this.filesToDownload++;
							}
						}
						if (lib.getDownloads().getClassifiers() != null) {
							final Map<OperatingSystem, String> nativesClassifier = lib.getNatives();
							if (nativesClassifier != null && nativesClassifier.containsKey(OperatingSystem.getCurrent())) {
								String nativesName = nativesClassifier.get(OperatingSystem.getCurrent()).replace("${arch}", Arch.CURRENT.getBit());
								final File nativePath = new File(workDir.getNativesCacheDir(), lib.getArtifactNatives(nativesName));
								final Downloader downloadTask8 = new Downloader(nativePath, lib.getDownloads().getClassifiers().get(nativesName).getUrl().toString(), lib.getDownloads().getClassifiers().get(nativesName).getSha1(), this);
								if (downloadTask8.requireUpdate()) {
									this.jarsExecutor.submit(downloadTask8);
									this.filesToDownload++;
								}
							}
						}
					}
				}
			}
		}
		File versionFolder = new File(workDir.getVersionsDir(), minecraftVersion.getId());
		final Downloader versionsJar = new Downloader(new File(versionFolder, minecraftVersion.getId() + ".jar"), minecraftVersion.getDownloads().getClient().getUrl().toString(), minecraftVersion.getDownloads().getClient().getSha1(), this);
		if (versionsJar.requireUpdate()) {
			this.jarsExecutor.submit(versionsJar);
			this.filesToDownload++;
		}

		final String modFileName = "mod.jar";
		final File modFile = new File(engine.getGameFolder().getPlayDir(), "mods/"+modFileName);
		if(!modFile.exists()) modFile.getParentFile().mkdirs();
        final Downloader downloadModTask = new Downloader(modFile, PhotonUpdaterManager.getURL(UpdateFileType.MOD, LauncherConfig.getConfig().versionChannel), PhotonUpdaterManager.getSHA1(UpdateFileType.MOD), this);
        GameVerifier.addToFileList(modFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
        if (downloadModTask.requireUpdate()) {
            this.jarsExecutor.submit(downloadModTask);
            this.filesToDownload++;
        }
        
		final File photonFile = new File(engine.getGameFolder().getLibsDir(), "com/photon/api.jar");
		if(!photonFile.exists()) photonFile.getParentFile().mkdirs();
        final Downloader downloadAPITask = new Downloader(photonFile, PhotonUpdaterManager.getURL(UpdateFileType.API, LauncherConfig.getConfig().versionChannel), PhotonUpdaterManager.getSHA1(UpdateFileType.API), this);
        GameVerifier.addToFileList(photonFile.getAbsolutePath().replace(engine.getGameFolder().getGameDir().getAbsolutePath(), "").replace('/', File.separatorChar));
        if (downloadAPITask.requireUpdate()) {
            this.jarsExecutor.submit(downloadAPITask);
            this.filesToDownload++;
        }

		this.jarsExecutor.shutdown();
		try { this.jarsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	private void downloadJavaManifest() {
		if (minecraftVersion.getJavaVersion() != null) {
			String json = null;
			String manifestUrl = "https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json";
			try { json = JsonUtil.loadJSON(manifestUrl); } catch (IOException e) { e.printStackTrace(); }
			finally {
				this.javaManifest = (JavaManifest) JsonUtil.getGson().fromJson(json, JavaManifest.class);
				ConsoleManager.create("CurrentRuntime: " + this.javaManifest.getCurrentOS()).withType(EnumLogType.LAUNCHER).end(); // windows-x64
				Map<String, List<JavaRuntime>> r = this.javaManifest.getCurrentJava();
				for (String run : r.keySet()) {
					if (run.equals(minecraftVersion.getJavaVersion().getComponent())) {
						ConsoleManager.create("Choosen: " + run).withType(EnumLogType.LAUNCHER).end();
						ArrayList<JavaRuntime> s = (ArrayList<JavaRuntime>) r.get(run);
						this.indexJava(s.get(0).getManifest().getUrl().toString());
						break;
					}
				}
			}
		}
	}
	
	private void indexJava(String url) {
		String javaManifestJson = null;
		try { javaManifestJson = JsonUtil.loadJSON(url); } catch (IOException e) { e.printStackTrace(); }
		finally {
			jvmManifest = (JVMManifest) JsonUtil.getGson().fromJson(javaManifestJson, JVMManifest.class);
			updateJava();
		}
	}
	
	private void updateJava() {
		final Map<String, JVMFile> objects = this.jvmManifest.getFiles();
		for (String javaFile : objects.keySet()) {
			JVMFile jvmFile = (JVMFile) objects.get(javaFile);
			File localFolder = new File(workDir.getRuntimeDir(), this.minecraftVersion.getJavaVersion().getComponent());
			localFolder.mkdirs();
			File local = new File(localFolder, javaFile);
			if (!jvmFile.getType().equals(EnumJavaFileType.DIRECTORY.getName())) {
				Downloader downloadTask = new Downloader(local, jvmFile.getDownloads().getRaw().getUrl().toString(), jvmFile.getDownloads().getRaw().getSha1(), this);
				if (downloadTask.requireUpdate()) {
					this.javaExecutor.submit(downloadTask);
					this.filesToDownload++;
				}
			} else local.mkdirs();
		}
		
		this.javaExecutor.shutdown();
		try { this.javaExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public void runGame() {
		final GameRunner runner = new GameRunner(this.engine, this);
		try { runner.launch(); } catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * @param updater
	 * @param engine
	 * @param session
	 * @param jsonFile
	 * @return Minecraft Version : the MC version
	 */
	public static MinecraftVersion prepareGameUpdate(GameEngine engine) {
		String json = null;
		try { json = JsonUtil.loadJSONFile(downloadVersion(engine.getGameLinks().getJsonUrl(), engine)); } catch (IOException e) { e.printStackTrace(); }
		return (MinecraftVersion) JsonUtil.getGson().fromJson(json, MinecraftVersion.class);
	}

	
	/**
	 * 
	 * @param urlVers Veersion URL
	 * @param engine The game engine instance
	 * @return The downloaded file from web
	 */
	public static File downloadVersion(String urlVers, GameEngine engine) {
		URI uri = null;
		try { uri = new URI(urlVers); } catch (URISyntaxException e1) { e1.printStackTrace(); }
		final String path = uri.getPath();
		final String idStr = path.substring(path.lastIndexOf('/') + 1);
		final File versionIdFolder = new File(engine.getGameFolder().getVersionsDir(), idStr.replace(".json", ""));
		
		versionIdFolder.mkdirs();
		File theFile = new File(versionIdFolder, idStr);
		try {
			URL url = new URL(urlVers);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
		return theFile;
	}

	/**
	 * @return Get current Info text
	 */
	public String getCurrentInfo() { return this.currentInfoText; }

	/**
	 * Set current info text
	 * @param name The text of the info
	 */
	public void setCurrentInfoText(String name) {
		this.currentInfoText = name;
	}

	/**
	 * @return The current File name
	 */
	public String getCurrentFile() {
		return this.currentFile;
	}

	/**
	 * Set current File name
	 * @param name The name
	 */
	public void setCurrentFile(String name) {
		this.currentFile = name;
	}

	public enum EnumJavaFileType {
		FILE("file"), DIRECTORY("directory");

		private String name;

		EnumJavaFileType(String par1Name) { this.name = par1Name; }

		public String getName() { return this.name; }
	}
}