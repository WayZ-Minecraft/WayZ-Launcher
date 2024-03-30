const fs = require('fs');
const crypto = require('crypto');
const informations = require('./informations');

// Enum for channels
const UpdateChannel = {
    STABLE: 'stable',
    DEV: 'dev'
};

// Enum for types
const UpdateFileType = {
    MOD: 'mod',
    LAUNCHER: 'launcher',
    API: 'api',
    NETWORK: 'network',
};

module.exports = {

    async update(url, type, channel, file, callback, password, user) {
        let hasFinished = false;
        const update = await hasUpdate(url, type, channel, file, password, user);
        if (!update) return;

        const res = await informations.getFile(url, this.getURL(type, channel), password, user);
        const data = await res.arrayBuffer();
        console.log("Downloading", file);
        fs.writeFileSync(file, Buffer.from(data));
        fs.chmodSync(file, '777');
        console.log("-> Downloaded", file);

        hasFinished = true;
        callback();
    },
    
    getURL(type, channel) {
        return "services_updates/" +
            type +
            (channel !== UpdateChannel.STABLE ? "-" + channel : "") +
            ".jar";
    },

    UpdateChannel,
    UpdateFileType,

    getDigest
};

function hasUpdate(url, type, channel, file, password, user) {
    if (!fs.existsSync(file)) return true;

    return getSHA1(url, type, channel, password, user).then(sha => {
        if(sha != "UNKNOWN" && getDigest(file) != sha) return true;
        return false;
    });
}

function getSHA1(url, type, channel, password, user) {
    /* If we can't reach the site, disable download */
    return informations.getFile(url, "services_updates/the-sha.php?type="+type+"&channel="+channel, password, user)
        .then(res => res.text())
        .then(data => {
            if (data === null || data.includes("-")) return "UNKNOWN";
            else return data;
        });
}

/**
 * Get the SHA1 hash of a file. The SHA1 Will be hashed with the first 40 characters
 * @returns The SHA1 hash of a file or "UNKNOWN" if the file does not exist
 */
function getDigest(file) {
    if(!fs.existsSync(file) || !fs.lstatSync(file).isFile()) return "UNKNOWN";
    const data = fs.readFileSync(file);
    const sha = crypto.createHash('sha1').update(data).digest('hex').substring(0, 40);
    return sha;
}