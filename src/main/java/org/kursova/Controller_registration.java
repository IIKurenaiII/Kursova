package org.kursova;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kursova.animations.Shake;

import java.io.IOException;

public class Controller_registration {
    @FXML
    private Text emailText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text numberphoneText;
    @FXML
    private Text passwordText;
    @FXML
    private Text lastNameText;
    @FXML
    private Text userNameText;
    @FXML
    private TextField UserNameField;
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
    @FXML
    private Text passwordWarningText;
    @FXML
    private Text firstnameWarningText;
    @FXML
    private Text lastnameWarningText;
    @FXML
    private Text emailWarningText;
    @FXML
    private Text phoneNumberWarningText;

    private Stage stage;

    @FXML
    private void initialize() {
        stage = new Stage();

        CancelButton.setOnAction(event -> {
            try {
                loadScene("/org/kursova/authorization-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        RegistrationButton.setOnAction(event -> {
            if (signUpNewUser()) {
                try {
                    loadScene("/org/kursova/authorization-view.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setupTextVisibility();
        passwordWarningText.setVisible(false);
        firstnameWarningText.setVisible(false);
        lastnameWarningText.setVisible(false);
        emailWarningText.setVisible(false);
        phoneNumberWarningText.setVisible(false);
    }

    private void setupTextVisibility() {
        setTextVisibilityWithAnimation(FirstnameField, firstNameText);
        setTextVisibilityWithAnimation(LastnameField, lastNameText);
        setTextVisibilityWithAnimation(UserNameField, userNameText);
        setTextVisibilityWithAnimation(PasswordField, passwordText);
        setTextVisibilityWithAnimation(PhoneNumberField, numberphoneText);
        setTextVisibilityWithAnimation(EmailField, emailText);
    }

    private void setTextVisibilityWithAnimation(TextField textField, Text text) {
        Shake textShake = new Shake(text);
        text.setVisible(false);

        textField.setOnKeyReleased(event -> {
            if (!textField.getText().isEmpty()) {
                if (!text.isVisible()) {
                    text.setVisible(true);
                    textShake.fadeIn();
                }
            } else {
                if (text.isVisible()) {
                    textShake.fadeOut();
                    text.setVisible(false);
                }
            }
        });
    }

    private boolean signUpNewUser() {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String firstname = FirstnameField.getText();
        String lastname = LastnameField.getText();
        String password = PasswordField.getText();
        String username = UserNameField.getText();
        String phoneNumber = PhoneNumberField.getText();
        String email = EmailField.getText();
        String role;

        boolean validFirstName = firstname.isEmpty() || isNameValid(firstname);
        boolean validLastName = lastname.isEmpty() || isNameValid(lastname);
        boolean validEmail = email.isEmpty() || isEmailValid(email);
        boolean validPassword = password.isEmpty() || isPasswordValid(password);
        boolean validPhoneNumber = phoneNumber.isEmpty() || isPhoneNumberValid(phoneNumber);

        if (!validFirstName && !firstname.isEmpty()) {
            firstnameWarningText.setVisible(true);
        } else {
            firstnameWarningText.setVisible(false);
        }

        if (!validLastName && !lastname.isEmpty()) {
            lastnameWarningText.setVisible(true);
        } else {
            lastnameWarningText.setVisible(false);
        }

        if (!validEmail && !email.isEmpty()) {
            emailWarningText.setVisible(true);
        } else {
            emailWarningText.setVisible(false);
        }

        if (!validPassword && !password.isEmpty()) {
            passwordWarningText.setVisible(true);
        } else {
            passwordWarningText.setVisible(false);
        }

        if (!validPhoneNumber && !phoneNumber.isEmpty()) {
            phoneNumberWarningText.setVisible(true);
        } else {
            phoneNumberWarningText.setVisible(false);
        }

        if (firstname.isEmpty() || lastname.isEmpty() || password.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            showWarning("Увага", "Будь ласка, заповніть всі поля.");
            return false;
        }

        if (!validFirstName || !validLastName || !validEmail || !validPassword || !validPhoneNumber) {
            showWarning("Увага", "Перевірте правильність введених даних.");
            return false;
        }

        if (dbHandler.userExists(username)) {
            showWarning("Увага", "Користувач з таким логіном вже існує.");
            return false;
        }

        if (dbHandler.isFirstUser()) {
            role = "Super Admin";
        } else {
            role = "User";
        }

        User user = new User(firstname, lastname, password, username, phoneNumber, email, role);
        dbHandler.signUpUser(user);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успішна реєстрація");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Ви зареєстровані!");
        successAlert.showAndWait();
        return true;
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        centerStageOnScreen(stage);
        stage.show();
    }

    private void centerStageOnScreen(Stage stage) {
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setX((screenWidth - stage.getWidth()) / 2);
        stage.setY((screenHeight - stage.getHeight()) / 2);
    }

    private boolean isNameValid(String name) {
        return !name.matches(".*\\d.*");
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private boolean isPhoneNumberValid(String password) {
        return password.length() >= 10;
    }

}
