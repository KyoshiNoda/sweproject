package com.example.frontend.controllers;

import com.example.frontend.App;
import com.example.frontend.Game;
import com.example.frontend.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FavGamesPageController {
    @FXML private ListView<Game> gamesListView;
    private User currentUser;

    public void setUserData(User user) {
        this.currentUser = user;
        gamesListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Game> call(ListView<Game> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Game game, boolean empty) {
                        super.updateItem(game, empty);
                        if (empty || game == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            VBox vbox = new VBox();
                            vbox.setStyle("-fx-background-size: contain; " +
                                    "-fx-background-position: center center; " +
                                    "-fx-alignment: center; " +
                                    "-fx-text-alignment: center;"
                            );
                            ImageView gameImageView = new ImageView(new Image(game.getBackground_image()));
                            gameImageView.setFitHeight(80);
                            gameImageView.setFitWidth(300);
                            javafx.scene.control.Label gameLabel = new javafx.scene.control.Label(game.getName());
                            gameLabel.setFont(Font.font("Arial", 16));
                            vbox.getChildren().addAll(gameImageView, gameLabel);
                            vbox.setOnMouseClicked(event -> {
                                removeGameHandler(game);
                            });
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
        gamesListView.setFixedCellSize(100);
        gamesListView.setPrefHeight(600);
        gamesListView.getItems().setAll(currentUser.getFavGames());
    }

    private void removeGameHandler(Game game) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Remove " + game.getName());
        confirmationDialog.setContentText("Are you sure you want to remove this game from favorites?");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                currentUser.getFavGames().remove(game);
                DocumentReference userRef = App.db.collection("Users").document(currentUser.getId());
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("favGames", currentUser.getFavGames());
                ApiFuture<WriteResult> updateFuture = userRef.update(updateData);
                try {
                    updateFuture.get();
                    showAlert("Game removed from favorites successfully", Alert.AlertType.INFORMATION);
                } catch (InterruptedException | ExecutionException e) {
                    showAlert("Failed to remove game from favorites. Please try again.", Alert.AlertType.ERROR);
                }
                gamesListView.getItems().setAll(currentUser.getFavGames());
            }
        });
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Game Removal");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
