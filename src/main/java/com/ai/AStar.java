package com.ai;

import com.game.Game;
import com.history.core.util.EmptyUtil;

import java.awt.Point;
import java.util.ArrayList;
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
        return (Math.abs(currentNode.x - endNode.x) + Math.abs(currentNode.y - endNode.y));
    }

    // 获取G值 currentNode：当前节点 return
    private int getG(Node currentNode) {
        Node fatherNode = currentNode.fatherNode;
        if (fatherNode != null && (currentNode.x == fatherNode.x || currentNode.y == fatherNode.y)) {
            // 判断当前节点与其父节点之间的位置关系（水平？对角线）
            return currentNode.g + 1;
        }
        return currentNode.g;
    }

    // 获取F值 ： G + H currentNode return
    private int getF(Node currentNode) {
        return currentNode.g + currentNode.h;
    }

    // 将选中节点周围的节点添加进“开启列表” node
    private void inOpen(Node node) {

        int x = node.x;
        int y = node.y;
        int[] directList = {0, 1, 2, 3};
        int direct = Game.getAinBdirection(new Point(node.y, node.x), new Point(endNode.y, endNode.x));

        int xValue = Math.abs(endNode.y - node.y);
        int yValue = Math.abs(endNode.x - node.x);
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
            Node node2 = getDirectNode(node, directList[i]);
            if (node2 != null && (node2.cango && !open.contains(node2))) {
                node2.fatherNode = map[x][y];
                // 将选中节点作为父节点
                node2.g = getG(node2);
                node2.h = getH(node2, endNode);
                node2.f = getF(node2);
                open.add(node2);
            }
        }
    }

    // 使用冒泡排序将开启列表中的节点按F值从小到大排序
    private void sort(List<Node> arr) {
        for (int i = 0; i < arr.size() - 1; i++) {
            for (int j = i + 1; j < arr.size(); j++) {
                if (arr.get(i).f > arr.get(j).f) {
                    Node tmp = arr.get(i);
                    arr.set(i, arr.get(j));
                    arr.set(j, tmp);
                }
            }
        }
    }

    // 将节点添加进“关闭列表”
    private void inClose(Node node, List<Node> open) {
        if (open.contains(node)) {
            node.cango = false;
            // 设置为不可达
            open.remove(node);
            close.add(node);
        }
    }

    public List<Point> search() {
        // 对起点即起点周围的节点进行操作
        if (endNode == null || !map[endNode.x][endNode.y].cango) {
            for (int i = 0; i < 8; i++) {
                Node newEnd = getDirectNode(endNode, i);
                if (newEnd == null || !map[newEnd.x][newEnd.y].cango) {
                    continue;
                }
                endNode = newEnd;
                return search();
            }
        }
        inOpen(map[startNode.x][startNode.y]);
        close.add(map[startNode.x][startNode.y]);
        map[startNode.x][startNode.y].cango = false;
        map[startNode.x][startNode.y].fatherNode = map[startNode.x][startNode.y];
        sort(open);
        // 重复步骤
        do {
            if (EmptyUtil.isNotEmpty(open)) {
                inOpen(open.get(0));
                inClose(open.get(0), open);
            }
        } while (EmptyUtil.isNotEmpty(open) && !open.contains(map[endNode.x][endNode.y]));
        // 知道开启列表中包含终点时，循环退出
        inClose(map[endNode.x][endNode.y], open);
        List<Point> path = null;
        if (EmptyUtil.isNotEmpty(close)) {
            path = new ArrayList<>();
            Node node = map[endNode.x][endNode.y];
            while (node != null && !(node.x == startNode.x && node.y == startNode.y)) {
                Point point = new Point(node.x, node.y);
                path.add(point);
                node = node.fatherNode;
            }
        }
        return path;
    }

    private Node getDirectNode(Node node, int direct) {
        switch (direct) {
            case 0 -> {
                if (node.x - 1 >= 0) {
                    return map[node.x - 1][node.y];
                }
            }
            case 1 -> {
                if (node.y + 1 < map[0].length) {
                    return map[node.x][node.y + 1];
                }
            }
            case 2 -> {
                if (node.x + 1 < map.length) {
                    return map[node.x + 1][node.y];
                }
            }
            case 3 -> {
                if (node.y - 1 >= 0) {
                    return map[node.x][node.y - 1];
                }
            }
            case 4 -> {
                if (node.x - 1 >= 0 && node.y - 1 >= 0) {
                    return map[node.x - 1][node.y - 1];
                }
            }
            case 5 -> {
                if (node.x - 1 >= 0 && node.y + 1 < map[0].length) {
                    return map[node.x - 1][node.y + 1];
                }
            }
            case 6 -> {
                if (node.x + 1 < map.length && node.y + 1 < map[0].length) {
                    return map[node.x + 1][node.y + 1];
                }
            }
            case 7 -> {
                if (node.x + 1 < map.length && node.y - 1 >= 0) {
                    return map[node.x + 1][node.y - 1];
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + direct);
        }
        return null;
    }
}

class Node {
    final int x; // x坐标
    final int y; // y坐标
    int f = 0; // F值
    int g = 0; // G值
    int h = 0; // H值
    boolean cango; // 是否可到达（是否为障碍物）
    Node fatherNode; // 父节点

    public Node(int x, int y, boolean reachable) {
        super();
        this.x = x;
        this.y = y;
        this.cango = reachable;
    }
}
