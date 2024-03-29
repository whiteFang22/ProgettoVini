package com.example.client;

import com.example.classes.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LogFormControllers {
    @FXML
    private BorderPane mainPaneLog;
    @FXML
    private VBox loginVBox;
    @FXML
    private VBox registrationVBox;
    @FXML
    private Label loginMessageLable;
    @FXML
    private Label registrationMessage;
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
    protected void onRegistrationButtonClick() throws IOException {
        System.out.println("Registration");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/client-registration");
//      RECUPERO IL mainPaneLog
        BorderPane parent = (BorderPane) loginVBox.getParent();
        parent.setCenter(view);
//      AGGIUSTO LE DIMENSIONI DELLA WINDOW
        parent.getScene().getWindow().sizeToScene();
    }

    @FXML
    protected void onLoginButtonClick() {
        System.out.println("Login-form");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("general/login-form");
        BorderPane parent = (BorderPane) registrationVBox.getParent().getParent();
        parent.setCenter(view);
        parent.getScene().getWindow().sizeToScene();
    }

    /*
        controllo il tipo di utente selezionato col menù per il login
        estrapolo i dati dal form
        chiamo il metodo di login dell'utente specifico
     */
    @FXML
    protected void aceesso() {
        System.out.println("Login");
        BorderPane parent = (BorderPane) loginVBox.getParent();
        Stage stage = (Stage) loginVBox.getScene().getWindow();

        UtenteGenerico user = SharedData.getInstance().getUser();
        String userType = SharedData.getInstance().getUserType();

        user.setEmail(email.getText());
        user.setpasswordhash(password.getText());
        Response res = user.login();

        System.out.println(res.isSuccess());

        boolean success = res.isSuccess();

        System.out.println(email.getText() +" "+ password.getText());
        if (success)
        {
            switch (userType) {
                case "cliente" -> user = (Cliente) res.getData();
                case "impiegato" -> user = (Impiegato) res.getData();
                case "amministratore" -> user = (Amministratore) res.getData();
            }
            user.setAuthCode(res.getAuthCode());

            // RESTITUISCE LA SCHERMATA HOME DEL PROGRAMMA
            SharedData.getInstance().setUser(user);
            System.out.println(SharedData.getInstance().getUser());
            //Impiegato imp = new Impiegato( null,null,null,null,null,null,null );
            //SharedData.getInstance().setUser(imp);
            GetStage obj = new GetStage();
            obj.set(stage, userType);
        }
        else
        {
            //stampa un messaggio di errore a schermo
            System.out.println("errore di autenticazione");
            loginMessageLable.setText("errore di autenticazione");
            SharedData.getInstance().resetInstance(true);
        }
    }

    @FXML
    protected  void registrazione(){
        System.out.println("Registrazione");
        BorderPane parent = (BorderPane) registrationVBox.getParent().getParent();
        Stage stage = (Stage) registrationVBox.getScene().getWindow();

        //Ho aggiunto il campo password.getText() per istanziare gia l'utente con la password in modo che poi sia sufficiente
        //chiamare c.registrazione(), credo sia da implementare in front end 
        Cliente c = new Cliente(nome.getText(), cognome.getText(),password.getText(), codiceFiscale.getText(), email.getText(), numTelefonico.getText(), indirizzo.getText(),false);
        SharedData.getInstance().setUser(c);
        boolean success = c.registrazione();

        if (success){
            GetStage obj = new GetStage();
            obj.set(stage, "cliente");
        }
        else {
            System.out.println("registration error");
            registrationMessage.setText("prova con un'altra email");
        }
    }
}
