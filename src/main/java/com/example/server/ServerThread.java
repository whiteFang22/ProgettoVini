package com.example.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import com.example.classes.Request;
import com.example.classes.Response;
import com.example.classes.UtenteGenerico;
import com.example.classes.Cliente;
import com.example.classes.AuthCodeGenerator;
import com.example.classes.Vino;

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
   * 0 - Client registration to db
   * 1 - Client Login
   * 9 - Client wine search in db
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
    //main loop
    while (true)
    { 
      try
      {
        System.out.println("Thread running...... pid: ");
        long pid = ProcessHandle.current().pid();
        System.out.println(pid);

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
          switch(requestId){
            case 0:
            System.out.println("Got from client request id: " + requestId);
              //Registrazione Cliente, requestData instance of Cliente
               if(requestData != null && requestData instanceof Cliente){
                  Cliente user = (Cliente) requestData;
                  System.out.println("Request OK, contacting db");
                  String dbquery = "INSERT INTO utenti (nome, cognome, passwordhash, codiceFiscale, email, numeroTelefonico) VALUES (?,?,?,?,?,?)";
                  rowsAffected = db.executeUpdate(dbquery,user.getNome(),user.getCognome(),user.getPasswordhash(),user.getCodiceFiscale(),user.getEmail(),user.getNumeroTelefonico());
                  System.out.println("query Executed "+ rowsAffected + " rows Affected");
                  response.set(1,null,null);
                  response.setSuccess();
                  message(response,os);
               }
              break;
            case 1:
              // Login Utente, requestData instance of UtenteGenerico 
              System.out.println("Got from client request id: " + requestId);
              if(requestData != null && requestData instanceof UtenteGenerico){
                UtenteGenerico user = (UtenteGenerico) requestData;
                String dbquery = "SELECT * FROM utenti where  = ? ";
                ResultSet resultset = db.executeQuery(dbquery, user.getEmail());
                if(resultset.next()){
                  System.out.println("User found, checking password");
                  String passwordhash = resultset.getString("passwordhash");
                  if(passwordhash.equals(user.getPasswordhash())){
                    System.out.println("password match");
                    final String authCode = AuthCodeGenerator.generateAuthCode();
                    this.connectionAuthCode = authCode;
                    response.setId(1);
                    response.setAuthCode(authCode);
                    response.setData(user);
                    response.setSuccess();
                    message(response, os);
                  }
                  else
                  {
                    response.setId(0);
                    response.setAuthCode(null);
                    response.setData(user);
                    message(response, os);
                    System.out.println("wrong password");
                  }
                }
                else{
                  System.out.println("User not found");
                }
              }
              break;
            case 2:
               System.out.println("Got from client request id: " + requestId);
               //Modifica password 
               if(requestData != null && requestData instanceof Cliente && clientAuthCode == connectionAuthCode){
                Cliente cliente = (Cliente) requestData;
                String query = "UPDATE users SET password = ? WHERE codiceFiscale = ?;";
                rowsAffected = db.executeUpdate(query,cliente.getPasswordhash(),cliente.getCodiceFiscale());
                System.out.println("query Executed "+ rowsAffected + " rows Affected");
                response.set(1,null,null);
                response.setSuccess();
                message(response,os);
              }
              break;
            case 9:
              System.out.println("Got from client request id: " + requestId);
              //Ricerca Vino nel database
               if(requestData != null && requestData instanceof Vino && clientAuthCode == connectionAuthCode){
                  Vino wineToSearch = (Vino) requestData;
                  String dbquery = "SELECT * FROM vino where nome = ? and anno = ?";
                  ResultSet resultSet = db.executeQuery(dbquery,wineToSearch.getNome(), wineToSearch.getAnno());
                  // Iterate through the result set
                  List<Vino> wineList = new ArrayList<>();
                  try {
                      while (resultSet.next()) {
                          // Extract data from the result set and create an object
                          Vino vino = new Vino(resultSet.getString("nome"),resultSet.getInt("anno"));
                          // Add the object to the list
                          wineList.add(vino);
                      }
                  } catch (SQLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
                  response.set(1, wineList, this.connectionAuthCode);
                  response.setSuccess();
                  message(response, os);
              }

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
        System.out.println("Thread exiting due to client closing connection");
        stop();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.exit(0);
        stop();
      }
    }
  }

  //Thread dies
  public void stop(){
    try {
        this.db.close();
        this.socket.close();
        System.out.println("Thread exiting...");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        finally{
           System.exit(0);
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

}

