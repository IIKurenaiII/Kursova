module org.example.kursova {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens org.kursova to javafx.fxml;
    exports org.kursova;
}