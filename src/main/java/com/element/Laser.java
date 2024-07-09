package com.element;

import com.element.enums.Direct;
import com.element.tank.Tank;
import com.game.Game;
import com.taowater.ztream.Ztream;
import com.util.ImageUtil;

import java.awt.*;

/**
 * 激光
 *
 * @author zhu56
 * @date 2023/09/14 00:43
 */
public class Laser extends Bullet {

    public Laser(Tank master, Direct direct) {
        super(master);
        setReach(24);
        this.setLife(8);
        int reach = getReach();
        switch (direct.ordinal()) {
            case 0 -> {
                setHeight(16 * reach);
                this.setY(master.getY() - getHeight());
            }
            case 2 -> {
                setHeight(16 * reach);
                this.setY(master.getY() + 32);
            }
            case 1 -> {
                setWidth(16 * reach);
                this.setX(master.getX() + 32);
            }
            case 3 -> {
                setWidth(16 * reach);
                this.setX(master.getX() - getWidth());
            }
            default -> {
            }
        }
        length();
    }

    @Override
    public void death() {
        setIsLive(false);
        getMaster().decrBulletNum();
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage16("bullet_2", getDirect().ordinal() * 16, 0));
        g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), Game.getStage());
        if (getLife() > 0) {
            setLife(getLife() - 1);
        } else {
            death();
            return;
        }
        if (!Game.pause) {
            bitTank();
        }
        if (!isInStage()) {
            death();
        }
    }

    private void length() {
        Tank master = getMaster();
        int masterX = master.getX();
        int masterY = master.getY();
        int masterWidth = master.getWidth();
        int masterHeight = master.getHeight();
        Ztream.of(Game.getStage().getMapElements()).forEach(e -> {
            if (isTouch(e) && !e.getMapType().isBulletGo()) {
                int eX = e.getX();
                int eY = e.getY();
                int eWidth = e.getWidth();
                int eHeight = e.getHeight();
                switch (getDirect()) {
                    case UP -> {
                        eHeight = masterY - (eY + eHeight);
                        setY(masterY - eHeight);
                    }
                    case DOWN -> {
                        setHeight(eY - (masterY + masterHeight));
                        setY(masterY + 32);
                    }
                    case RIGHT -> {
                        setWidth(eX - (masterX + masterWidth));
                        setX(masterX + 32);
                    }
                    case LEFT -> {
                        eWidth = masterX - (eX + eWidth);
                        setX(masterX - eWidth);
                    }
                    default -> {
                    }
                }
            }
        });
    }
}