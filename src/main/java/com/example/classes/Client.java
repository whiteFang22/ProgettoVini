package com.example.classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        Cliente examplecliente = new Cliente("andrea","rossi","1234", "ndrvrd87g12f463x","andrerossi@gmail.com", "1234","Via averdi",true);
        //examplecliente.registrazione();
        examplecliente.login();
        //examplecliente.ClientModificaCredenziali("1234");
        
        FiltriRicerca fr = new FiltriRicerca(null, null, null, 2012);
        //examplecliente.cercaVini(fr);
        Map<Integer, Integer> list = new HashMap();
        list.put(4,15);
        //list.put(2,10);
        //list.put(6,1);


        //examplecliente.acquistaBottiglie(list);
        //examplecliente.proponiAcquisto(true);
        examplecliente.confermaPagamento(true);

        //Amministratore exampleAdmin = new Amministratore("1234","admin","admin","admin","root@admin.admin","0000","none",true);
         
        //Impiegato exampleImpiegato = new Impiegato("1111", "andrea", "martini", "ndrmrt88g655f", "amartini@gmail.com", "3421234554", "Via Roma 33", true);
        //exampleImpiegato.login();
        /*
        OrdineVendita work = (OrdineVendita) exampleImpiegato.recuperaOrdineVendita(10).getData();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.DECEMBER, 1);
        Date date = calendar.getTime();
        work.setDataConsegna(date);
        exampleImpiegato.gestioneOrdineVendita(work);
        */
        //exampleAdmin.registrazioneImpiegato(exampleImpiegato, "1111");
        //exampleAdmin.AdminModificaCredenziali("dverdi@gmail.com","0000" , false);
        //exampleAdmin.AdminModificaCredenziali("everdi@gmail.com", null, true);

        //System.out.println(exampleImpiegato.ricercaClienti("rossi"));
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(2023, Calendar.NOVEMBER, 1); // Adjust year, month, and day

        // Convert Calendar to Date
        //Date Date1 = calendar.getTime();
        //calendar.set(2023,Calendar.DECEMBER,1);
        //Date Date2 = calendar.getTime();
        //System.out.println(exampleImpiegato.ricercaOrdiniVendita(Date1,Date2));
        /* 
        OrdineAcquisto oa = exampleImpiegato.recuperaOrdineAcquisto(10);
        System.out.println(oa);
        exampleImpiegato.gestioneOrdineAcquisto(oa);
        */
        //System.out.println(exampleImpiegato.ricercaOrdiniAcquisto(Date1, Date2));


    }
}
