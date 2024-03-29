const informations = require('./informations');
const os = require('./operating_system');
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

    update(url, path, file, password, user) {
        if (fs.existsSync(file)) {
            console.log("JVM already avalible:", file);
            return;
        }
        
        informations.getFile(url, "services_updates/" + path, password, user)
            .then(data => data.arrayBuffer())
            .then(data => {
                console.log("Downloading", file);
                fs.writeFileSync(file, Buffer.from(data));
                console.log("-> Downloaded", file);
                decompress(file, path);
            });
    },

    update(file) {
        if (fs.existsSync(file)) {
            console.log("JVM already avalible:", file);
            return;
        }

        downloadJavaManifest(file);
    },

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

function getManifest() {
    const manifestUrl = "https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/";
    return informations.getFile(manifestUrl, "all.json", null, null).then(res => res.json());
}

async function downloadJavaManifest(file) {
    getManifest().then(manifest => {
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

async function indexJava(url, file) {
    informations.getFile(url, "", null, null)
        .then(data => data.json())
        .then(json => updateJava(json, file));

}

async function updateJava(manifest, file) {
    const path = file+"/";
    
    for (const file of Object.entries(manifest.files)) { // TODO: Check if this is correct
        const downloader = file.get(0).downloads;
        // console.log(file[1]);
        // downloadFile(manifest.url, path, file);
        // informations.getFile(url, file, null, null)
        //     .then(data => data.arrayBuffer())
        //     .then(data => {
        //         console.log("Downloading", file);
        //         fs.writeFileSync(file, Buffer.from(data));
        //         console.log("-> Downloaded", file);
        //         decompress(file, path);
        //     });
    }
}