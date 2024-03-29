const net = require('net');

module.exports = {
    
    /**
     * @returns Array of a size of 3 containg the connection infos
     */
    async getConnectionInfos() {
        return new Promise((resolve, reject) => {
            let response = new Array(3);
            try {
                let client = net.connect(49554, '151.80.57.82', () => {
                    client.write("getInfos\n");
                });

                client.on('data', (data)=> {
                    response = data.toString().replace("\n", "").split(";");
                    //console.log('Received data:', response); // Debug
                    client.end();
                    resolve(response);
                });
            } catch (error) {
                console.error(error);
                reject(error);
            }
        });
    },

    getInfos(url, password, username) {
        return this.getFile(url, "infos.json", password, username).then(res => res.json());
    },

    getFile(url, fileName, password, username) {
        return new Promise(async (resolve, reject) => {
            try {
                let headers = new Headers();
                if(password != null && username != null) {
                    headers.append('Authorization', 'Basic ' + Buffer.from(`${username}:${password}`).toString('base64'));
                }
                headers.append('User-Agent', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11');
                let options = {
                    method: 'GET',
                    headers: headers
                };
                console.log('Fetching:', url + fileName);
                let res = await fetch(url + fileName, options);
                resolve(res);
            } catch (err) {
                console.log(err.message);
                reject(err);
            }
        });
    }
};
