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
        if (!getIsLive()) {
            return;
        }
        setIsLive(false);
        getMaster().decrBulletNum();
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage16("bullet_2", getDirect().ordinal() * 16, 0));
        g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), Game.getStage());
    }

    @Override
    public void update() {
        if (getLife() > 0) {
            setLife(getLife() - 1);
        } else {
            death();
            return;
        }
        bitTank();
    }

    private void length() {
        Tank master = getMaster();
        int masterX = master.getX();
        int masterY = master.getY();
        int masterWidth = master.getWidth();
        int masterHeight = master.getHeight();
        var obstacles = Game.getStage().getMapElements().stream()
                .filter(e -> e.getIsLive() && !e.getMapType().isBulletGo())
                .toList();
        switch (getDirect()) {
            case UP -> {
                int end = obstacles.stream()
                        .filter(e -> e.getX() < masterX + masterWidth && e.getX() + e.getWidth() > masterX)
                        .filter(e -> e.getY() + e.getHeight() <= masterY)
                        .mapToInt(e -> e.getY() + e.getHeight()).max().orElse(0);
                setX(masterX);
                setY(end);
                setWidth(masterWidth);
                setHeight(masterY - end);
            }
            case DOWN -> {
                int start = masterY + masterHeight;
                int end = obstacles.stream()
                        .filter(e -> e.getX() < masterX + masterWidth && e.getX() + e.getWidth() > masterX)
                        .filter(e -> e.getY() >= start)
                        .mapToInt(Element::getY).min().orElse(Game.getStage().getHeight());
                setX(masterX);
                setY(start);
                setWidth(masterWidth);
                setHeight(Math.max(0, end - start));
            }
            case RIGHT -> {
                int start = masterX + masterWidth;
                int end = obstacles.stream()
                        .filter(e -> e.getY() < masterY + masterHeight && e.getY() + e.getHeight() > masterY)
                        .filter(e -> e.getX() >= start)
                        .mapToInt(Element::getX).min().orElse(Game.getStage().getWidth());
                setX(start);
                setY(masterY);
                setWidth(Math.max(0, end - start));
                setHeight(masterHeight);
            }
            case LEFT -> {
                int end = obstacles.stream()
                        .filter(e -> e.getY() < masterY + masterHeight && e.getY() + e.getHeight() > masterY)
                        .filter(e -> e.getX() + e.getWidth() <= masterX)
                        .mapToInt(e -> e.getX() + e.getWidth()).max().orElse(0);
                setX(end);
                setY(masterY);
                setWidth(masterX - end);
                setHeight(masterHeight);
            }
            default -> { }
        }
    }
}
