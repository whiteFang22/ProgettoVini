package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RicercaController {
    @FXML
    VBox VboxRevisione;
    @FXML
    VBox VboxRicerca;
    @FXML
    Button esciButton;

    @FXML
    protected void onAcquistaButton(){
        System.out.println("Acquista");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-bonifico");

        BorderPane parent = (BorderPane) VboxRevisione.getParent().getParent();
        parent.setCenter(view);

        object = new FxmlLoader();
        view = object.getPage("cliente/left-pagamento");
        parent.setLeft(view);

    }
    @FXML
    private void exit(){
        System.out.println("exit");
        Stage stage = (Stage) esciButton.getScene().getWindow();
        System.out.println(stage);

        FxmlLoader object = new FxmlLoader();
        Pane root =  object.getPage("general/log");
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(611);
        stage.setHeight(523);
        // azzera shared.user
    }

}
