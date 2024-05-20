package org.kursova;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller_registration {
    @FXML
    private TextField UsernNamelField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private TextField PhoneNumberField;

    @FXML
    private TextField FirstnameField;

    @FXML
    private TextField LastnameField;

    @FXML
    private TextField EmailField;

    @FXML
    private Button RegistrationButton;

    @FXML
    private Button CancelButton;

    private Stage stage;

    @FXML
    private void initialize () {
        stage = new Stage();

        CancelButton.setOnAction(event ->{
            closeWindow();
        });

        RegistrationButton.setOnAction(event ->{
            signUpNewUser();
            closeWindow();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/kursova/authorization-view.fxml"));

            try {
                Stage primaryStage = (Stage) RegistrationButton.getScene().getWindow(); // Отримуємо вікно авторизації
                Parent root = loader.load();
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void signUpNewUser() {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String firstname = FirstnameField.getText();
        String lastname = LastnameField.getText();
        String password = PasswordField.getText();
        String username = UsernNamelField.getText();
        String phoneNumber = PhoneNumberField.getText();
        String email = EmailField.getText();

        String role = "User";

        User user = new User(firstname, lastname, password, username, phoneNumber, email,role);

        dbHandler.signUpUser(user);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успішна реєстрація");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Ви зареєстровані!");
        successAlert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }
}
