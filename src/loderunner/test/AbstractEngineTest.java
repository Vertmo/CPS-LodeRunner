package loderunner.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
import loderunner.services.Status;

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
        for(int i = 0; i < 10; i++) s.setNature(i, 1, Cell.PLT);
        return s;
    }


    // Préconditions

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        // Opération
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
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
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testInitPre3() { // Négatif
        // Conditions initiales
        EditableScreen s = new EditableScreenImpl();
        s.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
    }

    @Test
    public void testStepPre1() { // Positif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left)));
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    @Test
    public void testStepPre2() { // Négatif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2), new HashSet<>(), new HashSet<>());
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
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
                    tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Right)));
        engine.step();
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    // TODO

    // Transitions

    @Test
    public void testStepTrans1() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(7, 2))));
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Right, Command.Right)));
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans2() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Right, Command.Right)));
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans3() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.DigL)));
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans4() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.DigR)));
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans5() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.DigL, Command.Neutral)));
        engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans6() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        List<Command> coms = new ArrayList<>(); coms.add(Command.DigL);
        for(int i = 0; i < 16; i++) coms.add(Command.Neutral);
        tcp.setCommands(new ArrayList<>(coms));
        for(int i = 0; i < 16; i++) engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat + le trou est rebouché
        Assert.assertEquals(Cell.PLT, engine.getEnvironment().getCellNature(4, 1));
    }

    // TODO

    // Etats remarquables
    @Test
    public void testPlayerCrushed() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))));
        List<Command> coms = new ArrayList<>(); coms.add(Command.DigL); coms.add(Command.Left);
        for(int i = 0; i < 15; i++) coms.add(Command.Neutral);
        tcp.setCommands(new ArrayList<>(coms));
        for(int i = 0; i < 16; i++) engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat + le trou est rebouché + le joueur a été tué par le trou
        Assert.assertEquals(Cell.PLT, engine.getEnvironment().getCellNature(4, 1));
        Assert.assertEquals(Status.Loss, engine.getStatus());
    }

    // TODO

    // Scénarios
    // TODO
}
