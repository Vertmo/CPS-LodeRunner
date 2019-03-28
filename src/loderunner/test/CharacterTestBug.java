package loderunner.test;

import loderunner.contracts.CharacterContract;
import loderunner.impl.CharacterImplBug;

public class CharacterTestBug extends AbstractCharacterTest {

    @Override
    public void beforeTests() {
        setCharacter(new CharacterContract(new CharacterImplBug()));
    }
}
