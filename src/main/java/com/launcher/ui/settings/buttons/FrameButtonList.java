package com.launcher.ui.settings.buttons;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.AnchorPane;

public class FrameButtonList {
    
    List<FrameButton> buttonList = new ArrayList<FrameButton>();
    final static int buttonJump = 15;

    public List<FrameButton> getButtonList() {
        return buttonList;
    }

    public void addButton(String text, String imagePath, boolean isNavMenu) {
        FrameButton button = new FrameButton(text, imagePath, isNavMenu, this);
        this.buttonList.add(button);
    }

    public AnchorPane printButtonList() {
        AnchorPane buttonList = new AnchorPane();
        int buttonHeight = 0;
        for (FrameButton button : this.buttonList) {
            buttonList.getChildren().add(button.loadNavigateButton());
            AnchorPane.setTopAnchor(button.button, (double)buttonHeight);
            buttonHeight += button.buttonHeight + buttonJump;
        }
        buttonList.setPrefHeight(buttonHeight);
        return buttonList;
    }
}
