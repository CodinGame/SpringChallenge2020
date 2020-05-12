package com.codingame.spring2020;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Player;
import com.codingame.spring2020.action.Action;

public class Pacman {
    private Player owner;
    private int id;
    private int number;
    private String message;

    private Coord position;
    private int speed = 1;
    private int abilityDuration = 0;
    private int abilityCooldown = 0;
    private boolean endOfSpeed = false;

    private Action intent;
    private Ability.Type abilityToUse;
    private List<Coord> intendedPath = new ArrayList<Coord>();
    private boolean pathResolved = false;
    private String warningPathMessage;

    private PacmanType type;
    private int currentPathStep;
    private int previousPathStep;
    private boolean blocked;
    private boolean dead = false;
    private List<String> gameSummary = new ArrayList<>();

    public Pacman(int id, int number, Player owner, PacmanType type) {
        this.owner = owner;
        this.id = id;
        this.number = number;
        this.setType(type);
    }

    public void setMessage(String message) {
        this.message = message;
        if (message != null && message.length() > 48) {
            this.message = message.substring(0, 46) + "...";
        }
    }

    public void setPosition(Coord coord) {
        this.position = coord;
    }

    public Coord getPosition() {
        return position;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAbilityDuration() {
        return abilityDuration;
    }

    public void setAbilityDuration(int abilityDuration) {
        this.abilityDuration = abilityDuration;
    }

    public void tickAbilityDuration() {
        if (abilityDuration > 0) {
            abilityDuration--;
            endOfSpeed = abilityDuration == 0;
        } else {
            endOfSpeed = false;
        }
    }

    public int getAbilityCooldown() {
        return abilityCooldown;
    }

    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }

    public void tickAbilityCooldown() {
        if (abilityCooldown > 0) {
            abilityCooldown--;
        }
    }

    public void turnReset() {
        message = null;
        if (!isDead()) {
            tickAbilityDuration();
            tickAbilityCooldown();
        }
        setAbilityToUse(null);
        setCurrentPathStep(0);
        blocked = false;
        this.intent = Action.NO_ACTION;
    }

    public boolean gotBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public Player getOwner() {
        return owner;
    }

    public String getMessage() {
        return message;
    }

    public Action getIntent() {
        return intent;
    }

    public void setIntent(Action intent) {
        this.intent = intent;
    }

    public Ability.Type getAbilityToUse() {
        return abilityToUse;
    }

    public void setAbilityToUse(Ability.Type abilityToUse) {
        this.abilityToUse = abilityToUse;
    }

    public List<Coord> getIntendedPath() {
        return intendedPath;
    }

    public void setIntendedPath(List<Coord> intendedPath) {
        this.intendedPath = intendedPath;
    }

    public void clearIntendedPath() {
        this.intendedPath.clear();
    }

    public boolean isPathResolved() {
        return pathResolved;
    }

    public void setPathResolved(boolean pathResolved) {
        this.pathResolved = pathResolved;
    }

    public PacmanType getType() {
        return type;
    }

    public void setType(PacmanType type) {
        this.type = type;
    }

    public void setCurrentPathStep(int step) {
        setPreviousPathStep(currentPathStep);
        currentPathStep = step;

    }

    public boolean moveFinished() {
        return getCurrentPathStep() == intendedPath.size() - 1;
    }

    public int getCurrentPathStep() {
        return currentPathStep;
    }

    public int getPreviousPathStep() {
        return previousPathStep;
    }

    public void setPreviousPathStep(int previousPathStep) {
        this.previousPathStep = previousPathStep;
    }

    public boolean fastEnoughToMoveAt(int step) {
        return speed > step;
    }

    public boolean isSpeeding() {
        return speed == Config.SPEED_BOOST;
    }

    public String getWarningPathMessage() {
        return warningPathMessage;
    }

    public void setWarningPathMessage(String warningPathMessage) {
        this.warningPathMessage = warningPathMessage;
    }

    public void clearGameSummary() {
        gameSummary = new ArrayList<>();
    }

    public List<String> getGameSummary() {
        return gameSummary;
    }

    public void addToGameSummary(String message) {
        this.gameSummary.add(message);
    }

    public void setDead() {
        this.dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isEndOfSpeed() {
        return endOfSpeed;
    }
}
