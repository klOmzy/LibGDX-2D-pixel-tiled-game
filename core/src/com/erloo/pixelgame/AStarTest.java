package com.erloo.pixelgame;

import java.util.List;

public class AStarTest {
    public static void main(String[] args) {
        // Create a simple 5x5 grid with a single obstacle in the middle
        boolean[][] grid = new boolean[][]{
                {true, true, true, true, true},
                {true, true, true, true, true},
                {true, true, false, true, true},
                {true, true, true, true, true},
                {true, true, true, true, true}
        };

        // Create the A* object
        AStar aStar = new AStar();

        // Create two Node objects representing the start and end points
        Node start = new Node(0, 0);
        Node end = new Node(4, 4);

        // Call the findPath method
        List<Node> path = aStar.findPath(start, end, grid);

        // Print the path to the console for debugging
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            System.out.println("Node " + i + ": (" + node.x + ", " + node.y + ")");
        }
    }
}
