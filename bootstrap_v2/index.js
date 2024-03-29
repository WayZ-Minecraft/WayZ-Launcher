// const ConsoleWindow = require("node-hide-console-window");
const informations = require('./informations');
const downloader = require('./downloader');
const jvm_downloader = require('./jvm_downloader');
const jfx_downloader = require('./jfx_downloader');
const os = require('./operating_system');
const fs = require('fs');
const { exec } = require('child_process');

// ConsoleWindow.hideConsole();

informations.getConnectionInfos().then(connectionData => {
	informations.getInfos(connectionData[0], connectionData[1], connectionData[2]).then(infos => main(connectionData[0], connectionData[1], connectionData[2], infos));
});

function main(url, password, user, infos) {
	/* Bootstrap data */
	const bootstrapWorkingDirectory = os.getWorkingDirectory(infos.project_id + "-bootstrap");
	const launcher = bootstrapWorkingDirectory+"/launcher.jar";
	const jrePath = bootstrapWorkingDirectory+"/runtime/";

	/* Main data */
	const launcherWorkingDirectory = os.getWorkingDirectory(infos.project_name + "-Launcher");
	const API = launcherWorkingDirectory+"/libraries/com/photon/";
	const FX = launcherWorkingDirectory+"/libraries/jfx/";

	/* Check if the directories exists */
	checkExistOrCreate(API);
	checkExistOrCreate(FX);
	checkExistOrCreate(jrePath);

	// Create a new promise
	const prom = new Promise((resolve, reject) => {
		// Call the download function
		download(url, password, user, launcher, jrePath, API, FX);
		// Resolve the promise after download completes
		resolve();
	});

	// Wait for the promise to resolve
	prom.then(() => {
		// Call the start function
		start(launcher, jrePath, API, FX);
	});
}

async function start(launcher, jrePath, API, FX) {
	/* Start the launcher */
	let path = launcher + ";" + API + "api.jar;";
	let pathNatives = "";

	const files = fs.readdirSync(FX);
	for(let i = 0; i < files.length; i++) {
		const f = files[i];
		path = path.concat(FX+f + (i == files.length - 1 ? "" : ";"));
		if(f.includes("native")) pathNatives = pathNatives.concat(FX+f + (i == files.length - 1 ? "" : ";"));
	}
	
	/* Fix the path for the OS */
	path = os.fixPath(path);
	pathNatives = os.fixPath(pathNatives);
	
	/* Start the launcher */
	try {
		let command = jrePath+'bin/java -cp '+path+' com.launcher.LauncherEngine -Djava.library.path='+pathNatives;
		console.log("Starting the launcher with command :\n", command, "\n\n");
		exec(command);
	} catch (error) {
		console.error("Error starting the launcher", error);
	}
}

function download(url, password, user, launcher, jrePath, API, FX) {
	/* Update API */
	downloader.update(url, downloader.UpdateFileType.API, downloader.UpdateChannel.STABLE, API+"api.jar", () => {}, password, user);

	/* Update JavaFX */
	for (const type in jfx_downloader.JFXTypes) {
		const file = "javafx." + jfx_downloader.JFXTypes[type] + ".jar";
		jfx_downloader.update(url, FX, file, password, user);
	}
	
	/* Update JVM */
	// jvm_downloader.update(url, jrePath, jrePath+getJVMName(), password, user);
	jvm_downloader.update(jrePath + jvm_downloader.getJVMName());

	/* Update the launcher */
	downloader.update(url, downloader.UpdateFileType.LAUNCHER, downloader.UpdateChannel.STABLE, launcher, () => {}, password, user);
}

function checkExistOrCreate(workingDirectory) {
	if (!fs.existsSync(workingDirectory))
		fs.mkdirSync(workingDirectory, { recursive: true });
}