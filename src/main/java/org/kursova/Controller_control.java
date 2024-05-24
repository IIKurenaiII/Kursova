package org.kursova;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Controller_control {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> firstnameColumn;
    @FXML
    private TableColumn<User, String> lastnameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> phonenumberColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private Button exitBtn;
    @FXML
    private Button deleteUser;
    @FXML
    private Button takeRoleBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private Button refreshTableBtn;
    @FXML
    private Button addUserField;
    @FXML
    private ChoiceBox<String> choiceAddField;
    @FXML
    private TextField searchUserField;
    @FXML
    private TextField firstNameAddField;
    @FXML
    private TextField lastNameAddField;
    @FXML
    private TextField passwordAddField;
    @FXML
    private TextField userNameAddField;
    @FXML
    private TextField phoneNumberAddField;
    @FXML
    private TextField emailAddField;
    @FXML
    private RadioButton firstNameRadioBtn;
    @FXML
    private RadioButton loginRadioBtn;

    private DatabaseHandler dbHandler;
    private ObservableList<String> options = FXCollections.observableArrayList("User", "Admin");

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();

        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phonenumberColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        String currentUserRole = getCurrentUserRole();
        if (!"Super Admin".equals(currentUserRole)) {
            deleteUser.setDisable(true);
        }

        exitBtn.setOnAction(actionEvent -> showExitConfirmation());
        deleteUser.setOnAction(actionEvent -> handleDeleteUser());
        takeRoleBtn.setOnAction(actionEvent -> handleTakeRole());
        searchBtn.setOnAction(actionEvent -> handleSearchUser());
        refreshTableBtn.setOnAction(actionEvent -> updateTable());
        addUserField.setOnAction(actionEvent -> handleAddUser());

        choiceAddField.setItems(options);
        choiceAddField.setValue("Оберіть опцію");

        updateTable();
    }

    private String getCurrentUserRole() {
        String currentUsername = Controller_authorization.getCurrentUserLogin();
        if (currentUsername == null || currentUsername.isEmpty()) {
            return "";
        }
        return dbHandler.getUserRoleByUsername(currentUsername);
    }
    public void updateTable() {
        ObservableList<User> userList = dbHandler.getAllUsers();
        userTable.setItems(userList);
    }

    private void showExitConfirmation() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження виходу");
        alert.setHeaderText("Ви впевнені, що хочете вийти?");
        alert.setContentText("Натисніть OK для виходу або Cancel для скасування.");

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(okButton, cancelButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                Stage stage = (Stage) exitBtn.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (selectedUser.getRole().equalsIgnoreCase("admin")) {
                showWarning("Неможливо видалити", "Адмінів не можна видаляти.");
            } else {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Підтвердження видалення");
                alert.setHeaderText("Ви впевнені, що хочете видалити користувача?");

                ButtonType deleteButton = new ButtonType("Видалити");
                ButtonType cancelButton = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(deleteButton, cancelButton);

                alert.showAndWait().ifPresent(type -> {
                    if (type == deleteButton) {
                        dbHandler.deleteUser(selectedUser.getUsername());
                        updateTable();
                    }
                });
            }
        } else {
            showWarning("Увага", "Будь ласка, виберіть користувача для видалення.");
        }
    }

    private void handleTakeRole() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Підтвердження надання ролі");
            alert.setHeaderText("Оберіть нову роль для користувача:");

            ButtonType userRole = new ButtonType("Користувач");
            ButtonType adminRole = new ButtonType("Адмін");
            ButtonType cancelButton = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(userRole, adminRole, cancelButton);

            alert.showAndWait().ifPresent(type -> {
                if (type == userRole) {
                    selectedUser.setRole("User");
                    dbHandler.updateUserRole(selectedUser.getUsername(), "User");
                    updateTable();
                } else if (type == adminRole) {
                    selectedUser.setRole("Admin");
                    dbHandler.updateUserRole(selectedUser.getUsername(), "Admin");
                    updateTable();
                }
            });
        } else {
            showWarning("Увага", "Будь ласка, виберіть користувача для зміни ролі.");
        }
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleSearchUser() {
        String searchText = searchUserField.getText();
        ObservableList<User> userList;

        if (searchText.isEmpty()) {
            showWarning("Увага", "Будь ласка, введіть значення для пошуку.");
            return;
        }

        if (firstNameRadioBtn.isSelected()) {
            userList = dbHandler.searchUserByFirstName(searchText);
        } else if (loginRadioBtn.isSelected()) {
            userList = dbHandler.searchUserByUsername(searchText);
        } else {
            userList = dbHandler.getAllUsers();
        }

        userTable.setItems(userList);
    }

    private void handleAddUser() {
        String firstName = firstNameAddField.getText();
        String lastName = lastNameAddField.getText();
        String password = passwordAddField.getText();
        String userName = userNameAddField.getText();
        String phoneNumber = phoneNumberAddField.getText();
        String email = emailAddField.getText();
        String role = choiceAddField.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || userName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || role.equals("Оберіть опцію")) {
            showWarning("Увага", "Будь ласка, заповніть всі поля та оберіть позицію користувача.");
            return;
        }

        if (dbHandler.userExists(userName)) {
            showWarning("Увага", "Користувач з таким логіном вже існує.");
            return;
        }

        User newUser = new User(firstName, lastName, password, userName, phoneNumber, email, role);
        dbHandler.signUpUser(newUser);
        updateTable();

        clearAddUserFields();
    }

    private void clearAddUserFields() {
        firstNameAddField.clear();
        lastNameAddField.clear();
        passwordAddField.clear();
        userNameAddField.clear();
        phoneNumberAddField.clear();
        emailAddField.clear();
        choiceAddField.setValue("Оберіть опцію");
    }
}
