package com.element.tank;

import com.element.enums.Direct;
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
        if (star.getIsLive()) {
            star.draw(g);
        } else if (getIsLive()) {
            g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), Game.stage);
            if (flash.getIsLive()) {
                flash.draw(g);
            }
        }
    }
}
