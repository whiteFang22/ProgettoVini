module com.example.client {
    requires com.google.gson;
    requires javafx.controls;
    requires java.sql;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                        
    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.classes;
}