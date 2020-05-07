package com.codingame.spring2020.pathfinder;

import com.codingame.spring2020.Coord;

public class PathItem {
    public int cumulativeLength = 0;
    int totalPrevisionalLength = 0;
    PathItem precedent = null;
    Coord coord;

    public int getTotalPrevisionalLength() {
        return totalPrevisionalLength;
    }
}