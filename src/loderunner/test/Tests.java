package loderunner.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ScreenTest.class,
            EditableScreenTest.class,
            EnvironmentTest.class,
            CharacterTest.class,
            EngineTest.class,
            GameTest.class
})

public class Tests {}
