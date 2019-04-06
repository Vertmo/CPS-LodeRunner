package loderunner.services;

import java.util.List;

public interface Game {
    /* Observators */

    // const
    public List<Level> getLevels();

    public Engine getEngine();
    public int getLevelIndex();
    public int getScore();
    public int getLevelScore();
    public int getHP();

    /* Invariants */

    // inv: getLevelScore() >= getScore()

    /* Constructors */

    // pre: !levels.isEmpty()
    // post: getLevels() == levels
    // post: getLevelIndex() == 0
    // post: getEngine().equals(Engine::init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
    //                                       levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords()))
    // post: getScore() == getLevelScore() == 0
    // post: getHP() == 3
    void init(List<Level> levels);

    /* Operators */

    // pre: getLevelIndex() < getLevels().size()
    // pre: getHP() > 0
    // post: getEngine().getStatus()@pre == Loss
    //       => (getLevelScore() == getScore() &&
    //           getHP() = getHP()@pre - 1 &&
    //           getEngine() ==
    //           Engine::init(levels.get(getLevelIndex()).getScreen(), levels.get(getLevelIndex()).getPlayerCoord(),
    //                        levels.get(getLevelIndex()).getGuardCoords(), levels.get(getLevelIndex()).getTreasureCoords()))
    // post: getEngine().getStatus() != Loss => getHP() == getHP()@pre
    // post: getEngine().getStatus()@pre == Win
    //       => (getScore() == getLevelScore() &&
    //           getLevelIndex() == getLevelIndex()@pre + 1 &&
    //           (getLevelIndex() < getLevels().size() => getEngine().equals(
    //            Engine::init(levels.get(getLevelIndex()).getScreen(), levels.get(getLevelIndex()).getPlayerCoord(),
    //                         levels.get(getLevelIndex()).getGuardCoords(), levels.get(getLevelIndex()).getTreasureCoords()))))
    // post: getEngine().getStatus()@pre != Win => getLevelIndex() == getLevelIndex()@pre && getScore() == getScore()@pre
    // post: getEngine().getStatus()@pre == Playing => getEngine().equals(getEngine()@pre)
    // post: \exists Item i \in getEngines().getTreasures()@pre
    //         (i.getCol() == getEngine().getPlayer().getCol() && i.getHgt() == getEngine().getPlayer().getHgt())
    //       => getLevelScore() == getLevelScore()@pre + 1
    // post: (\not \exists Item i \in getEngines().getTreasures()@pre
    //             (i.getCol() == getEngine().getPlayer().getCol() && i.getHgt() == getEngine().getPlayer().getHgt())
    //        && getEngine().getStatus() != Loss)
    //       => getLevelScore() == getLevelScore()@pre
    void checkStateAndUpdate();
}
