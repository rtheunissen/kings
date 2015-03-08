import java.util.*;

public class Deck extends ArrayList<Card>{

  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  private int deckSize;

  public Deck(){
    super(52);
    createCards();
  }

  public Deck(int[] array){
    for (int i = 0; i < 52; i++){
      add(new Card(array[i]));
    }
  }

  public int size(){
    return deckSize;
  }

  private void createCards(){
    deckSize = 52;
    for (int i = 0; i < 52; i++){
      add(new Card(i));
    }
  }

  public void renew(){
    deckSize = 52;
    clear();
    for (int i = 0; i < 52; i++){
      add(new Card(i));
    }
  }

  public void shuffle(){
    Random random = new Random();
    for (int i = 0; i < deckSize; i++){
      int index = random.nextInt(deckSize);
      Card temp = get(i);
      set(i, get(index));
      set(index, temp);
    }
  }

  public boolean isEmpty(){
    return deckSize == 0;
  }

  public Card drawCard(){
    if (deckSize > 0) return remove(--deckSize);
    return null;
  }

  public String toString(){
    if (deckSize == 0) return "[]";
    if (deckSize == 1) return "[" + get(0) + "]";
    StringBuilder string = new StringBuilder("[");
    for (int i = 0; i < deckSize - 1; i++){
      string.append(get(i).getCode());
      string.append(", ");
    }
    string.append(get(deckSize).getCode());
    string.append("]");
    return string.toString();
  }
}
