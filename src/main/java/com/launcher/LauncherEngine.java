package com.launcher;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import com.launcher.SplashScreen.SplashPanel;
import com.launcher.ui.home.GlobalHome;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.utils.GameEngine;
import com.launcher.utils.GameFolder;
import com.launcher.utils.GameLinks;
import com.launcher.utils.LauncherConfig;
import com.photon.PhotonEngine;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.ConsoleManager;
import com.photon.util.TranslationManager;
import com.photon.util.os.ApplicationUtils;
import com.photon.util.os.FileLocation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LauncherEngine /* extends Application */
{	
	public static Color boxColor = new Color(12, 13, 14);
	public static Color buttonColor = new Color(24, 26, 28);
	public static Color hoveredButtonColor = new Color(164, 164, 164);
	public static Color tooltipTextColor = new Color(168, 168, 168, 200);
	
	public static String VERSION = "1.1.2";
	public static String JFX_VERSION = "11.0.2";
	public static GameFolder gameFolder;
	private static GameLinks gameLinks;
	public static GameEngine gameEngine;
	
    public static Font getFont(int size) { return FileLocation.loadFont("fonts/axia_bold.otf", "Myriad", size); }
    
    public static Font getFontBOLD(int size) { return FileLocation.loadFont("fonts/axia_bold.otf", "Myriad", size).deriveFont(Font.BOLD); }
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		/* Init logging */
    	final File logsFolder = new File("./logs/");
    	if(!logsFolder.exists()) logsFolder.mkdirs();
    	ConsoleManager.registerFileHandler(new File(logsFolder, "launcher.log"));
		
		for(String arg : args) {
			if(arg.contains("delfile-")) {
				final File toDelete = new File(arg.replace("delfile-", ""));
				if(toDelete.exists()) toDelete.delete();
			}
		}
		
		try { PhotonEngine.loadClient(new String(new byte[] { 49,53,49,46,56,48,46,53,55,46,56,50 })); }
		catch (IOException e) {
    		JOptionPane.showMessageDialog(null, "Unable to connect to our services", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
		if(PhotonInfosManager.getInfos() == null) {
			JOptionPane.showMessageDialog(null, "Unable to get services informations", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		/* Init launcher folders and infos */
    	gameLinks = new GameLinks(NetworkDirectories.config.webUrl+"launcher/", "fabric-loader-0.14.21-1.16.5.json");
		gameFolder = new GameFolder(PhotonInfosManager.getInfos().project_id+"-launcher");
    	gameEngine = new GameEngine(gameFolder, gameLinks, PhotonInfosManager.getInfos().project_name);

		/* Check for updates */
    	final File currentExecutionFile = new File(LauncherEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	final File currentExecutionFolder = currentExecutionFile.getParentFile();
		
    	if(PhotonInfosManager.hasLauncherUpdate(LauncherEngine.VERSION)) {
    		ConsoleManager.print("Update avalible! Updating: "+currentExecutionFile+" in : "+currentExecutionFolder);
    		final SplashScreen splash = new SplashScreen(PhotonInfosManager.getInfos().project_name, PhotonInfosManager.getGameLogo(), 250, 250);
    		final SplashPanel panel = (SplashPanel)splash.getContentPane();
    		PhotonInfosManager.updateLauncherFromDir(currentExecutionFolder, "jar");
    		while(!PhotonInfosManager.updateFinished) {
    			if(PhotonInfosManager.updateSize > 0 && PhotonInfosManager.updateSizeDownloaded >= PhotonInfosManager.updateSize) ApplicationUtils.launch(new File(currentExecutionFolder, "/launcher-"+PhotonInfosManager.getLatestLauncherUpdate()+".jar"), new String[] {}, true);
    			panel.progressBar.setValue((int)PhotonInfosManager.updateSizeDownloaded);
    			panel.progressBar.setMaximum((int)PhotonInfosManager.updateSize);
    		}
    		return;
    	}
		
		/* Load config and translations system */
    	LauncherConfig.load(gameEngine);
    	TranslationManager.load((String)LauncherConfig.getConfig().language, "lang");

		/* Display the interface */
		Application.launch(MainStage.class, args);
	}

	/* This allow us to extract JFX */
	public static void unzip(String zipFilePath, String destDir) throws IOException {
       	final byte[] buffer = new byte[1024];
        final File folder = new File(destDir);
        if (!folder.exists()) folder.mkdir();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
			ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                final File newFile = new File(destDir + File.separator + zipEntry.getName());
				newFile.getParentFile().mkdirs();
				if(zipEntry.isDirectory()) newFile.mkdirs();
				else {
					newFile.createNewFile();
					final FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
					fos.close();
				}
				zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }
	
	public static class MainStage extends Application {
		public MainStage() { /* This constructor exist only because JFX needs it */ }
		
		public static Pane globalPane = new Pane();

        public final static Pane homePane = GlobalHome.getHomeMenu();
        public final static Pane settingsPane = GlobalSettings.getSettingsMenu();


        public static final int launcherHeight = 617;
        public static final int launcherWidht = 990;

        private static double[] Offset = new double[]{0, 0};

		@Override
		public void start(Stage stage) throws Exception {
			final Rectangle globalShape = new Rectangle(0, 0, launcherWidht, launcherHeight);
            globalShape.setArcHeight(20);
            globalShape.setArcWidth(20);
     
            setScene("LAUNCHER");
            globalPane.setClip(globalShape);

            // Create launcher scene
            Scene scene = new Scene(globalPane, launcherWidht, launcherHeight);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add("launcher.css");

            // Make launcher draggable
            scene.setOnMousePressed(event -> {
                Offset[0] = stage.getX() - event.getScreenX();
                Offset[1] = stage.getY() - event.getScreenY();
            });

            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() + Offset[0]);
                stage.setY(event.getScreenY() + Offset[1]);
            });

            // Set stage properties
            stage.getIcons().add(getIcon());
            stage.initStyle( StageStyle.TRANSPARENT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
		}

		public static Image getIcon() { return new Image("logos/WayZ.png"); }

        private static void setScene(Pane pane) {
            globalPane.getChildren().clear();
            globalPane.getChildren().add(pane);
        }

        public static void setScene(String scene) {
            switch (scene) {
                case "SETTINGS":
                    setScene(settingsPane);
                    break;
                case "LAUNCHER":
                    setScene(homePane);
                    break;
                default: break;
            }
        }
	}
}
