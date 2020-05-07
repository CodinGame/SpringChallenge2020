package com.codingame.spring2020;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementResolution {
    List<Pacman> pacmenToKill = new ArrayList<>();
    List<Pacman> movedPacmen = new ArrayList<>();
    List<Pacman> blockedPacmen = new ArrayList<>();
    Map<Pacman, Pacman> blockedBy = new HashMap<>();

    public void addMovedPacman(Pacman pac) {
        movedPacmen.add(pac);

    }

    public List<Pacman> getMovedPacmen() {
        return movedPacmen;
    }

    public List<Pacman> getBlockedPacmen() {
        return blockedPacmen;
    }

    public void addBlockedPacmen(Pacman pac) {
        blockedPacmen.add(pac);

    }

    public Pacman getBlockerOf(Pacman pac) {
        return blockedBy.get(pac);
    }

}
