// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  private boolean sentLoginCommand = false;
  private String clientLoginID;
   
	
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String clientLoginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor from 
    this.clientUI = clientUI;
    this.clientLoginID = clientLoginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    //display message
    String message = msg.toString();
    
    //if client is attempting to log in
    if (message.startsWith("#login")) {
      
      
      //split the entry into the 
      String[] clientEntry = message.split(" ");
      //if the entry is valid
      if (clientEntry.length >= 2) {
        
    	//set the second word equal to the login ID
    	clientLoginID = clientEntry[1];
        //send message that client has logged on
    	clientUI.display(clientLoginID + " has logged on");
      }
    } else {
      //display regular message if invalid entry
    	clientUI.display(msg.toString());
    }
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message){
  
	  //if the message is a login command called
	  if (message.startsWith("#login")) {
	      
		  //if login command wasn't already set to true, set it to true now
		  if (!sentLoginCommand) {
	        sentLoginCommand = true;
	        
	        //sent message to server
	        try {
	          sendToServer(message);
	        } catch (IOException e) {
	          //if you cannot sent message to server, then quit system
	          clientUI.display("Could not send message to server. Terminating client.");
	          quit();
	        }
	      
	        //if login already occurred
	      } else {
	        //send error message and quit
	    	clientUI.display("ERROR: #login is not able to connect more than once after connecting.");
	        quit();
	      }
		
		//if client didn't try to log in
	    } else {
	      
	      //try sending message to server
	      try {
	        sendToServer(message);
	      } catch (IOException e) {
	        //send error message and quit
	    	clientUI.display("Could not send message to server. Terminating client.");
	        quit();
	      }
	    }
	  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
  /**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	@Override
  protected void connectionException(Exception exception) {
	clientUI.display("The server is shut down");
	//terminate the connection
	quit();
		
	}
  
	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
		clientUI.display("Connection closed");
	}
    
}
//End of ChatClient class