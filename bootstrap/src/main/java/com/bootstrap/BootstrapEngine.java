package com.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.photon.informations.PhotonInfosManager;
import com.photon.informations.PhotonUpdaterManager;
import com.photon.informations.PhotonUpdaterManager.UpdateFileType;
import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.os.FileLocation;

public class BootstrapEngine {
    
    public static void main(String[] args) {
        String[] response = new String[3];
        try {
            final Socket clientSocket = new Socket(new String(new byte[] { 49,53,49,46,56,48,46,53,55,46,56,50 }), 49554);
            final PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer.println("getInfos");
            response = reader.readLine().split(";");
            clientSocket.close();
        } catch (IOException e) { e.printStackTrace(); }
        
        NetworkDirectories.config.webUrl = PhotonUpdaterManager.url = response[0];
        NetworkDirectories.config.webPassword = response[1];
        NetworkDirectories.config.webUser = response[2];

        /* Display a splash screen */
        final SplashScreen splash = new SplashScreen("Bootstrap", PhotonInfosManager.getGameLogo(), 250, 250);
        splash.setVisible(true);
        final SplashPanel panel = (SplashPanel)splash.getContentPane();
        panel.progressBar.setVisible(false);

        final File launcher = new File("launcher.jar");
        final File API = new File(FileLocation.getWorkingDirectory(PhotonInfosManager.getInfos().project_name+"-Launcher"), "/libraries/com/photon/api.jar");
        final File FX = new File(FileLocation.getWorkingDirectory(PhotonInfosManager.getInfos().project_name+"-Launcher"), "/libraries/jfx/");
        final File FXNATIVES = new File(FileLocation.getWorkingDirectory(PhotonInfosManager.getInfos().project_name+"-Launcher"), "/libraries/jfx/");
        final File jrePath = new File("runtime/bootstrap/").getAbsoluteFile();
        String path = launcher.getAbsolutePath()+";"+API.getAbsolutePath()+";";
        String pathNatives = "";
        
        /* Init paths */
        if(!API.getParentFile().exists()) API.getParentFile().mkdirs();
        if(!FX.exists()) FX.mkdirs();
        if(!FXNATIVES.exists()) FXNATIVES.mkdirs();
        if(!jrePath.exists()) jrePath.mkdirs();
        
        /* Update API */
        PhotonUpdaterManager.update(UpdateFileType.API, API, (val, max)-> {
            panel.progressBar.setVisible(true);
            panel.progressBar.setValue(val);
            panel.progressBar.setMaximum(max);
        });

        /* Update JFX */
        for(JFXFileType type : JFXFileType.values()) JFXUpdateManager.update(type, new File(FX, "javafx."+type.name().toLowerCase()+".jar"));

        /* Update JVM */
        JVMUpdateManager.update(new File(jrePath, "jvm.zip"));
        
        /* Update the launcher */
        if(PhotonUpdaterManager.update(UpdateFileType.LAUNCHER, launcher, (val, max)-> {
            panel.progressBar.setVisible(true);
            panel.progressBar.setValue(val);
            panel.progressBar.setMaximum(max);
        })) {
            final File[] files = FX.listFiles();
            for(int i = 0; i < files.length; i++) {
                final File f = files[i];
                path = path.concat(f.getAbsolutePath()+ (i==files.length-1 ? "" : ";"));
                if(f.getName().contains("native")) pathNatives = pathNatives.concat(f.getAbsolutePath()+ (i==files.length-1 ? "" : ";"));
            }
            try {
                final ProcessBuilder builder = new ProcessBuilder(jrePath.getAbsolutePath()+"\\bin\\java", "-cp", path, "com.launcher.LauncherEngine", "-Djava.library.path="+pathNatives);
                ConsoleManager.create(String.join(" ",  builder.command())).end();
                builder.start();
                System.exit(0);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
