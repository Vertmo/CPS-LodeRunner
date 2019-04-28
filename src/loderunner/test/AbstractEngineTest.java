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
import loderunner.impl.PortalPairImpl;
import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Guard;
import loderunner.services.PortalPair;
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
        s.setNature(1, 2, Cell.LAD); s.setNature(1, 3, Cell.LAD);
        s.setNature(2, 3, Cell.PLT); s.setNature(3, 3, Cell.PLT);
        s.setNature(4, 4, Cell.HDR); s.setNature(5, 3, Cell.PLT);
        return s;
    }


    // Préconditions

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        // Opération
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(), new HashSet<>());
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
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testInitPre4() { // Négatif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 2), new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2), new CoordImpl(5, 2))),
                    new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testInitPre5() { // Négatif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(5, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testInitPre6() { // Positif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        PortalPair pp = new PortalPairImpl(new CoordImpl(3, 2));
        pp.setOutPCoord(new CoordImpl(4, 2));
        // Opération
        engine.init(s, new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(pp)));
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre7() { // Négatif
        // Conditions initiales
        EditableScreen s = createPlayableScreen();
        PortalPair pp = new PortalPairImpl(new CoordImpl(3, 2));
        pp.setOutPCoord(new CoordImpl(4, 4));
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.init(s, new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(pp)));
    }

    @Test
    public void testStepPre1() { // Positif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left)));
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    @Test
    public void testStepPre2() { // Négatif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Left)));
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Right)));
        engine.step();
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    @Test
    public void testStepPre4() { // Négatif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(4, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Right)));
        engine.step();
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        engine.step();
    }

    @Test
    public void testStepPre5() { // Positif
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(2, 2), new CoordImpl(2, 4))), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Left, Command.Right)));
        engine.step();
        // Opération
        engine.step();
        // Oracle: pas d'exception
    }

    // Transitions

    @Test
    public void testStepTrans1() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(3, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(7, 2))),
                    new HashSet<>(), new HashSet<>());
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Right, Command.Right)));
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans3() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(2, 2), new CoordImpl(5, 4))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(), new HashSet<>());
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
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
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>(); coms.add(Command.DigL);
        for(int i = 0; i < 16; i++) coms.add(Command.Neutral);
        tcp.setCommands(new ArrayList<>(coms));
        for(int i = 0; i < 16; i++) engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat + le trou est rebouché
        Assert.assertEquals(Cell.PLT, engine.getEnvironment().getCellNature(4, 1));
    }

    @Test
    public void testStepTrans7() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(4, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(7, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>();
        for(int i = 0; i < 2; i++) coms.add(Command.Neutral);
        tcp.setCommands(coms);
        engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat
    }

    @Test
    public void testStepTrans8() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(4, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(7, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>(); coms.add(Command.DigR);
        for(int i = 0; i < 10; i++) coms.add(Command.Neutral);
        tcp.setCommands(coms);
        engine.step(); engine.step(); engine.step(); engine.step(); engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat + le trésor est tombé au dessus du garde
        Assert.assertEquals(1, engine.getEnvironment().getCellContent(5, 2).size());
    }

    // Etats remarquables
    @Test
    public void testPlayerCrushed() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>(); coms.add(Command.DigL); coms.add(Command.Left);
        for(int i = 0; i < 15; i++) coms.add(Command.Neutral);
        tcp.setCommands(coms);
        for(int i = 0; i < 16; i++) engine.step();
        // Opération
        engine.step();
        // Oracle: vérifié par contrat + le trou est rebouché + le joueur a été tué par le trou
        Assert.assertEquals(Cell.PLT, engine.getEnvironment().getCellNature(4, 1));
        Assert.assertEquals(Status.Loss, engine.getStatus());
    }

    @Test
    public void testGuardWithTreasureCaughtPlayer() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(5, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>(); coms.add(Command.Neutral); coms.add(Command.Neutral);
        tcp.setCommands(coms);

        // Opération
        engine.step(); engine.step();

        // Oracle: vérifié par contrat + le joueur a été tué par le garde
        Assert.assertEquals(Status.Loss, engine.getStatus());
    }

    @Test
    public void testGuardCrushedInHole() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(7, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(9, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(0, 3))), new HashSet<>(), new HashSet<>());
        Guard g = engine.getGuards().iterator().next();
        List<Command> coms = new ArrayList<>();
        coms.add(Command.DigR); coms.add(Command.Left); coms.add(Command.Left);
        coms.add(Command.DigR); coms.add(Command.Left); coms.add(Command.Left);
        coms.add(Command.DigR);
        for(int i = 0; i < 22; i++) coms.add(Command.Neutral);
        tcp.setCommands(coms);

        // Opération
        for(int i = 0; i < 20; i++) engine.step();

        // Oracle: vérifié par contrat + le garde est revenu a sa position de départ
        Assert.assertEquals(2, g.getHgt());
        Assert.assertEquals(9, g.getCol());
    }

    @Test
    public void testAllTreasures() {
        // Conditions initiales
        engine.init(createPlayableScreen(), new CoordImpl(7, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>();
        coms.add(Command.Left);
        tcp.setCommands(coms);

        // Opération
        engine.step();

        // Oracle: vérifié par contrat + le niveau est gagné
        Assert.assertEquals(Status.Win, engine.getStatus());
    }

    @Test
    public void testTriggerTrap() {
        // Conditions initiales
        EditableScreen es = createPlayableScreen();
        es.setNature(8, 1, Cell.TRP);
        engine.init(es, new CoordImpl(9, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>();
        coms.add(Command.Left); coms.add(Command.Neutral);
        tcp.setCommands(coms);

        // Opération
        engine.step(); engine.step();

        // Oracle: vérifié par contrat + le piege s'est déclenché et le joueur est tombé dedans
        Assert.assertEquals(Cell.EMP, engine.getEnvironment().getCellNature(8, 1));
        Assert.assertEquals(1, engine.getPlayer().getHgt());
    }

    @Test
    public void testPortal() {
        // Conditions initiales
        EditableScreen es = createPlayableScreen();
        PortalPair pp = new PortalPairImpl(new CoordImpl(8, 2));
        pp.setOutPCoord(new CoordImpl(5, 2));
        engine.init(es, new CoordImpl(9, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))), new HashSet<>(), new HashSet<>(Arrays.asList(pp)));
        List<Command> coms = new ArrayList<>();
        coms.add(Command.Left); coms.add(Command.Neutral);
        tcp.setCommands(coms);

        // Opération
        engine.step(); engine.step();

        // Oracle: vérifié par contrat + le piege a traversé le portail
        Assert.assertEquals(5, engine.getPlayer().getCol());
        Assert.assertEquals(2, engine.getPlayer().getHgt());
    }

    @Test
    public void testGrabKey() {
        // Conditions initiales
        EditableScreen es = createPlayableScreen();
        engine.init(es, new CoordImpl(9, 2),
                    new HashSet<>(),
                    new HashSet<>(Arrays.asList(new CoordImpl(6, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(8, 2))), new HashSet<>());
        List<Command> coms = new ArrayList<>();
        coms.add(Command.Left);
        tcp.setCommands(coms);

        // Opération
        engine.step();

        // Oracle: vérifié par contrat + le joueur a attrapé la clé
        Assert.assertEquals(1, engine.getPlayer().getNbKeys());
    }

    // Scénarios

    @Test
    public void testScenar() {
        engine.init(createPlayableScreen(), new CoordImpl(3, 2),
                    new HashSet<>(Arrays.asList(new CoordImpl(5, 2))),
                    new HashSet<>(Arrays.asList(new CoordImpl(5, 4))), new HashSet<>(), new HashSet<>());
        List<Command> coms = new ArrayList<>();
        coms.add(Command.Left); coms.add(Command.Left);
        coms.add(Command.Up); coms.add(Command.Up);
        coms.add(Command.Right); coms.add(Command.Right); coms.add(Command.Right); coms.add(Command.Right);
        tcp.setCommands(coms);

        engine.step(); engine.step();
        engine.step(); engine.step();
        engine.step(); engine.step(); engine.step(); engine.step();

        // Oracle: vérifié par contrats + le joueur a gagné
        Assert.assertEquals(Status.Win, engine.getStatus());

        Assert.assertEquals(4, engine.getPlayer().getHgt());
        Assert.assertEquals(5, engine.getPlayer().getCol());
    }

}
