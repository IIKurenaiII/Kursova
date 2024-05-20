package org.kursova;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Controller_profile {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private Button okayBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button editBtnConfirm;

    private boolean isEdited = false;
    private boolean isPasswordEntered = false;

    @FXML
    private void initialize() {
        saveBtn.setVisible(false);
        saveBtn.setDisable(true);
        editBtnConfirm.setOnAction(event -> handleEditBtnAction());
        saveBtn.setOnAction(event -> handleSaveBtnAction());
        okayBtn.setOnAction(event -> handleOkayBtnAction());

        loginField.setText(Controller_authorization.getCurrentUserLogin());

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            updateSaveBtnState();
        });
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            updateSaveBtnState();
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            updateSaveBtnState();
        });
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            updateSaveBtnState();
        });
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEdited = true;
            updateSaveBtnState();
        });
    }

    private void handleEditBtnAction() {
        if (!isPasswordEntered) {
            if (authenticateUser()) {
                fillUserDetails();
                saveBtn.setVisible(true);
                saveBtn.setDisable(true);
                isPasswordEntered = true;
                editBtnConfirm.setDisable(true);
            } else {
                showAlert("Невірний пароль", "Пароль, який ви ввели, невірний.");
            }
        } else {
            showAlert("Помилка", "Пароль вже введений. Поля не можна редагувати.");
        }
    }

    private boolean authenticateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Підтвердження пароля");
        dialog.setHeaderText("Введіть свій поточний пароль");
        dialog.setContentText("Пароль:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String inputPassword = result.get();
            return inputPassword.equals(Controller_authorization.getCurrentUserPassword());
        }
        return false;
    }

    private void fillUserDetails() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUsername(Controller_authorization.getCurrentUserLogin());
        user.setPassword(Controller_authorization.getCurrentUserPassword());

        ResultSet resultSet = dbHandler.getUser(user);
        try {
            if (resultSet.next()) {
                String firstName = resultSet.getString(Const.USER_FIRSTNAME);
                String lastName = resultSet.getString(Const.USER_LASTNAME);
                String password = resultSet.getString(Const.USER_PASSWORD);
                String phoneNumber = resultSet.getString(Const.USER_PHONENUMBER);
                String email = resultSet.getString(Const.USER_EMAIL);

                firstNameField.setText(firstName);
                lastNameField.setText(lastName);
                passwordField.setText(password);
                phoneNumberField.setText(phoneNumber);
                emailField.setText(email);

                isEdited = false;
            } else {
                showAlert("Помилка", "Користувача не знайдено.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка", "Сталася помилка при завантаженні даних користувача.");
        }
    }

    private void handleSaveBtnAction() {
        if (isEdited) {
            DatabaseHandler dbHandler = new DatabaseHandler();
            User user = new User();
            user.setUsername(Controller_authorization.getCurrentUserLogin());
            user.setPassword(passwordField.getText());
            user.setFirstname(firstNameField.getText());
            user.setLastname(lastNameField.getText());
            user.setPhonenumber(phoneNumberField.getText());
            user.setEmail(emailField.getText());

            dbHandler.updateUser(user);

            showAlert("Успіх", "Ваші дані було успішно збережено.");
            saveBtn.setDisable(true);
            isEdited = false;
        }
    }

    private void updateSaveBtnState() {
        saveBtn.setDisable(!isEdited);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleOkayBtnAction() {
        Stage stage = (Stage) okayBtn.getScene().getWindow();
        stage.close();
    }
}
