package de.bsommerfeld;

import de.bsommerfeld.orchestra.ui.Orchestra;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        System.setProperty("javafx.enablePreview", "true");
        Application.launch(Orchestra.class);
    }
}