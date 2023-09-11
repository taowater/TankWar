package com.element;

import com.element.inter.Draw;
import com.game.Game;
import com.scene.Scene;
import com.util.MusicUtil;

import java.awt.*;
import java.awt.image.BufferedImage;



/**
 * @author Zhu_wuliu
 */ // 界面中所有元素块的类
public class ElementOld implements Draw {
	// x坐标
	public int x;
	// y坐标
	public int y;
	public int width = 32;
	public int height = 32;
	// 定义存活变量
	public int life;
	// 图片
	public BufferedImage image;
	// 定义是否存活的布尔变量
	public boolean isLive;

	public ElementOld(int x, int y) {
		this.x = x;
		this.y = y;
		this.image = Game.getMaterial("material");
		this.isLive = true;
	}

	boolean isInStage() {
		return x >= 0 && x <= Game.stage.getWidth() - width && y >= 0 && y <= Game.stage.getHeight() - height;
	}
	public boolean isTouch(ElementOld material) {
		return getRect().intersects(material.getRect()) && material.isLive;
	}
	// 获取自身矩形
	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}
	// 描绘自身
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, x, y, width, height, Game.getStage());
	}

	public void draw(Graphics g, Scene scene) {
		g.drawImage(image, x, y, width, height, scene);
	}
	// 属性值自键
    void downLife() {
		if (life > 0)
			life--;
		else
			isLive = false;
	}

	void dispose() {
		this.image = null;
	}
}

class BigBomb extends Bomb {
	public BigBomb(int x, int y) {
		super(x, y);
		this.life = 4;
		MusicUtil.play("爆炸");
	}

	public boolean isTouch(ElementOld material) {
		return getRect().intersects(material.getRect()) && material.isLive;
	}

	@Override
	public void draw(Graphics g) {
		image = Game.getMaterial("material").getSubimage(64 + (4 - life) * 32, 32, 32, 32);
		super.draw(g);
	}

}

