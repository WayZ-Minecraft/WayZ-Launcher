package com.launcher.ui.settings.boxes.launcher;

import java.io.File;
import java.io.IOException;

import com.launcher.LauncherEngine;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.ui.settings.boxes.TextFieldElement;
import com.launcher.utils.LauncherConfig;
import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;
import com.photon.util.TranslationManager;
import com.photon.util.os.ApplicationUtils;
import com.photon.util.os.FileLocation;
import com.photon.util.os.OperatingSystem;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class LaunchSettings extends TextFieldElement {

    final static String title = TranslationManager.format("settings.launcher.properties.title");
    final GridPane content = new GridPane();
    final static int boxHeight = 240;

    public LaunchSettings(int boxWidth) {
        super(title, boxWidth, boxHeight);

        this.content.setPrefWidth(boxWidth - 2*sidePadding);
        this.content.setPrefHeight(boxHeight - 20);
        this.content.setPadding(new Insets(10, 0, 10, 0));

        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding);
        this.content.setLayoutY(30);

        this.content.setVgap(10);

        this.fillBox();
    }
    
    protected void fillBox() {
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.launcher.properties.swt.keep_open"), LauncherConfig.getConfig().keep_open, (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            LauncherConfig.getConfig().keep_open = object;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        }), 0, 0);
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.launcher.properties.swt.send_reports"), LauncherConfig.getConfig().send_reports, (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            LauncherConfig.getConfig().send_reports = object;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        }), 0, 1);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.reset_files"),  TranslationManager.format("settings.launcher.properties.btn.reset_files.inner"), "logos/delete.png", "#ffffff", (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            deleteFolder(LauncherEngine.gameEngine.getGameFolder().getGameDir());
            ApplicationUtils.exitProperly();
        }), 0, 2);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.get_files"), TranslationManager.format("settings.launcher.properties.btn.get_files.inner"), "logos/folder.png", "#ffeb7e", (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            OperatingSystem.openFolder(LauncherEngine.gameEngine.getGameFolder().getGameDir());
        }), 0, 3);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.clear_logs"), TranslationManager.format("settings.launcher.properties.btn.clear_logs.inner"), "logos/delete.png", "#ffeb7e", (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            try {
                LauncherEngine.clearLogs();
            } catch (IOException e) {
                ConsoleManager.create(ConsoleManager.of(e)).error().withType(EnumLogType.LAUNCHER).end();
            }
        }), 0, 4);
    }

    private static void deleteFolder(File folder) {
        if(!folder.exists()) return;
        if(folder.isDirectory()) {
            for(File file : folder.listFiles()) deleteFolder(file);
        }
        folder.delete();
    }
}
