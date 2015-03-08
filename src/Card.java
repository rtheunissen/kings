public class Card{
    private int value, colour, code, suit;

    public static final int BLACK = 0;
    public static final int RED = 1;

    public static final int CLUB = 0;
    public static final int DIAMOND = 1;
    public static final int HEART = 2;
    public static final int SPADE = 3;

    public Card(int code){
        this.value = code / 4;
        this.suit = code - ((value - 1) * 4) - 4;
        this.colour = (code == 4 * value + 1 || code == 4 * value + 2) ? 1 : 0;
        this.code = code;
    }

    public String toString(){
        StringBuilder card = new StringBuilder();
        if (value < 10 && value != 0) card.append(value + 1);
        else if (value == 0) card.append("Ace");
        else if (value == 10) card.append("Jack");
        else if (value == 11) card.append("Queen");
        else if (value == 12) card.append("King");
        
        if (suit == CLUB) card.append(" of Clubs");
        else if (suit == DIAMOND) card.append(" of Diamonds");
        else if (suit == HEART) card.append(" of Hearts");
        else if (suit == SPADE) card.append(" of Spades");
        return card.toString();
    }

    public int getValue(){
        return value;
    }

    public int getColour(){
        return colour;
    }

    public int getCode(){
        return code;
    }

    public int getSuit(){
        return suit;
    }

    public boolean isKing(){
        return value == 12;
    }
    
    public boolean equals(Card that){
        return this.code==that.code;
    }
    
    public boolean follows(Card card){
      if (card == null) return false;
      return (this.value == (card.getValue() - 1) && this.colour != card.getColour());
    }
}
