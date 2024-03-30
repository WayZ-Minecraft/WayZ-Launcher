// const ConsoleWindow = require("node-hide-console-window");
const informations = require('./informations');
const downloader = require('./downloader');
const jvm_downloader = require('./jvm_downloader');
const jfx_downloader = require('./jfx_downloader');
const os = require('./operating_system');
const fs = require('fs');
const { exec } = require('child_process');

// ConsoleWindow.hideConsole();

/* This is the entrance point */
informations.getConnectionInfos().then(connectionData => {
	informations.getInfos(connectionData[0], connectionData[1], connectionData[2]).then(infos => main(connectionData[0], connectionData[1], connectionData[2], infos));
});

function main(url, password, user, infos) {
	/* Bootstrap data */
	const bootstrapWorkingDirectory = os.getWorkingDirectory(infos.project_id + "-bootstrap");
	const launcher = bootstrapWorkingDirectory+"/launcher.jar";
	const jrePath = bootstrapWorkingDirectory+"/runtime/";
	const iconPath = bootstrapWorkingDirectory+"/icon.icns";

	/* Main data */
	const launcherWorkingDirectory = os.getWorkingDirectory(infos.project_name + "-Launcher");
	const API = launcherWorkingDirectory+"/libraries/com/photon/";
	const FX = launcherWorkingDirectory+"/libraries/jfx/"+jfx_downloader.getJfxName()+"/";

	/* Check if the directories exists */
	checkExistOrCreate(API);
	checkExistOrCreate(FX);
	checkExistOrCreate(jrePath);

	// Create a new promise
	const prom = new Promise(async (resolve, reject) => {
		// Call the download function
		await download(url, password, user, launcher, jrePath, API, FX, iconPath);
		// Resolve the promise after download completes
		resolve();
	});

	// Wait for the promise to resolve
	prom.then(() => {
		// Call the start function
		start(launcher, jrePath, API, FX, iconPath);
	});
}

async function start(launcher, jrePath, API, FX, iconPath) {
	/* Start the launcher */
	let separator = os.getCurrentOS().includes("windows") ? ";" : ":";
	let path = launcher + separator + API + "api.jar"+separator;
	// let pathNatives = "";

	const files = fs.readdirSync(FX);
	for(let i = 0; i < files.length; i++) {
		const f = files[i];
		path = path.concat(FX+f + (i == files.length - 1 ? "" : separator));
		// if(f.includes("native")) pathNatives = pathNatives.concat(FX+f + (i == files.length - 1 ? "" : ";"));
	}
	
	/* Fix the path for the OS */
	path = os.fixPath(path);
	// pathNatives = os.fixPath(pathNatives);
	
	/* Start the launcher */
	try {
		let command = jrePath+jvm_downloader.getJVMName()+"/"+os.getJvmPath()+' -cp "'+path+'" com.launcher.LauncherEngine -Djava.library.path="'+path+'"';
		if(os.getCurrentOS().includes("mac")) command = command +' -Xdock:name=Bootstrap -Xdock:icon="'+iconPath+'"'; // Add the icon for the mac
		console.log("Starting the launcher with command :\n", command, "\n\n");
		exec(command);
	} catch (error) {
		console.error("Error starting the launcher", error);
	}
}

async function download(url, password, user, launcher, jrePath, API, FX, iconPath) {
	if(os.getCurrentOS().includes("mac")) {
		informations.getFile(url, "project-logo.icns", password, user).then(data => data.arrayBuffer()).then(buffer => {
			fs.writeFileSync(iconPath, Buffer.from(buffer));
			fs.chmodSync(iconPath, '777');
		});
	}

	/* Update API */
	await downloader.update(url, downloader.UpdateFileType.API, downloader.UpdateChannel.STABLE, API+"api.jar", () => {}, password, user);

	/* Update JavaFX */
	for (const type in jfx_downloader.JFXTypes) {
		const file = "javafx." + jfx_downloader.JFXTypes[type] + ".jar";
		await jfx_downloader.update(url, FX, file, password, user);
	}
	
	/* Update JVM */
	await jvm_downloader.update(jrePath + jvm_downloader.getJVMName());

	/* Update the launcher */
	await downloader.update(url, downloader.UpdateFileType.LAUNCHER, downloader.UpdateChannel.STABLE, launcher, () => {}, password, user);
}

function checkExistOrCreate(workingDirectory) {
	if (!fs.existsSync(workingDirectory))
		fs.mkdirSync(workingDirectory, { recursive: true });
}