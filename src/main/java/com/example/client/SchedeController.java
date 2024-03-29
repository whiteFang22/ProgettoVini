package com.example.client;

import com.example.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javafx.scene.paint.Color.*;

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

    // Per visualizzare i vini
    @FXML
    HBox viniProposti;
    @FXML
    TextField cliente;
    @FXML
    TextField prezzo;
    @FXML
    DatePicker dataConsegna;
    @FXML
    Button firmaButton;

    //per scheda vino
    @FXML
    TextField produttore;
    @FXML
    TextField provenienza;
    @FXML
    TextArea vitigni;
    @FXML
    TextArea noteTecniche;

    //per scheda ordine
    @FXML
    Text resMessage;

    private static ListView<HBox> listView;


    @FXML
    protected void manageSchede(Object data){
        System.out.println("managing");
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        /*centerTitle = (Label) parent.lookup("#centerTitle");
        System.out.println(centerTitle.getText());
        switch (centerTitle.getText()) {
            case "CLIENTI REGISTRATI" -> VediSchedaCliente(parent, (Cliente) data);
            case "ORDINI DI ACQUISTO" -> VediSchedaOrdiniAcquisto(parent, (OrdineAcquisto) data);
            case "ORDINI DI VENDITA" -> VediSchedaOrdiniVendita(parent, (OrdineVendita) data);
            case "PROPOSTE DI ACQUISTO" -> VediSchedaPropostaAcquisto(parent, (PropostaAcquisto) data);
        }*/
        if (data instanceof Vino) VediSchedaVino(parent, (Vino) data);
        else if (data instanceof Cliente) VediSchedaCliente(parent, (Cliente) data);
        else if (data instanceof OrdineVendita) VediSchedaOrdiniVendita(parent, (OrdineVendita) data);
        else if (data instanceof OrdineAcquisto) VediSchedaOrdiniAcquisto(parent, (OrdineAcquisto) data);
        else if (data instanceof  PropostaAcquisto) VediSchedaPropostaAcquisto(parent, (PropostaAcquisto) data);

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

    private void VediSchedaVino(BorderPane parent, Vino vino){
        System.out.println("scheda-vino");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("general/scheda-vino");
        parent.setCenter(view);

        nome = (TextField) parent.lookup("#nome");
        nome.setText(vino.getNome());
        produttore = (TextField) parent.lookup("#produttore");
        produttore.setText((vino.getProduttore()));
        provenienza = (TextField) parent.lookup("#provenienza");
        provenienza.setText(vino.getProvenienza());
        noteTecniche = (TextArea) parent.lookup("#noteTecniche");
        noteTecniche.setText((vino.getNoteTecniche()));
        vitigni = (TextArea) parent.lookup("#vitigni");
        vitigni.setText(vino.getVitigni().toString());
    }

    private void VediSchedaOrdiniAcquisto(BorderPane parent, OrdineAcquisto ordine){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordini-acquisto");
        parent.setCenter(view);

        //show wines
        creaGrid(ordine.getViniMancanti());
    }
    private void VediSchedaOrdiniVendita(BorderPane parent, OrdineVendita ordine){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-ordine-vendita");
        parent.setCenter(view);

        //show wines
        creaGrid(ordine.getViniAcquistati());

        //show infos
        email = (TextField) parent.lookup("#email");
        email.setText(ordine.getCliente().getEmail());
        indirizzo = (TextField) parent.lookup("#indirizzo");
        indirizzo.setText(ordine.getIndirizzoConsegna());
        prezzo = (TextField) parent.lookup("#prezzo");
        ordine.ottimizza();
        prezzo.setText(Float.toString(ordine.calcolaTotale()));
        firmaButton = (Button) parent.lookup("#firmaButton");
        firmaButton.setUserData(ordine);
    }
    private void VediSchedaPropostaAcquisto(BorderPane parent, PropostaAcquisto proposta){
        System.out.println("scheda-cliente");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/scheda-proposta-acquisto");
        parent.setCenter(view);

        //show wines
        creaGrid(proposta.getVini());
    }

    protected void creaGrid(Map<Vino, Integer> listaVini){
        listView = new ListView<>();
        listView.setId("listaVini");
        ObservableList<HBox> items = FXCollections.observableArrayList();
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        listaVini.forEach((vino, qnt) -> {
            HBox riga;

            TextField nomeVinotext = new TextField(vino.getNome()+" - "+vino.getAnno());
            nomeVinotext.setUserData(vino);
            nomeVinotext.setMinWidth(100);

            TextField quantita = new TextField("quantità: "+qnt);
            quantita.setUserData(vino);
            quantita.setMaxWidth(90);
            riga = new HBox(nomeVinotext, quantita);

            riga.setSpacing(10);
            items.add(riga);
        });

        listView.setItems(items);
        listView.setMinWidth(300);
        //listView.setMinHeight(100);
        listView.setSelectionModel(null);

        //BorderPane main = find.findBorderPane(VboxRicerca);
        viniProposti = (HBox) parent.lookup("#vini");
        viniProposti.getChildren().clear();
        viniProposti.getChildren().add(listView);
    }

    @FXML
    protected void onFirma(){
        Date dc = java.sql.Date.valueOf(dataConsegna.getValue());
        Impiegato imp = (Impiegato) SharedData.getInstance().getUser();

        OrdineVendita ord = (OrdineVendita) firmaButton.getUserData();
        System.out.println("id: "+ord.getId());
        System.out.println(ord.getViniAcquistati());
        System.out.println(ord.getDataConsegna());
        ord.setDataConsegna(dc);
        boolean succ = imp.gestioneOrdineVendita(ord);

        if (succ) {
            resMessage.setText("ordine firmato correttamente");
            resMessage.setFill(GREEN);
            firmaButton.setDisable(true);
        }
        else {
            resMessage.setText("qualcosa è andato storto");
            resMessage.setFill(RED);
        }
    }

    @FXML
    protected void indietroSC() throws IOException {
        System.out.println("indietro");
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        String path = "";
        String settage = parent.getLeft().getId();
        System.out.println(settage);
        switch (settage) {
            case "VBoxRicercaCliente" -> {
                System.out.println("cli");
                path = "impiegato/center-clienti-registrati";
            }
            case "VboxOrdiniProposte" -> {
                System.out.println("ordprop");
                path = "impiegato/center-ordini-proposte";
            }
            case "LVboxRicerca" -> {
                System.out.println("viniC");
                path = "cliente/scheda-vini-client";
            }
            case "VBoxRicercaVini" -> {
                System.out.println("viniI");
                path = "impiegato/center-vini";
            }
        }

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage(path);
        parent.setCenter(view);

        FiltroController controller = new FiltroController();
        if (settage.equals("VBoxRicercaCliente")) controller.cercaClienti();
    }

}
