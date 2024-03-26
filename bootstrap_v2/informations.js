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
                    console.log("connected.");
                });

                client.on('data', (data)=> {
                    response = data.toString().replace("\n", "").split(";");
                    console.log('Received data:', response);
                    client.end();
                    resolve(response);
                });

                client.on('end', ()=> {
                    console.log("Disconnected.");
                });
            } catch (error) {
                console.error(error);
                reject(error);
            }
        });
    },

    getInfos(url, password, username) {
        (async () => {
            try {
                let headers = new Headers();
                headers.append('Authorization', 'Basic ' + Buffer.from(`${password}:${username}`).toString('base64'));
                let options = {
                    method: 'GET',
                    headers: headers
                };
                let res = await fetch(url, options);
                console.log('Status Code:', res.status);
          
                // let users = await res.json();
                
            } catch (err) {
              console.log(err.message); //can be console.error
            }
          })();
    }

};
