package com.erloo.pixelgame.pathfinding;

public class Node {
    public int x, y;
    public boolean walkable;
    public Node parent;
    public float gCost, hCost, fCost;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
