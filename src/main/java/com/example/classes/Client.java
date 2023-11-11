package com.example.classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Client implements AutoCloseable{
    private String serverAddress;
    private int serverPort;
    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.open();
    }

    public void open() {
        try {
            socket = new Socket(serverAddress, serverPort);

            System.out.println("socket open");
            this.os = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("OS open");
            this.is =  new ObjectInputStream(socket.getInputStream());
            System.out.println("IS open");
            is.readObject();

        } catch (IOException e) {
            System.out.println("Error opening socket");
            e.printStackTrace();
        }

        catch (Exception e){
            System.out.println("Error opening socket");
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
              
                os.close();
                is.close();

            }
        } catch (IOException e) {
            System.out.println("Error closing socket");
            e.printStackTrace();
        }
    }

    public Response message(Request request) {
        Response response = null;
        try {
            if (socket == null || socket.isClosed()) {
                throw new IllegalStateException("Socket is not open. Call open() before sending a message.");
            }

            os.writeObject(request);
            os.flush();
            // Delay
            System.out.println("Client sent request id: " + request.getId());
            response = (Response) is.readObject();

            System.out.println("Got back from the server id: " + response.getId());
            return response;
        } catch (Exception e) {
            System.out.println("Communication Error");
            e.printStackTrace();
            return response;
        }
    }

    public static void main(String[] args) {

        Cliente examplecliente = new Cliente("andrea","verdi","12344321", "ndrvrd87g12f463x","fcdecardelli@gmail.com", "123321","Via averdi",true);
        //examplecliente.registrazione();
        examplecliente.login();
        //examplecliente.ClientModificaCredenziali("1234");
        
        //FiltriRicerca fr = new FiltriRicerca(null, null, null, 2012);
        //examplecliente.cercaVini(fr);

        Map<Integer, Integer> list = new HashMap();
        list.put(1,10);

        examplecliente.acquistaBottiglie(list);
        examplecliente.confermaPagamento(true);

    }
}
