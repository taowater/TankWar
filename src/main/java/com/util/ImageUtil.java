package com.util;

import com.game.Game;
import com.history.core.util.EmptyUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片工具类
 *
 * @author zhu56
 * @date 2023/09/14 00:22
 */
@UtilityClass
public class ImageUtil {

    private static final Map<String, BufferedImage> IMAGE_CACHE = new ConcurrentHashMap<>(0);


    @SneakyThrows
    private static BufferedImage doGetMaterial(String realPath) {
        return ImageIO.read(new File(Game.getPath(realPath)));
    }

    public static BufferedImage getMaterial(String path) {
        return IMAGE_CACHE.computeIfAbsent("image/" + path + ".png", ImageUtil::doGetMaterial);
    }

    public static BufferedImage getMaterial() {
        return getMaterial("material");
    }

    public static BufferedImage getSubImage(BufferedImage source, int x, int y, int w, int h) {
        if (EmptyUtil.isEmpty(source)) {
            return null;
        }
        return source.getSubimage(x, y, w, h);
    }

    public static BufferedImage getSubImage(String path, int x, int y, int w, int h) {
        return getSubImage(getMaterial(path), x, y, w, h);
    }


    public static BufferedImage getSubImage16(String path, int x, int y) {
        return getSubImage(path, x, y, 16, 16);
    }

    public static BufferedImage getSubImage28(String path, int x, int y) {
        return getSubImage(path, x, y, 28, 28);
    }

    public static BufferedImage getSubImage32(String path, int x, int y) {
        return getSubImage(path, x, y, 32, 32);
    }

    public static BufferedImage getSubImage64(String path, int x, int y) {
        return getSubImage(path, x, y, 64, 64);
    }

    public static BufferedImage getSubImage192(String path, int x, int y) {
        return getSubImage(path, x, y, 192, 192);
    }
}
