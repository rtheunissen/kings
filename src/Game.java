
public class Game{

  private Deck deck = new Deck();
  private Hand  aiHand = new Hand();
  private Hand yourHand = new Hand();
  private Cascade[] cascades = new Cascade[8];
  private AI ai = new AI();
  public boolean playState = false;
  public boolean gameOver = false;
  public Painter painter = new Painter();

  public Game(){
    for (int i = 0; i < 8; i++){
      cascades[i] = new Cascade();
    }
  }
  
  public Painter getPainter(){
    return painter;
  }

  public boolean checkMoveFromHand(int handDragIndex, int dropIndex){
    Card yours = yourHand.getCard(handDragIndex);
    Cascade dest = cascades[dropIndex];
    if (dest.isEmpty() && dropIndex % 2 != 1) return true;
    return yours.follows(dest.bottomCard()) || ((dest.isEmpty() && dropIndex % 2 != 0) && yours.isKing());
  }

  public void sortHand(int ascending){
    yourHand.sort(ascending);
  }

  public boolean checkCascadeMove(int cascadeDragIndex, int handDropIndex){
    if (cascadeDragIndex == -1){ return false; }
    Card top = cascades[cascadeDragIndex].topCard();
    Cascade dest = cascades[handDropIndex];
    if (top == null) return false;
    if (handDropIndex % 2 == 1) return dest.isEmpty() && top.isKing() || top.follows(dest.bottomCard());
    return dest.isEmpty() || top.follows(dest.bottomCard());
  }

  public void makeMoveFromHand(Hand hand, int source, int destination){
    cascades[destination].addCard(hand.removeCard(source));
  }

  public void makeCascadeMove(int source, int destination){
    while (!cascades[source].isEmpty()){
      cascades[destination].addCard(cascades[source].removeTop());
    }
  }

  public void newGame(){
    clearAll();
    playState = true;
    gameOver = false;
    deck.renew();
    deck.shuffle();
  }
  private void clearAll(){
    aiHand.clear();
    yourHand.clear();
    for (Cascade c : cascades){
      c.clear();
    }
  }

  public void transferCascades(int source, int dest){
    int size = cascades[source].size();
    for (int i = 0; i < size; i++){
      cascades[dest].addCard(cascades[source].removeTop());
    }
  }

  public boolean playAiHand(){
    if (aiHand.isEmpty()){
       endGame();
      return false;
    }
    return ai.aiMove(deck, aiHand, cascades);
  }

  public AI getAi(){
    return ai;
  }

  public void endGame(){
    playState = false;
    gameOver = true;
  }

  public Deck getDeck(){
    return deck;
  }

  public void addCardToAi(){
    if(deck.isEmpty()) return;
    Card newCard = deck.drawCard();
    painter.captureDeck(deck.size());
    if (!aiHand.isEmpty()){
      Card top = aiHand.getCard(0);
      aiHand.set(0, newCard);
      aiHand.addCard(top);
    } else{
      aiHand.addCard(newCard);
    }
  }

  public Cascade[] getCascades(){
    return cascades;
  }

  public Hand getPlayerHand(){
    return yourHand;
  }

  public Hand getAiHand(){
    return aiHand;
  }

}
