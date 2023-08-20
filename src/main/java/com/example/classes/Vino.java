package com.example.classes;

import java.util.List;

public class Vino {
    private String nome;
    private String produttore;
    private String provenienza;
    private int anno;
    private String noteTecniche;
    private List<String> vitigni;
    private float prezzo;
    private int numeroVendite;
    private int disponibilita;
    private String id;


    public Vino(String nome, String produttore, String provenienza, int anno, String noteTecniche,
                List<String> vitigni, float prezzo, int numeroVendite, int disponibilita, String id) {
        this.nome = nome;
        this.produttore = produttore;
        this.provenienza = provenienza;
        this.anno = anno;
        this.noteTecniche = noteTecniche;
        this.vitigni = vitigni;
        this.prezzo = prezzo;
        this.numeroVendite = numeroVendite;
        this.disponibilita = disponibilita;
        this.id = id;
    }

    public String getNome() {return nome;}

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProduttore() {return produttore;}

    public void setProduttore(String produttore) {
        this.produttore = produttore;
    }

    public String getProvenienza() {return provenienza;}

    public void setProvenienza(String provenienza) {
        this.provenienza = provenienza;
    }

    public int getAnno() {return anno;}

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public String getNoteTecniche() {return noteTecniche;}

    public void setNoteTecniche(String noteTecniche) {
        this.noteTecniche = noteTecniche;
    }

    public List<String> getVitigni() {return vitigni;}

    public void setVitigni(List<String> vitigni) {
        this.vitigni = vitigni;
    }

    public float getPrezzo() {return prezzo;}

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getNumeroVendite() {return numeroVendite;}

    public void setNumeroVendite(int numeroVendite) {
        this.numeroVendite = numeroVendite;
    }

    public int getDisponibilita() {return disponibilita;}

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId() {return id;}
}
