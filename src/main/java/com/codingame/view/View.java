package com.codingame.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.BitmapText;
import com.codingame.gameengine.module.entities.BlendableEntity.BlendMode;
import com.codingame.gameengine.module.entities.ContainerBasedEntity;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.spring2020.Cell;
import com.codingame.spring2020.Config;
import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Grid;
import com.codingame.spring2020.Pacman;
import com.codingame.spring2020.PacmanType;
import com.codingame.view.event.AnimatedEventModule;
import com.codingame.view.event.ViewerEvent;
import com.codingame.view.tooltip.TooltipGlobalData;
import com.codingame.view.tooltip.TooltipModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class View {
    @Inject private GraphicEntityModule gem;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private TooltipModule tooltipModule;
    @Inject private Provider<GridView> gridViewProvider;
    @Inject private AnimatedEventModule events;
    @Inject ToggleModule toggleModule;

    public static final double EPS = 0.000001;

    // Game zone group
    private static final int Z_LAYER_GRID = 0;
    private static final int Z_LAYER_PELLET = 5;
    private static final int Z_LAYER_PAC = 10;
    private static final int Z_LAYER_HUD = 30;
    // Pacman group
    private static final int Z_LAYER_PACMAN_PLASMA = 2;
    private static final int Z_LAYER_PACMAN_SPRITE = 0;
    private static final int Z_LAYER_PACMAN_SWITCH_FX = 1;
    private static final int Z_LAYER_COOLDOWNBAR_BACKGROUND = 2;
    private static final int Z_LAYER_COOLDOWNBAR = 3;
    private static final int Z_LAYER_PACMAN_MESSAGE = 4;
    private static final double Y_OFFSET_PACMAN_MESSAGE = -0.82;
    private static final double X_OFFSET_PACMAN_COOLDOWN = -0.5;
    private static final double Y_OFFSET_PACMAN_COOLDOWN = -0.6;

    public static final int GRID_COLOR = 0xFF8C00;
    public static final int Z_LAYER_POWERUP = 4;
    public static final int HUD_HEIGHT = 150;
    public static final Map<Coord, TileMapping> NEIGHBOUR_MAP = new HashMap<>();
    public static final Map<Coord, TileMapping> CORNER_MAP = new HashMap<>();

    static {
        CORNER_MAP.put(new Coord(-1, -1), new TileMapping(1, 10));
        NEIGHBOUR_MAP.put(new Coord(0, -1), new TileMapping(2));
        CORNER_MAP.put(new Coord(1, -1), new TileMapping(4, 18));
        NEIGHBOUR_MAP.put(new Coord(-1, 0), new TileMapping(8));
        NEIGHBOUR_MAP.put(new Coord(1, 0), new TileMapping(16));
        CORNER_MAP.put(new Coord(-1, 1), new TileMapping(32, 72));
        NEIGHBOUR_MAP.put(new Coord(0, 1), new TileMapping(64));
        CORNER_MAP.put(new Coord(1, 1), new TileMapping(128, 80));
    }

    Group gameZone;
    Sprite background;
    List<PacmanView> pacmen;
    Map<PacmanView, PacmanView> pacmenWrapClones;
    Rectangle pacMask;
    Map<Coord, Entity<?>> cells;
    int cellSize = 40; // breaks on bufferedGroups
    int pacmanSize = (int) (cellSize * 1.5);
    Map<Coord, Entity<?>> pellets;
    private int screenWidth;
    private int screenHeight;
    List<BitmapText> scoreLabels;
    private int totalPacmen;
    Random random;
    private int pacmenPerPlayer;
    Grid gridModel;
    GridView grid;

    Map<Pacman, PacmanType> switchers;
    List<Move> movers;
    List<Pacman> goners;
    List<Pacman> cooldownLaunchers;
    List<Bump> bumpers;
    List<Flash> flashers;
    List<PacmanView> dying;

    private static double fitAspectRatioContains(int srcWidth, int srcHeight, int maxWidth, int maxHeight) {
        return Math.min((double) maxWidth / srcWidth, (double) maxHeight / srcHeight);
    }

    public void init(List<Pacman> pacmenModel, Grid grid, int pacmenPerPlayer) {
        random = new Random();
        switchers = new HashMap<>();
        cooldownLaunchers = new ArrayList<>();
        goners = new ArrayList<>();
        dying = new ArrayList<>();
        movers = new ArrayList<>();
        bumpers = new ArrayList<>();
        flashers = new ArrayList<>();
        this.pacmenPerPlayer = pacmenPerPlayer;
        this.gridModel = grid;

        background = gem.createSprite().setImage("BACKGROUND_semi.jpg");
        gameZone = gem.createGroup();

        screenWidth = gem.getWorld().getWidth();
        screenHeight = gem.getWorld().getHeight();
        int gameZoneWidth = grid.getWidth() * cellSize;
        int gameZoneHeight = grid.getHeight() * cellSize;

        double coefficient = fitAspectRatioContains(gameZoneWidth, gameZoneHeight, screenWidth, screenHeight - HUD_HEIGHT);
        gameZone.setScale(coefficient);
        if (coefficient > 2.8) {
            pacmanSize = cellSize;
        }

        center(gameZone, gameZoneWidth * coefficient, gameZoneHeight * coefficient, screenWidth, screenHeight);
        gameZone.setY(gameZone.getY() + HUD_HEIGHT / 2);

        TooltipGlobalData tooltipData = new TooltipGlobalData(
            grid.getWidth(), grid.getHeight(), gameZone.getX(), gameZone.getY(), cellSize, coefficient
        );
        tooltipModule.init(tooltipData);

        pacMask = gem.createRectangle()
            .setWidth(gameZoneWidth)
            .setHeight(gameZoneHeight)
            .setZIndex(20);
        gameZone.add(pacMask);

        Group speedboostGroup = gem.createGroup().setZIndex(Z_LAYER_PAC - 1).setMask(pacMask);
        gameZone.add(speedboostGroup);

        events.init(gameZone.getScaleX(), speedboostGroup.getId());

        initPacmen(pacmenModel);
        initGrid(grid);
        initHud();
        endOfTurn();
    }

    private void initHud() {

        int hudWidth = 530;
        Group hudGroup = gem.createGroup()
            .setZIndex(Z_LAYER_HUD);

        Sprite hudRed = gem.createSprite()
            .setImage("HUD_Masque_RED")
            .setZIndex(1);

        Sprite hudBlue = gem.createSprite()
            .setImage("HUD_Masque_BLUE")
            .setX(screenWidth - hudWidth)
            .setZIndex(1);

        hudGroup.add(hudRed, hudBlue);

        scoreLabels = new ArrayList<BitmapText>(gameManager.getPlayerCount());

        int playerHudZoneWidth = screenWidth / (gameManager.getPlayerCount());
        double avatarRotation = 0.08;
        int avatarSize = 130;
        int playerHudNameOffset = 630;
        int playerHudScoreOffset = 320;
        int playerHudAvatarOffset = 882;

        for (Player p : gameManager.getPlayers()) {

            int coefMirror = p.getIndex() == 0 ? -1 : 1;

            BitmapText nameLabel = gem.createBitmapText()
                .setFont("BRLNS_66")
                .setFontSize(36)
                .setText(p.getNicknameToken())
                .setMaxWidth(300)
                .setAnchorX(0.5)
                .setX(playerHudZoneWidth + coefMirror * playerHudNameOffset)
                .setY(7)
                .setZIndex(2);

            BitmapText scoreLabel = gem.createBitmapText()
                .setFont("BRLNS_66")
                .setFontSize(72)
                .setText("0")
                .setAnchorX(0.5)
                .setX(playerHudZoneWidth + coefMirror * playerHudScoreOffset)
                .setY(10)
                .setZIndex(2);

            Sprite avatar = gem.createSprite()
                .setImage(p.getAvatarToken())
                .setAnchor(0.5)
                .setX(playerHudZoneWidth + coefMirror * playerHudAvatarOffset)
                .setY(70)
                .setRotation(coefMirror * avatarRotation)
                .setBaseHeight(avatarSize)
                .setBaseWidth(avatarSize)
                .setZIndex(0);

            hudGroup.add(nameLabel, scoreLabel, avatar);
            scoreLabels.add(scoreLabel);
        }

    }

    private void initGrid(Grid gridModel) {
        grid = gridViewProvider.get();

        grid.init(gridModel, cellSize);

        ContainerBasedEntity<?> buffer = grid.group;
        buffer.setZIndex(Z_LAYER_GRID);
        ContainerBasedEntity<?> tunnels = grid.groupTunnel;
        tunnels.setZIndex(Z_LAYER_PAC + 1);
        pellets = new HashMap<>();
        cells = grid.cells;

        gridModel.getCells().forEach(
            (Coord coord, Cell cell) -> {
                if (cell.hasPellet()) {
                    Sprite pellet = gem.createSprite()
                        .setBaseWidth((int) (cellSize * 0.3))
                        .setBaseHeight((int) (cellSize * 0.3))
                        .setAnchor(0.5)
                        .setZIndex(Z_LAYER_PELLET)
                        .setImage("Bonusx1");

                    setToGridCenterCoordinates(pellet, coord);

                    pellets.put(coord, pellet);
                    gameZone.add(pellet);
                }

                if (cell.hasCherry()) {
                    Sprite cherry = gem.createSprite()
                        .setBaseWidth((int) (cellSize * 0.6))
                        .setBaseHeight((int) (cellSize * 0.6))
                        .setAnchor(0.5)
                        .setImage("Bonusx5")
                        .setZIndex(Z_LAYER_PELLET);

                    setToGridCenterCoordinates(cherry, coord);

                    pellets.put(coord, cherry);
                    gameZone.add(cherry);
                }
            }
        );

        gameZone.add(buffer, tunnels);
    }

    private void center(Entity<?> entity, double entityWidth, double entityHeight, int containerWidth, int containerHeight) {
        int x = (int) (containerWidth / 2 - (double) entityWidth / 2);
        int y = (int) (containerHeight / 2 - (double) entityHeight / 2);
        entity
            .setX(x)
            .setY(y);

    }

    private void initPacmen(List<Pacman> pacmenModel) {
        pacmen = new ArrayList<>(pacmenModel.size());
        pacmenWrapClones = new HashMap<>(pacmenModel.size());
        totalPacmen = pacmenPerPlayer * gameManager.getPlayerCount();

        for (Pacman pacModel : pacmenModel) {

            PacmanView pacman = createPacmanGroup(pacModel);
            PacmanView clone = createClone(pacman, pacModel, true);
            clone.group.setVisible(false);
            // Pacs are face to face when the game starts
            if (pacModel.getPosition().getX() * 2 < gridModel.getWidth()) {
                pacman.rotationWrapper.setScaleX(-1, Curve.NONE);
            }

            setToGridCenterCoordinates(pacman.group, pacModel.getPosition());
            gameZone.add(pacman.group);
            pacmen.add(pacman);

            toggleModule.displayOnToggleState(pacman.message, "messageToggle", true);

            updateTooltipText(pacman);
        }

    }

    private void updateTooltipText(PacmanView pacman) {
        tooltipModule.setTooltipText(
            pacman.sprite,
            getTooltipText(pacman.model)
        );
    }

    private String getTooltipText(Pacman pacModel) {
        String color = pacModel.getOwner().getColor();
        if (pacModel.getType() == PacmanType.NEUTRAL) {
            return String.format(
                "id: %d (%s)",
                pacModel.getNumber(),
                color
            );
        }
        if (pacModel.getAbilityDuration() > 0) {
            return String.format(
                "id: %d (%s)\ntype: %s\nspeed turns left: %d",
                pacModel.getNumber(),
                color,
                pacModel.getType().name().toUpperCase(),
                pacModel.getAbilityDuration() - 1
            );
        }
        return String.format(
            "id: %d (%s)\ntype: %s",
            pacModel.getNumber(),
            color,
            pacModel.getType().name().toUpperCase()
        );
    }

    private PacmanView createPacmanGroup(Pacman pacModel) {
        return createPacmanGroup(pacModel, true, false);
    }

    private PacmanView createPacmanGroup(Pacman pacModel, boolean alive, boolean clone) {
        PacmanView pacman = new PacmanView(pacModel);

        pacman.sprite = gem.createSpriteAnimation()
            .setDuration(gameManager.getFrameDuration())
            .setLoop(true)
            .setVisible(true)
            .setPlaying(false)
            .setAnchor(0.5)
            .setMask(pacMask)
            .setZIndex(Z_LAYER_PACMAN_SPRITE);

        pacman.switchFX = gem.createSpriteAnimation()
            .setImages(getFxImages(1, 20, "transition"))
            .setVisible(false)
            .setZIndex(Z_LAYER_PACMAN_SWITCH_FX)
            .setAnchor(0.5)
            .setBlendMode(BlendMode.ADD)
            .setScale(0.5);

        pacman.message = gem.createText()
            .setFontSize(30)
            .setFontFamily("Arial")
            .setAnchor(0.5)
            .setFillColor(pacModel.getOwner().getColorToken())
            .setMaxWidth(136)
            .setStrokeThickness(4)
            .setZIndex(Z_LAYER_PACMAN_MESSAGE)
            .setVisible(true);

        setToAbsoluteCenterWithOffset(pacman.message, pacModel.getPosition(), 0, Y_OFFSET_PACMAN_MESSAGE);

        pacman.plasmaBallContainer = gem.createGroup()
            .setAlpha(0)
            .setZIndex(Z_LAYER_PACMAN_PLASMA);

        events.addPlasmaBall(pacman.plasmaBallContainer.getId());

        pacman.group = gem.createGroup(pacman.plasmaBallContainer, pacman.switchFX, pacman.sprite)
            .setZIndex(Z_LAYER_PAC);

        if (!clone) {
            pacman.cooldownBarBackground = gem.createRectangle()
                .setScale(0.5)
                .setWidth(100)
                .setHeight(15)
                .setLineWidth(3)
                .setFillColor(0x000000)
                .setZIndex(Z_LAYER_COOLDOWNBAR_BACKGROUND)
                .setVisible(false);

            pacman.cooldownBar = gem.createRectangle()
                .setScale(0.5)
                .setWidth(100)
                .setHeight(15)
                .setLineColor(0x000000)
                .setLineWidth(3)
                .setFillColor(0x90c43b)
                .setVisible(false)
                .setZIndex(Z_LAYER_COOLDOWNBAR);

            setToAbsoluteCenterWithOffset(pacman.cooldownBar, pacModel.getPosition(), X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
            setToAbsoluteCenterWithOffset(pacman.cooldownBarBackground, pacModel.getPosition(), X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
        }

        Group baseScaleWrapper = wrapGroupAround(pacman.sprite).setScale(0.67);
        pacman.rotationWrapper = wrapGroupAround(pacman.sprite);

        if (!clone && pacModel.getType() != PacmanType.NEUTRAL) {
            pacman.death = gem.createSpriteAnimation()
                .setImages(PacmanView.getDeathPacmanImages(pacModel.getOwner().getIndex(), pacModel.getType()))
                .setDuration(gameManager.getFrameDuration() / 2)
                .setLoop(false)
                .pause()
                .setVisible(false)
                .setAnchor(0.5)
                .setMask(pacMask);

            pacman.deathFX = gem.createSpriteAnimation()
                .setImages(getFxImages(28, 39, "mort_FX"))
                .setVisible(false)
                .setLoop(false)
                .pause()
                .setZIndex(1)
                .setAnchor(0.5)
                .setBlendMode(BlendMode.ADD)
                .setDuration(gameManager.getFrameDuration() / 2);

            pacman.sprite.getParent().ifPresent(group -> group.add(pacman.death, pacman.deathFX));
        }

        Player owner = pacModel.getOwner();
        pacman.setViewState(owner.getIndex(), pacModel.getType(), alive);

        gem.commitEntityState(0, baseScaleWrapper);

        return pacman;
    }

    private Group wrapGroupAround(Entity<?> entity) {
        Group wrapper = gem.createGroup();
        Optional<ContainerBasedEntity<?>> parent = entity.getParent();
        parent.ifPresent(p -> {
            p.remove(entity);
        });
        wrapper.add(entity);
        parent.ifPresent(p -> {
            p.add(wrapper);
        });
        return wrapper;
    }

    private String[] getFxImages(int startNumber, int endNumber, String mask) {
        List<String> fxImages = new ArrayList<String>();
        for (int i = startNumber; i < endNumber + 1; i++) {
            String formattedNumber = String.format("%04d", i);
            fxImages.add(mask + formattedNumber);
        }
        return fxImages.toArray(new String[0]);
    }

    void setToGridCoordinates(Entity<?> entity, Coord position) {
        entity
            .setX(position.getX() * cellSize)
            .setY(position.getY() * cellSize);
    }

    private void setToGridCenterCoordinates(Entity<?> entity, Point2D.Double position) {
        entity
            .setX((int) (position.getX() * cellSize + cellSize / 2))
            .setY((int) (position.getY() * cellSize + cellSize / 2));
    }

    private void setToGridCenterCoordinates(Entity<?> entity, Coord position) {
        entity
            .setX(position.getX() * cellSize + cellSize / 2)
            .setY(position.getY() * cellSize + cellSize / 2);
    }

    public void eatPellet(Coord coord, List<Pacman> list, int pelletValue) {
        Entity<?> pellet = pellets.get(coord);
        pellet.setVisible(false);

        Map<String, Object> params = events.createAnimationEvent("splash", 1).getParams();
        double x = convertXFromGridToAbsoluteCenter(coord.getX());
        double y = convertYFromGridToAbsoluteCenter(coord.getY());
        params.put("x", x);
        params.put("y", y);
        params.put("v", pelletValue);

    }

    public void bumpPacman(Pacman pacModel, Coord from, Coord to, int distanceToBlocker) {
        bumpers.add(new Bump(pacModel, from, to, distanceToBlocker));
    }

    private void animateBump(Pacman pacModel, Coord from, Coord to, int distanceToBlocker) {

        PacmanView pac = pacmen.get(pacModel.getId());
        double progress = distanceToBlocker / 4d;
        double midTime = 0.5;

        boolean alive = !isInPain(pacModel);

        if (to.manhattanTo(from) > 1) {
            Coord unitDiff = to.subtract(from).getUnitVector();
            Coord exitingCoord = from.subtract(unitDiff);
            Coord enteringCoord = to.add(unitDiff);

            //Wrapping
            Point2D.Double pacFrom = new Point2D.Double(from.getX(), from.getY());
            Point2D.Double pacTo = getPartWay(from, exitingCoord, progress);
            Point2D.Double cloneFrom = new Point2D.Double(enteringCoord.getX(), enteringCoord.getY());
            Point2D.Double cloneTo = getPartWay(enteringCoord, to, progress);

            PacmanView pacClone = summonClone(pac, pacModel, alive);

            translatePacman(pac, pacFrom, pacTo, to, from, alive, 0, midTime, false);
            translatePacman(pacClone, cloneFrom, cloneTo, to, from, alive, 0, midTime, false);

            translatePacman(pac, pacTo, pacFrom, to, from, alive, midTime, 1, true);
            translatePacman(pacClone, cloneTo, cloneFrom, to, from, alive, midTime, 1, true);

            //XXX: this is duplicate code from animateMove
            pacClone.group.setVisible(false);
            if (!alive) {
                animateDeath(pac, pacModel);
            }
        } else {
            Point2D.Double fromPoint = new Point2D.Double(from.getX(), from.getY());
            Point2D.Double partWay = getPartWay(from, to, progress);
            translatePacman(pac, fromPoint, partWay, from, to, alive, 0, midTime, false);
            translatePacman(pac, partWay, fromPoint, from, to, alive, midTime, 1, true);
        }
    }

    private Point2D.Double getPartWay(Coord from, Coord to, double progress) {
        return new Point2D.Double(
            from.getX() + (to.getX() - from.getX()) * progress,
            from.getY() + (to.getY() - from.getY()) * progress
        );
    }

    public void launchBumpFx(Coord fromA, Coord fromB, Coord to, int distance) {
        double mid = 0.5;
        double x, y;

        if (pointsLinedUpOnGrid(fromA, fromB)) {
            //A o B

            if (Math.abs(fromA.getX() - fromB.getX()) == grid.width - 1) {
                // Both pacs are placed on a border
                // Don't show the "choc" animation
                return;
            }
            double meanX = ((fromA.getX() + fromB.getX()) / 2d);
            double meanXWrap = ((fromA.getX() + fromB.getX() + grid.width) / 2d) % grid.width;
            double meanY = ((fromA.getY() + fromB.getY()) / 2d);

            boolean wrap = Math.abs(fromA.getX() - fromB.getX()) > grid.width / 2d;

            x = convertXFromGridToAbsoluteCenter(wrap ? meanXWrap : meanX);
            y = convertYFromGridToAbsoluteCenter(meanY);

        } else {
            //A o
            //  B
            x = convertXFromGridToAbsoluteCenter(to.getX());
            y = convertYFromGridToAbsoluteCenter(to.getY());
        }

        double angle = Math.atan2(fromB.getY() - fromA.getY(), fromB.getX() - fromA.getX());

        ViewerEvent event = events.createAnimationEvent("play", mid);
        event.getParams().put("x", x);
        event.getParams().put("y", y);
        event.getParams().put("name", "choc");
        event.getParams().put("a", angle); //TODO: convert to one of four possible states to save on serialized characters
    }

    private double convertXFromGridToAbsoluteCenter(double x) {
        return (x + 0.5) * (gameZone.getScaleX() * cellSize) + gameZone.getX();
    }

    private double convertYFromGridToAbsoluteCenter(double y) {
        return (y + 0.5) * (gameZone.getScaleY() * cellSize) + gameZone.getY();
    }

    private void setToAbsoluteCenterWithOffset(Entity<?> entity, Coord coord, double offsetX, double offsetY) {
        entity.setX((int) ((coord.getX() + offsetX + 0.5) * (gameZone.getScaleX() * cellSize) + gameZone.getX()));
        entity.setY((int) ((coord.getY() + offsetY + 0.5) * (gameZone.getScaleY() * cellSize) + gameZone.getY()));
    }

    private void setToAbsoluteCenterWithOffset(Entity<?> entity, Point2D.Double position, double offsetX, double offsetY) {
        entity.setX((int) ((position.x + offsetX + 0.5) * (gameZone.getScaleX() * cellSize) + gameZone.getX()));
        entity.setY((int) ((position.y + offsetY + 0.5) * (gameZone.getScaleY() * cellSize) + gameZone.getY()));
    }

    private boolean pointsLinedUpOnGrid(Coord from, Coord to) {
        return from.getX() == to.getX() || from.getY() == to.getY();
    }

    public void flashPacman(Pacman pacModel, double time) {
        flashers.add(new Flash(pacModel, time));
    }

    public void launchFlashFx(Pacman pacModel, double time) {
        PacmanView pac = pacmen.get(pacModel.getId());

        ViewerEvent event = events.createAnimationEvent("flash", time);
        event.getParams().put("id", pac.sprite.getId());

        int cloneId = pacmenWrapClones.get(pac).sprite.getId();
        ViewerEvent cloneEvent = events.createAnimationEvent("flash", time);
        cloneEvent.getParams().put("id", cloneId);

    }

    public void launchSpeedBoostFx(Pacman pacModel, Point2D.Double from, Point2D.Double to, double startTime, double endTime, boolean flip) {
        PacmanView pac = pacmen.get(pacModel.getId());
        int indexColor = pacModel.getOwner().getIndex();
        ViewerEvent event = events.createAnimationEvent("speed", startTime);
        double fromX = (from.getX() * cellSize + cellSize / 2);
        double fromY = (from.getY() * cellSize + cellSize / 2);
        double toX = (to.getX() * cellSize + cellSize / 2);
        double toY = (to.getY() * cellSize + cellSize / 2);

        event.getParams().put("color", indexColor);

        event.getParams().put("id", pac.sprite.getId());
        event.getParams().put("toX", toX);
        event.getParams().put("toY", toY);
        event.getParams().put("fromX", fromX);
        event.getParams().put("fromY", fromY);
        event.getParams().put("walkEnd", endTime);
        event.getParams().put("walkFlip", flip ? 1 : 0);

    }

    public void movePacman(Pacman pacModel, Coord from, Coord to) {
        movers.add(new Move(pacModel, from, to));
    }

    private void performMoves() {
        movers.stream()
            .forEach((move) -> animateMove(move.pacModel, move.from, move.to));
    }

    private void performBumps() {
        bumpers.stream()
            .forEach((bump) -> animateBump(bump.pacModel, bump.from, bump.to, bump.distanceToBlocker));
    }

    private void performFlashes() {
        flashers.stream()
            .sorted(Comparator.comparingDouble(flash -> flash.time))
            .forEach((flash) -> launchFlashFx(flash.pacModel, flash.time));
    }

    private void translatePacman(
        PacmanView pacman, Coord from, Coord to, Coord rotateFrom, Coord rotateTo, boolean alive, double startTime, double endTime
    ) {
        translatePacman(
            pacman,
            new Point2D.Double(from.getX(), from.getY()),
            new Point2D.Double(to.getX(), to.getY()),
            rotateFrom, rotateTo, alive, startTime,
            endTime, false
        );
    }

    private void translatePacman(
        PacmanView pacman, Point2D.Double from, Point2D.Double to, Coord rotateFrom,
        Coord rotateTo, boolean alive, double startTime, double endTime, boolean flip
    ) {
        rotatePacman(pacman, rotateFrom, rotateTo, startTime);
        pacman.sprite.play();
        setToAbsoluteCenterWithOffset(pacman.message, from, 0, Y_OFFSET_PACMAN_MESSAGE);
        setToGridCenterCoordinates(pacman.group, from);
        if (pacman.cooldownBar != null) {
            setToAbsoluteCenterWithOffset(pacman.cooldownBar, from, X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
            setToAbsoluteCenterWithOffset(pacman.cooldownBarBackground, from, X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
            gem.commitEntityState(startTime, pacman.cooldownBar, pacman.cooldownBarBackground);
        }
        gem.commitEntityState(startTime, pacman.group, pacman.sprite, pacman.rotationWrapper, pacman.message);

        setToAbsoluteCenterWithOffset(pacman.message, to, 0, Y_OFFSET_PACMAN_MESSAGE);
        setToGridCenterCoordinates(pacman.group, to);
        if (pacman.cooldownBar != null) {
            setToAbsoluteCenterWithOffset(pacman.cooldownBar, to, X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
            setToAbsoluteCenterWithOffset(pacman.cooldownBarBackground, to, X_OFFSET_PACMAN_COOLDOWN, Y_OFFSET_PACMAN_COOLDOWN);
        }

        if (pacman.model.isSpeeding()) {
            launchSpeedBoostFx(pacman.model, from, to, startTime, endTime, flip);
        }

    }

    private void animateMove(Pacman pacModel, Coord from, Coord to) {
        PacmanView pac = pacmen.get(pacModel.getId());

        boolean alive = !isInPain(pacModel);

        pac.setViewState(pacModel.getOwner().getIndex(), pacModel.getType(), alive);
        pac.sprite.play();
        gem.commitEntityState(0, pac.sprite);
        if (from.manhattanTo(to) > 1) {

            // pacman wraps
            Coord unitDiff = to.subtract(from).getUnitVector();
            Coord exitingCoord = from.subtract(unitDiff);
            Coord enteringCoord = to.add(unitDiff);

            PacmanView pacClone = summonClone(pac, pacModel, alive);

            // Original pac
            translatePacman(pac, enteringCoord, to, to, from, alive, 0, 1);

            // Clone pac
            translatePacman(pacClone, from, exitingCoord, to, from, alive, 0, 1);

            pacClone.group.setVisible(false);
            if (!alive) {
                animateDeath(pac, pacModel);
            }

        } else {
            translatePacman(pac, from, to, from, to, alive, 0, 1);

            if (!alive) {
                // Die after final movement
                animateDeath(pac, pacModel);
            }

        }

    }

    private PacmanView summonClone(PacmanView pac, Pacman pacModel, boolean alive) {
        PacmanView pacClone = pacmenWrapClones.get(pac);
        pacClone.setViewState(pacModel.getOwner().getIndex(), pacModel.getType(), alive);
        pacClone.group.setVisible(true);

        pacClone.sprite.setDuration(pac.sprite.getDuration());
        return pacClone;
    }

    private PacmanView createClone(PacmanView pac, Pacman pacModel, boolean alive) {
        boolean isClone = true;
        PacmanView pacClone = createPacmanGroup(pacModel, alive, isClone);

        pacmenWrapClones.put(pac, pacClone);
        gameZone.add(pacClone.group);
        gem.commitEntityState(0, gameZone);
        pacClone.sprite.setDuration(pac.sprite.getDuration());
        return pacClone;
    }

    public void rotatePacman(PacmanView pac, Coord from, Coord to, double t) {
        if (from.getY() > to.getY()) {
            pac.rotationWrapper.setRotation(Math.PI / 2 * pac.rotationWrapper.getScaleX(), Curve.NONE);
        } else if (from.getY() < to.getY()) {
            pac.rotationWrapper.setRotation(-Math.PI / 2 * pac.rotationWrapper.getScaleX(), Curve.NONE);
        } else if (from.getX() > to.getX()) {
            pac.rotationWrapper.setRotation(0, Curve.NONE);
            pac.rotationWrapper.setScaleX(1, Curve.NONE);
        } else if (from.getX() < to.getX()) {
            pac.rotationWrapper.setRotation(0, Curve.NONE);
            pac.rotationWrapper.setScaleX(-1, Curve.NONE);
        }
        gem.commitEntityState(t, pac.rotationWrapper);
    }

    public void killPacman(Pacman pacModel) {
        goners.add(pacModel);
        PacmanView pacman = pacmen.get(pacModel.getId());
        PacmanView clone = summonClone(pacman, pacModel, false);
        clone.group.setVisible(false);
        sendShake(pacman, 0, 1);
        sendShake(clone, 0, 1);
        cooldownLaunchers.remove(pacModel);
    }

    public void changePacmanType(Pacman pacModel, PacmanType originalType) {
        switchers.put(pacModel, originalType);
    }

    private void performSwitches() {
        // Switchers can also be dying during the switch
        for (Entry<Pacman, PacmanType> switcher : switchers.entrySet()) {
            Pacman pacmanModel = switcher.getKey();
            PacmanType originalType = switcher.getValue();
            PacmanView pacman = pacmen.get(pacmanModel.getId());
            animateSwitch(pacman, originalType, pacmanModel, !isInPain(pacmanModel));
            updateTooltipText(pacman);
        }

    }

    private void sendShake(PacmanView pacman, double startTime, double endTime) {
        Map<String, Object> params = events.createAnimationEvent("shake", startTime).getParams();
        params.put("id", pacman.group.getId());
        params.put("length", endTime - startTime);
    }

    private void animatePain(Pacman pacmanModel, double start, double end) {
        PacmanView pacman = pacmen.get(pacmanModel.getId());
        pacman.setViewState(pacmanModel.getOwner().getIndex(), pacmanModel.getType(), false);
        gem.commitEntityState(start, pacman.sprite);
        animateDeath(pacman, pacmanModel);
    }

    /**
     * Gives this pac X eyes at t=0 and turns it invisible at t=1. Also shakes it.
     */
    private void animatePain(Pacman pacmanModel) {
        animatePain(pacmanModel, 0, 1);
    }

    /**
     * Replaces the pacman with a death animation which begins on the next frame
     */
    private void animateDeath(PacmanView pacman, Pacman pacmanModel) {
        pacman.sprite.setVisible(false);
        pacman.death.setVisible(true);
        pacman.death.play();
        pacman.plasmaBallContainer.setVisible(false);

        dying.add(pacman);

    }

    private boolean isInPain(Pacman pacman) {
        return goners.contains(pacman);
    }

    private void animateSwitch(PacmanView pacman, PacmanType originalType, Pacman pacModel, boolean alive) {
        double switchFXStart = 7 / 35d;
        double switchFXDuration = 20 / 35d;

        pacman.setViewState(pacModel.getOwner().getIndex(), originalType, alive);
        gem.commitEntityState(0, pacman.sprite);

        pacman.setViewState(pacModel.getOwner().getIndex(), pacModel.getType(), alive);
        pacman.switchFX
            .reset()
            .setDuration((int) (gameManager.getFrameDuration() * switchFXDuration))
            .setVisible(true)
            .play();
        gem.commitEntityState(switchFXStart, pacman.switchFX);

        pacman.switchFX.setVisible(false);
        gem.commitEntityState(switchFXStart + switchFXDuration, pacman.switchFX);

        bouncePacman(pacman);

        if (!alive) {
            animateDeath(pacman, pacModel);
        }
    }

    public void bouncePacman(PacmanView pacman) {
        double growthEnd = 9 / 35d;
        double shrinkStart = 26 / 35d;
        double shrinkEnd = 31 / 35d;
        double reboundEnd = 33 / 35d;
        double settleEnd = 35 / 35d;

        pacman.sprite.setScale(1.3);
        gem.commitEntityState(growthEnd, pacman.sprite);
        gem.commitEntityState(shrinkStart, pacman.sprite);
        pacman.sprite.setScale(0.9);
        gem.commitEntityState(shrinkEnd, pacman.sprite);
        pacman.sprite.setScale(1.35);
        gem.commitEntityState(reboundEnd, pacman.sprite);
        pacman.sprite.setScale(1);
        gem.commitEntityState(settleEnd, pacman.sprite);
    }

    public void addCooldownAnimation(Pacman pac) {
        cooldownLaunchers.add(pac);
    }

    public void performCooldownAnimation() {
        int totalCooldown = Config.ABILITY_COOLDOWN - 1;

        List<Pacman> cooldownToRemove = new ArrayList<>();

        for (Pacman pacmanModel : cooldownLaunchers) {
            int currentCooldown = pacmanModel.getAbilityCooldown() - 1;
            PacmanView pacman = pacmen.get(pacmanModel.getId());

            int width = pacman.cooldownBarBackground.getWidth();
            if (currentCooldown <= 0) {
                pacman.cooldownBar.setVisible(false);
                pacman.cooldownBarBackground.setVisible(false);
                cooldownToRemove.add(pacmanModel);
            } else {
                pacman.cooldownBar.setVisible(true);
                pacman.cooldownBarBackground.setVisible(true);
            }
            pacman.cooldownBar.setWidth((int) (((double) (totalCooldown - currentCooldown) / totalCooldown) * width));
        }

        cooldownLaunchers.removeAll(cooldownToRemove);
    }

    public void setPacMessage(Pacman pacModel) {
        PacmanView pac = pacmen.get(pacModel.getId());
        if (pacModel.getMessage() != null) {
            pac.message.setText(pacModel.getMessage());
            pac.message.setVisible(true);
        } else {
            pac.message.setVisible(false);
        }
        gem.commitEntityState(0, pac.message);
    }

    public void updateScores() {
        for (Player p : gameManager.getPlayers()) {
            BitmapText scoreLabel = scoreLabels.get(p.getIndex());
            scoreLabel.setText(String.valueOf(p.pellets));
        }
    }

    private void scaleStackedPacmen() {
        Map<Coord, List<Pacman>> pacByPosition = new HashMap<>();

        gameManager.getPlayers().stream()
            .flatMap(player -> player.getAlivePacmen())
            .forEach(pacModel -> {
                pacByPosition.computeIfAbsent(pacModel.getPosition(), key -> new ArrayList<>(totalPacmen));
                pacByPosition.get(pacModel.getPosition()).add(pacModel);
            });

        for (PacmanView pacman : pacmen) {
            pacman.group
                .setScaleX(1)
                .setScaleY(1)
                .setZIndex(Z_LAYER_PAC);
        }
        pacByPosition.values().stream()
            .forEach(list -> {
                int idx = 0;
                for (Pacman pacModel : list) {
                    PacmanView pac = pacmen.get(pacModel.getId());
                    double shrink = 1 - (idx * 0.1);
                    pac.group
                        .setZIndex(Z_LAYER_PAC + idx)
                        .setScaleX(shrink)
                        .setScaleY(shrink);

                    idx++;
                }

            });
    }

    public void endOfTurn() {
        performMoves();
        performBumps();
        performSwitches();
        performCooldownAnimation();
        performFlashes();
        // Goners that are not switchers animate differently
        goners.stream()
            .filter(e -> !switchers.containsKey(e))
            .filter(e -> !isAMover(e))
            .forEach(goner -> {
                animatePain(goner);
            });

        pacmen.stream()
            .filter(pac -> pac.model.isSpeeding() || pac.model.isEndOfSpeed())
            .forEach(pac -> updateTooltipText(pac));
        ;
        scaleStackedPacmen();

    }

    private boolean isAMover(Pacman e) {
        return movers.stream().anyMatch(m -> m.pacModel == e);
    }

    public void getAllPellets(Player player) {
        List<Pacman> alivePacmen = player.getAlivePacmen().collect(Collectors.toList());
        if (!alivePacmen.isEmpty()) {
            double maxDistance = new Coord(0, 0).euclideanTo(gridModel.getWidth(), gridModel.getHeight());
            pellets.forEach(
                (pos, pellet) -> {
                    if (pellet.isVisible()) {
                        Coord destination = alivePacmen.get(
                            random.nextInt(
                                alivePacmen.size()
                            )
                        ).getPosition();

                        double dist = pos.euclideanTo(destination);
                        setToGridCenterCoordinates(pellet, destination);

                        double t = Math.max(0, Math.min(1, dist / maxDistance));
                        gem.commitEntityState(t, pellet);
                    }
                }
            );
        }
    }

    public void startOfTurn() {
        switchers.clear();
        goners.clear();
        movers.clear();
        bumpers.clear();
        flashers.clear();

        for (PacmanView pacman : dying) {
            //Place dying pac below other pacs
            pacman.group.setZIndex(Z_LAYER_PAC - 1);
            gem.commitEntityState(0, pacman.group);

            pacman.message.setVisible(false);
            pacman.cooldownBar.setVisible(false);
            pacman.cooldownBarBackground.setVisible(false);
            pacman.death.setVisible(false);

            // Program the end of the dying animation
            pacman.deathFX
                .setVisible(true)
                .play();

            gem.commitEntityState(0.5, pacman.deathFX, pacman.death, pacman.message, pacman.cooldownBar, pacman.cooldownBarBackground);

            pacman.deathFX.setVisible(false);
        }
        dying.clear();

        for (PacmanView pacman : pacmen) {
            // By default, pacmen are still
            pacman.sprite.pause();
            gem.commitEntityState(0, pacman.sprite);
        }
    }

    public void chargeSpeed(Pacman pacModel) {
        PacmanView pacman = pacmen.get(pacModel.getId());
        ViewerEvent event = events.createAnimationEvent("play", 0);
        double x = convertXFromGridToAbsoluteCenter(pacModel.getPosition().getX());
        double y = convertYFromGridToAbsoluteCenter(pacModel.getPosition().getY());
        event.params.put("x", x);
        event.params.put("y", y);
        event.params.put("name", "charge");
        sendShake(pacman, 0, 1);

        PacmanView clone = pacmenWrapClones.get(pacman);
        pacman.plasmaBallContainer.setAlpha(1);
        clone.plasmaBallContainer.setAlpha(1);

    }

    public Sprite createTunnelMask(Coord coord, int cellSize, int padding, double cornerSize) {
        Sprite mask = gem.createSprite()
            .setAnchorX(0.5)
            .setBaseWidth(cellSize - padding)
            .setBaseHeight((int) (cellSize + 2 * cornerSize - padding));

        mask.setImage("tunnel_masque");
        if (coord.getX() == (grid.width - 1)) {
            mask.setScaleX(-1);
        } else if (coord.getX() == 0) {
            mask.setScaleX(1);
        }
        return mask;
    }

    public void endSpeed(Pacman pacModel) {
        PacmanView pacman = pacmen.get(pacModel.getId());
        PacmanView clone = pacmenWrapClones.get(pacman);
        pacman.plasmaBallContainer.setAlpha(0);
        clone.plasmaBallContainer.setAlpha(0);
    }
}
