package loderunner.contracts;

import java.util.List;

import loderunner.contracts.errors.InvariantError;
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
        // inv: getLevelScore() >= getScore()
        if(getLevelScore() < getScore())
            throw new InvariantError("Game", "getLevelScore() >= getScore()");
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
        // post: getEngine() == Engine::init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
        //                                   levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords())
        Engine eng = new EngineImpl(new NeutralCommandProvider());
        eng.init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
                 levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords());
        if(!getEngine().equals(eng))
            throw new PostconditionError("Game", "init", "The engine is not as it should be");
        // post: getScore() == getLevelScore() == 0
        if(getScore() != 0 || getLevelScore() != 0)
            throw new PostconditionError("Game", "init", "getScore() == getLevelScore() == 0");
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
        int levelScore_pre = getLevelScore();
        int levelIndex_pre = getLevelIndex();
        Engine engine_pre = getEngine().clone();

        // run
        super.checkStateAndUpdate();

        // post-invariant
        checkInvariant();

        // post: getEngine().getStatus()@pre == Loss
        //       => (getLevelScore() == getScore() &&
        //           getHP() = getHP()@pre - 1 &&
        //           getEngine().equals(
        //           Engine::init(levels.get(getLevelIndex()).getScreen(), levels.get(getLevelIndex()).getPlayerCoord(),
        //                        levels.get(getLevelIndex()).getGuardCoords(), levels.get(getLevelIndex()).getTreasureCoords())))
        // TODO
        // post: getEngine().getStatus() != Loss => getHP() == getHP()@pre
        if(status_pre != Status.Loss && getHP() != hp_pre)
            throw new PostconditionError("Game", "checkAndUpdate", "hp has changed while it shouldn't have");
        // post: getEngine().getStatus()@pre == Win
        //       => (getScore() == getLevelScore() &&
        //           getLevelIndex() == getLevelIndex()@pre + 1 &&
        //           (getLevelIndex() < getLevels().size() => getEngine().equals(
        //            Engine::init(levels.get(getLevelIndex()).getScreen(), levels.get(getLevelIndex()).getPlayerCoord(),
        //                         levels.get(getLevelIndex()).getGuardCoords(), levels.get(getLevelIndex()).getTreasureCoords()))))
        // TODO
        // post: getEngine().getStatus()@pre != Win => getLevelIndex() == getLevelIndex()@pre && getScore() == getScore()@pre
        if(status_pre != Status.Win &&
           (getLevelIndex() != levelIndex_pre || getScore() != score_pre))
            throw new PostconditionError("Game", "checkAndUpdate", "levelIndex or score has changed while it shouldn't have");
        // post: getEngine().getStatus()@pre == Playing => getEngine().equals(getEngine()@pre)
        if(status_pre == Status.Playing && !getEngine().equals(engine_pre))
            throw new PostconditionError("Game", "checkAndUpdate", "engine has changed while it shouldn't have");
        // post: \exists Item i \in getEngines().getTreasures()@pre
        //         (i.getCol() == getEngine().getPlayer().getCol() && i.getHgt() == getEngine().getPlayer().getHgt())
        //       => getLevelScore() == getLevelScore()@pre + 1
        // TODO
        // post: (\not \exists Item i \in getEngines().getTreasures()@pre
        //             (i.getCol() == getEngine().getPlayer().getCol() && i.getHgt() == getEngine().getPlayer().getHgt())
        //        && getEngine().getStatus() != Loss)
        //       => getLevelScore() == getLevelScore()@pre
        // TODO
    }
}
