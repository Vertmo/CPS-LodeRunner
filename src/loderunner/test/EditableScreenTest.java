package loderunner.test;

import loderunner.impl.EditableScreenImpl;
import loderunner.contracts.EditableScreenContract;

public class EditableScreenTest extends AbstractEditableScreenTest {
    @Override
    public void beforeTests() {
        setScreen(new EditableScreenContract(new EditableScreenImpl()));
    }
}
