package com.game;

import lombok.SneakyThrows;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;

/**
 * @author Zhu_wuliu
 */
public class Music implements Runnable {
    private final String filename;

    public Music(String filename) {
        this.filename = filename;
    }

    @Override
    @SneakyThrows
    public void run() {
        File soundFile = new File(Game.getPath(filename));
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try (SourceDataLine auLine = (SourceDataLine) AudioSystem.getLine(info)) {
            auLine.open(format);
            auLine.start();
            int nBytesRead = 0;
            byte[] abData = new byte[512];

            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auLine.write(abData, 0, nBytesRead);
                }
            }
            auLine.drain();
        }

    }
}