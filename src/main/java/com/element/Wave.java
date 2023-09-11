package com.element;

import com.game.Game;
import com.history.core.util.stream.Ztream;

import java.awt.Graphics;
import java.util.List;


public class Wave extends Bullet {

    public Wave(Tank tank) {
        super(tank);
        this.reach = 8;
        this.width = 192;
        this.height = 192;
        switch (direct) {
            case 0 -> {
                this.x = master.x - 96 + 16;
                this.y = y - 96 - 32;
            }
            case 1 -> {
                this.x = master.x + 32 - 48;
                this.y = y - 96 + 16;
            }
            case 2 -> {
                this.x = master.x - 96 + 16;
                this.y = y - 32;
            }
            case 3 -> {
                this.x = master.x - 192 + 48;
                this.y = y - 96 + 16;
            }
        }
    }

    @Override
    public void bitTank() {
        List<Enemy> tanks = Game.getStage().getEnemys();
        Ztream.of(Game.getStage().getRewards()).forEach(e->{});

        for (Tank tank : tanks) {
            if (this.isTouch(tank)) {
                tank.isLive = false;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        image = Game.getMaterial("wave").getSubimage(direct * 192, 0, 192, 192);
        g.drawImage(image, x, y, 192, 192, Game.getStage());
        if (isLive) {
            if (!Game.pause) {
                bitTank();
                if (reach > 0)
                    reach--;
                else
                    isLive = false;
            }
        } else {
            Game.getStage().bullets.remove(this);
            master.buttlenumber--;
        }
    }
}