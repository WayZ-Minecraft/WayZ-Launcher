package com.launcher.utils.updater;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.launcher.utils.GameEngine;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.file.FileUtil;
import com.launcher.utils.file.GameUtils;
import com.launcher.utils.minecraft.json.Argument;
import com.launcher.utils.minecraft.json.ArgumentType;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.auth.Session;
import com.photon.util.os.OperatingSystem;

@SuppressWarnings("deprecation")
public class GameRunner {

	private GameEngine engine;
	private Session session;

	public GameRunner(GameEngine gameEngine, Session account) {
		this.engine = gameEngine;
		this.session = account;
		ConsoleManager.print(EnumLogType.LAUNCHER, "Unpacking natives ...");
		this.unpackNatives();
		ConsoleManager.print(EnumLogType.LAUNCHER, "Deleting unrequired Natives ...");
		this.deleteFakeNatives();
		
		LauncherConfig.load(gameEngine);
	}

    public void launch() throws Exception {
    	ArrayList<String> commands = this.getLaunchCommand();
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectInput(Redirect.INHERIT);
        processBuilder.redirectOutput(Redirect.INHERIT);
        processBuilder.redirectError(Redirect.INHERIT);
		processBuilder.directory(engine.getGameFolder().getGameDir());
		processBuilder.redirectErrorStream(true);
		String cmds = "";
		for (String command : commands) cmds += command + " ";
		String[] ary = cmds.split(" ");
		engine.getGameUpdater().setCurrentInfoText("updater.launching");
		ConsoleManager.print(EnumLogType.LAUNCHER, "Launching: " + hideAccessToken(ary));
		try {
			this.engine.getGameUpdater().getFrameToHide().setVisible(false);
			Process process = processBuilder.start();
			process.waitFor();
			int exitVal = process.exitValue();
			if (exitVal != 0) ConsoleManager.print(EnumLogType.LAUNCHER, "Minecraft has crashed!");
			System.exit(0);
		} catch (IOException e) { throw new Exception("Cannot launch !", e); }
	}

	private ArrayList<String> getLaunchCommand() {
		ArrayList<String> commands = new ArrayList<String>();
		OperatingSystem os = OperatingSystem.getCurrentPlatform();
        commands.add(OperatingSystem.getJavaPath());
        commands.add("-XX:-UseAdaptiveSizePolicy");
		
		if (engine.getJVMArguments() != null) {
			commands.addAll(engine.getJVMArguments().getJVMArguments());
		}

		if (os.equals(OperatingSystem.OSX)) {
			commands.add("-Xdock:name=Minecraft");
			commands.add("-Xdock:icon=" + engine.getGameFolder().getAssetsDir() + "icons/minecraft.icns");
		} else if (os.equals(OperatingSystem.WINDOWS)) {
			commands.add("-XX:+UseConcMarkSweepGC");
			commands.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}
		commands.add("-Djava.library.path=" + engine.getGameFolder().getNativesDir().getAbsolutePath());
		commands.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
		commands.add("-Dfml.ignorePatchDiscrepancies=true");

		boolean is32Bit = "32".equals(System.getProperty("sun.arch.data.model"));
		String defaultArgument = is32Bit ? "-Xmx512M -Xmn128M" : "-Xmx1G -Xmn128M";
		if (LauncherConfig.getConfig().allocatedram != null && !LauncherConfig.getConfig().allocatedram.isEmpty()) {
			final int memory = Integer.parseInt(LauncherConfig.getConfig().allocatedram);
			defaultArgument = is32Bit ? "-Xmx512M -Xmn128M" : "-Xmx" + memory+"G -Xmn128M";
		}
		String str[] = defaultArgument.split(" ");
		List<String> args = Arrays.asList(str);
		commands.addAll(args);

		commands.add("-cp");
		commands.add("\"" + GameUtils.constructClasspath(engine) + "\"");
		commands.add("net.minecraft.launchwrapper.Launch");

		if (engine.getMinecraftVersion().getMinecraftArguments() != null) {
	        final String[] argsD = getArgumentsOlder();
	        List<String> arguments = Arrays.asList(argsD);
	        commands.addAll(arguments);
		}
		if (engine.getMinecraftVersion().getArguments() != null) {
			List<Argument> argsNewer = engine.getMinecraftVersion().getArguments().get(ArgumentType.GAME);
			final String[] newerArgumentsString = getArgumentsNewer(argsNewer);

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < newerArgumentsString.length; i++) {
				sb.append(newerArgumentsString[i] + " ");
			}
			String sub = sb.toString().replace("--demo", "").replace("--width", "").replace("--height", "");
			String strcs[] = sub.split(" ");
			List<String> newerList = Arrays.asList(strcs);
			commands.addAll(newerList);
		}

		commands.add("--width=" + 854);
		commands.add("--height=" + 480);
		commands.add("--tweakClass");
		commands.add("net.minecraftforge.fml.common.launcher.FMLTweaker");
		return commands;
	}
	
	private String[] getArgumentsOlder() {
		final Map<String, String> map = new HashMap<String, String>();
		final StrSubstitutor substitutor = new StrSubstitutor(map);
		final String[] split = engine.getMinecraftVersion().getMinecraftArguments().split(" ");
		map.put("auth_player_name", this.session.getUsername());
		map.put("auth_uuid", this.session.getUuid());
		map.put("auth_access_token", this.session.getToken());
		map.put("user_type", "legacy");
		map.put("version_name", this.engine.getMinecraftVersion().getId());
		map.put("version_type", "release");
		map.put("game_directory", this.engine.getGameFolder().getPlayDir().getAbsolutePath());
		map.put("assets_root", this.engine.getGameFolder().getAssetsDir().getAbsolutePath());
		map.put("assets_index_name", this.engine.getMinecraftVersion().getAssets());
		map.put("user_properties", "{}");

		for (int i = 0; i < split.length; i++)
			split[i] = substitutor.replace(split[i]);

		return split;
	}

	private String[] getArgumentsNewer(List<Argument> args) {
		final Map<String, String> map = new HashMap<String, String>();
		final StrSubstitutor substitutor = new StrSubstitutor(map);
		final String[] split = new String[args.size()];
		for (int i = 0; i < args.size(); i++) {
			split[i] = args.get(i).getArguments();
		}
		map.put("auth_player_name", this.session.getUsername());
		map.put("auth_uuid", this.session.getUuid());
		map.put("auth_access_token", this.session.getToken());
		map.put("user_type", "legacy");
		map.put("version_name", this.engine.getMinecraftVersion().getId());
		map.put("version_type", "release");
		map.put("game_directory", this.engine.getGameFolder().getPlayDir().getAbsolutePath());
		map.put("assets_root", this.engine.getGameFolder().getAssetsDir().getAbsolutePath());
		map.put("assets_index_name", this.engine.getMinecraftVersion().getAssets());
		map.put("user_properties", "{}");

		for (int i = 0; i < split.length; i++)
			split[i] = substitutor.replace(split[i]);

		return split;
	}

	private void unpackNatives() {
		try {
			FileUtil.unpackNatives(engine.getGameFolder().getNativesDir(), engine);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void deleteFakeNatives() {
		try {
			FileUtil.deleteFakeNatives(engine.getGameFolder().getNativesDir(), engine);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static List<String> hideAccessToken(String[] arguments) {
        final ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0 && Objects.equals(arguments[i-1], "--accessToken")) {
                output.add("????????");
            } else {
                output.add(arguments[i]);
            }
        }
        return output;
    }
}