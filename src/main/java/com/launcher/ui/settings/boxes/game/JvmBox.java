package com.launcher.ui.settings.boxes.game;

import com.launcher.ui.JFXUtils;
import com.launcher.ui.settings.boxes.TextFieldElement;

import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class JvmBox extends TextFieldElement {
    
    final static String title = "JVM Arguments";
    final static int boxHeight = 160;

    final Pane content = new Pane();
    int contentWidth = boxWidth - 4*sidePadding;

    final static String textJVM = "-XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 ...";
    final static int contentHeight = 80;
    final static int iconSize = 20;
    final static Color iconBackgroundColor = Color.web("#8b2628");

    public JvmBox(int boxWidth) {
        super(title, boxWidth, boxHeight);

        this.box.getChildren().add(content);
        this.content.setLayoutX(sidePadding * 2);
        this.content.setLayoutY(50);

        this.fillBox();
    }

    private Pane getIconBox() {
        StackPane iconBox = new StackPane();
        
        Rectangle background = JFXUtils.loadBackground(30, contentHeight, iconBackgroundColor, 20);
        iconBox.getChildren().add(background);

        ImageView icon = JFXUtils.loadImageView("logos/java.png", 200, iconSize, "#ffffff");
        iconBox.getChildren().add(icon);

        return iconBox;
    }

    private Pane getJvmInfoPane(){
        Pane infoText = JFXUtils.loadTextBox("", textSizeZoneText, contentWidth, contentHeight, backgroundColorZoneText, false ,true, true);
        TextArea textArea = (TextArea)infoText.getChildren().get(1);
        textArea.setTranslateX(30);
        textArea.setPromptText(textJVM);
        return infoText;
    }

    @Override
    protected void fillBox() {
        Pane jvmInfoPane = this.getJvmInfoPane();
        this.content.getChildren().add(jvmInfoPane);

        Pane iconBox = this.getIconBox();
        this.content.getChildren().add(iconBox);
    }
    
}
