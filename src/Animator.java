
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Animator{

   private int animateX = 0;
   private int animateY = 0;
   private boolean flipped;
   private double scaleX = 1.0;
   private double animationRotation = 0;
   private ImageManager images = KingsCorners.imageLoader;
   private SoundManager sounds = new SoundManager();
   private int startX, startY, destX, destY, dest, sourceCascadeSize, totalX, totalY;
   double targetAngle, updateAngle;
   private int animationCardCode, totalAngle;
   private Cascade sourceCascade;
   private int updateX, updateY, offByX, offByY, countX, countY;
   private double divX, divY;
   private boolean deckEmpty;
   private final int ANIMATION_CONSTANT = 20; // lower = faster, larger gaps. 45 % 150/AC = 0

   public void setValues(Integer animateX, Integer animateY, Double scaleX,
                         Double animationRotation, Boolean flipped){
      this.animateX = animateX;
      this.animateY = animateY;
      this.scaleX = scaleX;
      this.animationRotation = animationRotation;
      this.flipped = flipped;
   }

   public void setFlipped(boolean flipped){
      this.flipped = flipped;
   }

   public void setAnimationRotation(double animationRotation){
      this.animationRotation = animationRotation;
   }

   public void setAnimateX(int animateX){
      this.animateX = animateX;
   }

   public void setAnimateY(int animateY){
      this.animateY = animateY;
   }

   public void setScaleX(double scaleX){
      this.scaleX = scaleX;
   }

   public void setIndexOfAiHand(int index){
   }

   public void setAnimateAiMoveFields(int indexOfAiHand, int animationCardCode,
                                      Rectangle2D handShape,
                                      Rectangle2D targetCascadeBounds, int destX,
                                      int destY, int animateX, int animateY,
                                      int targetAngle){
      this.destX = destX;
      this.destY = destY;
      this.animateX = this.startX = animateX;
      this.animateY = this.startY = animateY;
      this.targetAngle = targetAngle;
      this.animationCardCode = animationCardCode;
      this.totalX = Math.abs(startX - destX);
      this.totalY = Math.abs(startY - destY);
      this.divX = (double)totalX / ANIMATION_CONSTANT;
      this.divY = (double)totalY / ANIMATION_CONSTANT;
      this.updateX = (int)divX;
      this.updateY = (int)divY;
      this.offByX = this.offByY = 0;
      this.offByX = totalX % ANIMATION_CONSTANT;
      this.offByY = totalY % ANIMATION_CONSTANT;
      this.countX = this.countY = 0;
      this.updateAngle = targetAngle / (double)ANIMATION_CONSTANT;
   }

   public void setAnimateCascadeMoveFields(Cascade sourceCascade, int source,
                                           int dest, int sourceCascadeSize,
                                           int destCascadeSize,
                                           double animationRotation,
                                           int targetAngle, int startY,
                                           int cascadeOffsetY){
      this.sourceCascadeSize = sourceCascadeSize;
      this.dest = dest;
      this.animationRotation = animationRotation;
      this.animateY = this.startY = animateY;
      this.targetAngle = targetAngle;
      this.totalY = cascadeOffsetY;
      this.sourceCascade = sourceCascade;
      this.totalAngle = Math.abs(source * 45 - targetAngle);
   }

   public void setAnimateAiDrawFields(int destX, int destY, int deckX, int deckY,
                                      double targetAngle, boolean deckEmpty){
      this.destX = destX;
      this.destY = destY;
      this.targetAngle = targetAngle;
      this.totalX = Math.abs(deckX - destX);
      this.totalY = Math.abs(deckY - destY);
      this.divX = (double)totalX / ANIMATION_CONSTANT;
      this.divY = (double)totalY / ANIMATION_CONSTANT;
      this.updateX = (int)divX;
      this.updateY = (int)divY;
      this.offByX = this.offByY = 0;
      this.offByX = totalX % ANIMATION_CONSTANT;
      this.offByY = totalY % ANIMATION_CONSTANT;
      this.countX = this.countY = 0;
      this.updateAngle = targetAngle / (double)ANIMATION_CONSTANT;
      this.deckEmpty = deckEmpty;
   }

   public void setAnimatePlayerDrawFields(int destX, int destY, int deckX,
                                          int deckY, double targetAngle,
                                          int animatingCardCode){
      this.animateX = this.startX = deckX;
      this.animateY = this.startY = deckY;
      this.targetAngle = targetAngle;
      this.destX = destX;
      this.destY = destY;
      this.flipped = false;
      this.scaleX = 1.0;
      this.animationRotation = 0;
      this.animationCardCode = animatingCardCode;
      this.totalX = Math.abs(deckX - destX);
      this.totalY = Math.abs(deckY - destY);
      this.divX = (double)totalX / ANIMATION_CONSTANT;
      this.divY = (double)totalY / ANIMATION_CONSTANT;
      this.updateX = (int)divX;
      this.updateY = (int)divY;
      this.offByX = this.offByY = 0;
      this.offByX = totalX % ANIMATION_CONSTANT;
      this.offByY = totalY % ANIMATION_CONSTANT;
      this.countX = this.countY = 0;
      this.updateAngle = targetAngle / (double)ANIMATION_CONSTANT;
   }

   private void paintCard(Graphics2D g, int c, int x, int y){
      g.drawImage(images.getCard(c), x, y, 86, 126, null);
   }

   public void paintCard(Graphics2D g, int c, int x, int y, int width,
                         int height){
      g.drawImage(images.getCard(c), x, y, width, height, null);
   }

   public void log(Object s){
      System.out.println(s);
   }

   public boolean animateAiMove(Graphics2D g, AffineTransform a, Game game){
      if (GamePanel.config.isMoveInstant()) return true;
      boolean check = false;
      if (!flipped) {
         scaleX -= 0.2;
         if (scaleX < 0.0001) {
            sounds.playSound("flick");
            flipped = true;
            check = true;
         } else {
            paintCard(g, 52, (int)(animateX + (1 - scaleX) * 43), animateY, (int)(scaleX * 43), 126);
         }
      }
      if (flipped && scaleX < 1.0) {
         scaleX += 0.2;
         if (!check && scaleX != 1.0)
            paintCard(g, animationCardCode, animateX + (int)((1 - scaleX) * 43), animateY, (int)((scaleX) * 86), 126);
      }
      if (flipped && scaleX == 1.0) {

         if (Math.abs(animationRotation - targetAngle) > 0.001) {
            animationRotation += updateAngle;
         }

         if (animateX - destX != 0)
            animateX -= (updateX + ((countX++ < offByX) ? 1 : 0));
         if (animateY - destY != 0) {
            if (animateY - destY < 0)
               animateY += (updateY + ((countY++ < offByY) ? 1 : 0));
            else animateY -= (updateY + ((countY++ < offByY) ? 1 : 0));
         }

         if (!(animateX == destX && animateY == destY)) {
            a.rotate(Math.toRadians(animationRotation), animateX, animateY);
            g.setTransform(a);
            paintCard(g, animationCardCode, animateX, animateY);
         }
      }
      boolean finished = animateX == destX && animateY == destY;
      if (finished) sounds.playSound("tick");
      return finished;
   }

   public boolean animateCascadeMove(Graphics2D g, AffineTransform a){
      if (GamePanel.config.isMoveInstant()) return true;
      int y = 423;
      int x = 383;
      int updatedY = animateY - startY;
      int uy = 0;

      double angle = 150 / (double)ANIMATION_CONSTANT;
      if (animationRotation != targetAngle) {
         animationRotation += (animationRotation < targetAngle) ? angle : -angle;
      }

      uy = (int)((double)totalY / ((double)totalAngle / angle)) + 1;
      if (totalY - updatedY < uy) uy = totalY - updatedY;
      animateY += uy;

      a.rotate(Math.toRadians(animationRotation), 426, 360);
      g.setTransform(a);

      y += updatedY + ((dest % 2 == 1) ? 44 : 0);
      for (int j = 0; j < sourceCascadeSize; j++) {
         y += ((j != sourceCascadeSize && j < 5) ? 22 : 5);
         paintCard(g, sourceCascade.cardAt(j).getCode(), x, y);
      }
      return (animationRotation == targetAngle);
   }

   public boolean animateCascadeDeal(Graphics2D g, AffineTransform a, Game game,
                                     int cascadeIndex){
      if (GamePanel.config.isDealInstant()) return true;
      Deck deck = game.getDeck();
      int n = cascadeIndex;
      int destX = (((n + 1) % 2 == 1) ? 383 : ((n == 1) ? 383 - 43 : 383 + 43));
      int destY = 442 - ((n == 0) ? 0 : ((n == 1) ? 125 : ((n == 2) ? 296 : 125)));

      if (!flipped) {
         scaleX -= 0.1;
         if (scaleX < 0.0001) {
            sounds.playSound("flick");
            flipped = true;
         }
         if (scaleX > 0) {
            if (cascadeIndex == 2) {
               a.rotate(Math.toRadians(180), animateX + 43, animateY + 63);
               g.setTransform(a);
            }
            paintCard(g, 52, animateX + (int)((1 - scaleX) * 43), animateY, (int)((scaleX) * 86), 126);
         }
      }
      if (flipped && scaleX < 1.0) {
         scaleX += 0.1;
         if (cascadeIndex == 2) {
            a.rotate(Math.toRadians(180), animateX + 43, animateY + 63);
            g.setTransform(a);
         }
         paintCard(g, deck.get(deck.size() - 1).getCode(), animateX + (int)((1 - scaleX) * 43), animateY, (int)((scaleX) * 86), 126);
         a.setToIdentity();
      }
      if (flipped && scaleX == 1.0) {
         int rotatedAngle = 45 * (cascadeIndex * 2);
         if (rotatedAngle == 180) {
            animationRotation = 0;
            rotatedAngle = 0;
         }
         if (cascadeIndex == 3) rotatedAngle = -90;
         if (!(animateX == destX && animateY == destY)) {
            int anchorX = (cascadeIndex == 3) ? animateX + 86 : (cascadeIndex == 2) ? animateX + 43 : animateX;
            int anchorY = (cascadeIndex == 2) ? animateY + 63 : animateY;
            a.rotate(Math.toRadians((cascadeIndex == 2) ? animationRotation + 180 : animationRotation), anchorX, anchorY);
            g.setTransform(a);
            paintCard(g, deck.get(deck.size() - 1).getCode(), animateX, animateY);
            a.setToIdentity();
         }

         int speed = (cascadeIndex == 0 || cascadeIndex == 2) ? 10 : 5;
         if (animateX < destX)
            animateX += (destX - animateX < speed) ? destX - animateX : speed;
         else if (animateX > destX)
            animateX -= (animateX - destX < speed) ? animateX - destX : speed;
         if (animateY < destY)
            animateY += (destY - animateY < speed) ? destY - animateY : speed;
         else if (animateY > destY)
            animateY -= (animateY - destY < speed) ? animateY - destY : speed;

         if (animationRotation != rotatedAngle)
            animationRotation += (animationRotation < rotatedAngle) ? 15 : -15;
      }
      return (animateX == destX && animateY == destY);
   }

   public boolean animateAiDraw(Graphics2D g, AffineTransform a, Game game,
                                int cardsToBeAdded){
      if (deckEmpty || ((cardsToBeAdded > 0 && GamePanel.config.isDealInstant())) || (cardsToBeAdded <= 0 && GamePanel.config.isDrawInstant()))
         return true;
      if (animationRotation != targetAngle) animationRotation += updateAngle;
      if (animateX - destX != 0)
         animateX += (updateX + ((countX++ < offByX) ? 1 : 0));
      if (animateY - destY != 0) {
         if (animateY - destY < 0)
            animateY += (updateY + ((countY++ < offByY) ? 1 : 0));
         else animateY -= (updateY + ((countY++ < offByY) ? 1 : 0));
      }
      boolean finished = animateX == destX && animateY == destY;
      a.rotate(Math.toRadians(animationRotation), animateX, animateY);
      g.setTransform(a);
      if (!finished) paintCard(g, 52, animateX, animateY);
      a.setToIdentity();
      return finished;
   }

   public boolean animatePlayerDraw(Graphics2D g, AffineTransform a, Game game,
                                    int cardsToBeAdded){
      if ((cardsToBeAdded > 0 && GamePanel.config.isDealInstant()) || (cardsToBeAdded <= 0 && GamePanel.config.isDrawInstant()))
         return true;
      boolean check = false;
      if (!flipped) {
         scaleX -= 0.1;
         if (scaleX < 0.0001) {
            sounds.playSound("flick");
            flipped = true;
         }
         if (scaleX > 0) {
            paintCard(g, 52, animateX + (int)((1 - scaleX) * 43), animateY, (int)((scaleX) * 86), 126);
         }
      }
      if (flipped && scaleX < 1.0) {
         if ((scaleX += 0.1) != 1.0)
            paintCard(g, animationCardCode, animateX + (int)((1 - scaleX) * 43), animateY, (int)((scaleX) * 86), 126);
      }
      if (flipped && scaleX == 1.0 && !check) {
         if (animationRotation != targetAngle) animationRotation += updateAngle;
         if (animateX - destX != 0)
            animateX += (updateX + ((countX++ < offByX) ? 1 : 0));
         if (animateY - destY != 0) {
            if (animateY - destY < 0)
               animateY += (updateY + ((countY++ < offByY) ? 1 : 0));
            else animateY -= (updateY + ((countY++ < offByY) ? 1 : 0));
         }
         if (Math.abs(animationRotation - targetAngle) < 0.001)
            animationRotation += updateAngle;
         a.rotate(Math.toRadians(animationRotation), animateX, animateY);
         g.setTransform(a);
         paintCard(g, animationCardCode, animateX, animateY);
      }
      a.setToIdentity();
      return animateX == destX && animateY == destY;
   }
}
