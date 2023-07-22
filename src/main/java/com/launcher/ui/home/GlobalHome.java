package com.launcher.ui.home;


import com.launcher.LauncherEngine;
import com.launcher.LauncherEngine.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.buttons.GlobalHomeButton;
import com.launcher.ui.home.buttons.PlayButton;
import com.photon.informations.PhotonInfosManager;
import com.photon.network.NetworkDirectories;

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


    public static ProgressBar pb = new ProgressBar(0);

    /**
     * 
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
        pb.setProgress(0.2);
        pb.setVisible(false);
        pb.setStyle(String.format("-fx-accent: %s; -fx-background-color: %s; -fx-box-border: %s; -fx-padding: 0;",
        "#8b2628", "#0c0d0e", "none"));
        globalPane.getChildren().add(pb);
        
        // // Status bar
        // final Text status = JFXUtils.loadText("", 15, "ffffff", "light");
        // status.setLayoutX(MainStage.launcherWidth-15-status.getLayoutBounds().getWidth());
        // status.setLayoutY(MainStage.launcherHeight-25);
        // status.setVisible(disabled);
        // globalPane.getChildren().add(status);

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
    public static Text loadVersion(int textSize) { return JFXUtils.loadText(PhotonInfosManager.getInfos().project_name+" "+LauncherEngine.VERSION, textSize,"ffff", "light"); }

    /**
     * load a cross
     * @param width : width of the cross
     * @param height : height of the cross
     * @return Pane : white cross
     */
    private static Pane loadCross(int width,int height){
        Pane pane = new Pane();
        pane.setPrefSize(width, height);

        Line line1 = new Line(width/4,height/4,width * 3/4,height *3/4);
        line1.setStrokeWidth(1);
        line1.setStyle("-fx-stroke: #ffffff");
        
        Line line2 = new Line(width/4,height * 3/4,width *3/4, height/4);
        line2.setStrokeWidth(1);
        line2.setStyle("-fx-stroke: #ffffff");

        pane.getChildren().addAll(line1,line2);

        return pane;
    }

    /**
     * A Line Centered in the middle of the Pane
     * @param Size : size of the Pane (width and height) (Line size = Size/2)
     * @return StackPane : Line
     */
    private static StackPane loadLine(int Size){
        StackPane pane = new StackPane();
        pane.setPrefSize(Size, Size);

        Line line = new Line(-Size/4,0,Size/4,0);
        line.setStrokeWidth(1);
        line.setStyle("-fx-stroke: #ffffff");

        pane.getChildren().add(line);

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
        Pane cross = loadCross(height ,height);
        Bar.getChildren().add(cross);
        AnchorPane.setRightAnchor(cross, 10.0);
        AnchorPane.setTopAnchor(cross, height/2 - cross.getPrefHeight()/2);

        cross.setOnMouseClicked(event -> {
            LauncherEngine.MainStage.closeLauncher();
        });

        // Minimize Line (top right)
        StackPane line = loadLine(height);
        Bar.getChildren().add(line);
        AnchorPane.setRightAnchor(line, 40.0);
        AnchorPane.setTopAnchor(line, height/2 - line.getPrefHeight()/2);

        line.setOnMouseClicked(e -> {
            LauncherEngine.MainStage.minimizeLauncher();
        });
        return Bar;

    }
}
