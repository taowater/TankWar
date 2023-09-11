package com.util;

import com.game.Music;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhu56
 * @date 2023/09/11 23:12
 */
public class MusicUtil {

    public static void play(String string) {
        CompletableFuture.runAsync(new Music("music/" + string + ".wav"));
    }

    public static void start() {
        MusicUtil.play("开局");
    }
}
