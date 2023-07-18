package com.launcher.ui.settings.boxes.launcher;

import com.launcher.ui.settings.boxes.TextFieldElement;
import com.photon.util.TranslationManager;

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
    
    protected void fillBox(){
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.launcher.properties.swt.keep_open")), 0, 0);
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.launcher.properties.swt.send_reports")), 0, 1);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.reset_files"), TranslationManager.format("settings.launcher.properties.btn.reset_files.inner"), "logos/refresh-arrow.png", "#ffffff"), 0, 2);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.get_files"), TranslationManager.format("settings.launcher.properties.btn.get_files.inner"), "logos/folder.png", "#ffeb7e"), 0, 3);
        this.content.add(this.getButtonLine(TranslationManager.format("settings.launcher.properties.btn.uninstall"), TranslationManager.format("settings.launcher.properties.btn.uninstall.inner"), "logos/delete.png", "#8b2628"), 0, 4);
    }
}
