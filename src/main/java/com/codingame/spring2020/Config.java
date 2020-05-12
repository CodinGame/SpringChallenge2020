package com.codingame.spring2020;

import java.util.Properties;
import java.util.function.Function;

public class Config {  

    public static final Coord[] ADJACENCY = { new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1) };

    public static final int ID_ROCK = 0;
    public static final int ID_PAPER = 1;
    public static final int ID_SCISSORS = 2;
    public static final int ID_NEUTRAL = -1;

    public static final int CHERRY_SCORE = 10;

    public static int MAP_MIN_WIDTH = 28;
    public static int MAP_MAX_WIDTH = 33;
    public static int MAP_MIN_HEIGHT = 10;
    public static int MAP_MAX_HEIGHT = 15;
    
    public static int PACMAN_BASE_SPEED = 1;
    public static int SPEED_BOOST = 2;
    public static int ABILITY_DURATION = 6;
    public static int ABILITY_COOLDOWN = 10;

    public static int NUMBER_OF_CHERRIES;
    public static boolean FOG_OF_WAR;
    public static boolean MAP_WRAPS;
    public static boolean BODY_BLOCK;
    public static boolean FRIENDLY_BODY_BLOCK; // only applies if BODY_BLOCK is true
    public static boolean SPEED_ABILITY_AVAILABLE;
    public static boolean SWITCH_ABILITY_AVAILABLE;
    public static int MIN_PACS_PER_PLAYER = 2;
    public static int MAX_PACS_PER_PLAYER = 5;
    public static boolean PROVIDE_DEAD_PACS;

    public static void setDefaultValueByLevel(LeagueRules rules) {
        NUMBER_OF_CHERRIES = rules.numberOfCherries;
        FOG_OF_WAR = rules.forOfWar;
        MAP_WRAPS = rules.mapWraps;
        BODY_BLOCK = rules.bodyBlock;
        FRIENDLY_BODY_BLOCK = rules.friendlyBodyBlock;
        SPEED_ABILITY_AVAILABLE = rules.speedAbilityAvailable;
        SWITCH_ABILITY_AVAILABLE = rules.switchAbilityAvailable;
        MIN_PACS_PER_PLAYER = rules.minPacsPerPlayer;
        MAX_PACS_PER_PLAYER = rules.maxPacsPerPlayer;
        PROVIDE_DEAD_PACS = rules.provideDeadPacs;
    }

    public static void apply(Properties params) {
        MAP_MAX_WIDTH = getFromParams(params, "MAP_MAX_WIDTH", MAP_MAX_WIDTH);
        MAP_MIN_HEIGHT = getFromParams(params, "MAP_MIN_HEIGHT", MAP_MIN_HEIGHT);
        MAP_MAX_HEIGHT = getFromParams(params, "MAP_MAX_HEIGHT", MAP_MAX_HEIGHT);
        MAP_MIN_WIDTH = getFromParams(params, "MAP_MIN_WIDTH", MAP_MIN_WIDTH);
        ABILITY_DURATION = getFromParams(params, "ABILITY_DURATION", ABILITY_DURATION);
        ABILITY_COOLDOWN = getFromParams(params, "ABILITY_COOLDOWN", ABILITY_COOLDOWN);
        PACMAN_BASE_SPEED = getFromParams(params, "PACMAN_BASE_SPEED", PACMAN_BASE_SPEED);
        SPEED_BOOST = getFromParams(params, "SPEED_BOOST", SPEED_BOOST);
        NUMBER_OF_CHERRIES = getFromParams(params, "NUMBER_OF_CHERRIES", NUMBER_OF_CHERRIES);
        FOG_OF_WAR = getFromParams(params, "FOG_OF_WAR", FOG_OF_WAR);
        MAP_WRAPS = getFromParams(params, "MAP_WRAPS", MAP_WRAPS);
        BODY_BLOCK = getFromParams(params, "BODY_BLOCK", BODY_BLOCK);
        FRIENDLY_BODY_BLOCK = getFromParams(params, "FRIENDLY_BODY_BLOCK", FRIENDLY_BODY_BLOCK);
        SPEED_ABILITY_AVAILABLE = getFromParams(params, "SPEED_ABILITY_AVAILABLE", SPEED_ABILITY_AVAILABLE);
        SWITCH_ABILITY_AVAILABLE = getFromParams(params, "SWITCH_ABILITY_AVAILABLE", SWITCH_ABILITY_AVAILABLE);
        MIN_PACS_PER_PLAYER = getFromParams(params, "MIN_PACS_PER_PLAYER", MIN_PACS_PER_PLAYER);
        MAX_PACS_PER_PLAYER = getFromParams(params, "MAX_PACS_PER_PLAYER", MAX_PACS_PER_PLAYER);
        PROVIDE_DEAD_PACS = getFromParams(params, "PROVIDE_DEAD_PACS", PROVIDE_DEAD_PACS);
    }

    private static int getFromParams(Properties params, String name, int defaultValue) {
        return getFromParams(params, name, defaultValue, Integer::parseInt);
    }

    private static boolean getFromParams(Properties params, String name, boolean defaultValue) {
        return getFromParams(params, name, defaultValue, Boolean::parseBoolean);
    }

    private static <T> T getFromParams(Properties params, String name, T defaultValue, Function<String, T> create) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return create.apply(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

}
