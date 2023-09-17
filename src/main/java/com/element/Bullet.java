package com.element;

import com.ai.AStar;
import com.contant.CanGoStrategy;
import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.element.map.Brick;
import com.element.map.Iron;
import com.element.map.MapElement;
import com.element.tank.Enemy;
import com.element.tank.Player;
import com.element.tank.Tank;
import com.game.*;
import com.history.core.util.EmptyUtil;
import com.history.core.util.stream.Ztream;
import com.scene.Stage;
import com.util.ImageUtil;
import com.util.MusicUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//子弹的类
@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends MoveElement {
    private Tank master;
    boolean canTurn;

    /**
     * 射程
     */
    private int reach = 18 * 3;
    static int[] list = null;

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

        if (!isTouch(master)) {
            g.drawImage(getImage(), getX() + 8, getY() + 8, 16, 16, Game.getStage());
        }
        if (Game.pause) {
            return;
        }
        if (!isInStage()) {
            death();
        }
        setOldPosition();
        normalFly();
        if (canTurn) {
            guaiwan();
        }
        if (bitFort(Game.stage.fort) || bitTank() || bitBrick() || bitBullet()) {
            death();
        }
        if (reach > 0) {
            reach--;
        } else {
            death();
        }
    }

    public void death() {
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
        AtomicBoolean flag = new AtomicBoolean(false);
        Ztream.of(Game.getStage().getBullets()).parallel().forEach(b -> {
            if (this != b && this.master != b.master && isTouch(b)) {
                b.death();
                flag.set(true);
            }
        });
        return flag.get();
    }

    protected boolean bitTank() {
        AtomicBoolean flag = new AtomicBoolean(false);
        Ztream.of(Game.getStage().getTanks()).forEach(tank -> {
            if (master == tank || !isTouch(tank)) {
                return;
            }
            if (tank instanceof Player player) {
                player.decrMaxLife();
                player.setIsLive(false);
                flag.set(true);
            } else if (tank instanceof Enemy enemy) {
                if (master instanceof Player player) {
                    if (Game.stage.getPlayers().size() < 2) {
                        Stage.CountData.LEVEL[enemy.getType()]++;
                    } else {
                        if (master == Game.stage.getPlayers().get(0)) {
                            Stage.CountData.LEVEL[enemy.getType()]++;
                        } else {
                            Stage.CountData.LEVEL_2[enemy.getType()]++;
                        }
                    }
                    enemy.death();
                    enemy.setBitdead(true);
                    player.setScore(player.getScore() + enemy.getMask());
                    if (enemy.getWithReward()) {
                        Game.stage.creatReward();
                    }
                    flag.set(true);
                }
            }
        });
        return flag.get();
    }

    public Rectangle getRect() {
        if (getDirect().ordinal() > 3) {
            return new Rectangle(getX() + 8, getY() + 8, 16, 16);
        }
        return super.getRect();
    }

    private MapElement[] getbBitElement() {
        List<MapElement> elements = Game.getStage().getMapElements();
        MapElement[] elements_temp = new MapElement[3];
        int index = 0;

        for (MapElement element : elements) {
            if (isTouch(element) && element.getIsLive() && !CanGoStrategy.BULLET_MAP.getOrDefault(element.getMapType(), true)) {
                elements_temp[index++] = element;
                death();
            }
            if (index > 2) {
                break;
            }
        }
        return elements_temp;
    }

    private void bitSmallBrick(MapElement[] elements, int begin) {
        Ztream.of(elements).forEach(e -> {
            if (e instanceof Brick brick) {
                for (int i = begin; i - begin < 2; i++) {
                    brick.flag[list[i]] = false;
                }
            }
        });
    }

    private boolean bitBrick() {
        MapElement[] elements = getbBitElement();
        boolean flag = false;
        if (getDirect().ordinal() > 3) {
            for (MapElement mapElement : elements) {
                if (mapElement != null && !CanGoStrategy.BULLET_MAP.get(mapElement.getMapType())) {
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
            MapElement[] element_temp = new MapElement[4];
            boolean[] isSmallElementLive = new boolean[element_temp.length];
            for (int key = 0; key < elements.length; key++) {
                MapElement mapElement = elements[key];
                if (mapElement != null) {
                    if (mapElement instanceof Brick brick) {
                        isSmallElementLive[key] = (brick.flag[list[0]] || brick.flag[list[1]]);
                    } else if (mapElement instanceof Iron) {
                        if (master instanceof Player player) {
                            if (player.getLevel() > 2) {
                                mapElement.setIsLive(false);
                                return true;
                            }
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
        return flag;
    }

    private void normalFly() {
        int speed = getSpeed();
        int n = (int) (speed / Math.sqrt(2));
        switch (getDirect()) {
            case UP:// 上
                list = new int[]{2, 3, 0, 1};
                decrY(speed);
                break;
            case RIGHT:// 右
                list = new int[]{0, 2, 1, 3};
                incrX(speed);
                break;
            case DOWN:// 下
                list = new int[]{0, 1, 2, 3};
                incrY(speed);
                break;
            case LEFT:// 左
                list = new int[]{1, 3, 0, 2};
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



