import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

public class Configuration extends JPanel implements PropertyChangeListener{

   private Properties properties = new Properties();
   private int dealSpeed, drawSpeed, aiMoveSpeed;
   private int sortHandKey, newGameKey, endTurnKey;
   private boolean soundEnabled, instantDeal, instantDraw, instantMove;
   private final int NEW = 0;
   private final int END = 1;
   private final int SORT = 2;
   private int editingKeyIndex = -1;
   private GamePanel parent;
   private Color transparent = new Color(0, 0, 0, 0);
   private boolean firstTimeRun;
   private boolean ascending = true;
   private JSlider dealSpeedSlider = new JSlider();
   private JSlider aiMoveSpeedSlider = new JSlider();
   private JSlider drawSpeedSlider = new JSlider();
   private JLabel sortHandKeyField = new JLabel();
   private JLabel newGameKeyField = new JLabel();
   private JLabel endTurnKeyField = new JLabel();
   private JCheckBox soundEnabledCheckbox = new JCheckBox();
   private JCheckBox ascendingBox = new JCheckBox();
   private JCheckBox instantDealBox = new JCheckBox();
   private JCheckBox instantDrawBox = new JCheckBox();
   private JCheckBox instantMoveBox = new JCheckBox();

   public Configuration(GamePanel parent) throws Exception{
      this.setLayout(null);
      this.setFocusable(true);
      this.addKeyListener(new KeyboardManager());
      this.addMouseListener(new MouseManager(this));
      this.setFocusTraversalKeysEnabled(false);
      this.setBackground(transparent);
      this.parent = parent;

      Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);

      newGameKeyField.setBounds(164, 200, 80, 25);
      endTurnKeyField.setBounds(164, 230, 80, 25);
      sortHandKeyField.setBounds(164, 260, 80, 25);

      sortHandKeyField.setFont(labelFont);
      newGameKeyField.setFont(labelFont);
      endTurnKeyField.setFont(labelFont);
      dealSpeedSlider.setMaximum(10);
      dealSpeedSlider.setMinimum(0);
      dealSpeedSlider.setSnapToTicks(true);
      dealSpeedSlider.setBackground(transparent);
      dealSpeedSlider.setBounds(160, 24, 170, 40);

      aiMoveSpeedSlider.setMaximum(10);
      aiMoveSpeedSlider.setMinimum(0);
      aiMoveSpeedSlider.setPaintTicks(false);
      aiMoveSpeedSlider.setPaintLabels(false);
      aiMoveSpeedSlider.setBackground(transparent);
      aiMoveSpeedSlider.setBounds(160, 139, 170, 40);

      drawSpeedSlider.setMaximum(10);
      drawSpeedSlider.setMinimum(0);
      drawSpeedSlider.setPaintTicks(false);
      drawSpeedSlider.setPaintLabels(false);
      drawSpeedSlider.setBackground(transparent);
      drawSpeedSlider.setBounds(160, 81, 170, 40);

      soundEnabledCheckbox.setBounds(348, 230, 25, 25);
      soundEnabledCheckbox.setRolloverEnabled(false);
      soundEnabledCheckbox.setBackground(transparent);
      soundEnabledCheckbox.addPropertyChangeListener(this);

      ascendingBox.setBounds(348, 260, 25, 25);
      ascendingBox.setRolloverEnabled(false);
      ascendingBox.setBackground(transparent);
      ascendingBox.addPropertyChangeListener(this);

      instantDrawBox.setBounds(348, 88, 25, 25);
      instantDrawBox.setRolloverEnabled(false);
      instantDrawBox.setBackground(transparent);
      instantDrawBox.addPropertyChangeListener(this);

      instantDealBox.setBounds(348, 31, 25, 25);
      instantDealBox.setRolloverEnabled(false);
      instantDealBox.setBackground(transparent);
      instantDealBox.addPropertyChangeListener(this);

      instantMoveBox.setBounds(348, 146, 25, 25);
      instantMoveBox.setRolloverEnabled(false);
      instantMoveBox.setBackground(transparent);
      instantMoveBox.addPropertyChangeListener(this);

      dealSpeedSlider.addPropertyChangeListener(this);
      drawSpeedSlider.addPropertyChangeListener(this);
      aiMoveSpeedSlider.addPropertyChangeListener(this);

      this.setFocusable(true);
      this.add(dealSpeedSlider);
      this.add(aiMoveSpeedSlider);
      this.add(drawSpeedSlider);
      this.add(soundEnabledCheckbox);
      this.add(newGameKeyField);
      this.add(endTurnKeyField);
      this.add(sortHandKeyField);
      this.add(ascendingBox);
      this.add(instantDealBox);
      this.add(instantDrawBox);
      this.add(instantMoveBox);

      InputStream url = getSettingsDirectory().toURI().toURL().openStream();
      properties.load(url);
      read();
      updateLabels();
   }

   public boolean isFirstTimeRun(){
      return firstTimeRun;
   }

   public void cancelFirstTime(){
      firstTimeRun = false;
      parent.repaint();
   }

   private void read(){
      if (properties.isEmpty()) {
         setDefaults();
         firstTimeRun = true;
         setProperties();
      }
      drawSpeed = Integer.parseInt(properties.getProperty("draw-speed"));
      dealSpeed = Integer.parseInt(properties.getProperty("deal-speed"));
      aiMoveSpeed = Integer.parseInt(properties.getProperty("ai-move-speed"));
      sortHandKey = Integer.parseInt(properties.getProperty("sort-hand-key"));
      newGameKey = Integer.parseInt(properties.getProperty("new-game-key"));
      endTurnKey = Integer.parseInt(properties.getProperty("end-turn-key"));
      soundEnabled = Boolean.parseBoolean(properties.getProperty("sound-enabled"));
      ascending = Boolean.parseBoolean(properties.getProperty("ascending-sort"));
      instantDeal = Boolean.parseBoolean(properties.getProperty("instant-deal"));
      instantDraw = Boolean.parseBoolean(properties.getProperty("instant-draw"));
      instantMove = Boolean.parseBoolean(properties.getProperty("instant-move"));

      drawSpeedSlider.setValue(drawSpeed);
      dealSpeedSlider.setValue(dealSpeed);
      aiMoveSpeedSlider.setValue(aiMoveSpeed);
      soundEnabledCheckbox.setSelected(soundEnabled);
      ascendingBox.setSelected(ascending);

      instantDrawBox.setSelected(instantDraw);
      instantMoveBox.setSelected(instantMove);
      instantDealBox.setSelected(instantDeal);

      newGameKeyField.setText(KeyEvent.getKeyText(newGameKey));
      endTurnKeyField.setText(KeyEvent.getKeyText(endTurnKey));
      sortHandKeyField.setText(KeyEvent.getKeyText(sortHandKey));
   }

   private void setFields(){
      drawSpeed = drawSpeedSlider.getValue();
      dealSpeed = dealSpeedSlider.getValue();
      aiMoveSpeed = aiMoveSpeedSlider.getValue();
      ascending = ascendingBox.isSelected();
      soundEnabled = soundEnabledCheckbox.isSelected();
      instantDraw = instantDrawBox.isSelected();
      instantDeal = instantDealBox.isSelected();
      instantMove = instantMoveBox.isSelected();
   }

   private void setProperties(){
      properties.setProperty("draw-speed", drawSpeed + "");
      properties.setProperty("deal-speed", dealSpeed + "");
      properties.setProperty("ai-move-speed", aiMoveSpeed + "");
      properties.setProperty("sort-hand-key", sortHandKey + "");
      properties.setProperty("new-game-key", newGameKey + "");
      properties.setProperty("end-turn-key", endTurnKey + "");
      properties.setProperty("sound-enabled", soundEnabled + "");
      properties.setProperty("ascending-sort", ascending + "");
      properties.setProperty("instant-deal", instantDeal + "");
      properties.setProperty("instant-draw", instantDraw + "");
      properties.setProperty("instant-move", instantMove + "");
   }

   private void setDefaults(){
      drawSpeed = 8;
      dealSpeed = 9;
      aiMoveSpeed = 7;
      sortHandKey = (int)'A';
      newGameKey = 10;  // Enter
      endTurnKey = 32;  // Space
      soundEnabled = false;
      ascending = true;
      instantDeal = false;
      instantDraw = false;
      instantMove = false;
   }

   public void paintComponent(Graphics g){
      parent.repaint();
      super.paintComponent(g);
   }

   public void store(){
      setFields();
      setProperties();
      OutputStream os = null;
      File fir = getSettingsDirectory();
      try {
         os = new FileOutputStream(fir);
      } catch (FileNotFoundException e) {
         System.out.println(e);
      }
      try {
         properties.store(os, "Configuration for Kings in the Corner");
      } catch (IOException e) {
      }
   }

   public File getSettingsDirectory(){
      String userHome = System.getProperty("user.home");
      if (userHome == null) {
         throw new IllegalStateException("user.home==null");
      }
      File settingsDirectory = new File(userHome + File.separator + "kings.properties");
      if (!settingsDirectory.exists()) {
         try {
            if (!settingsDirectory.createNewFile()) {
               throw new IllegalStateException(settingsDirectory.toString());
            }
         } catch (IOException e) {
         }
      }
      return settingsDirectory;
   }

   public boolean isAscendingHand(){
      return ascending;
   }

   public void setDrawSpeed(int drawSpeed){
      this.drawSpeed = drawSpeed;
   }

   public int getDrawSpeed(){
      return drawSpeed;
   }

   public void setDealSpeed(int dealSpeed){
      this.dealSpeed = dealSpeed;
   }

   public int getDealSpeed(){
      return dealSpeed;
   }

   public void setAiMoveSpeed(int aiSpeed){
      this.aiMoveSpeed = aiSpeed;
   }

   public int getAiMoveSpeed(){
      return aiMoveSpeed;
   }

   public void setEndKey(int endKey){
      this.endTurnKey = endKey;
   }

   public int getEndKey(){
      return endTurnKey;
   }

   public void setSortKey(int sortKey){
      this.sortHandKey = sortKey;
   }

   public int getSortKey(){
      return sortHandKey;
   }

   public void setNewKey(int newKey){
      this.newGameKey = newKey;
   }

   public int getNewKey(){
      return newGameKey;
   }

   public void setSoundEnabled(boolean soundEnabled){
      this.soundEnabled = soundEnabled;
   }

   public boolean getSoundEnabled(){
      return soundEnabled;
   }

   public boolean isSoundEnabled(){
      return soundEnabled;
   }

   public boolean isDrawInstant(){
      return instantDraw;
   }

   public boolean isDealInstant(){
      return instantDeal;
   }

   public boolean isMoveInstant(){
      return instantMove;
   }

   public void updateLabels(){
      editingKeyIndex = -1;
      newGameKeyField.setText(KeyEvent.getKeyText(newGameKey));
      endTurnKeyField.setText(KeyEvent.getKeyText(endTurnKey));
      sortHandKeyField.setText(KeyEvent.getKeyText(sortHandKey));

      sortHandKeyField.setForeground(Color.BLACK);
      endTurnKeyField.setForeground(Color.BLACK);
      newGameKeyField.setForeground(Color.BLACK);
      store();
      repaint();
   }
   private static final long serialVersionUID = 1L;

   @Override
   public void propertyChange(PropertyChangeEvent arg0){
      store();
      repaint();
   }

   private class KeyboardManager extends KeyAdapter{

      @Override
      public void keyPressed(KeyEvent key){
         int c = key.getKeyCode();
         if (editingKeyIndex == -1) return;
         if (editingKeyIndex == NEW && endTurnKey != c && sortHandKey != c)
            newGameKey = c;
         else if (editingKeyIndex == END && newGameKey != c && sortHandKey != c)
            endTurnKey = c;
         else if (editingKeyIndex == SORT && endTurnKey != c && newGameKey != c)
            sortHandKey = c;
         updateLabels();
      }
   }

   private class MouseManager extends MouseAdapter{

      private Configuration parent;

      public MouseManager(Configuration parent){
         super();
         this.parent = parent;
      }

      @Override
      public void mouseClicked(MouseEvent e){
         parent.requestFocus();
         Point p = e.getPoint();
         if (newGameKeyField.getBounds().contains(p)) {
            editingKeyIndex = 0;
         } else if (endTurnKeyField.getBounds().contains(p))
            editingKeyIndex = 1;
         else if (sortHandKeyField.getBounds().contains(p)) editingKeyIndex = 2;
         else editingKeyIndex = -1;
         if (editingKeyIndex == 0) {

            newGameKeyField.setForeground(Color.BLUE);
            endTurnKeyField.setForeground(Color.BLACK);
            sortHandKeyField.setForeground(Color.BLACK);
         } else if (editingKeyIndex == 1) {
            endTurnKeyField.setForeground(Color.BLUE);
            newGameKeyField.setForeground(Color.BLACK);
            sortHandKeyField.setForeground(Color.BLACK);
         } else if (editingKeyIndex == 2) {
            sortHandKeyField.setForeground(Color.BLUE);
            endTurnKeyField.setForeground(Color.BLACK);
            newGameKeyField.setForeground(Color.BLACK);
         }
         repaint();
      }
   }
}
