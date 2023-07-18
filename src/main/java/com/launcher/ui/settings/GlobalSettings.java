package com.launcher.ui.settings;

import com.launcher.LauncherEngine.MainStage;
import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.FrameSettings;
import com.launcher.ui.settings.boxes.game.JvmBox;
import com.launcher.ui.settings.boxes.game.RamBox;
import com.launcher.ui.settings.boxes.game.resolutionBox;
import com.launcher.ui.settings.boxes.launcher.LanguageBox;
import com.launcher.ui.settings.boxes.launcher.LaunchSettings;
import com.launcher.ui.settings.buttons.ActionButton;
import com.launcher.ui.settings.buttons.FrameButtonList;
import com.launcher.utils.LauncherConfig;
import com.photon.util.TranslationManager;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GlobalSettings {

    public static ScrollPane frame = new ScrollPane();

    public final static ScrollPane launcherFrame = getLauncherFrame();
    public final static ScrollPane gameFrame = getGameFrame();

    final static int frameLayoutX = 300;
    final static int frameLayoutY = 120;

    final static int frameWidth = FrameSettings.frameWidth - 50;

    /**
     * 
     * @return Pane : The title settings with the underline bar
     */
    public static Pane setTitle(){
        Pane titlePane = new Pane();
        Text title = JFXUtils.loadText("Settings :", 27, "#ffffff", "light");
        titlePane.getChildren().add(title);

        Rectangle underline = new Rectangle(250, 1);
        underline.setFill(Color.web("#7b7b7b"));
        titlePane.getChildren().add(underline);
        underline.setLayoutY(20);

        return titlePane;
    }

    /**
     * 
     * @return Pane : The back button
     */
    private static Pane getBackButton(){
        ActionButton button = new ActionButton(TranslationManager.format("btn.settings.back"), "logos/back.png", "#e0e0e0");
        Runnable back = () -> { MainStage.setScene("LAUNCHER"); };
        button.setClick(back);
        return button.loadNavigateButton();
    }

    /**
     * 
     * @return Pane : The save button
     */
    private static Pane getSaveButton() {
        ActionButton button = new ActionButton(TranslationManager.format("btn.settings.save"), "logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png", "#3ba55d");
        button.setClick(()-> {
            LauncherConfig.saveConfig();
        });
        return button.loadNavigateButton();
    }

    /**
     * 
     * @return Pane : The reset button
     */
    private static Pane getResetButton(){
        ActionButton button = new ActionButton(TranslationManager.format("btn.settings.reset"), "logos/refresh-arrow.png", "#8b2628");
        button.setClick(()-> {
            LauncherConfig.resetConfig();
        });
        return button.loadNavigateButton();
    }

    /**
     * 
     * @return Pane : The frame buttons (Launcher, Game, ...)
     */
    private static Pane getFrameButtons(){
        FrameButtonList buttonList = new FrameButtonList();
        buttonList.addButton(TranslationManager.format("btn.frame.settings.launcher"), FrameName.LAUNCHER, "logos/launcherSettings.png", true);
        buttonList.addButton(TranslationManager.format("btn.frame.settings.game"), FrameName.GAME, "logos/gameSettings.png", true);
        buttonList.setSelectedButton(0);
        return buttonList.printButtonList();
    }


    /**
     * 
     * @return Pane : The settings template (background, NavButtons, title, ...)
     */
    private static Pane getSettingsTemplate() {
        Pane template = new Pane();

        ImageView backgroundPicture = JFXUtils.loadImageView("settingsBackground.jpg", MainStage.launcherWidht, MainStage.launcherHeight);
        template.getChildren().add(backgroundPicture);

        Pane buttonBack = getBackButton();
        template.getChildren().add(buttonBack);
        buttonBack.setLayoutX(30);
        buttonBack.setLayoutY(MainStage.launcherHeight - 70);

        Pane buttonSave = getSaveButton();
        template.getChildren().add(buttonSave);
        buttonSave.setLayoutX(30 + buttonSave.getPrefWidth() + 15);
        buttonSave.setLayoutY(MainStage.launcherHeight - 70);

        Pane buttonReset = getResetButton();
        template.getChildren().add(buttonReset);
        buttonReset.setLayoutX(MainStage.launcherWidht - 30 - buttonReset.getPrefWidth());
        buttonReset.setLayoutY(MainStage.launcherHeight - 70);

        Pane buttonList = getFrameButtons();
        template.getChildren().add(buttonList);
        buttonList.setLayoutX(30);
        buttonList.setLayoutY(MainStage.launcherHeight / 2 - buttonList.getPrefHeight() / 2);

        Pane title = setTitle();
        template.getChildren().add(title);
        title.setLayoutX(300);
        title.setLayoutY(70);
        
        return template;
    }

    /**
     * To charge the launcher frame
     * @return ScrollPane : The frame
     */
    private static ScrollPane getLauncherFrame() {
        FrameSettings launcherFrame = new FrameSettings();
        launcherFrame.addBox(new LanguageBox(frameWidth));
        launcherFrame.addBox(new LaunchSettings(frameWidth));

        return launcherFrame.getFrame();
    }

    /**
     * To charge the game frame
     * @return ScrollPane : The frame
     */
    private static ScrollPane getGameFrame(){
        FrameSettings launcherFrame = new FrameSettings();
        launcherFrame.addBox(new RamBox(frameWidth));
        launcherFrame.addBox(new resolutionBox(frameWidth));
        launcherFrame.addBox(new JvmBox(frameWidth));

        return launcherFrame.getFrame();
    }

    /**
     * To charge a settings frame
     * @param frameName : The name of the frame to return
     * @return ScrollPane : The frame
     */
    private static ScrollPane getFrame(FrameName frameName){
        ScrollPane frame;
        switch (frameName) {
            case LAUNCHER:
                frame = launcherFrame;
                break;
            case GAME:
                frame = gameFrame;
                break;
                default: throw new IllegalArgumentException("The frame name is not valid");
            }
        frame.requestFocus();
        return frame;
    }

    public static void setLauncherFrame(FrameName frameName) { frame.setContent(getFrame(frameName)); }

    /**
     * Function that return the settings menu
     * @return Pane : The settings menu
     */
    public static Pane getSettingsMenu(){
        Pane settingsMenu = getSettingsTemplate();
        
        frame.setContent(getFrame(FrameName.LAUNCHER));
        frame.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        frame.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsMenu.getChildren().add(frame);
        frame.setLayoutX(frameLayoutX);
        frame.setLayoutY(frameLayoutY);

        return settingsMenu;
    }
}
