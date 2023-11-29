package com.example.client;

import com.example.classes.Cliente;
import com.example.classes.UtenteGenerico;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.nio.Buffer;

public class PagamentoController {
    @FXML
    VBox VboxPagamento;
    @FXML
    Label risultatoPagamento;
    @FXML
    Button button1;
    @FXML
    Button homeButton;
    @FXML
    Button annullaPagamento;

    @FXML
    AnchorPane borderCenter;
    private boolean cliccato = false;
    private static ListView<HBox> listView;

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
    protected void onAnnullaClick(){
        BorderPane parent = (BorderPane) VboxPagamento.getParent().getParent();
        Cliente c = (Cliente) SharedData.getInstance().getUser();
        c.confermaPagamento(false);
        indietro(parent);
    }
    private void indietro(BorderPane parent){
        SharedData.getInstance().resetInstance(false);

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/left-ricerca");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("cliente/scheda-vini-client");
        parent.setCenter(view);
    }

    @FXML
    protected void confermaPagamento(){
        Cliente user = (Cliente) SharedData.getInstance().getUser();
        boolean success = user.confermaPagamento(true);
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        if(success){
            //notifica all'utente che è andato tutto a buon fine e torna con bottone a schermata iniziale
            risultatoPagamento.setText("Il pagamento è andato a buon fine");
            risultatoPagamento.setTextFill(Color.GREEN);
            button1.setDisable(true);

            homeButton = (Button) parent.lookup("#homeButton");
            homeButton.setDisable(false);
            homeButton.setVisible(true);

            RadioButton radio = (RadioButton) parent.lookup("#bonificoChoice");
            radio.setDisable(true);
            radio = (RadioButton) parent.lookup("#cartaChoice");
            radio.setDisable(true);

            annullaPagamento = (Button) parent.lookup("#annullaPagamento");
            annullaPagamento.setDisable(true);

            if (cliccato) {
                indietro(parent);
            }
        }
        else {
            //
            risultatoPagamento.setText("Il pagamento NON è andato a buon fine. Prova con un altro conto");
            risultatoPagamento.setTextFill(Color.RED);
        }
        cliccato = !cliccato;
    }
    @FXML
    protected void toHome(){
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        indietro(parent);
    }
}

