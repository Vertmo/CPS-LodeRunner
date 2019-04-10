package loderunner.test;

import loderunner.contracts.GameContract;
import loderunner.impl.GameImplBug;

public class GameTestBug extends AbstractGameTest {

    @Override
    public void beforeTests() {
        TestCommandProvider tcp = new TestCommandProvider();
        setCommandProvider(tcp);
        setGame(new GameContract(new GameImplBug(tcp)));
    }
}
