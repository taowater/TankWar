package com.element;

import com.game.Game;

import java.awt.*;

/**
 * @author Zhu_wuliu
 */ // 坦克出现时闪光的类
public class Star extends ElementOld {

	public Star(Tank tank) {
		super(tank.x, tank.y);
		this.life = 14;
	}

	@Override
    public void draw(Graphics g) {
		image = Game.getMaterial("material").getSubimage(life % 7 * 32, 0, width, height);
		super.draw(g);
		downLife();
	}
}
