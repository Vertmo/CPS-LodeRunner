package loderunner.test;

import loderunner.impl.ScreenImplBug;
import loderunner.contracts.ScreenContract;

public class ScreenTestBug extends AbstractScreenTest {
    @Override
    public void beforeTests() {
        screen = new ScreenContract(new ScreenImplBug());
    }
}
