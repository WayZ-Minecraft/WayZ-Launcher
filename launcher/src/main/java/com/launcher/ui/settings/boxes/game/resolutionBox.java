package com.launcher.ui.settings.boxes.game;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.ui.settings.boxes.SettingBox;
import com.launcher.utils.LauncherConfig;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Pair;

public class resolutionBox extends SettingBox{

    final static String title = TranslationManager.format("settings.game.resolution.title");
    final static int boxHeight = 105;
    TextField[] textFields;

    final static String maskedColor = "#ffff00";
    final GridPane content = new GridPane();

    public resolutionBox(int boxWidth) {
        super(title, boxWidth, boxHeight);
        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding);
        this.content.setLayoutY(50);
        this.content.setHgap(40);
        this.fillBox();
    }

    /**
     * load a cross
     * @param width : width of the cross
     * @param height : height of the cross
     * @return Pane : white cross
     */
    private Pane loadCross(int width,int height){
        Pane pane = new Pane();

        Line line1 = new Line(0,0,width,height);
        line1.setStrokeWidth(1);
        line1.setStyle("-fx-stroke: #ffffff");
        
        Line line2 = new Line(0,height,width,0);
        line2.setStrokeWidth(1);
        line2.setStyle("-fx-stroke: #ffffff");

        pane.getChildren().addAll(line1,line2);
        return pane;
    }

    /**
     * load the resolution custom zone (2 text field and a cross)
     * @param width : width of the resolution
     * @param height : height of the resolution
     * @param isActivated : true if the resolution is activated, false if not
     * @return Pane : resolution custom zone
     */
    private Pane loadResolutionCustom(int width, int height, boolean isActivated){
        Pane pane = new Pane();

        Pair<StackPane,TextField> widthResolution = JFXUtils.loadTextField(Integer.toString(width), lineTextSize, lineTextColor, 100, lineHeight, backgroundColorZoneText, (o, e) -> {
            FileLocation.playSound("sounds/key_typing", 0);
            LauncherConfig.getConfig().screenWidth = ((TextField)o).getText();
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        });
        
        Pair<StackPane,TextField> heightResolution = JFXUtils.loadTextField(Integer.toString(height), lineTextSize, lineTextColor, 100, lineHeight, backgroundColorZoneText, (o, e) -> {
            FileLocation.playSound("sounds/key_typing", 0);
            LauncherConfig.getConfig().screenHeight = ((TextField)o).getText();
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        });
        StackPane heightResolutionBox = heightResolution.getKey();
        this.textFields = new TextField[]{widthResolution.getValue(), heightResolution.getValue()};

        Pane cross = this.loadCross((int)(lineTextSize*0.8), (int)(lineTextSize * 0.8));

        pane.getChildren().addAll(widthResolution.getKey(), heightResolutionBox, cross);
        cross.setLayoutX(95);
        cross.setLayoutY((lineHeight - lineTextSize*0.8)/2);
        heightResolutionBox.setLayoutX(105);

        if (!isActivated) this.setMask(pane, false);
        return pane;
    }

    /**
     * set the mask of the box (work only in the resolution custom zone)
     * @param Box : box to mask
     * @param Activate : true to activate the mask, false to desactivate
     */
    private void setMask(Pane Box,boolean Activate) {
        for (TextField textField : this.textFields) textField.setDisable(!Activate);
    }


    /**
     * set the link between the checkboxs
     * @param checkBoxs : checkboxs to link
     */
    private void setupCheckBoxes(CheckBox[] checkBoxs) {
        for (CheckBox checkBox : checkBoxs) {
            checkBox.setOnMouseEntered(e -> {
                FileLocation.playSound("sounds/hover_btn", 0);
                checkBox.setCursor(Cursor.HAND);
            });
            checkBox.setOnAction(e -> {
                for (CheckBox checkBox2 : checkBoxs) checkBox2.setSelected(checkBox2 == checkBox);
            });
        }
    }

    @Override
    protected void fillBox() {
        CheckBox standard = new CheckBox("Standard");
        standard.setFont(JFXUtils.getFont("regular", lineTextSize));
        standard.setSelected(true);
        
        CheckBox custom = new CheckBox("Custom");
        custom.setFont(JFXUtils.getFont("regular", lineTextSize));

        setupCheckBoxes(new CheckBox[]{standard, custom});
        
        Pane resolutionCustom = this.loadResolutionCustom(Integer.valueOf(LauncherConfig.getConfig().screenWidth), Integer.valueOf(LauncherConfig.getConfig().screenHeight), false);
        this.setMask(resolutionCustom, LauncherConfig.getConfig().useCustomSize);
        if(LauncherConfig.getConfig().useCustomSize) {
            custom.setSelected(true);
            standard.setSelected(false);
        }
        custom.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.setMask(resolutionCustom, newValue);
            LauncherConfig.getConfig().useCustomSize = newValue;
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
        });

        this.content.add(standard, 0, 0);
        this.content.add(custom, 1, 0);
        custom.setTranslateX(20);
        this.content.add(resolutionCustom, 2, 0);
    }
}
