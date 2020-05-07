package com.codingame.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.codingame.gameengine.module.entities.ContainerBasedEntity;
import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.spring2020.Cell;
import com.codingame.spring2020.CellType;
import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Grid;
import com.google.inject.Inject;

public class GridView {
    @Inject private GraphicEntityModule gem;
    @Inject private View view;

    ContainerBasedEntity<?> group;
    ContainerBasedEntity<?> groupTunnel;
    Map<Coord, Entity<?>> cells;

    int width;
    int height;

    public void init(Grid gridModel, int cellSize) {
        group = gem.createBufferedGroup();
        groupTunnel = gem.createGroup();
        cells = new HashMap<>();

        width = gridModel.getWidth();
        height = gridModel.getHeight();

        for (Entry<Coord, Cell> entry : gridModel.getCells().entrySet()) {
            Coord coord = entry.getKey();
            Cell cell = entry.getValue();
            int padding = 0;

            if (cell.getType() == CellType.WALL) {
                int neighbourCode = getNeighbourCode(coord, gridModel);
                if (neighbourCode != 255) {
                    Sprite wall = gem.createSprite()
                        .setBaseWidth(cellSize - padding)
                        .setBaseHeight(cellSize - padding)
                        .setImage(String.valueOf(neighbourCode));
                    view.setToGridCoordinates(wall, coord);
                    group.add(wall);
                    cells.put(coord, wall);
                }
            } else if (cell.getType() == CellType.FLOOR) {
                Sprite floor = gem.createSprite()
                    .setBaseWidth(cellSize - padding)
                    .setBaseHeight(cellSize - padding)
                    .setImage("floor");
                if (isTunnel(coord)) {
                    double emptyBorderSize = 100d;
                    double tileSize = 250d;
                    double cornerSize = cellSize * emptyBorderSize / tileSize;
                    Sprite tunnel = view.createTunnelMask(coord, cellSize, padding, cornerSize);
                    groupTunnel.add(tunnel);
                    tunnel
                        .setX((int) (coord.getX() * cellSize + cellSize / 2))
                        .setY((int) (coord.getY() * cellSize - cornerSize));
                }
                view.setToGridCoordinates(floor, coord);
                group.add(floor);
                cells.put(coord, floor);
            }
        }
    }

    private boolean isTunnel(Coord coord) {
        return coord.getX() == 0 || coord.getX() == (width - 1);
    }

    private int getNeighbourCode(Coord coord, Grid gridModel) {
        int total = 0;
        for (Entry<Coord, TileMapping> entry : View.NEIGHBOUR_MAP.entrySet()) {
            Coord delta = entry.getKey();
            TileMapping mapping = entry.getValue();

            Coord n = coord.add(delta);
            Cell cell = gridModel.get(n);
            if (cell.isWall() || !cell.isValid()) {
                total += mapping.value;
            }
        }
        for (Entry<Coord, TileMapping> entry : View.CORNER_MAP.entrySet()) {
            Coord delta = entry.getKey();
            TileMapping mapping = entry.getValue();

            if ((total & mapping.mask) == mapping.mask) {
                Coord n = coord.add(delta);
                Cell cell = gridModel.get(n);
                if (cell.isWall() || !cell.isValid()) {
                    total += mapping.value;
                }
            }

        }
        return total;
    }
}
