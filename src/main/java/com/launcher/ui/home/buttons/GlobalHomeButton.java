package com.launcher.ui.home.buttons;

import com.launcher.MainStage;
import com.launcher.ui.JFXUtils;
import com.photon.informations.ObjectInfos;
import com.photon.informations.PhotonInfosManager;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;
import com.photon.util.os.OperatingSystem;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GlobalHomeButton {

    /**
     * 
     * @param path : Path of the logo
     * @param width : Width of the logo
     * @param height : Height of the logo
     * @return Pane : The logo with the animation scale (Using Pane to prevent annimation detection with png transparency)
     */
    private static Pane loadLogoMedia(String path, int width, int height){
        Pane selectZone = new Pane();
        selectZone.setPrefSize(width, height);
        ImageView logo = JFXUtils.loadImageView(path, width, height, "f2f2f2");
        selectZone.getChildren().add(logo);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), selectZone);
        scaleTransition.setCycleCount(1);
        
        selectZone.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn", 0);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });
        
        selectZone.setOnMouseExited(e -> {
            scaleTransition.setFromX(1.1);
            scaleTransition.setFromY(1.1);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
            
        });

        return selectZone;
    };

    /**
     * 
     * @return ImageView : Youtube logo
     */
    private static Pane loadYoutubeLogo(){
        return loadLogoMedia("logos/youtube.png", 25, 25);
    }
    
    /**
     * 
     * @return ImageView : Discord logo
     */
    private static Pane loadDiscordLogo(){
        return loadLogoMedia("logos/discord.png", 25, 25);
    }

    /**
     * 
     * @return ImageView : Twitch logo
     */
    private static Pane loadTwitchLogo(){
        return loadLogoMedia("logos/twitch.png", 25, 25);
    }

    /**
     * 
     * @return Rectangle : The horizontal line between twitch and settings
     */
    private static Rectangle loadHorizontalLine(){
        Rectangle line = new Rectangle(35, 2);
        line.setFill(Color.web("#7b7b7b"));
        line.setArcHeight(5);
        line.setArcWidth(5);
        return line;
    }

    /**
     * @param color : Color of the logo in hex format
     * @return ImageView : Settings logo
     */
    private static ImageView loadSettingsLogo(String color){
        return JFXUtils.loadImageView("logos/settings.png", 25, 25, color);
    }

    /**
     * 
     * @return Text : Charge the texte for Settings button ("Settings")
     */
    private static Text loadTextSettings(String color) {
        return JFXUtils.loadText(TranslationManager.format("btn.settings"), 20, color, "light");  
    }

    /**
     * 
     * @return AnchorPane : Pane from Settings button
     */
    private static AnchorPane setttingPane(boolean disabled) {
        final ImageView logoSettings = loadSettingsLogo(disabled ? "#7b7b7b" : "#f2f2f2");
        final Text textSettings = loadTextSettings(disabled ? "#7b7b7b" : "#f2f2f2");

        AnchorPane button = new AnchorPane();
        button.setPrefSize(150, 20);

        button.getChildren().add(logoSettings);
        AnchorPane.setTopAnchor(logoSettings, 0.0);
        AnchorPane.setLeftAnchor(logoSettings, 0.0);

        button.getChildren().add(textSettings);
        AnchorPane.setTopAnchor(textSettings, 1.0);
        AnchorPane.setLeftAnchor(textSettings, 40.0);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.6), logoSettings);
        
        button.setOnMouseEntered(e -> {
            if(disabled) return;
            FileLocation.playSound("sounds/settings_gear", -20f);
            rotateTransition.setByAngle(-100);
            rotateTransition.play();
        });

        button.setOnMouseExited(e -> {
            if(disabled) return;
            rotateTransition.setByAngle(100);
            rotateTransition.play();
        });

        button.setOnMouseClicked(e -> {
            if(disabled) return;
            MainStage.setScene("SETTINGS");
        });

        return button;
    }

    /**
     * 
     * @return AnchorPane : Pane from Home button
     */
    public static AnchorPane printBackgroundHomeButton(boolean disabled){

        final ObjectInfos infos = PhotonInfosManager.getInfos();
        final Pane logoYoutube = loadYoutubeLogo();
        final Pane logoDiscord = loadDiscordLogo();
        final Pane logoTwitch = loadTwitchLogo();
        final Rectangle line = loadHorizontalLine();
        final AnchorPane settingsPane = setttingPane(disabled);

        AnchorPane canvas = new AnchorPane();
        canvas.setPrefSize(70, 300);

        logoYoutube.setOnMouseReleased(event -> {
            OperatingSystem.openLink(infos.youtube_url);
            FileLocation.playSound("sounds/click_btn", 0);
        });
        canvas.getChildren().add(logoYoutube);
        AnchorPane.setTopAnchor(logoYoutube, 45.0);
        AnchorPane.setLeftAnchor(logoYoutube, 25.0);

        logoDiscord.setOnMouseReleased(event -> {
            OperatingSystem.openLink(infos.discord_url);
            FileLocation.playSound("sounds/click_btn", 0);
        });
        canvas.getChildren().add(logoDiscord);
        AnchorPane.setTopAnchor(logoDiscord, 95.0);
        AnchorPane.setLeftAnchor(logoDiscord, 25.0);

        logoTwitch.setOnMouseReleased(event -> {
            OperatingSystem.openLink(infos.twitch_url);
            FileLocation.playSound("sounds/click_btn", 0);
        });
        canvas.getChildren().add(logoTwitch);
        AnchorPane.setTopAnchor(logoTwitch, 145.0);
        AnchorPane.setLeftAnchor(logoTwitch, 25.0);

        canvas.getChildren().add(line);
        AnchorPane.setTopAnchor(line, 205.0);
        AnchorPane.setLeftAnchor(line, 20.0);

        settingsPane.setOnMouseReleased(event -> FileLocation.playSound("sounds/click_btn", 0));
        canvas.getChildren().add(settingsPane);
        AnchorPane.setTopAnchor(settingsPane, 225.0);
        AnchorPane.setLeftAnchor(settingsPane, 25.0);



        return canvas;
    }
}
