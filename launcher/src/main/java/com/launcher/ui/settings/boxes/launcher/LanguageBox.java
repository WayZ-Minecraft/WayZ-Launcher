package com.launcher.ui.settings.boxes.launcher;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.FrameName;
import com.launcher.ui.settings.GlobalSettings;
import com.launcher.ui.settings.boxes.SettingBox;
import com.launcher.utils.LauncherConfig;
import com.photon.util.TranslationManager;
import com.photon.util.os.FileLocation;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
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
public class LanguageBox extends SettingBox {

    final static String title = TranslationManager.format("settings.launcher.language.title");
    final GridPane content = new GridPane();
    StackPane[] languages = new StackPane[5];
    FillTransition[] transitions = new FillTransition[5];
    int selectedLanguage = 0;
    String[] language = new String[5];
    final static int boxHeight = 150;

    final static Color basicColor = Color.web("#00000000");
    final static Color focusColor = Color.web("#4b4b4b");
    final static Color selectedColor = Color.web("#2f2f2f");


    public LanguageBox(int boxWidth) {
        super(title, boxWidth, boxHeight);

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
    private StackPane getLanguage(String language, String imagePath, int index){
        this.language[index] = language;

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

        this.setFocus(languageBox, background, index);
        switch(LauncherConfig.getConfig().language) {
        case "en":
            this.selectedLanguage = 1;
            break;
        case "fr":
            this.selectedLanguage = 2;
            break;
        case "de":
            this.selectedLanguage = 3;
            break;
        case "ru":
            this.selectedLanguage =4;
            break;
        }
        if(LauncherConfig.getConfig().systemLang) this.selectedLanguage = 0;
        if (this.selectedLanguage == index) this.setSelected(languageBox, background, index);

        return languageBox;
    }

    /**
     * Change the background color of the language box when the mouse is on it (hover)
     * @param languageBox : the language box
     * @param background : the background of the language box
     * @param UnActiveColor : the color of the background when the mouse is not on it
     * @param ActiveColor : the color of the background when the mouse is on it
     * @param index : the index of the language box (this.languages)
     */
    private void setHover(StackPane languageBox, Rectangle background, Color UnActiveColor, Color ActiveColor, int index){
        FillTransition fillTransition = this.transitions[index];
        languageBox.setOnMouseEntered(e -> {
            FileLocation.playSound("sounds/hover_btn", 0);
            fillTransition.setFromValue(UnActiveColor);
            fillTransition.setToValue(ActiveColor);
            fillTransition.playFromStart();
            languageBox.setCursor(Cursor.HAND);
        });
    
        languageBox.setOnMouseExited(e -> {
            fillTransition.setFromValue(ActiveColor);
            fillTransition.setToValue(UnActiveColor);
            fillTransition.playFromStart();
        });
    }

    /**
     * Set the focus animation on the language box
     * @param languageBox : the language box
     * @param background : the background of the language box
     */
    private void setFocus(StackPane languageBox, Rectangle background, int index) {
        this.transitions[index] = new FillTransition(Duration.seconds(0.3), background);
        this.transitions[index].setCycleCount(1);

        this.setHover(languageBox, background, basicColor, focusColor, index);

        languageBox.setOnMouseClicked(e -> {
            setUnselected(this.languages[this.selectedLanguage], background, this.selectedLanguage);
            setSelected(languageBox, background, index);
            for(int i = 0; i < this.languages.length; i++) {
                if(this.languages[i] == languageBox) {
                    this.selectedLanguage = i;
                }
            }
            switch(this.language[this.selectedLanguage]) {
            case "System":
                LauncherConfig.getConfig().systemLang = true;
                LauncherConfig.getConfig().language = TranslationManager.getSystem();
                break;
            case "English":
                LauncherConfig.getConfig().systemLang = false;
                LauncherConfig.getConfig().language = TranslationManager.locale_en.getLanguage();
                break;
            case "French":
                LauncherConfig.getConfig().systemLang = false;
                LauncherConfig.getConfig().language = TranslationManager.locale_fr.getLanguage();
                break;
            case "Deutsch":
                LauncherConfig.getConfig().systemLang = false;
                LauncherConfig.getConfig().language = TranslationManager.locale_de.getLanguage();
                break;
            case "Russia":
                LauncherConfig.getConfig().systemLang = false;
                LauncherConfig.getConfig().language = TranslationManager.locale_ru.getLanguage();
                break;
            }
            GlobalSettings.saveButton.setIcon("logos/"+(!LauncherConfig.isSaved()?"not_":"")+"saved.png");
            GlobalSettings.setLauncherFrame(FrameName.LAUNCHER);
            FileLocation.playSound("sounds/click_btn", 0);
        });

    }

    /**
     * Set the selected animation on the language box, to show that it is the selected language
     * @param languageBox : the language box
     * @param background : the background of the language box
     * @param index : the index of the language box (this.languages)
     */
    private void setSelected(StackPane languageBox, Rectangle background, int index) {
        this.setHover(languageBox, background, selectedColor, focusColor, index);

        if (background.getFill().toString().equals(basicColor.toString())){ // if the language box is not hovered (ex: on launch)
            background.setFill(selectedColor);
            return;
        };


        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0.2), new KeyValue(background.fillProperty(), selectedColor)),
            new KeyFrame(Duration.seconds(0.4), new KeyValue(background.fillProperty(), focusColor))
        );
        timeline.setCycleCount(1);

        timeline.playFromStart();


    }

    /**
     * Set the basic animation on the language box, to undo the selected animation
     * @param languageBox : the language box
     * @param background : the background of the language box
     * @param index : the index of the language box (this.languages)
     */
    private void setUnselected(StackPane languageBox, Rectangle background, int index) {
        this.setHover(languageBox, background, basicColor, focusColor, index);
        
        FillTransition fillTransition = this.transitions[index];
        fillTransition.setFromValue(selectedColor);
        fillTransition.setToValue(basicColor);
        fillTransition.playFromStart();
    }
    
    @Override
    protected void fillBox() {
        StackPane system = getLanguage("System", "logos/language/system.png", 0);
        StackPane english = getLanguage("English", "logos/language/united-kingdom.png", 1);
        StackPane french = getLanguage("French", "logos/language/france.png", 2);
        StackPane german = getLanguage("Deutsch", "logos/language/germany.png", 3);
        StackPane russia = getLanguage("Russia", "logos/language/russia.png", 4);

        this.languages = new StackPane[] {system, english, french, german, russia};

        content.add(this.languages[0], 0, 0);
        content.add(this.languages[1], 1, 0);
        content.add(this.languages[2], 2, 0);
        content.add(this.languages[3], 3, 0);
        content.add(this.languages[4], 4, 0);
    }
}
