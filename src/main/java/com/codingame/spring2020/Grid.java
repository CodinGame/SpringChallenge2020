package com.codingame.spring2020;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Grid {
    int width, height;
    Map<Coord, Cell> cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new LinkedHashMap<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Coord coord = new Coord(x, y);
                Cell cell = new Cell();
                cells.put(coord, cell);
            }
        }
    }

    public Grid(String[] rows) {
        this.width = rows[0].length();
        this.height = rows.length;
        cells = new LinkedHashMap<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Coord coord = new Coord(x, y);
                char cellChar = rows[y].charAt(x);
                CellType type = getCellTypeFromChar(cellChar);
                Cell cell = new Cell(type);
                cell.setHasPellet(cellHasPellet(cellChar));
                cells.put(coord, cell);
            }
        }
    }

    private CellType getCellTypeFromChar(char cellChar) {
        switch (cellChar) {
        case '#':
        case 'x':
            return CellType.WALL;
        case ' ':
        case '.':
        case 'o':
            return CellType.FLOOR;
        default:
            throw new RuntimeException("Unrecognised cell type: " + cellChar);
        }
    }

    private boolean cellHasPellet(char cellChar) {
        switch (cellChar) {
        case '#':
        case 'x':
        case ' ':
            return false;
        case '.':
        case 'o':
            return true;
        default:
            throw new RuntimeException("Unrecognised cell type: " + cellChar);
        }
    }

    public Cell get(Coord coord) {
        return get(coord.x, coord.y);
    }

    public Cell get(int x, int y) {
        return cells.getOrDefault(new Coord(x, y), Cell.NO_CELL);
    }

    public List<Coord> getNeighbours(Coord pos) {
        return Arrays
            .stream(Config.ADJACENCY)
            .map(delta -> getCoordNeighbour(pos, delta))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public Optional<Coord> getCoordNeighbour(Coord pos, Coord delta) {
        Coord n = pos.add(delta);
        if (Config.MAP_WRAPS) {
            n = new Coord((n.x + width) % width, n.y);    
        }

        if (get(n) != Cell.NO_CELL) {
            return Optional.of(n);
        }
        return Optional.empty();
    }
    
    public int calculateDistance(Coord a, Coord b) {
        int dv = Math.abs(a.y - b.y);
        int dh = Math.min(
            Math.abs(a.x - b.x),
            Math.min(a.x + width - b.x, b.x + width - a.x)
        );
        return dv + dh;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Coord, Cell> getCells() {
        return cells;
    }

    public List<Coord> getAllPellets() {
        return cells.entrySet().stream().filter(e -> e.getValue().hasPellet()).map(e -> e.getKey()).collect(Collectors.toList());
    }

    public List<Coord> getAllCherries() {
        return cells.entrySet().stream().filter(e -> e.getValue().hasCherry()).map(e -> e.getKey()).collect(Collectors.toList());
    }
}
