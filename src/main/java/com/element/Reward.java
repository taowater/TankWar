package com.element;

import com.util.ImageUtil;
import com.util.MusicUtil;

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
        this.setLife(320);
        this.isDraw = true;
        MusicUtil.play("奖励");
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage32("material", 32 * id, 64));
        if (getLife() > 0 && isDraw) {
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