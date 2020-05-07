package com.codingame.spring2020;

import java.util.Objects;

public class BumpCouple {

    private Coord fromA;
    private Coord fromB;
    private Coord to;
    private int distance;
    public BumpCouple(Coord from, Coord fromBlocker, Coord to, int distance) {
        this.fromA = from;
        this.fromB = fromBlocker;
        this.to = to;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;

        if (object != null && object instanceof BumpCouple) {
            BumpCouple other = (BumpCouple) object;
            isEqual = Objects.equals(this.fromA, other.fromA) && Objects.equals(this.fromB, other.fromB);
            isEqual |= Objects.equals(this.fromA, other.fromB) && Objects.equals(this.fromB, other.fromA);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.fromA.hashCode() + this.fromB.hashCode();
    }

    public Coord getFrom() {
        return fromA;
    }

    public Coord getFromBlocker() {
        return fromB;
    }

    public Coord getTo() {
        return to;
    }

    public int getDistance() {
        return distance;
    }
}
