package com.scene;

import com.game.Game;
import com.game.TankWar;

import java.awt.*;
import java.awt.event.KeyEvent;

public class StageStart extends Scene {
	private int starttime = 8;
	public StageStart() {
		setBounds(0, 0, TankWar.WIDTH, TankWar.HEIGHT);
	}

	public void paint(Graphics g) {
		super.paint(g);
		start(g);

	}
	private void start(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(),this.getHeight());
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.getWidth(), (8 - starttime) * 32);
		g.fillRect(0, this.getHeight() - (8 - starttime) * 32, this.getWidth(), (8 - starttime) * 32);
		g.setColor(Color.BLACK);
		Game.drawText("STAGE "+ Game.stagesth, 200,235,0,g,this);
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			Game.tankWar.toGame();
		}
	}

	public void run() {
		while (true) {
			Game.Sleep(50);
			if (starttime > 0) {
				starttime--;
			}
			this.repaint();
		}
	}

}
