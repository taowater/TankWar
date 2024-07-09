package com.element.tank;

import com.element.Flash;
import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.game.Game;
import com.taowater.ztream.Ztream;
import com.util.ImageUtil;
import com.util.MusicUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * 玩家操作坦克的类
 *
 * @author zhu56
 * @date 2023/09/28 00:36
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Player extends Tank {
    private int score = 0;
    private int level = 1;
    private int[] DIRECTKEY = new int[5];
    private int maxlife;
    private Direct newDirect;

    public Player(int x, int y, Direct direct) {
        super(x, y, direct);
        this.setSpeed(4);
        this.newDirect = direct;
        this.maxbuttle = 1;
        this.maxlife = 99;
        this.bulletType = 0;
        this.setImage(ImageUtil.getMaterial("player1"));
        this.flash = new Flash(this);
    }

    public void setImage(int i) {
        setImageName("player" + i);
    }

    // 描绘
    @Override
    public void draw(Graphics g) {
        BufferedImage tank = ImageUtil.getMaterial(getImageName()).getSubimage((level - 1) * 56, 28 * getDirect().ordinal(), 28, 28);
        if (star.getIsLive()) {
            star.draw(g);
        } else if (getIsLive()) {
            star.dispose();
            super.draw(g, tank);
            if (flash.getIsLive()) {
                flash.draw(g);
            }
            if (!Game.pause) {
                setOldPosition();
                active();
                beRewarded();
            }
        } else {
            if (maxlife > 0) {
                reborn();
            }
        }
    }

    // 行动
    void active() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            if ((ACTIVE[i] || getGoing()) && time - moveLastTime >= 20) {
                moveLastTime = time;
                if (!getGoing()) {
                    setDirect(newDirect);
                }
                move();
            }
        }
        if (ACTIVE[4] && time - shootLastTime >= 500) {
            shoot();
            shootLastTime = time;
        }
    }

    // 获得奖励
    void beRewarded() {
        Ztream.of(Game.getStage().getRewards()).forEach(e -> {
            if (getRect().intersects(e.getRect())) {
                Game.stage.reward(this, e);
                e.setIsLive(false);
                MusicUtil.play("奖励");
            }
        });
    }

    public void decrMaxLife() {
        this.maxlife--;
    }

    // 移动
    @Override
    public void move() {
        super.move();
        setGoing(getX() % 16 != 0 || getY() % 16 != 0);
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
        }
    }

    void clearBrick() {
        Ztream.of(Game.getStage().getMapElements()).forEach(e -> {
            if (isTouch(e) && e.getMapType() == MapElementType.BRICK) {
                MusicUtil.play("移动");
                Game.getStage().removeElement(e);
            }
        });
    }

    void reborn() {
        setX(32 * 4);
        setY(32 * 12);
        if (this.DIRECTKEY == Game.PlayerDIRECT[1]) {
            setX(32 * 8);
        }
        setDirect(Direct.UP);
        setSpeed(4);
        level = 1;
        star.setLife(14);
        star.setIsLive(true);
        flash.life = 32;
        flash.setIsLive(true);
        setIsLive(true);
        initMove();
    }

    public void keyPressed(KeyEvent e) {
        if (!getIsLive()) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            if (e.getKeyCode() == DIRECTKEY[i]) {
                ACTIVE[i] = true;
                if (i < 4) {
                    newDirect = Direct.get(i);
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (!getIsLive()) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            if (e.getKeyCode() == DIRECTKEY[i]) {
                ACTIVE[i] = false;
            }
        }
        var tempType = e.getKeyCode() - 49;
        if (tempType >= 0 && tempType < 5) {
            bulletType = tempType;
        }
    }
}


