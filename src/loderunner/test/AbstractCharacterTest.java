package loderunner.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.CharacterImpl;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.EnvironmentImpl;
import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.EditableScreen;
import loderunner.services.Environment;

public abstract class AbstractCharacterTest {
    private Character character;

    public void setCharacter(Character character) {
        this.character = character;
    }

    @Before
    public abstract void beforeTests();

    @After
    public final void afterTests() {
        character = null;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // Preconditions

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales
        Environment env = new EnvironmentImpl(); env.init(10, 5);
        // Opération
        character.init(env, 5, 2);
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre2() { // Negatif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        Environment env = new EnvironmentImpl(); env.init(es);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        character.init(env, 5, 3);
    }

    // Transitions

    @Test
    public void testGoLeftTrans1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goLeft();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(4, character.getCol());
    }

    @Test
    public void testGoLeftTrans2() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        es.setNature(4, 4, Cell.MTL);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goLeft();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(5, character.getCol());
    }

    @Test
    public void testGoLeftTrans3() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goLeft();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(5, character.getCol());
    }

    @Test
    public void testGoLeftTrans4() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(5, 3, Cell.PLT); es.setNature(4, 4, Cell.HDR);
        env.init(es);
        character.init(env, 5, 4);
        character.goLeft();
        // Opération
        character.goLeft();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(3, character.getCol());
    }

    @Test
    public void testGoRightTrans1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goRight();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(6, character.getCol());
    }

    @Test
    public void testGoRightTrans2() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 3, Cell.PLT);
        es.setNature(4, 4, Cell.MTL);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goRight();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(6, character.getCol());
    }

    @Test
    public void testGoRightTrans3() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 4);
        // Opération
        character.goRight();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(5, character.getCol());
    }

    @Test
    public void testGoRightTrans4() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(5, 3, Cell.PLT); es.setNature(4, 4, Cell.HDR);
        env.init(es);
        character.init(env, 5, 4);
        character.goLeft();
        // Opération
        character.goRight();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(5, character.getCol());
    }

    @Test
    public void testGoRightTrans5() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        env.init(es);
        env.addCellContent(5, 3, new CharacterImpl());
        character.init(env, 5, 4);
        // Opération
        character.goRight();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(6, character.getCol());
    }

    @Test
    public void testGoUpTrans1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(5, 1, Cell.PLT); es.setNature(4, 2, Cell.LAD);
        env.init(es);
        character.init(env, 5, 2);
        character.goLeft();
        // Opération
        character.goUp();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(3, character.getHgt());
    }

    @Test
    public void testGoUpTrans2() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(5, 1, Cell.PLT);
        env.init(es);
        character.init(env, 5, 2);
        character.goLeft();
        // Opération
        character.goUp();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(2, character.getHgt());
    }

    @Test
    public void testGoDownTrans1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        env.init(es);
        character.init(env, 5, 2);
        // Opération
        character.goDown();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(1, character.getHgt());
    }

    @Test
    public void testGoDownTrans2() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        env.init(es);
        character.init(env, 5, 2); es.setNature(5, 1, Cell.PLT);
        // Opération
        character.goDown();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(1, character.getHgt());
    }

    @Test
    public void testGoDownTrans3() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        env.init(es);
        character.init(env, 5, 2); es.setNature(5, 1, Cell.LAD);
        // Opération
        character.goDown();
        // Oracle: vérifié par contrat + le personnage devrait avoir bougé
        assertEquals(1, character.getHgt());
    }

    // Etats remarquables

    @Test
    public void testFarLeft() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(0, 3, Cell.PLT);
        env.init(es);
        character.init(env, 0, 4);
        character.goLeft();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(0, character.getCol());
    }

    @Test
    public void testFarRight() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(9, 3, Cell.PLT);
        env.init(es);
        character.init(env, 9, 4);
        character.goRight();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(9, character.getCol());
    }

    @Test
    public void testFarUp() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        Environment env = new EnvironmentImpl();
        es.setNature(5, 3, Cell.PLT); es.setNature(4, 4, Cell.LAD);
        env.init(es);
        character.init(env, 5, 4);
        character.goLeft();
        character.goUp();
        // Oracle: vérifié par contrat + le personnage ne devrait pas avoir bougé
        assertEquals(4, character.getHgt());
    }

    // Paires de transitions
    // On ne va pas toutes les faire par contre...

    @Test
    public void testUpDown() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 1, Cell.MTL);
        es.setNature(4, 2, Cell.LAD);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 2);
        character.goLeft();
        // Opérations
        character.goUp(); character.goDown();
        // Oracle
        assertEquals(4, character.getCol());
        assertEquals(2, character.getHgt());
    }

    @Test
    public void testDownUp() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 1, Cell.MTL);
        es.setNature(4, 1, Cell.LAD);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 2);
        character.goLeft();
        // Opérations
        character.goDown(); character.goUp();
        // Oracle
        assertEquals(4, character.getCol());
        assertEquals(2, character.getHgt());
    }

    @Test
    public void testUpLeft() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 1, Cell.MTL);
        es.setNature(4, 2, Cell.LAD); es.setNature(4, 3, Cell.LAD);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 2);
        character.goLeft();
        // Opérations
        character.goUp(); character.goLeft();
        // Oracle
        assertEquals(3, character.getCol());
        assertEquals(3, character.getHgt());
    }

    @Test
    public void testRightDown() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 1, Cell.MTL);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 2);
        // Opérations
        character.goRight(); character.goDown();
        // Oracle
        assertEquals(6, character.getCol());
        assertEquals(1, character.getHgt());
    }

    // Scénarios
    @Test
    public void testScenar1() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 0, Cell.MTL); es.setNature(6, 0, Cell.PLT); es.setNature(8, 0, Cell.PLT);
        es.setNature(4, 1, Cell.PLT);
        es.setNature(7, 1, Cell.LAD); es.setNature(7, 2, Cell.LAD); es.setNature(7, 3, Cell.LAD);
        es.setNature(6, 4, Cell.HDR); es.setNature(5, 4, Cell.HDR);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 1);
        // Opérations
        character.goLeft();
        character.goRight(); character.goRight();
        character.goUp(); character.goUp(); character.goUp(); character.goUp(); // Il grimpe l'echelle
        character.goRight(); character.goDown(); character.goDown(); character.goDown(); // Il tombe de l'echelle
        character.goUp();
        character.goLeft(); character.goRight();
        // Oracle
        assertEquals(8, character.getCol());
        assertEquals(1, character.getHgt());
    }

    @Test
    public void testScenar2() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 0, Cell.MTL); es.setNature(6, 0, Cell.PLT); es.setNature(8, 0, Cell.PLT);
        es.setNature(4, 1, Cell.PLT);
        es.setNature(7, 1, Cell.LAD); es.setNature(7, 2, Cell.LAD); es.setNature(7, 3, Cell.LAD);
        es.setNature(6, 4, Cell.HDR); es.setNature(5, 4, Cell.HDR);
        Environment env = new EnvironmentImpl(); env.init(es);
        character.init(env, 5, 1);
        // Opérations
        character.goLeft();
        character.goRight(); character.goRight();
        character.goUp(); character.goUp(); character.goUp(); character.goUp();
        character.goLeft(); character.goLeft(); character.goLeft();
        character.goDown(); character.goDown(); character.goDown();
        // Oracle
        assertEquals(4, character.getCol());
        assertEquals(2, character.getHgt());
    }
}
