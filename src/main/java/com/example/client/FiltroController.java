package com.example.client;

import com.example.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FiltroController implements Initializable {
    @FXML
    Button ordiniProposteButton;
    @FXML
    Button clientiButton;
    @FXML
    Button registraButton;
    @FXML
    Button reportButton;
    @FXML
    Button viniButton;
    @FXML
    Button credenzialiButton;
    @FXML
    Button esciButton;

    @FXML
    DatePicker data1;
    @FXML
    DatePicker data2;

    @FXML
    Label centerTitle;

    @FXML
    ToggleGroup filtroRicerca;
    @FXML
    BorderPane mainBorderImpiegati;
    @FXML
    HBox listaClienti;
    final FindParent find = new FindParent();
    private static ListView<HBox> listView;

    @FXML
    private void ShowLeftOriniProposte(){
        System.out.println("left-ordini-proposte");
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/left-ordini-proposte");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("impiegato/center-ordini-proposte");
        parent.setCenter(view);
    }

    @FXML
    private void ShowLeftClienti() throws IOException {
        System.out.println("left-clienti");
        BorderPane parent = (BorderPane) clientiButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/left-clienti");
        parent.setLeft(view);

        object = new FxmlLoader();
        view = object.getPage("impiegato/center-clienti-registrati");
        parent.setCenter(view);

        cercaClienti();
    }

    @FXML
    private void showLeftVini(){
        System.out.println("left-ordini-proposte");
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("impiegato/left-vini");
        parent.setLeft(view);

        view = object.getPage("impiegato/center-vini");
        parent.setCenter(view);
        RicercaController controller = new RicercaController();
        //controller.ricercaVini();
    }

    @FXML
    private void registraImpiegato(){
        System.out.println("registra impiegato");
        BorderPane parent = (BorderPane) registraButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("amministratore/registrationForm");
        parent.setCenter(view);
    }
    @FXML
    private void showReportPage(){
        System.out.println("registra impiegato");
        BorderPane parent = (BorderPane) reportButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("amministratore/reportP1");
        parent.setCenter(view);
    }
    @FXML
    private  void showModificaCredenziali(){
        System.out.println("registra impiegato");
        BorderPane parent = (BorderPane) credenzialiButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("amministratore/credenziali-imp");
        parent.setCenter(view);
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
        SharedData.getInstance().resetInstance(true);

    }

    @FXML
    private void ChangeCenter() throws IOException, ParseException {
        System.out.println("change-center");
        RadioButton btn = (RadioButton) filtroRicerca.getSelectedToggle();

        BorderPane parent = (BorderPane) btn.getParent().getParent().getParent();
        centerTitle = (Label) parent.lookup("#centerTitle");

        switch (btn.getText()) {
            case "Ordini Acquisto" -> {
                centerTitle.setText("ORDINI DI ACQUISTO");
                cercaOrdiniAcquisto();
            }
            case "Ordini Vendita" -> {
                centerTitle.setText("ORDINI DI VENDITA");
                cercaOrdiniVendita();
            }
            case "Proposte Acquisto" -> {
                centerTitle.setText("PROPOSTE DI ACQUISTO");
                cercaProposteAcquisto();
            }
        }
    }

    @FXML
    protected void cercaClienti() {
        listView = new ListView<>();
        listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        TextField filtro_cognome = (TextField) parent.lookup("#filtro_cognome");

        List<Cliente> clienti = imp.ricercaClienti(filtro_cognome.getText());
        System.out.println("cognome is: "+filtro_cognome.getText());
        System.out.println("size: "+clienti.size());
        System.out.println(clienti.get(0).getCognome());
        SchedeController sch = new SchedeController();

        int i=0;
        for (Cliente cliente : clienti){
            TextField email = new TextField(cliente.getEmail());
            email.setMinWidth(100);

            /*Button schedaAcquisti = new Button("ACQUISTI");
            schedaAcquisti.setOnAction(actionEvent -> {
                Cliente data = (Cliente) schedaAcquisti.getUserData();
                sch.manageSchede(data);
            });*/

            Button schedaCiente = new Button("VEDI SCHEDA");
            schedaCiente.setUserData(cliente);
            schedaCiente.setOnAction(event ->{
                Cliente data = (Cliente) schedaCiente.getUserData();
                sch.manageSchede(data);
            });

            HBox riga = new HBox(email, schedaCiente);
            riga.setSpacing(10);
            items.add(riga);
        }
        listView.setItems(items);
        listView.setMinWidth(350);
        listView.setMinHeight(100);
        listView.setSelectionModel(null);

        BorderPane main = SharedData.getInstance().getCurrentParent();
        listaClienti = (HBox) main.lookup("#listaClienti");
        listaClienti.getChildren().clear();
        listaClienti.getChildren().add(listView);
    }

    @FXML
    protected void cercaOrdiniVendita() throws IOException, ParseException {
        listView = new ListView<>();
        //listView.setId("lista");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        SchedeController sch = new SchedeController();
        System.out.println("data1: "+data1.getValue());
        System.out.println("data2: "+data2.getValue());

        // Converti l'oggetto Instant in un oggetto Date
        if (data1.getValue()!=null && data2.getValue()!=null){
            Date d1 = java.sql.Date.valueOf(data1.getValue());
            Date d2 = java.sql.Date.valueOf(data2.getValue());

            System.out.println("data1F: "+d1);
            System.out.println("data2F: "+d2);
            Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
            List<OrdineVendita> ordini = imp.ricercaOrdiniVendita(d1,d2);

            //List<OrdineVendita> ordini = null;

            for (OrdineVendita ordine : ordini){
                Text dataCreazione = new Text(ordine.getDataCreazione().toString());

                Button cliente = new Button(ordine.getCliente().getEmail());
                cliente.setUserData(ordine.getCliente());
                cliente.setOnAction(event ->{
                    Cliente obj = (Cliente) cliente.getUserData();
                    sch.VediSchedaCliente(SharedData.getInstance().getCurrentParent(), obj);
                });

                Button schedaOrdine = new Button("VEDI SCHEDA");
                schedaOrdine.setUserData(ordine);
                schedaOrdine.setOnAction(actionEvent -> {
                    sch.manageSchede(ordine);
                });

                HBox riga = new HBox(dataCreazione, cliente, schedaOrdine);
                riga.setSpacing(10);
                items.add(riga);
            }
            listView.setItems(items);
            listView.setMinWidth(350);
            listView.setMinHeight(100);
            listView.setSelectionModel(null);

            BorderPane main = SharedData.getInstance().getCurrentParent();
            listaClienti = (HBox) main.lookup("#lista");
            listaClienti.getChildren().clear();
            listaClienti.getChildren().add(listView);
        }
        System.out.println("devi inserire un intervallo temporale");
    }
    @FXML
    protected void cercaOrdiniAcquisto() throws IOException, ParseException {
        listView = new ListView<>();
        //listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        SchedeController sch = new SchedeController();
        if (data1.getValue()!=null && data2.getValue()!=null) {
            Date d1 = java.sql.Date.valueOf(data1.getValue());
            Date d2 = java.sql.Date.valueOf(data2.getValue());

            Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
            List<OrdineAcquisto> ordini = imp.ricercaOrdiniAcquisto(d1, d2);

            //List<OrdineAcquisto> ordini = null;

            for (OrdineAcquisto ordine : ordini) {
                Text dataCreazione = new Text(ordine.getDataCreazione().toString());

                Button cliente = new Button(ordine.getCliente().getEmail());
                cliente.setUserData(ordine.getCliente());
                cliente.setOnAction(event -> {
                    Cliente obj = (Cliente) cliente.getUserData();
                    sch.VediSchedaCliente(SharedData.getInstance().getCurrentParent(), obj);
                });

                Button schedaOrdine = new Button("VEDI SCHEDA");
                schedaOrdine.setUserData(ordine);
                schedaOrdine.setOnAction(actionEvent -> {
                    sch.manageSchede(ordine);
                });

                HBox riga = new HBox(dataCreazione, cliente, schedaOrdine);
                riga.setSpacing(10);
                items.add(riga);
            }
            listView.setItems(items);
            listView.setMinWidth(350);
            listView.setMinHeight(100);
            listView.setSelectionModel(null);

            BorderPane main = SharedData.getInstance().getCurrentParent();
            listaClienti = (HBox) main.lookup("#lista");
            listaClienti.getChildren().clear();
            listaClienti.getChildren().add(listView);
        }
        System.out.println("devi inserire un intervallo temporale");
    }
    @FXML
    protected void cercaProposteAcquisto() throws IOException, ParseException {
        System.out.println("cerca proposte");
        listView = new ListView<>();
        //listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        SchedeController sch = new SchedeController();

        // Converti l'oggetto Instant in un oggetto Date
        if (data1.getValue()!=null && data2.getValue()!=null) {
            Date d1 = java.sql.Date.valueOf(data1.getValue());
            Date d2 = java.sql.Date.valueOf(data2.getValue());

            System.out.println("data1F: " + d1);
            System.out.println("data2F: " + d2);
            Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
            List<PropostaAcquisto> proposte = imp.ricercaProposteAcquisto(d1, d2);
            System.out.println("check1");
            for (PropostaAcquisto proposta : proposte) {
                System.out.println("check2");
                Text dataCreazione = new Text(proposta.getDataCreazione().toString());

                Button cliente = new Button(proposta.getCliente().getEmail());
                cliente.setUserData(proposta.getCliente());
                cliente.setOnAction(event -> {
                    Cliente obj = (Cliente) cliente.getUserData();
                    sch.VediSchedaCliente(SharedData.getInstance().getCurrentParent(), obj);
                });

                Button schedaOrdine = new Button("VEDI SCHEDA");
                schedaOrdine.setUserData(proposta);
                schedaOrdine.setOnAction(actionEvent -> {
                    sch.manageSchede(proposta);
                });

                HBox riga = new HBox(dataCreazione, cliente, schedaOrdine);
                riga.setSpacing(10);
                items.add(riga);
            }
            listView.setItems(items);
            listView.setMinWidth(350);
            listView.setMinHeight(100);
            listView.setSelectionModel(null);

            BorderPane main = SharedData.getInstance().getCurrentParent();
            listaClienti = (HBox) main.lookup("#lista");
            listaClienti.getChildren().clear();
            listaClienti.getChildren().add(listView);
        }
        System.out.println("devi inserire un intervallo temporale");
    }

    @FXML
    protected void onWaitOrdiniVendita(){
        waitOrdini("vendita");
    }

    @FXML
    protected void onWaitOrdiniAcquisto(){
        waitOrdini("acquisto");
    }

    private void waitOrdini(String tipo){
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        centerTitle = (Label) parent.lookup("#centerTitle");
        centerTitle.setText("ORDINI DI ACQUISTO");

        Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
        Response res = new Response();
        switch (tipo) {
            case "vendita" -> res = imp.recuperaOrdineVendita(60);
            case "acquisto" -> res = imp.recuperaOrdineAcquisto(60);
        }
        if (res.isSuccess()){
            //show scheda-ordine
            SchedeController sch = new SchedeController();
            sch.manageSchede(res.getData());
        }
        else {
            //scrivi messaggio per avvisare che nessun ordine è arrivato
            System.out.println("TIMEOUT");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // evito che venga inizializzato due volte: più fxml hanno stesso controller
        if (mainBorderImpiegati!=null) {
            SharedData.getInstance().setCurrentParent(mainBorderImpiegati);
        }
    }

}
