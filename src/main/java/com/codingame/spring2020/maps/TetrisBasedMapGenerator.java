package com.codingame.spring2020.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codingame.spring2020.Cell;
import com.codingame.spring2020.CellType;
import com.codingame.spring2020.Config;
import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Grid;

public class TetrisBasedMapGenerator {

    class TetrisPiece {
        Set<Coord> blocks;
        int maxX, maxY;

        public TetrisPiece(Set<Coord> cells) {
            this.blocks = cells;
            maxX = cells.stream().mapToInt(Coord::getX).max().getAsInt();
            maxY = cells.stream().mapToInt(Coord::getY).max().getAsInt();
        }
    }

    List<TetrisPiece> pieces;

    public void init() {
        pieces = new ArrayList<>();

        Set<Coord> cells;
        TetrisPiece t;

        // ##
        // ##
        cells = new HashSet<>();
        cells.add(new Coord(0, 0));
        cells.add(new Coord(1, 0));
        cells.add(new Coord(0, 1));
        cells.add(new Coord(1, 1));
        pieces.add(new TetrisPiece(cells));

        // #
        // ##
        cells = new HashSet<>();
        cells.add(new Coord(0, 0));
        cells.add(new Coord(0, 1));
        cells.add(new Coord(1, 1));
        t = new TetrisPiece(cells);
        pieces.add(t);
        pieces.add(flipX(t));
        pieces.add(flipY(t));
        pieces.add(transpose(t));

        // #
        // ##
        // #
        cells = new HashSet<>();
        cells.add(new Coord(0, 0));
        cells.add(new Coord(0, 1));
        cells.add(new Coord(1, 1));
        cells.add(new Coord(0, 2));
        t = new TetrisPiece(cells);
        pieces.add(t);
        pieces.add(flipX(t));
        pieces.add(transpose(t));
        pieces.add(flipY(transpose(t)));

        // #
        //###
        // #
        cells = new HashSet<>();
        cells.add(new Coord(1, 0));
        cells.add(new Coord(1, 1));
        cells.add(new Coord(2, 1));
        cells.add(new Coord(1, 2));
        cells.add(new Coord(0, 1));
        t = new TetrisPiece(cells);
        pieces.add(t);

        // #
        // ###
        cells = new HashSet<>();
        cells.add(new Coord(0, 0));
        cells.add(new Coord(0, 1));
        cells.add(new Coord(1, 1));
        cells.add(new Coord(2, 1));
        t = new TetrisPiece(cells);
        pieces.add(t);
        pieces.add(flipX(t));
        pieces.add(flipY(t));
        pieces.add(flipX(flipY(t)));
        pieces.add(flipX(flipY(transpose(t))));
        pieces.add(transpose(t));
        pieces.add(flipY(transpose(t)));
        pieces.add(flipX(transpose(t)));

    }

    private TetrisPiece flip(TetrisPiece t, Function<Coord, Coord> func) {
        Set<Coord> result = new HashSet<>();
        for (Coord coord : t.blocks) {
            result.add(func.apply(coord));
        }
        return new TetrisPiece(result);
    }

    private TetrisPiece flipX(TetrisPiece t) {
        return flip(t, coord -> new Coord(t.maxX - coord.getX(), coord.getY()));
    }

    private TetrisPiece flipY(TetrisPiece t) {
        return flip(t, coord -> new Coord(coord.getX(), t.maxY - coord.getY()));
    }

    private TetrisPiece transpose(TetrisPiece t) {
        return flip(t, coord -> new Coord(coord.getY(), coord.getX()));
    }

    public void generateWithHorizontalSymetry(Grid grid, Random random) {
        int w = grid.getWidth() / 2 + 1;
        int h = grid.getHeight();
        Grid miniGrid = new Grid(w, h);
        generate(miniGrid, random);

        for (Map.Entry<Coord, Cell> entry : miniGrid.getCells().entrySet()) {
            Coord sourceCoord = entry.getKey();
            Cell sourceCell = entry.getValue();
            //left
            grid.get(sourceCoord).copy(sourceCell);

            //right
            Coord pos = new Coord(grid.getWidth() - sourceCoord.getX() - 1, sourceCoord.getY());
            grid.get(pos).copy(sourceCell);
        }
        
        List<Coord> generatedFloors = grid.getCells()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().isFloor())
            .map(e -> e.getKey())
            .collect(Collectors.toList());
        
        LinkedList<List<Coord>> islands = detectIslands(generatedFloors, grid);
        islands.stream()
            .sorted((a, b) -> b.size() - a.size())
            .skip(1)
            .forEach(
                list -> list.stream()
                    .forEach(coord -> {
                        Cell c = grid.get(coord);
                        c.setType(CellType.WALL);
                    })
            );

    }

    public void generateWithCentralSymetry(Grid grid, Random random) {
        int w = grid.getWidth() / 2 + 1;
        int h = grid.getHeight() / 2 + 1;
        Grid miniGrid = new Grid(w, h);
        generate(miniGrid, random);

        Coord pos;
        for (Map.Entry<Coord, Cell> entry : miniGrid.getCells().entrySet()) {
            Coord sourceCoord = entry.getKey();
            Cell sourceCell = entry.getValue();
            grid.get(sourceCoord).copy(sourceCell);
            pos = new Coord(grid.getWidth() - sourceCoord.getX() - 1, sourceCoord.getY());
            grid.get(pos).copy(sourceCell);
            pos = new Coord(sourceCoord.getX(), grid.getHeight() - sourceCoord.getY() - 1);
            grid.get(pos).copy(sourceCell);
            pos = new Coord(grid.getWidth() - sourceCoord.getX() - 1, grid.getHeight() - sourceCoord.getY() - 1);
            grid.get(pos).copy(sourceCell);

        }
    }

    public void generate(Grid grid, Random random) {
        int generateWidth = grid.getWidth() / 2 + 1;
        int generateHeight = grid.getHeight() / 2 + 1;

        Map<Coord, TetrisPiece> generatedPieces = new HashMap<>();
        Map<Coord, Coord> blockOrigin = new HashMap<>();
        Set<Coord> occupied = new HashSet<>();
        for (int y = 0; y < generateHeight; ++y) {
            for (int x = 0; x < generateWidth; ++x) {
                Coord pos = new Coord(x, y);
                if (!occupied.contains(pos)) {
                    Collections.shuffle(pieces, random);

                    // Take the first available piece, unless it is the only one.
                    Optional<TetrisPiece> pieceOpt = pieces.stream()
                        .filter(p -> pieceFits(p, occupied, pos))
                        .skip(1)
                        .findFirst();

                    if (pieceOpt.isPresent()) {
                        TetrisPiece piece = pieceOpt.get();
                        placePiece(generatedPieces, blockOrigin, occupied, pos, piece);
                    }
                }
            }
        }

        grid.getCells().values().stream().forEach(c -> c.setType(CellType.WALL));

        List<Coord> generatedFloors = new ArrayList<>();
        for (int y = 1; y < generateHeight; ++y) {
            for (int x = 1; x < generateWidth; ++x) {
                Coord pos = new Coord(x, y);
                Coord origin = blockOrigin.get(pos);

                Coord gridPos = new Coord(x * 2 - 1, y * 2 - 1);
                if (origin != null) {
                    TetrisPiece piece = generatedPieces.get(origin);
                    // Which piece of the tetris block are we considering?
                    Coord block = new Coord(pos.getX() - origin.getX(), pos.getY() - origin.getY());
                    for (Coord delta : Config.ADJACENCY) {
                        Coord adj = new Coord(block.getX() + delta.getX(), block.getY() + delta.getY());
                        if (!piece.blocks.contains(adj)) {

                            for (int i = 0; i < 3; ++i) {
                                Coord cellPos;
                                if (delta.getX() == 0) {
                                    cellPos = new Coord(gridPos.getX() - 1 + i, gridPos.getY() + delta.getY());
                                } else {
                                    cellPos = new Coord(gridPos.getX() + delta.getX(), gridPos.getY() - 1 + i);
                                }
                                Cell cell = grid.get(cellPos);
                                if (cell.isValid()) {
                                    cell.setType(CellType.FLOOR);
                                    generatedFloors.add(cellPos);
                                }
                            }
                        }
                    }
                }
            }
        }

        
    }

    private LinkedList<List<Coord>> detectIslands(List<Coord> generatedFloors, Grid grid) {
        LinkedList<List<Coord>> islands = new LinkedList<>();
        Queue<Coord> fifo = new LinkedList<>();
        Set<Coord> computed = new HashSet<>();
        List<Coord> island;

        for (Coord first : generatedFloors) {
            if (computed.contains(first))
                continue;

            fifo.add(first);
            island = new LinkedList<>();
            computed.add(first);
            island.add(first);

            while (!fifo.isEmpty()) {
                Coord e = fifo.poll();
                for (Coord n : grid.getNeighbours(e)) {
                    if (!computed.contains(n) && grid.get(n).isFloor()) {
                        fifo.add(n);
                        computed.add(n);
                        island.add(n);
                    }
                }
            }
            islands.add(island);
        }
        islands.sort((a, b) -> {
            return b.size() - a.size();
        });
        return islands;
    }

    private void placePiece(
        Map<Coord, TetrisPiece> generatedPieces, Map<Coord, Coord> blockOrigin, Set<Coord> occupied, Coord pos, TetrisPiece tetrisPiece
    ) {
        generatedPieces.put(pos, tetrisPiece);
        for (Coord coord : tetrisPiece.blocks) {
            Coord d = new Coord(pos.getX() + coord.getX(), pos.getY() + coord.getY());
            blockOrigin.put(d, pos);
            occupied.add(d);
        }
    }

    private void output(Set<Coord> occupied, int generateWidth, int generateHeight) {
        for (int y = 0; y < generateHeight; ++y) {
            for (int x = 0; x < generateWidth; ++x) {
                if (occupied.contains(new Coord(x, y))) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();

    }

    private boolean pieceFits(TetrisPiece piece, Set<Coord> occupied, Coord pos) {
        for (Coord coord : piece.blocks) {
            Coord d = new Coord(pos.getX() + coord.getX(), pos.getY() + coord.getY());
            if (occupied.contains(d)) {
                return false;
            }
        }
        return true;
    }

    private void output(TetrisPiece piece) {
        for (int y = 0; y <= piece.maxY; ++y) {
            for (int x = 0; x <= piece.maxX; ++x) {
                if (piece.blocks.contains(new Coord(x, y))) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();

    }

}
