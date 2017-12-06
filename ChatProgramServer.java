/* [ChatProgramServer.java]
 * Description: This is an example of a chat server.
 * The program  waits for a client and accepts a message. 
 * It then responds to the message and quits.
 * This server demonstrates how to employ multithreading to accepts multiple clients
 * @author Mangat
 * @version 1.0a
 */

//imports for network communication
import java.io.*;
import java.net.*;
import java.util.*;

class ChatProgramServer {
  
  ServerSocket serverSock;// server socket for connection
  static Boolean running = true;  // controls if the server is accepting clients
  static Queue<String> incoming = new LinkedList<String>();
  static ArrayList<Client> clientList = new ArrayList<Client>();
  
   /** Main
    * @param args parameters from command line
    */
  public static void main(String[] args) { 
    new ChatProgramServer().go(); //start the server
  }
  
  /** Go
    * Starts the server
    */
  public void go() { 
        System.out.println("Waiting for a client connection..");
    
     Socket client = null;//hold the client connection
        
    try {
      serverSock = new ServerSocket(796);  //assigns an port to the server
     // serverSock.setSoTimeout(5000);  //5 second timeout
         while(running) {  //this loops to accept multiple clients
            client = serverSock.accept();  //wait for connection
           System.out.println("Client connected");
           //Note: you might want to keep references to all clients if you plan to broadcast messages
           //Also: Queues are good tools to buffer incoming/outgoing messages
           Thread t = new Thread(new ConnectionHandler(client)); //create a thread for the new client and pass in the socket
           t.start(); //start the new thread
         }
    }catch(Exception e) { 
     // System.out.println("Error accepting connection");
      //close all and quit
      try {
        client.close();
      }catch (Exception e1) { 
        System.out.println("Failed to close socket");
      }
      System.exit(-1);
    }
  }
  
  class MessageSender implements Runnable {
    
    public void run(){
      
      send(clientList);
      
    }
    
    public void send(ArrayList<Client> list){
      Client client;
      while (running){ 
        for (int i = 0; i<list.size();i++){
          client = list.get(i);
          try{
            PrintWriter output = new PrintWriter(client.getSocket().getOutputStream());
            
            output.println(incoming.remove());
            output.flush();
          }catch(IOException e) {
            e.printStackTrace();        
          }  
        }
      }
    }
    
  }
  
  //***** Inner class - thread for client connection
  class ConnectionHandler implements Runnable { 
    private PrintWriter output; //assign printwriter to network stream
    private BufferedReader input; //Stream for network input
    private Client client;  //keeps track of the client socket
    private boolean running;
    /* ConnectionHandler
     * Constructor
     * @param the socket belonging to this client connection
     */    
    ConnectionHandler(Socket s) { 
      this.client.setSocket(s);  //constructor assigns client to this    
      try {  //assign all connections to client
        this.output = new PrintWriter(client.getSocket().getOutputStream());
        InputStreamReader stream = new InputStreamReader(client.getSocket().getInputStream());
        this.input = new BufferedReader(stream);
      }catch(IOException e) {
        e.printStackTrace();        
      }            
      running=true;
    } //end of constructor
  
    
    /* run
     * executed on start of thread
     */
    public void run() {  

      //Get a message from the client
      String msg="";
      
         //Send a message to the client
      output.println("Clients connected:");
      output.flush(); 
      for (int i = 0; i<clientList.size();i++){
        output.println(clientList.get(i).getName());
        output.flush();
      }
      
      try {
          if (input.ready()) { //check for an incoming messge
            msg = input.readLine();  //get a message from the client
            System.out.println("client username: " + msg); 
            incoming.add(msg);
            //running=false; //stop receving messages
          }
          }catch (IOException e) { 
            System.out.println("Failed to recieve username");
            e.printStackTrace();
          }
          
          
      //Get a message from the client
      while(running) {  // loop unit a message is received        
        try {
          if (input.ready()) { //check for an incoming messge
            msg = input.readLine();  //get a message from the client
            System.out.println("msg from client: " + msg); 
            incoming.add(msg);
            //running=false; //stop receving messages
          }
          }catch (IOException e) { 
            System.out.println("Failed to receive msg from the client");
            e.printStackTrace();
          }
        }    
      
   
      
      //close the socket
      try {
        input.close();
        output.close();
        client.getSocket().close();
      }catch (Exception e) { 
        System.out.println("Failed to close socket");
      }
    } // end of run()
  } //end of inner class   
} //end of ChatProgramServer class
