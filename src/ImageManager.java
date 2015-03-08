import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageManager{
  private BufferedImage[] cardImages = new BufferedImage[53];
  public  ImageIcon [] buttonImages = new ImageIcon[7];
  private HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();


  public ImageManager() {
    loadComponentImages();
    loadCardImages();
    loadButtonImages();
  }
  

  
 public void clearImage(BufferedImage img){  
    Graphics2D g = img.createGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
      Rectangle2D.Double rect = 
        new Rectangle2D.Double(0,0,img.getWidth(),img.getHeight()); 
      g.fill(rect);
      g.dispose();
  }
  
  
  private void loadButtonImages(){
    buttonImages[0] = getImageIcon("new-game", ".png");
    buttonImages[1] = getImageIcon("settings", ".png");
    buttonImages[2] = getImageIcon("exit", ".png");
    buttonImages[3] = getImageIcon("end-turn", ".png");
    buttonImages[6] = getImageIcon("accept" ,".png");
    buttonImages[5] = getImageIcon("title", ".png");
    buttonImages[4] = getImageIcon("question" ,".png");
  }
  
  public ImageIcon [] getButtonIcons(){
    return buttonImages;
  }

  public void loadCardImages()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gs = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gs.getDefaultConfiguration();

    BufferedImage card = null;
    BufferedImage white = getPNG("white");
    BufferedImage shadow = getPNG("shadow");

    for (int i = 0; i < 53; i++) {
      BufferedImage img = gc.createCompatibleImage(126, 186,BufferedImage.TRANSLUCENT);
      RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
      hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

      if (i < 40) {
        String colour = null;
        BufferedImage suit = null;

        int s = (i + 4) % 4;
        int number = i / 4 + 1;
        if (s == 0) suit = getResourceImage("clubs", ".gif");
        else if (s == 1) suit = getResourceImage("diamonds", ".gif");
        else if (s == 2) suit = getResourceImage("hearts", ".gif");
        else if (s == 3) suit = getResourceImage("spades", ".gif");
        if ((s == 0) || (s == 3)) colour = "black_"; else
        colour = "red_";
        card = getResourceImage(colour + number, ".gif");

        Graphics2D g = img.createGraphics();
        g.setRenderingHints(hints);
        g.drawImage(shadow, 0, 0, 126, 186, null);
        g.drawImage(white, 3,3, 120, 180, null);
        g.drawImage(suit, 3, 3, 120, 180,  null);
        g.drawImage(card, 3,3, 120, 180,  null);

        g.dispose();
        cardImages[i] = img;
      } else if (i == 52) {
        card = getPNG("back2");
        Graphics2D g = img.createGraphics();
        g.setRenderingHints(hints);
        g.drawImage(shadow, 0, 0, 126, 186, null);
        g.drawImage(white, 3,3, 120, 180, null);
        g.drawImage(card, 3, 3,120, 180,  null);
        g.dispose();
        cardImages[i] = img;
        g.dispose();
      } else {
        card = getResourceImage("card-" + i, ".png");
        Graphics2D g = img.createGraphics();
        g.setRenderingHints(hints);
        g.drawImage(shadow, 0, 0, 126, 186, null);
        g.drawImage(card, 3, 3, 120, 180, null);
        g.dispose();
        cardImages[i] = img;
      }
    }
    

  }

  private void loadComponentImages() {
    String[] names = { 
      "background3.gif", 
      "shadow.png", 
      "back2.png", 
      "white.png", 
      "right-panel.png", 
      "new-game.png", 
      "title.png", 
      "paneltest.png", 
      "win.png", 
      "lose.png", 
      "instructions.png",
      "king-place.png"
    };

    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>(names.length);
    for (int i = 0; i < names.length; i++) {
      BufferedImage current = getResourceImage(names[i], "");
      images.add(current);
      imageMap.put(names[i], (BufferedImage)images.get(i));
    }
  }

  public ImageIcon getImageIcon(String fileName, String ext){
    return new ImageIcon(getResourceImage(fileName,ext));
  }

  public BufferedImage getResourceImage(String fileName, String ext)  {
    String imageDirectory = "images/";
    URL imgURL = getClass().getResource(imageDirectory + fileName + ext);
    BufferedImage image = null;
    try {
      image = ImageIO.read(imgURL);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }

  public BufferedImage getCard(int code) {
    return cardImages[code];
  }

  public BufferedImage get(String type, String extension) {
    return imageMap.get(type + extension);
  }

  public BufferedImage getPNG(String type) {
    return imageMap.get(type + ".png");
  }
}