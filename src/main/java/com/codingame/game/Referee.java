package com.codingame.game;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.spring2020.Config;
import com.codingame.spring2020.Game;
import com.codingame.spring2020.LeagueRules;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Referee extends AbstractReferee {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private CommandManager commandManager;
    @Inject private Game game;
    @Inject private EndScreenModule endScreenModule;

    long seed;
    boolean gameOverFrame;

    @Override
    public void init() {
        gameOverFrame = false;

        this.seed = gameManager.getSeed();

        // Set configuration depending on game rules:
        Config.setDefaultValueByLevel(LeagueRules.fromIndex(gameManager.getLeagueLevel()));

        // Override configuration with game parameters:
        if (System.getProperty("allow.config.override") != null) {
            computeConfiguration(gameManager.getGameParameters());
        }

        try {
            gameManager.setFrameDuration(500);
            gameManager.setMaxTurns(gameManager.getPlayerCount() > 3 ? 100 : 200);
            gameManager.setTurnMaxTime(50);

            game.init(seed);
            sendGlobalInfo();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee failed to initialize");
            abort();
        }
    }

    private void computeConfiguration(Properties gameParameters) {
        Config.apply(gameParameters);
    }

    private void abort() {
        System.err.println("Unexpected game end");
        gameManager.endGame();
    }

    private void sendGlobalInfo() {
        for (Player player : gameManager.getActivePlayers()) {
            for (String line : game.getGlobalInfoFor(player)) {
                player.sendInputLine(line);
            }
        }
    }

    @Override
    public void gameTurn(int turn) {
        if (!gameOverFrame) {
            if (game.isSpeedTurn()) {
                game.performGameSpeedUpdate();
            } else {
                game.resetGameTurnData();

                // Give input to players
                for (Player player : gameManager.getActivePlayers()) {
                    for (String line : game.getCurrentFrameInfoFor(player)) {
                        player.sendInputLine(line);
                    }
                    player.execute();
                }
                // Get output from players
                handlePlayerCommands();

                game.performGameUpdate();
            }

            if (game.isGameOver()) {
                gameOverFrame = true;
            }
        } else {
            game.resetGameTurnData();
            game.performGameUpdate();
            game.performGameOver();
            gameManager.endGame();
        }
    }

    private void handlePlayerCommands() {
        for (Player player : gameManager.getActivePlayers()) {
            try {
                commandManager.parseCommands(player, player.getOutputs(), game);
            } catch (TimeoutException e) {
                player.deactivate("Timeout!");
                player.setTimedOut(true);
                gameManager.addToGameSummary(player.getNicknameToken() + " has not provided " + player.getExpectedOutputLines() + " lines in time");
            }
        }

    }

    static public String join(Object... args) {
        return Stream.of(args).map(String::valueOf).collect(Collectors.joining(" "));
    }

    @Override
    public void onEnd() {
        gameManager.getPlayers().forEach(player -> player.setScore(player.pellets));
        printWinner();
        endScreenModule.setScores(
            gameManager.getPlayers()
                .stream()
                .mapToInt(player -> player.getScore())
                .toArray(),

            gameManager.getPlayers()
                .stream()
                .map(player -> {
                    if (player.isTimedOut()) {
                        return String.format("%d pellets (timed out)", player.pellets);
                    } else {
                        return String.format("%d pellets", player.pellets);
                    }
                })
                .collect(Collectors.toList())
                .toArray(new String[2])
        );
    }

    private void printWinner() {
        int maxScore = gameManager.getPlayers()
            .stream()
            .mapToInt(player -> player.pellets)
            .max()
            .getAsInt();

        List<Player> players = gameManager.getPlayers()
            .stream()
            .filter(player -> player.pellets == maxScore)
            .collect(Collectors.toList());

        if (players.size() > 1) {
            gameManager.addToGameSummary(
                String.format(
                    "Game tied! Each player has %d pellet" + (maxScore == 1 ? "" : "s"),
                    maxScore
                )
            );
        } else {
            Player player = players.get(0);
            gameManager.addToGameSummary(
                String.format(
                    "%s wins with %d pellet" + (player.pellets == 1 ? "" : "s"),
                    player.getNicknameToken(),
                    player.pellets
                )
            );
        }
    }

}
