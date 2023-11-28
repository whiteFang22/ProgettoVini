package com.example.client;

import com.example.classes.Amministratore;
import com.example.classes.Impiegato;
import com.example.classes.ReportMensile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdmController {
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField nome;
    @FXML
    private TextField cognome;
    @FXML
    private TextField codiceFiscale;
    @FXML
    private TextField numTelefonico;
    @FXML
    private TextField indirizzo;

    @FXML
    private Text registrationMessage;
    @FXML
    private ToggleGroup changeImp;
    @FXML
    private Button confermaButton;
    @FXML
    private Text textResponse;

    @FXML
    private TextField introiti;
    @FXML
    private TextField spese;
    @FXML
    private TextField bottVendute;
    @FXML
    private TextField bottDisp;

    @FXML
    private HBox gridImpiegati;
    private static ListView<HBox> listView;


    private ReportMensile report = new ReportMensile(0,0,0,0,null,null);
    @FXML
    protected  void registrazione(){
        System.out.println("Registrazione");
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        Stage stage = (Stage) parent.getScene().getWindow();
        Amministratore adm = (Amministratore) SharedData.getInstance().getUser();

        //Ho aggiunto il campo password.getText() per istanziare gia l'utente con la password in modo che poi sia sufficiente
        //chiamare c.registrazione(), credo sia da implementare in front end
        Impiegato i = new Impiegato(nome.getText(), cognome.getText(),password.getText(), codiceFiscale.getText(), email.getText(), numTelefonico.getText(), indirizzo.getText(),false);
        boolean success = adm.registrazioneImpiegato(i, password.getText());

        if (success){
            GetStage obj = new GetStage();
            obj.set(stage, "amministratore");
        }
        else {
            System.out.println("registration error");
            registrationMessage.setText("prova con un'altra email");
            registrationMessage.setFill(Color.RED);
        }
    }

    @FXML
    protected void onChangeImp(){
        RadioButton btn = (RadioButton) changeImp.getSelectedToggle();
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        switch (btn.getText()) {
            case "modifica password" -> {
                password.setDisable(false);
                confermaButton.setUserData(false);
            }
            case "elimina impiegato" -> {
                password.setDisable(true);
                confermaButton.setUserData(true);
            }
        }
    }
    @FXML
    protected void confermaCredenziali(){
        boolean eliminaImpiegato = false;
        if (confermaButton.getUserData() != null) eliminaImpiegato = (boolean) confermaButton.getUserData();
        Amministratore adm = (Amministratore) SharedData.getInstance().getUser();

        boolean success = adm.AdminModificaCredenziali(email.getText(), password.getText(), eliminaImpiegato);
        if (success){
            textResponse.setText("operazione avvenuta con successo");
            textResponse.setFill(Color.GREEN);
        }
        else {
            textResponse.setText("qualcosa è andato storto, riprova");
            textResponse.setFill(Color.RED);
        }
    }

    @FXML
    protected void continua1(){
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        Amministratore adm = (Amministratore) SharedData.getInstance().getUser();

        //retrive informations
        report.setIntroiti(Integer.parseInt(introiti.getText()));
        report.setSpese(Integer.parseInt(spese.getText()));
        report.setBottiglieVendute(Integer.parseInt(bottVendute.getText()));
        report.setBottiglieDisponibili(Integer.parseInt(bottDisp.getText()));

        //change page
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("amministratore/reportP2");
        parent.setCenter(view);

        listView = new ListView<>();
        listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();
        //List<String> impiegati =  adm.ricercaImpiegati();
        List<String> impiegati = new ArrayList<>();

        // Aggiungere stringhe alla lista
        impiegati.add("Prima stringa");
        impiegati.add("Seconda stringa");
        impiegati.add("Terza stringa");
        impiegati.add("Quarta stringa");
        for (String impiegato : impiegati){
            Text imp = new Text(impiegato);

            TextField valutazione = new TextField();
            valutazione.setMaxWidth(40);

            /*Button cliente = new Button(ordine.getCliente().getEmail());
            cliente.setUserData(ordine.getCliente());
            cliente.setOnAction(event ->{
                Cliente obj = (Cliente) cliente.getUserData();
                sch.VediSchedaCliente(SharedData.getInstance().getCurrentParent(), obj);
            });

            Button schedaOrdine = new Button("VEDI SCHEDA");
            schedaOrdine.setUserData(ordine);
            schedaOrdine.setOnAction(actionEvent -> {
                sch.manageSchede(ordine);
            });*/

            HBox riga = new HBox(imp, valutazione);
            riga.setSpacing(10);
            items.add(riga);
        }
        listView.setItems(items);
        listView.setMinWidth(350);
        listView.setMinHeight(200);
        listView.setSelectionModel(null);

        BorderPane main = SharedData.getInstance().getCurrentParent();
        gridImpiegati = (HBox) main.lookup("#gridImpiegati");
        gridImpiegati.getChildren().clear();
        gridImpiegati.getChildren().add(listView);
    }

    @FXML
    protected void creaReport(){
        Amministratore adm = (Amministratore) SharedData.getInstance().getUser();
        Map<String, Integer> valutazioni = new HashMap<>();
        String email = "";
        TextField val = null;

        for (HBox riga:  listView.getItems()){
            for (Node node : riga.getChildren()){
                if (node instanceof Text){
                    Text impiegato = (Text) node;
                    email = impiegato.getText();
                }
                if (node instanceof TextField){
                    val = (TextField) node;
                    valutazioni.put(email, Integer.parseInt(val.getText()));
                    System.out.println(val.getText());
                }
            }
        }

        report.setValutazioneDipendenti(valutazioni);
        adm.preparazioneReport(report);
    }
}
