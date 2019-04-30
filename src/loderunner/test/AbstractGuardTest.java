package loderunner.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.EnvironmentImpl;
import loderunner.impl.GuardImpl;
import loderunner.impl.PlayerImpl;
import loderunner.services.Cell;
import loderunner.services.EditableScreen;
import loderunner.services.Environment;
import loderunner.services.Guard;
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
        es.setNature(7, 2, Cell.LAD); es.setNature(7, 3, Cell.LAD); es.setNature(7, 4, Cell.LAD);
        es.setNature(5, 4, Cell.PLT); es.setNature(6, 4, Cell.PLT); es.setNature(8, 4, Cell.PLT); es.setNature(9, 4, Cell.PLT);
        es.setNature(1, 4, Cell.HDR); es.setNature(2, 4, Cell.HDR);

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

    @Test
    public void testInitTrans2() {
        // Conditions initiales
        Environment env = new EnvironmentImpl(); env.init(10, 5);
        // Opération
        guard.init(env, new PlayerImpl(), 5, 2);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testClimbLeftTrans1() {
        // Conditions initiales
        Environment env = createEnvironment(); env.dig(5, 1);
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 5, 2); guard.goDown();
        // Opération
        guard.climbLeft();
        // Oracle: vérifié par contrats + garde en (4, 2)
        assertEquals(4, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    @Test
    public void testClimbLeftTrans2() {
        // Conditions initiales
        Environment env = createEnvironment(); env.dig(5, 1);
        env.addCellContent(4, 2, new GuardImpl());
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 5, 2); guard.goDown();
        // Opération
        guard.climbLeft();
        // Oracle: vérifié par contrats + garde en (5, 1)
        assertEquals(5, guard.getCol());
        assertEquals(1, guard.getHgt());
    }

    @Test
    public void testClimbRightTrans1() {
        // Conditions initiales
        Environment env = createEnvironment(); env.dig(2, 1);
        Player p = new PlayerImpl(); p.init(env, 4, 2);
        guard.init(env, p, 2, 2); guard.goDown();
        // Opération
        guard.climbRight();
        // Oracle: vérifié par contrats + garde en (3, 2)
        assertEquals(3, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    @Test
    public void testClimbRightTrans2() {
        // Conditions initiales
        Environment env = createEnvironment(); env.dig(4, 3);
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 4, 4); guard.goDown();
        // Opération
        guard.climbRight();
        // Oracle: vérifié par contrats + garde en (4, 3)
        assertEquals(4, guard.getCol());
        assertEquals(3, guard.getHgt());
    }

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

    @Test
    public void testStepTrans8() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl());
        Player p = new PlayerImpl(); p.init(env, 7, 2);
        guard.init(env, p, 7, 5); guard.goDown();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (7, 4)
        assertEquals(7, guard.getCol());
        assertEquals(4, guard.getHgt());
    }

    @Test
    public void testStepTrans9() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl()); env.dig(6, 4);
        Player p = new PlayerImpl(); p.init(env, 6, 2);
        guard.init(env, p, 7, 5); guard.goDown();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (6, 4)
        assertEquals(6, guard.getCol());
        assertEquals(4, guard.getHgt());
    }

    @Test
    public void testStepTrans10() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl()); env.dig(5, 1);
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 5, 2); guard.goDown();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + timeInHole de 1
        assertEquals(1, guard.getTimeInHole());
    }

    @Test
    public void testStepTrans11() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl()); env.dig(5, 1);
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 5, 2); guard.goDown();
        for(int i = 0; i < 5; i++) guard.step();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (4, 2)
        assertEquals(4, guard.getCol());
        assertEquals(2, guard.getHgt());
    }

    @Test
    public void testStepTrans12() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl()); env.dig(5, 1);
        Player p = new PlayerImpl(); p.init(env, 7, 5);
        guard.init(env, p, 1, 5); guard.step();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (4, 2)
        assertEquals(2, guard.getCol());
        assertEquals(4, guard.getHgt());
    }

    @Test
    public void testStepTrans13() {
        // Conditions initiales
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 1, 2);
        guard.init(env, p, 1, 5); guard.step();
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde en (4, 2)
        assertEquals(1, guard.getCol());
        assertEquals(3, guard.getHgt());
    }

    @Test
    public void testStepTrans14() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl()); env.dig(5, 1);
        Player p = new PlayerImpl(); p.init(env, 7, 5);
        guard.init(env, p, 1, 5);
        guard.step();
        guard.setIsShot(true);
        // Opération
        guard.step();
        // Oracle: vérifié par contrats + garde meurt et retourne en (1, 5)
        assertEquals(1, guard.getCol());
        assertEquals(5, guard.getHgt());
    }

    // Etats remarquables

    @Test
    public void testGuardCaughtPlayer() {
        // Conditions initiales
        Environment env = createEnvironment();
        env.addCellContent(7, 3, new GuardImpl());
        Player p = new PlayerImpl(); p.init(env, 3, 2);
        guard.init(env, p, 4, 2); guard.step();
        // Opération
        guard.step();
        // Oracle: garde en (3, 2)
        assertEquals(p.getCol(), guard.getCol());
        assertEquals(p.getHgt(), guard.getHgt());
    }

    // Scénarios

    @Test
    public void testScenar3() {
        Environment env = createEnvironment();
        Player p = new PlayerImpl(); p.init(env, 7, 5);
        env.dig(4, 1);
        guard.init(env, p, 3, 2);
        guard.step(); guard.step();
        assertEquals(4, guard.getCol()); assertEquals(1, guard.getHgt());
        for(int i = 0; i < 5; i++) guard.step();
        assertEquals(4, guard.getCol()); assertEquals(1, guard.getHgt());
        guard.step(); guard.step(); guard.step(); guard.step();
        assertEquals(7, guard.getCol()); assertEquals(3, guard.getHgt());
        p.goRight(); guard.step();
        p.goRight(); guard.step(); guard.step(); guard.step();
        assertEquals(5, guard.getHgt()); assertEquals(9, guard.getCol());
    }

}
