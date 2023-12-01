package com.example.frontend.controllers;

import com.example.frontend.App;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterPageController {
    @FXML private TextField firstNameInput;
    @FXML private TextField lastNameInput;
    @FXML private TextField emailInput;
    @FXML private PasswordField passwordInput;
    @FXML private PasswordField confirmPasswordInput;
    @FXML
    public void initialize() { new Hyperlink("Log In").setOnAction(this::loginPageHandler); }

    @FXML
    private void submitHandler(ActionEvent event) {
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        String email = emailInput.getText();
        String password = passwordInput.getText();
        String confirmPassword = confirmPasswordInput.getText();

        if (!password.equals(confirmPassword)) {
            showAlert("Password and Confirm Password do not match");
            return;
        }

        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String uid = createUserInFirebase(email, hashedPassword);
            assert uid != null;
            DocumentReference userDocRef = App.db.collection("Users").document(uid);
            Map<String, Object> userData = new HashMap<>();

            userData.put("firstName", firstName);
            userData.put("lastName", lastName);
            userData.put("email", email);
            userData.put("favGames", new ArrayList<String>());
            userData.put("password", hashedPassword);
            userDocRef.set(userData);
            System.out.println("User data added to Firestore for UID: " + uid);
        } catch (Exception e) {
            showAlert("Error creating user: " + e.getMessage());
        }
    }

    private String createUserInFirebase(String email, String hashedPassword) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(hashedPassword);
            UserRecord userRecord = App.auth.createUser(request);
            return userRecord.getUid();
        } catch (Exception e) {
            showAlert("Error creating user: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void loginPageHandler(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/login-page.fxml"));
        Parent root;
        try { root = loader.load(); } catch (IOException e) { throw new RuntimeException(e);}
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
