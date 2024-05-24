package org.kursova;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import java.io.IOException;
import java.util.Optional;

public class Controller_home {
    @FXML
    private Button exitBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private TableView<Item> itemTableView;
    @FXML
    private Button updateBtn;
    @FXML
    private Button addButtonView;
    @FXML
    private Button editButtonView;
    @FXML
    private Button profileBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button controlBtn;
    @FXML
    private TableColumn<Item, String> productNumColumn;
    @FXML
    private TableColumn<Item, String> productNameColumn;
    @FXML
    private TableColumn<Item, String> statusColumn;
    @FXML
    private TableColumn<Item, String> typeColumn;
    @FXML
    private TableColumn<Item, String> quantityColumn;
    @FXML
    private TableColumn<Item, String> priceColumn;
    @FXML
    private TableColumn<Item, String> dateColumn;
    @FXML
    private TableColumn<Item, String> authorColumn;
    @FXML
    private RadioButton missRadioBtn;
    @FXML
    private RadioButton availableRadioBtn;
    @FXML
    private RadioButton showAllRadioBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchPropertiesComboBox;

    private final ToggleGroup radioGroup = new ToggleGroup();

    private ObservableList<Item> list = FXCollections.observableArrayList();
    private DatabaseHandler dbHandler;
    private static String currentUserRole;
    private Controller_authorization authController;
    private static User loggedInUser;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();

        productNumColumn.setCellValueFactory(new PropertyValueFactory<>("productNum"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        missRadioBtn.setToggleGroup(radioGroup);
        availableRadioBtn.setToggleGroup(radioGroup);
        showAllRadioBtn.setToggleGroup(radioGroup);
        missRadioBtn.setOnAction(event -> updateTable());
        availableRadioBtn.setOnAction(event -> updateTable());
        showAllRadioBtn.setOnAction(event -> updateTable());

        updateBtn.setOnAction(actionEvent -> updateTable());
        searchBtn.setOnAction(this::handleSearchButtonAction);
        editButtonView.setOnAction(actionEvent -> {
            Item selectedItem = itemTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                openEditWindow(selectedItem);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Попередження");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, оберіть елемент для редагування.");
                alert.showAndWait();
            }
        });

        addButtonView.setOnAction(actionEvent -> loadScene("/org/kursova/add_window-view.fxml"));
        profileBtn.setOnAction(actionEvent -> loadScene("/org/kursova/profile_window-view.fxml"));
        controlBtn.setOnAction(actionEvent -> loadScene("/org/kursova/control-window_view.fxml"));
        exitBtn.setOnAction(actionEvent -> showExitConfirmation());
        deleteBtn.setOnAction(actionEvent -> handleDeleteButtonAction());

        itemTableView.setItems(list);
        showAllRadioBtn.setSelected(true);
        updateTable();

        authController = new Controller_authorization();
    }

    public void updateTable() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        ObservableList<Item> productList;

        if (missRadioBtn.isSelected()) {
            productList = dbHandler.getProductsByStatus("Відсутнє");
        } else if (availableRadioBtn.isSelected()) {
            productList = dbHandler.getProductsByStatus("Наявне");
        } else {
            productList = dbHandler.getAllProducts();
        }

        itemTableView.setItems(productList);
    }

    public void loadScene(String window) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));

        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження виходу");
        alert.setHeaderText(null);
        alert.setContentText("Ви хочете вийти з програми або з аккаунта?");

        ButtonType exitAppButton = new ButtonType("Вийти з програми");
        ButtonType exitAccountButton = new ButtonType("Вийти з аккаунта");
        ButtonType cancelButton = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(exitAppButton, exitAccountButton, cancelButton);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == exitAppButton) {
                Platform.exit();
            } else if (result.get() == exitAccountButton) {
                loadLoginScene();
            }
        }
    }

    @FXML
    private void searchProduct() {
        String selectedProperty = searchPropertiesComboBox.getValue();
        String searchValue = searchField.getText().trim();

        if (selectedProperty != null && !searchValue.isEmpty()) {
            ObservableList<Item> filteredList;
            switch (selectedProperty) {
                case "Назва продукту":
                    filteredList = dbHandler.searchProductByName(searchValue);
                    break;
                case "Тип":
                    filteredList = dbHandler.searchProductByType(searchValue);
                    break;
                case "Кількість":
                    filteredList = dbHandler.searchProductByQuantity(searchValue);
                    break;
                case "Ціна":
                    filteredList = dbHandler.searchProductByPrice(searchValue);
                    break;
                case "Дата":
                    filteredList = dbHandler.searchProductByDate(searchValue);
                    break;
                case "Хто додав":
                    filteredList = dbHandler.searchProductByAuthor(searchValue);
                    break;
                default:
                    filteredList = FXCollections.emptyObservableList();
            }
            itemTableView.setItems(filteredList);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка пошуку");
            alert.setHeaderText(null);
            alert.setContentText("Будь ласка, оберіть властивість та введіть значення для пошуку.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        searchProduct();
    }

    private void openEditWindow(Item selectedItem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kursova/edit_window-view.fxml"));
            Parent root = loader.load();

            Controller_edit editController = loader.getController();
            editController.initData(selectedItem);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            editController.setStage(stage);

            stage.showAndWait();
            updateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        Item selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Підтвердження видалення");
            alert.setHeaderText(null);
            alert.setContentText("Ви впевнені, що хочете видалити цей елемент?");

            ButtonType okButton = new ButtonType("Так", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Ні", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == okButton) {
                    dbHandler.deleteItem(selectedItem);
                    updateTable();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження");
            alert.setHeaderText(null);
            alert.setContentText("Будь ласка, оберіть елемент для видалення.");
            alert.showAndWait();
        }
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public void setRole(String role) {
        currentUserRole = role;
    }

    public void setDeleteButtonVisibility(String role) {
        if ("Admin".equals(role)) {
            deleteBtn.setVisible(true);
        } else if ("Super Admin".equals(role)) {
            deleteBtn.setVisible(true);
        } else {
            deleteBtn.setVisible(false);
        }
    }

    public void setControlButtonVisibility(String role) {
        if ("Admin".equals(role)) {
            controlBtn.setVisible(true);
        } else if ("Super Admin".equals(role)) {
            controlBtn.setVisible(true);
        } else {
            controlBtn.setVisible(false);
        }
    }
    private void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kursova/authorization-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) exitBtn.getScene().getWindow();
            stage.setScene(new Scene(root));

            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            stage.setX((screenWidth - stage.getWidth()) / 2);
            stage.setY((screenHeight - stage.getHeight()) / 2);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

