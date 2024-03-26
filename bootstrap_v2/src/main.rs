use std::fs;
use std::path::{Path, PathBuf};
use platform_dirs::AppDirs;
use tokio;

mod informations;
use informations::PhotonInfosManager;

mod java_update;

mod network;
use network::Network;

#[tokio::main]
async fn main() {
    let mut response = PhotonInfosManager::fetch_infos();
    let exceptions = vec![];
    let infos = PhotonInfosManager::get_infos(response[0], response[1], response[2], &exceptions);
    match infos.await {
        Ok(object_infos) => {
            let project_name_ref: &str = &(object_infos.project_name + "-Launcher");
            
            // Create directories
            let working_directory = FileLocation::get_working_directory(&(object_infos.project_id + "-bootstrap"));
            let launcher = working_directory.join("launcher.jar");
            let jre_path = working_directory.join("runtime/");
            let api = FileLocation::get_working_directory(&(project_name_ref)).join("/libraries/com/photon/api.jar");
            let fx = FileLocation::get_working_directory(&(project_name_ref)).join("/libraries/jfx/");
            let path = format!("{};{}", launcher.to_str().unwrap(), api.to_str().unwrap());
            let path_natives = String::new(); // Placeholder for pathNatives in Java code
            
            // Check if the directories exist
            check_exist_or_create(&api);
            check_exist_or_create(&fx);
            check_exist_or_create(&jre_path);

            // Check Java version
            let mut need_java = false;
            let java_version_output = std::process::Command::new("java")
                .arg("-version")
                .output()
                .expect("Failed to execute command");

            if java_version_output.status.success() {
                let java_version = String::from_utf8_lossy(&java_version_output.stdout);
                if check_java_version(&java_version.to_string()) {
                    println!("Java version is >= 17");
                } else {
                    need_java = true;
                }
            } else {
                need_java = true;
                let error_message = String::from_utf8_lossy(&java_version_output.stderr);
                eprintln!("Failed to check Java version: {}. Downloading a new one", error_message);
            }

            // Download Java
            if need_java {
                let jvm_zip_file = jre_path.join("jvm.zip");
            }

            // Download launcher

            // Download API
        }
        Err(err) => {
            eprintln!("Error fetching object infos: {}", err);
        }
    }
}

fn check_java_version(version: &String) -> bool {
    let version_number: u32 = version
        .split('.')
        .next()
        .unwrap_or("0")
        .parse()
        .unwrap_or(0);
    return version_number >= 17
}

fn check_exist_or_create(path: &Path) {
    if !path.exists() {
        println!("Creating dir : {:?}", path);
        fs::create_dir_all(path).unwrap();
    }
}

struct FileLocation;

impl FileLocation {
    fn get_working_directory(dir_name: &str) -> PathBuf {
        let prefixed_dir = ".".to_string() + dir_name;
        let app_dirs = AppDirs::new(Some(&prefixed_dir), false).unwrap();
        return PathBuf::from(app_dirs.config_dir)
    }
}