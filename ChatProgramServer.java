/* [ChatProgramServer.java]
 * Description: This chat server accepts clients
 * and recieves messages that specify either a broadcast
 * or one on one private messages. It also handles messages
 * for adding and disconnecting users that it recives from
 * the client
 * @author David Stewart
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
  static Queue<String> privateIncoming = new LinkedList<String>();
  static ArrayList<Socket> clientList = new ArrayList<Socket>();
  static ArrayList<String> names = new ArrayList<String>();
  
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
    
    Socket client = new Socket();//hold the client connection
    
    try {
      Thread msgSend = new Thread(new MessageSender());
      msgSend.start();
      Thread privMsgSend = new Thread(new PrivateMessageSender());
      privMsgSend.start();
      serverSock = new ServerSocket(5000);  //assigns an port to the server
      // serverSock.setSoTimeout(5000);  //5 second timeout
      while(running) {  //this loops to accept multiple clients
        //client.clear();
           client = (serverSock.accept());  //wait for connection
           System.out.println("Client connected");
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
  
  class PrivateMessageSender implements Runnable {
    
    public void run(){
      
      while (running){
        send(clientList);
      }
      
    }
    
    public void send (ArrayList<Socket> list){
      Socket client;
      String msg;
      String name;
      int socketIndex = 0;
      System.out.print("");//Will not work unless this is here (no idea why)
      while (privateIncoming.size()>0){
      
      //to do:
      // find index of recipient username
        msg = privateIncoming.remove();
        name = getName(msg.substring(msg.indexOf(" ")+3,msg.indexOf(" ")+13));
        for (int i = 0; i<names.size();i++){
          if (names.get(i).equals(name)){
            socketIndex = i;
          }
        }
      //find corresponding socket
        client = list.get(socketIndex);
      //create printwriter
        try{
            PrintWriter output = new PrintWriter(client.getOutputStream());
            output.println(msg); 
            output.flush();
          }catch(IOException e) {
            e.printStackTrace();        
          }  
      }                 
      
    }
    
    public String getName(String msg){
      
      for (int i = 9;i>0;i--){
        if (msg.substring(i).equals("^")){
          msg=msg.substring(0,i);
        }else{
          return msg;
        }
      }
      return "";
    }
      
    
  }
    
    
  class MessageSender implements Runnable {
    
    public void run(){
      
      while (running){
        send(clientList);
      }
    }
    
    public void send(ArrayList<Socket> list){
      Socket client;
      String msg = "";
      System.out.print("");//Will not work unless this is here (no idea why)
      while (incoming.size()>0){ 
        msg = incoming.remove();
        
        for (int i = 0; i<list.size();i++){
          client = list.get(i);
          try{
            PrintWriter output = new PrintWriter(client.getOutputStream());
            //System.out.println("sending message: '"+msg+"'");
            output.println(msg); 
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
    private Socket client;  //keeps track of the client socket
    private boolean running;
    private int clientNumber;
    private String name;
    
    
    
    /* ConnectionHandler
     * Constructor
     * @param the socket belonging to this client connection
     */    
    ConnectionHandler(Socket s) { 
      client = s;  //constructor assigns client to this 
      try {  //assign all connections to client
        this.output = new PrintWriter(client.getOutputStream());
        InputStreamReader stream = new InputStreamReader(client.getInputStream());
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
      String name="";     
      
      boolean accepted = true;
      boolean gotUserName = false;
      
      while(!gotUserName){
        try {
          if (input.ready()) { //check for an incoming messge
            name = input.readLine();  //get a message from the client
            for (int i = 0; i<names.size();i++){
              if (name.equals(names.get(i))){
                accepted = false;
              }
            }
            if (accepted){//username free, can proceed
              System.out.println("client username: " + name);
              incoming.add("0n"+name+"^^^^^^^^^^".substring(name.length()));
              gotUserName = true;
            }else{//username already taken
              output.println("1");
              output.flush();
              accepted=true;
            }
            
          }
        }catch (IOException e) { 
          System.out.println("Failed to recieve username");
          e.printStackTrace();
        }
      }
      
      //send client list of all users already connected
      for (int i = 0;i<names.size();i++){
        output.println("0n"+names.get(i)+"^^^^^^^^^^".substring(names.get(i).length()));
        output.flush();
      }

      
      clientList.add(client);
      names.add(name);
    
    
      //Get a message from the client
      while(running) {  // loop unit a message is received        
        try {
          if (input.ready()) { //check for an incoming messge
            msg = input.readLine();  //get a message from the client
            if (msg.substring(0,1).equals("2")){//private
              privateIncoming.add(name+": "+msg);
              output.println(name+": "+msg);
              output.flush();
            }else if (msg.substring(0,1).equals("1")){//broadcast
              incoming.add(name+": "+msg);
            }else if (msg.equals("3")){//disconnect
              incoming.add("0d"+name+"^^^^^^^^^^".substring(name.length()));
              for (int i=0;i<names.size();i++){
                if (names.get(i).equals(name)){
                  names.remove(i);
                  clientList.remove(i);
                }
              }
              running=false;
            }
          }
          }catch (IOException e) { 
            System.out.println("Failed to receive msg from the client");
            e.printStackTrace();
          }
        }    
      
   
      
      //close the socket
      try {
        System.out.println("Disconnecting user: "+name);
        input.close();
        output.close();
        client.close();
      }catch (Exception e) { 
        System.out.println("Failed to close socket");
      }
      System.out.println("Disconnected");
    } // end of run()
    
  } //end of inner class   
} //end of ChatProgramServer class 
