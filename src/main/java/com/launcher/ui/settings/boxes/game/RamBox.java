package com.launcher.ui.settings.boxes.game;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.TextFieldElement;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

/**
 * RamBox
 * The ram box in the game settings
 */
public class RamBox extends TextFieldElement{
    
    final GridPane content = new GridPane();
    int contentWidth;
    final static int boxHeight = 170;
    
    final String ramDefinition = "RAM allocation in the Minecraft launcher determines the amount of memory the game can use for smoother performance and gameplay. It helps Minecraft store and access data quickly while running, but allocating too much RAM can lead to system issues. Finding the right balance is important for optimal gameplay experience.";

    public RamBox(int boxWidth) {
        super("Allocate Ram", boxWidth, boxHeight);

        this.contentWidth = boxWidth - 2*sidePadding;
        this.content.setPrefWidth(this.contentWidth);
        this.content.setPrefHeight(boxHeight - 20);
        this.content.setPadding(new Insets(10, 0, 10, 0));

        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding);
        this.content.setLayoutY(30);

        this.content.setVgap(10);
        this.content.setHgap(30);

        this.fillBox();

    }
    
    @Override
    protected void fillBox() {
        this.content.add(this.getSwitchLine("Automatic", 2), 0, 0);
        this.content.add(JFXUtils.loadSlider(20, this.contentWidth/2 - sidePadding, 12, sliderBackgroundColorPreThumb, 
        sliderBackgroundColorPostThumb, sliderThumbColor, "Ram allocate", lineTextSize, lineTextColor), 0, 1);
        this.content.add(JFXUtils.loadTextBox(ramDefinition, textSizeZoneText, this.contentWidth/2 - sidePadding, 100, 
            backgroundColorZoneText,true ,false, false), 1, 0, 1, 2);
    }
    



}
