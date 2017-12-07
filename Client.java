import java.io.*;
import java.net.*;

class Client{
  private String name = "";
  private Socket s;
  
  Client(String name, Socket s){
    this.name = name;
    this.s = s;
  }
  
  Client(){
    this.name = null;
    this.s = null;
  }
  
  public String getName(){
    return this.name;
  }
  
  public Socket getSocket(){
    return this.s;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public void setSocket (Socket s){
    this.s = s;
  }
    
}
