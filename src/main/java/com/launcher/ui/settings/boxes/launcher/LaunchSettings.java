package com.launcher.ui.settings.boxes.launcher;

import com.launcher.ui.settings.boxes.TextFieldElement;
import com.photon.util.TranslationManager;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class LaunchSettings extends TextFieldElement {

    final GridPane content = new GridPane();
    final static int boxHeight = 210;

    public LaunchSettings(int boxWidth) {
        super(TranslationManager.format("title.launcher"), boxWidth, boxHeight);

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
        this.content.add(this.getSwitchLine(TranslationManager.format("btn.keep_open")), 0, 0);
        this.content.add(this.getSwitchLine(TranslationManager.format("btn.send_reports")), 0, 1);
        this.content.add(this.getButtonLine(TranslationManager.format("btn.reset_files"), "Reset", "logos/refresh-arrow.png", "#ffffff"), 0, 2);
        this.content.add(this.getButtonLine(TranslationManager.format("btn.uninstall"), "Uninstall", "logos/delete.png", "#8b2628"), 0, 3);
    }
}
