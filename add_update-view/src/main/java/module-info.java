module org.example.add_updateview {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.add_updateview to javafx.fxml;
    exports org.example.add_updateview;
}