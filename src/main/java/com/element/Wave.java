package com.element;

import com.element.tank.Tank;
import com.game.Game;
import com.taowater.ztream.Ztream;
import com.util.ImageUtil;

import java.awt.*;


public class Wave extends Bullet {

    public Wave(Tank tank) {
        super(tank);
        this.setReach(8);
        this.setWidth(192);
        this.setHeight(192);
        int masterX = getMaster().getX();
        int masterY = getMaster().getY();
        switch (getDirect()) {
            case UP -> {
                setX(masterX - 96 + 16);
                setY(getY() - 96 - 32);
            }
            case RIGHT -> {
                setX(masterX + 32 - 48);
                setY(getY() - 96 + 16);
            }
            case DOWN -> {
                setX(masterX - 96 + 16);
                setY(getY() - 32);
            }
            case LEFT -> {
                setX(masterX - 192 + 48);
                setY(getY() - 96 + 16);
            }
        }
    }

    @Override
    public boolean bitTank() {
        boolean hit = false;
        for (var enemy : Game.getStage().getEnemies()) {
            if (enemy.getIsLive() && isTouch(enemy)) {
                hit |= hitEnemy(enemy);
            }
        }
        return hit;
    }

    public void death() {
        if (!getIsLive()) {
            return;
        }
        setIsLive(false);
        getMaster().decrBulletNum();
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage192("wave", getDirect().ordinal() * 192, 0));
        g.drawImage(getImage(), getX(), getY(), 192, 192, Game.getStage());
    }

    @Override
    public void update() {
        bitTank();
        if (getReach() > 0) {
            setReach(getReach() - 1);
        } else {
            death();
        }

    }
}
