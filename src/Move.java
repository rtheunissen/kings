public class Move{
  public static final int FROM_HAND = 0;
  public static final int CASCADE = 1;
  int source;
  int destination;
  public int type;

  public Move(int source, int destination, int type){
    this.source = source;
    this.destination = destination;
    this.type = type;
  }
  
  public void setSource(int source){
    this.source = source;
  }
  
  public void setDestination(int destination){
    this.destination = destination;
  }
  
  
  public void setType(int type){
    this.type = type;
  }

  public int getSource(){
    return source;
  }

  public int getDestination(){
    return destination;
  }

  public int getType(){
    return type;
  }

  public String toString(){
    return "Move={source=" + source + " destination=" + destination + " type=" + ((type == 0) ? "FROM_HAND" : "CASCADE") + "}";
  }
}
