package com.launcher.ui.settings.boxes.launcher;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.TextFieldElement;
import com.launcher.utils.LauncherConfig;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * LanguageBox
 * The language box in the launcher settings
 */
public class LanguageBox extends TextFieldElement {

    final boolean isSelected = false;
    final GridPane content = new GridPane();
    final static int boxHeight = 150;

    final static Color basicColor = Color.web("#00000000");
    final static Color focusColor = Color.web("#4b4b4b");


    public LanguageBox(int boxWidth) {
        super(TranslationManager.format("title.languages"), boxWidth, boxHeight);

        this.content.setPrefWidth(boxWidth - 2*sidePadding);
        this.content.setPrefHeight(boxHeight - 20);
        this.content.setPadding(new Insets(10, 0, 10, 0));

        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding);
        this.content.setLayoutY(30);

        this.content.setHgap(15);
        this.content.setVgap(1);

        this.fillBox();
    }

    /**
     * Load a language box with the language name and the flag
     * @param language : the language name
     * @param imagePath : the path to the flag image
     * @return StackPane : the language box
     */
    private StackPane getLanguage(String language, String imagePath){
        StackPane languageBox = new StackPane();
        languageBox.setMinWidth(80);
        languageBox.setMinHeight(80);

        Rectangle background = JFXUtils.loadBackground(80, 80, basicColor, 20);
        languageBox.getChildren().add(background);

        ImageView languageImage = JFXUtils.loadImageView(imagePath, 100, 40);
        languageBox.getChildren().add(languageImage);
        languageImage.setTranslateY(-10);
        
        Text languageText = JFXUtils.loadText(language, 14, "#ffffff", "regular");
        languageBox.getChildren().add(languageText);
        languageText.setTranslateY(25);

        languageBox.setOnMouseClicked(e -> {
            switch(language) {
            case "System":
                LauncherConfig.getConfig().language = TranslationManager.getSystem();
                break;
            case "English":
                LauncherConfig.getConfig().language = TranslationManager.locale_en.getLanguage();
                break;
            case "French":
                LauncherConfig.getConfig().language = TranslationManager.locale_fr.getLanguage();
                break;
            case "Deutsch":
                LauncherConfig.getConfig().language = TranslationManager.locale_de.getLanguage();
                break;
            case "Russia":
                LauncherConfig.getConfig().language = TranslationManager.locale_ru.getLanguage();
                break;
            }
            FileLocation.playSound("sounds/click_btn");
        });

        this.setFocus(languageBox, background);

        return languageBox;
    }

    /**
     * Set the focus animation on the language box
     * @param languageBox : the language box
     * @param background : the background of the language box
     */
    private void setFocus(StackPane languageBox, Rectangle background) {
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.3), background);
        fillTransition.setCycleCount(1);
    
        languageBox.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn");
            fillTransition.setFromValue(basicColor);
            fillTransition.setToValue(focusColor);
            fillTransition.playFromStart();
        });
    
        languageBox.setOnMouseExited(e -> {
            fillTransition.setFromValue(focusColor);
            fillTransition.setToValue(basicColor);
            fillTransition.playFromStart();
        });
    }
    
    @Override
    protected void fillBox() {
        StackPane system = getLanguage("System", "logos/language/system.png");
        StackPane english = getLanguage("English", "logos/language/united-kingdom.png");
        StackPane french = getLanguage("French", "logos/language/france.png");
        StackPane german = getLanguage("Deutsch", "logos/language/germany.png");
        StackPane russia = getLanguage("Russia", "logos/language/russia.png");

        content.add(system, 0, 0);
        content.add(english, 1, 0);
        content.add(french, 2, 0);
        content.add(german, 3, 0);
        content.add(russia, 4, 0);
    }
}
