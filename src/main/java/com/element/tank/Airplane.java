package com.element.tank;

import com.element.enums.Direct;
import com.element.tank.Player;
import com.game.Game;
import com.util.ImageUtil;

import java.awt.*;

public  class Airplane extends Player {

    public Airplane(int x, int y, int direct) {
        super(x, y, Direct.UP);
        setImage(ImageUtil.getMaterial("飞机"));
    }

    @Override
    public boolean isTouchWall() {
        return !isInStage();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(getImage(), getX(), getY(), 32, 32, Game.stage);
        if (star.getIsLive()) {
            star.draw(g);
        } else if (getIsLive()) {
            star.dispose();
            g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), Game.stage);
            if (flash.getIsLive()) {
                flash.draw(g);
            }
            if (!Game.pause) {
                setOldXY();
                active();
                beRewarded();
            }
        } else {
            if (getMaxlife() > 0) {
                Reborn();
            }
        }
    }
}