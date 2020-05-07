package com.codingame.view.tooltip;

public class TooltipGlobalData {
    public int width, height, x, y, cellSize;
    private double coefficient;

    public TooltipGlobalData(int width, int height, int x, int y, int cellSize, double coefficient) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.cellSize = cellSize;
        this.coefficient = coefficient;
    }

}
