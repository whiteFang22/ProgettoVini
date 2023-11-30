package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Queue;

import com.example.classes.*;

/**
 *
 * The class {@code Server} defines a server that waits
 * for a message and then sends an answer.
 *
**/

public class Server
{
  private static final int COREPOOL = 5;
  private static final int MAXPOOL = 100;
  private static final long IDLETIME = 5000;
  private static final int SPORT = 4444;
  
  private ServerSocket socket;
  private ThreadPoolExecutor pool;
  private LinkedBlockingQueue<OrdineVendita> ovQueue;
  private LinkedBlockingQueue<OrdineAcquisto> oaQueue;


  /**
   * Class constructor.
   *
   * @throws IOException if the creation of the server socket fails.
   *
  **/

  public Server() throws IOException
  {
    this.socket = new ServerSocket(SPORT);
    this.ovQueue = new LinkedBlockingQueue<OrdineVendita>();
    this.oaQueue = new LinkedBlockingQueue<OrdineAcquisto>();
  }

  /**
   * Runs the server code.
   *
  **/
  private void run()
  {
    this.pool = new ThreadPoolExecutor(COREPOOL, MAXPOOL, IDLETIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    System.out.println("Server Running");
    while (true)
    {
      try
      {
        Socket s = this.socket.accept();
        System.out.println("Client Connected");
        this.pool.execute(new ServerThread(this, s));
      }
      catch (Exception e)
      {
        System.out.println("FATAL ERROR: Server died");
        e.printStackTrace();
        break;
      }
    }
    this.pool.shutdown();
  }

  /**
   * Gets the server pool.
   *
   * @return the thread pool.
   *
  **/
  public ThreadPoolExecutor getPool()
  {
    return this.pool;
  }
  /**
   * Gets first object from queue
   * 
   * @return the upper object of queue
   * @throws InterruptedException
   */
  public Object oVqueueTake() throws InterruptedException{
    return this.ovQueue.take();
  }
  /*
   * Waits @timeout seconds for an object than returns
   * @return OrdineVendita
   */
  public OrdineVendita oVqueuePoll(long timeout) throws InterruptedException{
    return this.ovQueue.poll(timeout, TimeUnit.SECONDS);
  }


  public Object oAqueueTake() throws InterruptedException{
    return this.oaQueue.take();
  }
  /*
   * Waits @timeout seconds for an object than returns
   * @return OrdineAcquisto
   */
  public OrdineAcquisto oAqueuePoll(long timeout) throws InterruptedException{
    return this.oaQueue.poll(timeout, TimeUnit.SECONDS);
  }
 /**
   * Puts object in their queue
   * 
   * @return nothing
 * @throws InterruptedException
   */
  public void queuePut(Object o) throws InterruptedException{
    if(o instanceof OrdineVendita){
      OrdineVendita obj = (OrdineVendita) o;
      this.ovQueue.put(obj);
      System.out.println("in ordinevenditaqueue");
    }
    else if(o instanceof OrdineAcquisto){
      OrdineAcquisto obj = (OrdineAcquisto) o;
      this.oaQueue.put(obj);
      System.out.println("in ordineacquistoqueue");

    }
  }
    public OrdineAcquisto popFromOaQueue(OrdineAcquisto lookFor) throws InterruptedException{
      LinkedBlockingQueue<OrdineAcquisto> tempQueue = new LinkedBlockingQueue<OrdineAcquisto>();

        // Variable to check if the element is found
        OrdineAcquisto foundOrdineAcquisto = null;
        while (!this.oaQueue.isEmpty()) {
            // Dequeue an element
            OrdineAcquisto currentElement = this.oaQueue.take();

            // Check if the current element is the target
            if (currentElement.getCliente().getEmail() == lookFor.getCliente().getEmail()) {
                foundOrdineAcquisto = currentElement;
                System.out.println("popped");
            }
            else{
               // Enqueue the current element to the temporary queue
              tempQueue.put(currentElement);

            }
        }

        while (!tempQueue.isEmpty()) {
            this.oaQueue.put(tempQueue.take());
        }
        return foundOrdineAcquisto;
    
  }
  /**
   * Closes the server execution.
   *
  **/
  public void close()
  {
    try
    {
      this.socket.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally{
      this.pool.shutdown();
    }
  }

  /**
   * Starts the demo.
   *
   * @param args  the method does not requires arguments.
   *
   * @throws IOException if the execution fails.
   *
  **/
  public static void main(final String[] args) throws IOException
  {
    new Server().run();
  }
}
