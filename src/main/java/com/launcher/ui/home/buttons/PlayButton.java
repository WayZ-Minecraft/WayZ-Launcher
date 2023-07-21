package com.launcher.ui.home.buttons;

import com.launcher.LauncherEngine;
import com.launcher.LauncherEngine.MainStage;
import com.launcher.ui.AlertPopup;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.GlobalHome;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.updater.GameUpdater;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayButton {

    private static final Color backgroundColor = Color.web("#0c0d0e");
    private static final Color hoveredColor = Color.web("#1a1b1c");

    private static Thread updateThread;
    private static GameUpdater updater;
    
    /**
     * 
     * @return ImageView : Icone play 
     */
    private static ImageView loadIconPlay(){
        return JFXUtils.loadImageView("logos/iconPlay.png", 20, 20, "#3ba55d");
    }

    /**
     * 
     * @return Text : Charge the texte for Play button ("Launch the game")
     */
    private static Text loadTextPlay() {
        return JFXUtils.loadText(TranslationManager.format("btn.play"), 20, "#f2f2f2", "regular");     
    }

    /**
     * 
     * @return AnchorPane : Background for Play button
     */
    private static AnchorPane printBackgroundPlay(){
        Rectangle shape = new Rectangle(270, 70);
        shape.setArcHeight(36);
        shape.setArcWidth(36);
        shape.setFill(backgroundColor);

        Rectangle shape2 = new Rectangle(270, 70);
        shape2.setArcHeight(36);
        shape2.setArcWidth(36);
        shape2.setFill(Color.web("#1a1b1c00"));         
        
        AnchorPane buttonCanvas = new AnchorPane();
        buttonCanvas.setPrefSize(270, 70);
        buttonCanvas.getChildren().add(shape);
        buttonCanvas.getChildren().add(shape2);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), shape2);
        scaleTransition.setCycleCount(1);
        
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.4), shape2);
        translateTransition.setCycleCount(1);
        
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.2), shape2);
        
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
            scaleTransition,
            translateTransition,
            fillTransition
        );

        Timeline timelineEntered = new Timeline(
            new KeyFrame(Duration.seconds(0.1), e -> {
                parallelTransition.stop();
                fillTransition.setFromValue(Color.web("#1a1b1c00"));
                fillTransition.setToValue(Color.web("#1a1b1cff"));
                scaleTransition.setFromX(0);
                scaleTransition.setToX(1);
                translateTransition.setFromX(-135);
                translateTransition.setToX(0);
                parallelTransition.play();
            }),
            new KeyFrame(Duration.seconds(0.4), e -> {})
        );

        Timeline timelineExited = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                parallelTransition.stop();
                fillTransition.setFromValue(Color.web("#1a1b1cff"));
                if (shape2.getScaleX() == 1){
                    scaleTransition.setFromX(1);
                    scaleTransition.setToX(0);
                    translateTransition.setFromX(0);
                    translateTransition.setToX(135);
                } else {
                    scaleTransition.setFromX(shape2.getScaleX());
                    scaleTransition.setToX(0);
                    translateTransition.setFromX(shape2.getTranslateX());
                    translateTransition.setToX(-135);
                }
                parallelTransition.play();
            }),
            new KeyFrame(Duration.seconds(0.2), e -> {
                fillTransition.setFromValue(Color.web("#1a1b1cff"));
                fillTransition.setToValue(Color.web("#1a1b1c00"));       
            }),
            new KeyFrame(Duration.seconds(0.4), e -> {})
        );

        buttonCanvas.setOnMouseEntered(e -> {
            timelineExited.stop();
            timelineEntered.play();
            FileLocation.playSound("sounds/hover_btn", 0);
        });
            
        buttonCanvas.setOnMouseExited(e -> {
            timelineEntered.stop();
            timelineExited.play();
        });

        return buttonCanvas;
    }


    /**
     * Set the click event for Play button
     * @param playButton : AnchorPane : Play button
     */
    private static void setClick(AnchorPane playButton){
        // Init Updater
        updater = new GameUpdater(GameUpdater.prepareGameUpdate(LauncherEngine.gameEngine), LauncherEngine.gameEngine);

        playButton.setOnMouseReleased(event -> {
            FileLocation.playSound("sounds/click_btn", 0);
            
            /* Disable buttons */
            MainStage.homePane = GlobalHome.getHomeMenu(true);
            MainStage.setScene("LAUNCHER");

            /* Start updating */
            updateThread = new Thread(() -> { updater.downloadGameAndRun(); });
            final Thread t = new Thread(() -> {
                GlobalHome.pb.setVisible(true);
                while(updateThread.isAlive()) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(updater.filesToDownload > 0) {
                        System.out.println(updater.downloadedFiles + " / " + updater.filesToDownload);
                        GlobalHome.pb.setProgress(updater.downloadedFiles/(double)updater.filesToDownload);
                        Thread.yield();
                        // status.setText(TranslationManager.format("updater.count", updater.downloadedFiles, updater.filesToDownload));
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            updateThread.start();
            
            /* Set executables for every type of exit and launch */
            LauncherEngine.gameEngine.startRunnable = () -> { MainStage.globalPane.setVisible(false); };
            LauncherEngine.gameEngine.exitRunnable = () -> {
                MainStage.globalPane.setVisible(true);
                GlobalHome.pb.setVisible(false);
                GlobalHome.pb.setProgress(0);
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
                GlobalHome.pb.setVisible(false);
                GlobalHome.pb.setProgress(0);
                MainStage.homePane = GlobalHome.getHomeMenu(false);
                MainStage.setScene("LAUNCHER");
                updateThread.interrupt();
            };
        });
    }
    
    /**
     * 
     * @return AnchorPane : Play button
     */
    public static AnchorPane playButton(){
        AnchorPane buttonCanvas = printBackgroundPlay();
        
        final ImageView iconPlay = loadIconPlay();
        buttonCanvas.getChildren().add(iconPlay);
        AnchorPane.setTopAnchor(iconPlay, 35 - iconPlay.getFitHeight()/2);
        AnchorPane.setRightAnchor(iconPlay, 25.0);

        final Text textPlay = loadTextPlay();
        buttonCanvas.getChildren().add(textPlay);
        AnchorPane.setTopAnchor(textPlay, 35.0 - textPlay.getLayoutBounds().getHeight()/2);
        AnchorPane.setLeftAnchor(textPlay, 40.0);

        setClick(buttonCanvas);

        return buttonCanvas;
    }
}
