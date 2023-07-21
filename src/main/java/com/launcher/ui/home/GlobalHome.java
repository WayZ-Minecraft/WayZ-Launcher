package com.launcher.ui.home;


import com.launcher.LauncherEngine;
import com.launcher.LauncherEngine.MainStage;
import com.launcher.ui.AlertPopup;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.buttons.GlobalHomeButton;
import com.launcher.ui.home.buttons.PlayButton;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.updater.GameUpdater;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GlobalHome {

    private static Thread updateThread;
    private static GameUpdater updater;
    private static ProgressBar pb = new ProgressBar(0.6);

    /**
     * 
     * @return Pane : The home menu
     */
    public static Pane getHomeMenu(boolean disabled) {
        Pane globalPane = new Pane();

        // Init Updater
        updater = new GameUpdater(GameUpdater.prepareGameUpdate(LauncherEngine.gameEngine), LauncherEngine.gameEngine);

        // Background
        ImageView backgroundPicture = JFXUtils.loadImageView("homeBackground.jpg", MainStage.launcherWidth, MainStage.launcherHeight);
        globalPane.getChildren().add(backgroundPicture);
        
        // Home button
        AnchorPane homeButton = GlobalHomeButton.printBackgroundHomeButton(disabled);
        globalPane.getChildren().add(homeButton);
        homeButton.setLayoutX(20);
        homeButton.setLayoutY(320);

        // Progress bar
        pb.setLayoutX(0);
        pb.setLayoutY(MainStage.launcherHeight-20);
        pb.setPrefWidth(MainStage.launcherWidth);
        pb.setProgress(0.5);
        pb.setVisible(false);
        pb.setStyle(String.format("-fx-accent: %s; -fx-background-color: %s; -fx-border-color: %s;",
        "#00ff00", "#000000", "#000000"));
        globalPane.getChildren().add(pb);
        
        // Status bar
        final Text status = JFXUtils.loadText("", 15, "ffffff", "light");
        status.setLayoutX(MainStage.launcherWidth-15-status.getLayoutBounds().getWidth());
        status.setLayoutY(MainStage.launcherHeight-25);
        status.setVisible(disabled);
        globalPane.getChildren().add(status);

        // Play button
        AnchorPane playButton = PlayButton.playButton();
        globalPane.getChildren().add(playButton);
        playButton.setLayoutX(680);
        playButton.setLayoutY(500);
        playButton.setDisable(disabled);
        playButton.setOnMouseReleased(event -> {
            FileLocation.playSound("sounds/click_btn", 0);
            
            /* Disable buttons */
            MainStage.homePane = GlobalHome.getHomeMenu(true);
            MainStage.setScene("LAUNCHER");

            /* Start updating */
            updateThread = new Thread(() -> { updater.downloadGameAndRun(); });
			updateThread.start();
            final Thread t = new Thread(() -> {
                while(updateThread.isAlive()) {
                    if(updater.filesToDownload > 0) {
                        pb.setVisible(true);
                        pb.setProgress((updater.downloadedFiles/updater.filesToDownload)/10);
                        status.setText(TranslationManager.format("updater.count", updater.downloadedFiles, updater.filesToDownload));
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            
            /* Set executables for every type of exit and launch */
            LauncherEngine.gameEngine.startRunnable = () -> { MainStage.globalPane.setVisible(false); };
            LauncherEngine.gameEngine.exitRunnable = () -> {
                MainStage.globalPane.setVisible(true);
                pb.setVisible(false);
                pb.setProgress(0);
                MainStage.homePane = GlobalHome.getHomeMenu(false);
                MainStage.setScene("LAUNCHER");
                updateThread.interrupt();
            };
            LauncherEngine.gameEngine.crashRunnable = () -> {
                new AlertPopup(TranslationManager.format("popup.error.title"),
                    TranslationManager.format("popup.error.message.crash"+(LauncherConfig.getConfig().send_reports? "":".nosending")));
				// 	panel.playBtn.setTextColor(Color.white);
				// 	panel.playBtn.setText(TranslationManager.format("mainPanel.play.text"));
                MainStage.globalPane.setVisible(true);
                pb.setVisible(false);
                pb.setProgress(0);
                MainStage.homePane = GlobalHome.getHomeMenu(false);
                MainStage.setScene("LAUNCHER");
                updateThread.interrupt();
            };
        });

        // Logo
        ImageView iconImage = getIconImage();
        globalPane.getChildren().add(iconImage);
        iconImage.setLayoutX(20);
        iconImage.setLayoutY(20);

        //Make a box
        AnchorPane anchor = new AnchorPane();
        Rectangle box = new Rectangle(MainStage.launcherWidth, 20);
        box.setFill(javafx.scene.paint.Color.rgb(
            LauncherEngine.boxColor.getRed(),
            LauncherEngine.boxColor.getGreen(),
            LauncherEngine.boxColor.getBlue(), 1));
        anchor.getChildren().add(box);

        // Version (corner top right)
        Text version = loadVersion();
        anchor.getChildren().add(version);
        version.setLayoutX(15);
        version.setLayoutY(15);

        globalPane.getChildren().add(anchor);
        return globalPane;
    }

    /**
     * Get the logo of the launcher
     * @return ImageView : The logo of the launcher
     */
    public static ImageView getIconImage() { return JFXUtils.loadImageView(NetworkDirectories.config.webUrl+"/project-logo.png", 125, 125); }

    /**
     * 
     * @return Text : Version du launcher
     */
    public static Text loadVersion() { return JFXUtils.loadText(PhotonInfosManager.getInfos().project_name+" "+LauncherEngine.VERSION, 13,"ffff", "light"); }
}
