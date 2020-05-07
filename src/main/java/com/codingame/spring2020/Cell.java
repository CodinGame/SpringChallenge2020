package com.codingame.spring2020;

public class Cell {
    public static final Cell NO_CELL = new Cell() {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void copy(Cell other) {
            throw new RuntimeException("Invalid cell");
        }
        
        @Override
        public void setType(CellType type) {
            throw new RuntimeException("Invalid cell");
        }

    };

    private CellType type;

    private Ability powerUp;
    private boolean hasPellet;
    private boolean hasCherry;

    public Cell() {

    }

    public Cell(CellType type) {
        this.setType(type);

    }

    public boolean isValid() {
        return true;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public Ability getPowerUp() {
        return powerUp;
    }

    public void setPowerUp(Ability powerUp) {
        this.powerUp = powerUp;
    }

    public boolean isFloor() {
        return type == CellType.FLOOR;
    }

    public boolean isWall() {
        return type == CellType.WALL;
    }

    public boolean hasPellet() {
        return hasPellet;
    }

    public void setHasPellet(boolean hasPellet) {
        this.hasPellet = hasPellet;
    }

    public boolean hasCherry() {
        return hasCherry;
    }

    public void setHasCherry(boolean hasCherry) {
        this.hasCherry = hasCherry;
    }

    public void copy(Cell source) {
        setType(source.type);
        setHasPellet(source.hasPellet);
    }

}
