package loderunner.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import loderunner.impl.GameImpl;
import loderunner.io.LevelLoadException;
import loderunner.services.Game;
import loderunner.services.Level;
import loderunner.windows.GameWindow;

public class Main extends Application {
    private GameWindow window;

    @Override
    public void start(Stage stage) throws Exception {
        window = new GameWindow(stage);

        // Load levels
        List<String> params = getParameters().getUnnamed();
        List<Level> levels = new ArrayList<>();
        for(String lfn: params) {
            File lf = new File(lfn);
            try {
                levels.add(new loderunner.io.Level(lf));
            } catch(LevelLoadException e) {
                System.out.println(e);
            }
        }

        if(levels.size() == 0) {
            System.err.println("No level could be loaded, aborting");
            stage.close();
            return;
        }

        GameRunner gm = new GameRunner(window, levels);
        gm.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    class GameRunner extends Thread {
        private GameWindow window;
        private List<Level> levels;

        public GameRunner(GameWindow window, List<Level> levels) {
            this.window = window;
            this.levels = levels;
        }

        @Override
        public void run() {
            Game game = new GameImpl(window.getCommandProvider());
            game.init(levels);

            while(game.getHP() > 0 && game.getLevelIndex() < game.getLevels().size()) {
                game.getEngine().step();
                game.checkStateAndUpdate();
                Platform.runLater(() -> {
                        window.redrawCanvas(game.getEngine());
                        window.redrawHp(game.getHP());
                        window.redrawScore(game.getScore()+game.getEngine().getLevelScore());
                    });
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {}
            }

            // Show end of game screen
            Platform.runLater(() -> {
                    window.drawGameOver(game.getScore());
                });
        }
    }
}
