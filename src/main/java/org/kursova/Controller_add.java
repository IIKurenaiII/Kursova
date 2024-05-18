package org.kursova;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class Controller_add {
    @FXML
    private Button addButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField authorField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton absentRadioButton;
    @FXML
    private RadioButton availableRadioButton;
    @FXML
    private CheckBox checkAuthorBtn;

    private final ToggleGroup radioGroup = new ToggleGroup();
    private int productNumber;

    @FXML
    private void initialize(){
        absentRadioButton.setToggleGroup(radioGroup);
        availableRadioButton.setToggleGroup(radioGroup);

        assignProductNumber();

        addButton.setOnAction(event -> {
            if (radioGroup.getSelectedToggle() == null || productNameField.getText().isEmpty() || typeField.getText().isEmpty() || quantityField.getText().isEmpty() ||
                    priceField.getText().isEmpty() || authorField.getText().isEmpty() || datePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText("Одне з полів пусте або радіо-кнопка не вибрана");
                alert.setContentText("Будь ласка, заповніть всю форму.");
                alert.showAndWait();
            } else {
                signUpNewItem();

                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.close();
            }
        });

        exitButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        });

        checkAuthorBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    authorField.setText(Controller_authorization.getCurrentUserLogin());
                } else {
                    authorField.clear();
                }
            }
        });
    }

    private void signUpNewItem() {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String num = String.valueOf(productNumber);
        String productName = productNameField.getText();
        String status = absentRadioButton.isSelected() ? "Відсутнє" : "Наявне";
        String type = typeField.getText();
        String quantity = quantityField.getText();
        String price = priceField.getText();
        LocalDate date = datePicker.getValue();
        String author = authorField.getText();

        String dateString = date.toString();

        Item item = new Item(num, productName, status, type, quantity, price, dateString,author);

        dbHandler.signUpProduct(item);
    }

    private void assignProductNumber() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        int existingProductCount = dbHandler.getProductCount();
        productNumber = existingProductCount + 1;
    }
}
