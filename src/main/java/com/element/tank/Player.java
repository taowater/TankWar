package com.element.tank;

import com.element.Flash;
import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.element.map.MapElement;
import com.element.tank.Tank;
import com.game.Game;
import com.history.core.util.stream.Ztream;
import com.util.MusicUtil;
import lombok.Data;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

//玩家操作坦克的类
@Data
public class Player extends Tank {
    private int score = 0;
    private int level = 1;
    private int[] DIRECTKEY = new int[5];
    private int maxlife;
    private Direct newDirect;

    public Player(int x, int y, Direct direct) {
        super(x, y, direct);
        this.isPlayer = true;
        this.setSpeed(4);
        this.newDirect = direct;
        this.maxbuttle = 1;
        this.maxlife = 99;
        this.bulletType = 0;
        this.setImage(Game.getMaterial("player1"));
        this.flash = new Flash(this);
    }

    public void setImage(int i) {
        setImage( Game.getMaterial("player" + i));
    }

    // 描绘
    @Override
    public void draw(Graphics g) {
        BufferedImage tank = getImage().getSubimage((level - 1) * 56, 28 * getDirect().ordinal(), 28, 28);
        if (star.getIsLive()) {
            star.draw(g);
        } else if (getIsLive()) {
            star.dispose();
            super.draw(g, tank);
            if (flash.getIsLive()) {
                flash.draw(g);
            }
            if (!Game.pause) {
                setOldXY();
                active();
                beRewarded();
            }
        } else {
            if (maxlife > 0) {
                Reborn();
            }
        }
    }

    // 行动
    void active() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            if (ACTIVE[i] || getGoing()) {
                if (time - moveLastTime >= 20) {
                    moveLastTime = time;
                    if (!getGoing()) {
                        setDirect(newDirect);
                    }
                    move();
                }
            }
        }
        if (ACTIVE[4]) {
            if (time - shootLastTime >= 500) {
                shoot();
                shootLastTime = time;
            }
        }
    }

    // 获得奖励
    void beRewarded() {
        Ztream.of(Game.getStage().getRewards()).forEach(e -> {
            if (getRect().intersects(e.getRect())) {
                Game.stage.Reward(this, e);
                e.setIsLive(false);
                MusicUtil.play("奖励");
            }
        });
    }

    public void decrMaxLife(){
        this.maxlife--;
    }

    // 移动
    @Override
    public void move() {
        super.move();
         setGoing(  getX() % 16 != 0 || getY() % 16 != 0);
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
        }
    }

    void clearBrick() {
        for (int i = 0; i < Game.getStage().elements.size(); i++) {
            MapElement element = Game.getStage().elements.get(i);
            if (isTouch(element)) {
                if (element.getMapType() == MapElementType.BRICK) {
                    MusicUtil.play("移动");
                    Game.getStage().elements.remove(element);
                }
            }
        }
    }

    void Reborn() {
       setX(  32 * 4);
        setY ( 32 * 12);
        if (this.DIRECTKEY == Game.PlayerDIRECT[1]) {
            setX( 32 * 8);
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

    public void KeyPressed(KeyEvent e) {
        if (getIsLive()) {
            for (int i = 0; i < 5; i++) {
                if (e.getKeyCode() == DIRECTKEY[i]) {
                    ACTIVE[i] = true;
                    if (i < 4) {
                        newDirect = Direct.get(i);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (getIsLive()) {
            for (int i = 0; i < 5; i++) {
                if (e.getKeyCode() == DIRECTKEY[i]) {
                    ACTIVE[i] = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_1) {
                bulletType = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                bulletType = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                bulletType = 2;
            } else if (e.getKeyCode() == KeyEvent.VK_4) {
                bulletType = 3;
            } else if (e.getKeyCode() == KeyEvent.VK_5) {
                bulletType = 4;
            }
        }
    }
}


