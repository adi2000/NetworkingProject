import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

class ChatProgramClient {
  
  //GUI variables
  JButton sendButton, clearButton;
  JTextField typeField;
  JTextArea msgArea;  
  JPanel southPanel;
  
  Socket mySocket;
  BufferedReader input;
  Scanner write;
  PrintWriter output;
  boolean running = true;
  
  public static void main(String[] args) { 
    new ChatProgramClient().go();
  }
    
  public void go() {
    
    System.out.println("attempting to make a connection");
    
    try{
      
      mySocket = new Socket("10.242.190.203", 796); //local address socket
      InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream());
      input = new BufferedReader(stream1);
      write = new Scanner(System.in);
      output = new PrintWriter(mySocket.getOutputStream());
      
    } catch (IOException e){
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    
    System.out.println("connection made"); 
            
    JFrame window = new JFrame("Chat Client");
    southPanel = new JPanel();
    southPanel.setLayout(new GridLayout(2,0));
    
    sendButton = new JButton("SEND");
    clearButton = new JButton("CLEAR");
    
    JLabel errorLabel = new JLabel("");
    
    typeField = new JTextField("", 10);
    
    msgArea = new JTextArea();
    msgArea.setEditable(false);
    
    southPanel.add(typeField);
    southPanel.add(sendButton);
    southPanel.add(errorLabel);
    southPanel.add(clearButton);
    
    window.add(BorderLayout.CENTER,msgArea);
    window.add(BorderLayout.SOUTH,southPanel);
    
    window.setSize(400,400);
    window.setVisible(true);
    
    
    String str;
   
    
    String msg = "";
    
   // System.out.println("0");
    while(running){
      
    str = write.nextLine();
    output.println(str);  
    output.flush();
    
    if (str.equals("end")){
      running = false;
    }
     // System.out.println("1");
      try{
        //System.out.println("2");
        if (input.ready()){
          msg = input.readLine();
          System.out.println("msg from server:" + msg);
          //running = false;
        }
      } catch(IOException e){
        System.out.println("Failed to receive msg from the server");
        e.printStackTrace();
      }
    }
          
     try {  //after leaving the main loop we need to close all the sockets
      input.close();
      output.close();
      mySocket.close();
      System.out.println("closed socket");
    }catch (Exception e) { 
      System.out.println("Failed to close socket");
    }
  }
}