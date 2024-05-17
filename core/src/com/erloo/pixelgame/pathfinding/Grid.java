package com.erloo.pixelgame.pathfinding;

public class Grid {
    public Node[] nodes;
    public int width, height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        nodes = new Node[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                nodes[i + j * width] = new Node(i, j);
            }
        }
    }
    public void printGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Node node = nodes[x + y * width];
                System.out.print(node.walkable ? "." : "#");
            }
            System.out.println();
        }
    }
    public Node getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x + y * width];
        } else {
            return null;
        }
    }

    public boolean isValid(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return true;
    }
}

