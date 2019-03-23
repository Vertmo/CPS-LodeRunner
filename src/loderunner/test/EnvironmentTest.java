package loderunner.test;

import loderunner.impl.EnvironmentImpl;
import loderunner.contracts.EnvironmentContract;

public class EnvironmentTest extends AbstractEnvironmentTest {
    @Override
    public void beforeTests() {
        setScreen(new EnvironmentContract(new EnvironmentImpl()));
    }
}
