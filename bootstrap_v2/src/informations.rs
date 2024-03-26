use std::{convert::TryInto, fmt::{format, Result}, net::TcpStream};
use core::time::Duration;
use std::error::Error;
use serde::Deserialize;
use base64;
use std::io::{Write, Read};

use crate::network::Network;

pub struct PhotonInfosManager;

impl PhotonInfosManager {
    pub async fn get_infos(fetch_url: &str, password: &str, user: &str, exceptions: &[String]) {
        // // Construct the URL
        // let response = Self::get_file("infos.json", fetch_url, password, user, exceptions).await?;
        
        // // Handle non-exception case
        // if response.status().is_success() {
        //     let response_text = response.text().await?;
        //     if response_text.trim().is_empty() {
        //         return Err("Empty response from server".into());
        //     }
        //     return Ok(serde_json::from_str(&response_text)?);
        // } else {
        //     // Handle non-success status code
        //     return Err(format!("Server returned non-success status code: {}", response.status()).into());
        // }
    }

    pub fn get_file(file: &str, fetch_url: &str, password: &str, user: &str, exceptions: &[String]) -> Result<()> {
        let web_url = format!("{}{}", fetch_url, file);
        let mut stream = TcpStream::connect(web_url)?;
    
        Network::set_connect_timeout(&mut stream)?;
        Network::set_user_agent(&mut stream)?;

        if Network::check_exceptions(&web_url, &exceptions) {
            return Ok(());
        }

        Network::authenticate(&mut stream, &user, &password)?;

        // // Construct the URL
        // let web_url = format!("{}{}", fetch_url, file);
        // let url = Url::parse(&web_url).await?;
    
        // // Create a new Reqwest client
        // let client = Client::builder()
        //     .timeout(Duration::from_secs(TIME_OUT))
        //     .user_agent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
        //     .build()?;
    
        // // Set basic authentication
        // let auth: String = format!("{}:{}", user, password);
        // let auth_base64 = base64::encode(&auth);
        // let auth_final = "Basic ".to_string() + (&auth_base64);
        // let mut headers = reqwest::header::HeaderMap::new();
        // headers.insert(
        //     reqwest::header::AUTHORIZATION,
        //     reqwest::header::HeaderValue::from_str(&auth_final).unwrap(),
        // );
        
        // // Perform the HTTP request
        // return client.get(url.clone()).headers(headers.clone()).send();
    }

    pub fn fetch_infos() -> [&str; 3] {
        let mut response = ["", "", ""];
        
        // Connect to the server
        let mut stream = match std::net::TcpStream::connect("151.80.57.82:49554") {
            Ok(stream) => stream,
            Err(e) => {
                eprintln!("Failed to connect: {}", e);
                return response;
            }
        };
    
        // Write request
        if let Err(e) = stream.write_all(b"getInfos\n") {
            eprintln!("Failed to send data: {}", e);
            return response;
        }
    
        // Read response
        let mut buffer = String::new();
        if let Err(e) = stream.read_to_string(&mut buffer) {
            eprintln!("Failed to receive data: {}", e);
            return response;
        }
    
        // Close the connection
        drop(stream);
        
        response = buffer.trim().split(';').collect::<Vec<&str>>().try_into().unwrap(); // Split response by ';';
        return response;
    }
}

#[derive(Debug, Deserialize)]
pub struct ObjectInfos {
    pub project_name: String,
    pub project_id: String
}