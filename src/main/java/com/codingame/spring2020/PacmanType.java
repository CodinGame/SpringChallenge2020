package com.codingame.spring2020;

import java.util.stream.Stream;

public enum PacmanType {
    ROCK(Config.ID_ROCK), PAPER(Config.ID_PAPER), SCISSORS(Config.ID_SCISSORS), NEUTRAL(Config.ID_NEUTRAL);

    int id;

    private PacmanType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PacmanType fromId(int id) {
        return Stream.of(values())
            .filter(type -> type.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public static PacmanType fromCharacter(char c) {
        switch (c) {
        case 'r':
        case 'R':
            return ROCK;
        case 'p':
        case 'P':
            return PAPER;
        case 's':
        case 'S':
            return SCISSORS;
        case 'n':
        case 'N':
            return NEUTRAL;

        }
        throw new RuntimeException(c + " is not a valid pac type");
    }
}
