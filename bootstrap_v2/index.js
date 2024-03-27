// const ConsoleWindow = require("node-hide-console-window");
const informations = require('./informations');
const downloader = require('./downloader');
const jvm_downloader = require('./jvm_downloader');
const jfx_downloader = require('./jfx_downloader');
const path = require('path');
const fs = require('fs');
const { exec } = require('child_process');

// ConsoleWindow.hideConsole();

informations.getConnectionInfos().then(connectionData => {
	informations.getInfos(connectionData[0], connectionData[1], connectionData[2]).then(infos => main(connectionData[0], connectionData[1], connectionData[2], infos));
});

function main(url, password, user, infos) {
	/* Bootstrap data */
	const bootstrapWorkingDirectory = getWorkingDirectory(infos.project_id + "-bootstrap");
	const launcher = bootstrapWorkingDirectory+"/launcher.jar";
	const jrePath = bootstrapWorkingDirectory+"/runtime/";

	/* Main data */
	const launcherWorkingDirectory = getWorkingDirectory(infos.project_name + "-Launcher");
	const API = launcherWorkingDirectory+"/libraries/com/photon/";
	const FX = launcherWorkingDirectory+"/libraries/jfx/";

	/* Check if the directories exists */
	checkExistOrCreate(API);
	checkExistOrCreate(FX);
	checkExistOrCreate(jrePath);

	download(url, password, user, launcher, jrePath, API, FX)
	start(launcher, jrePath, API, FX);
}

function start(launcher, jrePath, API, FX) {
	/* Start the launcher */
	let path = launcher + ";" + API + "api.jar;";
	let pathNatives = "";

	const files = fs.readdirSync(FX);
	for(let i = 0; i < files.length; i++) {
		const f = files[i];
		path = path.concat(FX+f + (i == files.length - 1 ? "" : ";"));
		if(f.includes("native")) pathNatives = pathNatives.concat(FX+f + (i == files.length - 1 ? "" : ";"));
	}
	
	/* Start the launcher */
	try {
		exec('java -cp "'+path+'" com.launcher.LauncherEngine -Djava.library.path='+pathNatives);
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
	if(!checkJavaInstallation())
		jvm_downloader.update(url, jrePath, jrePath+"jvm.zip", password, user);

	/* Update the launcher */
	downloader.update(url, downloader.UpdateFileType.LAUNCHER, downloader.UpdateChannel.STABLE, launcher, () => {}, password, user);

}

function checkExistOrCreate(workingDirectory) {
	if (!fs.existsSync(workingDirectory))
		fs.mkdirSync(workingDirectory, { recursive: true });
}

function getWorkingDirectory(workDir) {
	const userHome = process.env.HOME || process.env.USERPROFILE || '.';
	let workingDirectory = '';
	switch (process.platform) {
		case 'linux':
		case 'freebsd':
		case 'openbsd':
		case 'sunos':
			workingDirectory = path.join(userHome, '.' + workDir);
			break;
		case 'win32':
			workingDirectory = path.join(userHome, 'AppData', 'Roaming', '.' + workDir);
			break;
		case 'darwin':
			workingDirectory = path.join(userHome, 'Library', 'Application Support', workDir);
			break;
		default:
			workingDirectory = path.join(userHome, '.' + workDir);
	}
	if (!fs.existsSync(workingDirectory)) {
		fs.mkdirSync(workingDirectory, { recursive: true });
	}
	return workingDirectory;
}

/**
 * @return true if the java version is greater than 17
 */
function checkJavaInstallation() {
	try {
		const output = exec('java -version').toString();
		const versionString = output.split('\n')[0].split(' ')[2].replace(/"/g, '');
		const version = parseFloat(versionString);
		console.log("Java version:", versionString, version);
		return version >= 17;
	} catch (error) {
		return false;
	}
}