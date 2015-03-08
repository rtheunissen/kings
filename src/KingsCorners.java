
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class KingsCorners extends JFrame{

   private static final long serialVersionUID = 1L;
   public static final String IMAGE_DIRECTORY = "images/";
   public static final String AUDIO_DIRECTORY = "sounds/";
   public static ImageManager imageLoader = new ImageManager();
   private GamePanel gamePanel;
   private static JFrame frame;

   public static void main(String[] args) throws Exception{
      new KingsCorners();
   }

   private KingsCorners() throws Exception{
      System.setProperty("sun.java2d.transaccel", "true");
      System.setProperty("sun.java2d.trace", "timestamp,log,count");
      //System.setProperty("sun.java2d.opengl", "true");
      System.setProperty("sun.java2d.d3d", "true");
      System.setProperty("sun.java2d.ddforcevram", "true");
      System.setProperty("sun.java2d.d3dtexbpp", "16");
      System.setProperty("apple.awt.graphics.UseQuartz", "false");
      //  System.out.println(System.getProperties());
      frame = new JFrame("Kings in the Corner");
      createUI(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
   }

   private void createUI(GraphicsDevice gd) throws Exception{
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.getContentPane().setMaximumSize(new Dimension(1280, 720));
      frame.getContentPane().setMinimumSize(new Dimension(1280, 720));
      frame.getContentPane().setPreferredSize(new Dimension(1280, 720));
      frame.setIconImage(imageLoader.getResourceImage("icon-64-big", ".png"));
      gamePanel = new GamePanel();
      frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }
}
