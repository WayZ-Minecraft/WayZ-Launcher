package com.launcher;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.TranslationManager;
import com.photon.util.os.ApplicationUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LauncherEngine {
	public static Color boxColor = new Color(12, 13, 14);
	public static Color buttonColor = new Color(24, 26, 28);
	public static Color hoveredButtonColor = new Color(164, 164, 164);
	public static Color tooltipTextColor = new Color(168, 168, 168, 200);
	
	public static String VERSION = "1.0.1";
	public static GameFolder gameFolder;
	private static GameLinks gameLinks;
	public static GameEngine gameEngine;
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		try { PhotonEngine.loadClient(new String(new byte[] { 49,53,49,46,56,48,46,53,55,46,56,50 })); }
		catch (IOException e) {
    		JOptionPane.showMessageDialog(null, "Unable to connect to our services. We'll be back in a moment", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
		if(PhotonInfosManager.getInfos() == null) {
			JOptionPane.showMessageDialog(null, "Unable to get services informations. Maybe check your connection", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		/* Init launcher folders and infos */
    	gameLinks = new GameLinks(NetworkDirectories.config.webUrl+"launcher/", "fabric-loader-0.14.21-1.16.5.json");
		gameFolder = new GameFolder(PhotonInfosManager.getInfos().project_id+"-launcher");
    	gameEngine = new GameEngine(gameFolder, gameLinks, PhotonInfosManager.getInfos().project_name);
		
		/* Init logging */
    	final File logsFolder = new File(gameFolder.gameDir, "/logs/");
    	if(!logsFolder.exists()) logsFolder.mkdirs();
    	ConsoleManager.registerFileHandler(new File(logsFolder, "launcher.log"));
		
		/* Check for updates */
    	final File currentExecutionFile = new File(LauncherEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	final File currentExecutionFolder = currentExecutionFile.getParentFile();
		
    	if(PhotonInfosManager.hasLauncherUpdate(LauncherEngine.VERSION)) {
			ConsoleManager.create("Update avalible! Updating: "+currentExecutionFile+" in : "+currentExecutionFolder).withType(EnumLogType.LAUNCHER).end();
    		final SplashScreen splash = new SplashScreen(PhotonInfosManager.getInfos().project_name, PhotonInfosManager.getGameLogo(), 250, 250);
    		final SplashPanel panel = (SplashPanel)splash.getContentPane();
			/* Download the file */
			PhotonInfosManager.updateLauncherFromDir(currentExecutionFolder);
			while(PhotonInfosManager.isUpdating) {
				ConsoleManager.print("Update "+PhotonInfosManager.updateSizeDownloaded+"Mb on "+PhotonInfosManager.updateSize+"Mb");
				panel.progressBar.setValue((int)PhotonInfosManager.updateSizeDownloaded);
				panel.progressBar.setMaximum((int)PhotonInfosManager.updateSize);
			}
			/* Launch and exit */
			ApplicationUtils.launch(new File(currentExecutionFolder, "/launcher.jar"), new String[] {}, true);
    		return;
    	}
		
		/* Load config and translations system */
    	LauncherConfig.load(gameEngine);
    	TranslationManager.load((String)LauncherConfig.getConfig().language, "lang");

		/* Display the interface */
		Application.launch(MainStage.class, args);
	}
	
	public static class MainStage extends Application {
		public MainStage() { /* This constructor exist only because JFX needs it */ }
		
		public static Pane globalPane = new Pane();

        public static Pane homePane = GlobalHome.getHomeMenu(false);
        public final static Pane settingsPane = GlobalSettings.getSettingsMenu();

        public static final int launcherHeight = 617;
        public static final int launcherWidth = 990;

        private static double[] Offset = new double[]{0, 0};

		public static Stage classStage;

		@Override
		public void start(Stage stage) throws Exception {
			classStage = stage;

			final Rectangle globalShape = new Rectangle(0, 0, launcherWidth, launcherHeight);
            globalShape.setArcHeight(20);
            globalShape.setArcWidth(20);
     
            setScene("LAUNCHER");
            globalPane.setClip(globalShape);
			
            // Create launcher scene
            Scene scene = new Scene(globalPane, launcherWidth, launcherHeight);
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
			globalShape.requestFocus();
            stage.getIcons().add(getIcon());
            stage.initStyle( StageStyle.TRANSPARENT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
		}

		public static Image getIcon() { return new Image(NetworkDirectories.config.webUrl+"/project-logo.png"); }

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

		public static void closeLauncher() { System.exit(0); }

		public static void minimizeLauncher() { classStage.setIconified(true); }

	}
}
