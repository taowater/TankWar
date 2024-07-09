package com.util;

import com.game.Game;
import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音乐播放工具
 *
 * @author zhu56
 * @date 2023/09/11 23:12
 */
@UtilityClass
public class MusicUtil {

    private static final Map<String, File> files = new ConcurrentHashMap<>();

    public static void play(String string) {
        var file = files.computeIfAbsent(string, s -> new File(Game.getPath(STR."music/\{string}.wav")));
        CompletableFuture.runAsync(() -> {
            try {
                var audioInputStream = AudioSystem.getAudioInputStream(file);
                var format = audioInputStream.getFormat();
                var info = new DataLine.Info(SourceDataLine.class, format);
                try (
                        var auLine = (SourceDataLine) AudioSystem.getLine(info)) {
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
            } catch (Exception _) {
            }
        });
    }

    public static void start() {
        MusicUtil.play("开局");
    }
}
