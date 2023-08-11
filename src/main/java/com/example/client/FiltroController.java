package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FiltroController {
    @FXML
    Button ordiniProposteButton;

    @FXML
    Button clientiButton;

    @FXML
    Label centerTitle;

    @FXML
    ToggleGroup filtroRicerca;

    @FXML
    VBox VboxOrdiniProposte;

    @FXML
    private void ShowLeftOriniProposte(){
        System.out.println("left-ordini-proposte");
        BorderPane parent = (BorderPane) ordiniProposteButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/left-ordini-proposte");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("impiegato/center-ordini-proposte");
        parent.setCenter(view);
    }

    @FXML
    private void ShowLeftClienti(){
        System.out.println("left-clienti");
        BorderPane parent = (BorderPane) clientiButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/left-clienti");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("impiegato/center-clienti-registrati");
        parent.setCenter(view);
    }

    @FXML
    private void ChangeCenter() {
        System.out.println("change-center");
        RadioButton btn = (RadioButton) filtroRicerca.getSelectedToggle();

        BorderPane parent = (BorderPane) btn.getParent().getParent().getParent();
        centerTitle = (Label) parent.lookup("#centerTitle");

        switch (btn.getText()) {
            case "Ordini Acquisto":
                centerTitle.setText("ORDINI DI ACQUISTO");
                break;
            case "Ordini Vendita":
                centerTitle.setText("ORDINI DI VENDITA");
                break;
            case "Proposte Acquisto":
                centerTitle.setText("PROPOSTE DI ACQUISTO");
                break;
        }
    }
}
