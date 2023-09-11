package com.element;

import com.ai.AStar;
import com.game.Game;

import java.awt.*;
import java.util.List;

// 敌方坦克的类
public class Enemy extends Tank {

    int target = 0;
    final int type;
    // 定义是否能行动的布尔变量
    public int mask = 0;
    private int flashtime;
    private boolean imageFlag = false;
    public boolean withReward;

    public Enemy(int x, int y, int type, int direct) {
        super(x, y, direct);
        this.isEnemy = true;
        this.type = type;
        this.maxbuttle = 1;
        this.withReward = false;
        this.speed = 2;
        this.mask = 100;
        this.bitdead = false;
        this.image = Game.getMaterial("enemy");
        init();
    }

    private void init() {
        if (type == 1) {
            this.mask = 200;
            speed = 4;
            target = Game.Rand(25);
        }else if(type==2){
            this.mask = 300;
        }else if(type ==3){
            this.mask = 500;
        }
    }

    // 描绘自身
    @Override
    public void draw(Graphics g) {
        int offset = imageFlag ? 1 : 0;
        image = Game.getMaterial("enemy").getSubimage(type * 28 * 4 + offset * 28, direct * 28, 28, 28);
        imageFlag = !imageFlag;
        if (isLive) {
            if (withReward) {
                if (flashtime > 1) {
                    image = Game.getMaterial("enemy").getSubimage(type * 28 * 4 + 2 * 28, direct * 28, 28,
                            28);
                }
            }
            super.draw(g);
            if (!Game.pause&&Game.stage.pausetime==0) {
                if (x % 16 == 0 && y % 16 == 0) {
                    setOldXY();
                    going = false;
                } else
                    going = true;
                if (!star.isLive) {
                    switch (type) {
                        case 0:
                            RandomGo();
                            if (Game.Rand(60) == 1) {
                                shoot();
                            }
                            break;
                        case 1:
                            if (y / 16 < 24) {
                                goToFort();
                            } else {
                                RandomGo();
                            }
                            if (canBit(Game.stage.fort) && Game.Rand(30) == 1) {
                                shoot();
                            }
                            break;
                        case 2:
                            RandomGo();
                            if (canBit(Game.stage.players.get(Game.Rand(1))) && Game.Rand(50) == 1) {
                                shoot();
                            }
                            break;
                        case 3:
                            trackMove();
                            if (canBit(Game.stage.players.get(Game.Rand(1))) && Game.Rand(10) == 1) {
                                shoot();
                            }
                            break;
                    }
                }
            }
        } else {
            Game.stage.enumber--;
            BigBomb bigbomb = new BigBomb(x, y);
            Game.getStage().bombs.add(bigbomb);
            Game.getStage().getEnemys().remove(this);
        }
        flashtime = Game.Reduce(flashtime, 0, 3, 1);
    }

    // 判断能否击中玩家
    private boolean canBit(ElementOld element) {
        if (element.isLive) {
            if (x == element.x) {
                if ((y > element.y && direct == 0) || (y < element.y && direct == 2)) {
                    return true;
                }
            } else if (y == element.y) {
                if ((x < element.x && direct == 1) || (x > element.x && direct == 3)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 追袭玩家
    private void trackMove() {
        if (Game.stage.players.size() > 0) {
            Player player = Game.stage.players.get(0);
            if (player != null && player.isLive) {
                int i = player.y / 16;
                int j = player.x / 16;
                if (i > 0 && j > 0 && player.isLive) {
                    if (!going) {
                        int[][] currenMap = getCurrentMap(Game.tankcango);
                        List<Point> path = new AStar(currenMap, y / 16, x / 16, i, j).search();
                        if (path.size() > 0) {
                            Point point = path.get(path.size() - 1);
                            this.direct = getNextStep(point.x, point.y);
                        }
                    }
                }
                move();
                if (isTouchOtherTanks() || isTouchWall()) {
                    stay();
                }
            } else {
                RandomGo();
            }
        }
    }

    // 偷袭碉堡
    private void goToFort() {
        if (Game.stage.fort.isLive) {
            Fort fort = Game.stage.fort;
            if (!going) {
                int[][] currenMap = getCurrentMap(Game.tankcango);
                List<Point> path = new AStar(currenMap, y / 16, x / 16, 24, target).search();
                if (path.size() > 0) {
                    Point point = path.get(path.size() - 1);
                    this.direct = getNextStep(point.x, point.y);
                }
            }
            move();
            if (isTouchOtherTanks() || isTouchWall()) {
                stay();
            }
        } else {
            RandomGo();
        }
    }

    // 随机移动
    private void RandomGo() {
        move();
        if (isTouchOtherTanks() || isTouchWall()) {
            stay();
            if (Game.Rand(2) > 0) {
                setDirect(getR(direct));
            } else {
                setDirect(getL(direct));
            }
        }
    }
}