package com.element;

import com.game.Game;
import com.game.Music;
import com.history.core.util.stream.Ztream;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

// 坦克的类，所有坦克的父类
public class Tank extends MoveElement {

	public boolean isPlayer;
	public boolean isEnemy;
	public int bulletType = 0;
	int buttlenumber = 0;
	// 最大子弹数
	int maxbuttle;
	// 定义新生成时闪光
	Star star;
	// 定义新生成时无敌闪烁
	public Flash flash = null;
	boolean bitdead;
	final boolean[] ACTIVE = {false, false, false, false, false};

	long shootLastTime = 0;
	long moveLastTime = 0;

	// 坦克构造方法，要求初始化坦克坐标及方向
    Tank(int x, int y, int direct) {
		super(x, y, direct);
		this.oldx = x;
		this.oldy = y;
		this.maxbuttle = 1;
		this.width = 32;
		this.height = 32;
		this.speed = 16;
		this.cango = Game.tankcango;
		this.star = new Star(this);

	}

	public void draw(Graphics g, BufferedImage image) {
		if (star.isLive) {
			star.draw(g);
		} else {
			g.drawImage(image, x + 2, y + 2, 28, 28, Game.getStage());
		}
	}

	@Override
	public void draw(Graphics g) {
		draw(g,this.image);
	}

	void initMove() {
		for (int i = 0; i < ACTIVE.length - 1; i++) {
			ACTIVE[i] = false;
		}
	}

	// 移动
	@Override
	public void move() {
		// Game.PlaySound("移动");
		super.move();
	}

	// 判断是否撞上其他坦克
	boolean isTouchOtherTanks() {
		return Ztream.of(Game.stage.getAllTank()).anyMatch(e -> e != this && isTouch(e));
	}

	private void stageAdd(Bullet bullet) {
		Game.getStage().bullets.add(bullet);
		buttlenumber++;
	}

	private void creatBullet(int direct) {
		Bullet bullet = new Bullet(this);
		bullet.direct = direct;
		stageAdd(bullet);
	}
	private void creatGBullet(int direct) {
		Bullet bullet = new Bullet(this);
		bullet.direct = direct;
		bullet.reach = 120;
		bullet.setCanGuaiWan(true);
		stageAdd(bullet);
	}
	private void creatLaser() {
		Bullet bullet = new Laser(this, direct);
		stageAdd(bullet);
	}
	private void creatWave() {
    	Wave wave = new Wave(this);
		stageAdd(wave);
	}

	// 发射子弹方法
    void shoot() {
		if (buttlenumber < maxbuttle) {
			if (bulletType == 0) {
				creatBullet(this.direct);
			} else if (bulletType == 1)
				creatLaser();
			else if (bulletType == 2) {
				creatBullet(direct);
				creatBullet(direct + 4);
				int directTemp = (direct + 5) < 8 ? direct + 5 : 4;
				creatBullet(directTemp);
			}else if (bulletType == 3) {
				creatGBullet(this.direct);
			}
			else if (bulletType == 4) {
				creatWave();
			}
		}
	}
}