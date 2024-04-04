const informations = require('./informations');
const downloader = require('./downloader');
const os = require('./operating_system');
const fs = require('fs');

// Enum for types
const JFXTypes = {
    GSON: 'gson',
    LOG4J: 'log4j',
    BASE: 'base',
    CONTROLS: 'controls',
    GRAPHICS: 'graphics',
    SWING: 'swing',
    NATIVES_WINDOWS: 'natives_windows'
};

module.exports = {

    async update(url, path, name, password, user) {
        if (!fs.existsSync(path)) fs.mkdirSync(path, { recursive: true });
        const update = await hasUpdate(url, name, path + name, password, user);
        if (!update) return;
        const data = await informations.getFile(url, "services_updates/jfx/"+this.getJfxName()+"/"+name, password, user);
        const buffer = await data.arrayBuffer();
        const file = path + name;

        console.log("Downloading", file);
        fs.writeFileSync(file, Buffer.from(buffer));
        const currentPermissions = fs.statSync(file).mode;
        const desiredPermissions = parseInt('777', 8);
        if (currentPermissions !== desiredPermissions) {
            fs.chmodSync(file, desiredPermissions);
        }
        console.log("-> Downloaded", file);
    },

    getJfxName() {
        switch (process.platform) {
            case 'linux':
            case 'freebsd':
            case 'openbsd':
            case 'sunos':
                return "linux";
            case 'win32':
                return "windows";
            case 'darwin':
                return "mac";
            default:
                return "linux";
        }
    },

    JFXTypes: JFXTypes
}

function hasUpdate(url, name, file, password, user) {
    if (!fs.existsSync(file)) return true;
    return getSHA1(url, name, password, user).then(sha => {
        if(sha != "UNKNOWN" && downloader.getDigest(file) != sha) return true;
        return false;
    });
}

function getSHA1(url, name, password, user) {
    /* If we can't reach the site, disable download */
    return informations.getFile(url, "services_updates/jfx/the-sha.php?type="+name.replace(".jar", ""), password, user)
        .then(res => res.text())
        .then(data => {
            if (data === null || data.includes("-")) return "UNKNOWN";
            else return data;
        });
}