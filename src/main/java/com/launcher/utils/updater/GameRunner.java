package com.launcher.utils.updater;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.launcher.utils.GameEngine;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.file.FileUtil;
import com.launcher.utils.file.GameUtils;
import com.launcher.utils.minecraft.json.Argument;
import com.launcher.utils.minecraft.json.ArgumentType;
import com.launcher.utils.minecraft.json.MinecraftVersion;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.os.OperatingSystem;

public class GameRunner {

	private GameEngine engine;
	private GameUpdater updater;

	public GameRunner(GameEngine gameEngine, GameUpdater updater) {
		this.engine = gameEngine;
		this.updater = updater;
		ConsoleManager.print(EnumLogType.LAUNCHER, "Cleaning and unpacking natives...");
		this.unpackNatives();
		LauncherConfig.load(gameEngine);
	}

    public void launch() throws Exception {
    	final ArrayList<String> commands = this.getLaunchCommand();
        final ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectInput(Redirect.INHERIT);
        processBuilder.redirectOutput(Redirect.INHERIT);
        processBuilder.redirectError(Redirect.INHERIT);
		processBuilder.directory(engine.getGameFolder().getGameDir());
		processBuilder.redirectErrorStream(true);
		updater.setCurrentInfoText("updater.launching");
		
		/* Display configuration */
		List<String> commandLine = processBuilder.command();
		ConsoleManager.create(String.join(" ", commandLine)).end();

		try {
			this.engine.startRunnable.run();
			final Process process = processBuilder.start();
			final int exitVal = process.waitFor();
			if (exitVal != 0) {
				/* Show the frames and active buttons */
				try { process.waitFor(); } catch (InterruptedException e) { e.printStackTrace(); }
				this.engine.crashRunnable.run();
				this.updater.reset();
				/* Log the error */
				ConsoleManager.create("Process exited with code '"+exitVal+"', game has crashed").withType(EnumLogType.LAUNCHER).error().end();
			} else {
				if(!LauncherConfig.getConfig().keep_open) System.exit(0);
				else {
					this.engine.exitRunnable.run();
					this.updater.reset();
				}
			}
		} catch (IOException e) { throw new Exception("Cannot launch !", e); }
	}

	private ArrayList<String> getLaunchCommand() {
		final ArrayList<String> commands = new ArrayList<String>();
		final OperatingSystem os = OperatingSystem.getCurrentPlatform();
		final MinecraftVersion minecraftVersion = engine.getMinecraftVersion();
		final boolean hasCustomJVM = minecraftVersion.getJavaVersion() != null;

		if (hasCustomJVM && minecraftVersion.getJavaVersion().getComponent() != null) commands.add(GameEngine.getJavaPath(minecraftVersion, engine));
	 	else commands.add(OperatingSystem.getJavaPath());

		if (os.equals(OperatingSystem.OSX)) {
			commands.add("-Xdock:name=Minecraft");
			commands.add("-Xdock:icon=" + engine.getGameFolder().getAssetsDir() + "icons/minecraft.icns");
		} else if (os.equals(OperatingSystem.WINDOWS)) {
			if (minecraftVersion.getArguments() == null) commands.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}
		
		if (hasCustomJVM) {
			commands.add("-XX:+UnlockExperimentalVMOptions");
			commands.add("-XX:+UseG1GC");
			commands.add("-XX:G1NewSizePercent=20");
			commands.add("-XX:G1ReservePercent=20");
			commands.add("-XX:MaxGCPauseMillis=50");
			commands.add("-XX:G1HeapRegionSize=32M");
			commands.add("-Djava.net.preferIPv4Stack=true");
			commands.add("-Dminecraft.applet.TargetDirectory=" + engine.getGameFolder().getGameDir());
		}
		
		final String customArgs = LauncherConfig.getConfig().vmarguments;
		if(customArgs !=null && !customArgs.isEmpty()) {
			final String str2[] = customArgs.split(" ");
			commands.addAll(Arrays.asList(str2));
		}

		commands.add("-Xmx" + (LauncherConfig.getConfig().allocatedram*1024) + "M");
		commands.add("-Djava.library.path=" + engine.getGameFolder().getNativesDir().getAbsolutePath());
		commands.add("-Dminecraft.launcher.brand=Minecraft");
		commands.add("-Dminecraft.launcher.version=999");
		commands.add("-cp");
		commands.add("\"" + GameUtils.constructClasspath(engine) + "\"");
		commands.add("-DFabricMcEmu=net.minecraft.client.main.Main");

		commands.add(minecraftVersion.getMainClass());

		/** ----- Minecraft Arguments ----- */
		if (minecraftVersion.getMinecraftArguments() != null) commands.addAll(Arrays.asList(getArgumentsOlder()));

		/** ----- Minecraft Arguments 1.13+ ----- */
		if (minecraftVersion.getArguments() != null) {
			final String[] defaultArguments = getArgumentsNewer(minecraftVersion.getArguments().get(ArgumentType.GAME));
			
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < defaultArguments.length; i++) sb.append(defaultArguments[i] + " ");
			String sub = sb.toString().replace("--demo", "").replace("--width", "").replace("--height", "");
			String strcs[] = sub.split(" ");
			List<String> newerList = Arrays.asList(strcs);
			commands.addAll(newerList);
		}
		
		/** ----- Size of window ----- */
		if(LauncherConfig.getConfig().useCustomSize) {
			commands.add("--width");
			commands.add(LauncherConfig.getConfig().screenWidth);
			commands.add("--height");
			commands.add(LauncherConfig.getConfig().screenHeight);
		}
		
		return commands;
	}
	
	private String[] getArgumentsOlder() {
		final HashMap<String, String> map = new HashMap<String, String>();
		final String[] split = engine.getMinecraftVersion().getMinecraftArguments().split(" ");
		/* Creating RAND UUID -> IG account system */
		map.put("auth_player_name", UUID.randomUUID().toString().substring(0, 16));
		map.put("auth_uuid", UUID.randomUUID().toString());
		map.put("auth_access_token", UUID.randomUUID().toString());
		map.put("user_type", "legacy");
		map.put("version_name", this.engine.getMinecraftVersion().getId());
		map.put("version_type", "release");
		map.put("game_directory", this.engine.getGameFolder().getPlayDir().getAbsolutePath());
		map.put("assets_root", this.engine.getGameFolder().getAssetsDir().getAbsolutePath());
		map.put("assets_index_name", this.engine.getMinecraftVersion().getAssets());
		map.put("user_properties", "{}");
		for (int i = 0; i < split.length; i++) {
			if(!split[i].contains("${")) continue;
			final String arg = split[i].substring(split[i].indexOf("{")+1, split[i].indexOf("}"));
			if(map.get(arg) !=null) split[i] = split[i].replace(arg, map.get(arg));
			split[i] = split[i].replace("${", "").replace("}", "");
		}
		return split;
	}

	private String[] getArgumentsNewer(List<Argument> args) {
		final HashMap<String, String> map = new HashMap<String, String>();
		final String[] split = new String[args.size()];
		for (int i = 0; i < args.size(); i++) split[i] = args.get(i).getArguments();
		/* Creating RAND UUID -> IG account system */
		map.put("auth_player_name", UUID.randomUUID().toString().substring(0, 16));
		map.put("auth_uuid", UUID.randomUUID().toString());
		map.put("auth_access_token", UUID.randomUUID().toString());
		map.put("user_type", "legacy");
		map.put("version_name", this.engine.getMinecraftVersion().getId());
		map.put("version_type", "release");
		map.put("game_directory", this.engine.getGameFolder().getPlayDir().getAbsolutePath());
		map.put("assets_root", this.engine.getGameFolder().getAssetsDir().getAbsolutePath());
		map.put("assets_index_name", this.engine.getMinecraftVersion().getAssets());
		map.put("user_properties", "{}");
		for (int i = 0; i < split.length; i++) {
			if(!split[i].contains("${")) continue;
			final String arg = split[i].substring(split[i].indexOf("{")+1, split[i].indexOf("}"));
			if(map.get(arg) !=null) split[i] = split[i].replace(arg, map.get(arg));
			split[i] = split[i].replace("${", "").replace("}", "");
		}

		return split;
	}

	private void unpackNatives() {
		try { FileUtil.unpackNatives(engine.getGameFolder().getNativesDir(), engine); }
		catch (IOException e) { e.printStackTrace(); }
	}

	public static List<String> hideAccessToken(String[] arguments) {
        final ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0 && Objects.equals(arguments[i-1], "--accessToken")) output.add("????????");
            else output.add(arguments[i]);
        }
        return output;
    }
}