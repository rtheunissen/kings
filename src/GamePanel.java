
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.*;

public class GamePanel extends JPanel{

   private static final long serialVersionUID = 1L;
   private Game game = new Game();
   private Painter painter = game.getPainter();
   public static Configuration config;
   private Animator animator = new Animator();
   private JLabel[] buttons = new JLabel[6];
   private ImageManager images = KingsCorners.imageLoader;
   private SoundManager sounds = new SoundManager();
   private Cursor hand, grab;
   private ArrayList<Shape> playerHandShapes = new ArrayList<Shape>();
   private ArrayList<Shape> aiHandShapes = new ArrayList<Shape>();
   private ArrayList<Shape> handDropShapes = new ArrayList<Shape>(8);
   private ArrayList<Shape> cascadeDragShapes = new ArrayList<Shape>(8);
   private ArrayList<Shape> cascadeTopLefts = new ArrayList<Shape>(8);
   private Shape[] cascadeTargets = new Shape[8];
   private Shape openHandSpace;
   private Shape openAiHandSpace;
   private int handDragIndex = -1;
   private int dropIndex = 0;
   private int cascadeDragIndex = -1;
   private int dragX, dragY;
   private boolean animatingPlayerDraw;
   private boolean animatingAiDraw;
   private boolean animatingCascade;
   private boolean animatingCascadeDeal;
   private boolean animatingAiMove;
   private double handAngle = 4.2;
   private int cascadeAnimateIndex = -1;
   private int indexOfAiHand = -1;
   private RoundRectangle2D emptyPlayerCardShape;
   private RoundRectangle2D emptyAiCardShape;
   private int cardsToBeAdded;
   private int targetCascade;
   private boolean instructionsVisible;
   private AffineTransform a = new AffineTransform();

   public GamePanel(){
      super(true);
      this.setFocusable(true);
      this.setFocusTraversalKeysEnabled(false);
      this.setLayout(null);
      this.addKeyListener(new KeyboardManager());
      this.addMouseListener(new MouseManager());
      this.addMouseMotionListener(new MouseManager());
      this.setMinimumSize(new Dimension(1280, 720));
      this.setMaximumSize(new Dimension(1280, 720));
      this.setPreferredSize(new Dimension(1280, 720));
      this.setSize(new Dimension(1280, 720));
      this.setBounds(0, 0, 1280, 720);

      try {
         config = new Configuration(this);
      } catch (Exception e) {
      }
      GamePanel.config.setVisible(false);
      GamePanel.config.setBounds(864, 400, 406, 310);
      this.add(config);

      hand = Toolkit.getDefaultToolkit().createCustomCursor(images.getResourceImage("grab", ".gif"), new Point(8, 8), "Hand");
      grab = Toolkit.getDefaultToolkit().createCustomCursor(images.getResourceImage("grabbing", ".gif"), new Point(6, 5), "Grab");

      buttons[0] = new JLabel("New Game");
      buttons[1] = new JLabel("Settings");
      buttons[2] = new JLabel("Exit");
      buttons[3] = new JLabel("End Turn");
      buttons[4] = new JLabel("?");
      buttons[5] = new JLabel("Kings in the Corner");

      buttons[0].setBounds(861, 364, 130, 26);
      buttons[1].setBounds(999, 330, 130, 26);
      buttons[2].setBounds(1137, 330, 130, 26);
      buttons[3].setBounds(999, 364, 130, 26);
      buttons[4].setBounds(1265, 2, 15, 15);
      buttons[5].setBounds(860, 330, 130, 26);

      setup();
   }

   private void setup(){
      emptyAiCardShape = new RoundRectangle2D.Float(1023, 102, 86, 126, 7, 7);
      openAiHandSpace = new RoundRectangle2D.Float(1023, 102, 0, 0, 7, 7);
      emptyPlayerCardShape = new RoundRectangle2D.Float(1023, 492, 86, 126, 7, 7);
      openHandSpace = new RoundRectangle2D.Float(1023, 492, 0, 0, 7, 7);

      int x = 383;
      int y = 442;
      handDropShapes.clear();
      cascadeDragShapes.clear();
      cascadeTopLefts.clear();
      a.setToIdentity();
      for (int u = 0; u < 8; u++) {
         a.rotate(Math.toRadians(u * 45), 426, 360);
         if (u % 2 == 1) a.translate(0, 45);
         cascadeDragShapes.add(a.createTransformedShape(new RoundRectangle2D.Float(x, y, 86, 126, 4, 4)));
         a.translate(0, -45);
         handDropShapes.add(a.createTransformedShape(new RoundRectangle2D.Float(x, y, 86, 400, 4, 4)));
         a.translate(0, 45);
         cascadeTopLefts.add(a.createTransformedShape(new Rectangle2D.Float(x, y, 0, 0)));
         a.setToIdentity();
      }
      for (int i = 0; i < buttons.length; i++) {
         buttons[i].setIcon(images.getButtonIcons()[i]);
         buttons[i].setBackground(new Color(0, 0, 0, 0));
         buttons[i].setForeground(new Color(0, 0, 0, 0));
         this.add(buttons[i]);
      }
      painter.captureDeck(game.getDeck().size());
      repaint();
   }

   @Override
   public void paintComponent(Graphics graphics){
      super.paintComponent(graphics);
      Graphics2D g = (Graphics2D)graphics;
      painter.setRenderingHints(g);
      painter.paintBackground(g);
      painter.paintDeckCapture(g);
      painter.paintPlaceHolders(g, a, game, cascadeDragIndex);

      if (animatingCascadeDeal || ((animatingPlayerDraw || animatingAiDraw) && cardsToBeAdded > 0))
         sleep((10 - config.getDealSpeed()));
      else if (animatingAiDraw || animatingPlayerDraw)
         sleep(10 - config.getDrawSpeed());
      if (animatingCascade) sleep(10 - config.getAiMoveSpeed());
      if (animatingAiMove || animatingCascade)
         sleep((10 - config.getAiMoveSpeed()) * 2);
      if (animatingCascadeDeal) sleep(10 - config.getDealSpeed());

      if (game.gameOver) {
         a.setToIdentity();
         g.setTransform(a);
         if (game.getAiHand().isEmpty()) {
            g.drawImage(images.getPNG("lose"), 970, 128, 200, 75, null);
         } else if (game.getPlayerHand().isEmpty())
            g.drawImage(images.getPNG("win"), 970, 514, 200, 75, null);
      }

      painter.paintCascadeCapture(g, a, game, cascadeDragIndex, dropIndex, dragX, dragY);

      if (animatingCascadeDeal) {
         if (animator.animateCascadeDeal(g, a, game, targetCascade)) {
            sounds.playSound("tick");
            animatingCascadeDeal = false;
            game.getCascades()[targetCascade * 2].addCard(game.getDeck().drawCard());
            setCascadeTargets();
            painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
            animator.setFlipped(false);
            if (++targetCascade < 4) {
               animatingCascadeDeal = true;
               animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, false);
               repaint(0, 0, 720, 720);
            }
            if (targetCascade == 4) {
               cardsToBeAdded = 7;
               setupPlayerDraw();
               animatingPlayerDraw = true;
            }
         } else {
            repaint(0, 0, 720, 720);
         }
      }
      painter.paintPlayerCapture(g, a, game, handDragIndex, dragX, dragY, dropIndex);
      if (animatingPlayerDraw) {
         if (animator.animatePlayerDraw(g, a, game, cardsToBeAdded)) {
            animatingPlayerDraw = false;
            sounds.playSound("tick");
            game.getPlayerHand().addCard(game.getDeck().drawCard());
            setPlayerHandShapes();
            painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);
            boolean check = cardsToBeAdded > 0;
            if (cardsToBeAdded-- > 0) {
               animatingPlayerDraw = true;
               setupPlayerDraw();
               animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, false);
            }
            if (check && cardsToBeAdded == 0) {
               cardsToBeAdded = 7;
               animator.setAnimationRotation(0);
               setupAiDraw();
               animatingAiDraw = true;
               animatingPlayerDraw = false;
            }
            repaint();
         } else {
            repaint();
         }
      }

      if (animatingAiDraw) {
         if (animator.animateAiDraw(g, a, game, cardsToBeAdded)) {
            animatingAiDraw = false;
            if (game.getDeck().size() > 0) {
               sounds.playSound("tick");
               game.addCardToAi();
            }
            boolean check = cardsToBeAdded > 0;
            setAiHandShapes();
            painter.captureAiHand(a, game, animatingAiDraw, -1, false);
            animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, true);
            if (cardsToBeAdded-- > 0) {
               setupAiDraw();
               animatingAiDraw = true;
            }
            if (check && cardsToBeAdded == 0) {
               animator.setFlipped(false);
               setupPlayerDraw();
               animatingPlayerDraw = true;
               animatingAiDraw = false;
            } else if (!check) {
               animator.setAnimateX(0);
               animator.setAnimateY(0);
               playAiHand();
            }
            repaint();
         } else {
            repaint();
         }
      }
      painter.paintAiCapture(g, a);
      if (animatingAiMove) {
         if (animator.animateAiMove(g, a, game)) {
            game.getCascades()[targetCascade].addCard(game.getAiHand().removeCard(indexOfAiHand));
            setAiHandShapes();
            setCascadeTargets();
            painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
            painter.captureAiHand(a, game, animatingAiDraw, -1, false);
            painter.paintCascadeCapture(g, a, game, cascadeDragIndex, dropIndex, dragX, dragY);
            if (game.getAiHand().isEmpty()) {
               sounds.playSound("lose");
               animatingAiMove = false;
               game.endGame();
               repaint();
            } else {
               playAiHand();
            }
         } else {
            repaint();
         }
      }

      if (animatingCascade) {
         Move aiMove = game.getAi().getMove();
         if (animator.animateCascadeMove(g, a)) {
            sounds.playSound("tick");
            cascadeAnimateIndex = -1;
            animatingCascade = false;
            int source = (Integer)aiMove.getSource();
            int dest = (Integer)aiMove.getDestination();
            game.transferCascades(source, dest);
            setCascadeTargets();
            painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
            animator.setValues(0, 0, 1.0, 0.0, false);
            playAiHand();
         } else {
            repaint();
         }
      }
      a.setToIdentity();
      g.setTransform(a);
      if (instructionsVisible || config.isFirstTimeRun())
         g.drawImage(images.getPNG("instructions"), 27, 135, 800, 450, null);
      if (config.isVisible())
         g.drawImage(images.getPNG("paneltest"), 864, 400, 406, 310, null);

      //   Toolkit.getDefaultToolkit().sync();
   }

   private void setupAiDraw(){
      int handsize = game.getAiHand().size();
      int deckSize = game.getDeck().size();
      double targetAngle = (handsize % 2 == 1) ? -(handAngle / 2 + handAngle * (handsize - 1) / 2) : -handAngle * (handsize / 2);
      Rectangle2D openSpace = openAiHandSpace.getBounds2D();
      int destX = (int)openSpace.getX();
      int destY = (int)openSpace.getY();
      painter.captureDeck(deckSize - 1);
      animator.setAnimateAiDrawFields(destX, destY, getDeckX(), getDeckY(), targetAngle, deckSize == 0);
   }

   private void setupPlayerDraw(){
      int handsize = game.getPlayerHand().size();
      double targetAngle = (handsize % 2 == 1) ? (handAngle / 2 + handAngle * (handsize - 1) / 2) : handAngle * (handsize / 2);
      int destX = (int)openHandSpace.getBounds2D().getX();
      int destY = (int)openHandSpace.getBounds2D().getY();
      Deck deck = game.getDeck();
      game.getPainter().captureDeck(deck.size() - 1);
      boolean deckEmpty = deck.isEmpty();
      if (deckEmpty) return;
      animator.setAnimatePlayerDrawFields(destX, destY, getDeckX(), getDeckY(), targetAngle, deck.get(deck.size() - 1).getCode());
   }

   private void setCascadeTargets(){
      int anchorX = 426;
      int anchorY = 360;
      int i = 0;
      Cascade[] cascades = game.getCascades();
      for (int k = 0; k < 8; k++, i++) {
         if (i == 8) i = 0;
         Cascade current = cascades[i];
         int size = current.size();
         a.setToIdentity();
         int x = 383; // 386
         int y = 423; // 442
         a.rotate(Math.toRadians(i * 45), anchorX, anchorY);
         if (i % 2 == 1) a.translate(0, 45);
         if (size == 13) y += 22;
         for (int j = 0; j < size; j++) {
            if (size == 13) y += 5;
            else y += ((j != size && j < 5) ? 22 : 5);
         }
         cascadeTargets[k] = a.createTransformedShape(new Rectangle2D.Float(x, y + (size >= 5 ? 5 : 22), 0, 0));
      }
   }

   private void setPlayerHandShapes(){
      int handsize = game.getPlayerHand().size();
      int anchorX = 1066;
      int anchorY = 732 - (5 * handsize / 4);
      double initialAngle = Math.toRadians(-handAngle * (handsize + ((animatingPlayerDraw) ? 2 : 1)) / 2);
      a.rotate(initialAngle, anchorX, anchorY);
      playerHandShapes.clear();
      double angle = Math.toRadians(handAngle);
      for (int i = 0; i < handsize; i++) {
         a.rotate(angle, anchorX, anchorY);
         a.translate(0, -(5 * handsize / 4));
         playerHandShapes.add(a.createTransformedShape(emptyPlayerCardShape));
         a.translate(0, +(5 * handsize / 4));
         if (i == handsize - 1) {
            a.rotate(angle, anchorX, anchorY);
            openHandSpace = a.createTransformedShape(new RoundRectangle2D.Float(1023 - 8, 492 - (5 * (handsize - 1) / 4), 0, 0, 7, 7));
            a.rotate(-angle, anchorX, anchorY);
         }
      }
      a.setToIdentity();
   }

   private void setAiHandShapes(){
      int handsize = game.getAiHand().size();
      double initialAngle = Math.toRadians(-handAngle * (handsize + ((animatingAiDraw) ? 0 : 1)) / 2);
      a.rotate(initialAngle, 1066, 342 - (5 * handsize / 4));
      aiHandShapes.clear();
      int x = 1023; // x-location
      int y = 102 - (5 * handsize / 4); // y-location h / 8 - h / 32 + h/48;
      a.rotate(Math.toRadians(-handAngle * ((handsize > 1) ? 1 : 1)), 1066, 342 - (5 * handsize / 4));
      openAiHandSpace = a.createTransformedShape(new RoundRectangle2D.Float(x, y, 0, 0, 7, 7));
      a.rotate(-Math.toRadians(-handAngle * ((handsize > 1) ? 1 : 1)), 1066, 342 - (5 * handsize / 4));    
      for (int i = handsize; i > 0; i--) {
         a.rotate(Math.toRadians(handAngle), 1066, 342 - (5 * handsize / 4));
         a.translate(0, -(5 * handsize / 4));
         aiHandShapes.add(a.createTransformedShape(emptyAiCardShape));
         a.translate(0, +(5 * handsize / 4));
      }
      a.setToIdentity();
   }

   private void newGame(){
      if (config.isVisible()) showSettings(false);
      if (animatingCascadeDeal || animatingAiDraw || animatingPlayerDraw || animatingCascade || animatingAiMove)
         return;

      int confirm = 0;
      if (game.playState)
         confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to start a new game?", "New Game?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (confirm != 0) {
         repaint();
         return;
      }

      game.newGame();
      painter.clearImages();
      painter.captureDeck(game.getDeck().size());
      targetCascade = 0;
      indexOfAiHand = -1;
      animatingCascadeDeal = true;
      animatingPlayerDraw = false;
      animatingCascade = false;
      animatingAiDraw = false;
      animatingAiMove = false;
      animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, false);
      setup();
      repaint();
   }

   private void endTurn(){
      if (config.isVisible()) showSettings(false);
      if (animatingCascadeDeal || animatingAiDraw || animatingPlayerDraw || animatingCascade || animatingAiMove)
         return;
      if (!game.playState || game.gameOver) {
         return;
      }
      animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, false);
      setupAiDraw();
      animatingAiDraw = true;
      animatingAiMove = false;
      animatingCascade = false;
      animatingPlayerDraw = false;
      painter.captureAiHand(a, game, animatingAiDraw, -1, false);
      repaint();
   }

   private void sortHand(){
      game.sortHand((config.isAscendingHand() ? Hand.ASCENDING : Hand.DECENDING));
      painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);
      repaint();
   }

   public int getDeckX(){
      return 383 - (game.getDeck().size()) / 4 + 1;
   }

   public int getDeckY(){
      return 297 - (game.getDeck().size()) / 4 + 1;
   }

   private boolean playAiHand(){
      if (game.getAiHand().isEmpty()) return false;
      if (animatingAiDraw) {
         animator.setValues(getDeckX(), getDeckY(), 1.0, 0.0, false);
         return false;
      }
      game.playAiHand();
      Move aiMove = game.getAi().getMove();
      if (aiMove == null) {
         animatingAiMove = false;
         animatingCascade = false;
         animatingPlayerDraw = game.getDeck().size() > 0;
         indexOfAiHand = -1;
         animator.setFlipped(false);
         animator.setScaleX(1.0);
         setupPlayerDraw();
         painter.captureAiHand(a, game, animatingAiDraw, -1, false);
         painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
         painter.captureDeck(game.getDeck().size());
         repaint();
         return false;
      }
      animator.setValues(0, 0, 1.0, 0.0, false);
      animatingPlayerDraw = false;
      if (aiMove.getType() == Move.FROM_HAND) {
         indexOfAiHand = (Integer)aiMove.getSource();
         targetCascade = (Integer)aiMove.getDestination();

         int aiCode = game.getAiHand().getCard(indexOfAiHand).getCode();
         Rectangle2D handShape = aiHandShapes.get(indexOfAiHand).getBounds2D();
         Rectangle2D targetCascadeBounds = cascadeTargets[targetCascade].getBounds2D();
         int startX = (int)handShape.getX();
         int startY = (int)handShape.getY();
         int destX = (int)targetCascadeBounds.getX();
         int destY = (int)targetCascadeBounds.getY();
         if (targetCascade == 4) {
            destY -= 126;
            destX -= 86;
         }

         int rotatedAngle = targetCascade * 45;
         if (rotatedAngle >= 180) rotatedAngle = (360 - rotatedAngle) * -1;
         if (rotatedAngle == -180) rotatedAngle = 0;
         animator.setAnimateAiMoveFields(indexOfAiHand, aiCode, handShape, targetCascadeBounds, destX, destY, startX, startY, rotatedAngle);

         animatingAiMove = true;
         animatingCascade = false;
         painter.captureAiHand(a, game, animatingAiDraw, indexOfAiHand, false);
         repaint();
         return true;
      } else if (aiMove.getType() == Move.CASCADE) {
         indexOfAiHand = -1;
         cascadeAnimateIndex = aiMove.getSource();
         int destIndex = aiMove.getDestination();
         Cascade sourceCascade = game.getCascades()[cascadeAnimateIndex];
         int sourceCascadeSize = sourceCascade.size();
         Cascade destCascade = game.getCascades()[destIndex];
         int destCascadeSize = destCascade.size();
         int targetAngle = destIndex * 45;
         if (Math.abs(destIndex * 45 - cascadeAnimateIndex * 45) > 180) {
            targetAngle = (destIndex > cascadeAnimateIndex) ? ((360 - destIndex * 45) - cascadeAnimateIndex * 45) * ((cascadeAnimateIndex == 0) ? -1 : 1) : cascadeAnimateIndex * 45 + (360 - cascadeAnimateIndex * 45) + destIndex * 45;
         }
         int startY = (int)cascadeTopLefts.get(cascadeAnimateIndex).getBounds2D().getY();
         int cascadeOffsetY = ((destCascadeSize > 5) ? 110 + (destCascadeSize - 5) * 5 : destCascadeSize * 22);
         if (destCascadeSize >= 5) cascadeOffsetY -= 17;

         animator.setAnimateCascadeMoveFields(sourceCascade, cascadeAnimateIndex, destIndex, sourceCascadeSize, destCascadeSize, cascadeAnimateIndex * 45, targetAngle, startY, cascadeOffsetY);

         animatingAiMove = false;
         animatingCascade = true;
         sounds.playSound("brush");
         painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
         repaint();
         return true;
      }
      return false;
   }

   public static void log(Object s){
      System.out.println(s);
   }

   public void returnCards(){
      if (handDragIndex != -1 || cascadeDragIndex != -1) {
         sounds.playSound("tick");
      }
      handDragIndex = -1;
      cascadeDragIndex = -1;
      painter.setHandHover(-1);
      dropIndex = 0;
      repaint();
   }

   private void showSettings(boolean visible){
      if (visible) config.updateLabels();
      config.setVisible(visible);
      config.repaint();
      buttons[1].setIcon(images.getButtonIcons()[(visible) ? 6 : 1]);
      repaint();
      validate();
   }
   
   
   private void sleep(int milliseconds){
      try {
         Thread.currentThread();
         Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
      }
   }

   private class MouseManager extends MouseAdapter{

      @Override
      public void mouseDragged(MouseEvent e){
         if (!game.playState || game.gameOver) return;
         dragX = e.getX();
         dragY = e.getY();
         dropIndex = -1;
         for (int i = 0; i < 8; i++) {
            if (handDropShapes.get(i).contains(e.getPoint())) {
               dropIndex = i;
               break;
            }
         }
         repaint();
      }

      @Override
      public void mousePressed(MouseEvent e){

         config.cancelFirstTime();

         Point p = e.getPoint();
         if (buttons[0].getBounds().contains(p)) {
            newGame();
            return;
         } else if (buttons[1].getBounds().contains(p)) {
            if (!config.isVisible()) showSettings(true);
            else showSettings(false);
            return;
         } else if (buttons[2].getBounds().contains(p)) {
            System.exit(0);
            return;
         } else if (buttons[3].getBounds().contains(p)) {
            endTurn();
            return;
         } else if (buttons[4].getBounds().contains(p) || buttons[5].getBounds().contains(p)) {
            instructionsVisible = !instructionsVisible;
            repaint();
            return;
         }
         if (instructionsVisible) {
            instructionsVisible = false;
            repaint();
            return;
         }
         if (animatingCascadeDeal || animatingAiDraw || animatingPlayerDraw || animatingCascade || animatingAiMove)
            return;
         if (!game.playState || game.gameOver || config.isVisible()) return;
         cascadeDragIndex = -1;
         handDragIndex = -1;

         // if you're dragging a cascade...
         for (int c = 0; c < 8; c++) {
            if (game.getCascades()[c].isEmpty()) continue;
            if (cascadeDragShapes.get(c).contains(e.getPoint())) {
               sounds.playSound("flick");
               cascadeDragIndex = c;
               dropIndex = c;
               painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
               setCursor(grab);
               dragX = e.getX();
               dragY = e.getY();
               repaint();
               return;
            }
         }

         // if you're selecting a card from your hand...
         for (int s = playerHandShapes.size() - 1; s >= 0; s--) {
            if (playerHandShapes.get(s).contains(e.getPoint())) {
               sounds.playSound("flick");
               handDragIndex = s;
               painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);
               setCursor(grab);
               dragX = e.getX();
               dragY = e.getY();
               repaint();
               return;
            }
         }
      }

      @Override
      public void mouseReleased(MouseEvent arg0){
         if (animatingCascadeDeal || animatingAiDraw || animatingPlayerDraw || animatingCascade || animatingAiMove)
            return;
         if (!game.playState || game.gameOver) return;
         for (JLabel b : buttons)
            if (b.getBounds().contains(arg0.getPoint())) return;
         setCursor(Cursor.getDefaultCursor());
         if (dropIndex >= 0) {
            if (handDragIndex >= 0) {
               if (game.checkMoveFromHand(handDragIndex, dropIndex)) {
                  sounds.playSound("tick");
                  game.makeMoveFromHand(game.getPlayerHand(), handDragIndex, dropIndex);
                  setCascadeTargets();
                  painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
                  setPlayerHandShapes();
               } else returnCards();
            } else {
               if (game.checkCascadeMove(cascadeDragIndex, dropIndex)) {
                  sounds.playSound("tick");
                  game.makeCascadeMove(cascadeDragIndex, dropIndex);
                  setCascadeTargets();
                  painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
                  setPlayerHandShapes();
               } else returnCards();
            }
         } else {
            returnCards();
         }

         dragX = dragY = 0;
         handDragIndex = -1;
         cascadeDragIndex = -1;
         painter.captureCascadeCards(a, game, cascadeDragIndex, cascadeAnimateIndex);
         painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);
         dropIndex = -1;
         painter.setHandHover(-1);
         if (game.getPlayerHand().isEmpty()) {
            sounds.playSound("win");
            game.endGame();
         }
         repaint();

      }

      @Override
      public void mouseMoved(MouseEvent e){
         if (!game.playState || game.gameOver || config.isVisible()) return;
         for (int s = playerHandShapes.size() - 1; s >= 0; s--) {
            if (playerHandShapes.get(s).contains(e.getPoint())) {
               setCursor(hand);
               painter.setHandHover(s);
               painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);
               repaint();
               return;
            }
         }
         boolean check = painter.getHover() != -1;
         painter.setHandHover(-1);
         painter.capturePlayerHand(a, game, animatingPlayerDraw, handDragIndex);

         for (int s = cascadeDragShapes.size() - 1; s >= 0; s--) {
            if (cascadeDragShapes.get(s).contains(e.getPoint())) {
               if (!game.getCascades()[s].isEmpty()) setCursor(hand);
               return;
            }
         }
         if (!check) repaint();
         setCursor(Cursor.getDefaultCursor());
      }
   }

   private class KeyboardManager extends KeyAdapter{

      @Override
      public void keyPressed(KeyEvent e){
         if (instructionsVisible) {
            instructionsVisible = false;
            repaint();
            return;
         }
         if (e.getKeyCode() == config.getNewKey()) newGame();
         else if (e.getKeyCode() == config.getEndKey()) endTurn();
         else if (e.getKeyCode() == config.getSortKey()) sortHand();
      }
   }
}
