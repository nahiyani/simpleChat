package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
import java.io.*;

import edu.seg2105.edu.server.ui.ServerConsole;

// Importing oscf classes
import ocsf.server.ConnectionToClient;
import ocsf.server.AbstractServer;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
	/**
	   * The default port to listen on.
	   */
	
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  
  public void handleLoginInfo(String msg, ConnectionToClient client) {
	
	  // splitting the command and the login info to set client info
      String[] loginInfo = (msg.toString()).split(" ");
	  // the second piece of login info split is set as the loginID for client
      String clientLoginID = loginInfo[1];

      // Set the client's login ID in their connection information
      client.setInfo("clientLoginID", clientLoginID);
      //output messages to notify message has been received and that client has logged on
      System.out.println("Message received: " + msg.toString() + " from null");
      System.out.println(clientLoginID + " has logged on.");
      
      //sent the message to client
      try {
    	  client.sendToClient(clientLoginID + " has logged on.");
      } catch (IOException e) {
    	  e.printStackTrace();
      }
  }
  
  public void handleMessageFromClient (Object msg, ConnectionToClient client){
      
    String msgStr = (String) msg;
    if (msgStr.startsWith("#login")) {
    	   	
        handleLoginInfo(msgStr, client);
    	
    } else {
    	// other processing for regular messages
        System.out.println("Message received: " + msg + " from " + client.getInfo("clientLoginID"));
        // send message to all clients on same server
        this.sendToAllClients(client.getInfo("clientLoginID") + " : " + msg);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }
  
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("New client connected to server");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println("Client " + client.getInfo("clientLoginID") + " is disconnected.");
  }
  
  public void setServerConsole(ServerConsole serverConsole) {
  }
  
  public void disconnectAll() {
	// create a list of threads
	Thread[] clientThreadList = getClientConnections();

	//for each connected client in the list, disconnect each one individually
	for (Thread clientThread : clientThreadList) {
	  if (clientThread instanceof ConnectionToClient) {
		//creating instance of ConnectionToClient class
	    ConnectionToClient client = (ConnectionToClient) clientThread;
	    //try to close the client
	    try {
	      client.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	  }
	}
		
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) {
    
	  int port = 0; //Port to listen on

    try {
      if (args.length > 0) {
    	  port = Integer.parseInt(args[0]); //Get port from command line    	
      }
    }catch(Throwable e)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

}
//End of EchoServer class
