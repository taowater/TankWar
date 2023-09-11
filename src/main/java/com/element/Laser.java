package com.element;

import com.game.Game;
import com.history.core.util.stream.Ztream;

import java.awt.*;

public class Laser extends Bullet {

    public Laser(Tank master, int direct) {
        super(master);
        this.reach = 24;
        this.life = 8;
        switch (direct) {
            case 0:
                height = 16 * reach;
                this.y = master.y - height;
                break;
            case 2:
                height = 16 * reach;
                this.y = master.y + 32;
                break;
            case 1:
                width = 16 * reach;
                this.x = master.x + 32;
                break;
            case 3:
                width = 16 * reach;
                this.x = master.x - width;
                break;
        }
        Length();
    }

    @Override
    public void draw(Graphics g) {
        image = Game.getMaterial("bullet_2").getSubimage(direct * 16, 0, 16, 16);
        g.drawImage(image, x, y, width, height, Game.getStage());
        if (life > 0)
            life--;
        else
            isLive = false;
        if (isLive) {
            if (!Game.pause) {
                bitTank();
            }
            if (!isInStage())
                isLive = false;
        } else {
            master.buttlenumber--;
            Game.getStage().bullets.remove(this);
        }
    }

    private void Length() {
        Ztream.of(Game.getStage().elements).forEach(e->{
            if (isTouch(e) && !Bullet.GO_MAP.get(e.getMapType())) {
                switch (direct) {
                    case 0 -> {
                        height = master.y - (e.y + e.height);
                        this.y = master.y - height;
                    }
                    case 2 -> {
                        height = e.y - (master.y + master.height);
                        this.y = master.y + 32;
                    }
                    case 1 -> {
                        width = e.x - (master.x + master.width);
                        this.x = master.x + 32;
                    }
                    case 3 -> {
                        width = master.x - (e.x + e.width);
                        this.x = master.x - width;
                    }
                }
            }
        });
    }
}