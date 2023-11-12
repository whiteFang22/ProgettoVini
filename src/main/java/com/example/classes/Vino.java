package com.example.classes;

import java.util.List;
import com.google.gson.*;
import java.lang.reflect.Type;

import java.io.Serializable;

public class Vino implements Serializable{
    public int id;
    public String nome;
    public String produttore;
    public String provenienza;
    public int anno;
    public String noteTecniche;
    public List<String> vitigni;
    public float prezzo;
    public int numeroVendite;
    public int disponibilita;

    public Vino(String nome, String produttore, String provenienza, int anno, String noteTecniche,
                List<String> vitigni, float prezzo, int numeroVendite, int disponibilita) {
        this.nome = nome;
        this.produttore = produttore;
        this.provenienza = provenienza;
        this.anno = anno;
        this.noteTecniche = noteTecniche;
        this.vitigni = vitigni;
        this.prezzo = prezzo;
        this.numeroVendite = numeroVendite;
        this.disponibilita = disponibilita;
    }
    public Vino(int id, String nome,int anno, float prezzo){
        this.id = id;
        this.nome = nome;
        this.anno = anno;
        this.prezzo = prezzo;
    }
    public Vino(int id, String nome, String produttore, String provenienza, int anno, String noteTecniche,
                List<String> vitigni, float prezzo, int numeroVendite, int disponibilita) {
        this.id = id;
        this.nome = nome;
        this.produttore = produttore;
        this.provenienza = provenienza;
        this.anno = anno;
        this.noteTecniche = noteTecniche;
        this.vitigni = vitigni;
        this.prezzo = prezzo;
        this.numeroVendite = numeroVendite;
        this.disponibilita = disponibilita;
    }
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Vino.class, new VinoSerializer()).create();
        return gson.toJson(this, Vino.class);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProduttore() {
        return produttore;
    }

    public void setProduttore(String produttore) {
        this.produttore = produttore;
    }

    public String getProvenienza() {
        return provenienza;
    }

    public void setProvenienza(String provenienza) {
        this.provenienza = provenienza;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public String getNoteTecniche() {
        return noteTecniche;
    }

    public void setNoteTecniche(String noteTecniche) {
        this.noteTecniche = noteTecniche;
    }

    public List<String> getVitigni() {
        return vitigni;
    }

    public void setVitigni(List<String> vitigni) {
        this.vitigni = vitigni;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getNumeroVendite() {
        return numeroVendite;
    }

    public void setNumeroVendite(int numeroVendite) {
        this.numeroVendite = numeroVendite;
    }

    public int getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
    }
}
