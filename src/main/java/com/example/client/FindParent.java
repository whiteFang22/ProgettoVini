package com.example.client;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FindParent {
    private Pane parent;
    private Pane currentparent;

    public BorderPane findBorderPane(Node child){
        boolean found = false;
        Parent current = child.getParent();
        while (!found){
            if (current instanceof BorderPane){
                found = true;
            }
            else current = current.getParent();
        }

        return (BorderPane) current;
    }

    public Stage findStage(Node child){
        return (Stage) child.getScene().getWindow();
    }
}
