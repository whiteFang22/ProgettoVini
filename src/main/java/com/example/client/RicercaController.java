package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class RicercaController {
    @FXML
    VBox VboxRevisione;
    @FXML
    VBox VboxRicerca;

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

}
