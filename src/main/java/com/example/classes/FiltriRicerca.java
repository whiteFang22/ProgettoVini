package com.example.classes;

import java.io.Serializable;
import java.util.Date;

public record FiltriRicerca(Date data1, Date data2, String nome, Integer annoProduzione) implements Serializable {
}