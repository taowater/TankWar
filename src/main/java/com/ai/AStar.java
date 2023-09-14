package com.ai;

import com.element.enums.Direct;
import com.game.Game;
import com.history.core.util.Any;
import com.history.core.util.EmptyUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zhu_wuliu
 */
public class AStar {

    private final Node[][] map;
    // 节点数组
    private final Node startNode;
    // 起点
    private Node endNode;
    // 使用ArrayList数组作为“开启列表”和“关闭列表”
    private final List<Node> open = new ArrayList<>();
    private final List<Node> close = new ArrayList<>();

    public AStar(int[][] currenMap, int x1, int y1, int x2, int y2) {

        map = new Node[currenMap.length][currenMap[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new Node(i, j, currenMap[i][j] == 0);
            }
        }
        startNode = map[x1][y1];
        endNode = map[x2][y2];
    }

    // 获取H值 currentNode：当前节点 endNode：终点
    private int getH(Node currentNode, Node endNode) {
        return (Math.abs(currentNode.getX() - endNode.getX()) + Math.abs(currentNode.getY() - endNode.getY()));
    }

    // 获取G值 currentNode：当前节点 return
    private int getG(Node currentNode) {
        Node fatherNode = currentNode.getFatherNode();
        if (fatherNode != null && (currentNode.getX() == fatherNode.getX() || currentNode.getY() == fatherNode.getY())) {
            // 判断当前节点与其父节点之间的位置关系（水平？对角线）
            return currentNode.getG() + 1;
        }
        return currentNode.getG();
    }

    // 获取F值 ： G + H currentNode return
    private int getF(Node currentNode) {
        return currentNode.getG() + currentNode.getH();
    }

    // 将选中节点周围的节点添加进“开启列表” node
    private void inOpen(Node node) {

        int x = node.getX();
        int y = node.getY();
        int endX = endNode.getX();
        int endY = endNode.getY();
        int[] directList = {0, 1, 2, 3};
        int direct = Game.getAinBdirection(new Point(y, x), new Point(endY, endX));

        int xValue = Math.abs(endY - y);
        int yValue = Math.abs(endX - x);
        if (direct == 4) {
            if (xValue > yValue) {
                directList = new int[]{1, 2, 0, 3};
            } else {
                directList = new int[]{2, 1, 3, 0};
            }
        } else if (direct == 5) {
            if (xValue > yValue) {
                directList = new int[]{3, 2, 0, 1};
            } else {
                directList = new int[]{2, 3, 1, 0};
            }
        } else if (direct == 6) {
            if (xValue > yValue) {
                directList = new int[]{3, 0, 2, 1};
            } else {
                directList = new int[]{0, 3, 1, 2};
            }
        } else if (direct == 7) {
            if (xValue > yValue) {
                directList = new int[]{1, 0, 2, 3};
            } else {
                directList = new int[]{0, 1, 3, 2};
            }
        }
        for (int i = 0; i < 4; i++) {
            Node node2 = getDirectNode(node, Direct.get(directList[i]));
            if (node2 != null && (node2.getCango() && !open.contains(node2))) {
                node2.setFatherNode(map[x][y]);
                // 将选中节点作为父节点
                node2.setG(getG(node2));
                node2.setH(getH(node2, endNode));
                node2.setF(getF(node2));
                open.add(node2);
            }
        }
    }

    // 将节点添加进“关闭列表”
    private void inClose(Node node, List<Node> open) {
        if (open.contains(node)) {
            node.setCango(false);
            // 设置为不可达
            open.remove(node);
            close.add(node);
        }
    }

    public List<Point> search() {
        // 对起点即起点周围的节点进行操作
        if (endNode == null || !endNode.getCango()) {
            for (int i = 0; i < 8; i++) {
                Node newEnd = getDirectNode(endNode, Direct.get(i));
                if (newEnd == null || !map[newEnd.getX()][newEnd.getY()].getCango()) {
                    continue;
                }
                endNode = newEnd;
                return search();
            }
        }
        var startX = startNode.getX();
        var startY = startNode.getY();
        var endX = endNode.getX();
        var endY = endNode.getY();
        inOpen(startNode);
        close.add(startNode);
        startNode.setCango(false);
        startNode.setFatherNode(startNode);
        Any.of(open).ifPresent(l -> l.sort(Comparator.comparing(Node::getF)));
        // 重复步骤
        do {
            if (EmptyUtil.isNotEmpty(open)) {
                inOpen(open.get(0));
                inClose(open.get(0), open);
            }
        } while (EmptyUtil.isNotEmpty(open) && !open.contains(endNode));
        // 知道开启列表中包含终点时，循环退出
        inClose(map[endX][endY], open);
        List<Point> path = null;
        if (EmptyUtil.isNotEmpty(close)) {
            path = new ArrayList<>();
            Node node = map[endX][endY];
            while (node != null && !(node.getX() == startX && node.getY() == startY)) {
                Point point = new Point(node.getX(), node.getY());
                path.add(point);
                node = node.getFatherNode();
            }
        }
        return path;
    }

    private Node getDirectNode(Node node, Direct direct) {
        var x = node.getX();
        var y = node.getY();
        switch (direct) {
            case UP -> {
                if (x - 1 >= 0) {
                    return map[x - 1][y];
                }
            }
            case RIGHT -> {
                if (y + 1 < map[0].length) {
                    return map[x][y + 1];
                }
            }
            case DOWN -> {
                if (x + 1 < map.length) {
                    return map[x + 1][y];
                }
            }
            case LEFT -> {
                if (y - 1 >= 0) {
                    return map[x][y - 1];
                }
            }
            case LEFT_UP -> {
                if (x - 1 >= 0 && y - 1 >= 0) {
                    return map[x - 1][y - 1];
                }
            }
            case RIGHT_UP -> {
                if (x - 1 >= 0 && y + 1 < map[0].length) {
                    return map[x - 1][y + 1];
                }
            }
            case RIGHT_DOWN -> {
                if (x + 1 < map.length && y + 1 < map[0].length) {
                    return map[x + 1][y + 1];
                }
            }
            case LEFT_DOWN -> {
                if (x + 1 < map.length && y - 1 >= 0) {
                    return map[x + 1][y - 1];
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + direct);
        }
        return null;
    }
}

@Data
@NoArgsConstructor
class Node {
    private int x; // x坐标
    private int y; // y坐标
    private int f; // F值
    private int g; // G值
    private int h; // H值
    private Boolean cango; // 是否可到达（是否为障碍物）
    private Node fatherNode; // 父节点

    public Node(int x, int y, boolean reachable) {
        super();
        this.x = x;
        this.y = y;
        this.f = 0;
        this.g = 0;
        this.h = 0;
        this.cango = reachable;
    }
}
