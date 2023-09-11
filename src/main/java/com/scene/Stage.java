package com.scene;

import com.element.*;
import com.element.inter.Draw;
import com.element.map.Iron;
import com.element.map.MapElement;
import com.game.Game;
import com.history.core.util.stream.Ztream;
import com.util.MusicUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;

import static com.game.Game.edited;
import static com.game.Game.tankWar;

public class Stage extends Scene {

    public static class CountData {
        public static final int[] level = {0, 0, 0, 0};
        public static final int[] level2 = {0, 0, 0, 0};
    }

    private int width, height;
    public int pausetime = 0;
    private int waittime = 64;
    // 玩家
    public ArrayList<Player> players = new ArrayList<Player>();

    // 敌方坦克数量
    public int enumber = 6;
    private long lastCreatEnemyTime = 0;
    public Fort fort;
    // 敌方坦克集合
    private final Vector<Enemy> enemys = new Vector<>();
    // 子弹集合
    public final Vector<Bullet> bullets = new Vector<>();
    // 爆炸效果集合
    public final Vector<Bomb> bombs = new Vector<>();
    // 奖励集合
    private final Vector<Reward> rewards = new Vector<>();

    public final Vector<MapElement> elements = new Vector<>();

    private int overy = 32 * 13;

    public boolean over = false;
    public boolean isLive;
    private int[][] fog;

    public Stage() {
        init();
    }

    private void drawElements(Graphics g) {
        Ztream.of(elements).cast(Draw.class).append(fort).append(players).append(enemys).append(bullets).append(bombs).append(rewards).forEach(e -> e.draw(g));
    }

    private void removeDeath() {
        bombs.removeIf(e -> !e.isLive);
        rewards.removeIf(e -> !e.isLive);
    }

    private void drawGame(Graphics g) {
        if (Game.pause) {
            drawPause(g);
        }
        if (Game.fail) {
            gameOver(g);
        }
        if (starttime > 0) {
            drawStart(g);
        }
    }

    // 界面描绘方法
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawElements(g);
        Ztream.of(players).forEach(e -> {
            if (Game.fogFlag) {
                drawFog(e, g);
            }
        });
        removeDeath();
        drawGame(g);

    }

    private void init() {
        setLayout(null);
        setGameMap();
        setBounds(32, 32, width, height);
        int width_temp = (tankWar.WIDTH > width + 32 * 4 - 16) ? tankWar.WIDTH : width + 32 * 4 - 16;
        tankWar.setSize(width_temp, tankWar.HEIGHT);
        isLive = true;
        setPlayer();
        MusicUtil.start();
    }

    private void setPlayer() {
        for (int i = 0; i < Game.player_number; i++) {
            Player player = new Player(32 * 4 + i * 3 * 32 + 32 * i, 32 * 12, 0);
            player.DIRECTKEY = Game.PlayerDIRECT[i];
            player.setImage(i + 1);
            players.add(player);
        }
    }

    public Vector<Tank> getAllTank() {
        Vector<Tank> tanks = new Vector<>();
        tanks.addAll(players);
        tanks.addAll(enemys);
        return tanks;
    }

    private int[][] getGameMap() {
        return Game.getMap(Game.stagesth);
    }

    public Vector<Enemy> getEnemys() {
        return enemys;
    }

    public Vector<Reward> getRewards() {
        return rewards;
    }

    private void setGameMap() {
        int[][] map;
        if (edited && Game.stagesth == 1) {
            map = Game.editon_map;
            edited = false;
        } else {
            map = getGameMap();
        }
        fort = new Fort(32 * 6, 32 * 12);
        int cow = map.length;
        int col = map[0].length;
        fog = new int[cow][col];
        width = col * 16;
        height = cow * 16;

        for (int i = 0; i < cow; i++) {
            for (int j = 0; j < col; j++) {
                if (map[i][j] > 0) {
                    MapElement element = Game.creatMapElement(map[i][j], j * 16, i * 16);
                    elements.add(element);
                }
            }
        }
    }

    // 描绘迷雾
    private void drawFog(Player player, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AlphaComposite ac;
        g.setColor(Color.GRAY);
        for (int i = 0; i < fog.length; i++) {
            for (int j = 0; j < fog[0].length; j++) {
                int a = Math.abs(player.x + 8 - j * 16);
                int b = Math.abs(player.y + 8 - i * 16);
                if (Math.sqrt(a * a + b * b) >= 32 * 4 && fog[i][j] <= 96) {
                    ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) ((96 - fog[i][j]) / 96.0));
                    g2d.setComposite(ac);
                    g2d.fillRect(j * 16, i * 16, 16, 16);
                } else if (Math.sqrt(a * a + b * b) < 120) {
                    fog[i][j] = 256;
                }
            }
        }
        for (int i = 0; i < fog.length; i++) {
            for (int j = 0; j < fog[0].length; j++) {
                if (fog[i][j] > 0) {
                    fog[i][j]--;
                }
            }
        }
    }

    // 获取当前战场二维地图
    public int[][] getMap() {
        int[][] map = new int[getHeight() / 16][getWidth() / 16];
        for (int i = 0; i < Game.getStage().elements.size(); i++) {
            MapElement element = Game.getStage().elements.get(i);
            int type = 0;
            if (element.isTree) {
                type = 1;
            } else if (element.isSnow) {
                type = 2;
            } else if (element.isBrick) {
                type = 3;
            } else if (element.isWater) {
                type = 4;
            } else if (element.isIron) {
                type = 5;
            }
            map[element.y / 16][element.x / 16] = type;
        }
        return map;
    }

    private void creatEnemy() {
        boolean flag = false;
        long tempTime = System.currentTimeMillis();
        if (tempTime - lastCreatEnemyTime >= 5 * 1000) {
            lastCreatEnemyTime = tempTime;
            int index = Game.Rand(3);
            Vector<Tank> tanks = Game.stage.getAllTank();
            for (Tank tank : tanks) {
                if (new Rectangle(32 * 6 * index, 0, 32, 32).intersects(tank.getRect())) {
                    flag = true;
                    break;
                }
            }
            if (!flag && enumber - enemys.size() > 0) {
                Enemy enemytank = new Enemy(32 * 6 * index, 0, Game.Rand(4), 2);
                if (Game.Rand(5) > 3) {
                    enemytank.withReward = true;
                }
                enemys.add(enemytank);
            }
        }
    }

    public void dispose() {
        for (Tank tank : getAllTank()) {
            tank.isLive = false;
        }
        for (Bullet bullet : bullets) {
            bullet.isLive = false;
        }
    }

    public void creatReward() {
        Reward reward = new Reward(Game.Rand(6), Game.Rand(width - 32), Game.Rand(height - 32));
        rewards.add(reward);
    }

    public void Reward(Player player, Reward reward) {
        switch (reward.id) {
            case 0:
                int m = fort.y / 16;
                int n = fort.x / 16;
                for (int i = m - 1; i < m + 2; i++) {
                    for (int j = n - 1; j < n + 3; j++) {
                        Iron iron = new Iron(j * 16, i * 16);
                        if (!fort.isTouch(iron)) {
                            elements.add(iron);
                        }
                    }
                }
                break;
            case 1:
                if (player.level < 4) {
                    player.level++;
                }
                break;
            case 2:
                this.pausetime = 128;
                break;
            case 3:
                player.flash.life = 64;
                player.flash.isLive = true;
                break;
            case 4:
                for (Enemy enemy : enemys) {
                    enemy.isLive = false;
                }
                break;
            case 5:
                player.maxlife++;
                break;
        }
    }

    private void drawPause(Graphics g) {
        if (flag > 8) {
            Game.drawText("PAUSE", width / 2 - 56, height / 2 - 8, 4, g, this);
        }
    }

    private void gameOver(Graphics g) {
        Game.drawText("GAME", width / 2 - 32 - 16, overy, 4, g, this);
        Game.drawText("OVER", width / 2 - 32 - 16, overy + 16, 4, g, this);
        if (overy > height / 2 - 16) {
            overy -= 8;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!Game.pause && !over) {
                MusicUtil.play("暂停");
            }
            if (isLive) {
                Game.pause = !Game.pause;
            } else {
                tankWar.toCount();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            Game.fogFlag = !Game.fogFlag;
        } else {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                player.KeyPressed(e);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.keyReleased(e);
        }
    }

    public void run() {
        while (true) {
            repaint();
            Game.Sleep(30);
            creatEnemy();
            if (!fort.isLive || players.size() == 0) {
                Game.fail = true;
            }
            if (pausetime > 0) {
                pausetime--;
            }
            if (starttime > 0) {
                starttime--;
            }
            if (enumber == 0 && enemys.size() == 0) {
                over = true;
            }
            if (overy == height / 2 - 16 || over) {
                waittime--;
            }
            if (waittime == 0) {
                isLive = false;
                dispose();
            }
            flag = Game.Reduce(flag, 0, 16, 1);
            if (!isLive) {
                Robot robot;
                try {
                    robot = new Robot();
                    robot.keyPress(KeyEvent.VK_SPACE);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}