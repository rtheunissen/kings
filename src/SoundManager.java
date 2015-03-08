
import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager{

   public synchronized void playSound(final String fileName){
      if (GamePanel.config.getSoundEnabled() == false) return;
      new Thread(new Runnable(){

         @Override
         public void run(){
            try {
               Clip clip = AudioSystem.getClip();
               InputStream is = KingsCorners.class.getResourceAsStream(KingsCorners.AUDIO_DIRECTORY + fileName + ".wav");
               InputStream bufferedIn = new BufferedInputStream(is);
               AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
               clip.open(inputStream);
               clip.start();
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }).start();
   }
}