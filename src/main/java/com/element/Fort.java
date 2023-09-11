package com.element;

import com.game.Game;

import java.awt.*;

public class Fort extends ElementOld {
	public Fort(int x, int y) {
		super(x, y);
		image = Game.getMaterial("material").getSubimage(0,3*32,32,32);
	}

	@Override
    public void draw(Graphics g) {
		if(!isLive){
			image = Game.getMaterial("material").getSubimage(32,3*32,32,32);
		}
		g.drawImage(image,x,y,32,32,Game.stage);
	}
}
