package com.launcher.ui.settings.boxes.game;

import java.lang.management.ManagementFactory;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.ui.settings.boxes.TextFieldElement;
import com.launcher.utils.LauncherConfig;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;
import com.sun.management.OperatingSystemMXBean;

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
        
        final OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        final int physicalMemorySize = (int)(os.getTotalMemorySize() / 1024 / 1024 / 1024)+1;
        Text ramAllocable = getNumberRamAllocable(physicalMemorySize);
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
    
    private Text getNumberRamAllocable(int ramAllocable) { return JFXUtils.loadText(TranslationManager.format("settings.game.ram.allocate") + " : " + ramAllocable + " GB", titleSize - 3, lineTextColor, "light"); }
    
    @Override
    protected void fillBox() {
        this.content.add(this.getSwitchLine(TranslationManager.format("settings.game.ram.automatic"), 2, LauncherConfig.getConfig().autoRAM, (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            LauncherConfig.getConfig().autoRAM = object;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        }), 0, 0);
        this.content.add(JFXUtils.loadSlider(20, this.contentWidth/2 - sidePadding, 12, sliderBackgroundColorPreThumb,
        sliderBackgroundColorPostThumb, sliderThumbColor, TranslationManager.format("settings.game.ram.slider"), lineTextSize, lineTextColor, (object, value) -> {
            LauncherConfig.getConfig().allocatedram = value;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        }), 0, 1);
        this.content.add(JFXUtils.loadTextBox(ramDefinition, textSizeZoneText, this.contentWidth/2 - sidePadding, 100,
            backgroundColorZoneText,true ,false, false, (o, e) -> {
                FileLocation.playSound("sounds/key_typing", 0);
                LauncherConfig.getConfig().allocatedram = Double.valueOf(o.getText());
                GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
            }), 1, 0, 1, 2);
    }
}
