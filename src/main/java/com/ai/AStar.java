package com.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** A deterministic four-direction A* path finder for the tile map. */
public class AStar {

    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private final int[][] map;
    private final int startRow;
    private final int startColumn;
    private final int targetRow;
    private final int targetColumn;

    public AStar(int[][] map, int startRow, int startColumn, int targetRow, int targetColumn) {
        if (map == null || map.length == 0 || map[0].length == 0) {
            throw new IllegalArgumentException("Map must not be empty");
        }
        this.map = map;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
    }

    /**
     * Returns the path in reverse order (target to the tile next to the start),
     * preserving the contract used by the tank movement code.
     */
    public List<Point> search() {
        if (!inBounds(startRow, startColumn) || !inBounds(targetRow, targetColumn)) {
            return List.of();
        }
        Point destination = findReachableDestination();
        if (destination == null || destination.x == startRow && destination.y == startColumn) {
            return List.of();
        }

        int rows = map.length;
        int columns = map[0].length;
        int[][] distance = new int[rows][columns];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        boolean[][] closed = new boolean[rows][columns];
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator
                .comparingInt(Node::estimatedTotal)
                .thenComparingInt(Node::heuristic));

        Node start = new Node(startRow, startColumn, 0,
                heuristic(startRow, startColumn, destination), null);
        distance[startRow][startColumn] = 0;
        open.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (closed[current.row][current.column]) {
                continue;
            }
            if (current.row == destination.x && current.column == destination.y) {
                return buildReversePath(current);
            }
            closed[current.row][current.column] = true;

            for (int[] direction : DIRECTIONS) {
                int nextRow = current.row + direction[0];
                int nextColumn = current.column + direction[1];
                if (!isWalkable(nextRow, nextColumn) || closed[nextRow][nextColumn]) {
                    continue;
                }
                int nextDistance = current.distance + 1;
                if (nextDistance >= distance[nextRow][nextColumn]) {
                    continue;
                }
                Node next = new Node(nextRow, nextColumn, nextDistance,
                        heuristic(nextRow, nextColumn, destination), current);
                distance[nextRow][nextColumn] = nextDistance;
                open.add(next);
            }
        }
        return List.of();
    }

    private Point findReachableDestination() {
        if (isWalkable(targetRow, targetColumn)) {
            return new Point(targetRow, targetColumn);
        }
        Point best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                int row = targetRow + rowOffset;
                int column = targetColumn + columnOffset;
                if (!isWalkable(row, column)) {
                    continue;
                }
                int distanceToStart = Math.abs(row - startRow) + Math.abs(column - startColumn);
                if (distanceToStart < bestDistance) {
                    bestDistance = distanceToStart;
                    best = new Point(row, column);
                }
            }
        }
        return best;
    }

    private List<Point> buildReversePath(Node destination) {
        List<Point> path = new ArrayList<>();
        for (Node node = destination; node.parent != null; node = node.parent) {
            path.add(new Point(node.row, node.column));
        }
        return path;
    }

    private int heuristic(int row, int column, Point destination) {
        return Math.abs(row - destination.x) + Math.abs(column - destination.y);
    }

    private boolean isWalkable(int row, int column) {
        return inBounds(row, column) && (map[row][column] == 0 || row == startRow && column == startColumn);
    }

    private boolean inBounds(int row, int column) {
        return row >= 0 && row < map.length && column >= 0 && column < map[0].length;
    }

    private record Node(int row, int column, int distance, int heuristic, Node parent) {
        int estimatedTotal() {
            return distance + heuristic;
        }
    }
}
