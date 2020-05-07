package com.codingame.utils;

public class Padding {
    public int left, right, top, bottom;

    public Padding() {
    }

    public Padding setLeft(int left) {
        this.left = left;
        return this;
    }

    public Padding setRight(int right) {
        this.right = right;
        return this;
    }

    public Padding setTop(int top) {
        this.top = top;
        return this;
    }

    public Padding setBottom(int bottom) {
        this.bottom = bottom;
        return this;
    }

}
