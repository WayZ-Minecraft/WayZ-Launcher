use std::net::TcpStream;
use serde::Deserialize;
use std::io::{Read, Write};

use crate::network::Network;

pub struct PhotonInfosManager;

impl PhotonInfosManager {
    pub fn get_infos(fetch_url: &str, password: &str, user: &str, exceptions: &[String]) -> ObjectInfos {
        // Get the infos from the server
        let response = Self::get_file("files/services_update/infos.json", fetch_url, password, user, exceptions);
        println!("{:?}", String::from_utf8_lossy(&response));
        let object_infos: ObjectInfos = match serde_json::from_slice(String::from_utf8_lossy(&response).as_bytes()) {
            Ok(data) => data,
            Err(e) => {
                println!("Failed to parse the response : {}", e);
                std::process::exit(1); // Quit the app with exit code 1
            }
        };
        object_infos
    }

    pub fn get_file(file: &str, fetch_url: &str, password: &str, user: &str, exceptions: &[String]) -> Vec<u8> {
        let web_url = format!("{}{}", fetch_url, file);
        // let mut stream = match TcpStream::connect(web_url.clone()+":80") {
        let mut stream = match TcpStream::connect("151.80.57.82:80") {
            Ok(stream) => stream,
            Err(_e) => {
                println!("Failed to connect to the server : {}", _e);
                return Vec::new(); // Return empty buffer if error occurs
            }
        };
        
        match Network::set_connect_timeout(&mut stream) {
            Ok(_) => (),
            Err(_e) => {
                println!("Failed to set the connection timeout : {}", _e);
                return Vec::new(); // Return empty buffer if error occurs
            }
        }
        match Network::set_user_agent(&mut stream) {
            Ok(_) => (),
            Err(_e) => {
                println!("Failed to set the user agent : {}", _e);
                return Vec::new(); // Return empty buffer if error occurs
            }
        }

        if Network::check_exceptions(&web_url, &exceptions) {
            println!("Exception occurred");
            return Vec::new(); // Return empty buffer if error occurs
        }

        match Network::authenticate(&mut stream, &user, &password){
            Ok(_) => (),
            Err(_e) => {
                println!("Failed to authenticate : {}", _e);
                return Vec::new(); // Return empty buffer if error occurs
            }
        }

        let mut buffer = Vec::new();
        if let Err(_e) = stream.read_to_end(&mut buffer) {
            println!("Failed to read the response : {}", _e);
            return Vec::new(); // Return empty buffer if error occurs
        }

        return buffer;
    }

    pub fn fetch_infos() -> Result<Vec<Vec<String>>, String> {        
        // Connect to the server
        let mut stream = match std::net::TcpStream::connect("151.80.57.82:49554") {
            Ok(stream) => stream,
            Err(e) => {
                return Err(format!("Failed to connect: {}", e));
            }
        };
    
        // Write request
        if let Err(e) = stream.write_all(b"getInfos\n") {
            return Err(format!("Failed to send data: {}", e));
        }
    
        // Read response
        let mut buffer = String::new();
        if let Err(e) = stream.read_to_string(&mut buffer) {
            return Err(format!("Failed to receive data: {}", e));
        }
    
        // Close the connection
        drop(stream);
        
        let split: Vec<Vec<String>> = buffer.trim().split(';').map(|s| s.split(',').map(|s| s.to_string()).collect()).collect();
        Ok(split)
    }
}

#[derive(Debug, Deserialize)]
pub struct ObjectInfos {
    pub project_name: String,
    pub project_id: String
}