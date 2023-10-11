package com.example.classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Client implements Serializable {
    private String serverAddress;
    private int serverPort;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /*
     Questo metodo sarà implementato nei metodi delle classi utente per
     poter effettuare richieste al server.

     Il server potrebbe restituire un oggetto della classe Response la quale contiene tanti attributi (tutti
     i possibili tipi di risultati che il server restituisce) che vengono settati lato server.
     In questo modo posso sempre ricevere dal server lo stesso oggetto che poi restituisco a chi ha chiamato
     il metodo.

     Possibili attributi della classe Responde:
     vini List<Vini>, messaggio String, List<OrdineVendita> ordiniVendita, List<OrdineAcquisto> ordiniAcquisto
     List<PropostaAcquisto> proposteAcquisto.
     Ogniuno di questi deve avere un rispettivo setter e getter. I setter saranno usati lato server
     mertre i getter lato client, rispettivamente per inserire ed estrapolare da responde solo
     quello che mi serve nella/dalla oggetto della classe Response.
    */
    public Response message(Request request) {
        Response response = null;
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            //delay
            System.out.println("Client sent request id: " + request.getId());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            response = (Response) objectInputStream.readObject();
            System.out.println("Got back from server id: " + response.getId());
            return response;
        } catch (Exception e) {
            System.out.println("Comunication Error");
            e.printStackTrace();
            return response;
        }
    }

    public static void main(String[] args) {
        Cliente examplecliente = new Cliente("andrea","verdi","1111", "ndrvrd87g12f463x","averdi@gmail.com", "123321","Via averdi");
        //examplecliente.registrazione();
        //examplecliente.login();
        //examplecliente.ClientModificaCredenziali("1111");
        Map<Integer,Integer> bottiglielist = new HashMap();
        bottiglielist.put(6,2);
        examplecliente.acquistaBottiglie(bottiglielist);
    }
}