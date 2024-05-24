package org.kursova;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kursova.animations.Shake;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller_authorization {
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button RegistrationButton;
    @FXML
    private Button ConfirmButton;

    private static String currentUserLogin;
    private static String currentUserPassword;

    @FXML
    public void initialize() {
        ConfirmButton.setOnAction(event -> {
            String loginUsername = userNameField.getText().trim();
            String loginPassword = passwordField.getText().trim();

            if (loginUsername.isEmpty() || loginPassword.isEmpty()) {
                Shake userAnim = new Shake(userNameField);
                Shake userPassword = new Shake(passwordField);
                userAnim.playAnim();
                userPassword.playAnim();
            } else {
                loginUser(loginUsername, loginPassword);
            }
        });

        RegistrationButton.setOnAction(event -> {
            loadScene("/org/kursova/registration-view.fxml", RegistrationButton);
        });
    }

    private void loginUser(String loginUsername, String loginPassword) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUsername(loginUsername);
        user.setPassword(loginPassword);
        ResultSet res = dbHandler.getUser(user);

        int count = 0;
        String role = "";

        try {
            while (res.next()) {
                count++;
                role = res.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (count >= 1) {
            loadHomeScene(role);
            currentUserLogin = loginUsername;
            currentUserPassword = loginPassword;
            closeCurrentWindow(ConfirmButton);
        } else {
            Shake userAnim = new Shake(userNameField);
            Shake userPassword = new Shake(passwordField);
            userAnim.playAnim();
            userPassword.playAnim();
        }

        Controller_home.setLoggedInUser(user);
    }

    private FXMLLoader loader;

    private void loadScene(String window, Button button) {
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));

        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) button.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadHomeScene(String role) {
        loadScene("/org/kursova/home-view.fxml", ConfirmButton);
        Controller_home controllerHome = loader.getController();
        controllerHome.setRole(role);
        controllerHome.updateTable();

        controllerHome.setDeleteButtonVisibility(role);
        controllerHome.setControlButtonVisibility(role);
        showAlert(role);
    }

    private void showAlert(String role) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Роль користувача");
        alert.setHeaderText(null);
        alert.setContentText("Ви маєта права: " + role);
        alert.showAndWait();
    }

    private void closeCurrentWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public static String getCurrentUserLogin() {
        return currentUserLogin;
    }

    public static String getCurrentUserPassword() {
        return currentUserPassword;
    }
}
