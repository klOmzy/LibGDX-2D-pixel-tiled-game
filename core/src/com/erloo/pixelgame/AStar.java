package com.erloo.pixelgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.erloo.pixelgame.Node;

public class AStar {

    public List<Node> findPath(Node start, Node goal, boolean[][] grid) {
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = getLowestFNode(openList);
            if (current.x == goal.x && current.y == goal.y) {
                return buildPath(current);
            }

            openList.remove(current);
            closedList.add(current);

            for (Node neighbor : getNeighbors(current, grid)) {
                if (closedList.contains(neighbor)) {
                    continue;
                }

                float tentativeG = current.g + 1; // assume all movements have a cost of 1
                if (!openList.contains(neighbor) || tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null; // no path found
    }

    private List<Node> getNeighbors(Node node, boolean[][] grid) {
        List<Node> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                int newX = node.x + x;
                int newY = node.y + y;
                if (newX >= 0 && newX < grid.length && newY >= 0 && newY < grid[0].length && grid[newX][newY]) {
                    neighbors.add(new Node(newX, newY));
                }
            }
        }
        return neighbors;
    }

    private float heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan distance
    }

    private Node getLowestFNode(List<Node> list) {
        Node lowest = list.get(0);
        for (Node node : list) {
            if (node.f < lowest.f) {
                lowest = node;
            }
        }
        return lowest;
    }

    private List<Node> buildPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }
}

