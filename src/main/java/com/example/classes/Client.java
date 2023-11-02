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

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.open();
    }

    public void open() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            System.out.println("Error opening socket");
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
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

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            // Delay
            System.out.println("Client sent request id: " + request.getId());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            response = (Response) objectInputStream.readObject();
            System.out.println("Got back from the server id: " + response.getId());
            return response;
        } catch (Exception e) {
            System.out.println("Communication Error");
            e.printStackTrace();
            return response;
        }
    }

    public static void main(String[] args) {
        Cliente examplecliente = new Cliente("andrea","verdi","1111", "ndrvrd87g12f463x","ferdi@gmail.com", "123321","Via averdi");
        examplecliente.registrazione();
        //examplecliente.login();
    }
}
