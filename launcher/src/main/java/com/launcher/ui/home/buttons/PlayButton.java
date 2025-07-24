package com.launcher.ui.home.buttons;

import java.io.File;
import java.util.StringJoiner;

import javax.swing.JOptionPane;

import com.launcher.LauncherEngine;
import com.launcher.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.home.GlobalHome;
import com.launcher.utils.LauncherConfig;
import com.launcher.utils.updater.GameUpdater;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbar.Type;
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
import javafx.scene.Cursor;
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
    private static Timeline timelineEntered;
    private static Timeline timelineExited;
    private static Timeline timelineClicked;

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

    private static void loadTimelines(Rectangle forground){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), forground);
        scaleTransition.setCycleCount(1);
        
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.4), forground);
        translateTransition.setCycleCount(1);
        
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.2), forground);
        
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
            scaleTransition,
            translateTransition,
            fillTransition
        );

        timelineEntered = new Timeline(
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

        timelineExited = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                parallelTransition.stop();
                fillTransition.setFromValue(Color.web("#1a1b1cff"));
                if (forground.getScaleX() == 1){
                    scaleTransition.setFromX(1);
                    scaleTransition.setToX(0);
                    translateTransition.setFromX(0);
                    translateTransition.setToX(135);
                } else {
                    scaleTransition.setFromX(forground.getScaleX());
                    scaleTransition.setToX(0);
                    translateTransition.setFromX(forground.getTranslateX());
                    translateTransition.setToX(-135);
                }
                parallelTransition.play();
            }),
            new KeyFrame(Duration.seconds(0.4), e -> {})
        );

        timelineClicked = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                parallelTransition.stop();
                fillTransition.setFromValue(Color.web("#1a1b1cff"));
                fillTransition.setToValue(Color.web("#1a1b1caa"));
                scaleTransition.setFromX(1);
                translateTransition.setFromX(0);
                parallelTransition.play();
                scaleTransition.setFromY(1);
            }),
            new KeyFrame(Duration.seconds(0.4), e -> {})
        );
    }


    /**
     * Use to get the background for Play button with animation
     * @return AnchorPane : Background for Play button
     */
    private static AnchorPane printBackgroundPlay(){
        Rectangle background = new Rectangle(270, 70);
        background.setArcHeight(36);
        background.setArcWidth(36);
        background.setFill(backgroundColor);

        Rectangle forground = new Rectangle(270, 70);
        forground.setArcHeight(36);
        forground.setArcWidth(36);
        forground.setFill(Color.web("#1a1b1c00"));         
        
        AnchorPane buttonCanvas = new AnchorPane();
        buttonCanvas.setPrefSize(270, 70);
        buttonCanvas.getChildren().add(background);
        buttonCanvas.getChildren().add(forground);

        loadTimelines(forground);

        buttonCanvas.setOnMouseEntered(e -> {
            timelineExited.stop();
            timelineEntered.play();
            FileLocation.playSound("sounds/hover_btn", 0);
            buttonCanvas.setCursor(Cursor.HAND);
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
                MainStage.globalPane.setVisible(true);

                /* Hide progress bar */
                GlobalHome.pb.setVisible(false);
                GlobalHome.pb.setProgress(0);

                /* Hide status text */
                GlobalHome.status.setVisible(false);
                GlobalHome.setStatus(0, 0);

                generatePlayButton();
                MainStage.homePane = GlobalHome.getHomeMenu(false);
                MainStage.setScene("LAUNCHER");
                updateThread.interrupt();
            });
        } catch (RuntimeException e) {
            ConsoleManager.create("Error on relaunch " + e).error().withType(EnumLogType.LAUNCHER).end();
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
            
            timelineExited.stop();
            timelineEntered.stop();
            timelineClicked.play(); 
            
            final Thread t = new Thread(() -> {
                if(updater.filesToDownload > nbFilesToBeUpdating) {
                    onUpdating = true;
                    GlobalHome.pb.setVisible(true);
                    GlobalHome.status.setVisible(true); 
                    while(updateThread.isAlive()) {
                        try { Thread.sleep(300); }
                        catch (InterruptedException e) {
                            ConsoleManager.create("Download interrupted").withType(EnumLogType.LAUNCHER).error().end();
                            break;
                        }
                        GlobalHome.pb.setProgress(MainStage.progress = updater.downloadedFiles / (double) updater.filesToDownload);
                        GlobalHome.setStatus(updater.downloadedFiles, updater.filesToDownload);
                        
                        updateProgressBar(updater);
                    }
                }
            });
            t.setDaemon(true);
            
            /* Start updating */
            updateThread = new Thread(() -> { updater.downloadGameAndRun(t); });
            updateThread.start();
            
            /* Set executables for every type of exit and launch */
            LauncherEngine.gameEngine.startRunnable = () -> {
                if(!LauncherConfig.getConfig().keep_open) MainStage.closeLauncher();
                else MainStage.minimizeLauncher(false);
            };
            LauncherEngine.gameEngine.exitRunnable = () -> Platform.runLater(() -> reLaunchLauncher());
            LauncherEngine.gameEngine.crashRunnable = () -> Platform.runLater(() -> {
                reLaunchLauncher();
                final File crashDir = new File(LauncherEngine.gameEngine.getGameFolder().getPlayDir(), "crash-reports/");
                if(!crashDir.exists()) crashDir.mkdirs();
                if(LauncherConfig.getConfig().send_reports) {
                    //TODO : Implement your own crash report sending system
                }
                JOptionPane.showMessageDialog(null, TranslationManager.format("popup.error.message.crash"+(LauncherConfig.getConfig().send_reports? "":".nosending")), TranslationManager.format("popup.error.title"), JOptionPane.ERROR_MESSAGE);
            });
        });
    }

    private static void updateProgressBar(GameUpdater updater) {
        try {
            if(Class.forName("com.nativejavafx.taskbar.TaskbarProgressbar") != null) {
                Platform.runLater(() -> {
                    if (TaskbarProgressbar.isSupported()) 
                        TaskbarProgressbar.showCustomProgress(MainStage.classStage, (updater.downloadedFiles * 100 / (double) updater.filesToDownload) / 100, Type.NORMAL);
                });
            }
        } catch (ClassNotFoundException e) {}
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
