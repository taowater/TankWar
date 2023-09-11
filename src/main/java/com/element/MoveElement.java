package com.element;

import com.element.map.MapElement;
import com.game.Game;

// 可移动元素块的类
public class MoveElement extends ElementOld {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int LEFT_UP = 4;
    public static final int RIGHT_UP = 5;
    public static final int RIGHT_DOWN = 6;
    public static final int LEFT_DOWN = 7;
    int oldx, oldy;
    // 方向
    int direct;
    int speed;
    boolean[] cango;
    boolean going = false;

    private MoveElement(int x, int y) {
        super(x, y);
    }

    MoveElement(int x, int y, int direct) {
        super(x, y);
        this.direct = direct;
    }

    void setDirect(int direct) {
        this.direct = direct;
    }
    int getL(int direct) {
        if (direct == 0) {
            return 3;
        }
        return direct - 1;
    }

    int getR(int direct) {
        if (direct == 3) {
            return 0;
        }
        return direct + 1;
    }

    int getContrary(int direct) {
        return getR(getR(direct));
    }

    // 移动
    public void move() {
        switch (this.direct) {
            case UP:// 上
                y -= speed;
                break;
            case RIGHT:// 右
                x += speed;
                break;
            case DOWN:// 下
                y += speed;
                break;
            case LEFT:// 左
                x -= speed;
                break;
        }
    }

    // 获取当前战场二维通行图
    int[][] getCurrentMap(boolean[] cango) {
        int[][] map = Game.getStageMap();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (i == map.length - 1 || j == map[0].length - 1) {
                    map[i][j] = 1;
                } else {
                    if (cango[map[i][j]] && cango[map[i][j + 1]] && cango[map[i + 1][j]] && cango[map[i + 1][j + 1]]) {
                        map[i][j] = 0;
                    } else {
                        map[i][j] = 1;
                    }
                }
            }
        }
        for (Tank tank : Game.getStage().getAllTank()) {
            if (tank != this) {
                int m = tank.y / 16;
                int n = tank.x / 16;
                map[m][n] = 1;
                if (n + 1 < map[0].length) {
                    map[m][n + 1] = 1;
                }
                if (m + 1 < map.length) {
                    map[m + 1][n] = 1;
                }
                if (n + 1 < map[0].length && m + 1 < map.length) {
                    map[m + 1][n + 1] = 1;
                }
            }
        }
        return map;
    }

    // 记录上一个可移动的坐标
    void setOldXY() {
        oldx = x;
        oldy = y;
    }

    // 修正坐标
    void stay() {
        x = oldx;
        y = oldy;
        going = false;
    }

    boolean isTouchWall() {
        for (int i = 0; i < Game.getStage().elements.size(); i++) {
            MapElement element = Game.getStage().elements.get(i);
            if ((isTouch(element) && !this.cango[element.type])) {
                return true;
            }
        }
        if (!isInStage()) {
            return true;
        }
        return false;
    }

    int getNextStep(int i, int j) {
        int direct = this.direct;
        int m = y / 16;
        int n = x / 16;
        if (m == i) {
            if (n == j + 1) {
                direct = 3;
            } else if (n == j - 1) {
                direct = 1;
            }
        } else if (n == j) {
            if (m == i + 1) {
                direct = 0;
            } else if (m == i - 1) {
                direct = 2;
            }
        }
        return direct;
    }
}
