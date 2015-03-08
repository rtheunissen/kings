
import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class Painter{

   private BufferedImage aiHandCapture;
   private BufferedImage playerHandCapture;
   private BufferedImage cascadeCardsCapture;
   private BufferedImage deckCapture;
   ImageManager images = KingsCorners.imageLoader;
   private double handAngle = 4.2;
   private int handHover = -1;

   public Painter(){
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      aiHandCapture = gc.createCompatibleImage(427, 330, BufferedImage.TRANSLUCENT);
      playerHandCapture = gc.createCompatibleImage(427, 330, BufferedImage.TRANSLUCENT);
      cascadeCardsCapture = gc.createCompatibleImage(853, 720, BufferedImage.TRANSLUCENT);
      deckCapture = gc.createCompatibleImage(97, 137, BufferedImage.TRANSLUCENT);
   }

   public void paintCard(Graphics2D g, int c, int x, int y){
      g.drawImage(images.getCard(c), x, y, 86, 126, null);
   }

   public void paintCard(Graphics2D g, int c, int x, int y, int width,
                         int height){
      g.drawImage(images.getCard(c), x, y, width, height, null);
   }

   public void setRenderingHints(Graphics2D g){
      g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
   }
   
   public void setHandHover(int handHover){
      this.handHover = handHover;
   }
   
   public int getHover(){
      return handHover;
   }

   public void captureAiHand(AffineTransform a, Game game,
                             boolean animatingAiDraw, int indexOfAiHand,
                             boolean neglect){
      a.setToIdentity();
      images.clearImage(aiHandCapture);
      Graphics2D g = aiHandCapture.createGraphics();
      setRenderingHints(g);
      int handsize = game.getAiHand().size();
      double initialAngle = Math.toRadians(-handAngle * (handsize + ((animatingAiDraw) ? 0 : 1)) / 2);
      a.rotate(initialAngle, 213, 342 - (5 * handsize / 4));
      g.setTransform(a);
      int x = 170; // x-location
      int y = 102 - (5 * handsize / 4); // y-location h / 8 - h / 32 + h/48;
      for (int i = handsize; i > 0; i--) {
         a.rotate(Math.toRadians(handAngle), 213, 342 - (5 * handsize / 4));
         g.setTransform(a);
         if (i != handsize - indexOfAiHand) {
            paintCard(g, 52, x, y);
         }
      }
      a.setToIdentity();
   }

   public void paintPlaceHolders(Graphics2D g, AffineTransform a, Game game,
                                 int cascadeDragIndex){
      for (int i = 0; i < 8; i++) {
         g.setTransform(a);
         g.drawImage(images.getPNG("king-place"), 382, (i % 2 == 1 ? 490 : 445), 86, 126, null);
         a.rotate(Math.toRadians(45), 426, 360);
      }
   }

   public void captureDeck(int deckSize){
       images.clearImage(deckCapture);
      if (deckSize <= 0) return;
      Graphics2D g = deckCapture.createGraphics();
      setRenderingHints(g);
      int size = deckSize / 4 + 1;
      for (int i = 0; i < size; i++) {
         g.drawImage(images.getCard(52), 13 - (deckSize) / 4 + 1 - i + size - 2, 11 - (deckSize) / 4 + 1 - i + size, 86, 126, null);
      }
   }

   public void capturePlayerHand(AffineTransform a, Game game,
                                 boolean animatingPlayerDraw, int handDragIndex){
      images.clearImage(playerHandCapture);
      Graphics2D g = playerHandCapture.createGraphics();
      setRenderingHints(g);
      Hand current = game.getPlayerHand();
      int handsize = current.size();
      int anchorX = 213;
      int anchorY = 342 - (5 * handsize / 4);
      double initialAngle = Math.toRadians(-handAngle * (handsize + ((animatingPlayerDraw) ? 2 : 1)) / 2);
      a.rotate(initialAngle, anchorX, anchorY);
      double angle = Math.toRadians(handAngle);
      int x = 170; // x-location
      int y = 102 - (5 * handsize / 4);// y-location
      for (int i = 0; i < handsize; i++) {
         a.rotate(angle, anchorX, anchorY);
         if (i == handDragIndex) continue;
         g.setTransform(a);
         paintCard(g, current.getCard(i).getCode(), x, (i == handHover) ? y - 3 : y);
      }
      a.setToIdentity();
      g.setTransform(a);
   }

   public void captureCascadeCards(AffineTransform a, Game game,
                                   int cascadeDragIndex, int cascadeAnimateIndex){
      images.clearImage(cascadeCardsCapture);
      Graphics2D g = cascadeCardsCapture.createGraphics();
      setRenderingHints(g);
      int anchorX = 426;
      int anchorY = 360;
      int x, y;
      int i = 0;
      Cascade[] cascades = game.getCascades();
      for (int k = 0; k < 8; k++, i++) {
         if (i == 8) i = 0;
         if (i == cascadeDragIndex || i == cascadeAnimateIndex) {
            continue;
         }
         Cascade current = cascades[i];
         int size = current.size();
         a.setToIdentity();
         x = 383; // 386
         y = 423; // 442
         a.rotate(Math.toRadians(i * 45), anchorX, anchorY);
         if (i % 2 == 1) a.translate(0, 45);
         g.setTransform(a);
         if (size == 13) y += 22;
         for (int j = 0; j < size; j++) {
            if (size == 13) y += 5;
            else y += ((j != size && j < 5) ? 22 : 5);
            paintCard(g, current.cardAt(j).getCode(), x, y);
         }
      }
      a.setToIdentity();
   }

   public void paintCascadeCapture(Graphics2D g, AffineTransform a, Game game,
                                   int cascadeDragIndex, int dropIndex,
                                   int dragX, int dragY){
      a.setToIdentity();
      g.setTransform(a);
      setRenderingHints(g);
      g.drawImage(cascadeCardsCapture, 0, 0, 853, 720, null);

      int x = 0, y = 0;
      if (cascadeDragIndex != -1) {
         x = dragX - 43;
         y = dragY - 63;
         a.setToIdentity();
         a.rotate(Math.toRadians(45 * ((dropIndex == -1) ? dropIndex + 1 : dropIndex)), dragX, dragY);
         g.setTransform(a);
         Cascade[] cascades = game.getCascades();
         Cascade current = cascades[cascadeDragIndex];
         int size = current.size();
         if (size == 13) y += 22;
         for (int j = 0; j < size; j++) {
            if (size == 13) y += 5;
            else y += ((j != size && j < 5) ? 22 : 5);
            paintCard(g, current.cardAt(j).getCode(), x, y);
         }
      }
   }

   public void clearImages(){
      images.clearImage(aiHandCapture);
      images.clearImage(playerHandCapture);
      images.clearImage(cascadeCardsCapture);
   }

   public void paintDeckCapture(Graphics2D g){
      g.drawImage(deckCapture, 383 - 13, 297 - 11, 97, 137, null);
   }

   public void paintPlayerCapture(Graphics2D g, AffineTransform a, Game game,
                                  int handDragIndex, int dragX, int dragY,
                                  int dropIndex){
      a.setToIdentity();
      g.setTransform(a);
      g.drawImage(playerHandCapture, 853, 390, 427, 330, null);
      if (handDragIndex != -1) {
         a.rotate(Math.toRadians(45 * ((dropIndex == -1 ? 0 : dropIndex))), dragX, dragY);
         g.setTransform(a);
         paintCard(g, game.getPlayerHand().getCard(handDragIndex).getCode(), dragX - 43, dragY - 63);
      }
   }

   public void paintAiCapture(Graphics2D g, AffineTransform a){
      a.setToIdentity();
      g.setTransform(a);
      g.drawImage(aiHandCapture, 853, 0, 427, 330, null);
   }

   public void paintBackground(Graphics2D g){
      g.drawImage(images.get("background3", ".gif"), 0, 0, 855, 720, null);
      g.drawImage(images.getPNG("right-panel"), 855, 0, 426, 720, null);
   }
}
