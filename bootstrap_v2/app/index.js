// const ConsoleWindow = require("node-hide-console-window");
const informations = require('./informations');
const downloader = require('./downloader');
const jvm_downloader = require('./jvm_downloader');
const jfx_downloader = require('./jfx_downloader');
const os = require('./operating_system');
const fs = require('fs');
const { exec, spawn } = require('child_process');

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
		start(launcher, jrePath, API, FX, iconPath, infos.project_name, url);
	});
}

async function start(launcher, jrePath, API, FX, iconPath, project_name, web_url) {
	/* Start the launcher */
	let separator = os.getCurrentOS().includes("windows") ? ";" : ":";
	let path = launcher + separator + API + "api.jar"+separator;
	
	const files = fs.readdirSync(FX);
	for(let i = 0; i < files.length; i++) {
		const f = files[i];
		path = path.concat(FX+f + (i == files.length - 1 ? "" : separator));
	}
	
	/* Fix the path for the OS */
	path = os.fixPath(path);
	
	/* Start the launcher */
	try {
		process.title = "Boostrap";

		/* Create the command to start the launcher */
		let command = jrePath+jvm_downloader.getJVMName()+"/"+os.getJvmPath()+' -cp "'+path+'" com.launcher.LauncherEngine';
		command = command+' -Djava.library.path="'+path+'"';
		command = command+' arg0='+project_name.toLowerCase()+' arg1='+project_name+' arg2='+web_url;

		if(os.getCurrentOS().includes("mac")) command = command+' -Xdock:name=Bootstrap -Xdock:icon="'+iconPath+'"'; // Add the icon for the mac

		console.log("Starting the launcher with command :\n", command, "\n\n");
		ls = spawn(command, { shell: true }); // Use shell option and pass command as a string
		ls.stdout.on('data', function (data) {
			console.log(data.toString());
		});
		
		ls.stderr.on('data', function (data) {
			console.log(data.toString());
		});
		
		ls.on('exit', function (code) {
			console.log('Child process exited with code ' + code.toString());
		});

	} catch (error) { //TODO : ~20s to start the launcher (3/4 for launcher itself)
		console.error("Error starting the launcher", error);
	}
}

async function download(url, password, user, launcher, jrePath, API, FX, iconPath) {
	if(os.getCurrentOS().includes("mac") && !fs.existsSync(iconPath)) {
		informations.getFile(url, "project-logo.icns", password, user).then(data => data.arrayBuffer()).then(buffer => {
			fs.writeFileSync(iconPath, Buffer.from(buffer));
			const currentPermissions = fs.statSync(iconPath).mode;
			const desiredPermissions = parseInt('777', 8);
			if (currentPermissions !== desiredPermissions) {
				fs.chmodSync(iconPath, desiredPermissions);
			}
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
	//await downloader.update(url, downloader.UpdateFileType.LAUNCHER, downloader.UpdateChannel.STABLE, launcher, () => {}, password, user);
}

function checkExistOrCreate(workingDirectory) {
	if (!fs.existsSync(workingDirectory))
		fs.mkdirSync(workingDirectory, { recursive: true });
}