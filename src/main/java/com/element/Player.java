package com.element;

import com.element.map.MapElement;
import com.game.Game;
import com.util.MusicUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

//玩家操作坦克的类
public class Player extends Tank {
    int score = 0;
    public int level = 1;
    public int[] DIRECTKEY = new int[5];
    public int maxlife;
    private int newDirect;

    public Player(int x, int y, int direct) {
        super(x, y, direct);
        this.isPlayer = true;
        this.speed = 4;
        this.newDirect = direct;
        this.maxbuttle = 1;
        this.maxlife = 99;
        this.bulletType = 0;
        this.image = Game.getMaterial("player1");
        this.flash = new Flash(this);
    }

    public void setImage(int i) {
        image = Game.getMaterial("player" + i);
    }

    // 描绘
    @Override
    public void draw(Graphics g) {
        BufferedImage tank = image.getSubimage((level - 1) * 56, 28 * direct, 28, 28);
        if (star.isLive) {
            star.draw(g);
        } else if (isLive) {
            star.dispose();
            super.draw(g, tank);
            if (flash.isLive) {
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
            if (ACTIVE[i] || going) {
                if (time - moveLastTime >= 20) {
                    moveLastTime = time;
                    if (!going) {
                        direct = newDirect;
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
        for (Reward reward : Game.getStage().getRewards()) {
            if (getRect().intersects(reward.getRect())) {
                Game.stage.Reward(this, reward);
                reward.isLive = false;
                MusicUtil.play("奖励");
            }
        }
    }

    // 移动
    @Override
    public void move() {
        super.move();
        going = x % 16 != 0 || y % 16 != 0;
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
        }
    }

    void ClearBrick() {
        for (int i = 0; i < Game.getStage().elements.size(); i++) {
            MapElement element = Game.getStage().elements.get(i);
            if (isTouch(element)) {
                if (element.type == 3) {
                    MusicUtil.play("移动");
                    Game.getStage().elements.remove(element);
                }
            }
        }
    }

    void Reborn() {
        x = 32 * 4;
        y = 32 * 12;
        if (this.DIRECTKEY == Game.PlayerDIRECT[1]) {
            x = 32 * 8;
        }
        direct = 0;
        speed = 4;
        level = 1;
        star.life = 14;
        star.isLive = true;
        flash.life = 32;
        flash.isLive = true;
        isLive = true;
        initMove();
    }

    public void KeyPressed(KeyEvent e) {
        if (isLive) {
            for (int i = 0; i < 5; i++) {
                if (e.getKeyCode() == DIRECTKEY[i]) {
                    ACTIVE[i] = true;
                    if (i < 4) {
                        newDirect = i;
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (isLive) {
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

class Airplane extends Player {

    public Airplane(int x, int y, int direct) {
        super(x, y, 0);
        this.image = Game.getMaterial("飞机");
    }

    @Override
    public boolean isTouchWall() {
        return !isInStage();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, 32, 32, Game.stage);
        if (star.isLive) {
            star.draw(g);
        } else if (isLive) {
            star.dispose();
            g.drawImage(image, x, y, width, height, Game.stage);
            if (flash.isLive) {
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
}
