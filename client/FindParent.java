package com.example.client;

import javafx.scene.layout.Pane;

public class FindParent {
    private Pane parent;
    private Pane currentparent;

    public Pane findParent(String id, Pane child){
        currentparent = (Pane) child.getParent();
        while (!currentparent.getId().equals(id)) {
            currentparent = (Pane) child.getParent();
        }
        parent = currentparent;
        return parent;
    }
}
