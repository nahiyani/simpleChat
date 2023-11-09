package edu.seg2105.edu.server.ui;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;
import java.util.Scanner;

public class ServerConsole implements ChatIF {

	//class variables for ServerConsole class
    private EchoServer server;
    private Scanner scanner;

    // constructor for ServerConcole class
    public ServerConsole(EchoServer server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    // Handle server commands
    private void handleServerCommand(String command) {

        //if command equals #quit
        if (command.equals("#quit")) {
            display("Server will quit");
            server.sendToAllClients("SERVER MSG> Server is quitting");
            server.stopListening();
            System.exit(0);

        //if the command is stop
        } else if (command.startsWith("#stop")) {
        	
        	//stop listening for clients
            server.stopListening();

        // if the command is close
        } else if (command.equals("#close")) {
        	//stop listening to the client and disconnect all clients connected
        	server.stopListening(); 
        	server.disconnectAll();
        	        
        // when command starts with setport
        } else if (command.startsWith("#setport")) {
        	
        	//if server is not listening
            if (!server.isListening()) {
                String[] cLine = command.split(" ");
                
                if (cLine.length == 2) {
                	//setting a new port and assigning it
                    int newPort = Integer.parseInt(cLine[1]);
                    server.setPort(newPort);
                    display("New port is: " + newPort);

                } else {
                    //display error message
                	display("Error setting port. Proper format is #setport <port>");
                }
            //if server is still listening
            } else {
                display("Server is required to stop listening before setting new port");
            }

        // if the command is start
        } else if (command.equals("#start")) {
        	
        	//if client is not listening
            if (!server.isListening()) {
            	//listen
                try {
                    server.listen();
                //exception
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //if client is still listening
            } else {
                //display error message
            	display("Server must be stopped before starting to listen for new clients again");
            }

        // if command is getport 
        } else if (command.equals("#getport")) {
            //display current port number
        	display("Current server port: " + server.getPort());
        
        // if invalid command is entered
        } else {
            display("Invalid command: " + command);
        }

    }
    
    public void accept() {

        String message;

        while (true) {
            message = scanner.nextLine();
            
            if (message != null && !message.isEmpty()) {

            	// if the message begins with "#" (server commands)
                if (message.startsWith("#")) {
                    handleServerCommand(message);

                } else {
                    // Send to console for all clients
                    server.sendToAllClients("SERVER MSG> " + message);

                    // Send to server console
                    display("SERVER MSG> " + message);
                }
            }
        }
    }

    // Implemented ChatIF method
    @Override
    public void display(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
      
        int port = EchoServer.DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number");
            }
        }
        EchoServer echoServer = new EchoServer(port);
        ServerConsole serverConsole = new ServerConsole(echoServer);
        echoServer.setServerConsole(serverConsole);

        try {
            //server is waiting to listen to client connections
            echoServer.listen();

            //accept request
            serverConsole.accept(); 
         
        } catch (IOException e) {
            System.err.println("ERROR - Could not listen for clients!");
        }
    }

}