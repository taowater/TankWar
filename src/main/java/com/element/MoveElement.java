package com.element;

import com.element.enums.Direct;
import com.element.enums.MapElementType;
import com.element.tank.Tank;
import com.game.Game;
import com.taowater.ztream.Ztream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.function.Function;

// 可移动元素块的类
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class MoveElement extends Element {

    private int oldX;
    private int oldY;
    /**
     * 方向
     */
    private Direct direct;
    private int speed;
    private Boolean going = false;


    protected MoveElement(int x, int y, Direct direct) {
        super(x, y);
        this.direct = direct;
    }

    // 移动
    public void move() {
        switch (this.direct) {
            case UP -> decrY(speed);
            case RIGHT -> incrX(speed);
            case DOWN -> incrY(speed);
            case LEFT -> decrX(speed);
            default -> throw new IllegalStateException("Unexpected value: " + this.direct);
        }
    }

    /**
     * 获取当前战场二维通行图
     *
     * @param cango
     * @return {@link int[][]}
     */
    public int[][] getCurrentMap(boolean[] cango) {
        int[][] map = Game.getStageMap();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (i == map.length - 1 || j == map[0].length - 1) {
                    map[i][j] = 1;
                    continue;
                }
                map[i][j] = (cango[map[i][j]] && cango[map[i][j + 1]] && cango[map[i + 1][j]] && cango[map[i + 1][j + 1]]) ? 0 : 1;
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
    public void setOldPosition() {
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
        Function<MapElementType, Boolean> fun;
        if (this instanceof Tank) {
            fun = MapElementType::isTankGo;
        } else if (this instanceof Bullet) {
            fun = MapElementType::isBulletGo;
        } else {
            fun = MapElementType::isBulletGo;
        }
        return !isInStage() || Ztream.of(Game.getStage().getMapElements())
                .anyMatch(e -> (isTouch(e) && !fun.apply(e.getMapType())));
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
