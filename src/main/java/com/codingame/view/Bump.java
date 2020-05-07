package com.codingame.view;

import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Pacman;

public class Bump {
    Pacman pacModel;
    Coord from;
    Coord to;
    int distanceToBlocker;

    public Bump(Pacman pacModel, Coord from, Coord to, int distanceToBlocker) {
        super();
        this.pacModel = pacModel;
        this.from = from;
        this.to = to;
        this.distanceToBlocker = distanceToBlocker;
    }

}
