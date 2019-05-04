package loderunner.contracts;

import java.util.List;

import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.decorators.GameDecorator;
import loderunner.impl.EngineImpl;
import loderunner.impl.NeutralCommandProvider;
import loderunner.services.Engine;
import loderunner.services.Game;
import loderunner.services.Level;
import loderunner.services.Status;

public class GameContract extends GameDecorator {

    public GameContract(Game delegate) {
        super(delegate);
    }

    public void checkInvariant() {
        // rien ?
    }

    @Override
    public void init(List<Level> levels) {
        // pre: !levels.isEmpty()
        if(levels.isEmpty())
            throw new PreconditionError("Game", "init", "!levels.isEmpty()");

        // run
        super.init(levels);

        // post-invariant
        checkInvariant();

        // post: getLevels() == levels
        if(getLevels() != levels)
            throw new PostconditionError("Game", "init", "getLevels() == levels");
        // post: getLevelIndex() == 0
        if(getLevelIndex() != 0)
            throw new PostconditionError("Game", "init", "getLevelIndex() != 0");
        // post: getEngine().equals(Engine::init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
        //                                       levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords(),
        //                                       levels.get(0).getKeyCoords(), levels.get(0).getPortals(),
        //                                       levels.get(0).getGunCoords())
        Engine eng = new EngineImpl(new NeutralCommandProvider());
        eng.init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
                 levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords(),
                 getLevels().get(0).getKeyCoords(), getLevels().get(0).getPortals(),
                 getLevels().get(0).getGunCoords());
        if(!getEngine().equals(eng))
            throw new PostconditionError("Game", "init", "The engine is not as it should be");
        // post: getScore() == 0
        if(getScore() != 0)
            throw new PostconditionError("Game", "init", "getScore() == 0");
        // post: getHP() == 3
        if(getHP() != 3)
            throw new PostconditionError("Game", "init", "getHP() == 3");
    }

    @Override
    public void checkStateAndUpdate() {
        // pre: getLevelIndex() < getLevels().size()
        if(getLevelIndex() >= getLevels().size())
            throw new PreconditionError("Game", "checkStateAndUpdate", "getLevelIndex() < getLevels().size()");
        // pre: getHP() > 0
        if(getHP() <= 0)
            throw new PreconditionError("Game", "checkStateAndUpdate()", "getHP() > 0");

        // pre-invariant
        checkInvariant();

        // captures
        Status status_pre = getEngine().getStatus();
        int hp_pre = getHP();
        int score_pre = getScore();
        int levelScore_pre = getEngine().getLevelScore();
        int levelIndex_pre = getLevelIndex();
        Engine engine_pre = getEngine().clone();

        // run
        super.checkStateAndUpdate();

        // post-invariant
        checkInvariant();

        // post: getEngine().getStatus()@pre == Loss
        //       => (getHP() == getHP()@pre - 1 &&
        //           getEngine().equals(
        //           Engine::init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
        //                        getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords())))
        if(status_pre == Status.Loss) {
            if(getHP() != hp_pre-1)
                throw new PostconditionError("Game", "checkAndUpdate", "the hp has not evolved as planned");
            if(getLevelIndex() < getLevels().size()) {
                Engine eng = new EngineImpl(new NeutralCommandProvider());
                eng.init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
                         getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords(),
                         getLevels().get(getLevelIndex()).getKeyCoords(), getLevels().get(getLevelIndex()).getPortals(),
                         getLevels().get(0).getGunCoords());
                if(!getEngine().equals(eng))
                    throw new PostconditionError("Game", "checkAndUpdate", "the engine was not correctly updated");
            }
        }
        // post: getEngine().getStatus()@pre != Loss => getHP() == getHP()@pre
        if(status_pre != Status.Loss && getHP() != hp_pre)
            throw new PostconditionError("Game", "checkAndUpdate", "hp has changed while it shouldn't have");
        // post: getEngine().getStatus()@pre == Win
        //       => (getScore() == getScore()@pre + getEngine.getLevelScore()@pre &&
        //           getLevelIndex() == getLevelIndex()@pre + 1 &&
        //           (getLevelIndex() < getLevels().size() => getEngine().equals(
        //            Engine::init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
        //                         getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords(),
        //                         getLevels().get(getLevelIndex()).getKeyCoords(), getLevels().get(getLevelIndex()).getPortals(),
        //                         getLevels().get(getLevelIndex()).getGunCoords()))))
        if(status_pre == Status.Win) {
            if(!(getScore() == score_pre + levelScore_pre &&
                 getLevelIndex() == levelIndex_pre+1))
                throw new PostconditionError("Game", "checkAndUpdate", "the score or level index has not evolved as planned");
            if(getLevelIndex() < getLevels().size()) {
                Engine eng = new EngineImpl(new NeutralCommandProvider());
                eng.init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
                         getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords(),
                         getLevels().get(getLevelIndex()).getKeyCoords(), getLevels().get(getLevelIndex()).getPortals(),
                         getLevels().get(getLevelIndex()).getGunCoords());
                if(!getEngine().equals(eng))
                    throw new PostconditionError("Game", "checkAndUpdate", "the engine was not correctly updated");
            }
        }
        // post: getEngine().getStatus()@pre != Win => getLevelIndex() == getLevelIndex()@pre && getScore() == getScore()@pre
        if(status_pre != Status.Win &&
           (getLevelIndex() != levelIndex_pre || getScore() != score_pre))
            throw new PostconditionError("Game", "checkAndUpdate", "levelIndex or score has changed while it shouldn't have");
        // post: getEngine().getStatus()@pre == Playing => getEngine().equals(getEngine()@pre)
        if(status_pre == Status.Playing && !getEngine().equals(engine_pre))
            throw new PostconditionError("Game", "checkAndUpdate", "engine has changed while it shouldn't have");
    }
}
