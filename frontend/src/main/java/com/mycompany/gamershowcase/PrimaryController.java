package com.mycompany.gamershowcase;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
