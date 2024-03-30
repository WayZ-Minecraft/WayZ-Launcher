const informations = require('./informations');
const os = require('./operating_system');
const downloader = require('./downloader');
const fs = require('fs');
const decompress = require("decompress");

class JavaVersion {
    constructor(name, version) {
        this.name = name;
        this.version = version;
    }
}

const JVM_17 = new JavaVersion("java-runtime-gamma", 17);

module.exports = {

    async update(file) {
        // if (fs.existsSync(file)) {
        //     console.log("JVM already avalible:", file);
        //     return;
        // }

        await downloadJavaManifest(file);
    },

    /**
     * @returns The name of the file for the JVM based on the current operating system
     */
    getJVMName() {
        switch (process.platform) {
            case 'linux':
            case 'freebsd':
            case 'openbsd':
            case 'sunos':
                return 'jvm_linux';
            case 'win32':
                return 'jvm_windows';
            case 'darwin':
                return 'jvm_macos';
            default:
                return 'jvm_linux';
        }
    }
}

/**
 * @returns Get the manifest for all JVMs
 */
function getManifest() {
    const manifestUrl = "https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/";
    return informations.getFile(manifestUrl, "all.json", null, null).then(res => res.json());
}

/**
 * Downloads the manifest for the JVM and selects the JVM to download
 * @param {*} file The file in which the JVM should be downloaded
 */
async function downloadJavaManifest(file) {
    await getManifest().then(manifest => {
        console.log("CurrentRuntime: " + os.getCurrentOS());
        const r = manifest[os.getCurrentOS()];
        for (const run in r) {
            if(run === JVM_17.name) {
                const s = r[run][0];
                console.log("Choosen:", run);
                indexJava(s.manifest.url, file);
                break;
            }
        }
    });
}

/**
 * Index JVM files/datas based on the manifest (For the selected JVM)
 * @param {*} url The url to the manifest for the selected JVM
 * @param {*} file The file in which the JVM should be downloaded
 */
async function indexJava(url, file) {
    informations.getFile(url, "", null, null)
        .then(data => data.json())
        .then(json => updateJava(json, file));
}

/**
 * Download JVM files based on the manifest
 * @param {*} manifest The content of the manifest for the selected JVM
 * @param {*} file The file in which the JVM should be downloaded
 */
async function updateJava(manifest, file) {
    const path = file+"/";
    if (!fs.existsSync(path)) fs.mkdirSync(path);
    
    for (const file of Object.entries(manifest.files)) { // TODO: Check if this is correct
        let fileName = file[0];
        let fileData = file[1];
        
        if(fileData.type === "directory") { // Create directory if it does not exist
            if (!fs.existsSync(path + fileName)) 
                fs.mkdirSync(path + fileName + "/", { recursive: true });
            continue;
        }

        let downloads = fileData["downloads"];
        if(downloads === undefined) continue;

        let rawData = downloads != null ? fileData.downloads["raw"] : null;
        if(rawData === undefined) continue;

        if(downloads !=null && rawData !=null && rawData.sha1 != downloader.getDigest(path+fileName)) {
            informations.getFile(rawData.url, "", null, null)
                .then(data => data.arrayBuffer())
                .then(data => {
                    fs.writeFileSync(path+fileName, Buffer.from(data));
                    fs.chmodSync(path+fileName, '777');
                });
        }
    }
}