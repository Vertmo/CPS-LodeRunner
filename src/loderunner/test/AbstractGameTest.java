package loderunner.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.CoordImpl;
import loderunner.io.LevelIO;
import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.Game;
import loderunner.services.Level;

public abstract class AbstractGameTest {
    private Game game;
    private TestCommandProvider tcp;

    public void setGame(Game game) {
        this.game = game;
    }

    public void setCommandProvider(TestCommandProvider tcp) {
        this.tcp = tcp;
    }

    @Before
    public abstract void beforeTests();

    @After
    public void afterTests() {
        game = null;
        tcp = null;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Level createPlayableLevel() {
        Level l = new LevelIO(10, 5);
        for(int i = 0; i < 10; i++) l.getScreen().setNature(i, 0, Cell.MTL);
        l.getPlayerCoord().setCol(5); l.getPlayerCoord().setHgt(1);
        l.getGuardCoords().add(new CoordImpl(3, 1));
        l.getTreasureCoords().add(new CoordImpl(6, 1));
        return l;
    }

    // Préconditions

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        // Opération
        game.init(levels);
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre2() { // Négatif
        // Conditions initiales
        List<Level> levels = new ArrayList<>();
        // Oracle: pas d'exception
        exception.expect(PreconditionError.class);
        // Opération
        game.init(levels);
    }

    @Test
    public void testCheckAndUpdatePre1() { // Positif
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        game.init(levels);
        // Opération
        game.checkStateAndUpdate();
        // Oracle: pas d'exception
    }

    @Test
    public void testCheckAndUpdatePre2() { // Négatif
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Right)));
        game.init(levels);
        game.getEngine().step();
        game.checkStateAndUpdate(); // Ending first level
        // Oracle: pas d'exception
        exception.expect(PreconditionError.class);
        // Opération
        game.checkStateAndUpdate();
    }

    // Transitions

    @Test
    public void testCheckAndUpdateTrans1() {
        // Conditions initiales
        List<Level> levels = new ArrayList<>();
        levels.add(createPlayableLevel()); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Neutral)));
        game.init(levels);
        game.getEngine().step();
        // Opération
        game.checkStateAndUpdate();
        // Oracle: vérifié par les contrats
    }

    @Test
    public void testCheckAndUpdateTrans2() {
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Right)));
        game.init(levels);
        game.getEngine().step();
        // Opération
        game.checkStateAndUpdate();
        // Oracle: vérifié par les contrats + on a fini le jeu et gagné du score
        Assert.assertEquals(1, game.getLevelIndex());
        Assert.assertEquals(1, game.getScore());
    }

    @Test
    public void testCheckAndUpdateTrans3() {
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Neutral, Command.Neutral)));
        game.init(levels);
        game.getEngine().step(); game.getEngine().step();
        // Opération
        game.checkStateAndUpdate();
        // Oracle: vérifié par les contrats + on a perdu une vie
        Assert.assertEquals(2, game.getHP());
    }

    // Etats remarquables

    @Test
    public void testGameOver() {
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Neutral, Command.Neutral,
                                                      Command.Neutral, Command.Neutral,
                                                      Command.Neutral, Command.Neutral)));
        game.init(levels);
        for(int i = 0; i < 3; i++) {
            game.getEngine().step(); game.getEngine().step();
            game.checkStateAndUpdate();
        }
        // Oracle: vérifié par les contrats + game over
        Assert.assertEquals(0, game.getHP());
    }

    // Scénarios

    @Test
    public void testScenar() {
        // Conditions initiales
        List<Level> levels = new ArrayList<>(); levels.add(createPlayableLevel());
        tcp.setCommands(new ArrayList<>(Arrays.asList(Command.Neutral, Command.Neutral,
                                                      Command.Right)));
        game.init(levels);
        game.getEngine().step(); game.getEngine().step();
        game.checkStateAndUpdate();

        game.getEngine().step(); game.checkStateAndUpdate();
        // Oracle: vérifié par les contrats + on a perdu une vie puis gagné un point, et le jeu est fini
        Assert.assertEquals(2, game.getHP());
        Assert.assertEquals(1, game.getScore());
        Assert.assertEquals(1, game.getLevelIndex());
    }
}
