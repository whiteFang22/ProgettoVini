package com.example.client;

import com.example.classes.CassaVino;
import com.example.classes.ConfezioneVini;
import com.example.classes.UtenteGenerico;
import com.example.classes.Vino;
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

public class RicercaController {
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
    private ToggleGroup TipologiaContenitore;

    final FindParent find = new FindParent();
    private static ListView<HBox> listView;


    @FXML
    protected void onAcquistaButton(){
        System.out.println("Acquista");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-bonifico");

        BorderPane parent = find.findBorderPane(VboxRevisione);
        parent.setCenter(view);

        object = new FxmlLoader();
        view = object.getPage("cliente/left-pagamento");
        parent.setLeft(view);

    }
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
        if (!annoProduzione.getText().equals("")){
            anno = Integer.parseInt(annoProduzione.getText());
        }
        List<Vino> listaVini = user.cercaVini(nomeVino.getText(), anno);

        // crea una tabella e per ogni riga aggiungi un vino
        listView = new ListView<>();
        listView.setId("listaVini");
        ObservableList<HBox> items = FXCollections.observableArrayList();

        int i = 0;
        for (Vino vino : listaVini) {
            TextField nomeVinotext = new TextField(vino.getNome()+" - "+vino.getAnno());
            nomeVinotext.setUserData(vino);
            nomeVinotext.setMinWidth(100);
            nomeVinotext.setEditable(false);

            TextField quantitaTextField = new TextField("0");
            quantitaTextField.setId("quantita");
            quantitaTextField.setEditable(false);
            quantitaTextField.setPrefColumnCount(2);

            Text prezoUnitario = new Text("prezzo unitario: "+Double.toString(vino.getPrezzo())+"€");

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

            HBox riga = new HBox(nomeVinotext, quantitaTextField, incrementaButton, decrementaButton, prezoUnitario);
            riga.setSpacing(10);
            items.add(riga);
        }

        listView.setItems(items);
        listView.setMinWidth(430);
        listView.setMinHeight(200);
        listView.setSelectionModel(null);

        BorderPane main = find.findBorderPane(VboxRicerca);
        gridVini = (HBox) main.lookup("#gridVini");
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
        revisione();
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
        revisione();
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
    private void revisione(){
        // REVISIONE ORDINE
        azzeraQuantita();
        System.out.println("-------------");
        System.out.println("REVISIONE");
        System.out.println(SharedData.getInstance().getUser().getEmail());
        int confezioni = 0, casse = 0;
        float prezzoTotale = 0;
        List<Object> contenitoriSelezionati = SharedData.getInstance().getContenitori();
        for (Object ob : contenitoriSelezionati){
            if (ob instanceof ConfezioneVini) {
                confezioni++;
                prezzoTotale += ((ConfezioneVini) ob).getPrezzo();
                ((ConfezioneVini) ob).visualizza();
            }
            else if (ob instanceof CassaVino) {
                casse++;
                prezzoTotale += ((CassaVino) ob).getPrezzo();
                System.out.println("cassa "+((CassaVino) ob).getVino().getNome() +" - quantità :"+((CassaVino) ob).getQuantita());
            }
        }
        System.out.println("prezzo: "+ prezzoTotale);
        System.out.println("n° confezioni: "+ confezioni);
        System.out.println("n° casse: "+ casse);
    }
}
