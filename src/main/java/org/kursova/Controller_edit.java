package org.kursova;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class Controller_edit {

    @FXML
    private TextField editNameField;
    @FXML
    private TextField editTypeField;
    @FXML
    private TextField editQuantityField;
    @FXML
    private TextField editPriceField;
    @FXML
    private TextField editAuthorField;
    @FXML
    private RadioButton radioBtnEnabled;
    @FXML
    private RadioButton radioBtnMiss;
    @FXML
    private Button confirmBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button minusBtn;

    private Item selectedItem;
    private Stage stage;

    private String initialName;
    private String initialType;
    private String initialQuantity;
    private String initialPrice;
    private String initialAuthor;
    private String initialStatus;

    @FXML
    private void initialize() {
        ToggleGroup statusToggleGroup = new ToggleGroup();
        radioBtnEnabled.setToggleGroup(statusToggleGroup);
        radioBtnMiss.setToggleGroup(statusToggleGroup);

        cancelBtn.setOnAction(this::handleCancelButtonAction);
        addBtn.setOnAction(event -> handleAddButtonAction());
        minusBtn.setOnAction(event -> handleMinusButtonAction());
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        saveChanges();
        closeWindow();
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        if (isChanged()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Незбережені зміни");
            alert.setHeaderText(null);
            alert.setContentText("У вас є незбережені зміни. Ви хочете зберегти їх перед виходом?");

            ButtonType saveButton = new ButtonType("Зберегти");
            ButtonType dontSaveButton = new ButtonType("Не зберігати");
            ButtonType cancelButton = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);

            alert.showAndWait().ifPresent(type -> {
                if (type == saveButton) {
                    saveChanges();
                    closeWindow();
                } else if (type == dontSaveButton) {
                    closeWindow();
                }
            });
        } else {
            closeWindow();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void saveChanges() {
        String editedName = editNameField.getText();
        String editedType = editTypeField.getText();
        String editedQuantity = editQuantityField.getText();
        String editedPrice = editPriceField.getText();
        String editedAuthor = editAuthorField.getText();
        String editedStatus = radioBtnEnabled.isSelected() ? "Наявне" : "Відсутнє";

        if (selectedItem != null) {
            selectedItem.setProductName(editedName);
            selectedItem.setType(editedType);
            selectedItem.setQuantity(editedQuantity);
            selectedItem.setPrice(editedPrice);
            selectedItem.setAuthor(editedAuthor);
            selectedItem.setStatus(editedStatus);

            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.updateProduct(selectedItem);
        } else {
            System.err.println("Не вдалося знайти вибраний елемент для редагування.");
        }
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            System.err.println("Сцена не встановлена. Неможливо закрити вікно.");
        }
    }

    private boolean isChanged() {
        return !initialName.equals(editNameField.getText()) ||
                !initialType.equals(editTypeField.getText()) ||
                !initialQuantity.equals(editQuantityField.getText()) ||
                !initialPrice.equals(editPriceField.getText()) ||
                !initialAuthor.equals(editAuthorField.getText()) ||
                !initialStatus.equals(radioBtnEnabled.isSelected() ? "Наявне" : "Відсутнє");
    }

    public void initData(Item item) {
        selectedItem = item;
        if (selectedItem != null) {
            initialName = selectedItem.getProductName();
            initialType = selectedItem.getType();
            initialQuantity = selectedItem.getQuantity();
            initialPrice = selectedItem.getPrice();
            initialAuthor = selectedItem.getAuthor();
            initialStatus = selectedItem.getStatus();

            editNameField.setText(initialName);
            editTypeField.setText(initialType);
            editQuantityField.setText(initialQuantity);
            editPriceField.setText(initialPrice);
            editAuthorField.setText(initialAuthor);
            if ("Наявне".equals(initialStatus)) {
                radioBtnEnabled.setSelected(true);
            } else {
                radioBtnMiss.setSelected(true);
            }
        }
    }

    private void handleAddButtonAction() {
        int currentQuantity = Integer.parseInt(editQuantityField.getText());
        editQuantityField.setText(String.valueOf(currentQuantity + 1));
    }

    private void handleMinusButtonAction() {
        int currentQuantity = Integer.parseInt(editQuantityField.getText());
        if (currentQuantity > 0) {
            editQuantityField.setText(String.valueOf(currentQuantity - 1));
        }
    }
}
