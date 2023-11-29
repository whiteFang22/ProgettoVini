package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class ControllerOrdine {
    @FXML
    private Button avantiButton;
    final FindParent find = new FindParent();

    @FXML
    protected void onConfermaOrdineClick(){
        System.out.println("Acquista");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/scheda-bonifico");

        BorderPane parent = find.findBorderPane(avantiButton);
        parent.setCenter(view);

        //devo abilitare i
        RadioButton radio = (RadioButton) parent.lookup("#bonificoChoice");
        radio.setDisable(false);
        radio = (RadioButton) parent.lookup("#cartaChoice");
        radio.setDisable(false);
    }
}
