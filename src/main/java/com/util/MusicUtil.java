package com.util;

import com.game.Music;

import java.util.concurrent.CompletableFuture;

/**
 * 音乐播放工具
 * @author zhu56
 * @date 2023/09/11 23:12
 */
public class MusicUtil {

    public static void play(String string) {
        Thread.startVirtualThread(new Music("music/" + string + ".wav"));
    }

    public static void start() {
        MusicUtil.play("开局");
    }
}
