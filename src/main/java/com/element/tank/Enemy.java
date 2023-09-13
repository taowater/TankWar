package com.element.tank;

import com.ai.AStar;
import com.element.BigBomb;
import com.element.ElementOld;
import com.element.Fort;
import com.element.enums.Direct;
import com.game.Game;
import com.history.core.util.EmptyUtil;
import lombok.Data;

import java.awt.*;
import java.util.List;

// 敌方坦克的类
@Data
public class Enemy extends Tank {

    private int target = 0;
    private int type;
    // 定义是否能行动的布尔变量
    private int mask = 0;
    private int flashtime;
    private Boolean imageFlag = false;
    private Boolean withReward;

    public Enemy(int x, int y, int type, Direct direct) {
        super(x, y, direct);
        this.isEnemy = true;
        this.type = type;
        this.maxbuttle = 1;
        this.withReward = false;
        this.setSpeed(2);
        this.mask = 100;
        this.bitdead = false;
        setImage(Game.getMaterial("enemy"));
        init();
    }

    private void init() {
        if (type == 1) {
            this.mask = 200;
            setSpeed(4);
            target = Game.Rand(25);
        } else if (type == 2) {
            this.mask = 300;
        } else if (type == 3) {
            this.mask = 500;
        }
    }

    // 描绘自身
    @Override
    public void draw(Graphics g) {
        int offset = imageFlag ? 1 : 0;
        setImage(Game.getMaterial("enemy").getSubimage(type * 28 * 4 + offset * 28, getDirect().ordinal() * 28, 28, 28));
        imageFlag = !imageFlag;
        if (withReward) {
            if (flashtime > 1) {
                setImage(Game.getMaterial("enemy").getSubimage(type * 28 * 4 + 2 * 28, getDirect().ordinal() * 28, 28, 28));
            }
        }
        super.draw(g);
        if (!Game.pause && Game.stage.pausetime == 0) {
            if (getX() % 16 == 0 && getY() % 16 == 0) {
                setOldXY();
                setGoing(false);
            } else {
                setGoing(true);
            }
            if (!star.getIsLive()) {
                switch (type) {
                    case 0 -> {
                        randomGo();
                        if (Game.Rand(60) == 1) {
                            shoot();
                        }
                    }
                    case 1 -> {
                        if (getY() / 16 < 24) {
                            goToFort();
                        } else {
                            randomGo();
                        }
                        if (canBit(Game.stage.fort) && Game.Rand(30) == 1) {
                            shoot();
                        }
                    }
                    case 2 -> {
                        randomGo();
                        if (canBit(Game.stage.getAnyPlayer()) && Game.Rand(50) == 1) {
                            shoot();
                        }
                    }
                    case 3 -> {
                        trackMove();
                        if (canBit(Game.stage.getAnyPlayer()) && Game.Rand(10) == 1) {
                            shoot();
                        }
                    }
                }
            }
        }
        flashtime = Game.Reduce(flashtime, 0, 3, 1);
    }

    public void death() {
        setIsLive(false);
        Game.stage.enumber--;
        BigBomb bigbomb = new BigBomb(getX(), getY());
        Game.getStage().addElement(bigbomb);
    }

    // 判断能否击中玩家
    private boolean canBit(ElementOld element) {
        if (EmptyUtil.isEmpty(element)){
            return false;
        }
        if (element.getIsLive()) {
            int x = getX();
            int y = getY();
            Direct direct = getDirect();
            if (getX() == element.getX()) {
                if ((y > element.getY() && direct == Direct.UP) || (y < element.getY() && direct == Direct.DOWN)) {
                    return true;
                }
            } else if (y == element.getY()) {
                if ((getX() < element.getX() && direct == Direct.RIGHT) || (x > element.getX() && direct == Direct.LEFT)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 追袭玩家
    private void trackMove() {
        if (EmptyUtil.isNotEmpty(Game.stage.getPlayers())) {
            Player player = Game.stage.getPlayers().get(0);
            if (player != null && player.getIsLive()) {
                int i = player.getY() / 16;
                int j = player.getX() / 16;
                if (i > 0 && j > 0 && player.getIsLive()) {
                    if (!getGoing()) {
                        int[][] currenMap = getCurrentMap(Game.tankcango);
                        List<Point> path = new AStar(currenMap, getY() / 16, getX() / 16, i, j).search();
                        if (EmptyUtil.isNotEmpty(path)) {
                            Point point = path.get(path.size() - 1);
                            this.setDirect(getNextStep(point.x, point.y));
                        }
                    }
                }
                move();
                if (isTouchOtherTanks() || isTouchWall()) {
                    stay();
                }
            } else {
                randomGo();
            }
        }
    }

    // 偷袭碉堡
    private void goToFort() {
        Fort fort = Game.stage.fort;
        if (fort.getIsLive()) {
            if (!getGoing()) {
                int[][] currenMap = getCurrentMap(Game.tankcango);
                List<Point> path = new AStar(currenMap, getY() / 16, getX() / 16, 24, target).search();
                if (EmptyUtil.isNotEmpty(path)) {
                    Point point = path.get(path.size() - 1);
                    this.setDirect(getNextStep(point.x, point.y));
                }
            }
            move();
            if (isTouchOtherTanks() || isTouchWall()) {
                stay();
            }
        } else {
            randomGo();
        }
    }

    // 随机移动
    private void randomGo() {
        move();
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
            Direct newDirect = Game.Rand(2) > 0 ? Direct.getR(getDirect()) : Direct.getL(getDirect());
            setDirect(newDirect);
        }
    }
}