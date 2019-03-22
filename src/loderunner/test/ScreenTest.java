package loderunner.test;

import loderunner.impl.ScreenImpl;
import loderunner.contracts.ScreenContract;

public class ScreenTest extends AbstractScreenTest {
    @Override
    public void beforeTests() {
        setScreen(new ScreenContract(new ScreenImpl()));
    }
}
