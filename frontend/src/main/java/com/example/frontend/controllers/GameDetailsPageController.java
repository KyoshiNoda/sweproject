package com.example.frontend.controllers;

import com.example.frontend.App;
import com.example.frontend.Game;
import com.example.frontend.User;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.SetOptions;
//import com.google.cloud.firestore.WriteResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import static com.example.frontend.RawgAPIConfig.getGames;
import static com.example.frontend.controllers.MainPageController.clickedGame;

public class GameDetailsPageController {

    @FXML
    private TextField RatingTextField;
    //@FXML
    //private TextArea DescriptionTextArea;
    @FXML
    private TextField ReleaseTextField;
    @FXML
    private TextField PlayTimeTextField;
    @FXML
    private ImageView GameImageView;
    @FXML
    private TextField ESRBTextField;
    @FXML
    private Button BackButton;
    @FXML
    private void initialize() {
        LoadGameDetails(clickedGame);
    }

    private void LoadGameDetails(Game game) {
        String imageUrl = game.getBackground_image();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                GameImageView = new ImageView(new Image(imageUrl));
                GameImageView.setFitWidth(200);
                GameImageView.setFitHeight(150);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        RatingTextField.setText(game.getRating());
        //DescriptionTextArea.setText(game.);
        ReleaseTextField.setText(game.getReleased());
        ESRBTextField.setText(game.getEsrb());

    }



    @FXML
    protected void Return_To_Main_Page() throws IOException {
        App.setRoot("main-page");
    }




}
