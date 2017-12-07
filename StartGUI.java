import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTabbedPane;

class StartGUI extends JFrame{
  
  JFrame thisFrame;
  JPanel main;
  JLabel label;
  JTextField askName;
  JButton button;
  
  static String username = ("");
  
  public StartGUI(){
    
    thisFrame = new JFrame();
    thisFrame.setSize(400,400);
    thisFrame.setResizable(false);
    thisFrame.setLocationRelativeTo(null);
    
    label = new JLabel("Enter Username:");
    main = new JPanel();
    main.add(label);
    
    askName = new JTextField(25);
    main.add(askName);
    
    button = new JButton("Go!");
    button.addActionListener(new buttonListener());
    main.add(button);
    
    thisFrame.add(main);
    thisFrame.setVisible(true);
  }
  
  public static String giveName(){
    return username;
  }
  
  public class buttonListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      thisFrame.dispose();
      new ChatProgramClient().go();
    }
  }  
  
  public static void main (String[]args){
    new StartGUI();
  }
}
