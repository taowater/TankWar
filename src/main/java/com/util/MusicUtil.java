package com.util;

import com.game.Game;
import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 音乐播放工具
 *
 * @author zhu56
 * @date 2023/09/11 23:12
 */
@UtilityClass
public class MusicUtil {

    private static final Map<String, byte[]> AUDIO_CACHE = new ConcurrentHashMap<>();
    private static final ExecutorService AUDIO_EXECUTOR = new ThreadPoolExecutor(
            2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32), task -> {
        Thread thread = new Thread(task, "tank-war-audio");
        thread.setDaemon(true);
        return thread;
    }, new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void play(String string) {
        byte[] audio = AUDIO_CACHE.computeIfAbsent(string, MusicUtil::loadAudio);
        AUDIO_EXECUTOR.execute(() -> {
            try (var input = new BufferedInputStream(new ByteArrayInputStream(audio));
                 var audioInputStream = AudioSystem.getAudioInputStream(input)) {
                var format = audioInputStream.getFormat();
                var info = new DataLine.Info(SourceDataLine.class, format);
                try (var auLine = (SourceDataLine) AudioSystem.getLine(info)) {
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
            } catch (Exception e) {
                System.err.println("Cannot play audio '" + string + "': " + e.getMessage());
            }
        });
    }

    private static byte[] loadAudio(String name) {
        try (var stream = Game.getResource("music/" + name + ".wav")) {
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load audio resource: " + name, e);
        }
    }

    public static void start() {
        MusicUtil.play("开局");
    }
}
