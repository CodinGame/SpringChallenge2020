package com.codingame.spring2020.pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Grid;

/*
 * Find the best path from a Coord to another Coord
 * currently : using Astar algorithm
 */
public class PathFinder {
  public static class PathFinderResult {
    public static final PathFinderResult NO_PATH = new PathFinderResult();
    public List<Coord> path = new ArrayList<>();
    public int weightedLength = -1;
    public boolean isNearest = false;

    public boolean hasNextCoord() {
      return path.size() > 1;
    }

    public Coord getNextCoord() {
      return path.get(1);
    }

    public boolean hasNoPath() {
      return weightedLength == -1;
    }
  }
  
  Grid grid = null;
  Coord from = null;
  Coord to = null;
  private Function<Coord, Integer> weightFunction = (Coord coord) -> (1);
  
  public PathFinder setGrid(Grid grid) {
      this.grid = grid;
      return this;
  }

  public PathFinder from(Coord Coord) {
    from = Coord;
    return this;
  }

  public PathFinder to(Coord Coord) {
    to = Coord;
    return this;
  }

  public PathFinder withWeightFunction(Function<Coord, Integer> weightFunction) {
    this.weightFunction = weightFunction;
    return this;
  }

  public PathFinderResult findPath() {
    if (from == null || to == null) {
      return new PathFinderResult();
    }

    AStar a = new AStar(grid, from, to, weightFunction);
    List<PathItem> pathItems = a.find();
    PathFinderResult pfr = new PathFinderResult();

    if (pathItems.isEmpty()) {
        pfr.isNearest = true;
        pathItems = new AStar(grid, from, a.getNearest(), weightFunction).find();
    }

    pfr.path = pathItems.stream()
        .map(item -> item.coord)
        .collect(Collectors.toList());
    pfr.weightedLength = pathItems.get(pathItems.size() - 1).cumulativeLength;
    return pfr;
  }
}
