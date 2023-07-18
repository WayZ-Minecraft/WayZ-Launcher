package com.launcher.ui.settings.boxes;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.buttons.LineButton;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Class to create a box of settings like the box of the launcher settings, the box of the ram settings, ...
 */
public abstract class TextFieldElement {
    
    protected final AnchorPane box = new AnchorPane();
    protected int boxWidth;
    protected int boxHeight;
    protected static final String boxColor = "#0c0d0e";
    protected static final int sidePadding = 20;

    protected Text title;
    protected final static int titleSize = 15;
    protected final static String titleColor = "#ffffff";

    public final static int lineHeight = 30;
    public final static int lineTextSize = 16;
    public final static Color lineTextColor = Color.web("#ffffff");

    protected final static int switchWidth = 30;
    protected final static int switchHeight = 16;
    protected final static Color switchColorNotCheck = Color.web("#f2f2f2");
    protected final static Color switchColorCheck = Color.web("#82a77d");
    protected final static Color switchColorBackground = Color.web("#181a1c");

    protected final static Color backgroundColorZoneText = Color.web("#181a1c");
    protected final static int textSizeZoneText = 14;

    protected final static Color sliderBackgroundColorPreThumb = Color.web("#82ca9c");
    protected final static Color sliderBackgroundColorPostThumb = Color.web("#181a1c");
    protected final static Color sliderThumbColor = Color.web("#f2f2f2");
    
    /**
     * Class to create a box of settings like the box of the launcher settings, the box of the ram settings, ...
     * @param title : the title of the box
     * @param boxWidth : the width of the box
     * @param boxHeight : the height of the box
     * @return a Settings box with the title
     */
    public TextFieldElement(String title, int boxWidth, int boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.title = JFXUtils.loadText(title + " :", titleSize, titleColor, "light");
        
        this.box.setPrefWidth(this.boxWidth);
        this.box.setPrefHeight(this.boxHeight);
        this.box.setStyle("-fx-background-color: " + boxColor + ";");
        
        Rectangle background = new Rectangle(boxWidth, boxHeight);
        background.setArcHeight(36);
        background.setArcWidth(36);
        this.box.setClip(background);
        
        this.box.getChildren().add(this.title);
        this.title.setLayoutX(sidePadding);
        this.title.setLayoutY(25);
    }

    /**
     * Function to fill the box with settings line, like switch, slider, button and his label
     * <p></p>
     * <b>Exemple for override:</b> To add a switch line, you can use the function getSwitchLine(String Setting, int columnNumber)
     * 
     */
    protected abstract void fillBox();
    
    /** Function to get the box
     * @return AnchorPane : the box
     */
    public AnchorPane getBox(){
        return this.box;
    };

    /** Function to get a line of settings box
     * @param Setting : the name of the setting (ex: "Keep launcher open")
     * @param settingBox : the box to manage the setting (ex: "checkbox", "slider", "button")
     * @param columnNumber : the number of column in the row to manage Bow width
     * @return AnchorPane : a line of settings box
     */
    private AnchorPane getSettingsLine(String Setting, Pane settingBox, int columnNumber){

        AnchorPane line = new AnchorPane();
        line.setPrefWidth((this.boxWidth - 2*sidePadding) / columnNumber);
        line.setPrefHeight(lineHeight);

        Text settingName = JFXUtils.loadText(Setting, lineTextSize, lineTextColor, "regular");
        line.getChildren().add(settingName);
        AnchorPane.setLeftAnchor(settingName, 0.0);
        AnchorPane.setTopAnchor(settingName, lineHeight/2 - settingName.getLayoutBounds().getHeight()/2);

        line.getChildren().add(settingBox);
        AnchorPane.setRightAnchor(settingBox, 75.0 / columnNumber - settingBox.getPrefWidth()/2);
        AnchorPane.setTopAnchor(settingBox, lineHeight/2 - settingBox.getPrefHeight()/2);
        
        return line;
    }

    /**
     *  Function to get a line of settings box
     * @param Setting : the name of the setting (ex: "Keep launcher open")
     * @param settingBox : the box to manage the setting (ex: "checkbox", "slider", "button")
     * @return 
     */
    private AnchorPane getSettingsLine(String Setting, Pane settingBox){
        return this.getSettingsLine(Setting, settingBox, 1);
    }

    /**
     * Function to get a line of settings box with a switch button
     * @param Setting : the name of the setting (ex: "Keep launcher open")
     * @param columnNumber : the number of column in the row to manage Bow width
     * @return Pane : a line of settings box with a switch button
     */
    protected Pane getSwitchLine(String Setting, int columnNumber) {
        final Pane boxCheck = JFXUtils.loadSwitchButton(switchWidth, switchHeight, switchColorBackground, switchColorCheck, switchColorNotCheck);
        return this.getSettingsLine(Setting, boxCheck, columnNumber);
    }

    protected Pane getSwitchLine(String Setting) {
        return this.getSwitchLine(Setting, 1);
    }

    /**
     * Function to get a line of settings box with a slider
     * @param setting : the name of the setting (ex: "Reset File")
     * @param buttonText : the text of the button (ex: "Reset")
     * @param imagePath : the path of the icon of the button (
     * @param pictureColor : the color of the icon of the button
     * @return Pane : a line of settings box with a slider
     */
    protected Pane getButtonLine(String setting, String buttonText, String imagePath, String pictureColor) {
        LineButton button = new LineButton(buttonText, imagePath, pictureColor);
        final Pane boxButton = button.loadNavigateButton();
        return this.getSettingsLine(setting, boxButton);
    }
}
