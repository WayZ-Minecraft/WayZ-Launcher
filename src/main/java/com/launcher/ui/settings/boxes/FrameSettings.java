package com.launcher.ui.settings.boxes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class FrameSettings {
    
    final ScrollPane scrollPane = new ScrollPane();
    final double scrollSpeed = 0.001;
    final Pane frame = new Pane();
    public final static int frameWidth = 600;
    final static int frameHeight = 400;

    List<TextFieldElement> boxList = new ArrayList<>();
    final static int boxJump = 25;

    public FrameSettings() {
        this.scrollPane.setPrefSize(frameWidth, frameHeight);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setId("framePane");
        
        this.frame.setStyle("-fx-background-color: #00000000;");
        this.scrollPane.setContent(this.frame);

        this.scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * scrollSpeed;
            asyncLoop(0, deltaY);
        });
    }
    
    private void asyncLoop(int count, double deltaY){
        CompletableFuture.delayedExecutor(15, TimeUnit.MILLISECONDS)
        .execute(() -> {
            // Perform asynchronous task here
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            
            // Condition to exit the loop
            if (count >= 10) return;

            // Recursive call with incremented count
            asyncLoop(count + 1, deltaY);
        });
    }
    
    public void addBox(TextFieldElement box){this.boxList.add(box);}

    public ScrollPane getFrame() {
        int boxHeight = 0;
        for (TextFieldElement box : this.boxList) {
            this.frame.getChildren().add(box.getBox());
            box.getBox().setLayoutY(boxHeight);
            boxHeight += box.getBox().getPrefHeight() + boxJump;
        }
        return this.scrollPane;
    }
}
