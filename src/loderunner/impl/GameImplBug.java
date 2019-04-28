package loderunner.impl;

import java.util.List;

import loderunner.services.CommandProvider;
import loderunner.services.Engine;
import loderunner.services.Game;
import loderunner.services.Level;
import loderunner.services.Status;

public class GameImplBug implements Game {
    private CommandProvider cp;
    private List<Level> levels;
    private Engine engine;
    private int levelIndex;
    private int score;
    private int hp;

    public GameImplBug(CommandProvider cp) {
        this.cp = cp;
    }

    @Override
    public List<Level> getLevels() {
        return levels;
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public int getLevelIndex() {
        return levelIndex;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getHP() {
        return hp;
    }

    @Override
    public void init(List<Level> levels) {
        this.levels = levels;
        levelIndex = 0;
        engine = new EngineImpl(cp);
        engine.init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
                    levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords(),
                    levels.get(0).getKeyCoords(), levels.get(0).getPortals());
        score = 0;
        hp = 3;
    }

    @Override
    public void checkStateAndUpdate() {
        // Won the game !
        if(getEngine().getStatus() == Status.Win) {
            score += getEngine().getLevelScore();
            // levelIndex++; // Oups
            if(getLevelIndex() < getLevels().size()) {
                engine.init(levels.get(getLevelIndex()).getScreen(),
                            levels.get(getLevelIndex()).getPlayerCoord(),
                            levels.get(getLevelIndex()).getGuardCoords(),
                            levels.get(getLevelIndex()).getTreasureCoords(),
                            levels.get(getLevelIndex()).getKeyCoords(),
                            levels.get(getLevelIndex()).getPortals());
            }
        }

        // Lost the game...
        if(getEngine().getStatus() == Status.Loss) {
            hp--;
            if(getLevelIndex() < getLevels().size()) {
                engine.init(levels.get(getLevelIndex()).getScreen(),
                            levels.get(getLevelIndex()).getPlayerCoord(),
                            levels.get(getLevelIndex()).getGuardCoords(),
                            levels.get(getLevelIndex()).getTreasureCoords(),
                            levels.get(getLevelIndex()).getKeyCoords(),
                            levels.get(getLevelIndex()).getPortals());
            }
        }
    }
}
