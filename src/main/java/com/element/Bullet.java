package com.element;

import com.ai.AStar;
import com.element.enums.Direct;
import com.element.map.Brick;
import com.element.map.Iron;
import com.element.map.MapElement;
import com.element.tank.Enemy;
import com.element.tank.Player;
import com.element.tank.Tank;
import com.game.Game;
import com.scene.Stage;
import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Ztream;
import com.util.ImageUtil;
import com.util.MusicUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//子弹的类
@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends MoveElement {
    private int[] hitParts;
    boolean canTurn;
    private Tank master;
    /**
     * 射程
     */
    private int reach = 18 * 3;

    // 子弹构造函数，要求初始化坐标及方向
    public Bullet(Tank master) {
        super(master.getX(), master.getY(), master.getDirect());
        this.setWidth(32);
        this.setHeight(32);
        this.setSpeed(8);
        this.master = master;
        init();
        MusicUtil.play("开始攻击");
    }

    public void setCanTurn(boolean flag) {
        this.canTurn = flag;
    }

    private void init() {
        if (master instanceof Player player) {
            if (player.getLevel() > 1) {
                this.setSpeed(16);
            }
        } else if (master instanceof Enemy enemy) {
            if (enemy.getType() == 2) {
                this.setSpeed(16);
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage16("bullet", getDirect().ordinal() * 16, 0));
        if (getIsLive() && !isTouch(master)) {
            g.drawImage(getImage(), getX() + 8, getY() + 8, 16, 16, Game.getStage());
        }
    }

    @Override
    public void update() {
        if (!isInStage()) {
            death();
            return;
        }
        setOldPosition();
        normalFly();
        if (canTurn) {
            guaiwan();
        }
        if (bitFort(Game.stage.fort) || bitTank() || bitBrick() || bitBullet()) {
            death();
            return;
        }
        if (reach > 0) {
            reach--;
        } else {
            death();
        }
    }

    public void death() {
        if (!getIsLive()) {
            return;
        }
        this.setIsLive(false);
        master.decrBulletNum();
        Bomb bomb = new Bomb(getX(), getY());
        Game.getStage().addElement(bomb);
    }


    private boolean bitFort(Fort fort) {
        if (isTouch(fort) && fort.getIsLive()) {
            fort.setIsLive(false);
            return true;
        }
        return false;
    }

    private boolean bitBullet() {
        boolean hit = false;
        for (Bullet b : Game.getStage().getBullets()) {
            if (this != b && b.getIsLive() && this.master != b.master && isTouch(b)) {
                b.death();
                hit = true;
            }
        }
        return hit;
    }

    protected boolean bitTank() {
        boolean hit = false;
        for (Tank tank : Game.getStage().getTanks()) {
            if (master == tank || !tank.getIsLive() || !isTouch(tank)) {
                continue;
            }
            if (tank instanceof Player player) {
                if (!player.flash.getIsLive()) {
                    player.decrMaxLife();
                    player.setIsLive(false);
                }
                hit = true;
            } else if (tank instanceof Enemy enemy) {
                hit |= hitEnemy(enemy);
            }
        }
        return hit;
    }

    protected boolean hitEnemy(Enemy enemy) {
        if (!(master instanceof Player player) || !enemy.getIsLive()) {
            return false;
        }
        List<Player> players = Game.stage.getPlayers();
        if (players.size() < 2 || master == players.get(0)) {
            Stage.CountData.LEVEL[enemy.getType()]++;
        } else {
            Stage.CountData.LEVEL_2[enemy.getType()]++;
        }
        enemy.death();
        enemy.setBitdead(true);
        player.setScore(player.getScore() + enemy.getMask());
        if (enemy.getWithReward()) {
            Game.stage.creatReward();
        }
        return true;
    }

    public Rectangle getRect() {
        if (getDirect().ordinal() > 3) {
            return new Rectangle(getX() + 8, getY() + 8, 16, 16);
        }
        return super.getRect();
    }

    private List<MapElement> getHitElements() {
        List<MapElement> elements = Game.getStage().getMapElements();
        List<MapElement> hitElements = new ArrayList<>();
        for (MapElement element : elements) {
            if (isTouch(element) && element.getIsLive() && !element.getMapType().isBulletGo()) {
                hitElements.add(element);
            }
        }
        return hitElements;
    }

    private void bitSmallBrick(List<MapElement> elements, int begin) {
        Ztream.of(elements).forEach(e -> {
            if (e instanceof Brick brick) {
                for (int i = begin; i - begin < 2; i++) {
                    brick.flag[hitParts[i]] = false;
                }
            }
        });
    }

    private boolean bitBrick() {
        List<MapElement> elements = getHitElements();
        if (elements.isEmpty()) {
            return false;
        }
        boolean flag = false;
        if (getDirect().ordinal() > 3) {
            for (MapElement mapElement : elements) {
                if (!mapElement.getMapType().isBulletGo()) {
                    if (mapElement instanceof Iron) {
                        if (master instanceof Player player && player.getLevel() > 2) {
                            mapElement.setIsLive(false);
                            flag = true;
                        }
                    } else {
                        mapElement.setIsLive(false);
                        flag = true;
                    }
                }
            }
        } else {
            boolean[] isSmallElementLive = new boolean[elements.size()];
            for (int key = 0; key < elements.size(); key++) {
                MapElement mapElement = elements.get(key);
                if (mapElement instanceof Brick brick) {
                    isSmallElementLive[key] = (brick.flag[hitParts[0]] || brick.flag[hitParts[1]]);
                } else if (mapElement instanceof Iron) {
                    if (master instanceof Player player) {
                        if (player.getLevel() > 2) {
                            mapElement.setIsLive(false);
                            return true;
                        }
                    }
                }
            }

            for (boolean b : isSmallElementLive) {
                if (b) {
                    flag = true;
                }
            }
            if (flag) {
                bitSmallBrick(elements, 0);
            } else {
                bitSmallBrick(elements, 2);
            }
        }
        return true;
    }

    private void normalFly() {
        int speed = getSpeed();
        int n = (int) (speed / Math.sqrt(2));
        switch (getDirect()) {
            case UP:// 上
                hitParts = new int[]{2, 3, 0, 1};
                decrY(speed);
                break;
            case RIGHT:// 右
                hitParts = new int[]{0, 2, 1, 3};
                incrX(speed);
                break;
            case DOWN:// 下
                hitParts = new int[]{0, 1, 2, 3};
                incrY(speed);
                break;
            case LEFT:// 左
                hitParts = new int[]{1, 3, 0, 2};
                decrX(speed);
                break;
            case LEFT_UP:// 左上
                decrX(n);
                decrY(n);
                break;
            case RIGHT_UP:// 右上
                incrX(n);
                decrY(n);
                break;
            case RIGHT_DOWN:// 右下
                incrX(n);
                incrY(n);
                break;
            case LEFT_DOWN:// 左下
                decrX(n);
                incrY(n);
                break;
        }
    }

    private void TrackFly(Tank tank) {
        int i = tank.getY() / 16;
        int j = tank.getX() / 16;
        int x = getX();
        int y = getY();
        if (i >= 0 && j >= 0 && tank.getIsLive()) {
            if (y % 16 == 0 && x % 16 == 0) {
                int[][] currenMap = getCurrentMap(Game.bulletcango);
                List<Point> path = new AStar(currenMap, y / 16, x / 16, i, j).search();
                if (EmptyUtil.isNotEmpty(path)) {
                    Point point = path.get(path.size() - 1);
                    this.setDirect(getNextStep(point.x, point.y));
                }
            }
        }
    }

    private void guaiwan() {
        List<Enemy> tanks = Game.getStage().getEnemies();
        if (EmptyUtil.isNotEmpty(tanks)) {
            TrackFly(tanks.get(0));
        } else {
            if (isTouchWall()) {
                stay();
                if (Game.rand(2) == 0) {
                    setDirect(Direct.getR(getDirect()));
                } else {
                    setDirect(Direct.getL(getDirect()));
                }
            }
        }
    }
}



