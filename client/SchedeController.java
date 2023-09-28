package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class SchedeController {
    @FXML
    Button vediScheda;
    @FXML
    Label centerTitle;

    @FXML
    private void ManageSchede(){
        System.out.println("managing");
        BorderPane parent = (BorderPane) vediScheda.getParent().getParent().getParent();
        centerTitle = (Label) parent.lookup("#centerTitle");
        System.out.println(centerTitle.getText());
        switch (centerTitle.getText()) {
            case "CLIENTI REGISTRATI":
                VediSchedaCliente(parent);
                break;
            case "ORDINI DI ACQUISTO":
                VediSchedaOrdiniAcquisto(parent);
                break;
            case "ORDINI DI VENDITA":
                VediSchedaOrdiniVendita(parent);
                break;
            case "PROPOSTE DI ACQUISTO":
                VediSchedaPropostaAcquisto(parent);
                break;
        }

    }

    private void VediSchedaCliente(BorderPane parent){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-cliente");
        parent.setCenter(view);
    }
    private void VediSchedaOrdiniAcquisto(BorderPane parent){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordine-acquisto");
        parent.setCenter(view);
    }
    private void VediSchedaOrdiniVendita(BorderPane parent){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordine-vendita");
        parent.setCenter(view);
    }
    private void VediSchedaPropostaAcquisto(BorderPane parent){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-proposta-acquisto");
        parent.setCenter(view);
    }
}
