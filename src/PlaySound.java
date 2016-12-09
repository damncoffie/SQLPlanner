import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Включает в себя методы для осуществления доступа к аудио-файлу и работы с ним.
 * @author Created by Cooper on 30.11.2016.
 * @version 1.0
 * */
class PlaySound {

    /**
     * Входящий аудиопоток
     * */
    static AudioInputStream sound;

    /**
     * Аудио-файл, хранящийся на диске
     * */
    private Clip clip;

    /**
     * @param fileName путь к файлу на диске в строковом виде
     * */
    PlaySound(String fileName) {

        try {
            File file = new File(fileName);
            if (file.exists()) {
                sound = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(sound);
            }
            else {
                throw new RuntimeException("Sound: file not found: " + fileName);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Воспроизводит мелодию
     * */
    public void play(){
        try {
            clip.setFramePosition(0);
            clip.start();
            while (!clip.isRunning())
                Thread.sleep(10);
            while (clip.isRunning())
                Thread.sleep(10);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Зацикливает мелодию
     * */
    void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Останавливает мелодию
     * */
    void stop(){
        clip.stop();
    }
}
