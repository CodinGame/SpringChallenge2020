package com.codingame.view;

import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Pacman;

public class Speed {
    Pacman pacModel;
    Coord from;
    Coord to;
    
    public Speed(Pacman pacModel, Coord from, Coord to) {
        super();
        this.pacModel = pacModel;
        this.from = from;
        this.to = to;
    }
}
