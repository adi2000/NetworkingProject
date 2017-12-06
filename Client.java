class Client{
  String name = "";
  String clientIP = "";
  
  Client(String name, String clientIP){
    this.name = name;
    this.clientIP = clientIP;
  }
  
  public String getName(){
    return this.name;
  }
  
  public String getIP(){
    return this.name;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public void setIP(String clientIP){
    this.clientIP = clientIP;
  }
    
}
