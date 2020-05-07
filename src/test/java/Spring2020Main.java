import java.util.Properties;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Spring2020Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        // Select agents here
        gameRunner.addAgent("python3 config/Boss.py", "Blinky", "https://static.codingame.com/servlet/fileservlet?id=43829808065962");
        gameRunner.addAgent("python3 config/Boss.py", "Inky", "https://static.codingame.com/servlet/fileservlet?id=43829821541064");

        gameRunner.setSeed(5842184981578562716L);

        Properties params = new Properties();

        gameRunner.setGameParameters(params);
        gameRunner.setLeagueLevel(3);

        gameRunner.start(8888);
    }
}
