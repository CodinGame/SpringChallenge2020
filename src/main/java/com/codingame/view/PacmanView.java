package com.codingame.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.SpriteAnimation;
import com.codingame.gameengine.module.entities.TextBasedEntity;
import com.codingame.spring2020.Pacman;
import com.codingame.spring2020.PacmanType;

public class PacmanView {

    private static class PacmanViewState {
        boolean alive;
        int playerIndex;
        PacmanType type;

        public PacmanViewState(int playerIndex, PacmanType type, boolean alive) {
            this.alive = alive;
            this.playerIndex = playerIndex;
            this.type = type;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PacmanViewState other = (PacmanViewState) obj;
            if (alive != other.alive) return false;
            if (playerIndex != other.playerIndex) return false;
            if (type != other.type) return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (alive ? 1231 : 1237);
            result = prime * result + playerIndex;
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }
    }

    private static final int DEATH_FRAME_INDEX_END = 8;

    private static Integer[] WALK_CYCLE = { 1, 2, 3, 4, 3, 2 };

    static Map<PacmanViewState, String[]> stateToImages = new HashMap<>();
    static {
        for (int idx = 0; idx < 2; ++idx) {
            for (PacmanType type : PacmanType.values()) {
                stateToImages.put(new PacmanViewState(idx, type, true), getWalkPacmanImages(idx, type, true));
                stateToImages.put(new PacmanViewState(idx, type, false), getWalkPacmanImages(idx, type, false));
            }
        }
    }

    private static String folderNameFromType(PacmanType type) {
        switch (type) {
        case NEUTRAL:
            return "1";
        case SCISSORS:
            return "2";
        case PAPER:
            return "3";
        case ROCK:
            return "4";
        }
        throw new RuntimeException("Unrecognised pac type: " + type);
    }

    public static String[] getDeathPacmanImages(int playerIndex, PacmanType type) {
        String indexPacmanType = folderNameFromType(type);
        String color = playerIndex == 0 ? "red" : "blue";

        List<String> deathPacmanImages = new ArrayList<String>();
        for (int i = 1; i < DEATH_FRAME_INDEX_END; i++) {
            deathPacmanImages.add(
                String.format(
                    "mort_%s_%s_mort%04d",
                    color,
                    indexPacmanType,
                    i
                )
            );
        }
        return deathPacmanImages.toArray(new String[0]);
    }

    private static String[] getWalkPacmanImages(int playerIndex, PacmanType type, boolean alive) {
        String indexPacmanType = folderNameFromType(type);
        String color = playerIndex == 0 ? "red" : "blue";

        List<String> walkPacmanImages = new ArrayList<String>();
        for (int i = 0; i < WALK_CYCLE.length; i++) {
            String extension = alive ? "" : "_2";
            walkPacmanImages.add(
                "paku_" + color + "_" + indexPacmanType + "_walk000" + WALK_CYCLE[i] + extension
            );
        }
        return walkPacmanImages.toArray(new String[0]);
    }

    Group group;

    SpriteAnimation sprite, death;
    TextBasedEntity<?> message;

    SpriteAnimation switchFX, deathFX;

    Group rotationWrapper;
    Rectangle cooldownBar;
    Rectangle cooldownBarBackground;
    
    Pacman model;

    Group plasmaBallContainer;

    public void setViewState(int playerIndex, PacmanType type, boolean alive) {
        sprite.setImages(stateToImages.get(new PacmanViewState(playerIndex, type, alive)));
        if (!alive && death != null) {
            death.setImages(getDeathPacmanImages(playerIndex, type));
        }
    }
    
    public PacmanView(Pacman model) {
        this.model = model;
    }

}
