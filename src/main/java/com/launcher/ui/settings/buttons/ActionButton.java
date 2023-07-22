package com.launcher.ui.settings.buttons;

import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ActionButton extends NavigateButton {

    Color animationColor = Color.web("#1a1b1c");

    /**
     * Constructor for interactive button like Back, Save, Reset, ...
     * @param text Text : Text for the button
     * @param imagePath String : Path for the image
     * @param pictureColor String : Color for the image
     */
    public ActionButton(String text, String imagePath, String pictureColor) {
        super(text, 17, imagePath, pictureColor, 50, 30, "#ffffff", "regular", "#0c0d0e", false);
        this.buttonWidth = (int) (text.length() * textSize / 2 + 2 * textSize + 2 * marginSide);
    }

    @Override
    protected void setTextPosition(Text textPlay) { AnchorPane.setRightAnchor(textPlay, marginSide); }

    public void setClick(Runnable action) {
        this.button.setOnMouseClicked(e -> {
            action.run();
            FileLocation.playSound("sounds/click_btn", 0);
        });
    }


    /**
     * 
     * @param background Rectangle : Background of the button
     * @return AnchorPane : Button with animation
     */
    @Override
    protected void setAnimation(Rectangle background) {
    
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.3), background);
        fillTransition.setCycleCount(1);

        this.button.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn", 0);
            fillTransition.setFromValue(this.backgroundColor);
            fillTransition.setToValue(this.animationColor);
            fillTransition.playFromStart();
        });
    
        this.button.setOnMouseExited(e -> {
            fillTransition.setFromValue(this.animationColor);
            fillTransition.setToValue(this.backgroundColor);
            fillTransition.playFromStart();
        });
        
    }


}