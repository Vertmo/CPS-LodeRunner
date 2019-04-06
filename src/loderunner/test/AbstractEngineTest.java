package loderunner.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.CoordImpl;
import loderunner.impl.EditableScreenImpl;
import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;

public abstract class AbstractEngineTest {
    private Engine engine;
    private TestCommandProvider tcp;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setCommandProvider(TestCommandProvider tcp) {
        this.tcp = tcp;
    }

    @Before
    public abstract void beforeTests();

    @After
    public void afterTests() {
        engine = null;
        tcp = null;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private EditableScreen createPlayableScreen() {
        EditableScreen s = new EditableScreenImpl();
        s.init(10, 5);
        for(int i = 0; i < 10; i++) s.setNature(i, 0, Cell.MTL);
        return s;
    }


    // Préconditions

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales
        EditableScreen s = new EditableScreenImpl();
        s.init(10, 5);
        for(int i = 0; i < 10; i++) s.setNature(i, 0, Cell.MTL);
        // Opération
        engine.init(s, new CoordImpl(5, 1), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 1))));
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre2() { // Négatif
        // Conditions initiales
        EditableScreen s = new EditableScreenImpl();
        s.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 1), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testInitPre3() { // Négatif
        // Conditions initiales
        EditableScreen s = new EditableScreenImpl();
        s.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 1), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 1))));
    }

    @Test
    public void testStepPre1() { // Positif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 1), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left)));
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    @Test
    public void testStepPre2() { // Négatif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 1), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left)));
        engine.step();
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.step();
    }

    @Test
    public void testStepPre3() { // Positif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 1),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 1))));
                    tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Right)));
        engine.step();
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    // TODO

    // Transitions
    // TODO

    // Etats remarquables
    // TODO

    // Scénarios
    // TODO
}
