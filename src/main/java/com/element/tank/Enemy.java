package com.element.tank;

import com.ai.AStar;
import com.element.BigBomb;
import com.element.Element;
import com.element.Fort;
import com.element.enums.Direct;
import com.game.Game;
import com.taowater.taol.core.util.EmptyUtil;
import com.taowater.ztream.Any;
import com.util.ImageUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.util.List;

/**
 * 敌方坦克
 *
 * @author zhu56
 * @date 2023/09/14 01:21
 */// 敌方坦克的类
@Data
@EqualsAndHashCode(callSuper = true)
public class Enemy extends Tank {

    private int target = 0;
    private int type;
    // 定义是否能行动的布尔变量
    private int mask = 0;
    private int flashTime;
    private Boolean imageFlag = false;
    private Boolean withReward;

    public Enemy(int x, int y, int type, Direct direct) {
        super(x, y, direct);
        this.type = type;
        this.maxbuttle = 1;
        this.withReward = false;
        this.setSpeed(2);
        this.mask = 100;
        this.bitdead = false;
        init();
    }

    private void init() {
        if (type == 1) {
            this.mask = 200;
            setSpeed(4);
            target = Game.rand(25);
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
        imageFlag = !imageFlag;
        int tempX = withReward && flashTime > 1 ? type * 28 * 4 + 2 * 28 : type * 28 * 4 + offset * 28;
        setImage(ImageUtil.getSubImage28("enemy", tempX, getDirect().ordinal() * 28));
        super.draw(g);
        if (!Game.pause && Game.stage.pausetime == 0) {
            if (getX() % 16 == 0 && getY() % 16 == 0) {
                setOldPosition();
                setGoing(false);
            } else {
                setGoing(true);
            }
            if (!star.getIsLive()) {
                switch (type) {
                    case 0 -> {
                        randomGo();
                        if (Game.rand(60) == 1) {
                            shoot();
                        }
                    }
                    case 1 -> {
                        if (getY() / 16 < 24) {
                            goToFort();
                        } else {
                            randomGo();
                        }
                        if (canBit(Game.stage.fort) && Game.rand(30) == 1) {
                            shoot();
                        }
                    }
                    case 2 -> {
                        randomGo();
                        if (canBit(Game.stage.getAnyPlayer()) && Game.rand(50) == 1) {
                            shoot();
                        }
                    }
                    case 3 -> {
                        trackMove();
                        if (canBit(Game.stage.getAnyPlayer()) && Game.rand(10) == 1) {
                            shoot();
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
            }
        }
        flashTime = Game.Reduce(flashTime, 0, 3, 1);
    }

    public void death() {
        setIsLive(false);
        Game.stage.enumber--;
        Game.getStage().addElement(new BigBomb(getX(), getY()));
    }

    /**
     * 判断能否击中一目标-粗略
     *
     * @param element 目标
     * @return boolean
     */
    private boolean canBit(Element element) {
        if (!Any.of(element).get(Element::getIsLive, false)) {
            return false;
        }
        int x = getX();
        int y = getY();
        Direct direct = getDirect();
        if (x == element.getX()) {
            return (y > element.getY() && direct == Direct.UP) || (y < element.getY() && direct == Direct.DOWN);
        }
        if (y == element.getY()) {
            return ((getX() < element.getX() && direct == Direct.RIGHT) || (x > element.getX() && direct == Direct.LEFT));
        }
        return false;
    }

    // 追袭玩家
    private void trackMove() {
        if (EmptyUtil.isEmpty(Game.stage.getPlayers())) {
            return;
        }
        Player player = Game.stage.getPlayers().get(0);
        if (!Any.of(player).get(Player::getIsLive, false)) {
            randomGo();
            return;
        }
        int i = player.getY() / 16;
        int j = player.getX() / 16;
        if (i > 0 && j > 0 && !getGoing()) {
            int[][] currenMap = getCurrentMap(Game.tankcango);
            List<Point> path = new AStar(currenMap, getY() / 16, getX() / 16, i, j).search();
            if (EmptyUtil.isNotEmpty(path)) {
                Point point = path.get(path.size() - 1);
                this.setDirect(getNextStep(point.x, point.y));
            }
        }
        move();
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
        }

    }

    // 偷袭碉堡
    private void goToFort() {
        Fort fort = Game.stage.fort;
        if (!fort.getIsLive()) {
            randomGo();
            return;
        }
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
    }

    // 随机移动
    private void randomGo() {
        move();
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
            Direct newDirect = Game.rand(2) > 0 ? Direct.getR(getDirect()) : Direct.getL(getDirect());
            setDirect(newDirect);
        }
    }
}