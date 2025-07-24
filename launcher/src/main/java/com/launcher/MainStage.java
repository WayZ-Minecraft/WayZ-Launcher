package com.launcher;

import com.launcher.ui.home.GlobalHome;
import com.launcher.ui.settings.GlobalSettings;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.os.FileLocation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainStage extends Application {
    public static Pane globalPane;

    public static Pane homePane = GlobalHome.getHomeMenu(false);
    public final static Pane settingsPane = GlobalSettings.getSettingsMenu();
    
    public static final int launcherHeight = 617;
    public static final int launcherWidth = 990;

    private static double[] Offset = new double[]{0, 0};

    public static Stage classStage;

    public static double progress = 0.0;

    public MainStage() { globalPane = new Pane(); }

    @Override
    public void start(Stage stage) throws Exception {
        ConsoleManager.create("Creating frame(s)...").withType(EnumLogType.LAUNCHER).end();
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
            FileLocation.muteSound(true);
            stage.setX(event.getScreenX() + Offset[0]);
            stage.setY(event.getScreenY() + Offset[1]);
        });

        scene.setOnMouseReleased(event -> FileLocation.muteSound(false));

        // Set stage properes
        globalShape.requestFocus();
        stage.getIcons().add(getIcon());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle(LauncherEngine.gameEngine.getName()+" - Launcher");
        stage.show();
    }

    public static Image getIcon() { return new Image("logos/project-logo.png"); }

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

    public static void closeLauncher() {
        Platform.runLater(() -> {
            classStage.close();
            System.exit(0);
        });
    }

    public static void minimizeLauncher() {
        minimizeLauncher(true);
    }

    public static void minimizeLauncher(boolean fullHide) {
        // Platform.setImplicitExit(explicit);
        Platform.runLater(() -> {
            if(fullHide) classStage.setIconified(true);
            else globalPane.setVisible(false);
        });
    }
}