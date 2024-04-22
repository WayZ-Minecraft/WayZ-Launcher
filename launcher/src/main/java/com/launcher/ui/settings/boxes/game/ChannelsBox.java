package com.launcher.ui.settings.boxes.game;

import java.util.ArrayList;
import java.util.List;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.ui.settings.boxes.SettingBox;
import com.launcher.utils.LauncherConfig;
import com.photon.informations.PhotonUpdaterManager.UpdateChannel;
import com.photon.network.NetworkDirectories;
import com.photon.util.ProtectorManager;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ChannelsBox extends SettingBox {

    final static String title = TranslationManager.format("settings.game.channels.title");
    final static int boxHeight = 220;

    final GridPane content = new GridPane();
    int contentWidth = boxWidth - 4*sidePadding;

    final static String textKey = TranslationManager.format("settings.game.channels.activation_key");
    final static int contentHeight = 60;
    final static int iconSize = 20;
    final static Color iconBackgroundColor = Color.web("#8b2628");

    private Pane activationPane;
    private List<Pane> childsToHideAndShow = new ArrayList<>();

    public ChannelsBox(int boxWidth) {
        super(title, boxWidth, boxHeight);

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
    
    private Pane getKeyInfoPane(){
        Pane infoText = JFXUtils.loadTextBox(LauncherConfig.getConfig().vmarguments, textSizeZoneText, contentWidth, contentHeight, backgroundColorZoneText, false, true, true, (o, e) -> {
            FileLocation.playSound("sounds/key_typing", 0);
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
            String key = ProtectorManager.hash(((TextArea)o).getText());
            if(ProtectorManager.hash(NetworkDirectories.config.channelsActiavtionKey).equals(key)) {
                LauncherConfig.getConfig().activationKey = ProtectorManager.hash(NetworkDirectories.config.channelsActiavtionKey);
                LauncherConfig.saveConfig();
                activationPane.setVisible(false);
                for(Pane pane : this.childsToHideAndShow) pane.setVisible(true);
            }
        });
        TextArea textArea = (TextArea)infoText.getChildren().get(1);
        textArea.setPromptText(textKey);
        return infoText;
    }

    private Pane getChannelInfoPane(String text, UpdateChannel channel, boolean isModChannel) {
        Pane boxPane = new Pane();
        ComboBox<UpdateChannel> combo = new ComboBox<>();
        combo.getItems().addAll(UpdateChannel.values());
        combo.setValue(channel);
        combo.setOnAction((event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            if(isModChannel)
                LauncherConfig.getConfig().modChannel = combo.getValue();
            else
                LauncherConfig.getConfig().apiChannel = combo.getValue();
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        });
        boxPane.getChildren().add(combo);
        return this.getSettingsLine(text, boxPane, 1);
    }

    private Pane getDeletePacksInfoPane() {
        return this.getSwitchLine(TranslationManager.format("settings.game.channels.delete_packs"), LauncherConfig.getConfig().preventContentPacksDownloading, (object, event) -> {
            FileLocation.playSound("sounds/click_btn", 0);
            LauncherConfig.getConfig().preventContentPacksDownloading = object;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        });
    }

    @Override
    protected void fillBox() {
        this.content.add(activationPane = this.getKeyInfoPane(), 0, 0);
        activationPane.setVisible(!LauncherConfig.getConfig().hasActivationKey());

        add(this.getDeletePacksInfoPane(), 0, 0);
        add(this.getChannelInfoPane(TranslationManager.format("settings.game.channels.mod"), LauncherConfig.getConfig().modChannel, true), 0, 1);
        add(this.getChannelInfoPane(TranslationManager.format("settings.game.channels.api"), LauncherConfig.getConfig().apiChannel, false), 0, 2);

        for(Pane pane : this.childsToHideAndShow)
            pane.setVisible(LauncherConfig.getConfig().hasActivationKey());
    }

    private void add(Pane pane, int r, int c) {
        this.content.add(pane, r, c);
        this.childsToHideAndShow.add(pane);
    }
}
