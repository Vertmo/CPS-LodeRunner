package loderunner.test;

import loderunner.contracts.EngineContract;
import loderunner.impl.EngineImpl;

public class EngineTest extends AbstractEngineTest {
    @Override
    public void beforeTests() {
        TestCommandProvider tcp = new TestCommandProvider();
        setCommandProvider(tcp);
        setEngine(new EngineContract(new EngineImpl(tcp)));
    }
}
