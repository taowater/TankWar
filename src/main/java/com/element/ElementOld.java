package com.element;

import com.element.inter.Draw;
import com.game.Game;
import com.scene.Scene;
import com.util.MusicUtil;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * 界面中所有元素块的类
 *
 * @author zhu56
 * @date 2023/09/12 21:17
 */
@Data
public class ElementOld implements Draw {
    // x坐标
    private int x;
    // y坐标
    private int y;
    private int width = 32;
    private int height = 32;
    // 定义存活变量
    private int life;
    // 图片
    private BufferedImage image;
    // 定义是否存活的布尔变量
    private Boolean isLive;

    public ElementOld(int x, int y) {
        this.x = x;
        this.y = y;
        this.image = Game.getMaterial("material");
        this.isLive = true;
    }

    public boolean isInStage() {
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
        if (life > 0) {
            life--;
        } else {
            isLive = false;
        }
    }

    public void dispose() {
        this.image = null;
    }

    public void incrX(int n) {
        this.x += n;
    }

    public void incrY(int n) {
        this.y += n;
    }

    public void decrX(int n) {
        this.x -= n;
    }

    public void decrY(int n) {
        this.y -= n;
    }
}



