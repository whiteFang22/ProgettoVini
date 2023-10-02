package com.example.client;

import com.example.classes.Cliente;
import com.example.classes.OrdineAcquisto;
import com.example.classes.OrdineVendita;
import com.example.classes.PropostaAcquisto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SchedeController {
    @FXML
    Button vediScheda;
    @FXML
    Label centerTitle;

    // Per scheda cliente
    @FXML
    TextField nome;
    @FXML
    TextField cognome;
    @FXML
    TextField codFis;
    @FXML
    TextField email;
    @FXML
    TextField telefono;
    @FXML
    TextField indirizzo;


    @FXML
    protected void manageSchede(Object data){
        System.out.println("managing");
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        centerTitle = (Label) parent.lookup("#centerTitle");
        System.out.println(centerTitle.getText());
        switch (centerTitle.getText()) {
            case "CLIENTI REGISTRATI" -> VediSchedaCliente(parent, (Cliente) data);
            case "ORDINI DI ACQUISTO" -> VediSchedaOrdiniAcquisto(parent, (OrdineAcquisto) data);
            case "ORDINI DI VENDITA" -> VediSchedaOrdiniVendita(parent, (OrdineVendita) data);
            case "PROPOSTE DI ACQUISTO" -> VediSchedaPropostaAcquisto(parent, (PropostaAcquisto) data);
        }

    }

    protected void VediSchedaCliente(BorderPane parent, Cliente cliente){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-cliente");
        parent.setCenter(view);

        // setta i campi
        nome = (TextField) parent.lookup("#nome");
        nome.setText(cliente.getNome());
        cognome = (TextField) parent.lookup("#cognome");
        cognome.setText(cliente.getCognome());
        codFis = (TextField) parent.lookup("#codFis");
        codFis.setText(cliente.getCodiceFiscale());
        email = (TextField) parent.lookup("#email");
        email.setText(cliente.getEmail());
        telefono = (TextField) parent.lookup("#telefono");
        telefono.setText(cliente.getNumeroTelefonico());
        indirizzo = (TextField) parent.lookup("#indirizzo");
        indirizzo.setText(cliente.getIndirizzoDiConsegna());
    }
    private void VediSchedaOrdiniAcquisto(BorderPane parent, OrdineAcquisto ordine){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordine-acquisto");
        parent.setCenter(view);
    }
    private void VediSchedaOrdiniVendita(BorderPane parent, OrdineVendita ordine){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordine-vendita");
        parent.setCenter(view);
    }
    private void VediSchedaPropostaAcquisto(BorderPane parent, PropostaAcquisto proposta){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-proposta-acquisto");
        parent.setCenter(view);
    }


    @FXML
    protected void indietro() throws IOException {
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/center-clienti-registrati");
        parent.setCenter(view);

        FiltroController controller = new FiltroController();
        controller.cercaClienti();
    }
}
