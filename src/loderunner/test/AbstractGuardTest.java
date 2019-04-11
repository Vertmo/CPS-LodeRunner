package loderunner.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.EnvironmentImpl;
import loderunner.impl.PlayerImpl;
import loderunner.services.Cell;
import loderunner.services.EditableScreen;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.Move;
import loderunner.services.Player;

public abstract class AbstractGuardTest extends AbstractCharacterTest {
    private Guard guard;

    public void setGuard(Guard guard) {
        super.setCharacter(guard);
        this.guard = guard;
    }

    private Environment createEnvironment() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 7);
        for(int i = 0; i < 10; i++) es.setNature(i, 0, Cell.MTL);
        for(int i = 0; i < 10; i++) es.setNature(i, 1, Cell.PLT);
        es.setNature(3, 1, Cell.HOL);
        es.setNature(7, 2, Cell.LAD); es.setNature(7, 3, Cell.LAD); es.setNature(7, 4, Cell.LAD);
        es.setNature(5, 4, Cell.PLT); es.setNature(6, 4, Cell.PLT); es.setNature(8, 4, Cell.PLT); es.setNature(9, 4, Cell.PLT);

        Environment e = new EnvironmentImpl();
        e.init(es);
        return e;
    }

    // Preconditions

    @Test
    public void testInitPre3() { // Positif
        // Conditions initiales
        Environment env = new EnvironmentImpl(); env.init(10, 5);
        // Opération
        guard.init(env, new PlayerImpl(), 5, 2);
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre4() { // Négatif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        Environment env = new EnvironmentImpl(); env.init(es);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        guard.init(env, new PlayerImpl(), 5, 3);
    }

    @Test
    public void testClimbLeftPre1() { // Positif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.HOL);
        Environment env = new EnvironmentImpl(); env.init(es);
        guard.init(env, new PlayerImpl(), 5, 3);
        guard.goDown();
        // Opération
        guard.climbLeft();
        // Oracle: pas d'exception
    }

    @Test
    public void testClimbLeftPre2() { // Négatif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl(); env.init(es);
        guard.init(env, new PlayerImpl(), 5, 3);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        guard.climbLeft();
    }

    @Test
    public void testClimbRightPre1() { // Positif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.HOL);
        Environment env = new EnvironmentImpl(); env.init(es);
        guard.init(env, new PlayerImpl(), 5, 3);
        guard.goDown();
        // Opération
        guard.climbRight();
        // Oracle: pas d'exception
    }

    @Test
    public void testClimbRightPre2() { // Négatif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl(); env.init(es);
        guard.init(env, new PlayerImpl(), 5, 3);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        guard.climbRight();
    }

    // Transitions
    // TODO

    @Test
    public void testStepTrans1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl(); env.init(es);
        guard.init(env, new PlayerImpl(), 5, 3);
        // Opération
        guard.step();
        // Oracle: vérifié par contrats
    }

    @Test
    public void testStepTrans2() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 5, 1);
        guard.init(env, p, 5, 3);
        // Opération
        guard.step();
        // Oracle: vérifié par contrats
    }

    @Test
    public void testStepTrans3() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 5, 1);
        guard.init(env, p, 7, 5); guard.goDown();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (7, 3)
        assertEquals(7, guard.getCol());
        assertEquals(3, guard.getHgt());
    }

    @Test
    public void testStepTrans4() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 5, 5);
        guard.init(env, p, 6, 2); guard.goRight();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (6, 2)
        assertEquals(6, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    @Test
    public void testStepTrans5() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 6, 2); guard.goRight();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (6, 2)
        assertEquals(6, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    @Test
    public void testStepTrans6() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 7, 5);
        guard.init(env, p, 6, 2); guard.goRight();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (7, 3)
        assertEquals(7, guard.getCol());
        assertEquals(3, guard.getHgt());
    }

    @Test
    public void testStepTrans7() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 6, 5);
        guard.init(env, p, 6, 2); guard.goRight();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (6, 2)
        assertEquals(6, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    // TODO

    // Etats remarquables
    // TODO

    // Paires de transitions
    // TODO

    // Scénarios
    // TODO

}
