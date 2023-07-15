package com.launcher.ui.settings.buttons;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.GlobalSettings;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
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

    boolean isSelected = false;
    final FrameButtonList list;
    
    /**
     * Constructor for interactive button like Game, Launcher, ...
     * @param text Text : Text for the button
     * @param imagePath String : Path for the image
     * @param pictureColor String : Color for the image
     */
    public FrameButton(String text, String imagePath, boolean isNavMenu, FrameButtonList list) {
        super(text, 21, imagePath, pictureColor, 50, 15, "#ffffff", "light", "#00000000", false);
        this.buttonWidth = 220;
        this.imageV = JFXUtils.loadImageView(imagePath, 250, (int)(textSize * 1.3), pictureColor);
        this.list = list;

        this.setClick();
    }

    @Override
    protected void setTextPosition(Text textPlay) {
        AnchorPane.setLeftAnchor(textPlay, this.imageV.getFitWidth() + this.marginSide + 15);
    }

    /**
     * Create the point animation
     * @return FillTransition : the point with the animation
     */
    private FillTransition pointAnimation(){
        final int pointRadius = 4;
        Circle circle = new Circle(pointRadius);
        circle.setFill(this.backgroundColor);

        this.button.getChildren().add(circle);
        
        final Text textWidth = JFXUtils.loadText(text, 1, backgroundColor, this.fontWeight);
        final double width = textWidth.getLayoutBounds().getWidth();

        AnchorPane.setRightAnchor(circle, (double)width + pointRadius);
        AnchorPane.setTopAnchor(circle, (double)(this.buttonHeight / 2 - pointRadius));

        FillTransition pointTransition = new FillTransition(Duration.seconds(0.3), circle);
        pointTransition.setCycleCount(1);

        return pointTransition;
    }

    protected void setClick(){
        this.button.setOnMouseClicked(e -> {
            GlobalSettings.setLauncherFrame(this.text);
            FileLocation.playSound("sounds/click_btn");
            
            /* Unselect all buttons */
            for(FrameButton btn : this.list.buttonList) btn.isSelected = false;

            isSelected = true;
            ParallelTransition transition = new ParallelTransition();
            FillTransition pointTransition = this.pointAnimation();
            transition.getChildren().addAll(pointTransition);

            if(isSelected) {
                pointTransition.setFromValue(this.backgroundColor);
                pointTransition.setToValue(pointColor);
            } else {
                pointTransition.setFromValue(pointColor);
                pointTransition.setToValue(this.backgroundColor);
            }
            transition.playFromStart();
        });
    }

    @Override
    protected void setAnimation(Rectangle background) {
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.3), background);
        fillTransition.setCycleCount(1);

        ParallelTransition transition = new ParallelTransition();
        transition.getChildren().addAll(fillTransition);

        this.button.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn");
            fillTransition.setFromValue(this.backgroundColor);
            fillTransition.setToValue(animationColor);
            transition.playFromStart();
        });

        this.button.setOnMouseExited(e -> {
            fillTransition.setFromValue(animationColor);
            fillTransition.setToValue(this.backgroundColor);
            transition.playFromStart();
        });
        
        this.pointAnimation();
    }
}
