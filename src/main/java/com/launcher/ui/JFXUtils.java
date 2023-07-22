package com.launcher.ui;

import java.io.InputStream;

import com.photon.util.ConsoleManager;
import com.photon.util.ConsoleManager.EnumLogType;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

public class JFXUtils {

    /**
     * to get a custom font
     * @param fontWeight : Weight of the font (light, bold, regular)
     * @param size : Size of the font
     * @return Font : The custom font
     */
    public static Font getFont(String fontWeight, int size){
        InputStream fontStream; 
        switch (fontWeight) {
            case "light":
                fontStream = JFXUtils.class.getResourceAsStream("/font/WorkSans-Light.ttf");
                break;
            case "bold":
                fontStream = JFXUtils.class.getResourceAsStream("/font/WorkSans-Bold.ttf");
                break;
            default:
                fontStream = JFXUtils.class.getResourceAsStream("/font/WorkSans-Regular.ttf");
                break;
        }

        return Font.loadFont(fontStream, size);
    }
    
    /**
     * Function to create a custom text
     * @param text : Text to display
     * @param size : Size of the text
     * @param color : Color of the text
     * @param fontWeight : Weight of the text (light, bold, regular)
     * @return Text : Text with the custom font
     */
    public static Text loadText(String text, int size, String color, String fontWeight){
        final Text textPlay = new Text(text);

        final Font customFont = getFont(fontWeight, size);
        textPlay.setFont(customFont);
        textPlay.setStyle("-fx-fill: " + color + ";");
        return textPlay;
    }

    public static Text loadText(String text, int size, Color color, String fontWeight){
        return loadText(text, size, color.toString().replace("0x", "#"), fontWeight);
    }

    /**
     * Function to create a custom textArea in a box
     * @param text : Text to display
     * @param textSize : Size of the text
     * @param with : Width of the box
     * @param height : Height of the box
     * @param backgroundColor : Color of the box
     * @return Pane : The box with the text
     */
    public static Pane loadTextBox(String text, int textSize, int with, int height, Color backgroundColor, boolean justify ,boolean editable, boolean mouseSelection, RunnableTask<TextArea, ? super KeyEvent> action){
        Pane textBox = new Pane();
        textBox.setPrefWidth(with);
        textBox.setPrefHeight(height);

        Rectangle background = JFXUtils.loadBackground(with, height, backgroundColor, 20);
        textBox.getChildren().add(background);

        TextArea textArea = new TextArea(text);
        textArea.setFont(JFXUtils.getFont("light", textSize));
        if (!mouseSelection){
            textArea.setTextFormatter(new TextFormatter<String>(change ->  {
                change.setAnchor(change.getCaretPosition());
                return change;
            }));
        }
        if (!editable) textArea.setEditable(false);
        textArea.setOnKeyTyped(event -> action.run(textArea, event));
        
        textArea.setPrefWidth(with - 20);
        textArea.setPrefHeight(height - 20);
        textArea.setWrapText(true);
        
        textArea.setId("textArea");
        if (justify) textArea.getStyleClass().add("justify");
        
        textArea.setLayoutX(10);
        textArea.setLayoutY(10);
        textBox.getChildren().add(textArea);

        return textBox;
    }

    /**
     * Function to create a custom textFiel in a box
     * @param text : Text to display
     * @param textSize : Size of the text
     * @param with : Width of the box
     * @param height : Height of the box
     * @param backgroundColor : Color of the box
     * @return Pane : The box with the text
     */
    public static Pair<StackPane,TextField> loadTextField(String text, int textSize, Color textColor ,int with, int height, Color backgroundColor, RunnableTask<TextField, ? super KeyEvent> action){
        StackPane textBox = new StackPane();
        textBox.setPrefSize(with, height);
        textBox.setMaxWidth(with);
        
        Rectangle background = loadBackground(textSize * 4, (int)(textSize * 2), backgroundColor, textSize);
        textBox.getChildren().add(background);
        
        final TextField valueText = new TextField(text);
        valueText.setFont(getFont("light", textSize));
        valueText.setStyle("-fx-background-color: transparent; -fx-text-fill: " + textColor.toString().replace("0x", "#") + ";");
        valueText.setAlignment(Pos.CENTER);
        valueText.selectRange(0, 0);
        if(action !=null) valueText.setOnKeyTyped(event -> action.run(valueText, event));
        textBox.getChildren().add(valueText);

        return new Pair<StackPane,TextField>(textBox, valueText);
    }

    /**
     * Function to import a picture
     * @param path : Path of the picture
     * @param width : Width of the picture !!! Picture save ratio tiny value is save!!!
     * @param height : Height of the picture
     * @return ImageView : The picture
     */
    public static ImageView loadImageView(String path, int width, int height){
        try {
            final Image image = new Image(path, width, height, true, true);
            ImageView imageV = new ImageView(image);
            imageV.setFitHeight(image.getHeight());
            imageV.setFitWidth(image.getWidth());
            return imageV;
        } catch(Exception e) {
            ConsoleManager.create(path).withType(EnumLogType.LAUNCHER).error().end();
            return null;
        }
    }

    /**
     * Function to import a picture with a color using Blend effect
     * @param path : Path of the picture
     * @param width : Width of the picture !!! Picture save ratio tiny value is save!!!
     * @param height : Height of the picture
     * @param color : Color of the picture in hexadecimal (ex: #3ba55d)
     * @return ImageView : The picture with the color
     */
    public static ImageView loadImageView(String path, int width, int height, String color){

        ImageView imageV = JFXUtils.loadImageView(path, width, height);

        Blend blend = new Blend();
        blend.setMode(BlendMode.SRC_ATOP);
        blend.setTopInput(new ColorInput(0, 0, imageV.getFitWidth(), imageV.getFitWidth(), Color.web(color)));
        imageV.setEffect(blend);

        return imageV;
    }

    /**
     * Function to create a background with a color and a border radius
     * @param width : Width of the background
     * @param height : Height of the background
     * @param color : Color of the background
     * @param arc : Border radius
     * @return Rectangle : The background
     */
    public static Rectangle loadBackground(int width, int height, Color color, int arc){
        Rectangle background = new Rectangle(width, height);
        background.setArcHeight(arc);
        background.setArcWidth(arc);
        background.setFill(color);
        return background;
    }


    /**
     * Function to create a switch button
     * @param width : Width of the switch button
     * @param height : Height of the switch button
     * @param backgroundColor : Color of the background
     * @param TriggerColorCheck : Color of the trigger when the button is checked
     * @param triggerColor : Color of the trigger when the button is not checked
     * @return Pane : The switch button
     */
    public static Pane loadSwitchButton(int width, int height, Color backgroundColor, Color TriggerColorCheck, Color triggerColor, boolean active, RunnableTask<Boolean, ? super MouseEvent> action) {
        Pane boxCheck = new Pane();
        boxCheck.setPrefSize(width, height);

        SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(active);
        
        Rectangle background = JFXUtils.loadBackground(width, height, backgroundColor, height);
        
        Circle trigger = new Circle(height/2);
        trigger.setFill(triggerColor);
        trigger.setCenterX(height/2);
        trigger.setCenterY(height/2);
        trigger.setTranslateX(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), trigger);
        FillTransition fillTransition = new FillTransition(Duration.millis(100), trigger);

        ParallelTransition parallelTransition = new ParallelTransition(translateTransition, fillTransition);
        
        boxCheck.getChildren().addAll(background, trigger);

        if(active) {
            translateTransition.setToX(width - height);
            fillTransition.setFromValue(triggerColor);
            fillTransition.setToValue(TriggerColorCheck);
            parallelTransition.play();
        }

        switchedOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState.booleanValue();
            translateTransition.setToX(isOn ? width - height : 1);
            fillTransition.setFromValue(isOn ? triggerColor : TriggerColorCheck);
            fillTransition.setToValue(isOn ? TriggerColorCheck : triggerColor);
            parallelTransition.play();
        });

        boxCheck.setOnMouseClicked(e -> {
            switchedOn.set(!switchedOn.get());
            action.run(switchedOn.get(), e);
        });

        return boxCheck;
    }


    /**
     * Function to create a custom slider
     * @param maxValue : Max value of the slider
     * @param width : Width of the slider
     * @param height : Height of the slider
     * @param BackgroundColorPreThumb : Color of the slider before the thumb
     * @param BackgroundColorPostThumb : Color of the slider after the thumb
     * @param thumbColor : Color of the thumb
     * @return Pair<StackPane,Slider> : The slider pane for presentation and the slider for the value
     */
    private static Pair<StackPane,Slider> generateSlider(int maxValue ,int width, int height, Color BackgroundColorPreThumb, Color BackgroundColorPostThumb ,Color thumbColor){
                
        final Slider slider = new Slider(0,maxValue,(int)(maxValue/2));
        slider.setMinWidth(width);
        slider.setMaxWidth(width);
        slider.setPrefSize(width, height);

        final Rectangle pb = new Rectangle(0, 0, width/2, height);
        pb.setArcHeight(height/1.5);
        pb.setArcWidth(height/1.5);
        pb.setFill(BackgroundColorPreThumb);
        pb.setDisable(true);

        final Rectangle track = new Rectangle(0, 0, width, height);
        track.setArcHeight(height/1.5);
        track.setArcWidth(height/1.5);
        track.setFill(BackgroundColorPostThumb);
        track.setDisable(true);

        final Rectangle thumb = new Rectangle(0, 0, 6, height * 1.7);
        thumb.setArcHeight(height/1.5);
        thumb.setArcWidth(height/1.5);
        thumb.setFill(thumbColor);
        thumb.setDisable(true);
        thumb.setTranslateX(slider.getValue()  * width/maxValue - 5);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                pb.setWidth(slider.getValue() * width/maxValue);
                thumb.setTranslateX(slider.getValue()  * width/maxValue - 5);
            }
        });

        StackPane pane = new StackPane();
        pane.setPrefSize(width, height);
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().addAll(track, pb, slider, thumb);

        return new Pair<>(pane, slider);
    }

    /**
     * Function to create a slider with a Label
     * @param maxValue : Max value of the slider
     * @param width : Width of the slider
     * @param height : Height of the slider
     * @param BackgroundColorPreThumb : Color of the slider before the thumb
     * @param BackgroundColorPostThumb : Color of the slider after the thumb
     * @param thumbColor : Color of the thumb
     * @param text : Label of the slider
     * @param textSize : Size of the label
     * @param textColor : Color of the label
     * @return Pane : The slider with a label and a textfield for the value
     */
    public static Pane loadSlider(int maxValue ,int width, int height, Color BackgroundColorPreThumb, Color BackgroundColorPostThumb, Color thumbColor, 
        String text, int textSize, Color textColor, RunnableTask<Slider, Double> action, RunnableTask<TextField, ? super KeyEvent> actionText) {
        Pair<StackPane,Slider> sliderPair = generateSlider(maxValue, width, height, BackgroundColorPreThumb, BackgroundColorPostThumb, thumbColor);
        Text label = loadText(text, textSize, textColor, "regular");
        
        final Slider slider = sliderPair.getValue();
        Pair<StackPane,TextField> valuePair = loadTextField(Double.toString(maxValue/2), textSize, textColor, textSize * 4, textSize * 2, BackgroundColorPostThumb, actionText);
        TextField valueText = valuePair.getValue();
        StackPane value = valuePair.getKey();
        
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                valueText.setText(Double.toString(Math.round(slider.getValue() * 10) / 10.0));
                action.run(slider, slider.getValue());
            }
        });
        
        valueText.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.ENTER){
                slider.setValue(Double.parseDouble(valueText.getText()));
            }
        });


        StackPane valuePane = new StackPane();
        valuePane.getChildren().add(value);
        valuePane.setAlignment(Pos.CENTER_RIGHT);

        GridPane pane = new GridPane();
        pane.add(label, 0, 0);
        pane.add(valuePane, 1, 0);
        pane.add(sliderPair.getKey(), 0, 1, 2, 1);
        pane.setVgap(7);

        return pane;
    }

    public static interface RunnableTask<T, E> {
        public void run(T object, E event);
    }
}
