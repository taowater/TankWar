package com.element.tank;

import com.element.*;
import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.game.Game;
import com.history.core.util.stream.Ztream;
import lombok.Data;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

// 坦克的类，所有坦克的父类
@Data
public class Tank extends MoveElement {

    public boolean isPlayer;
    public boolean isEnemy;
    public int bulletType = 0;
    int bulletNum;
    // 最大子弹数
    int maxbuttle;
    // 定义新生成时闪光
    Star star;
    // 定义新生成时无敌闪烁
    public Flash flash = null;
    boolean bitdead;
    final boolean[] ACTIVE = {false, false, false, false, false};

    long shootLastTime = 0;
    long moveLastTime = 0;

    // 坦克构造方法，要求初始化坦克坐标及方向
    public Tank(int x, int y, Direct direct) {
        super(x, y, direct);
        setOldX(x);
        setOldY(y);
        maxbuttle = 1;
        setWidth(32);
        setHeight(32);
        setSpeed(16);
        setCango(Tank.GO_MAP);
        this.star = new Star(this);

    }

    public static EnumMap<MapElementType, Boolean> GO_MAP = new EnumMap<>(MapElementType.class);

    static {
        GO_MAP.put(MapElementType.TREE, true);
        GO_MAP.put(MapElementType.SNOW, true);
        GO_MAP.put(MapElementType.BRICK, false);
        GO_MAP.put(MapElementType.WATER, false);
        GO_MAP.put(MapElementType.IRON, false);
    }

    public void draw(Graphics g, BufferedImage image) {
        if (star.getIsLive()) {
            star.draw(g);
        } else {
            g.drawImage(image, getX() + 2, getY() + 2, 28, 28, Game.getStage());
        }
    }

    @Override
    public void draw(Graphics g) {
        draw(g, getImage());
    }

    public void decrBulletNum(){
        this.bulletNum--;
    }

    void initMove() {
        for (int i = 0; i < ACTIVE.length - 1; i++) {
            ACTIVE[i] = false;
        }
    }

    // 移动
    @Override
    public void move() {
        // Game.PlaySound("移动");
        super.move();
    }

    // 判断是否撞上其他坦克
    boolean isTouchOtherTanks() {
        return Ztream.of(Game.stage.getTanks()).anyMatch(e -> e != this && isTouch(e));
    }

    private void stageAdd(Bullet bullet) {
        Game.getStage().elements.add(bullet);
        bulletNum++;
    }

    private void creatBullet(Direct direct) {
        Bullet bullet = new Bullet(this);
        bullet.setDirect(direct);
        stageAdd(bullet);
    }

    private void creatGBullet(Direct direct) {
        Bullet bullet = new Bullet(this);
        bullet.setDirect(direct);
        bullet.setReach(120);
        bullet.setCanTurn(true);
        stageAdd(bullet);
    }

    private void creatLaser() {
        Bullet bullet = new Laser(this, getDirect());
        stageAdd(bullet);
    }

    private void creatWave() {
        Wave wave = new Wave(this);
        stageAdd(wave);
    }

    // 发射子弹方法
    void shoot() {
        if (bulletNum < maxbuttle) {
            if (bulletType == 0) {
                creatBullet(getDirect());
            } else if (bulletType == 1) {
                creatLaser();
            } else if (bulletType == 2) {
                creatBullet(getDirect());
                creatBullet(Direct.get(getDirect().ordinal() + 4));
                int directTemp = (getDirect().ordinal() + 5) < 8 ? getDirect().ordinal() + 5 : 4;
                creatBullet(Direct.get(directTemp));
            } else if (bulletType == 3) {
                creatGBullet(getDirect());
            } else if (bulletType == 4) {
                creatWave();
            }
        }
    }
}