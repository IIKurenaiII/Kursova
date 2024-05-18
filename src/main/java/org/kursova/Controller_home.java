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
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import org.kursova.DatabaseHandler;


import java.io.IOException;

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
    private TableColumn<Item, Integer> productNum;
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
    private ComboBox searchPropertiesComboBox;

    private final ToggleGroup radioGroup = new ToggleGroup();

    private ObservableList<Item> list = FXCollections.observableArrayList();
    private int lastProductNum = 0;
    private DatabaseHandler dbHandler;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();

        productNum.setCellValueFactory(new PropertyValueFactory<>("productNum"));
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


        addButtonView.setOnAction(actionEvent -> loadScene("/org/kursova/add_window-view.fxml"));

        exitBtn.setOnAction(actionEvent -> showExitConfirmation());

        itemTableView.setItems(list);
        lastProductNum = list.size();
        showAllRadioBtn.setSelected(true);
        updateTable();
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
        alert.setHeaderText("Ви впевнені, що хочете вийти?");
        alert.setContentText("Натисніть OK для виходу або Cancel для скасування.");

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(okButton, cancelButton);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                Platform.exit();
            }
        });
    }

    @FXML
    private void handleComboBoxAction(ActionEvent event) {
        String selectedProperty = (String) searchPropertiesComboBox.getValue();
        if (selectedProperty != null) {
            searchField.clear();
            searchField.setPromptText("Введіть значення для " + selectedProperty.toLowerCase());
        }
    }

    @FXML
    private void searchProduct() {
        String selectedProperty = (String) searchPropertiesComboBox.getValue();
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

}
