package com.element;

import com.ai.AStar;
import com.element.map.Brick;
import com.element.map.MapElement;
import com.game.*;
import com.scene.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

//子弹的类
public class Bullet extends MoveElement {
    final Tank master;
    boolean canGuaiWan;
    int reach = 18 * 3;
    static int[] list = null;

    // 子弹构造函数，要求初始化坐标及方向
    public Bullet(Tank master) {
        super(master.x, master.y, master.direct);
        this.width = 32;
        this.height = 32;
        this.speed = 8;
        this.master = master;
        this.direct = master.direct;
        this.cango = Game.bulletcango;
        init();
        Game.PlaySound("开始攻击");
    }

    public void setCanGuaiWan(boolean flag) {
        this.canGuaiWan = flag;
    }

    private void init() {
        if (master.isPlayer) {
            Player player = (Player) master;
            if (player.level > 1) {
                this.speed = 16;
            }
        } else {
            Enemy enemy = (Enemy) master;
            if (enemy.type == 2) {
                this.speed = 16;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        image = Game.getMaterial("bullet").getSubimage(direct * 16, 0, 16, 16);
        if (isLive) {
            if (!isTouch(master))
                g.drawImage(image, x + 8, y + 8, 16, 16, Game.getStage());
            if (!Game.pause) {
                if (!isInStage()) {
                    isLive = false;
                }
                setOldXY();
                normalFly();
                if (canGuaiWan)
                    guaiwan();
                bitFort(Game.stage.fort);
                bitBullet();
                bitTank();
                bitBrick();
                if (reach > 0)
                    reach--;
                else
                    isLive = false;
            }
        } else {
            Game.getStage().bullets.remove(this);
            master.buttlenumber--;
            Bomb bomb = new Bomb(x, y);
            Game.getStage().bombs.add(bomb);
        }
    }

    private void bitFort(Fort fort) {
        if (isTouch(fort) && fort.isLive) {
            fort.isLive = false;
            isLive = false;
        }
    }

    private void bitBullet() {
        for (int i = 0; i < Game.getStage().bullets.size(); i++) {
            Bullet bullet = Game.getStage().bullets.get(i);
            if (this != bullet && this.master != bullet.master && isTouch(bullet)) {
                isLive = false;
                bullet.isLive = false;
            }
        }
    }

    void bitTank() {
        Vector<Tank> tanks = Game.getStage().getAllTank();
        for (Tank tank : tanks) {
            if (master != tank && isTouch(tank)) {
                if (tank.isPlayer) {
                    Player player = (Player) tank;
                    player.maxlife--;
                    player.isLive = false;
                    isLive = false;
                } else {
                    Enemy enemy = (Enemy) tank;
                    if (master.isPlayer) {
                        if (Game.stage.players.size() < 2) {
                            Stage.CountData.level[enemy.type]++;
                        } else {
                            if (master == Game.stage.players.get(0)) {
                                Stage.CountData.level[enemy.type]++;
                            } else {
                                Stage.CountData.level2[enemy.type]++;
                            }
                        }
                        enemy.isLive = false;
                        isLive = false;
                        enemy.bitdead = true;
                        Player player = (Player) master;
                        player.score += enemy.mask;
                        if (enemy.withReward) {
                            Game.stage.creatReward();
                        }
                    }
                }
            }
        }
    }

    public Rectangle getRect() {
        if (direct > 3) {
            return new Rectangle(x + 8, y + 8, 16, 16);
        } else
            return (super.getRect());
    }

    private MapElement[] getbBitElement() {
        Vector<MapElement> elements = Game.getStage().elements;
        MapElement[] elements_temp = new MapElement[3];
        int index = 0;
        int length = elements.size();
        for (int i = 0; i < length; i++) {
            MapElement element = elements.get(i);
            if (isTouch(element) && element.isLive && !cango[element.type]) {
                isLive = false;
                elements_temp[index++] = element;
            }
            if (index > 2) {
                break;
            }
        }
        return elements_temp;
    }

    private void bitSmallBrick(MapElement[] elements, int begin) {
        for (int key = 0; key < elements.length; key++) {
            MapElement mapElement = elements[key];
            if (mapElement != null && mapElement.isBrick) {
                Brick brick = (Brick) mapElement;
                for (int i = begin; i - begin < 2; i++) {
                    brick.flag[list[i]] = false;
                }
            }
        }
    }

    private void bitBrick() {
        MapElement[] elements = getbBitElement();
        if (elements != null) {
            if (direct > 3) {
                for (MapElement mapElement : elements) {
                    if (mapElement != null && !cango[mapElement.type]) {
                        if(mapElement.type == 5){
                            if(master.isPlayer){
                                Player player = (Player) master;
                                if (player.level > 2) {
                                    mapElement.isLive = false;
                                }
                            }
                        }else{
                            mapElement.isLive = false;
                        }
                    }
                }
            } else {
                MapElement[] element_temp = new MapElement[4];
                boolean[] isSmallElementLive = new boolean[element_temp.length];
                for (int key = 0; key < elements.length; key++) {
                    MapElement mapElement = elements[key];
                    if (mapElement != null) {
                        if (mapElement.isBrick) {
                            isSmallElementLive[key] = true;
                            Brick brick = (Brick) mapElement;
                            isSmallElementLive[key] = (brick.flag[list[0]] || brick.flag[list[1]]);
                        } else if (mapElement.isIron) {
                            if (master.isPlayer) {
                                Player player = (Player) master;
                                if (player.level > 2) {
                                    mapElement.isLive = false;
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
    }

    private void normalFly() {
        switch (direct) {
            case UP:// 上
                list = new int[]{2, 3, 0, 1};
                y -= speed;
                break;
            case RIGHT:// 右
                list = new int[]{0, 2, 1, 3};
                x += speed;
                break;
            case DOWN:// 下
                list = new int[]{0, 1, 2, 3};
                y += speed;
                break;
            case LEFT:// 左
                list = new int[]{1, 3, 0, 2};
                x -= speed;
                break;
            case LEFT_UP:// 左上
                y -= speed / Math.sqrt(2);
                x -= speed / Math.sqrt(2);
                break;
            case RIGHT_UP:// 右上
                x += speed / Math.sqrt(2);
                y -= speed / Math.sqrt(2);
                break;
            case RIGHT_DOWN:// 右下
                x += speed / Math.sqrt(2);
                y += speed / Math.sqrt(2);
                break;
            case LEFT_DOWN:// 左下
                x -= speed / Math.sqrt(2);
                y += speed / Math.sqrt(2);
                break;
        }
    }

    private void TrackFly(Tank tank) {
        int i = tank.y / 16;
        int j = tank.x / 16;
        if (i >= 0 && j >= 0 && tank.isLive) {
            if (y % 16 == 0 && x % 16 == 0) {
                int[][] currenMap = getCurrentMap(Game.bulletcango);
                ArrayList<Point> path = new AStar(currenMap, y / 16, x / 16, i, j).search();
                if (path.size() > 0) {
                    Point point = path.get(path.size() - 1);
                    this.direct = getNextStep(point.x, point.y);
                }
            }
        }
    }

    private void guaiwan() {
        Vector<Enemy> tanks = Game.getStage().getEnemys();
        if (tanks.size() > 0) {
            TrackFly(tanks.get(0));
        } else {
            if (isTouchWall()) {
                stay();
                if (Game.Rand(2) == 0) {
                    setDirect(getR(direct));
                } else {
                    setDirect(getL(direct));
                }
            }
        }
    }
}

class Laser extends Bullet {

    public Laser(Tank master, int direct) {
        super(master);
        this.reach = 24;
        this.life = 8;
        switch (direct) {
            case 0:
                height = 16 * reach;
                this.y = master.y - height;
                break;
            case 2:
                height = 16 * reach;
                this.y = master.y + 32;
                break;
            case 1:
                width = 16 * reach;
                this.x = master.x + 32;
                break;
            case 3:
                width = 16 * reach;
                this.x = master.x - width;
                break;
        }
        Length();
    }

    @Override
    public void draw(Graphics g) {
        image = Game.getMaterial("bullet_2").getSubimage(direct * 16, 0, 16, 16);
        g.drawImage(image, x, y, width, height, Game.getStage());
        if (life > 0)
            life--;
        else
            isLive = false;
        if (isLive) {
            if (!Game.pause) {
                bitTank();
            }
            if (!isInStage())
                isLive = false;
        } else {
            master.buttlenumber--;
            Game.getStage().bullets.remove(this);
        }
    }

    private void Length() {
        for (int i = 0; i < Game.getStage().elements.size(); i++) {
            MapElement element = Game.getStage().elements.get(i);
            if (isTouch(element) && !cango[element.type]) {
                switch (direct) {
                    case 0:
                        height = master.y - (element.y + element.height);
                        this.y = master.y - height;
                        break;
                    case 2:
                        height = element.y - (master.y + master.height);
                        this.y = master.y + 32;
                        break;
                    case 1:
                        width = element.x - (master.x + master.width);
                        this.x = master.x + 32;
                        break;
                    case 3:
                        width = master.x - (element.x + element.width);
                        this.x = master.x - width;
                        break;
                }
            }
        }
    }
}

