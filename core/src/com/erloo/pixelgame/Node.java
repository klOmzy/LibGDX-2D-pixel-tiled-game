package com.erloo.pixelgame;

public class Node {
    public int x;
    public int y;
    public float g; // cost from start to this node
    public float h; // heuristic cost from this node to goal
    public float f; // total cost
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

