package com.launcher.ui.settings.boxes.game;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.TextFieldElement;
import com.photon.util.TranslationManager;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * RamBox
 * The ram box in the game settings
 */
public class RamBox extends TextFieldElement{
    
    final static String title = TranslationManager.format("settings.game.ram.title");
    final GridPane content = new GridPane();
    int contentWidth;
    final static int boxHeight = 170;
    
    final String ramDefinition = TranslationManager.format("settings.game.ram.def");

    public RamBox(int boxWidth) {
        super(title, boxWidth, boxHeight);


        Text ramAllocable = getNumberRamAllocable(16);
        this.box.getChildren().add(ramAllocable);
        AnchorPane.setRightAnchor(ramAllocable, (double)sidePadding);
        ramAllocable.setLayoutY(25);



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

    private Text getNumberRamAllocable(int ramAllocable) {
        return JFXUtils.loadText(TranslationManager.format("settings.game.ram.allocate") + " : " + ramAllocable + " GB", titleSize - 3, lineTextColor, "light");
    }
    
    @Override
    protected void fillBox() {
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.game.ram.automatic"), 2), 0, 0);
        this.content.add(JFXUtils.loadSlider(20, this.contentWidth/2 - sidePadding, 12, sliderBackgroundColorPreThumb, 
        sliderBackgroundColorPostThumb, sliderThumbColor, TranslationManager.format("settings.game.ram.slider"), lineTextSize, lineTextColor), 0, 1);
        this.content.add(JFXUtils.loadTextBox(ramDefinition, textSizeZoneText, this.contentWidth/2 - sidePadding, 100, 
            backgroundColorZoneText,true ,false, false), 1, 0, 1, 2);
    }
    



}
