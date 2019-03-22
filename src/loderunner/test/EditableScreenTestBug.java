package loderunner.test;

import loderunner.impl.EditableScreenImplBug;
import loderunner.contracts.EditableScreenContract;

public class EditableScreenTestBug extends AbstractEditableScreenTest {
    @Override
    public void beforeTests() {
        setScreen(new EditableScreenContract(new EditableScreenImplBug()));
    }
}
