package loderunner.test;

import loderunner.contracts.CharacterContract;
import loderunner.impl.CharacterImpl;

public class CharacterTest extends AbstractCharacterTest {

    @Override
    public void beforeTests() {
        setCharacter(new CharacterContract(new CharacterImpl()));
    }
}
