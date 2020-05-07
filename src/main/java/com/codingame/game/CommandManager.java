package com.codingame.game;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.spring2020.Ability;
import com.codingame.spring2020.Config;
import com.codingame.spring2020.Coord;
import com.codingame.spring2020.Game;
import com.codingame.spring2020.Pacman;
import com.codingame.spring2020.PacmanType;
import com.codingame.spring2020.action.Action;
import com.codingame.spring2020.action.ActionType;
import com.codingame.spring2020.action.MoveAction;
import com.codingame.spring2020.action.SpeedAction;
import com.codingame.spring2020.action.SwitchAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CommandManager {

    @Inject private MultiplayerGameManager<Player> gameManager;

    static final Pattern PLAYER_ACTION_PATTERN = Pattern.compile(
        "^(WAIT|MOVE|SWITCH|SPEED|MSG)\\s+(?<id>\\d+).*",
        Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_WAIT_PATTERN = Pattern.compile(
        "^WAIT\\s+(?<id>\\d+)"
            + "(?:\\s+(?<message>.+))?"
            + "$",
        Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_MOVE_PATTERN = Pattern.compile(
        "^MOVE\\s+(?<id>\\d+)\\s+(?<x>-?\\d+)\\s+(?<y>-?\\d+)"
            + "(?:\\s+(?<message>.+))?"
            + "$",
        Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_SWITCH_TYPE_PATTERN = Pattern.compile(
        "^SWITCH\\s+(?<id>\\d+)\\s+(?<switchType>(ROCK|PAPER|SCISSORS))"
            + "(?:\\s+(?<message>.+))?"
            + "$",
        Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_SPEED_TYPE_PATTERN = Pattern.compile(
        "^SPEED\\s+(?<id>\\d+)"
            + "(?:\\s+(?<message>.+))?"
            + "$",
        Pattern.CASE_INSENSITIVE
    );

    static final Pattern PLAYER_MSG_TYPE_PATTERN = Pattern.compile(
        "^SMSG\\s+(?<message>.+)$",
        Pattern.CASE_INSENSITIVE
    );

    static String SWITCH_EXPECTED = "SWITCH <id> <type(ROCK|PAPER|SCISSORS)>";
    static String SPEED_EXPECTED = "SPEED <id>";
    static String MOVE_EXPECTED = "MOVE <id> <x> <y>";
    String EXPECTED = null;

    public void parseCommands(Player player, List<String> lines, Game game) {
        if (EXPECTED == null) {
            EXPECTED = MOVE_EXPECTED;
            if (Config.SPEED_ABILITY_AVAILABLE) EXPECTED += " or " + SPEED_EXPECTED;
            if (Config.SWITCH_ABILITY_AVAILABLE) EXPECTED += " or " + SWITCH_EXPECTED;
        }

        String[] commands = lines.get(0).split("\\|");
        for (String command : commands) {
            String cStr = command.trim();
            if (cStr.isEmpty()) {
                continue;
            }

            try {
                Pacman pac = null;
                Matcher match = PLAYER_ACTION_PATTERN.matcher(cStr);
                if (match.matches()) {
                    int pacNumber = Integer.parseInt(match.group("id"));
                    try {
                        pac = player.getAlivePacmen()
                            .filter((value) -> (value.getNumber() == pacNumber))
                            .findFirst()
                            .get();
                        if (pac.getIntent() != Action.NO_ACTION) {
                            throw new GameException(String.format("Pac %d cannot be commanded twice!", pacNumber));
                        }
                    } catch (NoSuchElementException e) {
                        throw new GameException(String.format("Pac %d doesn't exist", pacNumber));
                    }
                } else {
                    throw new InvalidInputException(EXPECTED, cStr);
                }

                match = PLAYER_MOVE_PATTERN.matcher(cStr);
                if (match.matches()) {
                    int x = Integer.valueOf(match.group("x"));
                    int y = Integer.valueOf(match.group("y"));
                    if (
                        !(0 <= x
                            && x < game.getGrid().getWidth()
                            && 0 <= y
                            && y < game.getGrid().getHeight())
                    ) {
                        throw new GameException(
                            String.format(
                                "Pac %d (%s) cannot reach its target (%d, %d) because it is out of grid!",
                                pac.getId(),
                                pac.getOwner().getColor(),
                                x,
                                y
                            )
                        );
                    }
                    Action intent = new MoveAction(new Coord(x, y), false);

                    handlePacmanCommand(pac, intent, match.group("message"));

                    continue;
                }

                match = PLAYER_SPEED_TYPE_PATTERN.matcher(cStr);
                if (match.matches()) {
                    Action intent = new SpeedAction();

                    handlePacmanCommand(pac, intent, match.group("message"));

                    continue;
                }
                match = PLAYER_SWITCH_TYPE_PATTERN.matcher(cStr);
                if (match.matches()) {
                    String switchType = String.valueOf(match.group("switchType"));
                    Action intent = new SwitchAction(PacmanType.valueOf(switchType.toUpperCase()));
                    handlePacmanCommand(pac, intent, match.group("message"));
                    continue;
                }

                match = PLAYER_WAIT_PATTERN.matcher(cStr);
                if (match.matches()) {
                    continue;
                }

                throw new InvalidInputException(EXPECTED, cStr);

            } catch (InvalidInputException | GameException e) {
                deactivatePlayer(player, e.getMessage());
                gameManager.addToGameSummary("Bad command: " + e.getMessage());
                return;
            } catch (Exception e) {
                deactivatePlayer(player, new InvalidInputException(e.toString(), EXPECTED, cStr).getMessage());
                gameManager.addToGameSummary("Bad command: " + e.getMessage());
                return;
            }
        }

        player.getAlivePacmen()
            .filter(pac -> pac.getIntent() == Action.NO_ACTION)
            .forEach(pac -> {
                pac.addToGameSummary(String.format(
                    "Pac %d received no command.", pac.getNumber()
                ));
            });
    }

    private void handlePacmanCommand(Pacman pac, Action intent, String message) {
        if (message != null && !message.trim().isEmpty()) {
            pac.setMessage(message);
        }
        pac.setIntent(intent);

        // set ability
        if (intent.getActionType() == ActionType.SPEED) {
            pac.setAbilityToUse(Ability.Type.SPEED);
        } else if (intent.getActionType() == ActionType.SWITCH) {
            PacmanType switchType = intent.getType();
            if (pac.getType() != switchType) {
                pac.setAbilityToUse(Ability.Type.fromType(switchType));
            } else {
                gameManager.addToGameSummary(
                    String.format("Pac %d is already in %s form!", pac.getNumber(), switchType)
                );
            }
        }
    }

    private void deactivatePlayer(Player player, String message) {
        player.deactivate(escapeHTMLEntities(message));
    }

    private String escapeHTMLEntities(String message) {
        return message
            .replace("&lt;", "<")
            .replace("&gt;", ">");
    }
}
