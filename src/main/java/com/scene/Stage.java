package com.scene;

import com.element.Bullet;
import com.element.Element;
import com.element.Fort;
import com.element.Reward;
import com.element.enums.Direct;
import com.element.inter.Draw;
import com.element.map.Iron;
import com.element.map.MapElement;
import com.element.map.Tree;
import com.element.tank.Enemy;
import com.element.tank.Player;
import com.element.tank.Tank;
import com.game.Game;
import com.game.TankWar;
import com.history.core.util.EmptyUtil;
import com.history.core.util.stream.Ztream;
import com.util.MusicUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.game.Game.*;

public class Stage extends Scene {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CountData {
        public static final int[] LEVEL = {0, 0, 0, 0};
        public static final int[] LEVEL_2 = {0, 0, 0, 0};
    }

    private int width;
    private int height;
    public int pausetime = 0;
    private int waittime = 64;

    // 敌方坦克数量
    public int enumber = 6;
    private long lastCreatEnemyTime = 0;
    public transient Fort fort;

    private final List<Element> elements = new ArrayList<>();

    private int overY = 32 * 13;

    public boolean over = false;
    public boolean isLive;
    private int[][] fog;

    public Stage() {
        super();
        init();
    }

    public void addElement(Element e) {
        if (EmptyUtil.isEmpty(e)) {
            return;
        }
        this.elements.add(e);
    }

    public void removeElement(Element e) {
        this.elements.remove(e);
    }

    private void drawElements(Graphics g) {
        Ztream.of(elements).asc(e -> e.getClass().isAssignableFrom(Tree.class) ? 1 : 0).cast(Draw.class).append(fort).forEach(e -> e.draw(g));
    }

    private void removeDeath() {
        elements.removeIf(e -> !e.getIsLive());
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
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        removeDeath();
        drawElements(g);
        if (fogFlag) {
            Ztream.of(getPlayers()).forEach(e -> drawFog(e, g));
        }

        drawGame(g);

    }

    private void init() {
        setLayout(null);
        setGameMap();
        setBounds(32, 32, width, height);
        int widthTemp = Math.max(TankWar.WIDTH, width + 32 * 4 - 16);
        tankWar.setSize(widthTemp, TankWar.HEIGHT);
        isLive = true;
        setPlayer();
        MusicUtil.start();
    }

    private void setPlayer() {
        for (int i = 0; i < Game.player_number; i++) {
            Player player = new Player(32 * 4 + i * 3 * 32 + 32 * i, 32 * 12, Direct.UP);
            player.setDIRECTKEY(Game.PlayerDIRECT[i]);
            player.setImage(i + 1);
            addElement(player);
        }
    }

    public List<Tank> getTanks() {
        return getElements(Tank.class);
    }

    private int[][] getGameMap() {
        return Game.getMap(Game.stagesth);
    }

    public List<Enemy> getEnemies() {
        return getElements(Enemy.class);
    }

    public List<Bullet> getBullets() {
        return getElements(Bullet.class);
    }

    public List<Reward> getRewards() {
        return getElements(Reward.class);
    }

    public List<Player> getPlayers() {
        return getElements(Player.class);
    }

    public Player getAnyPlayer() {
        return Ztream.of(getPlayers()).any().orElse(null);
    }

    private <E extends Element> List<E> getElements(Class<E> clazz) {
        return Ztream.of(elements).filter(e -> clazz.isAssignableFrom(e.getClass())).cast(clazz).toList();
    }

    public List<MapElement> getMapElements() {
        return getElements(MapElement.class);
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
                    addElement(Game.creatMapElement(map[i][j] - 1, j * 16, i * 16));
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
                int a = Math.abs(player.getX() + 8 - j * 16);
                int b = Math.abs(player.getY() + 8 - i * 16);
                var sqrt = Math.sqrt((double) (a * a) + (b * b));
                if (sqrt >= 32 * 4 && fog[i][j] <= 96) {
                    ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) ((96 - fog[i][j]) / 96.0));
                    g2d.setComposite(ac);
                    g2d.fillRect(j * 16, i * 16, 16, 16);
                } else if (sqrt < 120) {
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
        Ztream.of(Game.getStage().getMapElements()).forEach(e -> map[e.getY() / 16][e.getX() / 16] = e.getMapType().ordinal() + 1);
        return map;
    }

    private void creatEnemy() {
        boolean flag = false;
        long tempTime = System.currentTimeMillis();
        if (tempTime - lastCreatEnemyTime >= 5 * 1000) {
            lastCreatEnemyTime = tempTime;
            int index = Game.rand(3);
            List<Tank> tanks = Game.stage.getTanks();
            for (Tank tank : tanks) {
                if (new Rectangle(32 * 6 * index, 0, 32, 32).intersects(tank.getRect())) {
                    flag = true;
                    break;
                }
            }
            if (!flag && enumber - getEnemies().size() > 0) {
                Enemy enemy = new Enemy(32 * 6 * index, 0, Game.rand(4), Direct.DOWN);
                if (Game.rand(5) > 3) {
                    enemy.setWithReward(true);
                }
                addElement(enemy);
            }
        }
    }

    public void dispose() {
        Ztream.of(getTanks()).cast(Element.class).append(getBullets()).forEach(e -> e.setIsLive(false));
    }

    public void creatReward() {
        Reward reward = new Reward(Game.rand(6), Game.rand(width - 32), Game.rand(height - 32));
        elements.add(reward);
    }

    public void reward(Player player, Reward reward) {
        switch (reward.id) {
            case 0 -> {
                int m = fort.getY() / 16;
                int n = fort.getX() / 16;
                for (int i = m - 1; i < m + 2; i++) {
                    for (int j = n - 1; j < n + 3; j++) {
                        Iron iron = new Iron(j * 16, i * 16);
                        if (!fort.isTouch(iron)) {
                            elements.add(iron);
                        }
                    }
                }
            }
            case 1 -> {
                if (player.getLevel() < 4) {
                    player.setLevel(player.getLevel() + 1);
                }
            }
            case 2 -> this.pausetime = 128;
            case 3 -> {
                player.flash.life = 64;
                player.flash.setIsLive(false);
            }
            case 4 -> Ztream.of(getEnemies()).forEach(Enemy::death);
            case 5 -> player.setMaxlife(player.getMaxlife() + 1);
            default -> throw new IllegalStateException("Unexpected value: " + reward.id);
        }
    }

    private void drawPause(Graphics g) {
        if (flag > 8) {
            Game.drawText("PAUSE", width / 2 - 56, height / 2 - 8, 4, g, this);
        }
    }

    private void gameOver(Graphics g) {
        Game.drawText("GAME", width / 2 - 32 - 16, overY, 4, g, this);
        Game.drawText("OVER", width / 2 - 32 - 16, overY + 16, 4, g, this);
        if (overY > height / 2 - 16) {
            overY -= 8;
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
            Ztream.of(getPlayers()).forEach(p -> p.keyPressed(e));
        }
    }

    public void keyReleased(KeyEvent e) {
        Ztream.of(getPlayers()).forEach(p -> p.keyReleased(e));
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            repaint();
            Game.Sleep(30);
            creatEnemy();
            if (!fort.getIsLive() || EmptyUtil.isEmpty(getPlayers())) {
                Game.fail = true;
            }
            if (pausetime > 0) {
                pausetime--;
            }
            if (starttime > 0) {
                starttime--;
            }
            if (enumber == 0 && EmptyUtil.isEmpty(getEnemies())) {
                over = true;
            }
            if (overY == height / 2 - 16 || over) {
                waittime--;
            }
            if (waittime == 0) {
                isLive = false;
                dispose();
            }
            flag = Game.Reduce(flag, 0, 16, 1);
            if (!isLive) {
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_SPACE);
                break;
            }
        }
    }
}