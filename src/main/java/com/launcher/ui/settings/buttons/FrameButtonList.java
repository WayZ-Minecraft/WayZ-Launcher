package com.launcher.ui.settings.buttons;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.layout.AnchorPane;

public class FrameButtonList {
    
    List<FrameButton> buttonList = new ArrayList<FrameButton>();
    int selectedButton;
    final static int buttonJump = 15;

    public FrameButtonList() {
    }

    public List<FrameButton> getButtonList() {
        return buttonList;
    }

    public void addButton(String text, String imagePath, boolean isNavMenu) {
        FrameButton button = new FrameButton(text, imagePath, isNavMenu, this, this.buttonList.size());
        this.buttonList.add(button);
    }

    public void setSelectedButton(int index) {
        if (index == this.selectedButton) {
            this.buttonList.get(index).pointTransition.getShape().setFill(FrameButton.pointColor);
            return;
        }
        ParallelTransition parallelTransition = new ParallelTransition();
        FillTransition selectedPointTransition = this.buttonList.get(index).pointTransition;
        
        selectedPointTransition.setFromValue(FrameButton.backgroundColor);
        selectedPointTransition.setToValue(FrameButton.pointColor);
        
        FillTransition unselectedPointTransition = this.buttonList.get(this.selectedButton).pointTransition;
        
        unselectedPointTransition.setFromValue(FrameButton.pointColor);
        unselectedPointTransition.setToValue(FrameButton.backgroundColor);
        
        parallelTransition.getChildren().addAll(selectedPointTransition, unselectedPointTransition);
        this.selectedButton = index;

        parallelTransition.playFromStart();
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
