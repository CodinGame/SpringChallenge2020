package com.codingame.spring2020.pathfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;

import com.codingame.spring2020.CellType;
import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Grid;

/**
 * PATH : A*
 *
 */
public class AStar {
    Map<Coord, PathItem> closedList = new HashMap<>();
    PriorityQueue<PathItem> openList = new PriorityQueue<PathItem>(Comparator.comparingInt(PathItem::getTotalPrevisionalLength));
    List<PathItem> path = new ArrayList<PathItem>();

    Grid grid;
    Coord from;
    Coord target;
    Coord nearest;

    int dirOffset;
    private Function<Coord, Integer> weightFunction;

    public AStar(Grid grid, Coord from, Coord target, Function<Coord, Integer> weightFunction) {
        this.grid = grid;
        this.from = from;
        this.target = target;
        this.weightFunction = weightFunction;
        this.nearest = from;
    }

    public List<PathItem> find() {
        PathItem item = getPathItemLinkedList();
        path.clear();
        if (item != null) {
            calculatePath(item);
        }
        return path;
    }

    void calculatePath(PathItem item) {
        PathItem i = item;
        while (i != null) {
            path.add(0, i);
            i = i.precedent;
        }
    }

    PathItem getPathItemLinkedList() {
        PathItem root = new PathItem();
        root.coord = this.from;
        openList.add(root);

        while (openList.size() > 0) {
            PathItem visiting = openList.remove(); // imagine it's the best
            Coord visitingCoord = visiting.coord;

            if (visitingCoord.equals(target)) {
                return visiting;
            }
            if (closedList.containsKey(visitingCoord)) {
                continue;
            }
            closedList.put(visitingCoord, visiting);

            List<Coord> neighbors = grid.getNeighbours(visitingCoord);
            for (Coord neighbor : neighbors) {
                if (grid.get(neighbor).getType() == CellType.FLOOR) {
                    addToOpenList(visiting, visitingCoord, neighbor);
                }
            }

            if (grid.calculateDistance(visitingCoord, target) < grid.calculateDistance(nearest, target)) {
                this.nearest = visitingCoord;
            }
        }
        return null; // not found !
    }

    void addToOpenList(PathItem visiting, Coord fromCoord, Coord toCoord) {
        if (closedList.containsKey(toCoord)) {
            return;
        }
        PathItem pi = new PathItem();
        pi.coord = toCoord;
        pi.cumulativeLength = visiting.cumulativeLength + weightFunction.apply(toCoord);
        int manh = grid.calculateDistance(fromCoord, toCoord);
        pi.totalPrevisionalLength = pi.cumulativeLength + manh;
        pi.precedent = visiting;
        openList.add(pi);
    }

    public Coord getNearest() {
        return nearest;
    }

}
/** End of PATH */