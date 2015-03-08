
import java.util.ArrayList;

public class AI{

   public Move move;
   public Hand hand;

   public int getFirstEmptyCorner(Cascade[] cascades){
      for (int i = 1; i < 7; i += 2) if (cascades[i].isEmpty()) return i;
      return -1;
   }

   private int getClosestEmptyCorner(Cascade[] cascades, int target){
      int prev = (target == 0) ? 7 : target - 1;
      int post = target + 1;
      if (cascades[prev].isEmpty()) return prev;
      if (cascades[post].isEmpty()) return post;
      return getFirstEmptyCorner(cascades);
   }

   private boolean thereIsAnEmptySpace(Cascade[] cascades){
      for (int i = 0; i < 8; i += 2) if (cascades[i].isEmpty()) return true;
      return false;
   }

   private int getFirstEmptySpace(Cascade[] cascades){
      for (int i = 0; i < 8; i += 2) if (cascades[i].isEmpty()) return i;
      return -1;
   }

   public Move getMove(){
      return move;
   }

   private boolean submitMove(Move move){
      this.move = move;
      return move != null;
   }

   private boolean cascadeMove(Cascade[] cascades){
      for (int i = 0; i < 8; i++) {
         if (cascades[i].isEmpty()) continue;
         Card topCard = cascades[i].topCard();
         int prev = (i == 0 ? 7 : i - 1);
         int post = (i == 7 ? 0 : i + 1);
         for (int j = 0; j < 8; j++) {
            if (topCard.follows(cascades[prev].bottomCard())) {
               return submitMove(new Move(i, prev, Move.CASCADE));
            }
            if (topCard.follows(cascades[post].bottomCard())) {
               return submitMove(new Move(i, post, Move.CASCADE));
            }
            if (topCard.follows(cascades[j].bottomCard())) {
               return submitMove(new Move(i, j, Move.CASCADE));
            }
         }
      }
      return false;
   }

   private boolean handMove(Cascade[] cascades, Hand hand){
      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < hand.size(); j++) {
            if (cascades[i].topCard() != null && cascades[i].topCard().follows(hand.getCard(j))) {
               Card card = hand.getCard(j);
               for (int k = 0; k < hand.size(); k++) {
                  if (card.follows(hand.getCard(k))) {
                     card = hand.getCard(k);
                     k = 0;
                  }
               }
               int emptySpace = getFirstEmptySpace(cascades);
               if (emptySpace >= 0)
                  return submitMove(new Move(hand.indexOf(card), emptySpace, Move.FROM_HAND));
            }
         }
      }

      for (int i = 0; i < hand.size(); i++) {
         for (int j = 0; j < 8; j++) {
            if (!cascades[j].isEmpty() && hand.getCard(i).follows(cascades[j].bottomCard())) {
               return submitMove(new Move(i, j, Move.FROM_HAND));
            }
         }
      }
      return false;
   }

   public boolean kingMove(Cascade[] cascades, Hand hand){
      if (hand.containsKing()) {
         return submitMove(new Move(hand.getKing(), getFirstEmptyCorner(cascades), 0));
      }

      for (int i = 0; i < 8; i += 2) {
         if ((!cascades[i].isEmpty()) && (cascades[i].topCard().isKing())) {
            return submitMove(new Move(i, getClosestEmptyCorner(cascades, i), 1));
         }
      }
      return false;
   }

   public boolean aiMove(Deck deck, Hand hand, Cascade[] cascades){
      this.hand = hand;

      if (kingMove(cascades, hand)) return true;
      if (cascadeMove(cascades)) return true;
      if (thereIsAnEmptySpace(cascades)) {
         if (cascadeMove(cascades)) return true;
         if (handMove(cascades, hand)) return true;

         ArrayList<ArrayList<Card>> handCascades = buildHandCascades(hand);
         int hcSize = handCascades.size();
         if (hcSize == 0) {
            return submitMove(new Move(hand.getRandomIndex(), getFirstEmptySpace(cascades), Move.FROM_HAND));
         } else if (hcSize == 1) {
            return submitMove(new Move(hand.indexOf(handCascades.get(0).get(handCascades.size() - 1)), getFirstEmptySpace(cascades), 0));
         } else {
            int maxIndex = 0;
            int maxSize = 0;
            for (int i = 0; i < hcSize; i++) {
               if (handCascades.get(i).size() > maxSize) {
                  maxSize = handCascades.get(i).size();
                  maxIndex = i;
               }
            }
            return submitMove(
                    new Move(
                    hand.indexOf((handCascades.get(maxIndex)).get(handCascades.get(maxIndex).size() - 1)),
                    getFirstEmptySpace(cascades),
                    Move.FROM_HAND));
         }
      } else {
         if (cascadeMove(cascades)) return true;
         if (handMove(cascades, hand)) return true;
      }
      return submitMove(null);
   }

   private ArrayList<ArrayList<Card>> buildHandCascades(Hand hand){
      ArrayList<ArrayList<Card>> handCascades = new ArrayList(hand.size());

      for (int i = 0; i < hand.size(); i++) {
         handCascades.add(new ArrayList());
      }
      Hand sorted = hand.sorted(Hand.ASCENDING);
      for (int i = 0; i < sorted.size() - 1; i++) {
         handCascades.get(i).add(sorted.getCard(i));
         for (int j = i + 1; j < sorted.size(); j++) {
            if (!(sorted.getCard(j).getValue() == sorted.getCard(i).getValue() + 1))
               continue;
            if (sorted.getCard(j).getColour() != sorted.getCard(i).getColour()) {
               (handCascades.get(i)).add(sorted.getCard(j));
            }
         }
      }
      for (int i = 0; i < handCascades.size() - 1; i++) {
         if ((handCascades.get(i)).isEmpty()) {
            handCascades.remove(i);
            i--;
         }
      }
      return handCascades;
   }
}