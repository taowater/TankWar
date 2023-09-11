package com.element;

import com.game.Game;

import java.awt.*;

/**
 * @author Zhu_wuliu
 */
public class Reward extends ElementOld {
	public final int id;
	private int time;
	private boolean isDraw;

	public Reward(int id, int x, int y) {
		super(x, y);
		this.id = id;
		this.time = 3;
		this.life = 320;
		this.isDraw = true;
		Game.PlaySound("奖励");
	}

	@Override
    public void draw(Graphics g) {
		image = Game.getMaterial("material").getSubimage(32 * id, 64, 32, 32);
		if (life > 0 && isDraw) {
			super.draw(g);
		}
		time--;
		if (time < 0) {
			isDraw = !isDraw;
			time = 3;
		}
		downLife();
	}
}