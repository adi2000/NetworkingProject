import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTabbedPane;

class ChatProgramClient {
  
  String name=("");
  
  JPanel rightPanel;
  JTabbedPane tabbedPane;
  JButton sendButton, clearButton, exitButton;
  JTextField typeField;
  JTextArea msgArea;
  boolean close;
  
  JPanel leftPanel;
  JPanel searchPanel;
  JButton searchButton;
  JTextField searchBar;
  JPanel newPanel;
  JButton newButton;
  
  DefaultListModel<String> listModel;
  JList userList;
  ListSelectionModel listSelectionModel;
  
  Thread t;
  
  ArrayList<String> groupChats = new ArrayList<String>();
  ArrayList<String> tabNames = new ArrayList<String>();
  ArrayList<JTextArea> msgAreaList = new ArrayList<JTextArea>();
  
  Socket mySocket; //socket for connection
  BufferedReader input; //reader for network stream
  PrintWriter output;  //printwriter for network output
  boolean running = true; //thread status via boolean
  
  int key;
  
  public void go() {
    this.name = name;
    try{
      new GUIMaker().run();
      t = new Thread(new ConnectionHandler());
      t.start();       
    }catch(Exception e){
      System.out.println("error");
      e.printStackTrace();
    }
  }
  
  class GUIMaker implements Runnable{
    public void run(){
      
      JFrame window = new JFrame("Chat Client: "+ name);
      window.setResizable(true);
      window.setLocationRelativeTo(null);
      window.setSize(800,550);
      //window.setLayout(new GridLayout(0,2));
      
      //RIGHT PANEL CODE**************************************************/
      rightPanel = new JPanel();
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      
      JPanel textPanel = new JPanel();
      textPanel.setLayout(new FlowLayout());
      
      sendButton = new JButton("SEND");
      clearButton = new JButton("CLEAR");
      exitButton = new JButton("EXIT");
      
      sendButton.addActionListener(new sendListener());
      clearButton.addActionListener(new clearListener());
      exitButton.addActionListener(new exitListener());
      
      JLabel errorLabel = new JLabel("");
      
      typeField = new JTextField(32);
      
      msgArea = new JTextArea();
      msgArea.setEditable(false);
      Dimension d = new Dimension();
      d.width= 250;
      d.height = 500;
      msgArea.setPreferredSize(d);
      msgAreaList.add(msgArea);
      
      tabbedPane = new JTabbedPane();
      
      textPanel.add(typeField);
      textPanel.add(sendButton);
      textPanel.add(clearButton);
      textPanel.add(exitButton);
      
      tabbedPane.addTab("Broadcast", msgArea);
      rightPanel.add(tabbedPane);
      rightPanel.add(textPanel);
      
      // LEFT PANEL CODE *****************************************************/
      leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      
      searchPanel = new JPanel();
      searchBar = new JTextField(10);
      searchButton = new JButton("Search");
      searchPanel.add(searchBar);
      searchPanel.add(searchButton);
      
      //listPanel = new JPanel();
      listModel = new DefaultListModel<String>();
      listModel.addElement("ajkjkf");
      listModel.addElement("wewerwrewr");
      
      //list code: adding listSelectionModel
      userList = new JList(listModel);
      userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      listSelectionModel = userList.getSelectionModel();
      JScrollPane listScroll = new JScrollPane(userList);
      Dimension a = userList.getPreferredSize();
      a.width = 200;
      a.height = 300;
      listScroll.setPreferredSize(a);
      
      JButton newButton = new JButton("New Chat");
      newButton.addActionListener(new newListener());
      
      leftPanel.add(searchPanel);
      leftPanel.add(userList);
      leftPanel.add(newButton);
      
      /*****************************************************/
      window.add(BorderLayout.WEST,leftPanel);
      window.add(BorderLayout.EAST,rightPanel);
      
      
      window.setVisible(true);
    }
  }
  /**********************************************************************************************************************************/
  class ConnectionHandler implements Runnable{
    public void run(){
      
      try {
        //mySocket = new Socket("10.242.190.203", 796);
        mySocket = new Socket("127.0.0.1",5000); //attempt socket connection (local address). This will wait until a connection is made
        
        InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream()); //Stream for network input
        input = new BufferedReader(stream1);
        
        output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream
        System.out.println("connection achieved!");
      } catch (IOException e) {  //connection error occured
        System.out.println("Connection to Server Failed");
        e.printStackTrace();
      }
      
      output.println(StartGUI.giveName());
      output.flush();
      
      close = true;
      while (close){
        try{
          if (input.ready()){
            String text = input.readLine();
            //need to check for message type
            //group chats (0-9)
            //if (text)
            if ((text.substring(0,1)).equals("0")){
              text = text.substring(1, text.length());
              if((text.substring(0,1)).equals("n")){
                //new user
                text = text.substring(1, text.length());
                if (!(text.substring(0, text.indexOf("^"))).equals(name)){
                  listModel.addElement(text.substring(0, text.indexOf("^")));
                }
              }
              if ((text.substring(0,1)).equals("d")){
                text = text.substring(1, text.length());
                int index;
                for (int i = 0; i < listModel.size(); i++){
                  if (text.equals(listModel.getElementAt(i))){
                    index = i;
                  }
                  listModel.removeElementAt(i);
                }
              }
              if ((text.substring(0,1)).equals("g")){
                text = text.substring(1, text.length());
                groupChats.add(text);
              }
            }else{
              String sender = text.substring(0, text.indexOf(": "));
              System.out.println(sender);
              text = text.substring(text.indexOf(": ")+2, text.length());

              if ((text.substring(0,1)).equals("1")){
                String message = text.substring(2, text.length());
                (msgAreaList.get(0)).append(sender + ": " + message + "\n");
              }else if ((text.substring(0,1)).equals("2")){
                text = text.substring(2, text.length());
                String recipient = text.substring(0, 10);
                recipient = recipient.substring(0, recipient.indexOf("^"));
                boolean check = true;
                for (int i = 0; i < tabbedPane.getTabCount(); i ++){
                   if (recipient.equals(tabbedPane.getTitleAt(i))){
                     (msgAreaList.get(i)).append(sender + ": " + text + "\n");
                     check = false;
                  } //end of if tabbedpane title
                }if (check == true){
                  System.out.println("whatever");
                  makeNewTab(recipient);
                }//end of tabbedpane run through
              } //end of private message: "2"
            }
          } //end of input.ready()
        }catch (IOException e) {
          System.out.println("Failed to receive msg from the server");
          e.printStackTrace();
        }
      }
      
      try {  //after leaving the main loop we need to close all the sockets
        input.close();
        output.close();
        mySocket.close();
        System.out.println("closing everything");
      }catch (Exception e) { 
        System.out.println("Failed to close socket");
      }
    } //end of run()
  } //end of connectionhandler
  
  public void makeNewTab(String str){
    // String name = ("msgArea") + str;
    JTextArea msgArea = new JTextArea();
    msgArea.setEditable(false);
    Dimension d = new Dimension();
    d.width= 250;
    d.height = 500;
    msgArea.setPreferredSize(d);
    
    msgAreaList.add(msgArea);
        
    tabbedPane.addTab(str, msgArea);
  }
  
  public void sendBroad(){
    String text = typeField.getText();      
    output.println("1m" + text );
    output.flush();
    typeField.setText("");
  }
  
  public void sendPriv(){
    String text = typeField.getText();
    String to = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    while (to.length() < 10){
      to = to + ("^");
    }
    output.println("2m"+ to + text);
    output.flush();
    typeField.setText("");
  }
  
  /*public void sendGroup(String[] chatList){
   String text = typeField.getText();
   //output.println("*/
  
  
//****** Inner Classes for Action Listeners ****
  public class sendListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      if (tabbedPane.getSelectedIndex() == 0){
        sendBroad();
      }else if (tabbedPane.getSelectedIndex() > 0){
        System.out.println("okay");
        sendPriv();
      }
    }
  }
  
  //launches new tab when user selects other clients and makes a chat
  public class newListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      String str = ("");
      if (userList.getSelectedIndices().length > 1){    //multiple selections: group
        int[] newChatIndex = userList.getSelectedIndices();
        String[] newChatClients = new String[newChatIndex.length];
        
        for(int i = 0; i <newChatIndex.length; i ++){
          newChatClients[i] =(String)listModel.getElementAt(i);
          str = str + (String)listModel.getElementAt(i) +", ";
          makeNewTab(str);
        }       
      }else if (userList.getSelectedIndices().length == 1){      //single selection: private chat
        str = (String)listModel.getElementAt(userList.getSelectedIndex());
        makeNewTab(str);
      }
    }
  }
  
  public class clearListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      typeField.setText("");
    }
  }
  
  public class exitListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      close = false;
      output.println("3");
      output.flush();
      System.exit(0);
      
    }
  }
}

