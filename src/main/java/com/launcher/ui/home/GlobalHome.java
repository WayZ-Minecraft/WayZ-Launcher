package com.launcher.ui.home;


import com.launcher.LauncherEngine;
import com.launcher.LauncherEngine.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.buttons.GlobalHomeButton;
import com.launcher.ui.home.buttons.PlayButton;
import com.launcher.utils.updater.GameUpdater;
import com.photon.util.os.FileLocation;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class GlobalHome {

    private static Thread updateThread;
    private static GameUpdater updater;
    
    /**
     * 
     * @return Pane : The home menu
     */
    public static Pane getHomeMenu() {
        Pane globalPane = new Pane();

        // Init Updater
        updater = new GameUpdater(GameUpdater.prepareGameUpdate(LauncherEngine.gameEngine), LauncherEngine.gameEngine);

        // Background
        ImageView backgroundPicture = JFXUtils.loadImageView("homeBackground.jpg", MainStage.launcherWidht, MainStage.launcherHeight);
        globalPane.getChildren().add(backgroundPicture);
        
        // Progress bar

        // Status bar

        // Play button
        AnchorPane playButton = PlayButton.playButton();
        globalPane.getChildren().add(playButton);
        playButton.setLayoutX(680);
        playButton.setLayoutY(500);
        playButton.setOnMouseReleased(event -> {
            FileLocation.playSound("sounds/click_btn");
            playButton.setDisable(true);
            updateThread = new Thread(() -> { updater.downloadGameAndRun(); });
			updateThread.start();
        });

        // Logo
        ImageView iconImage = getIconImage();
        globalPane.getChildren().add(iconImage);
        iconImage.setLayoutX(20);
        iconImage.setLayoutY(20);

        // Version (corner top right)
        Text version = loadVersion();
        globalPane.getChildren().add(version);
        version.setLayoutX(MainStage.launcherWidht - version.getText().length()-35);
        version.setLayoutY(25);

        // Home button
        AnchorPane homeButton = GlobalHomeButton.printBackgroundHomeButton();
        globalPane.getChildren().add(homeButton);
        homeButton.setLayoutX(20);
        homeButton.setLayoutY(320);

        return globalPane;
    }


    /**
     * 
     * @return ImageView : Icone WayZ
     */
    public static ImageView getIconImage() {
        return JFXUtils.loadImageView("logos/WayZ.png", 125, 125);
    }

    /**
     * 
     * @return Text : Version du launcher
     */
    public static Text loadVersion() { return JFXUtils.loadText(LauncherEngine.VERSION, 13,"0c0d0e", "light"); }
}
