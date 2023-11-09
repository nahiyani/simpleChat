package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:

// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
* This class constructs the UI for a chat client.  It implements the
* chat interface in order to activate the display() method.
* Warning: Some of the code here is cloned in ServerConsole 
*
* @author Fran&ccedil;ois B&eacute;langer
* @author Dr Timothy C. Lethbridge  
* @author Dr Robert Lagani&egrave;re
*/
public class ClientConsole implements ChatIF {

public String clientLoginID;
//Class variables *************************************************
/**
* The default port to connect on.
*/
final public static int DEFAULT_PORT = 5555;

//Instance variables **********************************************

/**
* The instance of the client that created this ConsoleChat.
*/
ChatClient client;
 
/**
* Scanner to read from the console
*/
Scanner fromConsole; 

String key;


//Constructors ****************************************************

/**
* Constructs an instance of the ClientConsole UI.
*
* @param host The host to connect to.
* @param port The port to connect on.
*/
public ClientConsole(String clientLoginID, String host, int port) {
 
	this.clientLoginID = clientLoginID;
	
	//try to create a new ChatClient object
	try 
 {
   client = new ChatClient(clientLoginID, host, port, this);
   // Show client has logged in using the clientLoginId
   client.handleMessageFromClientUI("#login " + clientLoginID);
     
 } catch(IOException exception){
 	//output error message and quit
 	display("Error: Can't setup connection! Terminating client.");
 	System.exit(1);
 }
 
 // creating a scanner object to read from console
 fromConsole = new Scanner(System.in); 
}


//Instance methods ************************************************

/**
* This method waits for input from the console.  Once it is 
* received, it sends it to the client's message handler.
*/
public void accept(){

	  try {
   
   String message;

   while (true) 
   {
     message = fromConsole.nextLine();
     if (message.startsWith("#")) {
     	handleCommand(message);
     } else {
     	client.handleMessageFromClientUI(message);
     }
     
   }
 } 
 catch (Exception ex) 
 {
   display("Unexpected error while reading from console!");
 }
}

private void handleCommand(String command) {
	  
	  if (command.equals("#quit")) {
		  
		  //say which client wants to quit
		  display(clientLoginID + " has requested to quit");
		  
		  try {
		 
		      //closing the connection and letting user know
			  client.closeConnection();
			  //quitting the system
			  System.exit(0);
			  //output to let client know quitting has happened
			  display("Quit the system");
		  
	      //exception handler in case it didn't work
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
		  
	  } else if (command.equals("#logoff")){
		  
		 if (client.isConnected()) {
			//check for connection to server is closed gracefully, but not quit like #quit method
			try {
			  //closing the connection and letting user know
			  client.closeConnection();
			  //output message
			  display(clientLoginID + "is logged off");
			  //if it can't output error message
			} catch (IOException e) {
				e.printStackTrace();
		    }
		 //if already logged off
		 } else {
			 display(clientLoginID + "is already logged off");
		 }
		  
	  } else if (command.equals("#gethost")) {
		  
		  //declare who requested for the host number
	      display(clientLoginID + " has requested to get host");
		  //display the host 
		  display(client.getHost());
	  
	  } else if (command.startsWith("#sethost")) {
		  
		  //say who requested to set the port
		  display(clientLoginID + " has requested to set host");
		  
		  //if the client is not logged in, then allow to set the host
		  if (!client.isConnected()) {
			  //takes in the #sethost message followed by new name and puts it in array
			  String [] setHostCommandLine = command.split(" ");
			  //making sure the host is actually a word
			  if (setHostCommandLine.length == 2) {
		          //get the new host  
				  String newHost = setHostCommandLine[1];
		          //set it to the new host
				  client.setHost(newHost);
				  //output the new host
		          display("New host is : " + newHost);
		      
		      //if invalid entry has been entered
			  } else {
				  display("Invalid syntax. Must input #sethost <host>");
		      }
		  //if not, then return error message
		  } else {
			  display("Set host process denied. Log off before setting a host");
		  }
		  
	  } else if (command.equals("#getport")) {
		  
		  //indicate who requested to get port
		  display(clientLoginID + " requested to get port");
		  //display the port number 
		  display(String.valueOf(client.getPort()));
	  
	  } else if (command.startsWith("#setport")) {
		  
		  //indicate who requested to set new port
		  display(clientLoginID + " requested to set port");
		  //if the client is not logged in, then allow to set the host
		  if (!client.isConnected()) {
			  //takes in the #sethost message followed by new name and puts it in array
			  String [] setPortCommandLine = command.split(" ");
			  //making sure the host is actually a word
			  if (Integer.valueOf(setPortCommandLine[1]) <= 65535 && Integer.valueOf(setPortCommandLine[1]) >= 0) {
		          //get the new host  
				  String newPort = setPortCommandLine[1];
		          //set it to the new host
				  client.setPort(Integer.parseInt(newPort));
				  //output success message
		          display("New port is : " + newPort);
		      
		      //if it isn't a number or of the port number is out of range
			  } else {
				  display("Invalid entry. Enter input #setpost <port>, where port number is between 0 and 65535 inclusive");
		      }
		  //if client is not logged on, then return error message
		  } else {
			  display("Set port process denied. Log off before setting a port");
		  }
		  
	  } else if (command.startsWith("#login")) {
		  

		  //indicate who wants to log in
	      display(clientLoginID + " requested to log in");
		  
		  //if not logged in
		  if (!client.isConnected()){
			  
			  //try to log in
			  try {
				 //open a connection
				 client.openConnection();
				 //display logged on message
				 display(clientLoginID + ", you have logged in");
			  } catch(IOException e) {
				  e.printStackTrace();
			  }
		  //if already logged in, command returns message
		  } else {
			//display logged on message
			  display(clientLoginID + " is already logged in");
		  }
	  
	  //if invalid command has been written
	  } else {
		      display("Invalid command");
	  }
}

/**
* This method overrides the method in the ChatIF interface.  It
* displays a message onto the screen.
*
* @param message The string to be displayed.
*/
	public void display(String message) {
		System.out.println("> " + message);
	}


//Class methods ***************************************************

/**
* This method is responsible for the creation of the Client UI.
*
* @param args[0] The host to connect to.
*/
	public static void main(String[] args) {
	  
	//checks for if user put in login, and if they didn't ;et users know that they have to set a login ID
	if (args.length < 1) {
	 	System.out.println("ERROR - No login ID specified. Connection aborted.");
	 	//exit system
	 	System.exit(1);
	}
	
	//variables to create a new ClientConsole UI (loginID, host, port)
	String loginID = args[0];
	String host = "";
	int port = 0;
 
	  
	try {
		host = args[1];
		port = Integer.parseInt(args[2]);
	} catch(ArrayIndexOutOfBoundsException e) {
		host = "localhost";
		port = DEFAULT_PORT;
	}
 
	ClientConsole chat = new ClientConsole(loginID, host, port);
 
	// if the login ID has been provided but server isn't running yet, send error message
   if (!chat.client.isConnected()) {
	  System.out.println("ERROR - Can't setup connection! Terminating client.");
	  System.exit(1);

   } else {
   chat.accept(); // Wait for console data
   }
 }
}
 

//End of ConsoleChat class

