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
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import com.example.classes.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
   * 0 - Cliente, registrazione             tested
   * 1 - UtenteGenerico, Login              tested
   * 2 - Cliente, modifica credenziali      tested
   * 3 - Cliente, acquistabottiglie         tested    ok!
   * 4 - Cliente, confermaPagamento         tested    da sistemare 
   * 5 - Cliente, proponiAcquisto
   * 9 - Cliente, ricercaVino               tested
   * 10 - Impiegato, ricercaCliente         tested
   * 11 - Impiegato, ricercaOrdineVendita   tested
   * 12 - Impiegato, ricercaOrdineAcquisto
   * 13 - Impiegato, gestisciOrdineAcquisto
   * 14 - Impiegato, gestisciOrdineVendita
   * 20 - Admin, registraImpiegato          tested
   * 21 - Admin, eliminaUtente              tested
   * 22 - Admin, modificaCredenzialiUtente  tested
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
    message(null,os);
    //main loop
    while (stayOpen)
    { 
      try
      {
        //System.out.println("Thread running...... pid: ");
        //long pid = ProcessHandle.current().pid();
        //System.out.println(pid);

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
          /*
          Gson mapgson = new GsonBuilder()
              .registerTypeAdapter(new TypeToken<Map<Vino, Integer>>() {}.getType(), new VinoMapSerializer())
              .registerTypeAdapter(new TypeToken<Map<Vino, Integer>>() {}.getType(), new VinoMapDeserializer())
              .registerTypeAdapter(Vino.class, new VinoSerializer())
              .registerTypeAdapter(Vino.class, new VinoDeserializer())
              .create();
            Type mapType = new TypeToken<Map<Vino, Integer>>() {}.getType();
          */
          //request Handle
          switch(requestId){
            case 0:
              /*
               quando cerco di registrarmi con una mail già esistente si genera giustamente un errore ma poi,
               invece che restituire res con isSuccess=false il server si stoppa
               Lo stesso va fatto in registraImpiegato

              F: Dovrebbe essere OK ora
              */
            System.out.println("Got from client request id: " + requestId);
              //Registrazione Cliente, requestData instance of Cliente
                if(requestData != null && requestData instanceof Cliente){
                  Cliente user = (Cliente) requestData;
                  System.out.println("Request OK, contacting db");
                  
                  String dbquery = "INSERT INTO clienti (nome, cognome, password_hash, codice_fiscale, email, numero_telefonico, indirizzo_consegna) VALUES (?,?,?,?,?,?,?)";

                  try{
                    rowsAffected = db.executeUpdate(dbquery,user.getNome(),user.getCognome(),user.getPasswordhash(),user.getCodiceFiscale(),user.getEmail(),user.getNumeroTelefonico(), user.getIndirizzoDiConsegna());
                    System.out.println("query Executed "+ rowsAffected + " rows Affected");
                    response.set(1,null,null); //non dovrebbe darmi authCode?
                    response.setSuccess();
                    }
                  catch(SQLException e){
                    response.set(0,null,null);

                    e.printStackTrace();

                  }
                  finally{
                    message(response,os);
                  }
                }

              break;
            case 1:
              // Login Utente, requestData instance of Cliente/Impiegato/Amministratore 
              System.out.println("Got from client request id: " + requestId);

              if(requestData != null && requestData instanceof UtenteGenerico){
                //Login Cliente
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti where email = ?; ";
                ResultSet resultset = db.executeQuery(dbquery, user.getEmail());

                if(resultset.next()){
                  System.out.println("User found, checking password");
                  String passwordhash = resultset.getString("password_hash");
                  System.out.println(user.getPasswordhash());
                  System.out.println(passwordhash);
                  if(passwordhash.equals(user.getPasswordhash())){
                    System.out.println("password match");
                    /* bisogna fare distinzione se chi chiama il metodo è cliente, impiegato o amministratore
                       per farlo senza dover modificare ulterirmente il nostro codice scorri nella tabella
                       clienti e se non trovi scorri in impiegati. Se trovi in impiegati verifica il campo isAdmin
                       per capire se creare un impiegato o un amministratore 
                       RISOLTO
                       */
                    Cliente loggeduser = new Cliente(resultset.getString("nome"), resultset.getString("cognome"),resultset.getString("password_hash"), resultset.getString("codice_fiscale"), resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_consegna"),false);
                    final String authCode = AuthCodeGenerator.generateAuthCode();
                    this.connectionAuthCode = authCode;
                    System.out.println(authCode);
                    System.out.println("loggedUser: "+loggeduser);
                    this.loggedCliente = loggeduser;
                    System.out.println("loggedCliente: "+loggedCliente);
                    response.setId(1);
                    response.setAuthCode(authCode);
                    response.setData(loggeduser);
                    response.setSuccess();
                  }
                  else
                  {
                    //Found but wrong password
                    response.setId(0);
                    response.setAuthCode(null);
                    response.setData(user);
                    System.out.println("wrong password");
                  }
                }
                else
                {
                    //Not Found in Clienti look in impiegati
                    System.out.println("User not found in table clienti");
                    
                    dbquery = "SELECT * FROM impiegati where email = ?; ";
                    resultset = db.executeQuery(dbquery, user.getEmail());

                    if(resultset.next()){
                      System.out.println("User found, checking password");
                      String passwordhash = resultset.getString("password_hash");
                      System.out.println(user.getPasswordhash());
                      System.out.println(passwordhash);

                      if(passwordhash.equals(user.getPasswordhash())){
                        System.out.println("password match");
                        Boolean isAdmin = resultset.getBoolean("isAdmin");
                        if(isAdmin){
                          //Amministratore
                          Amministratore loggeduser = new Amministratore(resultset.getString("password_hash"), resultset.getString("nome"),
                          resultset.getString("cognome"), resultset.getString("codice_fiscale"),
                          resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_residenza"),false);
                          final String authCode = AuthCodeGenerator.generateAuthCode();
                          this.connectionAuthCode = authCode;
                          System.out.println(authCode);
                          this.loggedAmministratore = loggeduser;
                          response.setId(1);
                          response.setAuthCode(authCode);
                          response.setData(loggeduser);
                          response.setSuccess();
                         }
                        else{
                          //Impiegato
                          Impiegato loggeduser = new Impiegato(resultset.getString("password_hash"), resultset.getString("nome"),
                          resultset.getString("cognome"), resultset.getString("codice_fiscale"),
                          resultset.getString("email"), resultset.getString("numero_telefonico"), resultset.getString("indirizzo_residenza"),false);
                          final String authCode = AuthCodeGenerator.generateAuthCode();
                          this.connectionAuthCode = authCode;
                          System.out.println(authCode);
                          this.loggedImpiegato = loggeduser;
                          response.setId(1);
                          response.setAuthCode(authCode);
                          response.setData(loggeduser);
                          response.setSuccess();
                          }
                      }
                      else{
                        //wrong password
                        response.setId(0);
                        response.setAuthCode(null);
                        response.setData(user);
                        System.out.println("wrong password");
                      }
                    }
                    else{
                      //Not Found
                      response.setId(0);
                      response.setAuthCode(null);
                      response.setData(user);
                      System.out.println("User not Found");
                    }
                  }
              }
            else{
              //Invalid Request
              response.setId(0);
              response.setAuthCode(null);
              response.setData(null);
              }

              message(response, os);
              break;
            case 2:
               System.out.println("Got from client request id: " + requestId);
               //Modifica password 
               if(requestData != null && requestData instanceof Cliente && clientAuthCode.equals(connectionAuthCode)){
                Cliente cliente = (Cliente) requestData;

                String query = "UPDATE clienti SET password_hash = ? WHERE email = ?;";

                rowsAffected = db.executeUpdate(query,cliente.getPasswordhash(),cliente.getCodiceFiscale());
                System.out.println("query Executed "+ rowsAffected + " rows Affected");
                response.set(1,null,null);
                response.setSuccess();
                message(response,os);
              }
              else {
                System.out.println("Something went wrong");
                response.set(0,null,null);
                message(response,os);
              }
              break;

            case 3:

              System.out.println("Got from client request id: " + requestId);
              //Acquista Bottiglie 
              if(requestData != null && requestData instanceof Map<?,?> && clientAuthCode.equals(connectionAuthCode)){
                  Map<Integer, Integer> bottiglieList = (Map<Integer, Integer>) requestData;
                  Map<Vino, Integer> viniPresenti = new HashMap<>();
                  Map<Vino,Integer> viniMancanti = new HashMap<>();
                  boolean allInStock = true;
                  
                  
                  //Initialize OrdineVendita
                  System.out.println("cliente: "+loggedCliente);
                  //Calculate Time
                  Date now = new Date();
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(now);
                  calendar.add(Calendar.WEEK_OF_YEAR, 1);
                  Date nextWeek = calendar.getTime();

                  OrdineVendita ordineVendita = new OrdineVendita(loggedCliente, nextWeek ,now);

                  for (Map.Entry<Integer, Integer> line : bottiglieList.entrySet()){
                    Integer idVino = line.getKey();
                    Integer quantitaRichiesta = line.getValue();
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

                      if(disponibilita >= quantitaRichiesta){
                        //vino disponibile, aggiungi a OrdineVendita
                        viniPresenti.put(vino,quantitaRichiesta);
                      }
                      else{
                        //vino non disponibile o non abbastanza bottiglie in magazzino, creo Map viniMancanti
                        //Inserisco nell'ordineVendita anche i vini che non sono disponibili a magazzino!!
                        //Mentre in proposta solo quelli che mancano!!
                        viniPresenti.put(vino, quantitaRichiesta);
                        viniMancanti.put(vino, quantitaRichiesta - disponibilita);
                        allInStock = false;
                      }
                    }
                  }
                  ordineVendita.setCompletato(false);
                  ordineVendita.setViniAcquistati(viniPresenti);
                  if(allInStock){
                    //tutti i vini disponibili, finalizza OrdineVendita e salva in db
                    
                    dbStoreOrdineVendita(ordineVendita);
                    response.set(1,ordineVendita,this.connectionAuthCode);
                    response.setSuccess();
                  }
                  else{
                    //Non tutti i vini sono disponibili in quantita sufficienti, creo proposta di acquisto
                    
                    PropostaAcquisto propostaAcquisto = new PropostaAcquisto(this.loggedCliente, viniMancanti, this.loggedCliente.getIndirizzoDiConsegna(), ordineVendita);
                    //db Store
                    dbStoreOrdineVendita(ordineVendita);
                    
                    String dbquery2 = "SELECT * FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                    ResultSet resultSet = db.executeQuery(dbquery2,ordineVendita.getCliente().getEmail());
                    int id = 0;
                    if(resultSet.next()){
                      id = resultSet.getInt("id");
                    }
                    String dbquery3 = "INSERT INTO proposte_di_acquisto (cliente_id, lista_quantita, ordine_id) " +
                    "VALUES (?, ?, ?)";

                    //String lista_quantita_mancanti = mapgson.toJson(propostaAcquisto.getVini());
                    String lista_quantita_mancanti = mapVinoToIdSerialized(propostaAcquisto.getVini());

                    db.executeUpdate(dbquery3,propostaAcquisto.getCliente().getEmail(),lista_quantita_mancanti,id);

                    //response code 2, data: PropostaAcquisto
                    response.set(2,propostaAcquisto,this.connectionAuthCode);
                    response.setSuccess();
                  }
                  
              }
              else{
                response.set(0, null, this.connectionAuthCode);
              }
              message(response, os);

            break;
            case 4:

              System.out.println("Got from client request id: " + requestId);

              if(requestData != null && requestData instanceof Boolean && clientAuthCode.equals(connectionAuthCode)){
                Boolean conferma = (Boolean) requestData;
                if(conferma){
                  //Conferma = true -> invia a impiegato
                  
                  //TODO: se conferma è true invia copia ordine a un impiegato, sara poi lui a firmare l'ordine e aggiornare le disponibilita
                  //TODO: quindi il codice qua sotto andra spostato da un altra parte
                  //Aggiornamento quantità magazzino
                  String dbquery = "SELECT * from ordini_vendita WHERE cliente_id = ? and completato = 0";
                  ResultSet resultset = db.executeQuery(dbquery, this.loggedCliente.getEmail());
                  if(resultset.next()){
                    //Get lista_quantità from ordine_vendita and parse into Map
                    //Map<Vino, Integer> wineList = mapgson.fromJson(resultset.getString("lista_quantita"), mapType);
                    Gson gson = new Gson();
                    Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
                    Map<Integer, Integer> wineList = gson.fromJson(resultset.getString("lista_quantita"),mapType);
                    Date dataCreazione = resultset.getDate("data_creazione");
                    Map<Vino,Integer> listaQuantita = rebuildFromMapIntInt(wineList);
                    int id = resultset.getInt("id");
                    OrdineVendita ordineVendita = new OrdineVendita(id,loggedCliente, listaQuantita,null,dataCreazione);

                    //Invio copia su queue
                    this.server.queuePut(ordineVendita);

                    //Iterate through Map TODO: move to Impiegato
                    for (Map.Entry<Integer, Integer> row : wineList.entrySet()) {
                      int wineid = row.getKey();
                      int quantity = row.getValue();
                      dbquery = "SELECT disponibilita, numero_vendite FROM vini WHERE id = ?";
                      resultset = db.executeQuery(dbquery, wineid);
                      if(resultset.next()){
                        Integer disponibilita = resultset.getInt("disponibilita");
                        Integer numeroVendite = resultset.getInt("numero_vendite");

                        //TODO: what happens if not all bottles are in stock?
                        disponibilita-=quantity;
                        numeroVendite+=quantity;
                        dbquery="UPDATE vini SET disponibilita = ?, numero_vendite = ? WHERE id = ?";
                        db.executeUpdate(dbquery,disponibilita,numeroVendite,wineid);
                      }
                      else{
                        //Errore, non ce il vino
                        throw new Exception("ERROR: we haven't found the bottles you were looking for");
                      }
                    }
                    //Modifica ordine e proposta acquisto
                    dbquery = "UPDATE ordini_vendita SET completato = 1 WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(dbquery, this.loggedCliente.getEmail());
                    dbquery = "UPDATE proposte_di_acquisto SET completato = 1 WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(dbquery,this.loggedCliente.getEmail());
                  }
                  else{
                    //ERRORE, non ce l'ordine
                    response.set(0,null,this.connectionAuthCode);
                  }
                  
                }
                else{
                  //conferma = false
                  String dbquery = "DELETE FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                  db.executeQuery(dbquery, this.loggedCliente.getEmail());
                  dbquery = "DELETE FROM proposte_di_acquisto WHERE cliente_id = ? AND completato = 0";
                  db.executeUpdate(dbquery,this.loggedCliente.getEmail());
                }
                response.set(1, null, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            break;
            case 5:
              System.out.println("Got from client request id: " + requestId);
              //Cliente proponiAcquisto
              if(requestData != null && requestData instanceof Boolean && clientAuthCode.equals(connectionAuthCode)){
                Boolean conferma = (Boolean) requestData;
                String query = "SELECT * FROM proposte_di_acquisto WHERE Cliente_id = ? AND completato = 0";
                ResultSet resultSet = db.executeQuery(query, this.loggedCliente.getEmail());

                if(resultSet.next()){
                  if(conferma){
                    

                    //Get Objects from db
                    query = "SELECT pa.*, ov.*, cl.* FROM proposte_di_acquisto pa INNER JOIN ordini_vendita ov ON pa.ordine_id = ov.id INNER JOIN clienti cl ON pa.cliente_id = cl.email WHERE pa.cliente_id = ? AND pa.completato = 0;";
                    resultSet = db.executeQuery(query, this.loggedCliente.getEmail());

                    if(resultSet.next()){
                      PropostaAcquisto propostaAcquisto = dbGetPropostaAcquisto(resultSet);
                      OrdineAcquisto ordineAcquisto = new OrdineAcquisto(propostaAcquisto.getCliente(), null, propostaAcquisto, propostaAcquisto.getIndirizzoConsegna(), new Date());
                    
                      //STORE
                      query = "INSERT INTO ordini_di_acquisto (cliente_id,impiegato_id, proposta_associata_id, data_creazione, completato) VALUES (?,0,?,?,0)";
                      db.executeUpdate(query,ordineAcquisto.getCliente().getEmail(), resultSet.getString("pa.id"), ordineAcquisto.getDataCreazione());
                      
                      //Cliente conferma
                      query = "UPDATE proposte_di_acquisto SET completato = 1 WHERE cliente_id = ? AND completato = 0";
                      db.executeUpdate(query, this.loggedCliente.getEmail());

                      //TODO: Post OrdineAcquisto into Queue for worker to handle
                      this.server.queuePut(ordineAcquisto);

                      response.set(1, null, this.connectionAuthCode);
                      response.setSuccess();
                    }
                  }
                  else{
                    //Cliente annulla
                    query = "DELETE FROM proposte_di_acquisto WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(query,this.loggedCliente.getEmail());
                    query = "DELETE FROM ordini_vendita WHERE cliente_id = ? AND completato = 0";
                    db.executeUpdate(query,this.loggedCliente.getEmail());
                    
                    response.set(1,null,this.connectionAuthCode);
                    response.setSuccess();
                  }
                }
                else{
                  //nessuna proposta di acquisto in attesa di conferma
                  response.set(0, null, this.connectionAuthCode);
                }
              }
              else {
                response.set(0,null,this.connectionAuthCode);
              }
              message(response,os);
            break;
            case 9:
              // non ti passo vino ma FiltriRicerca
              System.out.println("Got from client request id: " + requestId);
              //Ricerca Vino nel database
               if(requestData != null && requestData instanceof FiltriRicerca && clientAuthCode.equals(connectionAuthCode)){
                  FiltriRicerca wineToSearch = (FiltriRicerca) requestData;
                  // non sempre i campi nome e annoProduzione sono pieni, alcune volte sono "" e a seconda di queso
                  // bisogna fare la query corrispondente
                  // RISOLTO
                  ResultSet resultSet;

                  if(wineToSearch.nome() != null && wineToSearch.annoProduzione() != -1){
                    String dbquery = "SELECT * FROM vini where nome = ? and anno = ?";
                    resultSet = db.executeQuery(dbquery,wineToSearch.nome(), wineToSearch.annoProduzione());
                  }
                  else if(wineToSearch.nome() == null && wineToSearch.annoProduzione() != -1){
                    String dbquery = "SELECT * FROM vini where anno = ?";
                    resultSet = db.executeQuery(dbquery,wineToSearch.annoProduzione());
                  }
                  else if(wineToSearch.nome() != null && wineToSearch.annoProduzione() == -1){
                    String dbquery = "SELECT * FROM vini where nome = ?";
                    resultSet = db.executeQuery(dbquery,wineToSearch.nome());
                  }
                  else{
                    String dbquery = "SELECT * FROM vini";
                    resultSet = db.executeQuery(dbquery);
                  }
                  
                  // Iterate through the result set
                  List<Vino> wineList = new ArrayList<>();
                  try {
                      while (resultSet.next()) {
                          // Extract data from the result set and create an object
                          Vino vino = new Vino(resultSet.getInt("id"),resultSet.getString("nome"),resultSet.getInt("anno"), resultSet.getFloat("prezzo"));
                          // Add the object to the list
                          wineList.add(vino);
                      }
                  } catch (SQLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
                 System.out.println(wineList);
                  response.set(1, wineList, this.connectionAuthCode);
                  response.setSuccess();
                }
              else{
                response.set(0, null, this.connectionAuthCode);
              }
              message(response, os);
            break;
            
            case 10:
              // se mando 5 volte di fila questa richiesta, il server smette di rispondermi
              // prova a capire se c'è un numero max di richieste che il server può soddisfare
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof String){
                String cognome = (String) requestData;
                List<Cliente> listaClienti = new ArrayList<>();
                ResultSet resultSet;
                      
                if(cognome.equals("")){
                  String dbquery = "Select * from clienti";
                  resultSet = db.executeQuery(dbquery);
                }
                else{
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
                      nome, clicognome, passwordtohash, codiceFiscale, email, numeroTelefonico, indirizzoDiConsegna,false);
                  listaClienti.add(cliente);
              }
              response.set(1,listaClienti,this.connectionAuthCode);
              response.setSuccess();
              message(response, os);
              }
            break;

            case 11:
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof FiltriRicerca){
                FiltriRicerca dateToSearch = (FiltriRicerca) requestData;
                List<OrdineVendita> list = new ArrayList();
                String dbquery = "SELECT * FROM ordini_vendita INNER JOIN clienti ON ordini_vendita.cliente_id = clienti.email WHERE data_creazione BETWEEN ? AND ?";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate1 = dateFormat.format(dateToSearch.data1());
                String formattedDate2 = dateFormat.format(dateToSearch.data2());
                System.out.println("formattedDate1: "+formattedDate1);
                ResultSet resultSet = db.executeQuery(dbquery,formattedDate1,formattedDate2);

                //resultSet unpack and cast into OrdineVendita
                while(resultSet.next()){
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
                  Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
                  Map<Integer, Integer> vini = gson.fromJson(listaQuantita, mapType);
                  Map<Vino,Integer> vinimap = rebuildFromMapIntInt(vini);

                  // Create a new Cliente object and add it to the list
                  Cliente cliente = new Cliente(nome, cognome, passwordtohash, codiceFiscale, email, numeroTelefonico, indirizzoDiConsegna,false);
                  OrdineVendita row = new OrdineVendita(cliente,vinimap,dataConsegna,dataCreazione);
                  list.add(row);
                }
                response.set(1, list, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            break; 
            case 12:
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof FiltriRicerca){
                FiltriRicerca dateToSearch = (FiltriRicerca) requestData;
                List<OrdineAcquisto> list = new ArrayList();
                String dbquery1 = "SELECT impiegati.email AS impiegato_email, impiegati.nome AS impiegato_nome, impiegati.cognome AS impiegato_cognome, impiegati.password_hash AS impiegato_password_hash, impiegati.codice_fiscale AS impiegato_codice_fiscale, impiegati.numero_telefonico AS impiegato_numero_telefonico, impiegati.indirizzo_residenza AS impiegato_indirizzo_residenza, impiegati.isAdmin AS impiegato_isAdmin, clienti.email AS cliente_email, clienti.nome AS cliente_nome, clienti.cognome AS cliente_cognome, clienti.password_hash AS cliente_password_hash, clienti.codice_fiscale AS cliente_codice_fiscale, clienti.numero_telefonico AS cliente_numero_telefonico, clienti.indirizzo_consegna AS cliente_indirizzo_di_consegna, ordini_di_acquisto.id AS ordine_id, ordini_di_acquisto.proposta_associata_id AS ordine_proposta_id, ordini_di_acquisto.indirizzo_azienda AS ordine_indirizzo_azienda, ordini_di_acquisto.data_creazione AS ordine_data_creazione, ordini_di_acquisto.completato AS ordine_completato, proposte_di_acquisto.id AS proposta_id, proposte_di_acquisto.cliente_id AS proposta_cliente_id, proposte_di_acquisto.lista_quantita AS proposta_lista_quantita, vendita.id AS vendita_id, vendita.cliente_id AS vendita_cliente_id, vendita.lista_quantita AS vendita_lista_quantita, vendita.indirizzo_consegna AS vendita_indirizzo_consegna, vendita.data_consegna AS vendita_data_consegna, vendita.data_creazione AS vendita_data_creazione FROM wineshop.impiegati INNER JOIN wineshop.clienti ON impiegati.email = clienti.email INNER JOIN wineshop.ordini_di_acquisto ON impiegati.email = ordini_di_acquisto.impiegato_id INNER JOIN wineshop.proposte_di_acquisto ON proposte_di_acquisto.ordine_id = ordini_di_acquisto.id INNER JOIN wineshop.ordini_vendita AS vendita ON vendita.cliente_id = clienti.email WHERE ordini_di_acquisto.data_creazione BETWEEN ? AND ?";
            
                
                ResultSet resultSet1 = db.executeQuery(dbquery1,dateToSearch.data1(),dateToSearch.data2());
                //resultSet unpack and cast into OrdineAcquisto
                while(resultSet1.next()){
                  String nomeCliente = resultSet1.getString("cliente_nome");
                  String cognomeCliente = resultSet1.getString("cliente_cognome");
                  String passwordtohashCliente = resultSet1.getString("cliente_password_hash");
                  String codiceFiscaleCliente = resultSet1.getString("cliente_codice_fiscale");
                  String emailCliente = resultSet1.getString("cliente_email");
                  String numeroTelefonicoCliente = resultSet1.getString("cliente_numero_telefonico");
                  String indirizzoDiConsegnaCliente = resultSet1.getString("cliente_indirizzo_di_consegna");
                  String indirizzoAziendaAcquisto = resultSet1.getString("ordine_indirizzo_azienda");
                  Date dataCreazioneAcquisto = resultSet1.getDate("ordine_data_creazione");
                  String nomeImpiegato = resultSet1.getString("impiegato_nome");
                  String cognomeImpiegato = resultSet1.getString("impiegato_cognome");
                  String passwordtohashImpiegato = resultSet1.getString("impiegato_password_hash");
                  String codiceFiscaleImpiegato = resultSet1.getString("impiegato_codice_fiscale");
                  String emailImpiegato = resultSet1.getString("impiegato_email");
                  String numeroTelefonicoImpiegato = resultSet1.getString("impiegato_numero_telefonico");
                  String indirizzoResidenzaImpiegato = resultSet1.getString("impiegato_indirizzo_residenza");
                  Date dataConsegnaVendita = resultSet1.getDate("vendita_data_consegna");
                  Date dataCreazioneVendita = resultSet1.getDate("vendita_data_creazione");

                  String listaQuantitaProposta = resultSet1.getString("proposta_lista_quantita");
                  Gson gson = new Gson();
                  Map<Vino, Integer> viniMancanti = gson.fromJson(listaQuantitaProposta, new TypeToken<Map<Vino, Integer>>() {}.getType());
                  String listaQuantitaVendita = resultSet1.getString("vendita_lista_quantita");
                  gson = new Gson();
                  Map<Vino, Integer> viniAcquistati = gson.fromJson(listaQuantitaVendita, new TypeToken<Map<Vino, Integer>>() {}.getType());

                  //class building
                  Cliente cliente = new Cliente(nomeCliente, cognomeCliente, passwordtohashCliente, codiceFiscaleCliente, emailCliente, numeroTelefonicoCliente, indirizzoDiConsegnaCliente,false);
                  Impiegato impiegato = new Impiegato(passwordtohashImpiegato, nomeImpiegato, cognomeImpiegato, codiceFiscaleImpiegato, emailImpiegato, numeroTelefonicoImpiegato, indirizzoResidenzaImpiegato,false);
                  OrdineVendita ordine = new OrdineVendita(cliente, viniAcquistati, dataConsegnaVendita, dataCreazioneVendita);
                  PropostaAcquisto proposta = new PropostaAcquisto(cliente, viniMancanti, indirizzoDiConsegnaCliente, ordine);
                  OrdineAcquisto row = new OrdineAcquisto(cliente, impiegato, proposta, indirizzoAziendaAcquisto, dataCreazioneAcquisto);
                  list.add(row);
                }
                response.set(1, list, this.connectionAuthCode);
                response.setSuccess();
                message(response, os);
              }
            break;
            case 13:
              System.out.println("Got from client request id: " + requestId);
              //gestisciOrdineAcquisto
              Object objectFromQueue = server.queueTake();
              if(objectFromQueue instanceof OrdineAcquisto){
                OrdineAcquisto ordineAcquisto = (OrdineAcquisto) objectFromQueue;
                ordineAcquisto.setImpiegato(loggedImpiegato);

                //TODO: l'impiegato qui deve inserire l'indirizzo dell'azienda, qui ne metto uno arbitrario
                ordineAcquisto.setIndirizzoAzienda("Via A. Rossi 7");

                //Aggiorno in db quanto gia creato dal cliente
                String query = "UPDATE ordini_di_acquisto SET impiegato_id = ?, indirizzo_azienda = ? WHERE cliente_id = ? AND completato = 0 AND impiegato_id = 0";
                db.executeUpdate(query,ordineAcquisto.getImpiegato().getEmail(), ordineAcquisto.getIndirizzoAzienda(), ordineAcquisto.getCliente().getEmail());
                
                //TODO: qui l'Impiegato fisicamente completa l'ordine

                //Aggiorno disponibilità vini
                    Map<Integer, Integer> wineList = toMapIntInt(ordineAcquisto.getPropostaAssociata().getVini());
                //Iterate through Map
                    for (Map.Entry<Integer, Integer> row : wineList.entrySet()) {
                      int wineid = row.getKey();
                      int quantity = row.getValue();
                      String dbquery = "SELECT disponibilita FROM vini WHERE id = ?";
                      ResultSet resultSet = db.executeQuery(dbquery, wineid);
                      if(resultSet.next()){
                        Integer disponibilita = resultSet.getInt("disponibilita");

                        disponibilita+=quantity;
                        dbquery="UPDATE vini SET disponibilita = ? WHERE id = ?";
                        db.executeUpdate(dbquery,disponibilita,wineid);
                      }
                      else{
                        //Errore, non ce il vino
                        throw new Exception("ERROR: we haven't found the bottles you were looking for");
                      }
                    }

                //retrieve id from db
                String dbquery = "SELECT id FROM ordini_di_acquisto WHERE cliente_id = ? AND completato = 0";
                ResultSet resultSet = db.executeQuery(dbquery, ordineAcquisto.getCliente().getEmail());
                if(resultSet.next()){
                  int id = resultSet.getInt("id");
                  //Update values in db
                  dbquery = "UPDATE ordini_di_acquisto SET completato = 1, where id = ?";
                  db.executeUpdate(dbquery, id);
                  response.set(1, ordineAcquisto, connectionAuthCode);
                  response.setSuccess();
                }
              }
              else{
                server.queuePut(objectFromQueue);
                response.set(0, null, connectionAuthCode);
              }
              message(response, os);
            break;
            case 14:
            //TODO: work in progress
              System.out.println("Got from client request id: " + requestId);
              //gestione OrdineVendita
              if(requestData != null && requestData instanceof OrdineVendita){
                Object fromQueue = this.server.queueTake();
                if(fromQueue instanceof OrdineVendita){
                  OrdineVendita completo = (OrdineVendita) fromQueue;
                  //Set data consegna and firma

                  String query = "UPDATE ordini_vendita SET data_consegna = ?, firmato = 1 WHERE id = ?";
                  db.executeUpdate(query,completo.getDataConsegna(),completo.getId());
                }
                else{
                  this.server.queuePut(fromQueue);
                  response.set(0,null,connectionAuthCode);
                }
              }
            break;
            case 20:
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof Impiegato){
                Impiegato impiegato = (Impiegato) requestData;
                String dbquery = "INSERT INTO impiegati (email, nome, cognome, password_hash, codice_fiscale, numero_telefonico, indirizzo_residenza, isAdmin) VALUES "+
                "(?,?,?,?,?,?,?,?)";
                try{
                  rowsAffected = db.executeUpdate(dbquery,impiegato.getEmail(),impiegato.getNome(),impiegato.getCognome(),impiegato.getPasswordhash(),impiegato.getCodiceFiscale(),impiegato.getNumeroTelefonico(),impiegato.getIndirizzoResidenza(),0);
                  System.out.println("query Executed "+ rowsAffected + " rows Affected");
                  response.set(1,null,null); //non dovrebbe darmi authCode?
                  response.setSuccess();
                }
                catch(SQLException e){
                  response.set(0,null,null);
                }
                finally{
                  message(response, os);
                }
                }
            break;
            case 21:
              //Delete user
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof UtenteGenerico){
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti WHERE email = ?";
                ResultSet resultSet = db.executeQuery(dbquery, user.getEmail());
                if(resultSet.next()){
                  //it's in clienti
                  dbquery = "DELETE FROM clienti WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getEmail());
                }
                else{
                  //it's in impiegati or does not exist
                  dbquery = "DELETE FROM clienti WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery, user.getEmail());
                }
              }
              response.set(1,rowsAffected,this.connectionAuthCode);
              if(rowsAffected == 1){ response.setSuccess();}
              message(response, os);
            break;
            case 22:
              //Modify password user
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof UtenteGenerico){
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM clienti where email = ?";
                ResultSet resultSet = db.executeQuery(dbquery, user.getEmail());
                if(resultSet.next()){
                  //it's in clienti
                  dbquery = "UPDATE clienti SET password_hash = ? WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery,user.getPasswordhash(), user.getEmail());
                }
                else{
                  //it's in impiegati or does not exist
                  dbquery = "UPDATE impiegati SET password_hash = ? WHERE email = ?";
                  rowsAffected = db.executeUpdate(dbquery,user.getPasswordhash(), user.getEmail());
                }
              }
              response.set(1,rowsAffected,this.connectionAuthCode);
              if(rowsAffected == 1){ response.setSuccess();}
              message(response, os);
            break;
            default:
              throw new IOException("Invalid Request");
            
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
    
    for (Map.Entry<Vino, Integer> line : map.entrySet()){
      int keyOut = line.getKey().getId();
      int valueOut = line.getValue();
      mapInteger.put(keyOut,valueOut);
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

      return db.executeUpdate(dbquery,ordineVendita.getCliente().getEmail(), lista_quantita,ordineVendita.getIndirizzoConsegna(),ordineVendita.getDataConsegna(), ordineVendita.getDataCreazione(), ordineVendita.isCompletato(),ordineVendita.isFirmato());
                    
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

}

