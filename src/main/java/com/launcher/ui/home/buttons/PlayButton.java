package com.launcher.ui.home.buttons;

import java.util.StringJoiner;

import javax.swing.JOptionPane;

import com.launcher.LauncherEngine;
import com.launcher.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.GlobalHome;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.updater.GameUpdater;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayButton {

    private static AnchorPane playButton;
    public static Thread loadingThread;
    private static int progress = 0;

    private static final Color backgroundColor = Color.web("#0c0d0e");

    private static Thread updateThread;
    private static GameUpdater updater;
    private static int nbFilesToBeUpdating = 3; // This is the number of files that is needed to consider the game as updated

    private static boolean onUpdating = false;


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


    private static Text loadTextLaunching(int progress) {
        String text;
        if (onUpdating){text = TranslationManager.format("btn.updating");}
        else {text = TranslationManager.format("btn.launching");}
        
        return JFXUtils.loadText(text + new StringJoiner("").add(".".repeat(progress))
                .toString(), 20, "#f2f2f2", "regular");     
    }


    /**
     * Use to get the background for Play button with animation
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
     * Generate a nex thread for the loading text
     * @return Thread : Loading Thread
     */
    private static void loadingText() {
        loadingThread = new Thread( () -> {
            while (loadingThread.isAlive()) {
                try {
                    Thread.sleep(1000);
                    progress = (progress + 1) % 4;
                    Platform.runLater(() -> {
                        Text textPlay = loadTextLaunching(progress);
                        playButton.getChildren().remove(3);
                        playButton.getChildren().add(textPlay);
                        AnchorPane.setTopAnchor(textPlay, 35.0 - textPlay.getLayoutBounds().getHeight()/2);
                        AnchorPane.setLeftAnchor(textPlay, 50.0);
                    });
                } catch (InterruptedException e) {
                    ConsoleManager.print(EnumLogType.LAUNCHER, "Loading text interrupted");
                    return; // Stop the thread
                }
            }
        });

        loadingThread.setDaemon(true);
        loadingThread.start();

    }

    /**
     * Fonction to recharge the launcher after the game is closed
     */
    private static void reLaunchLauncher() {
        loadingThread.interrupt();
        try {
            Platform.runLater(() -> {
                ConsoleManager.print("Relaunching launcher");
                MainStage.globalPane.setVisible(true);
                GlobalHome.pb.setVisible(false);
                GlobalHome.pb.setProgress(0);
                generatePlayButton();
                MainStage.homePane = GlobalHome.getHomeMenu(false);
                MainStage.setScene("LAUNCHER");
                updateThread.interrupt();
            });
        } catch (RuntimeException e) {
            ConsoleManager.print(EnumLogType.LAUNCHER,"Error on relaunch " + e);
        }
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
            loadingText();
            MainStage.setScene("LAUNCHER");

            
            final Thread t = new Thread(() -> {
                if(updater.filesToDownload > nbFilesToBeUpdating) {
                    onUpdating = true;
                    GlobalHome.pb.setVisible(true); 
                    while(updateThread.isAlive()) {
                        try {
                            Thread.sleep(300);
                            } catch (InterruptedException e) {
                                ConsoleManager.print(EnumLogType.LAUNCHER, "download interrupted");
                                break;
                            }
                            
                            GlobalHome.pb.setProgress(MainStage.progress = (updater.downloadedFiles/(double)updater.filesToDownload));
                            // status.setText(TranslationManager.format("updater.count", updater.downloadedFiles, updater.filesToDownload));
                        }
                    }

                });
                
                t.setDaemon(true);
                
                /* Start updating */
                updateThread = new Thread(() -> { updater.downloadGameAndRun(t); });

            updateThread.start();
            
            /* Set executables for every type of exit and launch */
            LauncherEngine.gameEngine.startRunnable = () -> { MainStage.globalPane.setVisible(false); };
            LauncherEngine.gameEngine.exitRunnable = () -> {
                reLaunchLauncher();
            };
            LauncherEngine.gameEngine.crashRunnable = () -> {
                reLaunchLauncher();
                JOptionPane.showMessageDialog(null, TranslationManager.format("popup.error.message.crash"+(LauncherConfig.getConfig().send_reports? "":".nosending")), TranslationManager.format("popup.error.title"), JOptionPane.ERROR_MESSAGE);
            };
        });
    }

    /**
     * Generate the play button
     */
    protected static void generatePlayButton(){
        playButton = printBackgroundPlay();
        onUpdating = false;
        
        final ImageView iconPlay = loadIconPlay();
        playButton.getChildren().add(iconPlay);
        AnchorPane.setTopAnchor(iconPlay, 35 - iconPlay.getFitHeight()/2);
        AnchorPane.setRightAnchor(iconPlay, 25.0);

        final Text textPlay = loadTextPlay();
        playButton.getChildren().add(textPlay);
        AnchorPane.setTopAnchor(textPlay, 35.0 - textPlay.getLayoutBounds().getHeight()/2);
        AnchorPane.setLeftAnchor(textPlay, 40.0);

        setClick(playButton);
    }
    
    /**
     * 
     * @return AnchorPane : Play button
     */
    public static AnchorPane getPlayButton(){
        if (playButton == null) generatePlayButton();
        return playButton;
    }
}
