import java.util.ArrayList;

public class Cascade extends ArrayList<Card>{

  private static final long serialVersionUID = 1L;

    public Cascade(){
      super(13);
    }

    public Card bottomCard(){
        if(!isEmpty()) return get(size()-1);
        return null;
    }

    public Card cardAt(int index){
        if(!isEmpty())return get(index);
        return null;
    }
    
    public Card topCard(){
      if(!isEmpty()) return get(0);
        return null;
    }

    public Card removeBottom(){
      if(!isEmpty()) return remove(size()-1);
      return null;
    }
    
    public Card removeTop(){
     if(!isEmpty()) return remove(0);
     return null;
    }

    public void addCard(Card card){
       add(card);
    }

    public ArrayList<Card> getCards(){
        return this;
    }
}
