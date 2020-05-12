package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.spring2020.Pacman;

public class Player extends AbstractMultiplayerPlayer {

    private List<Pacman> pacmen = new ArrayList<>();
    public int pellets = 0;
    private boolean timeout;

    public Player() {
        pacmen = new ArrayList<>();
    }

    public void addPacman(Pacman pacman) {
        pacmen.add(pacman);

    }

    public List<Pacman> getPacmen() {
        return pacmen;
    }

    public Stream<Pacman> getAlivePacmen() {
        return pacmen.stream().filter(pac -> !pac.isDead());
    }

    public Stream<Pacman> getDeadPacmen() {
        return pacmen.stream().filter(pac -> pac.isDead());
    }

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public void turnReset() {
        pacmen.forEach(a -> a.turnReset());
    }

    public boolean isTimedOut() {
        return timeout;
    }

    public void setTimedOut(boolean timeout) {
        this.timeout = timeout;
    }

    public String getColor() {
        return this.index == 0 ? "red" : "blue";
    }

}
