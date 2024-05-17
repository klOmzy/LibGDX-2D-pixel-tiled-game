package com.erloo.pixelgame.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Pathfinder {
    public List<Node> findPath(Node startNode, Node endNode, Grid grid) {
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = getLowestFCostNode(openList);

            if (currentNode == endNode) {
                return getPath(startNode, endNode);
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            for (Node neighbor : getNeighbors(currentNode, grid)) {
                if (closedList.contains(neighbor)) {
                    continue;
                }

                float tentativeGCost = currentNode.gCost + 1;

                if (!openList.contains(neighbor) || tentativeGCost < neighbor.gCost) {
                    neighbor.parent = currentNode;
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = heuristic(neighbor, endNode);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private List<Node> getNeighbors(Node node, Grid grid) {
        List<Node> neighbors = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                int neighborX = node.x + x;
                int neighborY = node.y + y;

                if (grid.isValid(neighborX, neighborY) && grid.nodes[neighborX + neighborY * grid.width].walkable) {
                    neighbors.add(grid.nodes[neighborX + neighborY * grid.width]);
                }
            }
        }

        return neighbors;
    }

    private Node getLowestFCostNode(List<Node> nodes) {
        return nodes.stream().min(Comparator.comparing(n -> n.fCost)).get();
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private List<Node> getPath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }
}

