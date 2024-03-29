package loderunner.services;

import java.util.List;

public interface Game {
    /* Observators */

    // const
    public List<Level> getLevels();

    public Engine getEngine();
    public int getLevelIndex();
    public int getScore();
    public int getHP();

    /* Constructors */

    // pre: !levels.isEmpty()
    // post: getLevels() == levels
    // post: getLevelIndex() == 0
    // post: getEngine().equals(Engine::init(levels.get(0).getScreen(), levels.get(0).getPlayerCoord(),
    //                                       levels.get(0).getGuardCoords(), levels.get(0).getTreasureCoords(),
    //                                       levels.get(0).getKeyCoords(), levels.get(0).getPortals(),
    //                                       levels.get(0).getGunCoords())
    // post: getScore() == 0
    // post: getHP() == 3
    void init(List<Level> levels);

    /* Operators */

    // pre: getLevelIndex() < getLevels().size()
    // pre: getHP() > 0
    // post: getEngine().getStatus()@pre == Loss
    //       => (
    //           getHP() = getHP()@pre - 1 &&
    //           getEngine().equals(
    //           Engine::init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
    //                        getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords())))
    // post: getEngine().getStatus()@pre != Loss => getHP() == getHP()@pre
    // post: getEngine().getStatus()@pre == Win
    //       => (getScore() == getScore()@pre + getEngine.getLevelScore()@pre &&
    //           getLevelIndex() == getLevelIndex()@pre + 1 &&
    //           (getLevelIndex() < getLevels().size() => getEngine().equals(
    //            Engine::init(getLevels().get(getLevelIndex()).getScreen(), getLevels().get(getLevelIndex()).getPlayerCoord(),
    //                         getLevels().get(getLevelIndex()).getGuardCoords(), getLevels().get(getLevelIndex()).getTreasureCoords(),
    //                         getLevels().get(getLevelIndex()).getKeyCoords(), getLevels().get(getLevelIndex()).getPortals(),
    //                         getLevels().get(getLevelIndex()).getGunCoords()))))
    // post: getEngine().getStatus()@pre != Win => getLevelIndex() == getLevelIndex()@pre && getScore() == getScore()@pre
    // post: getEngine().getStatus()@pre == Playing => getEngine().equals(getEngine()@pre)
    void checkStateAndUpdate();
}
