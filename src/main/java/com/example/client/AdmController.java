package com.example.client;

import com.example.classes.Amministratore;
import com.example.classes.Cliente;
import com.example.classes.Impiegato;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
    private Label registrationMessage;


    @FXML
    protected  void registrazione(){
        System.out.println("Registrazione");
        BorderPane parent = SharedData.getInstance().getCurrentParent();
        Stage stage = (Stage) parent.getScene().getWindow();
        Amministratore adm = (Amministratore) SharedData.getInstance().getUser();

        //Ho aggiunto il campo password.getText() per istanziare gia l'utente con la password in modo che poi sia sufficiente
        //chiamare c.registrazione(), credo sia da implementare in front end
        Impiegato i = new Impiegato(nome.getText(), cognome.getText(),password.getText(), codiceFiscale.getText(), email.getText(), numTelefonico.getText(), indirizzo.getText());
        boolean success = adm.registrazioneImpiegato(i, password.getText());

        if (success){
            GetStage obj = new GetStage();
            obj.set(stage, "amministratore");
        }
        else {
            System.out.println("registration error");
            registrationMessage.setText("prova con un'altra email");
        }
    }
}
