package com.launcher.ui.settings.boxes.game;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.TextFieldElement;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Pair;

public class resolutionBox extends TextFieldElement{

    final static String title = "Launch Resolution";
    final static int boxHeight = 105;

    final GridPane content = new GridPane();

    public resolutionBox(int boxWidth) {
        super(title, boxWidth, boxHeight);

        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding);
        this.content.setLayoutY(50);

        this.content.setHgap(35);

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
     * @return Pane : resolution custom zone
     */
    private Pane loadResolutionCustome(){
        Pane pane = new Pane();

        Pair<StackPane,TextField> widthResolution = JFXUtils.loadTextField("1920", lineTextSize, lineTextColor, 100, lineHeight, backgroundColorZoneText);  
        StackPane widthResolutionBox = widthResolution.getKey();
        // TextField widthResolutionText = widthResolution.getValue();

        Pair<StackPane,TextField> heightResolution = JFXUtils.loadTextField("1080", lineTextSize, lineTextColor, 100, lineHeight, backgroundColorZoneText);
        StackPane heightResolutionBox = heightResolution.getKey();
        // TextField heightResolutionText = heightResolution.getValue();

        Pane cross = this.loadCross((int)(lineTextSize*0.8), (int)(lineTextSize * 0.8));

        pane.getChildren().addAll(widthResolutionBox, heightResolutionBox, cross);
        cross.setLayoutX(95);
        cross.setLayoutY((lineHeight - lineTextSize*0.8)/2);
        heightResolutionBox.setLayoutX(105);

        return pane;
    }

    @Override
    protected void fillBox() {
        CheckBox fullScreen = new CheckBox("Fullscreen");
        fullScreen.setFont(JFXUtils.getFont("regular", lineTextSize));
        
        CheckBox custom = new CheckBox("standard");
        custom.setFont(JFXUtils.getFont("regular", lineTextSize));
        
        Pane resolutionCustom = this.loadResolutionCustome();

        this.content.add(fullScreen, 0, 0);
        this.content.add(custom, 1, 0);
        this.content.add(resolutionCustom, 2, 0);
    }
    
}
