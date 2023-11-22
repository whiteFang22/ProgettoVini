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

        RicercaController controller = new RicercaController();
        controller.ricercaVini();
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
    private void showRecordPage(){
        System.out.println("registra impiegato");
        BorderPane parent = (BorderPane) reportButton.getParent().getParent();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("amministratore/report");
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
        SharedData.getInstance().resetInstance();

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
            case "Proposte Acquisto" -> centerTitle.setText("PROPOSTE DI ACQUISTO");
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
            TextField cognome = new TextField(cliente.getCognome());
            cognome.setMinWidth(100);

            Button schedaAcquisti = new Button("ACQUISTI");
            schedaAcquisti.setOnAction(actionEvent -> {
                Cliente data = (Cliente) schedaAcquisti.getUserData();
                sch.manageSchede(data);
            });

            Button schedaCiente = new Button("VEDI SCHEDA");
            schedaCiente.setUserData(cliente);
            schedaCiente.setOnAction(event ->{
                Cliente data = (Cliente) schedaCiente.getUserData();
                sch.manageSchede(data);
            });

            HBox riga = new HBox(cognome, schedaAcquisti, schedaCiente);
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
        listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        SchedeController sch = new SchedeController();
        System.out.println("data1: "+data1.getValue());
        System.out.println("data2: "+data2.getValue());

        // Converti l'oggetto Instant in un oggetto Date
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
    @FXML
    protected void cercaOrdiniAcquisto() throws IOException, ParseException {
        listView = new ListView<>();
        listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        SchedeController sch = new SchedeController();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        Date data = formatoData.parse("22/09/2205");

        Impiegato imp = (Impiegato) SharedData.getInstance().getUser();
        //List<OrdineVendita> ordini = imp.ricercaOrdiniVendita(data, data);

        List<OrdineAcquisto> ordini = null;

        for (OrdineAcquisto ordine : ordini){
            Text dataCreazione = new Text(formatoData.format(ordine.getDataCreazione()));

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // evito che venga inizializzato due volte: più fxml hanno stesso controller
        if (mainBorderImpiegati!=null) {
            SharedData.getInstance().setCurrentParent(mainBorderImpiegati);
        }
    }

}
