package loderunner.test;

import loderunner.contracts.EngineContract;
import loderunner.impl.EngineImplBug;

public class EngineTestBug extends AbstractEngineTest {
    @Override
    public void beforeTests() {
        TestCommandProvider tcp = new TestCommandProvider();
        setCommandProvider(tcp);
        setEngine(new EngineContract(new EngineImplBug(tcp)));
    }
}
