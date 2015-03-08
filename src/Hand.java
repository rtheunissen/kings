
import java.util.ArrayList;
import java.util.Random;

public class Hand{

   private ArrayList<Card> hand = new ArrayList<Card>(7);
   public static final int ASCENDING = 1;
   public static final int DECENDING = 2;

   public Hand(ArrayList<Card> hand){
      this.hand = hand;
   }

   public Hand(){
   }

   public void addCard(Card card){
      hand.add(card);
   }

   public Card getCard(int index){
      return hand.get(index);
   }

   public int getRandomIndex(){
      return new Random().nextInt(hand.size());
   }

   public ArrayList<Card> getHand(){
      return hand;
   }

   public int indexOf(Card card){
      for (int i = 0; i < hand.size(); i++) {
         if (hand.get(i).equals(card)) return i;
      }
      return -1;
   }

   public void sort(int ascending){
      if (size() < 2) return;
      Card temp, low;
      low = getCard(0);
      for (int i = 1; i < size(); i++) {
         low = getCard(i - 1);
         for (int j = i; j < size(); j++) {
            if (getCard(j).getValue() < low.getValue()) low = getCard(j);
         }
         temp = getCard(i - 1);
         int lowWas = indexOf(low);
         set(i - 1, low);
         set(lowWas, temp);
      }
      if (ascending == ASCENDING) return;
      else {
         reverse();
      }
   }

   public void reverse(){
      Hand temp = new Hand();
      while (!this.isEmpty()) temp.addCard(this.removeCard(size() - 1));
      setHand(temp.getHand());
   }

   public Hand sorted(int ascending){
      Hand temp = this.clone();
      temp.sort(ascending);
      return temp;
   }

   public void setHand(ArrayList<Card> that){
      this.hand = (ArrayList<Card>)that.clone();
   }

   @Override
   public Hand clone(){
      Hand newHand = new Hand();
      for (Card c : hand) newHand.addCard(c);
      return newHand;
   }

   public boolean isEmpty(){
      return hand.isEmpty();
   }

   public Card removeCard(int index){
      return hand.remove(index);
   }

   public void set(int index, Card card){
      hand.set(index, card);
   }

   public void clear(){
      hand.clear();
   }

   public int size(){
      return hand.size();
   }

   public boolean containsKing(){
      for (Card c : hand) if (c.getValue() == 12) return true;
      return false;
   }

   public int getKing(){
      for (int i = 0; i < hand.size(); i++) {
         if (hand.get(i).getValue() == 12) return i;
      }
      return -1;
   }

   @Override
   public String toString(){
      return hand.toString();
   }
}
