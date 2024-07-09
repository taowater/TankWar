package com.game;

import com.element.enums.Direct;
import com.element.map.*;
import com.scene.Stage;
import com.taowater.ztream.Any;
import com.util.ImageUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@UtilityClass
public class Game {

    public static final Map<Integer, int[][]> MAP_CACHE = new ConcurrentHashMap<>(0);
    public static final int[][] PlayerDIRECT = {{KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_H},
            {KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_NUMPAD0},};
    public static final boolean[] tankcango = {true, true, true, false, false, false};
    public static final List<BiFunction<Integer, Integer, MapElement>> mapBuilder = List.of(
            Tree::new,
            Snow::new,
            Brick::new,
            Water::new,
            Iron::new
    );
    public static boolean fogFlag = false;
    public static boolean pause = false;
    public static int stagesth = 1;
    public static int player_number = 1;
    public static TankWar tankWar;
    public static Stage stage = null;
    public static boolean edited = false;
    public static int[][] editon_map;
    public static boolean fail = false;
    public static String[] title_menu = {"NEW GAME", "CONSTRUCTION"};
    public static String[] title_menu2 = {"1 PLAYER", "2 PLAYERS"};
    public static boolean[] bulletcango = {true, true, true, false, true, false};

    public static int Reduce(int n, int left, int right, int step) {
        if (n > left) {
            return n - step;
        } else {
            return right;
        }
    }

    public static int getAinBdirection(Point a, Point b) {
        if (a.x < b.x) {
            if (a.y < b.y) {
                return Direct.LEFT_UP.ordinal();
            }
            if (a.y == b.y) {
                return Direct.LEFT.ordinal();
            }
            return Direct.LEFT_DOWN.ordinal();
        }
        if (a.x > b.x) {
            if (a.y < b.y) {
                return Direct.RIGHT_UP.ordinal();
            }
            if (a.y == b.y) {
                return Direct.RIGHT.ordinal();
            }
            return Direct.RIGHT_DOWN.ordinal();
        }
        if (a.y > b.y) {
            return Direct.DOWN.ordinal();
        }
        return Direct.UP.ordinal();
    }

    public static int rand(int n) {
        return (int) (Math.random() * n);
    }

    public static void GameInit() {
        Game.fail = false;
        Game.tankWar.setSize(TankWar.WIDTH, TankWar.HEIGHT);
        Game.stagesth = 1;
    }

    @SneakyThrows
    public static void Sleep(int time) {
        Thread.sleep(time);
    }

    public static Stage getStage() {
        return Game.stage;
    }

    public static int[][] getStageMap() {
        return Game.stage.getMap();
    }

    public static List<Brick> getLogo(int key) {
        int[][] map = Game.getMap(0);
        int length = map[0].length;
        List<Brick> logo = new ArrayList<>();
        for (int i = 0; i + 1 < 12; i += 2) {
            for (int j = 0; j + 1 < length; j += 2) {
                Brick brick = new Brick(24 + j * 8, 64 + 16 + i * 8);
                brick.flag[0] = (map[i][j] == key);
                brick.flag[1] = (map[i][j + 1] == key);
                brick.flag[2] = (map[i + 1][j] == key);
                brick.flag[3] = (map[i + 1][j + 1] == key);
                if (brick.flag[0] || brick.flag[1] || brick.flag[2] || brick.flag[3]) {
                    logo.add(brick);
                }
            }
        }
        return logo;
    }

    public static MapElement creatMapElement(int type, int x, int y) {
        return Any.of(mapBuilder.get(type)).get(f -> f.apply(x, y));
    }

    @SneakyThrows
    public static String getPath(String path) {
        String realPath = Game.class.getClassLoader().getResource(path).getPath();
        return URLDecoder.decode(realPath, StandardCharsets.UTF_8);
    }

    public static int[][] getMap(int stage) {
        return MAP_CACHE.computeIfAbsent(stage, Game::doGetMap);
    }

    @SneakyThrows
    public static int[][] doGetMap(int stage) {
        int[][] map;
        int length = 0;
        int length2 = 0;
        StringBuilder string = new StringBuilder();
        String str;
        String name = STR."map/map\{stage}.txt";

        String path = getPath(name);
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            while (true) {
                if ((str = bf.readLine()) == null) {
                    break;
                }
                length = str.length();
                string.append(str);
                length2++;
            }
        }

        int index = 0;
        char[] mapchar = string.toString().toCharArray();
        map = new int[length2][length];
        int cow = length2;
        int col = length;
        for (int i = 0; i < cow; i++) {
            for (int j = 0; j < col; j++) {
                if (index < mapchar.length) {
                    map[i][j] = mapchar[index++] - 48;
                }
            }
        }
        return map;
    }

    public static void drawText(String string, int x, int y, int color, Graphics g, JPanel panel) {
        int index = 0;
        BufferedImage wordimage = null;
        BufferedImage words = ImageUtil.getMaterial("words");
        for (char word : string.toCharArray()) {
            if (word >= 'A' && word <= 'Z') {
                wordimage = words.getSubimage((word - 65) % 14 * 16, color * 48 + (word - 65) / 14 * 16, 16, 16);
            } else if (word >= '0' && word <= '9') {
                wordimage = words.getSubimage((word - 48) * 16, color * 48 + 32, 16, 16);
            } else if (word == ' ') {
                wordimage = words.getSubimage(12 * 16, color * 48 + 16, 16, 16);
            } else if (word == '*') {
                wordimage = words.getSubimage(13 * 16, color * 48 + 32, 16, 16);
            } else if (word == '<') {
                wordimage = words.getSubimage(10 * 16, color * 48 + 32, 16, 16);
            } else if (word == '>') {
                wordimage = words.getSubimage(12 * 16, color * 48 + 32, 16, 16);
            } else if (word == '_') {
                wordimage = words.getSubimage(11 * 16, color * 48 + 32, 16, 16);
            } else if (word == '.') {
                wordimage = words.getSubimage(13 * 16, color * 48 + 16, 16, 16);
            }
            if (x + (index++) * 16 >= TankWar.WIDTH - 128 && word == ' ' || word == '!') {
                y += 32;
                index = 0;
            }
            if (word != '!') {
                g.drawImage(wordimage, x + (index) * 16, y, 16, 16, panel);
            }
        }
    }
}
