package loderunner.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ScreenTestBug.class,
            EditableScreenTestBug.class,
            EnvironmentTestBug.class,
            CharacterTestBug.class,
            GameTestBug.class,
            PlayerTestBug.class,
            GuardTestBug.class,
            EngineTestBug.class
})

public class TestsBug {}
