const informations = require('./informations');
const fs = require('fs');
const decompress = require("decompress");

module.exports = {

    update(url, path, file, password, user) {
        if (fs.existsSync(file)) {
            console.log("JVM already avalible:", file);
            return;
        }
        
        informations.getFile(url, "services_updates/aJVM-17.zip", password, user)
            .then(data => data.arrayBuffer())
            .then(data => { 
                console.log("Downloading", file);
                fs.writeFileSync(file, Buffer.from(data));
                console.log("-> Downloaded", file);
                decompress(file, path);
            });
    }
}