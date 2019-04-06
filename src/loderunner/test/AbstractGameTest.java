package loderunner.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import loderunner.contracts.errors.PreconditionError;
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
        Level l = new loderunner.io.Level(10, 5);
        // TODO
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

    // TODO

    // Transitions
    // TODO

    // Etats remarquables
    // TODO

    // Paires de transitions
    // TODO

    // Scénarios
    // TODO
}
