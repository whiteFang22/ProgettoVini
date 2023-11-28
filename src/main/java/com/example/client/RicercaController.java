package com.example.client;

import com.example.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class RicercaController implements Initializable{
    @FXML
    VBox VboxRevisione;
    @FXML
    VBox VboxRicerca;
    @FXML
    Button esciButton;
    @FXML
    Button RicercaViniButton;
    @FXML
    private TextField nomeVino;
    @FXML
    private TextField annoProduzione;
    @FXML
    private HBox gridVini;
    @FXML
    private VBox viniDisponibili;
    @FXML
    private ToggleGroup TipologiaContenitore;
    @FXML
    private  Label nConfezioni;
    @FXML
    private Label nCasse;
    @FXML
    private Label prezzo;
    @FXML
    private Button propostaNo;
    @FXML
    private Button propostaSi;
    @FXML
    private BorderPane mainPaneInitialClient;

    private boolean success=false;
    final FindParent find = new FindParent();
    private static ListView<HBox> listView;


    @FXML
    protected void onAcquistaButton(){
        // usa metodo cliente che contatta il server per capire se c'è la disponibilità dei vini richiesti, se si
        // procedi con il pagamento altrimenti schermata proposta di acquisto
        Cliente user = (Cliente) SharedData.getInstance().getUser();
        System.out.println("vini:"+SharedData.getInstance().getVini());
        Response res = user.acquistaBottiglie(SharedData.getInstance().getVini());
        success = res.isSuccess();
        //F: success è legato al corretto funzionamento del server, è true anche se non ci sono bottiglie sufficienti purchè non
        //si siano verificati errori, ti rispondo con id = 1 se ce tutto, id = 2 se non ci sono abbastanza bottiglie
        // se true visualizzo l'ordine di vendita
        if (res.getId() == 1) {
            OrdineVendita ordine = (OrdineVendita) res.getData(); // deve restituirmi l'oridne di vendita creato sulla base dell'acquisto
            //visualizza l'ordine di vendita
            visualizzaPaginaOrdine();
            visualizzaListaOrdini(ordine);
            // recupero le informazioni di revisione ordine e le inserisco nel nuovo layout
            visualizzaLeft();
        }
        // se false chiedo se si vuole effettuare una proposta di acquisto: i vini non ci sono in magazzino
        else if(res.getId()==2) {
            // schermata proposta di acquisto
            System.out.println("Proposta");
            SharedData.getInstance().setRes(res); // salvo la res in modo che dopo possa accedere alla proposta di acquisto restituita dal server in precedenza
            BorderPane parent = SharedData.getInstance().getCurrentParent();
            FxmlLoader object = new FxmlLoader();
            Pane view = object.getPage("cliente/proposta-acquisto-client");
            parent.setCenter(view);
        }
    }
    protected void visualizzaLeft(){
        BorderPane parent = SharedData.getInstance().getCurrentParent();

        nConfezioni = (Label) parent.lookup("#nConfezioni");
        nCasse = (Label) parent.lookup("#nCasse");
        prezzo = (Label) parent.lookup("#prezzo");
        String confezioni = nConfezioni.getText();
        String casse = nCasse.getText();
        String prezzoTotale = prezzo.getText();

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/left-pagamento");
        parent.setLeft(view);

        nConfezioni = (Label) parent.lookup("#nConfezioni");
        nCasse = (Label) parent.lookup("#nCasse");
        prezzo = (Label) parent.lookup("#prezzo");
        nConfezioni.setText(confezioni);
        nCasse.setText(casse);
        prezzo.setText(prezzoTotale);
    }

    @FXML
    protected void annullaProposta(){
        SharedData.getInstance().resetInstance();

        System.out.println("Ordine");
        Cliente user = (Cliente) SharedData.getInstance().getUser();
        user.proponiAcquisto(false);

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-vini-client");
        BorderPane parent = find.findBorderPane(propostaNo);
        parent.setCenter(view);
    }
    @FXML
    protected void accettaProposta(){
        Cliente user = (Cliente) SharedData.getInstance().getUser();
        PropostaAcquisto proposta = (PropostaAcquisto) SharedData.getInstance().getRes().getData();
        Response res = user.proponiAcquisto(true);
        if ( res.isSuccess() ){
            OrdineVendita ordine = (OrdineVendita) res.getData();
            visualizzaPaginaOrdine();
            visualizzaListaOrdini(ordine);
            visualizzaLeft();
        }
    }
    @FXML
    protected void visualizzaPaginaOrdine(){
        /*Cliente user = (Cliente) SharedData.getInstance().getUser();
        Response res = user.acquistaBottiglie(SharedData.getInstance().getContenitori());
        OrdineVendita ordine = res.getOrdineVendita();
        */
        System.out.println("Ordine");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/ordine-di-vendita");
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        parent.setCenter(view);
    }
    // visualizza la lista dei vini in ordine
    protected void visualizzaListaOrdini(OrdineVendita ordine){
        listView = new ListView<>();
        listView.setId("listaViniOrdine");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        //crea manualmente lista di casse ma dopo dovrai recuperare da res
        /*List<CassaVino> casse = new ArrayList<>();
        Vino v1 = new Vino("Bordeaux",null,null,2020,null,null,23.65f,0,0);
        Vino v2 = new Vino("Martell Millesime",null,null,1944,null,null,12.6f,0,0);
        CassaVino ca = new CassaVino(v1, 6, 0);
        casse.add(ca);
        ca = new CassaVino(v2, 12, 0);
        casse.add(ca);*/
        ordine.ottimizza();
        System.out.println(ordine);
        System.out.println(ordine.getCasseVino());
        System.out.println(ordine.getconfezioniVini());
        List<CassaVino> casse = ordine.getCasseVino();
        List<ConfezioneVini> confezioni = ordine.getconfezioniVini();

        if(casse != null){
            for (CassaVino cassa : casse) {
                Vino vino = cassa.getVino();
                Label nomeVino = new Label(vino.getNome()+" - "+vino.getAnno());
                nomeVino.setUserData(vino);
                nomeVino.setMinWidth(135);

                String qnt = String.valueOf(cassa.getQuantita());
                Label quantitaField = new Label("quantità: "+qnt);
                quantitaField.setMinWidth(80);
                quantitaField.setId("quantita");

                Text costo = new Text("costo: "+String.format("%.2f", cassa.getPrezzo())+"€");
                HBox riga = new HBox(nomeVino, quantitaField, costo);
                riga.setSpacing(10);
                items.add(riga);
            }
        }
        // per ogni confezione scorri i vini contenuti
        if(confezioni != null){
            for (ConfezioneVini confezione : confezioni) {
                Map<Vino,Integer> vini = confezione.getVini();
                for (Map.Entry<Vino, Integer> entry : vini.entrySet()){
                    Vino vino = entry.getKey();
                    Label nomeVino = new Label(vino.getNome()+" - "+vino.getAnno());
                    nomeVino.setUserData(vino);
                    nomeVino.setMinWidth(135);

                    int quantita = entry.getValue();
                    Label quantitaField = new Label("quantità: "+quantita);
                    quantitaField.setMinWidth(80);
                    quantitaField.setId("quantita");

                    Text costo = new Text("costo: "+String.format("%.2f", vino.getPrezzo()*quantita )+"€");
                    HBox riga = new HBox(nomeVino, quantitaField, costo);
                    riga.setSpacing(10);
                    items.add(riga);
                }
            }
        }
        listView.setItems(items);
        listView.setMinWidth(380);
        listView.setMinHeight(200);
        listView.setSelectionModel(null);

        //BorderPane main = find.findBorderPane(VboxRicerca);
        BorderPane main = SharedData.getInstance().getCurrentParent();
        gridVini = (HBox) main.lookup("#gridOrdine");
        gridVini.getChildren().clear();
        gridVini.getChildren().add(listView);
    }

    // ordine serve a verificare se la visualizzazione è da parte della ricerca o dell'ordine
    // di vendita
    @FXML
    protected void exit(){

        System.out.println("exit");
        Stage stage = find.findStage(esciButton);
        System.out.println(stage);

        FxmlLoader object = new FxmlLoader();
        Pane root =  object.getPage("general/log");
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(611);
        stage.setHeight(523);

        // azzera l'istanza SharedData
        SharedData.getInstance().resetInstance();
    }

    // mostra a schermo la lista di vini richiesta dal client
    @FXML
    protected void ricercaVini(){
        UtenteGenerico user = SharedData.getInstance().getUser();
        int anno = -1; //indica che non va effettuata una ricerca per anno ma per nome
        String nome = null;
        if (!annoProduzione.getText().equals("")){
            anno = Integer.parseInt(annoProduzione.getText());
        }
        if (!nomeVino.getText().equals("")){
            nome = nomeVino.getText();
        }
        FiltriRicerca filtri = new FiltriRicerca(null, null, nome, anno);
        List<Vino> listaVini = user.cercaVini(filtri);

        // crea una tabella e per ogni riga aggiungi un vino
        creaGrid(listaVini);
    }
    protected void creaGrid(List<Vino> listaVini){
        listView = new ListView<>();
        listView.setId("listaVini");
        ObservableList<HBox> items = FXCollections.observableArrayList();
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        String left = parent.getLeft().getId();
        System.out.println("left: "+left);

        int i = 0;
        for (Vino vino : listaVini) {
            HBox riga = new HBox();

            Button nomeVinotext = new Button(vino.getNome()+" - "+vino.getAnno());
            nomeVinotext.setUserData(vino);
            nomeVinotext.setMinWidth(100);

            if (left.equals("VBoxRicercaVini")){
                Text disponibilita = new Text("disponibilità: "+vino.getDisponibilita());
                Text vendite = new Text("disponibilità: "+vino.getNumeroVendite());

                riga = new HBox(nomeVinotext, disponibilita, vendite);
            }
            else {
                TextField quantitaTextField = new TextField("0");
                quantitaTextField.setId("quantita");
                quantitaTextField.setEditable(false);
                quantitaTextField.setPrefColumnCount(2);

                //System.out.println(vino.getPrezzo());
                Text prezoUnitario = new Text("prezzo unitario: "+vino.getPrezzo()+"€");

                Button incrementaButton = new Button("+");
                incrementaButton.setOnAction(event -> {
                    int quantita = Integer.parseInt(quantitaTextField.getText());
                    quantitaTextField.setText(String.valueOf(quantita + 1));
                });

                Button decrementaButton = new Button("-");
                decrementaButton.setOnAction(event -> {
                    int quantita = Integer.parseInt(quantitaTextField.getText());
                    if (quantita > 0) {
                        quantitaTextField.setText(String.valueOf(quantita - 1));
                    }
                });
                riga = new HBox(nomeVinotext, quantitaTextField, incrementaButton, decrementaButton, prezoUnitario);
            }

            riga.setSpacing(10);
            items.add(riga);
        }

        listView.setItems(items);
        listView.setMinWidth(450);
        listView.setMinHeight(200);
        listView.setSelectionModel(null);

        //BorderPane main = find.findBorderPane(VboxRicerca);
        gridVini = (HBox) parent.lookup("#gridVini");
        gridVini.getChildren().clear();
        gridVini.getChildren().add(listView);
    }

    // quanto il cliente clicca il pulsante continua viene richiamato seleziona()
    // che a ua volta chiama il metodo corrispondente alla scelta dall'utente
    @FXML
    protected  void seleziona(){
        //VERIFICO LA TIPOLOGIA DI CONTENITORE
        RadioButton selected = (RadioButton) TipologiaContenitore.getSelectedToggle();
        switch (selected.getText()) {
            case "RIEMPI CONFEZIONE" -> {
                addInConfezione();
            }
            case "RIEMPI CASSA DA 6" -> {
                addInCassa(6);
            }
            case "RIEMPI CASSA DA 12" -> {
                addInCassa(12);
            }
        }
    }
    // aggiunge le bottiglie selezionate in confezioni da 1 a 5 vini ciascuna
    // se il cliente ha selezionato per uno stesso vino più di 5 bottiglie
    // creo delle casse da 6 non scontate fino a che il numero totale di unità rimanenti della bottiglia < 5
    private void addInConfezione(){
        int i=0, j=0, qnt, tot=0, capacita;
        boolean pieno = false;
        ConfezioneVini confezione = new ConfezioneVini();
        List<Object> contenitoriSelezionati = SharedData.getInstance().getContenitori();
        //contenitoriSelezionati = new ArrayList<>();
        Vino vino = null;

        // inserisco i vini in confezioni e aggiungo il tutto a contenitoriSelezionati
        for (HBox riga:  listView.getItems()){
            i=0;
            // itero sui componenti di ciascuna riga della listView
            for (Node node : riga.getChildren()){
                // il primo elemento è il nome del vino
                if (i==0){vino = (Vino) node.getUserData();}
                else if (i==1) {
                    TextField elem = (TextField) node;
                    qnt = Integer.parseInt(elem.getText());
                    tot += qnt;

                    // meccanismo per creare confezioni di vini
                    if (qnt>0) {
                        SharedData.getInstance().setViniSelezionati(vino.getId(), qnt);
                        while (qnt > confezione.getCapacita()){
                            if (qnt>=6){
                                CassaVino cassa = new CassaVino(vino, 6, 0);
                                contenitoriSelezionati.add(cassa);
                                qnt -= 6;
                            }
                            else {
                                capacita = confezione.getCapacita();
                                confezione.aggiungiVino(vino, capacita);
                                qnt -= capacita;
                                contenitoriSelezionati.add(confezione);
                                confezione = new ConfezioneVini();
                            }
                        }
                        if (qnt>0) {
                            pieno = confezione.aggiungiVino(vino, qnt);
                        }
                    }
                    if (j==listView.getItems().size()-1 && !pieno) contenitoriSelezionati.add(confezione);
                    else if (pieno) {
                        contenitoriSelezionati.add(confezione);
                        confezione = new ConfezioneVini();
                    }
                    break;
                }
                // incremento i per indicare che passo all'elemento successivo
                i++;
            }
            // incemento j per tenere traccia del numero di riga in listView
            j++;
        }
        SharedData.getInstance().setContenitori(contenitoriSelezionati);
        revisione(viniDisponibili);
        azzeraQuantita();
    }

    // crea delle casse da 6 o 12 in base al radioButton selezionata
    private void addInCassa(int qnt){
        int i=0, cassePerVino=0;
        boolean pieno = false;
        Vino vino = null;
        List<Object> contenitoriSelezionati = SharedData.getInstance().getContenitori();

        for (HBox riga:  listView.getItems()){
            i=0;
            for (Node node : riga.getChildren()){
                if (i==0){vino = (Vino) node.getUserData();}
                if (i==1) {
                    TextField elem = (TextField) node;
                    cassePerVino = Integer.parseInt(elem.getText());
                    if (cassePerVino!=0) SharedData.getInstance().setViniSelezionati(vino.getId(), cassePerVino*qnt);
                    while (cassePerVino>0){
                        CassaVino cassa = new CassaVino(vino, qnt, 0);
                        contenitoriSelezionati.add(cassa);
                        cassePerVino--;
                    }
                }
                i++;
            }
        }
        SharedData.getInstance().setContenitori(contenitoriSelezionati);
        revisione(viniDisponibili);
        azzeraQuantita();
    }

    // azzera le quantità selezionate quando si clicca CONTINUA
    private void azzeraQuantita(){
        for (HBox riga:  listView.getItems()){
            for (Node node : riga.getChildren()){
                if("quantita".equals(node.getId())){
                    TextField elem = (TextField) node;
                    elem.setText("0");
                }
            }
        }
    }
    private void revisione(Node n){
        // REVISIONE ORDINE
        //System.out.println("-------------");
        //System.out.println("REVISIONE");
        //System.out.println(SharedData.getInstance().getUser().getEmail());
        int confezioni = 0, casse = 0;
        float prezzoTotale = 0;
        List<Object> contenitoriSelezionati = SharedData.getInstance().getContenitori();
        for (Object ob : contenitoriSelezionati){
            if (ob instanceof ConfezioneVini) {
                confezioni++;
                prezzoTotale += ((ConfezioneVini) ob).getPrezzo();
                //((ConfezioneVini) ob).visualizza();
            }
            else if (ob instanceof CassaVino) {
                casse++;
                prezzoTotale += ((CassaVino) ob).getPrezzo();
                //System.out.println("cassa "+((CassaVino) ob).getVino().getNome() +" - quantità :"+((CassaVino) ob).getQuantita());
            }
        }

        BorderPane main = find.findBorderPane(n);
        nConfezioni = (Label) main.lookup("#nConfezioni");
        nCasse = (Label) main.lookup("#nCasse");
        prezzo = (Label) main.lookup("#prezzo");

        nConfezioni.setText(String.valueOf(confezioni));
        nCasse.setText(String.valueOf(casse));
        prezzo.setText(String.format("%.2f", prezzoTotale)+"€");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // evito che venga inizializzato due volte: più fxml hanno stesso controller
        if (mainPaneInitialClient!=null) {
            SharedData.getInstance().setCurrentParent(mainPaneInitialClient);
        }
        //System.out.println("ciao");
    }
}
