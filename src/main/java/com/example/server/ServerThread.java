package com.example.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.example.classes.*;

//import com.example.classes.Response;

/**
 *
 * The class {@code ServerThread} manages the interaction
 * with a client of the server.
 *
**/

public class ServerThread implements Runnable
{
  private static final int MAX = 100;
  private static final long SLEEPTIME = 2000;
  private static final int SOGLIA_VINI = 50;    //Soglia disponibilita vini

  private Server server;
  private Socket socket;
  private DBHandler db;
  private String connectionAuthCode = null;
  private Cliente loggedCliente = null;
  private Impiegato loggedImpiegato = null;
  private Amministratore loggedAmministratore = null;
  private Boolean stayOpen = true;

  /**
   * Class constructor.
   *
   * @param s  the server.
   * @param c  the client socket.
   *
  **/
  public ServerThread(final Server s, final Socket c)
  {
    this.server = s;
    this.socket = c;
    //connect to database
    this.db = new DBHandler();
  }

  @Override
  /*
   * thread main code, handles client requests via Switch case statement switching request codes
   * 0 - Cliente, registrazione
   * 1 - UtenteGenerico, Login
   * 2 - Cliente, modifica credenziali
   * 3 - Cliente, acquistabottiglie [1:ok] [2: ok, non tutto disponibile] [3:ordine ancora da completare, completa o elimina (tramite confermaPagamento(false))]
   * 4 - Cliente, confermaPagamento [1:ok conferma] [2:ok elimina]
   * 5 - Cliente, proponiAcquisto
   * 6 - Cliente, ricercaOrdiniVendita
   * 9 - Cliente, ricercaVino               tested    ok!
   * 10 - Impiegato, ricercaCliente         tested
   * 11 - Impiegato, ricercaOrdineVendita   tested    ok!
   * 12 - Impiegato, ricercaOrdineAcquisto
   * 33 - Impiegato, recuperaOrdineAcquisto
   * 13 - Impiegato, gestisciOrdineAcquisto
   * 34 - Impiegato, recuperaOrdineVendita
   * 14 - Impiegato, gestisciOrdineVendita
   * 15 - Impiegato, ricercaProposteAcquisto          ok!
   * 20 - Admin, registraImpiegato          tested    ok!
   * 21 - Admin, eliminaUtente              tested    ok!
   * 22 - Admin, modificaCredenzialiUtente  tested    ok!
   * 23 - Admin, preparazioneReport
   * 24 - Admin, ricercaImpiegati
  */
  public void run()
  {
    ObjectInputStream  is = null;
    ObjectOutputStream os = null;
    //build input/output stream from socket
    try
    { 
      is = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));    //is readobject()
      os = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));   //os writeobject()-flush()
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return;
    }
    //Empty message to open stream
    message(null,os);
    //main loop
    while (stayOpen)
    { 
      try
      {
        //Read Client Request
        Object objReq = is.readObject();
        if(objReq != null && objReq instanceof Request){
          //request unpack
          final Request crequest = (Request) objReq;
          final int requestId = crequest.getId();
          final Object requestData = crequest.getData();
          final String clientAuthCode = crequest.getAuthCode();
          int rowsAffected = 0;
          Response response = new Response();

          //request Handle
          switch (requestId) {
            case 0 -> {
              /*
               quando cerco di registrarmi con una mail già esistente si genera giustamente un errore ma poi,
               invece che restituire res con isSuccess=false il server si stoppa
               Lo stesso va fatto in registraImpiegato

              F: Dovrebbe essere OK ora
              */
              System.out.println("Got from client request id: " + requestId);
              //Registrazione Cliente, requestData instance of Cliente
              if (requestData != null && requestData instanceof Cliente) {
                Cliente user = (Cliente) requestData;
                System.out.println("Request OK, contacting db");

                String dbquery = "INSERT INTO clienti (nome, cognome, password_hash, codice_fiscale, email, numero_telefonico, indirizzo_consegna) VALUES (?,?,?,?,?,?,?)";

                try {
                  rowsAffected = db.executeUpdate(dbquery, user.getNome(), user.getCognome(), user.getPasswordhash(), user.getCodiceFiscale(), user.getEmail(), user.getNumeroTelefonico(), user.getIndirizzoDiConsegna());
                  System.out.println("query Executed " + rowsAffected + " rows Affected");
                  response.set(1, null, null); //non dovrebbe darmi authCode?
                  response.setSuccess();
                } catch (SQLException e) {
                  response.set(0, null, null);

                  e.printStackTrace();

                } finally {
                  message(response, os);
                }
              }
            }
            case 1 -> {
              // Login Utente, requestData instance of Cliente/Impiegato/Amministratore
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof UtenteGenerico) {
                //Login Cliente
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti where email = ?; ";
                ResultSet resultset = db.executeQuery(dbquery, user.getEmail());

                if (resultset.next()) {
                  System.out.println("User found, checking password");
                  String passwordhash = resultset.getString("password_hash");
                  System.out.println(user.getPasswordhash());
                  System.out.println(passwordhash);
                  if (passwordhash.equals(user.getPasswordhash())) {
                    System.out.println("password match");
                    /* bisogna fare distinzione se chi chiama il metodo è cliente, impiegato o amministratore
                       per farlo senza dover modificare ulterirmente il nostro codice scorri nella tabella
                       clienti e se non trovi scorri in impiegati. Se trovi in impiegati verifica il campo isAdmin
                       per capire se creare un impiegato o un amministratore
                       RISOLTO
                       */
                    Cliente loggeduser = new Cliente(resultset.getString("nome"), resultset.getString("cognome"), user.getPasswordhash(), resultset.getString("codice_fiscale"), resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_consegna"), false);
                    final String authCode = AuthCodeGenerator.generateAuthCode();
                    this.connectionAuthCode = authCode;
                    System.out.println(authCode);
                    System.out.println("loggedUser: " + loggeduser);
                    this.loggedCliente = loggeduser;
                    System.out.println("loggedCliente: " + loggedCliente);
                    response.setId(1);
                    response.setAuthCode(authCode);
                    response.setData(loggeduser);
                    response.setSuccess();
                  } else {
                    //Found but wrong password
                    response.setId(0);
                    response.setAuthCode(null);
                    response.setData(user);
                    System.out.println("wrong password");
                  }
                } else {
                  //Not Found in Clienti look in impiegati
                  System.out.println("User not found in table clienti");

                  dbquery = "SELECT * FROM impiegati where email = ?; ";
                  resultset = db.executeQuery(dbquery, user.getEmail());

                  if (resultset.next()) {
                    System.out.println("User found, checking password");
                    String passwordhash = resultset.getString("password_hash");
                    System.out.println(user.getPasswordhash());
                    System.out.println(passwordhash);

                    if (passwordhash.equals(user.getPasswordhash())) {
                      System.out.println("password match");
                      Boolean isAdmin = resultset.getBoolean("isAdmin");
                      if (isAdmin) {
                        //Amministratore
                        Amministratore loggeduser = new Amministratore(user.getPasswordhash(), resultset.getString("nome"),
                                resultset.getString("cognome"), resultset.getString("codice_fiscale"),
                                resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_residenza"), false);
                        final String authCode = AuthCodeGenerator.generateAuthCode();
                        this.connectionAuthCode = authCode;
                        System.out.println(authCode);
                        this.loggedAmministratore = loggeduser;
                        response.setId(1);
                        response.setAuthCode(authCode);
                        response.setData(loggeduser);
                        response.setSuccess();
                      } else {
                        //Impiegato
                        Impiegato loggeduser = new Impiegato(user.getPasswordhash(), resultset.getString("nome"),
                                resultset.getString("cognome"), resultset.getString("codice_fiscale"),
                                resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_residenza"), false);
                        final String authCode = AuthCodeGenerator.generateAuthCode();
                        this.connectionAuthCode = authCode;
                        System.out.println(authCode);
                        this.loggedImpiegato = loggeduser;
                        response.setId(1);
                        response.setAuthCode(authCode);
                        response.setData(loggeduser);
                        response.setSuccess();
                      }
                    } else {
                      //wrong password
                      response.setId(0);
                      response.setAuthCode(null);
                      response.setData(user);
                      System.out.println("wrong password");
                    }
                  } else {
                    //Not Found
                    response.setId(0);
                    response.setAuthCode(null);
                    response.setData(user);
                    System.out.println("User not Found");
                  }
                }
              } else {
                //Invalid Request
                response.setId(0);
                response.setAuthCode(null);
                response.setData(null);
              }
              message(response, os);
            }
            case 2 -> {
              System.out.println("Got from client request id: " + requestId);
              //Modifica password
              if (requestData != null && requestData instanceof Cliente && clientAuthCode.equals(connectionAuthCode)) {
                Cliente cliente = (Cliente) requestData;

                String query = "UPDATE clienti SET password_hash = ? WHERE email = ?;";

                rowsAffected = db.executeUpdate(query, cliente.getPasswordhash(), cliente.getCodiceFiscale());
                System.out.println("query Executed " + rowsAffected + " rows Affected");
                response.set(1, null, null);
                response.setSuccess();
                message(response, os);
              } else {
                System.out.println("Something went wrong");
                response.set(0, null, null);
                message(response, os);
              }
            }
            case 3 -> {
              System.out.println("Got from client request id: " + requestId);
              //Acquista Bottiglie
              if (requestData != null && requestData instanceof Map<?, ?> && clientAuthCode.equals(connectionAuthCode)) {
                //Check constrain -> only one ordinevendita not complete at any given time
                String query = "SELECT * FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                ResultSet resultSet = db.executeQuery(query, this.loggedCliente.getEmail());

                if(!resultSet.next()){

                  Map<Integer, Integer> bottiglieList = (Map<Integer, Integer>) requestData;
                  Map<Vino, Integer> viniPresenti = new HashMap<>();
                  Map<Vino, Integer> viniMancanti = new HashMap<>();
                  boolean allInStock = true;


                  //Initialize OrdineVendita
                  System.out.println("cliente: " + loggedCliente);
                  //Calculate Time
                  Date now = new Date();
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(now);
                  calendar.add(Calendar.WEEK_OF_YEAR, 1);
                  Date nextWeek = calendar.getTime();

                  OrdineVendita ordineVendita = new OrdineVendita(loggedCliente, nextWeek, now);

                  for (Map.Entry<Integer, Integer> line : bottiglieList.entrySet()) {
                    Integer idVino = line.getKey();
                    Integer quantitaRichiesta = line.getValue();
                    String dbquery = "SELECT * FROM vini where id = ?";
                    resultSet = db.executeQuery(dbquery, idVino);
                    Vino vino;
                    if (resultSet.next()) {
                      vino = rebuildVinoFromResultSet(resultSet);

                      if (vino.getDisponibilita() >= quantitaRichiesta) {
                        //vino disponibile, aggiungi a OrdineVendita
                        viniPresenti.put(vino, quantitaRichiesta);
                      } else {
                        //vino non disponibile o non abbastanza bottiglie in magazzino, creo Map viniMancanti
                        //Inserisco nell'ordineVendita anche i vini che non sono disponibili a magazzino!!
                        //Mentre in proposta solo quelli che mancano!!
                        viniPresenti.put(vino, quantitaRichiesta);
                        viniMancanti.put(vino, quantitaRichiesta - vino.getDisponibilita());
                        allInStock = false;
                      }
                    }
                  }
                  ordineVendita.setCompletato(false);
                  ordineVendita.setViniAcquistati(viniPresenti);

                  dbStoreOrdineVendita(ordineVendita);

                  if (allInStock) {
                    //tutti i vini disponibili, finalizza OrdineVendita e salva in db
                    response.set(1, ordineVendita, this.connectionAuthCode);
                    response.setSuccess();
                  } else {
                    //Non tutti i vini sono disponibili in quantita sufficienti, creo proposta di acquisto se non ne esiste gia una in elaborazione

                      PropostaAcquisto propostaAcquisto = new PropostaAcquisto(this.loggedCliente, viniMancanti, this.loggedCliente.getIndirizzoDiConsegna(), ordineVendita);

                      String dbquery2 = "SELECT * FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                      resultSet = db.executeQuery(dbquery2, ordineVendita.getCliente().getEmail());
                      int id = 0;
                      if (resultSet.next()) {
                        id = resultSet.getInt("id");
                      }
                      String dbquery3 = "INSERT INTO proposte_di_acquisto (cliente_id, lista_quantita, ordine_id, completato) " +
                              "VALUES (?, ?, ?, ?)";

                      //String lista_quantita_mancanti = mapgson.toJson(propostaAcquisto.getVini());
                      String lista_quantita_mancanti = mapVinoToIdSerialized(propostaAcquisto.getVini());

                      db.executeUpdate(dbquery3, propostaAcquisto.getCliente().getEmail(), lista_quantita_mancanti, id, 0);

                      //response code 2, data: PropostaAcquisto
                      response.set(2, propostaAcquisto, this.connectionAuthCode);
                      response.setSuccess();
                }

              }
              else {
                response.set(3,null, this.connectionAuthCode);
              }
              message(response, os);
            }
            }
            case 4 -> {
              //Conferma Pagamento
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof Boolean && clientAuthCode.equals(connectionAuthCode)) {
                Boolean conferma = (Boolean) requestData;
                if (conferma) {
                  //Conferma = true -> invia a impiegato

                  //se conferma è true invia copia ordine a un impiegato
                  //Aggiornamento quantità magazzino
                  String dbquery = "SELECT * from ordini_vendita WHERE cliente_id = ? and completato = 0";
                  ResultSet resultset = db.executeQuery(dbquery, this.loggedCliente.getEmail());
                  if (resultset.next()) {
                    //Get lista_quantità from ordine_vendita and parse into Map
                    //Map<Vino, Integer> wineList = mapgson.fromJson(resultset.getString("lista_quantita"), mapType);
                    Gson gson = new Gson();
                    Type mapType = new TypeToken<Map<Integer, Integer>>() {
                    }.getType();
                    Map<Integer, Integer> wineList = gson.fromJson(resultset.getString("lista_quantita"), mapType);
                    Date dataCreazione = resultset.getDate("data_creazione");
                    Map<Vino, Integer> listaQuantita = rebuildFromMapIntInt(wineList);
                    int id = resultset.getInt("id");
                    OrdineVendita ordineVendita = new OrdineVendita(id, loggedCliente, listaQuantita, null, dataCreazione);

                    //Controllo se i vini sono ancora disponibili o se nel frattempo sono stati acquistati da altri
                    System.out.println(checkAvability(wineList));
                    if(checkAvability(wineList)){
                    //Invio copia a impiegato su queue
                    this.server.queuePut(ordineVendita);

                    //Update disponibilita
                    Map<Vino, Integer> wineListVino = rebuildFromMapIntInt(wineList);
                    for (Map.Entry<Vino, Integer> row : wineListVino.entrySet()) {
                      Vino wine = row.getKey();
                      int quantity = row.getValue();

                      wine.setDisponibilita(wine.getDisponibilita() - quantity);
                      wine.setNumeroVendite(wine.getNumeroVendite() + quantity);
                      dbquery = "UPDATE vini SET disponibilita = ?, numero_vendite = ? WHERE id = ?";
                      db.executeUpdate(dbquery, wine.getDisponibilita(), wine.getNumeroVendite(), wine.getId());

                    }

                    //Set Completato
                    dbquery = "UPDATE ordini_vendita SET completato = 1 WHERE id = ? AND completato = 0";
                    db.executeUpdate(dbquery, id);

                    //Verifica Soglia disponibilita
                    verificaSoglia(SOGLIA_VINI);

                    response.set(1, ordineVendita, this.connectionAuthCode);
                    response.setSuccess();
                  }
                  else{
                    //disponibilità cambiate, vini non piu presenti!s
                    response.set(0,null,connectionAuthCode);
                  }
                  } else {
                    //ERRORE, non ce l'ordine
                    response.set(0, null, this.connectionAuthCode);
                  }

                } else {
                  //conferma = false
                  String dbquery = "DELETE FROM proposte_di_acquisto WHERE cliente_id = ? AND completato = 0";
                  db.executeUpdate(dbquery, this.loggedCliente.getEmail());
                  dbquery = "DELETE FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                  db.executeUpdate(dbquery, this.loggedCliente.getEmail());
                  response.set(2,null,connectionAuthCode);
                  response.setSuccess();

                }
                message(response, os);
              }
            }
            case 5 -> {
              System.out.println("Got from client request id: " + requestId);
              //Cliente proponiAcquisto
              if (requestData != null && requestData instanceof Boolean && clientAuthCode.equals(connectionAuthCode)) {
                Boolean conferma = (Boolean) requestData;
                String query = "SELECT * FROM proposte_di_acquisto WHERE Cliente_id = ? AND completato = 0";
                ResultSet resultSet = db.executeQuery(query, this.loggedCliente.getEmail());

                if (resultSet.next()) {
                  if (conferma) {
                  //Get Objects from db
                    query = "SELECT pa.*, ov.*, cl.* FROM proposte_di_acquisto pa INNER JOIN ordini_vendita ov ON pa.ordine_id = ov.id INNER JOIN clienti cl ON pa.cliente_id = cl.email WHERE pa.cliente_id = ? AND pa.completato = 0;";
                    resultSet = db.executeQuery(query, this.loggedCliente.getEmail());

                    if (resultSet.next()) {
                      PropostaAcquisto propostaAcquisto = dbGetPropostaAcquisto(resultSet);
                      OrdineAcquisto ordineAcquisto = new OrdineAcquisto(null, propostaAcquisto, propostaAcquisto.getIndirizzoConsegna(), new Date());

                      //STORE
                      //query = "INSERT INTO ordini_di_acquisto (cliente_id,impiegato_id, proposta_associata_id, indirizzo_azienda, data_creazione, completato) VALUES (?,0,?,?,?,0)";
                      //db.executeUpdate(query,ordineAcquisto.getCliente().getEmail(), resultSet.getString("pa.id"),"", ordineAcquisto.getDataCreazione());

                      //Cliente conferma
                      query = "UPDATE proposte_di_acquisto SET completato = 1 WHERE cliente_id = ? AND completato = 0";
                      db.executeUpdate(query, this.loggedCliente.getEmail());

                      //Post OrdineAcquisto into Queue for worker to handle
                      this.server.queuePut(ordineAcquisto);
                      System.out.println("queuePutted");

                      response.set(1, null, this.connectionAuthCode);
                      response.setSuccess();
                    }
                  } else {
                    //Cliente annulla
                    query = "DELETE FROM proposte_di_acquisto WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(query, this.loggedCliente.getEmail());
                    query = "DELETE FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(query, this.loggedCliente.getEmail());

                    response.set(1, null, this.connectionAuthCode);
                    response.setSuccess();
                  }
                } else {
                  //nessuna proposta di acquisto in attesa di conferma
                  response.set(0, null, this.connectionAuthCode);
                }
              } else {
                response.set(0, null, this.connectionAuthCode);
              }
              message(response, os);
            }
            case 6 -> {
              System.out.println("Got from client request id: " + requestId);
              //ricerca Ordini vendita Firmati
              if (requestData instanceof FiltriRicerca) {
                String query = "Select * from ordini_vendita WHERE cliente_id = ? AND firmato = 1";
                ResultSet resultSet = db.executeQuery(query, this.loggedCliente.getEmail());
                List<OrdineVendita> list = new ArrayList();
                while (resultSet.next()) {
                  OrdineVendita ordineVendita = dbGetOrdineVendita(resultSet);
                  list.add(ordineVendita);
                }
                response.set(1, list, this.connectionAuthCode);
                response.setSuccess();
              }
            }
            case 9 -> {
              // non ti passo vino ma FiltriRicerca
              System.out.println("Got from client request id: " + requestId);
              //Ricerca Vino nel database
              if (requestData != null && requestData instanceof FiltriRicerca && clientAuthCode.equals(connectionAuthCode)) {
                FiltriRicerca wineToSearch = (FiltriRicerca) requestData;
                // non sempre i campi nome e annoProduzione sono pieni, alcune volte sono "" e a seconda di queso
                // bisogna fare la query corrispondente
                // RISOLTO
                ResultSet resultSet;

                if (wineToSearch.nome() != null && wineToSearch.annoProduzione() != -1) {
                  String dbquery = "SELECT * FROM vini where nome = ? and anno = ?";
                  resultSet = db.executeQuery(dbquery, wineToSearch.nome(), wineToSearch.annoProduzione());
                } else if (wineToSearch.nome() == null && wineToSearch.annoProduzione() != -1) {
                  String dbquery = "SELECT * FROM vini where anno = ?";
                  resultSet = db.executeQuery(dbquery, wineToSearch.annoProduzione());
                } else if (wineToSearch.nome() != null && wineToSearch.annoProduzione() == -1) {
                  String dbquery = "SELECT * FROM vini where nome = ?";
                  resultSet = db.executeQuery(dbquery, wineToSearch.nome());
                } else {
                  String dbquery = "SELECT * FROM vini";
                  resultSet = db.executeQuery(dbquery);
                }

                // Iterate through the result set
                List<Vino> wineList = new ArrayList<>();

                try {
                  while (resultSet.next()) {
                    // Extract data from the result set and create an object
                    Vino vino = rebuildVinoFromResultSet(resultSet);
                    // Add the object to the list
                    wineList.add(vino);
                  }
                } catch (SQLException e) {
                  e.printStackTrace();
                }
                System.out.println(wineList);
                response.set(1, wineList, this.connectionAuthCode);
                response.setSuccess();
              } else {
                response.set(0, null, this.connectionAuthCode);
              }
              message(response, os);
            }
            case 10 -> {
              // se mando 5 volte di fila questa richiesta, il server smette di rispondermi
              // prova a capire se c'è un numero max di richieste che il server può soddisfare
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof String) {
                String cognome = (String) requestData;
                List<Cliente> listaClienti = new ArrayList<>();
                ResultSet resultSet;

                if (cognome.equals("")) {
                  String dbquery = "Select * from clienti";
                  resultSet = db.executeQuery(dbquery);
                } else {
                  String dbquery = "Select * from clienti where cognome = ?";
                  resultSet = db.executeQuery(dbquery, cognome);
                }

                //ResultSet cast to List Cliente
                while (resultSet.next()) {
                  String nome = resultSet.getString("nome");
                  String clicognome = resultSet.getString("cognome");
                  String passwordtohash = resultSet.getString("password_hash");
                  String codiceFiscale = resultSet.getString("codice_fiscale");
                  String email = resultSet.getString("email");
                  String numeroTelefonico = resultSet.getString("numero_telefonico");
                  String indirizzoDiConsegna = resultSet.getString("indirizzo_consegna");

                  // Create a new Cliente object and add it to the list
                  Cliente cliente = new Cliente(
                          nome, clicognome, passwordtohash, codiceFiscale, email, numeroTelefonico, indirizzoDiConsegna, false);
                  listaClienti.add(cliente);
                }
                response.set(1, listaClienti, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            }
            case 11 -> {
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof FiltriRicerca) {
                FiltriRicerca dateToSearch = (FiltriRicerca) requestData;
                List<OrdineVendita> list = new ArrayList();
                String dbquery = "SELECT * FROM ordini_vendita INNER JOIN clienti ON ordini_vendita.cliente_id = clienti.email WHERE data_creazione BETWEEN ? AND ?";
                System.out.println("Date1: " + dateToSearch.data1());
                System.out.println("Date2: " + dateToSearch.data2());
                ResultSet resultSet = db.executeQuery(dbquery, dateToSearch.data1(), dateToSearch.data2());

                //resultSet unpack and cast into OrdineVendita
                while (resultSet.next()) {
                  String nome = resultSet.getString("nome");
                  String cognome = resultSet.getString("cognome");
                  String passwordtohash = resultSet.getString("password_hash");
                  String codiceFiscale = resultSet.getString("codice_fiscale");
                  String email = resultSet.getString("email");
                  String numeroTelefonico = resultSet.getString("numero_telefonico");
                  String indirizzoDiConsegna = resultSet.getString("indirizzo_consegna");
                  Date dataConsegna = resultSet.getDate("data_consegna");
                  Date dataCreazione = resultSet.getDate("data_creazione");

                  String listaQuantita = resultSet.getString("lista_quantita");
                  Gson gson = new Gson();
                  Type mapType = new TypeToken<Map<Integer, Integer>>() {
                  }.getType();
                  Map<Integer, Integer> vini = gson.fromJson(listaQuantita, mapType);
                  Map<Vino, Integer> vinimap = rebuildFromMapIntInt(vini);

                  // Create a new Cliente object and add it to the list
                  Cliente cliente = new Cliente(nome, cognome, passwordtohash, codiceFiscale, email, numeroTelefonico, indirizzoDiConsegna, false);
                  OrdineVendita row = new OrdineVendita(cliente, vinimap, dataConsegna, dataCreazione);
                  list.add(row);
                }
                response.set(1, list, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            }
            case 12 -> {
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof FiltriRicerca) {
                FiltriRicerca dateToSearch = (FiltriRicerca) requestData;
                List<OrdineAcquisto> list = new ArrayList();

                String dbquery1 = "SELECT im.*, cl.*, ov.*, pa.*, oa.* FROM ordini_di_acquisto oa INNER JOIN clienti cl ON oa.cliente_id = cl.email INNER JOIN impiegati im ON oa.impiegato_id = im.email INNER JOIN proposte_di_acquisto pa ON oa.proposta_associata_id = pa.id INNER JOIN ordini_vendita ov ON pa.ordine_id = ov.id WHERE oa.data_creazione BETWEEN ? AND ? ";
                System.out.println("Date1: " + dateToSearch.data1());
                System.out.println("Date2: " + dateToSearch.data2());
                ResultSet resultSet1 = db.executeQuery(dbquery1, dateToSearch.data1(), dateToSearch.data2());

                //resultSet unpack and cast into OrdineAcquisto
                while (resultSet1.next()) {
                  String nomeCliente = resultSet1.getString("cl.nome");
                  String cognomeCliente = resultSet1.getString("cl.cognome");
                  String passwordtohashCliente = resultSet1.getString("cl.password_hash");
                  String codiceFiscaleCliente = resultSet1.getString("cl.codice_fiscale");
                  String emailCliente = resultSet1.getString("cl.email");
                  String numeroTelefonicoCliente = resultSet1.getString("cl.numero_telefonico");
                  String indirizzoDiConsegnaCliente = resultSet1.getString("cl.indirizzo_consegna");
                  String indirizzoAziendaAcquisto = resultSet1.getString("oa.indirizzo_azienda");
                  Date dataCreazioneAcquisto = resultSet1.getDate("oa.data_creazione");
                  String nomeImpiegato = resultSet1.getString("im.nome");
                  String cognomeImpiegato = resultSet1.getString("im.cognome");
                  String passwordtohashImpiegato = resultSet1.getString("im.password_hash");
                  String codiceFiscaleImpiegato = resultSet1.getString("im.codice_fiscale");
                  String emailImpiegato = resultSet1.getString("im.email");
                  String numeroTelefonicoImpiegato = resultSet1.getString("im.numero_telefonico");
                  String indirizzoResidenzaImpiegato = resultSet1.getString("im.indirizzo_residenza");
                  Date dataConsegnaVendita = resultSet1.getDate("ov.data_consegna");
                  Date dataCreazioneVendita = resultSet1.getDate("ov.data_creazione");

                  String listaQuantitaProposta = resultSet1.getString("pa.lista_quantita");
                  Gson gson = new Gson();
                  Type mapType = new TypeToken<Map<Integer, Integer>>() {
                  }.getType();
                  Map<Integer, Integer> viniMancanti = gson.fromJson(listaQuantitaProposta, mapType);
                  Map<Vino, Integer> viniMancantiMap = rebuildFromMapIntInt(viniMancanti);

                  String listaQuantitaVenduta = resultSet1.getString("ov.lista_quantita");
                  Map<Integer, Integer> viniAcquistati = gson.fromJson(listaQuantitaVenduta, mapType);
                  Map<Vino, Integer> viniAcquistatiMap = rebuildFromMapIntInt(viniAcquistati);


                  //class building
                  Cliente cliente = new Cliente(nomeCliente, cognomeCliente, passwordtohashCliente, codiceFiscaleCliente, emailCliente, numeroTelefonicoCliente, indirizzoDiConsegnaCliente, false);
                  Impiegato impiegato = new Impiegato(passwordtohashImpiegato, nomeImpiegato, cognomeImpiegato, codiceFiscaleImpiegato, emailImpiegato, numeroTelefonicoImpiegato, indirizzoResidenzaImpiegato, false);
                  OrdineVendita ordine = new OrdineVendita(cliente, viniAcquistatiMap, dataConsegnaVendita, dataCreazioneVendita);
                  PropostaAcquisto proposta = new PropostaAcquisto(cliente, viniMancantiMap, indirizzoDiConsegnaCliente, ordine);
                  OrdineAcquisto row = new OrdineAcquisto(impiegato, proposta, indirizzoAziendaAcquisto, dataCreazioneAcquisto);
                  list.add(row);
                }
                response.set(1, list, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            }
            case 33 -> {
              System.out.println("Got from client request id: " + requestId);
              //ordineAcquisto wait from queue
              if (requestData != null && requestData instanceof Integer) {
                //Set Time end
                Integer seconds = (Integer) requestData;

                OrdineAcquisto fromQueue = this.server.oAqueuePoll(seconds.intValue());
                if (fromQueue != null) {
                  response.set(1, fromQueue, connectionAuthCode);
                  response.setSuccess();
                } else {
                  response.set(0, null, connectionAuthCode);
                }
              }
              else{
                response.set(0,null,connectionAuthCode);
              }
              message(response, os);
            }
            case 13 -> {
              System.out.println("Got from client request id: " + requestId);
              //gestisciOrdineAcquisto
              if (requestData instanceof OrdineAcquisto && requestData != null) {
                OrdineAcquisto ordineAcquisto = (OrdineAcquisto) requestData;
                ordineAcquisto.setImpiegato(loggedImpiegato);

                //Aggiorno in db quanto gia creato dal cliente
                String query = "INSERT INTO ordini_di_acquisto (impiegato_id, proposta_associata_id, indirizzo_azienda, data_creazione, completato) VALUES (?,?,?,?,0)";
                db.executeUpdate(query, ordineAcquisto.getImpiegato().getEmail(), ordineAcquisto.getPropostaAssociata().getId(), ordineAcquisto.getIndirizzoAzienda(), ordineAcquisto.getDataCreazione());

                //Aggiorno disponibilità vini
                Map<Integer, Integer> wineList = toMapIntInt(ordineAcquisto.getPropostaAssociata().getVini());
                //Iterate through Map
                for (Map.Entry<Integer, Integer> row : wineList.entrySet()) {
                  int wineid = row.getKey();
                  int quantity = row.getValue();
                  String dbquery = "SELECT disponibilita FROM vini WHERE id = ?";
                  ResultSet resultSet = db.executeQuery(dbquery, wineid);
                  if (resultSet.next()) {
                    Integer disponibilita = resultSet.getInt("disponibilita");

                    disponibilita += quantity;
                    dbquery = "UPDATE vini SET disponibilita = ? WHERE id = ?";
                    db.executeUpdate(dbquery, disponibilita, wineid);
                  } else {
                    //Errore, non ce il vino
                    throw new Exception("ERROR: we haven't found the bottles you were looking for");
                  }
                }

                //retrieve id from db
                String dbquery = "SELECT id FROM ordini_di_acquisto WHERE impiegato_id = ? AND completato = 0";
                ResultSet resultSet = db.executeQuery(dbquery, ordineAcquisto.getImpiegato().getEmail());
                if (resultSet.next()) {
                  int id = resultSet.getInt("id");
                  //Update values in db
                  dbquery = "UPDATE ordini_di_acquisto SET completato = 1 where id = ?";
                  db.executeUpdate(dbquery, id);
                  response.set(1, ordineAcquisto, connectionAuthCode);
                  response.setSuccess();
                }
              }
              else {
                response.set(0, null, connectionAuthCode);
              }
              message(response, os);
            }
            case 34 -> {
              //ordineVendita wait from queue
              if (requestData != null && requestData instanceof Integer) {
                Object fromQueue = this.server.oVqueuePoll((Integer) requestData);
                if (fromQueue != null) {

                  response.set(1, fromQueue, connectionAuthCode);
                  response.setSuccess();

                } else {
                  response.set(0, null, connectionAuthCode);
                }
              }
              message(response, os);
            }
            case 14 -> {
              System.out.println("Got from client request id: " + requestId);
              //gestione OrdineVendita
              if (requestData != null && requestData instanceof OrdineVendita) {
                String dbquery;
                OrdineVendita ordineFromClient = (OrdineVendita) requestData;
                dbquery = "UPDATE ordini_vendita SET data_consegna = ?, firmato = 1 WHERE id = ?";
                db.executeUpdate(dbquery, ordineFromClient.getDataConsegna(), ordineFromClient.getId());
                response.set(1, ordineFromClient, connectionAuthCode);
                response.setSuccess();
              }
              message(response, os);
            }
            case 15 -> {
              //Ricerca ProposteAcquisto
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof FiltriRicerca) {
                String query = "SELECT pa.*, ov.*, cl.* FROM proposte_di_acquisto pa INNER JOIN ordini_vendita ov ON pa.ordine_id = ov.id INNER JOIN clienti cl ON pa.cliente_id = cl.email WHERE ov.data_creazione BETWEEN ? AND ?";
                FiltriRicerca dateToSearch = (FiltriRicerca) requestData;

                //Fix java.util.Date to Sql readable date
                java.sql.Date sqlDate1 = new java.sql.Date(dateToSearch.data1().getTime());
                java.sql.Date sqlDate2 = new java.sql.Date(dateToSearch.data2().getTime());
                System.out.println(sqlDate1 + " " + sqlDate2);
                ResultSet resultSet = db.executeQuery(query, sqlDate1, sqlDate2);

                List<PropostaAcquisto> list = new ArrayList<>();
                while (resultSet.next()) {
                  PropostaAcquisto propostaAcquisto = dbGetPropostaAcquisto(resultSet);
                  list.add(propostaAcquisto);
                }
                response.set(1, list, connectionAuthCode);
                response.setSuccess();
                System.out.println("list size" + list.size());
                message(response, os);
              }
            }
            case 20 -> {
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof Impiegato) {
                Impiegato impiegato = (Impiegato) requestData;
                String dbquery = "INSERT INTO impiegati (email, nome, cognome, password_hash, codice_fiscale, numero_telefonico, indirizzo_residenza, isAdmin) VALUES " +
                        "(?,?,?,?,?,?,?,?)";
                try {
                  rowsAffected = db.executeUpdate(dbquery, impiegato.getEmail(), impiegato.getNome(), impiegato.getCognome(), impiegato.getPasswordhash(), impiegato.getCodiceFiscale(), impiegato.getNumeroTelefonico(), impiegato.getIndirizzoResidenza(), 0);
                  System.out.println("query Executed " + rowsAffected + " rows Affected");
                  response.set(1, null, null); //non dovrebbe darmi authCode?
                  response.setSuccess();
                } catch (SQLException e) {
                  response.set(0, null, null);
                } finally {
                  message(response, os);
                }
              }
            }
            case 21 -> {
              //Delete user
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof UtenteGenerico) {
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti WHERE email = ?";
                ResultSet resultSet = db.executeQuery(dbquery, user.getEmail());
                if (resultSet.next()) {
                  //it's in clienti
                  dbquery = "DELETE FROM clienti WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getEmail());
                } else {
                  //it's in impiegati or does not exist
                  dbquery = "DELETE FROM clienti WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getEmail());
                }
              }
              response.set(1, rowsAffected, this.connectionAuthCode);
              if (rowsAffected == 1) {
                response.setSuccess();
              }
              message(response, os);
            }
            case 22 -> {
              //Modify password user
              System.out.println("Got from client request id: " + requestId);
              if (requestData != null && requestData instanceof UtenteGenerico) {
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti where email = ?";
                ResultSet resultSet = db.executeQuery(dbquery, user.getEmail());
                if (resultSet.next()) {
                  //it's in clienti
                  dbquery = "UPDATE clienti SET password_hash = ? WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getPasswordhash(), user.getEmail());
                } else {
                  //it's in impiegati or does not exist
                  dbquery = "UPDATE impiegati SET password_hash = ? WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getPasswordhash(), user.getEmail());
                }
              }
              response.set(1, rowsAffected, this.connectionAuthCode);
              if (rowsAffected == 1) {
                response.setSuccess();
              }
              message(response, os);
            }
            case 23 -> {
              if(requestData != null && requestData instanceof ReportMensile){
                ReportMensile reportMensile = (ReportMensile) requestData;
                String query = "SELECT * FROM report";
                ResultSet resultSet = db.executeQuery(query);
                if(resultSet.next()){
                  //ce un report passato
                  Gson gson = new Gson();
                  Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
                  Map<Integer,Integer> venditePerVinoPassato = gson.fromJson(resultSet.getString("vendite_per_vini"), mapType);
                  Type mapType2 = new TypeToken<Map<String, Integer>>() {}.getType();
                  Map<String,Integer> valutazioneDipendentiPassato = gson.fromJson(resultSet.getString("valutazione_dipendenti"), mapType2);
                  ReportMensile reportPassato = new ReportMensile(resultSet.getFloat("introiti"), resultSet.getFloat("spese"), resultSet.getInt("bottiglie_vendute"), resultSet.getInt("bottiglie_disponibili"), venditePerVinoPassato, valutazioneDipendentiPassato);
                  Map<Integer,Integer> venditePerVino = new HashMap<>();
                  for (Map.Entry<Integer, Integer> line : reportPassato.getVenditePerVino().entrySet()) {
                    Integer vinoId = line.getKey();
                    Integer venditePrecedenti = line.getValue();
                    query = "SELECT numero_vendite FROM vini WHERE id = ?";
                    resultSet = db.executeQuery(query, vinoId);
                    int venditeAttuali = 0;
                    if(resultSet.next()){
                      venditeAttuali = resultSet.getInt("numero_vendite");
                      venditePerVino.put(vinoId, venditeAttuali-venditePrecedenti);
                    }
                    else{
                      //vino non presente
                      venditeAttuali = 0;
                    }
                }
                reportMensile.setVenditePerVino(venditePerVino);
                }
                else{
                  query = "SELECT * FROM vini";
                  resultSet = db.executeQuery(query);
                  Map<Integer,Integer> venditePerVino = new HashMap<>();
                  while(resultSet.next()){
                    Vino vino = rebuildVinoFromResultSet(resultSet);
                    venditePerVino.put(vino.getId(),vino.getNumeroVendite());
                  }
                  reportMensile.setVenditePerVino(venditePerVino);
                }
                if(dbStoreReportMensile(reportMensile)){
                  response.set(1,null,connectionAuthCode);
                  response.setSuccess();
                }
                else{
                  response.set(0,null,connectionAuthCode);
                }

              }
              message(response, os);
            }
            default -> throw new IOException("Invalid Request");
          }
          Thread.sleep(SLEEPTIME);
        }
        else{
          throw new IOException("Client Request IO Error");
        }
      }

      catch (EOFException e){
        System.out.println("Thread waiting");
        try {
          Thread.sleep(SLEEPTIME);
        } 
        catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
      catch (SocketException e){
        System.out.println("Client closed socket: exiting...");
        stop();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        stop();
      }
    }
  }

  //Thread dies
  public void stop(){
    try {
        System.out.println("Thread exiting...");
        this.stayOpen = false;
        this.db.close();
        this.socket.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
  }

  private int message(Response response, ObjectOutputStream os){
    try{
      os.writeObject(response);
      os.flush();
      System.out.println("Server Sent a Response");
    }
    catch(Exception e){
      e.printStackTrace();
      return 0;
    }
    return 1;
  }

  private String mapVinoToIdSerialized(Map<Vino,Integer> map){
    Map<Integer,Integer> mapInteger = new HashMap<>();
    Gson gson = new Gson();
    if(map != null){
    for (Map.Entry<Vino, Integer> line : map.entrySet()){
      int keyOut = line.getKey().getId();
      int valueOut = line.getValue();
      mapInteger.put(keyOut,valueOut);
    }
  }
    return gson.toJson(mapInteger);
  }
  private Map<Integer,Integer> toMapIntInt(Map<Vino,Integer> mapVino){
    Map<Integer,Integer> mapInteger = new HashMap<Integer,Integer>();

    for(Map.Entry<Vino,Integer> line : mapVino.entrySet()){
      Vino vino = line.getKey();
      Integer quantita = line.getValue();
      mapInteger.put(vino.getId(),quantita);
    }
    return mapInteger;
  }

  private Map<Vino,Integer> rebuildFromMapIntInt(Map<Integer,Integer> mapInt) throws SQLException{
      Map<Vino,Integer> mapVino = new HashMap<Vino,Integer>();

      for (Map.Entry<Integer, Integer> line : mapInt.entrySet()){
        Integer idVino = line.getKey();
        Integer quantita = line.getValue();
        String dbquery = "SELECT * FROM vini where id = ?";
        ResultSet resultSet = db.executeQuery(dbquery,idVino);
        if(resultSet.next()){
        //Build vino object from resultSet
          int id = resultSet.getInt("id");
          String nome = resultSet.getString("nome");
          String produttore = resultSet.getString("produttore");
          String provenienza = resultSet.getString("provenienza");
          int anno = resultSet.getInt("anno");
          String noteTecniche = resultSet.getString("note_tecniche");
          String vitigniJson = resultSet.getString("vitigni");
          List<String> vitigni = new Gson().fromJson(vitigniJson, new TypeToken<List<String>>() {}.getType());
          float prezzo = resultSet.getFloat("prezzo");
          int numeroVendite = resultSet.getInt("numero_vendite");
          int disponibilita = resultSet.getInt("disponibilita");
          Vino vino = new Vino(id, nome, produttore, provenienza, anno, noteTecniche,vitigni, prezzo, numeroVendite, disponibilita);
          mapVino.put(vino, quantita);
        }
      }
    return mapVino;
    }
  private int dbStoreOrdineVendita(OrdineVendita ordineVendita)throws SQLException{
      //db Store
      String dbquery = "INSERT INTO ordini_vendita (cliente_id, lista_quantita, indirizzo_consegna, data_consegna, data_creazione, completato, firmato) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

      //String lista_quantita = mapgson.toJson(ordineVendita.getViniAcquistati());
      String lista_quantita = mapVinoToIdSerialized(ordineVendita.getViniAcquistati());
      java.sql.Date dataCreazione = new java.sql.Date(new Date().getTime());
      java.sql.Date dataConsegna = new java.sql.Date(new Date().getTime());
      if(ordineVendita.getDataConsegna() != null && ordineVendita.getDataCreazione() != null){
      dataConsegna = new java.sql.Date(ordineVendita.getDataConsegna().getTime());
      dataCreazione = new java.sql.Date(ordineVendita.getDataCreazione().getTime());
      }
      return db.executeUpdate(dbquery,ordineVendita.getCliente().getEmail(), lista_quantita,ordineVendita.getIndirizzoConsegna(),dataConsegna, dataCreazione, ordineVendita.isCompletato(),ordineVendita.isFirmato());
                    
    }
  private PropostaAcquisto dbGetPropostaAcquisto(ResultSet resultSet) throws SQLException{
      Cliente cliente = new Cliente(resultSet.getString("cl.nome"),resultSet.getString("cl.cognome"),null,resultSet.getString("cl.codice_fiscale"),resultSet.getString("cl.email"),resultSet.getString("cl.numero_telefonico"),resultSet.getString("cl.indirizzo_consegna"),false);
      int id = resultSet.getInt("pa.id");
      Gson gson = new Gson();
      Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
      Map<Integer,Integer> viniOrdineID = gson.fromJson(resultSet.getString("ov.lista_quantita"), mapType);
      Map<Integer, Integer> viniPropostaID = gson.fromJson(resultSet.getString("pa.lista_quantita"), mapType);
      Map<Vino,Integer> viniProposta = rebuildFromMapIntInt(viniPropostaID);
      Map<Vino,Integer> viniOrdine = rebuildFromMapIntInt(viniOrdineID);

      OrdineVendita ordineVendita = new OrdineVendita(cliente,viniOrdine,resultSet.getDate("data_consegna"),resultSet.getDate("data_creazione"));
      PropostaAcquisto propostaAcquisto = new PropostaAcquisto(id, cliente, viniProposta, resultSet.getString("indirizzo_consegna"), ordineVendita);
      return propostaAcquisto;
    }
  private PropostaAcquisto dbGetPropostaAcquistoNotCompleted(int ordineVenditaId) throws SQLException{
      String query = "SELECT pa.*, ov.*, cl.* FROM proposte_di_acquisto pa INNER JOIN ordini_vendita ov ON pa.ordine_id = ov.id INNER JOIN clienti cl ON pa.cliente_id = cl.email WHERE ov.id = ? AND pa.completato = 0";
      ResultSet resultSet = db.executeQuery(query, ordineVenditaId);         
      resultSet.next();
      Cliente cliente = new Cliente(resultSet.getString("cl.nome"),resultSet.getString("cl.cognome"),null,resultSet.getString("cl.codice_fiscale"),resultSet.getString("cl.email"),resultSet.getString("cl.numero_telefonico"),resultSet.getString("cl.indirizzo_consegna"),false);
      int id = resultSet.getInt("pa.id");
      Gson gson = new Gson();
      Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
      Map<Integer,Integer> viniOrdineID = gson.fromJson(resultSet.getString("ov.lista_quantita"), mapType);
      Map<Integer, Integer> viniPropostaID = gson.fromJson(resultSet.getString("pa.lista_quantita"), mapType);
      Map<Vino,Integer> viniProposta = rebuildFromMapIntInt(viniPropostaID);
      Map<Vino,Integer> viniOrdine = rebuildFromMapIntInt(viniOrdineID);
      

      OrdineVendita ordineVendita = new OrdineVendita(cliente,viniOrdine,resultSet.getDate("data_consegna"),resultSet.getDate("data_creazione"));
      PropostaAcquisto propostaAcquisto = new PropostaAcquisto(id, cliente, viniProposta, resultSet.getString("indirizzo_consegna"), ordineVendita);
      return propostaAcquisto;
    }
  private Vino rebuildVinoFromResultSet(ResultSet resultSet)throws SQLException{
    //Build vino object from resultSet
    int id = resultSet.getInt("id");
    String nome = resultSet.getString("nome");
    String produttore = resultSet.getString("produttore");
    String provenienza = resultSet.getString("provenienza");
    int anno = resultSet.getInt("anno");
    String noteTecniche = resultSet.getString("note_tecniche");
    String vitigniJson = resultSet.getString("vitigni");
    List<String> vitigni = new Gson().fromJson(vitigniJson, new TypeToken<List<String>>() {}.getType());
    float prezzo = resultSet.getFloat("prezzo");
    int numeroVendite = resultSet.getInt("numero_vendite");
    int disponibilita = resultSet.getInt("disponibilita");
    Vino vino = new Vino(id, nome, produttore, provenienza, anno, noteTecniche,vitigni, prezzo, numeroVendite, disponibilita);

      return vino;
    }
  private Cliente clienteFromID(String id)throws SQLException{
    String query = "SELECT * FROM clienti WHERE email = ?";
    ResultSet resultSet = db.executeQuery(query, id);
    String nome;
    String cognome;
    String email;
    String passwordhash;
    String codiceFiscale;
    String numeroTelefonico;
    String indirizzoConsegna;
    Cliente cliente = null;
    if(resultSet.next()){
      email = resultSet.getString("email");
      nome = resultSet.getString("nome");
      cognome = resultSet.getString("cognome");
      passwordhash = resultSet.getString("password_hash");
      codiceFiscale = resultSet.getString("codice_fiscale");
      numeroTelefonico = resultSet.getString("numero_telefonico");
      indirizzoConsegna = resultSet.getString("indirizzo_consegna");
      cliente = new Cliente(nome,cognome,passwordhash,codiceFiscale,email,numeroTelefonico,indirizzoConsegna,false);
    }
    return cliente;
  }
  private Boolean checkAvability(Map<Integer, Integer> bottiglieList) throws SQLException{
    Boolean available = true;
    for (Map.Entry<Integer, Integer> line : bottiglieList.entrySet()) {
      Integer idVino = line.getKey();
      Integer quantitaRichiesta = line.getValue();
      String dbquery = "SELECT * FROM vini where id = ?";
      ResultSet resultSet = db.executeQuery(dbquery, idVino);
      Vino vino;
      if(resultSet.next()){
        vino = rebuildVinoFromResultSet(resultSet);
        if(vino.getDisponibilita() < quantitaRichiesta){
          available = false;
      }
    }
  }
    return available;
  }
  private OrdineVendita dbGetOrdineVendita(ResultSet resultSet) throws SQLException{
      int id = 0;
      Cliente cliente = null;
      Date dataConsegna = null;
      Date dataCreazione = null;
      Map<Integer, Integer> winelistInt = new HashMap<>();
      Map<Vino, Integer> winelist = new HashMap<>();

      if(resultSet.next()){
        id = resultSet.getInt("id");
        cliente = clienteFromID(resultSet.getString("cliente_id"));
        dataConsegna = resultSet.getDate("data_consegna");
        dataCreazione = resultSet.getDate("data_creazione");
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
        winelistInt = gson.fromJson(resultSet.getString("lista_quantita"), mapType);
        winelist = rebuildFromMapIntInt(winelistInt);
        }
      return new OrdineVendita(id,cliente,winelist,dataConsegna, dataCreazione);
    }
     private OrdineVendita dbGetOrdineVenditaNext(ResultSet resultSet) throws SQLException{
      int id = 0;
      Cliente cliente = null;
      Date dataConsegna = null;
      Date dataCreazione = null;
      Map<Integer, Integer> winelistInt = new HashMap<>();
      Map<Vino, Integer> winelist = new HashMap<>();
        id = resultSet.getInt("id");
        cliente = clienteFromID(resultSet.getString("cliente_id"));
        dataConsegna = resultSet.getDate("data_consegna");
        dataCreazione = resultSet.getDate("data_creazione");
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
        winelistInt = gson.fromJson(resultSet.getString("lista_quantita"), mapType);
        winelist = rebuildFromMapIntInt(winelistInt);
      return new OrdineVendita(id,cliente,winelist,dataConsegna, dataCreazione);
    }
    
    
    public OrdineAcquisto buildOrdineAcquistoToReplenish(Map<Vino,Integer> lowVino) throws SQLException{

      //check for entities in db
      Cliente fakeCl = new Cliente(null, null, null, null, "clientesoglia", null, null, false);
      String query = "SELECT * FROM clienti WHERE email = ?";     //id of fake cliente for replenishing
      ResultSet resultSet = db.executeQuery(query,fakeCl.getEmail());
      if(resultSet.next()){
        //fake cliente already in db
        
      }
      else{
        //fake cliente not in db
        String insertClienteFake = "INSERT INTO clienti (email,nome,cognome,password_hash,codice_fiscale,numero_telefonico,indirizzo_consegna) values (?,'o','o','o','o','o','')";
        db.executeUpdate(insertClienteFake,fakeCl.getEmail());
      }
      //look for fake OrdineVendita
      query = "SELECT * FROM ordini_vendita WHERE cliente_id = ?";
      resultSet = db.executeQuery(query, fakeCl.getEmail());
      OrdineVendita fakeOv;
      if(resultSet.next()){
        //fake OrdineVendita gia presente, recupera
        fakeOv = dbGetOrdineVenditaNext(resultSet);
      }
      else{
        //fake ordineVendita non presente, crea
        fakeOv = new OrdineVendita(fakeCl, null, null);
        fakeOv.setIndirizzoConsegna("o");
        dbStoreOrdineVendita(fakeOv);
        //get fakeOv id
        query = "SELECT id FROM ordini_vendita WHERE cliente_id = ?";
        resultSet = db.executeQuery(query, fakeCl.getEmail());
        if(resultSet.next()){
          fakeOv.setId(resultSet.getInt("id"));
        }
      }

      //look for pending proposte
      PropostaAcquisto fakePa;
      query = "SELECT * FROM proposte_di_acquisto WHERE cliente_id = ? AND completato = 0";
      resultSet = db.executeQuery(query,fakeCl.getEmail());
      if(resultSet.next()){
        //Ce gia una proposta di acquisto per il rifornimento non completata, aggiornala
        
        System.out.println("proposta gia presente, aggiorna");
        fakePa = dbGetPropostaAcquistoNotCompleted(fakeOv.getId());
        query = "UPDATE proposte_di_acquisto SET lista_quantita = ? WHERE id = ?";
        db.executeUpdate(query, mapVinoToIdSerialized(lowVino),fakePa.getId());
        
      }
      else{
        //nessuna proposta per rifornimento pending
        System.out.println("proposta non presente, nuova");
        fakePa = new PropostaAcquisto(fakeCl, lowVino, null, null);
        //store in db
        query = "INSERT INTO proposte_di_acquisto (cliente_id,ordine_id,lista_quantita,completato) values (?,?,?,0)";
        db.executeUpdate(query,fakeCl.getEmail(),fakeOv.getId(),mapVinoToIdSerialized(lowVino));
        query = "SELECT id FROM proposte_di_acquisto WHERE cliente_id = ?";
        resultSet = db.executeQuery(query, fakeCl.getEmail());

        if(resultSet.next()){
          fakePa.setId(resultSet.getInt("id"));
        }
      }
      OrdineAcquisto ordineAcquisto = new OrdineAcquisto(null,fakePa,"",new Date());
      return ordineAcquisto;
    }
    
    public boolean dbStoreReportMensile(ReportMensile report){
      java.sql.Date dataCreazione = new java.sql.Date(new Date().getTime());
      String query = "INSERT INTO report (introiti,spese,bottiglie_vendute,bottiglie_disponibili,vendite_per_vini,valutazione_dipendenti,data_creazione) VALUES (?,?,?,?,?,?,?)";
      try{
      if(db.executeUpdate(query,report.getIntroiti(),report.getSpese(),report.getBottiglieDisponibili(),report.getBottiglieVendute(),report.getVenditePerVino(),report.getValutazioneDipendenti(),dataCreazione) == 1){
        return true;
      }
      else{
        return false;
      }
    }
    catch (SQLException e)
    {
      return false;
    }
    }

    public void verificaSoglia(int threshold) throws SQLException{
      Boolean underThreshold = true;
      Map<Vino,Integer> lowMap = new HashMap<>();
      //recupera vini
      String dbquery = "SELECT * FROM vini";
      ResultSet resultSet = db.executeQuery(dbquery);
      //check soglia
      while(resultSet.next()){
        Vino row = rebuildVinoFromResultSet(resultSet);
        if(row.getDisponibilita() < threshold){
          underThreshold = true;
          lowMap.put(row, threshold - row.getDisponibilita());
        }
      }
      if(underThreshold){
        try{
          //check if other already in queue
          OrdineAcquisto replenish = buildOrdineAcquistoToReplenish(lowMap);
          System.out.println(this.server.popFromOaQueue(replenish));
          this.server.queuePut(replenish);
        }
        catch(InterruptedException e){
          e.printStackTrace();
        }
      }
    }

}

