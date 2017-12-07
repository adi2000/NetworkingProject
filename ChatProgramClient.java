import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTabbedPane;

class ChatProgramClient {
  //implements ListSelectionListener?
  
  ArrayList<String> chatList = new ArrayList<String>();
      
  JPanel rightPanel;
  JTabbedPane tabbedPane;
  JButton sendButton, clearButton;
  JTextField typeField;
  JTextArea msgArea;
  
  JPanel leftPanel;
  JPanel searchPanel;
  JButton searchButton;
  JTextField searchBar;
  DefaultListModel<String> listModel;
  JList userList;
  ArrayList<Client> users;
  
  Socket mySocket; //socket for connection
  BufferedReader input; //reader for network stream
  PrintWriter output;  //printwriter for network output
  boolean running = true; //thread status via boolean
  
  int key;
    
  public static void main(String[] args) { 
    new ChatProgramClient().go();
  }
    
  public void go() {
    //setting window
    JFrame window = new JFrame("Chat Client");
    
    //ALL RIGHT PANEL CODE**************************************************/
    rightPanel = new JPanel();
    rightPanel.setLayout(new GridLayout(2,0));
    
    JPanel textPanel = new JPanel();
    textPanel.setLayout(new FlowLayout());

    sendButton = new JButton("SEND");
    clearButton = new JButton("CLEAR");
    
    sendButton.addActionListener(new sendListener());
    clearButton.addActionListener(new clearListener());
       
    JLabel errorLabel = new JLabel("");
    
    typeField = new JTextField(10);
    
    msgArea = new JTextArea();
    JPanel msgPanel = new JPanel();
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("tab 1", msgArea);
    tabbedPane.addTab("tab 2", msgPanel);
    //msgPanel.add(tabbedPane);
    
    textPanel.add(typeField);
    textPanel.add(sendButton);
    textPanel.add(clearButton);
    
    rightPanel.add(tabbedPane);
    rightPanel.add(textPanel);
    
    /*****************************************************/
    leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    
    searchPanel = new JPanel();
    searchBar = new JTextField(25);
    searchButton = new JButton("Search");
    
    listModel = new DefaultListModel<String>();
    users = ChatProgramServer.getList();
    System.out.println(users);
    
    for (int i = 0; i < users.size(); i++){
      String name = (users.get(i)).getName();
      listModel.addElement((String)name);
    }
    
    userList = new JList(listModel);
    userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    //userList.addListSelectionListener(new ListListener());
    JScrollPane listScroll = new JScrollPane(userList);
    userList.setVisibleRowCount(10);
    
    Dimension d = userList.getPreferredSize();
    d.width = 400;
    d.height = 400;
    listScroll.setPreferredSize(d);
    
    leftPanel.add(searchPanel);
    leftPanel.add(userList);
       
    /*****************************************************/
    
    window.add(BorderLayout.EAST,rightPanel);
    window.add(BorderLayout.WEST,leftPanel);
    
    window.setSize(600,400);
    window.setVisible(true);
    
    /**********************************************************************************************************************************/
    
    // call a method that connects to the server 
    try {
      //mySocket = new Socket("10.242.190.203", 796);
      mySocket = new Socket("127.0.0.1",5000); //attempt socket connection (local address). This will wait until a connection is made
      
      InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream()); //Stream for network input
      input = new BufferedReader(stream1);
            
      output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream
      System.out.println("connection achieved");
    } catch (IOException e) {  //connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }

    while (running){
      try{
        msgArea.append(input.readLine());
      }catch (IOException e) { 
        System.out.println("Failed to receive msg from the server");
        e.printStackTrace();
      }
    }
    
    try {  //after leaving the main loop we need to close all the sockets
      input.close();
      output.close();
      mySocket.close();
    }catch (Exception e) { 
      System.out.println("Failed to close socket");
    }
  }
  
  public void send(){
    String text = typeField.getText();      
    output.println(text);
    output.flush();
    typeField.setText("");
      
    msgArea.append("\n" + text);
  }
  
  //****** Inner Classes for Action Listeners ****
  public class sendListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      send();
    }
  }
  
  public class listListener implements ListSelectionListener{
    public void valueChanged(ListSelectionEvent e){
      if (e.getValueIsAdjusting() == false){

      }
    }
  }
  
  public class enterListener implements KeyListener{
    public void keyTyped(KeyEvent e) {
      key = e.getKeyCode();
      System.out.println("123");
      if (key == KeyEvent.VK_ENTER && typeField.getText() != ("")){
        System.out.println("456");
        send();
      }
    }
    public void keyReleased(KeyEvent e){
      //code
    }
    public void keyPressed(KeyEvent e){
      //code
    }
  }
  
  public class clearListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      typeField.setText("");
    }
  }
}
