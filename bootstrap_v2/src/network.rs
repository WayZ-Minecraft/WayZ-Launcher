use core::str;
use std::time::Duration;
use std::io::Read;
use std::net::{TcpStream, ToSocketAddrs};
use std::io::{Result, Error};
use std::collections::HashSet;

const TIME_OUT: u64 = 5000; // milliseconds

pub struct Network;

impl Network {
    pub fn set_connect_timeout(stream: &mut TcpStream) -> Result<()> {
        stream.set_read_timeout(Some(Duration::from_millis(TIME_OUT)))?;
        stream.set_write_timeout(Some(Duration::from_millis(TIME_OUT)))?;
        Ok(())
    }
    
    pub fn set_user_agent(stream: &mut TcpStream) -> Result<()> {
        let user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
        stream.write(format!("User-Agent: {}\r\n", user_agent).as_bytes())?;
        Ok(())
    }
    
    pub fn check_exceptions(url: &str, exceptions: &[String]) -> bool {
        exceptions.iter().any(|ex| url.contains(ex))
    }
    
    pub fn authenticate(stream: &mut TcpStream, web_user: &str, web_password: &str) -> Result<()> {
        let auth_string = format!("{}:{}", web_user, web_password);
        let encoded_auth = base64::encode(auth_string);
        stream.write(format!("Authorization: Basic {}\r\n", encoded_auth).as_bytes())?;
        Ok(())
    }
}