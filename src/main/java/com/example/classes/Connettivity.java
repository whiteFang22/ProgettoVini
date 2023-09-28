package com.example.classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connettivity {
    private String serverAddress;
    private int serverPort;

    public Connettivity(String serverAddress, int serverPort) {
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
    public Response message( Request req) {
        Response response = null;
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(req.getMessaggio());
            objectOutputStream.writeObject(req);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            response = (Response) objectInputStream.readObject();

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static void main(String[] args) {
        Connettivity connettivity = new Connettivity("localhost", 12345);

        String route = "/example-route";
        Object[] data = { "Hello", 42, true };

        //connettivity.message(route, data);
    }
}