package com.launcher.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertPopup {

    public AlertPopup(String title, String text) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
