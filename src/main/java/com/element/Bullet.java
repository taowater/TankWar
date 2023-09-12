package com.element;

import com.ai.AStar;
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
import com.util.MusicUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.*;
import java.util.List;

//子弹的类
@Data
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
        this.setCango(Bullet.GO_MAP);
        init();
        MusicUtil.play("开始攻击");
    }

    public static EnumMap<MapElementType, Boolean> GO_MAP = new EnumMap<>(MapElementType.class);

    static {
        GO_MAP.put(MapElementType.TREE, true);
        GO_MAP.put(MapElementType.SNOW, true);
        GO_MAP.put(MapElementType.BRICK, false);
        GO_MAP.put(MapElementType.WATER, true);
        GO_MAP.put(MapElementType.IRON, false);
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
        setImage(Game.getMaterial("bullet").getSubimage(getDirect().ordinal() * 16, 0, 16, 16));
        if (getIsLive()) {
            if (!isTouch(master))
                g.drawImage(getImage(), getX() + 8, getY() + 8, 16, 16, Game.getStage());
            if (!Game.pause) {
                if (!isInStage()) {
                    setIsLive(false);
                }
                setOldXY();
                normalFly();
                if (canTurn)
                    guaiwan();
                bitFort(Game.stage.fort);
                bitBullet();
                bitTank();
                bitBrick();
                if (reach > 0) {
                    reach--;
                } else {
                    setIsLive(false);
                }
            }
        } else {
            Game.getStage().bullets.remove(this);
            master.decrBulletNum();
            Bomb bomb = new Bomb(getX(), getY());
            Game.getStage().bombs.add(bomb);
        }
    }

    private void bitFort(Fort fort) {
        if (isTouch(fort) && fort.getIsLive()) {
            fort.setIsLive(false);
            setIsLive(false);
        }
    }

    private void bitBullet() {
        for (int i = 0; i < Game.getStage().bullets.size(); i++) {
            Bullet bullet = Game.getStage().bullets.get(i);
            if (this != bullet && this.master != bullet.master && isTouch(bullet)) {
                setIsLive(false);
                bullet.setIsLive(false);
            }
        }
    }

    void bitTank() {
        List<Tank> tanks = Game.getStage().getAllTank();
        for (Tank tank : tanks) {
            if (master != tank && isTouch(tank)) {
                if (tank instanceof Player player) {
                    player.decrMaxLife();
                    player.setIsLive(false);
                    setIsLive(false);
                } else if (tank instanceof Enemy enemy) {
                    if (master instanceof Player player) {
                        if (Game.stage.players.size() < 2) {
                            Stage.CountData.level[enemy.getType()]++;
                        } else {
                            if (master == Game.stage.players.get(0)) {
                                Stage.CountData.level[enemy.getType()]++;
                            } else {
                                Stage.CountData.level2[enemy.getType()]++;
                            }
                        }
                        enemy.setIsLive(false);
                        setIsLive(false);
                        enemy.setBitdead(true);
                        player.setScore(player.getScore() + enemy.getMask());
                        if (enemy.getWithReward()) {
                            Game.stage.creatReward();
                        }
                    }
                }
            }
        }
    }

    public Rectangle getRect() {
        if (getDirect().ordinal() > 3) {
            return new Rectangle(getX() + 8, getY() + 8, 16, 16);
        }
        return super.getRect();
    }

    private MapElement[] getbBitElement() {
        List<MapElement> elements = Game.getStage().elements;
        MapElement[] elements_temp = new MapElement[3];
        int index = 0;
        int length = elements.size();
        for (int i = 0; i < length; i++) {
            MapElement element = elements.get(i);
            if (isTouch(element) && element.getIsLive() && !Bullet.GO_MAP.get(element.getMapType())) {
                setIsLive(false);
                elements_temp[index++] = element;
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

    private void bitBrick() {
        MapElement[] elements = getbBitElement();
        if (getDirect().ordinal() > 3) {
            for (MapElement mapElement : elements) {
                if (mapElement != null && !Bullet.GO_MAP.get(mapElement.getMapType())) {
                    if (mapElement instanceof Iron) {
                        if (master instanceof Player player && player.getLevel() > 2) {
                            mapElement.setIsLive(false);
                        }
                    } else {
                        mapElement.setIsLive(false);
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
                        isSmallElementLive[key] = true;
                        isSmallElementLive[key] = (brick.flag[list[0]] || brick.flag[list[1]]);
                    } else if (mapElement instanceof Iron) {
                        if (master instanceof Player player) {
                            if (player.getLevel() > 2) {
                                mapElement.setIsLive(false);
                            }
                        }
                    }
                }

            }
            boolean flag = false;
            for (int i = 0; i < isSmallElementLive.length; i++) {
                if (isSmallElementLive[i]) {
                    flag = true;
                }
            }
            if (flag) {
                bitSmallBrick(elements, 0);
            } else {
                bitSmallBrick(elements, 2);
            }
        }
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
        List<Enemy> tanks = Game.getStage().getEnemys();
        if (EmptyUtil.isNotEmpty(tanks)) {
            TrackFly(tanks.get(0));
        } else {
            if (isTouchWall()) {
                stay();
                if (Game.Rand(2) == 0) {
                    setDirect(Direct.getR(getDirect()));
                } else {
                    setDirect(Direct.getL(getDirect()));
                }
            }
        }
    }
}



