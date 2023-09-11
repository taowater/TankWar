package com.element;

import com.game.Game;

import java.awt.*;

public class Flash extends ElementOld {

	private final Player master;
	public int life;

	public Flash(Player player) {
		super(player.x, player.y);
		this.master = player;
		this.life = 32;
	}

	@Override
    public void draw(Graphics g) {
		image = Game.getMaterial("material").getSubimage(life % 2 * 32, 32, 32, 32);
		g.drawImage(image, master.x, master.y, width, height, Game.getStage());
		downLife();
	}
}
