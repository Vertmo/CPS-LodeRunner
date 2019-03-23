package loderunner.test;

import loderunner.impl.EnvironmentImplBug;
import loderunner.contracts.EnvironmentContract;

public class EnvironmentTestBug extends AbstractEnvironmentTest {
    @Override
    public void beforeTests() {
        setScreen(new EnvironmentContract(new EnvironmentImplBug()));
    }
}
