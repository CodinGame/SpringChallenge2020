package com.codingame.view;

import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Pacman;

public class Move {
    Coord from, to;
    Pacman pacModel;

    public Move(Pacman pacModel, Coord from, Coord to) {
        super();
        this.from = from;
        this.to = to;
        this.pacModel = pacModel;
    }

}
