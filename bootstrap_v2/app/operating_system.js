const path = require('path');
const fs = require('fs');

module.exports = {
    getCurrentOS() {
        switch (process.platform) {
            case 'linux':
            case 'freebsd':
            case 'openbsd':
            case 'sunos':
                if (process.arch === 'x64') return 'linux-i386';
                return "linux";
            case 'win32':
                if (process.arch !== 'x64') return 'windows-x86';
                return "windows-x64";
            case 'darwin':
                return "mac-os";
            default:
                return "linux";
        }
    },

    fixPath(path) {
        switch (process.platform) {
            case 'linux':
            case 'freebsd':
            case 'openbsd':
            case 'sunos':
                return path;
            case 'win32':
                return path.replace('/', '\\'); // Replace / with \
            case 'darwin':
                // return path.replace(' ', '\\ '); // Escape spaces
                return path;
            default:
                return path;
        }
    },

    getWorkingDirectory(workDir) {
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
                workingDirectory = path.join(userHome, 'Library', 'Application\\ Support', workDir);
                break;
            default:
                workingDirectory = path.join(userHome, '.' + workDir);
        }
        if (!fs.existsSync(workingDirectory)) fs.mkdirSync(workingDirectory, { recursive: true });
	    return workingDirectory;
    },

    getJvmPath() {
        switch (process.platform) {
            case 'linux':
            case 'freebsd':
            case 'openbsd':
            case 'sunos':
                return 'bin/java';
            case 'win32':
                return 'bin\\javaw.exe';
            case 'darwin':
                return 'jre.bundle/Contents/Home/bin/';
            default:
                return 'bin/java';
        }
    }
}