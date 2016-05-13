package server;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author dallachiaram@gmail.com
 * @version 1.8
 * Classe che permette di registrare e creare un file audio
 */
public class AudioRecorder {
    private AudioFormat formato;
    private File tracciaAudio;
    private DataLine.Info info;
    private TargetDataLine line;
    private AudioInputStream audiostream;
    
    public AudioRecorder(String filePath){
        try {
            formato = new AudioFormat(11025, 8, 2, true, true);
            tracciaAudio = new File(filePath);
            info = new DataLine.Info(TargetDataLine.class, formato);
            line = (TargetDataLine) AudioSystem.getLine(info);
            
        }catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void inizioRecord() {
        try{
            line.open(formato, line.getBufferSize());
            line.start();
            audiostream = new AudioInputStream(line);
            System.out.println(audiostream.getFormat().toString());
            AudioSystem.write(audiostream, AudioFileFormat.Type.AU, tracciaAudio);
        } catch (LineUnavailableException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void fineRecord(){
        line.stop();
        line.close();
        System.out.println("Fine registrazione");
    }
    
    public static void main(String[] args){
        AudioRecorder audioRecorder = new AudioRecorder(
                System.getProperty("user.dir") + "\\src\\client\\file\\"
                + "inviati\\tracciaAudio\\" + "registra.au");
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(5000);
                audioRecorder.fineRecord();
                System.out.println(System.getProperty("user.dir") + "\\src\\client\\file\\"
                        + "inviati\\tracciaAudio\\" + "registra.au");
            } catch (InterruptedException ex) {
                
            }
        });
        stopper.start();
        audioRecorder.inizioRecord();
    }
}