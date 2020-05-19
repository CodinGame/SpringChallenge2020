
package com.codingame.spring2020;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.spring2020.Ability.Type;
import com.codingame.spring2020.action.ActionType;
import com.codingame.spring2020.action.MoveAction;
import com.codingame.spring2020.maps.TetrisBasedMapGenerator;
import com.codingame.spring2020.pathfinder.PathFinder;
import com.codingame.spring2020.pathfinder.PathFinder.PathFinderResult;
import com.codingame.view.View;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Game {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private View view;
    @Inject private PathFinder pathfinder;
    @Inject TetrisBasedMapGenerator mapGenerator;

    List<Pacman> pacmen;
    Random random;
    Grid grid;

    private int totalPacmen;
    private int pacmenPerPlayer;
    private int currentStep = 0;

    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private Grid generateGrid(long seed) {
        int width = randomInt(Config.MAP_MIN_WIDTH, Config.MAP_MAX_WIDTH);
        int height = randomInt(Config.MAP_MIN_HEIGHT, Config.MAP_MAX_HEIGHT);
        mapGenerator.init();

        if (gameManager.getPlayerCount() == 2) {
            if (width % 2 == 0) {
                width++;
            }
        } else if (gameManager.getPlayerCount() == 4) {
            if (height % 2 == 0) {
                height++;
            }
        }

        Grid grid = new Grid(width, height);

        if (gameManager.getPlayerCount() == 2) {
            mapGenerator.generateWithHorizontalSymetry(grid, random);
        } else if (gameManager.getPlayerCount() == 4) {
            mapGenerator.generateWithCentralSymetry(grid, random);
        } else {
            mapGenerator.generate(grid, random);
        }

        if (Config.MAP_WRAPS) {
            Grid bigGrid = new Grid(width + 2, height + 2);
            for (int y = 0; y < height + 2; ++y) {
                for (int x = 0; x < width + 2; ++x) {
                    Coord gridPos = new Coord(x - 1, y - 1);

                    if (isOuterBorder(x, y, width, height)) {
                        if (x == 0 && isTunnelExit(grid, gridPos.x + 1, gridPos.y)) {
                            bigGrid.get(x, y).setType(CellType.FLOOR);
                        } else if (x == width + 1 && isTunnelExit(grid, gridPos.x - 1, gridPos.y)) {
                            bigGrid.get(x, y).setType(CellType.FLOOR);
                        } else {
                            bigGrid.get(x, y).setType(CellType.WALL);
                        }

                    } else {
                        bigGrid.get(x, y).copy(grid.get(x - 1, y - 1));
                    }
                }

            }
            grid = bigGrid;
        }

        return grid;

    }

    private boolean isTunnelExit(Grid grid, int x, int y) {
        return grid.get(x, y).isFloor() && !grid.get(x, y - 1).isFloor() && !grid.get(x, y + 1).isFloor();
    }

    private boolean isOuterBorder(int x, int y, int width, int height) {
        return x == 0 || y == 0 || x == width + 1 || y == height + 1;
    }

    public void init(long seed) {
        String state = gameManager.getGameParameters().getProperty("state");
        if (state != null && System.getProperty("allow.config.override") != null) {
            initGameFromState(state);
        }

        random = new Random(seed);
        if (grid == null) {
            grid = generateGrid(seed);
        }

        if (pacmenPerPlayer == 0) {
            pacmenPerPlayer = randomInt(Config.MIN_PACS_PER_PLAYER, Config.MAX_PACS_PER_PLAYER);
        }
        totalPacmen = pacmenPerPlayer * gameManager.getPlayerCount();

        if (pacmen == null) {
            generatePacmen();

            List<Coord> freeCells = grid.cells.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getType() == CellType.FLOOR)
                .filter(entry -> entry.getKey().getX() != grid.getWidth() / 2)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

            Collections.shuffle(freeCells, random);

            if (gameManager.getPlayerCount() == 2) {
                List<Coord> leftCells = freeCells.stream()
                    .filter(c -> c.getX() <= grid.getWidth() / 2)
                    .collect(Collectors.toList());
                int i;
                for (i = 0; i < pacmenPerPlayer; ++i) {
                    Coord leftCell = leftCells.get(i);
                    Coord rightCell = new Coord(grid.getWidth() - 1 - leftCell.getX(), leftCell.getY());

                    int leftPlayer = random.nextInt(2);
                    int rightPlayer = (leftPlayer + 1) % 2;
                    gameManager.getPlayer(leftPlayer).getPacmen().get(i).setPosition(leftCell);
                    gameManager.getPlayer(rightPlayer).getPacmen().get(i).setPosition(rightCell);
                }
                for (int j = 0; j < Config.NUMBER_OF_CHERRIES / 2; j++) {
                    Coord leftCell = leftCells.get(i + j);
                    Coord rightCell = new Coord(grid.getWidth() - 1 - leftCell.getX(), leftCell.getY());
                    grid.get(leftCell).setHasCherry(true);
                    grid.get(rightCell).setHasCherry(true);
                }
            } else if (gameManager.getPlayerCount() == 4) {
                List<Coord> topLeftCells = freeCells.stream()
                    .filter(c -> c.getX() <= grid.getWidth() / 2)
                    .collect(Collectors.toList());

                for (int i = 0; i < pacmenPerPlayer; ++i) {
                    Coord topLeftCell = topLeftCells.get(i);
                    Coord topRightCell = new Coord(grid.getWidth() - 1 - topLeftCell.getX(), topLeftCell.getY());
                    Coord bottomLeftCell = new Coord(topLeftCell.getX(), grid.getHeight() - 1 - topLeftCell.getY());
                    Coord bottomRightCell = new Coord(grid.getWidth() - 1 - topLeftCell.getX(), grid.getHeight() - 1 - topLeftCell.getY());

                    gameManager.getPlayer(0).getPacmen().get(i).setPosition(topLeftCell);
                    gameManager.getPlayer(1).getPacmen().get(i).setPosition(topRightCell);
                    gameManager.getPlayer(2).getPacmen().get(i).setPosition(bottomLeftCell);
                    gameManager.getPlayer(3).getPacmen().get(i).setPosition(bottomRightCell);
                }
            } else {

                for (int i = 0; i < totalPacmen; ++i) {
                    Coord freeCell = freeCells.get(i);
                    pacmen.get(i).setPosition(freeCell);

                }
            }
        }

        // generate pellets
        grid.cells.entrySet().stream().forEach(entry -> {
            Coord coord = entry.getKey();
            Cell cell = entry.getValue();
            boolean spawnPellet = cell.isFloor() && !cell.hasCherry() &&
                pacmen.stream()
                    .filter(pac -> pac.getPosition().equals(coord))
                    .count() == 0;
            if (spawnPellet) {
                cell.setHasPellet(true);
            }
        });

        view.init(pacmen, grid, pacmenPerPlayer);

    }

    private void initGameFromState(String state) {
        String[] lines = state.split(";");
        int width = lines[0].length();
        int height = lines.length;

        Map<Integer, List<Coord>> spawns = new LinkedHashMap<>();
        spawns.put(0, new ArrayList<Coord>());
        spawns.put(1, new ArrayList<Coord>());
        Map<Coord, PacmanType> types = new HashMap<>();

        grid = new Grid(width, height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                char c = lines[y].charAt(x);
                Cell cell = grid.get(x, y);
                if (c == '#') {
                    cell.setType(CellType.WALL);
                } else {
                    cell.setType(CellType.FLOOR);
                }
                switch (c) {
                case '.':
                    cell.setHasPellet(true);
                    break;
                case 'o':
                    cell.setHasCherry(true);
                    break;
                case '1':
                case '0': {
                    int player = Integer.valueOf(String.valueOf(c));
                    spawns.computeIfAbsent(player, (i -> new ArrayList<>()));
                    spawns.get(player).add(new Coord(x, y));
                    totalPacmen++;
                    break;
                }
                case 'r':
                case 'p':
                case 's':
                case 'n': {
                    int player = 0;
                    saveSpawnPoint(spawns, types, new Coord(x, y), c, player);
                    break;
                }
                case 'R':
                case 'P':
                case 'S':
                case 'N': {
                    int player = 1;
                    saveSpawnPoint(spawns, types, new Coord(x, y), c, player);
                    break;
                }
                }
            }
        }

        pacmen = new ArrayList<>(totalPacmen);

        spawns.forEach((playerIdx, coords) -> {
            Player player = gameManager.getPlayer(playerIdx);
            for (Coord c : coords) {
                PacmanType type = types.getOrDefault(c, PacmanType.NEUTRAL);
                Pacman pacman = new Pacman(pacmen.size(), player.getPacmen().size(), player, type);
                pacman.setPosition(c);

                player.addPacman(pacman);
                pacmen.add(pacman);
            }

        });

    }

    private void saveSpawnPoint(Map<Integer, List<Coord>> spawns, Map<Coord, PacmanType> types, Coord pos, char c, int player) {
        spawns.computeIfAbsent(player, (i -> new ArrayList<>()));
        spawns.get(player).add(pos);
        totalPacmen++;
        types.put(pos, PacmanType.fromCharacter(c));
    }

    private void generatePacmen() {
        pacmen = new ArrayList<>(totalPacmen);
        int pacmanIndex = 0;
        int typeIndex = 0;
        while (pacmanIndex < totalPacmen) {
            for (Player player : gameManager.getPlayers()) {
                if (pacmanIndex < totalPacmen) {
                    PacmanType type = Config.SWITCH_ABILITY_AVAILABLE ? PacmanType.values()[typeIndex % 3] : PacmanType.NEUTRAL;
                    Pacman pac = new Pacman(pacmanIndex, player.getPacmen().size(), player, type);
                    player.addPacman(pac);
                    pacmanIndex++;
                    pacmen.add(pac);
                }
            }
            typeIndex++;
        }

    }

    public boolean canEat(Pacman pac1, Pacman pac2) {
        if (pac1.getOwner().equals(pac2.getOwner())) {
            return false;
        }
        if (pac1.getType() == PacmanType.PAPER) {
            return pac2.getType() == PacmanType.ROCK;
        }
        if (pac1.getType() == PacmanType.ROCK) {
            return pac2.getType() == PacmanType.SCISSORS;
        }
        if (pac1.getType() == PacmanType.SCISSORS) {
            return pac2.getType() == PacmanType.PAPER;
        }
        return false;
    }

    public List<String> getGlobalInfoFor(Player player) {
        List<String> lines = new ArrayList<String>();

        // add map info
        lines.add(String.format("%d %d", grid.width, grid.height));
        for (int y = 0; y < grid.getHeight(); ++y) {
            List<Cell> row = new ArrayList<>(grid.getWidth());
            for (int x = 0; x < grid.getWidth(); ++x) {
                row.add(grid.get(x, y));
            }
            lines.add(
                row.stream()
                    .map(cell -> cellToCharater(cell))
                    .collect(Collectors.joining())
            );
        }

        return lines;

    }

    private String getPacmanLineInfo(Player player, Pacman pac) {
        return String.format(
            "%d %d %d %d %s %d %d",
            pac.getNumber(),
            pac.getOwner() == player ? 1 : 0,
            pac.getPosition().x,
            pac.getPosition().y,
            pac.isDead() ? "DEAD" : pac.getType().name().toUpperCase(),
            pac.getAbilityDuration(),
            pac.getAbilityCooldown()
        );
    }

    private String getPelletLineInfo(Coord pellet, int value) {
        return String.format(
            "%d %d %d",
            pellet.x,
            pellet.y,
            value
        );
    }

    public List<String> getCurrentFrameInfoFor(Player player) {
        Player opponentPlayer = gameManager.getActivePlayers().get((player.getIndex() + 1) % 2);
        List<String> lines = new ArrayList<String>();

        lines.add(String.format("%d %d", player.pellets, opponentPlayer.pellets));

        List<Pacman> visiblePacmen = Config.FOG_OF_WAR ? findVisiblePacmen(player) : pacmen;

        if (Config.PROVIDE_DEAD_PACS) {
            Stream.concat(player.getDeadPacmen(), opponentPlayer.getDeadPacmen())
                .forEach(visiblePacmen::add);
        }

        lines.add(Integer.toString(visiblePacmen.size()));
        visiblePacmen
            .stream()
            .sorted(Comparator.comparing(Pacman::getId))
            .map(pac -> getPacmanLineInfo(player, pac))
            .forEach(lines::add);

        List<Coord> visiblePellets = Config.FOG_OF_WAR ? findVisiblePellets(player) : grid.getAllPellets();
        List<Coord> visibleCherries = grid.getAllCherries();

        lines.add(Integer.toString(visiblePellets.size() + visibleCherries.size()));

        for (Coord pellet : visiblePellets) {
            lines.add(getPelletLineInfo(pellet, 1));
        }

        for (Coord cherry : visibleCherries) {
            lines.add(getPelletLineInfo(cherry, Config.CHERRY_SCORE));
        }

        return lines;
    }

    public String cellToCharater(Cell cell) {
        if (cell.isWall()) {
            return "#";
        }

        return " ";
    }

    public void resetGameTurnData() {
        currentStep = 0;
        gameManager.getPlayers().stream().forEach(Player::turnReset);
    }

    public void performGameUpdate() {
        view.startOfTurn(Collections.emptyList());

        executePacmenAbilities();
        updateAbilityModifiers();
        processPacmenIntent();
        resolveMovement();

        // update view
        view.endOfTurn();

        printPacmenGameSummary();
    }

    public void performGameSpeedUpdate() {
        List<Player> speeders = pacmen.stream()
            .filter(p -> p.getIntendedPath().size() > 2)
            .map(Pacman::getOwner)
            .distinct()
            .collect(Collectors.toList());

        view.startOfTurn(speeders);

        gameManager.addToGameSummary("Only pacs with the SPEED ability enabled can move:");

        for (Pacman pac : pacmen) {
            if (pac.getSpeed() <= 1 || pac.getIntent().getActionType() != ActionType.MOVE) {
                List<Coord> path = new ArrayList<>();
                path.add(pac.getPosition());
                pac.setIntendedPath(path);
            }
            pac.setBlocked(false);
        }
        resolveMovement();

        // update view
        view.endOfTurn();

        printPacmenGameSummary();
    }

    private void resolveMovement() {
        Set<Pacman> pacmenToKill = new HashSet<>();

        MovementResolution resolution = resolvePacmenMovement();

        // display warning messages for unreachable targets.
        for (Pacman pac : pacmen) {
            if (pac.getWarningPathMessage() != null) {
                String summary = String.format(
                    "Pac %d: %s",
                    pac.getNumber(),
                    pac.getWarningPathMessage()
                );
                pac.addToGameSummary(summary);

                pac.setWarningPathMessage(null);
            }
        }

        // find pacmen to kill
        for (Pacman pac : pacmen) {
            otherPacmen(pac).forEach(other -> {
                if (canEat(pac, other) && pacmenHaveCollided(pac, other)) {
                    boolean added = pacmenToKill.add(other);
                    if (added) {
                        view.killPacman(other);
                    }
                }
            });
        }

        Map<Double, Set<Pacman>> pacmenToFlash = new HashMap<>();
        Set<BumpCouple> bumpToDisplay = new HashSet<BumpCouple>();

        for (Pacman pac : resolution.getBlockedPacmen()) {
            Pacman blocker = resolution.getBlockerOf(pac);
            Coord from = getIntendedPositionAtStep(pac, pac.getCurrentPathStep());
            Coord blockerFrom = getIntendedPositionAtStep(blocker, pac.getCurrentPathStep());
            Coord to = getIntendedPositionAtStep(pac, pac.getCurrentPathStep() + 1);
            int distanceToBlocker = grid.calculateDistance(pac.getPosition(), blocker.getPosition());

            if (pacmenToKill.contains(pac) && !resolution.getBlockedPacmen().contains(blocker)) {
                continue;
            }
            view.bumpPacman(pac, from, to, distanceToBlocker);

            BumpCouple bumpCouple = new BumpCouple(from, blockerFrom, to, distanceToBlocker);
            bumpToDisplay.add(bumpCouple);

            double flashTime = 0.5;
            Set<Pacman> flashingInUnison = pacmenToFlash.computeIfAbsent(flashTime, k -> new HashSet<Pacman>());
            flashingInUnison.add(pac);
            flashingInUnison.add(blocker);

        }

        pacmenToFlash.forEach((time, flashingInUnison) -> {
            flashingInUnison.forEach(pac -> {
                view.flashPacman(pac, time);
            });
        });

        for (BumpCouple bumpCouple : bumpToDisplay) {
            view.launchBumpFx(
                bumpCouple.getFrom(), bumpCouple.getFromBlocker(), bumpCouple.getTo(), bumpCouple.getDistance()
            );
        }

        for (Pacman pac : resolution.getBlockedPacmen()) {
            Coord target = getIntendedPositionAtStep(pac, pac.getCurrentPathStep() + 1);
            pac.addToGameSummary(
                String.format(
                    "Pac %d is blocked from entering (%d, %d).",
                    pac.getNumber(), target.getX(), target.getY()
                )
            );
        }

        killPacmen(pacmenToKill);

        eatPellets();
        eatCherries();

        currentStep++;
    }

    private Predicate<? super Pacman> getPacFastEnoughFilter(int step) {
        return pac -> pac.fastEnoughToMoveAt(step);
    }

    private void printPacmenGameSummary() {
        for (Player player : gameManager.getPlayers()) {
            if (player.getPacmen().stream().anyMatch(pac -> !pac.getGameSummary().isEmpty())) {
                gameManager.addToGameSummary(String.format("%s:", player.getNicknameToken()));
                player.getPacmen().stream()
                    .sorted(Comparator.comparing(Pacman::getId))
                    .forEach(pac -> {
                        pac.getGameSummary().forEach(line -> {
                            gameManager.addToGameSummary("- " + line);
                        });
                        pac.clearGameSummary();
                    });
            }
        }
    }

    private void updateAbilityModifiers() {
        for (Pacman pac : pacmen) {
            if (pac.getSpeed() > 1 && pac.getAbilityDuration() == 0) {
                pac.setSpeed(Config.PACMAN_BASE_SPEED);
                view.endSpeed(pac);
            }
        }
    }

    private boolean pacmenHaveCollided(Pacman a, Pacman b) {
        Coord fromA = getIntendedPositionAtStep(a, a.getPreviousPathStep());
        Coord fromB = getIntendedPositionAtStep(b, b.getPreviousPathStep());
        Coord toA = getIntendedPositionAtStep(a, a.getCurrentPathStep());
        Coord toB = getIntendedPositionAtStep(b, b.getCurrentPathStep());
        return toA.equals(toB) || (toA.equals(fromB) && toB.equals(fromA));
    }

    private Stream<Pacman> otherPacmen(Pacman pac) {
        return otherPacmen(pac, pacmen);
    }

    private Stream<Pacman> otherPacmen(Pacman pac, Collection<Pacman> collection) {
        return collection.stream().filter(p -> p != pac);
    }

    private static Comparator<? super Pacman> byDistanceTo(Coord position) {
        return (a, b) -> {
            return a.getPosition().manhattanTo(position) - b.getPosition().manhattanTo(position);
        };
    }

    private MovementResolution resolvePacmenMovement() {
        MovementResolution resolution = new MovementResolution();
        List<Pacman> pacmenToResolve = pacmen.stream()
            .filter(pac -> pac.getIntent().getActionType() == ActionType.MOVE)
            .filter(pac -> !pac.moveFinished())
            .filter(getPacFastEnoughFilter(currentStep))
            .collect(Collectors.toList());

        List<Pacman> resolvedPacmen = new ArrayList<>();

        do {
            resolvedPacmen.clear();
            for (Pacman pac : pacmenToResolve) {
                Optional<Pacman> blockedBy = otherPacmen(pac)
                    .filter(other -> {
                        return isBodyBlockedBy(pac, other);
                    })
                    .min(byDistanceTo(pac.getPosition()));

                if (blockedBy.isPresent()) {
                    resolvedPacmen.add(pac);
                    resolution.addBlockedPacmen(pac);
                    resolution.blockedBy.put(pac, blockedBy.get());
                }
            }

            resolvedPacmen.forEach(pac -> pac.setBlocked(true));

            pacmenToResolve.removeAll(resolvedPacmen);
        } while (!resolvedPacmen.isEmpty());

        for (Pacman pac : pacmenToResolve) {
            movePacman(pac);
            resolution.addMovedPacman(pac);
        }

        return resolution;
    }

    private boolean isBodyBlockedBy(Pacman pac, Pacman other) {
        if (!Config.BODY_BLOCK) {
            return false;
        }

        if (!Config.FRIENDLY_BODY_BLOCK && pac.getOwner() == other.getOwner()) {
            return false;
        }

        // Never blocked against something pac can eat
        if (canEat(pac, other)) {
            return false;
        }

        // If beaten, can go to same coord (we only block crossing in that case)
        if (canEat(other, pac) && pacmenWillShareSameCoord(pac, other)) {
            return false;
        }

        return pacmenWillCollide(pac, other);
    }

    private boolean pacmenWillShareSameCoord(Pacman a, Pacman b) {
        Coord toA = getIntendedPositionAtStep(a, a.gotBlocked() ? a.getCurrentPathStep() : a.getCurrentPathStep() + 1);
        Coord toB = getIntendedPositionAtStep(b, b.gotBlocked() ? b.getCurrentPathStep() : b.getCurrentPathStep() + 1);
        return toA.equals(toB);
    }

    private boolean pacmenWillCollide(Pacman a, Pacman b) {
        Coord fromA = getIntendedPositionAtStep(a, a.getCurrentPathStep());
        Coord fromB = getIntendedPositionAtStep(b, b.getCurrentPathStep());
        Coord toA = a.gotBlocked() ? fromA : getIntendedPositionAtStep(a, a.getCurrentPathStep() + 1);
        Coord toB = b.gotBlocked() ? fromB : getIntendedPositionAtStep(b, b.getCurrentPathStep() + 1);
        return toA.equals(toB) || (toA.equals(fromB) && toB.equals(fromA));
    }

    private Coord getIntendedPositionAtStep(Pacman pac, int step) {
        return pac.getIntendedPath().get(Math.min(step, pac.getIntendedPath().size() - 1));
    }

    private void processPacmenIntent() {
        for (Pacman pac : pacmen) {
            if (pac.getIntent().getActionType() == ActionType.MOVE) {
                pac.setIntendedPath(computeIntendedPath(pac));
            } else {
                ArrayList<Coord> intendedPath = new ArrayList<>();
                intendedPath.add(pac.getPosition());
                pac.setIntendedPath(intendedPath);
            }
            view.setPacMessage(pac);
        }
    }

    private void executePacmenAbilities() {
        for (Pacman pac : pacmen) {
            if (pac.getAbilityToUse() != null) {
                Ability.Type ability = pac.getAbilityToUse();
                if (pac.getAbilityCooldown() != 0) {
                    if (pac.getAbilityToUse() == Ability.Type.SPEED) {
                        pac.addToGameSummary(
                            String.format(
                                "Pac %d can't use a speed boost yet!",
                                pac.getNumber()
                            )
                        );
                    } else {
                        PacmanType pacType = getPacmanTypeFromAbility(ability);
                        pac.addToGameSummary(
                            String.format(
                                "Pac %d can't switch to %s form yet!",
                                pac.getNumber(),
                                pacType.toString()
                            )
                        );
                    }
                    continue;
                }
                if (
                    !Config.SPEED_ABILITY_AVAILABLE && ability == Ability.Type.SPEED
                        || !Config.SWITCH_ABILITY_AVAILABLE && ability != Ability.Type.SPEED
                ) {
                    String abilityCategory = ability == Ability.Type.SPEED ? "Speed boost" : "Pac type switching";
                    pac.addToGameSummary(
                        String.format(
                            "Pac %d: %s is not available in this league!",
                            pac.getNumber(),
                            abilityCategory
                        )
                    );
                    continue;
                }

                if (ability == Type.SET_ROCK || ability == Type.SET_PAPER || ability == Type.SET_SCISSORS) {
                    view.changePacmanType(pac, pac.getType());
                    pac.setType(getPacmanTypeFromAbility(ability));
                } else if (ability == Type.SPEED) {
                    pac.setSpeed(Config.SPEED_BOOST);
                    view.chargeSpeed(pac);
                    pac.setAbilityDuration(Config.ABILITY_DURATION);
                }

                pac.setAbilityCooldown(Config.ABILITY_COOLDOWN);
                view.addCooldownAnimation(pac);

                if (ability == Ability.Type.SPEED) {
                    pac.addToGameSummary(
                        String.format(
                            "Pac %d used a speed boost.",
                            pac.getNumber()
                        )
                    );
                } else {
                    pac.addToGameSummary(
                        String.format(
                            "Pac %d switch to %s form.",
                            pac.getNumber(),
                            pac.getType().toString()
                        )
                    );
                }

            }
        }
    }

    private PacmanType getPacmanTypeFromAbility(Type ability) {
        switch (ability) {
        case SET_ROCK:
            return PacmanType.ROCK;
        case SET_PAPER:
            return PacmanType.PAPER;
        case SET_SCISSORS:
            return PacmanType.SCISSORS;
        default:
            return null;
        }
    }

    private List<Coord> computeIntendedPath(Pacman pac) {
        MoveAction intent = (MoveAction) pac.getIntent();
        PathFinderResult pfr = pathfinder.setGrid(grid)
            .from(pac.getPosition())
            .to(intent.getTarget())
            .findPath();
        List<Coord> wholePath = pfr.path;
        if (pfr.isNearest) {
            Coord newTarget = pfr.path.get(pfr.path.size() - 1);
            if (pfr.path.size() > 1) {
                pac.setWarningPathMessage(
                    String.format(
                        "Warning: target (%d, %d) is unreachable, going to (%d, %d) instead.",
                        intent.getTarget().x,
                        intent.getTarget().y,
                        newTarget.x,
                        newTarget.y
                    )
                );
            } else {
                pac.setWarningPathMessage(
                    String.format(
                        "Warning: target (%d, %d) is unreachable. Staying here!",
                        intent.getTarget().x,
                        intent.getTarget().y
                    )
                );
            }
        } else {
            pac.setWarningPathMessage(null);
        }

        List<Coord> pathThisTurn = new ArrayList<>();

        if (wholePath.size() > 1) {
            int stepsThisTurn = Math.min(pac.getSpeed(), wholePath.size() - 1);
            pathThisTurn = wholePath.subList(0, stepsThisTurn + 1);
        } else {
            // pacman stay in place or cannot move
            pathThisTurn.add(pac.getPosition());
        }

        return pathThisTurn;
    }

    private void movePacman(Pacman pac) {
        pac.setCurrentPathStep(pac.getCurrentPathStep() + 1);

        Coord from = getIntendedPositionAtStep(pac, pac.getPreviousPathStep());
        Coord to = getIntendedPositionAtStep(pac, pac.getCurrentPathStep());

        pac.setPosition(to);
        view.movePacman(pac, from, to);

        String messageGameSummary = String.format(
            "Pac %d moved to (%d, %d).",
            pac.getNumber(),
            pac.getPosition().x,
            pac.getPosition().y
        );

        if (pac.getWarningPathMessage() != null) {
            messageGameSummary += " " + pac.getWarningPathMessage();
            pac.setWarningPathMessage(null);
        }

        pac.addToGameSummary(messageGameSummary);
    }

    private void killPacmen(Collection<Pacman> pacmenToKill) {
        for (Pacman pac : pacmenToKill) {
            pacmen.remove(pac);
            pac.setDead();
            Player pacOwner = pac.getOwner();
            if (!pacOwner.getAlivePacmen().findAny().isPresent()) {
                pacOwner.deactivate();
            }

            gameManager.addTooltip(
                pac.getOwner(), String.format(
                    "Pac %d has died.",
                    pac.getNumber()
                )
            );

            pac.addToGameSummary(
                String.format(
                    "Pac %d has died.",
                    pac.getNumber()
                )
            );
        }
    }

    private void eatItem(Function<Cell, Boolean> hasItem, int pelletValue) {
        Map<Coord, List<Pacman>> eatenBy = new HashMap<>();
        for (Pacman pac : pacmen) {
            Cell cell = grid.get(pac.getPosition());
            if (hasItem.apply(cell)) {
                eatenBy.computeIfAbsent(pac.getPosition(), key -> new ArrayList<>(totalPacmen));
                eatenBy.get(pac.getPosition()).add(pac);
                eatenBy.compute(pac.getPosition(), (key, list) -> {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    boolean eatenByMe = list.stream()
                        .anyMatch(item -> item.getOwner() == pac.getOwner());
                    if (!eatenByMe) {
                        list.add(pac);
                    }
                    return list;
                });
            }
        }

        eatenBy.forEach((coord, list) -> {
            list.stream()
                .map(Pacman::getOwner)
                .distinct()
                .forEach(player -> {
                    player.pellets += pelletValue;
                });

            grid.get(coord).setHasPellet(false);
            grid.get(coord).setHasCherry(false);
            view.eatPellet(coord, list, pelletValue);
        });

        view.updateScores();
    }

    private void eatPellets() {
        eatItem(Cell::hasPellet, 1);
    }

    private void eatCherries() {
        eatItem(Cell::hasCherry, Config.CHERRY_SCORE);
    }

    private boolean canImproveRanking(Player player) {
        int remainingPellets = getRemainingPellets();

        return remainingPellets > 0 && gameManager.getPlayers().stream()
            .filter(p -> p != player && p.pellets >= player.pellets)
            .anyMatch(p -> player.pellets + remainingPellets >= p.pellets);
    }

    public boolean isGameOver() {
        // one player left with pacmen
        List<Player> activePlayers = gameManager.getActivePlayers();
        if (activePlayers.size() <= 1) {
            return true;
        }

        // the game isn't over if a player can still improve its rank
        return gameManager.getActivePlayers().stream().noneMatch(this::canImproveRanking);
    }

    public void performGameOver() {
        // one player left with pacmen
        List<Player> activePlayers = gameManager.getActivePlayers();
        if (activePlayers.size() <= 1) {
            if (activePlayers.size() == 1) {
                gameManager.addToGameSummary(
                    String.format(
                        "Only %s still has pacs standing!",
                        activePlayers.get(0).getNicknameToken()
                    )
                );
                activePlayers.get(0).pellets += getRemainingPellets();
                view.updateScores();
                view.getAllPellets(activePlayers.get(0));
            } else {
                gameManager.addToGameSummary("No pacs remaining!");
            }
            return;
        }

        // We have a winner
        int remainingPellets = getRemainingPellets();
        for (Player player : gameManager.getPlayers()) {
            if (gameManager.getActivePlayers().stream().filter(p -> p != player).allMatch(p -> p.pellets + remainingPellets < player.pellets)) {
                gameManager.addToGameSummary(
                    String.format(
                        "Game Over: %s has so many pellets that they can't be defeated!",
                        player.getNicknameToken()
                    )
                );
                return;
            }
        }
    }

    private int getRemainingPellets() {
        return grid.getCells().values().stream()
            .filter(cell -> cell.hasPellet() || cell.hasCherry())
            .mapToInt(cell -> cell.hasPellet() ? 1 : Config.CHERRY_SCORE)
            .sum();
    }

    private List<Pacman> findVisiblePacmen(Player player) {
        List<Coord> coords = findVisibleItems(player, coord -> pacmen.stream().anyMatch(pac -> pac.getPosition().equals(coord)));
        List<Pacman> visiblePacmen = pacmen
            .stream()
            .filter(pac -> coords.contains(pac.getPosition()))
            .collect(Collectors.toList());
        return visiblePacmen;
    }

    private List<Coord> findVisibleItems(Player player, Function<Coord, Boolean> hasItem) {
        List<Coord> visibleItems = new ArrayList<Coord>();
        player.getAlivePacmen().forEach(playerPac -> {
            for (Coord unitMove : Config.ADJACENCY) {
                Coord currentCoord = playerPac.getPosition();
                while (grid.get(currentCoord).isFloor()) {
                    if (hasItem.apply(currentCoord) && !visibleItems.contains(currentCoord)) {
                        visibleItems.add(currentCoord);
                    }
                    Optional<Coord> nextCoord = grid.getCoordNeighbour(currentCoord, unitMove);
                    if (nextCoord.isPresent()) {
                        currentCoord = nextCoord.get();
                    } else {
                        break;
                    }
                    if (playerPac.getPosition().equals(currentCoord)) {
                        break;
                    }
                }
            }
        });
        return visibleItems;
    }

    private List<Coord> findVisiblePellets(Player player) {
        return findVisibleItems(player, coord -> grid.get(coord).hasPellet());
    }

    public boolean isSpeedTurn() {
        int numSteps = pacmen.stream()
            .mapToInt(p -> p.getIntendedPath().size())
            .max()
            .getAsInt() - 1;

        return currentStep > 0 && currentStep < numSteps;
    }

    public Grid getGrid() {
        return grid;
    }

}
