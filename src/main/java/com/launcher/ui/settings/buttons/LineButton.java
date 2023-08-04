package com.launcher.ui.settings.buttons;

import com.launcher.ui.settings.boxes.TextFieldElement;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LineButton extends NavigateButton {

    final Color animationColor = Color.web("#282a2c");
    final static double marginSide = 20;

    /**
     * Button to past in a settings line
     * @param text : text in the button
     * @param imagePath : path of the image
     * @param pictureColor : color of the image
     */
    public LineButton(String text, String imagePath, String pictureColor) {
        super(text, TextFieldElement.lineTextSize, imagePath, pictureColor, TextFieldElement.lineHeight,
        marginSide, TextFieldElement.lineTextColor.toString().replace("0x", "#"), "regular", "#181a1c", false);
        this.buttonWidth = (int) (text.length() * TextFieldElement.lineTextSize / 1.7 + 20 + 2 * marginSide); // 20 : size of the image
        this.buttonRadius = 15;
    }

    @Override protected void setTextPosition(Text textPlay) { AnchorPane.setLeftAnchor(textPlay, marginSide + 25); }

    /**
     * Function to set the fill animation of the button
     * @param background Rectangle : Background of the button
     * @return AnchorPane : Button with animation
     */
    @Override
    protected void setAnimation(Rectangle background) {
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.3), background);
        fillTransition.setCycleCount(1);

        this.button.setOnMouseEntered(e -> {
            fillTransition.setFromValue(this.backgroundColor);
            fillTransition.setToValue(this.animationColor);
            fillTransition.playFromStart();
            FileLocation.playSound("sounds/hover_btn", 0);
        });
    
        this.button.setOnMouseExited(e -> {
            fillTransition.setFromValue(this.animationColor);
            fillTransition.setToValue(backgroundColor);
            fillTransition.playFromStart();
        });
    }
}
