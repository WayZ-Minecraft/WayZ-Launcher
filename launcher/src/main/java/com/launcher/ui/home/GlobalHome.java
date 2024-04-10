package com.launcher.ui.home;

import com.launcher.LauncherEngine;
import com.launcher.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.buttons.GlobalHomeButton;
import com.launcher.ui.home.buttons.PlayButton;
import com.photon.network.NetworkDirectories;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GlobalHome {

    public static Text status;
    public static ProgressBar pb = new ProgressBar(0);

    /**
     * @return Pane : The home menu
     */
    public static Pane getHomeMenu(boolean disabled) {
        Pane globalPane = new Pane();

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
        pb.setLayoutY(MainStage.launcherHeight-15);
        pb.setPrefWidth(MainStage.launcherWidth);
        pb.setProgress(0);
        pb.setVisible(false);
        pb.setStyle(String.format("-fx-accent: %s; -fx-background-color: %s; -fx-box-border: %s; -fx-padding: 0;",
        "#8b2628", "#0c0d0e", "none"));
        globalPane.getChildren().add(pb);
        
        // Status text
        status = JFXUtils.loadText("", 15, "ffffff", "light");
        status.setLayoutX(MainStage.launcherWidth - 15 - status.getLayoutBounds().getWidth());
        status.setLayoutY(MainStage.launcherHeight - 25);
        status.setVisible(false);
        globalPane.getChildren().add(status);

        // Play button
        AnchorPane playButton = PlayButton.getPlayButton();
        globalPane.getChildren().add(playButton);
        playButton.setLayoutX(680);
        playButton.setLayoutY(500);
        playButton.setDisable(disabled);

        // Logo
        ImageView iconImage = getIconImage();
        globalPane.getChildren().add(iconImage);
        iconImage.setLayoutX(20);
        iconImage.setLayoutY(20);

        // Bar (top)
        AnchorPane TopBar = getTopBar(23);
        
        globalPane.getChildren().add(TopBar);
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
    public static Text loadVersion(int textSize) { return JFXUtils.loadText(LauncherEngine.gameEngine.getName(), textSize,"ffff", "light"); }

    /**
     * load a cross
     * @param width : width of the cross
     * @param height : height of the cross
     * @crossReduction : reduction of the cross (ex : 2 = 1/2 of the width and height)
     * @return Pane : white cross
     */
    private static Pane loadCross(int width,int height, double crossReduction){
        Pane cross = new Pane();
        cross.setPrefSize(width, height);

        Rectangle background = JFXUtils.loadBackground(width, height, Color.web("#2a2b2c00"), 2);
        cross.getChildren().add(background);

        int crossWith = (int)(width/(crossReduction * 1.5));
        int crossHeight = (int)(height/crossReduction);

        int[] crossPoint1 = new int[]{width/2 - crossWith/2, height/2 - crossHeight/2};
        int[] crossPoint2 = new int[]{width/2 + crossWith/2, height/2 + crossHeight/2};

        Line line1 = new Line(crossPoint1[0],crossPoint1[1],crossPoint2[0],crossPoint2[1]);
        line1.setStrokeWidth(1);
        line1.setStroke(Color.web("#ffffff"));
        
        Line line2 = new Line(crossPoint1[0],crossPoint2[1],crossPoint2[0],crossPoint1[1]);
        line2.setStrokeWidth(1);
        line2.setStroke(Color.web("#ffffff"));
        
        cross.getChildren().addAll(line1,line2);
        
        cross.setOnMouseClicked(event -> {
            FileLocation.playSound("sounds/click_btn", 0);
            MainStage.closeLauncher();
        });

        cross.setOnMouseEntered(e -> {
            line1.setStroke(Color.web("#8b2628"));
            line2.setStroke(Color.web("#8b2628"));
            background.setFill(Color.web("#2a2b2cff"));
            FileLocation.playSound("sounds/hover_btn", 0);
        });

        cross.setOnMouseExited(e -> {
            line1.setStroke(Color.web("#ffffff"));
            line2.setStroke(Color.web("#ffffff"));
            background.setFill(Color.web("#1a1b1c00"));
        });

        return cross;
    }

    /**
     * A Line Centered in the middle of the Pane
     * @param width : width of the pane
     * @param height : height of the pane
     * @param lineReduction : reduction of the line (ex : 2 = 1/2 of the width of the pane)
     * @return StackPane : Line
     */
    private static StackPane loadLine(int width, int height, double lineReduction){
        StackPane pane = new StackPane();
        pane.setPrefSize(width, height);

        Rectangle background = JFXUtils.loadBackground(width, height, Color.web("#2a2b2c00"), 2);
        pane.getChildren().add(background);

        Line line = new Line(width/2-width/(2*lineReduction),0,width/2+width/(2*lineReduction),0);
        line.setStrokeWidth(1);
        line.setStyle("-fx-stroke: #ffffff");

        pane.getChildren().add(line);

        pane.setOnMouseEntered(e -> {
            background.setFill(Color.web("#2a2b2cff"));
            FileLocation.playSound("sounds/hover_btn", 0);
        });

        pane.setOnMouseExited(e -> {
            background.setFill(Color.web("#1a1b1c00"));
        });

        return pane;
    }

    /**
     * Create the top bar of the launcher
     * @param height : height of the bar
     * @return AnchorPane : The bar
     */
    private static AnchorPane getTopBar(int height){
        AnchorPane Bar = new AnchorPane();
        Rectangle box = new Rectangle(MainStage.launcherWidth, height);
        box.setFill(Color.web("#0c0d0eee"));
        Bar.getChildren().add(box);

        // Version (corner top left)
        Text version = loadVersion(height * 2/3);
        Bar.getChildren().add(version);
        AnchorPane.setLeftAnchor(version, 10.0);
        AnchorPane.setTopAnchor(version, height/2 - version.getLayoutBounds().getHeight()/2);

        // Close button (corner top right)
        Pane cross = loadCross((int)(height * 1.5),height,3);
        Bar.getChildren().add(cross);
        AnchorPane.setRightAnchor(cross, 0.0);
        AnchorPane.setTopAnchor(cross, height/2 - cross.getPrefHeight()/2);

        // Minimize Line (top right)
        StackPane line = loadLine((int)(height*1.4), height, 4);
        Bar.getChildren().add(line);
        AnchorPane.setRightAnchor(line, height * 1.5);
        AnchorPane.setTopAnchor(line, height/2 - line.getPrefHeight()/2);

        line.setOnMouseClicked(e -> {
            FileLocation.playSound("sounds/click_btn", 0);
            MainStage.minimizeLauncher();
        });
        return Bar;
    }

    public static void setStatus(int min, int max) {
        status.setText(TranslationManager.format("updater.count", min, max));
        status.setLayoutX(MainStage.launcherWidth - 15 - status.getLayoutBounds().getWidth());
    }
}
