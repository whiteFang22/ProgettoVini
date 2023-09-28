package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PagamentoController {
    @FXML
    VBox VboxPagamento;

    @FXML
    protected void loadBonifico(){
        System.out.println("bonifico");
        BorderPane parent = (BorderPane) VboxPagamento.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-bonifico");
        parent.setCenter(view);
    }

    @FXML
    protected void loadCarta(){
        System.out.println("carta di credito");
        BorderPane parent = (BorderPane) VboxPagamento.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-cartaDiCredito");
        parent.setCenter(view);
    }

    @FXML
    protected void onIndietroCLick(){
        System.out.println("indietro: ricerca");
        BorderPane parent = (BorderPane) VboxPagamento.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/left-ricerca");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("cliente/scheda-vini-client");
        parent.setCenter(view);
    }
}
