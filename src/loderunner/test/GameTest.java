package loderunner.test;

import loderunner.contracts.GameContract;
import loderunner.impl.GameImpl;

public class GameTest extends AbstractGameTest {

    @Override
    public void beforeTests() {
        TestCommandProvider tcp = new TestCommandProvider();
        setCommandProvider(tcp);
        setGame(new GameContract(new GameImpl(tcp)));
    }
}
