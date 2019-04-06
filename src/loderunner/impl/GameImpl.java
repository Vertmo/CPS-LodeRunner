package loderunner.impl;

import java.util.List;

import loderunner.services.CommandProvider;
import loderunner.services.Engine;
import loderunner.services.Game;
import loderunner.services.Level;

public class GameImpl implements Game {
    private CommandProvider cp;
    private List<Level> levels;
    private Engine engine;
    private int levelIndex;
    private int score;
    private int levelScore;
    private int hp;

    public GameImpl(CommandProvider cp) {
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
    public int getLevelScore() {
        return levelScore;
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
                    levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords());
        score = 0;
        levelScore = 0;
        hp = 3;
    }

    @Override
    public void checkStateAndUpdate() {
        // TODO Auto-generated method stub
    }
}