package com.element;

import com.element.tank.Tank;
import com.game.Game;
import com.util.ImageUtil;

import java.awt.*;

/**
 * @author Zhu_wuliu
 */ // 坦克出现时闪光的类
public class Star extends ElementOld {

    public Star(Tank tank) {
        super(tank.getX(), tank.getY());
        this.setLife(14);
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage32("material", getLife() % 7 * 32, 0));
        super.draw(g);
        downLife();
    }
}
