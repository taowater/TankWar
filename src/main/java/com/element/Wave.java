package com.element;

import com.element.tank.Tank;
import com.game.Game;
import com.history.core.util.stream.Ztream;
import com.util.ImageUtil;

import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicBoolean;


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
        AtomicBoolean flag = new AtomicBoolean(false);
        Ztream.of(Game.getStage().getEnemies()).parallel().forEach(e -> {
            if (isTouch(e)) {
                e.setIsLive(false);
                flag.set(true);
            }
        });
        return flag.get();
    }

    public void death() {
        setIsLive(false);
        getMaster().decrBulletNum();
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage192("wave", getDirect().ordinal() * 192, 0));
        g.drawImage(getImage(), getX(), getY(), 192, 192, Game.getStage());

        if (Game.pause) {
            return;
        }
        bitTank();
        if (getReach() > 0) {
            setReach(getReach() - 1);
        } else {
            death();
        }

    }
}