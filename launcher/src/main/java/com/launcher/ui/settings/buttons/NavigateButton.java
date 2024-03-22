package com.launcher.ui.settings.buttons;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.JFXUtils.RunnableTask;
import com.photon.util.TranslationManager;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

abstract class NavigateButton {

    AnchorPane button = new AnchorPane();
    
    String text;
    int textSize;
    String textColor;
    String fontWeight;

    ImageView imageV;
    String pictureColor;
    
    int buttonWidth;
    int buttonHeight;
    int buttonRadius = 36;
    double marginSide;
    Color backgroundColor;

    boolean isNavMenu;
    Text textPlay;
    
    /**
     * Constructor for interactive button like Back, Save, Launcher, Game, ...
     * @param text Text : Text for the button
     * @param textSize int : Width of a character
     * @param imagePath String : Path for the image
     * @param pictureColor String : Color for the image
     * @param buttonHeight int : Height of the button
     * @param marginSide double : Margin on the side of the button
     * @param textColor String : Color of the text
     * @param fontWeight String : Weight of the text
     */
    protected NavigateButton(String text, int textSize, String imagePath, String pictureColor, int buttonHeight, double marginSide, String textColor, String fontWeight, String backgroundColor, boolean isNavMenu) {
        this.text = text;
        this.textSize = textSize;
        this.imageV = JFXUtils.loadImageView(imagePath, 250, textSize, pictureColor);
        this.pictureColor = pictureColor;
        this.buttonHeight = buttonHeight;
        this.marginSide = marginSide;
        this.textColor = textColor;
        this.fontWeight = fontWeight;
        this.backgroundColor = Color.web(backgroundColor);
        this.textPlay = JFXUtils.loadText(isNavMenu ? TranslationManager.format("btn.settings."+this.text.toLowerCase()) : this.text, this.textSize, this.textColor, this.fontWeight);
        this.isNavMenu = isNavMenu;
    }
    
    public void setIcon(String imagePath) {
        this.imageV = JFXUtils.loadImageView(imagePath, 250, this.textSize, this.pictureColor);
        this.button.getChildren().clear();
        this.loadNavigateButton();
    }

    /**
     * Set the position of the text
     * @param textPlay Text : Text to set
     * <p></p>
     * <b>Override Exemple : AnchorPane.setRightAnchor(0)</b>
     */
    protected abstract void setTextPosition(Text textPlay);

    /**
     * Set the animation of the background
     * @param background Rectangle : Background of the button
     * <p></p>
     * <b>Ovveride Exemple : FillTransition pointTransition = new FillTransition(Duration.seconds(0.3), background);</b>
     */
    protected abstract void setAnimation(Rectangle background);

    /**
     * Load the button with the animation
     * @return AnchorPane : Button with animation
     */
    public AnchorPane loadNavigateButton() {
        this.button.setPrefSize(buttonWidth, buttonHeight);
        
        Rectangle background = new Rectangle(buttonWidth, buttonHeight);
        background.setFill(backgroundColor);
        background.setArcHeight(buttonRadius);
        background.setArcWidth(buttonRadius);
        
        this.button.getChildren().add(background);
        
        this.button.getChildren().addAll(this.imageV, textPlay);
        AnchorPane.setLeftAnchor(this.imageV, this.marginSide);
        AnchorPane.setTopAnchor(this.imageV, this.buttonHeight / 2 - this.imageV.getLayoutBounds().getHeight() / 2);
        
        this.setTextPosition(textPlay);
        AnchorPane.setTopAnchor(textPlay, this.buttonHeight / 2 - textPlay.getLayoutBounds().getHeight() / 2);
        
        this.setAnimation(background);
        return this.button;
    }
    
    public void setAction(RunnableTask<AnchorPane, javafx.scene.input.MouseEvent> action) {
        this.button.setOnMouseClicked(event -> action.run(button, event));
    }
}


