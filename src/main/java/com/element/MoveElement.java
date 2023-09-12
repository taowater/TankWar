package com.element;

import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.game.Game;
import com.history.core.util.stream.Ztream;
import lombok.Data;

import java.util.EnumMap;

// 可移动元素块的类
@Data
public abstract class MoveElement extends ElementOld {

    private int oldX;
    private int oldY;
    // 方向
    private Direct direct;
    private int speed;

    private EnumMap<MapElementType, Boolean> cango;
    private Boolean going = false;


    public MoveElement(int x, int y, Direct direct) {
        super(x, y);
        this.direct = direct;
    }

    int getL(int direct) {
        if (direct == 0) {
            return 3;
        }
        return direct - 1;
    }

    // 移动
    public void move() {
        switch (this.direct) {
            case UP ->// 上
                    decrY(speed);
            case RIGHT ->// 右
                    incrX(speed);
            case DOWN ->// 下
                    incrY(speed);
            case LEFT ->// 左
                    decrX(speed);
        }
    }

    // 获取当前战场二维通行图
    public int[][] getCurrentMap(boolean[] cango) {
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
        Ztream.of(Game.getStage().getTanks()).forEach(tank -> {
            if (tank == this) {
                return;
            }
            int m = tank.getY() / 16;
            int n = tank.getX() / 16;
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
        });
        return map;
    }

    // 记录上一个可移动的坐标
    public void setOldXY() {
        setOldX(getX());
        setOldY(getY());
    }

    // 修正坐标
    public void stay() {
        setX(oldX);
        setY(oldY);
        going = false;
    }

    public boolean isTouchWall() {
        return Ztream.of(Game.getStage().getMapElements()).anyMatch(e -> (isTouch(e) && !getCango().get(e.getMapType()))) || !isInStage();
    }

    public Direct getNextStep(int i, int j) {
        int m = getY() / 16;
        int n = getX() / 16;
        if (m == i) {
            if (n == j + 1) {
                return Direct.LEFT;
            }
            if (n == j - 1) {
                return Direct.RIGHT;
            }
        } else if (n == j) {
            if (m == i + 1) {
                return Direct.UP;
            }
            if (m == i - 1) {
                return Direct.DOWN;
            }
        }
        return direct;
    }
}
