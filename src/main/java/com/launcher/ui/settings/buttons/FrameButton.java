package com.launcher.ui.settings.buttons;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.FrameName;
import com.launcher.ui.settings.GlobalSettings;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class FrameButton extends NavigateButton {
    
    final static Color animationColor = Color.web("#ffffff08");
    final static Color pointColor = Color.web("#ffffff");
    final static String pictureColor = "#ffffff";
    final static Color backgroundColor = Color.web("#00000000");

    FrameName frameName;
    final FillTransition pointTransition = this.pointAnimation();

    /**
     * Constructor for interactive button like Game, Launcher, ...
     * @param text Text : Text for the button
     * @param imagePath String : Path for the image
     */
    public FrameButton(String text, FrameName frameName ,String imagePath, boolean isNavMenu, FrameButtonList buttonList, int index) {
        super(text, 21, imagePath, pictureColor, 50, 15, "#ffffff", "light", backgroundColor.toString().replace("0x", "#"), false);
        this.buttonWidth = 220;
        this.imageV = JFXUtils.loadImageView(imagePath, 250, (int)(textSize * 1.3), pictureColor);
        this.frameName = frameName;

        this.setClick(buttonList, index);
    }

    @Override
    protected void setTextPosition(Text textPlay) {
        AnchorPane.setLeftAnchor(textPlay, this.imageV.getFitWidth() + this.marginSide + 15);
    }

    /**
     * Create the point animation
     * @return FillTransition : The FillTransition with the point
     */
    protected FillTransition pointAnimation(){
        final int pointRadius = 4;
        Circle circle = new Circle(pointRadius);
        circle.setFill(backgroundColor);

        this.button.getChildren().add(circle);

        AnchorPane.setRightAnchor(circle, 5.0 + pointRadius);
        AnchorPane.setTopAnchor(circle, (double)(this.buttonHeight / 2 - pointRadius));

        FillTransition pointTransition = new FillTransition(Duration.seconds(0.3), circle);
        pointTransition.setCycleCount(1);

        return pointTransition;
    }

    /**
     * Set the click event for the button
     * @param buttonList FrameButtonList : The button list
     * @param index int : The index of the button
     */
    protected void setClick(FrameButtonList buttonList, int index){
        this.button.setOnMouseClicked(e -> {
            GlobalSettings.setLauncherFrame(this.frameName);
            FileLocation.playSound("sounds/click_btn", 0);
            buttonList.setSelectedButton(index);
        });
    }

    @Override
    protected void setAnimation(Rectangle background) {
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.3), background);
        fillTransition.setCycleCount(1);

        this.button.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn", 0);
            fillTransition.setFromValue(backgroundColor);
            fillTransition.setToValue(animationColor);
            fillTransition.playFromStart();
        });

        this.button.setOnMouseExited(e -> {
            fillTransition.setFromValue(animationColor);
            fillTransition.setToValue(backgroundColor);
            fillTransition.playFromStart();
        });
        
        this.pointAnimation();
    }
}
