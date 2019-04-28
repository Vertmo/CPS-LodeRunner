package loderunner.test;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import loderunner.services.EditableScreen;
import loderunner.services.Cell;
import loderunner.contracts.errors.PreconditionError;

public abstract class AbstractEditableScreenTest extends AbstractScreenTest {
    private EditableScreen screen;

    public void setScreen(EditableScreen screen) {
        super.setScreen(screen);
        this.screen = screen;
    }

    // Preconditions

    @Test
    public void testSetNaturePre1() { // Positif
        // Conditions initiales
        screen.init(10, 5);
        // Opération
        screen.setNature(2, 3, Cell.PLT);
        // Oracle: pas d'exception
    }

    @Test
    public void testSetNaturePre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.setNature(-2, 3, Cell.PLT);
    }

    @Test
    public void testSetNaturePre3() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.setNature(2, 5, Cell.PLT);
    }

    // Les cas positifs de dig et fill sont maintenant observables !

    @Test
    public void testDigPre3() { // Positif
        // Conditions initiales
        screen.init(10, 5); screen.setNature(2, 3, Cell.PLT);
        // Opération
        screen.dig(2, 3);
        // Oracle: pas d'exception
    }

    @Test
    public void testFillPre3() { // Positif
        // Conditions initiales
        screen.init(10, 5); screen.setNature(2, 3, Cell.HOL);
        // Opération
        screen.fill(2, 3);
        // Oracle: pas d'exception
    }

    @Test
    public void testTriggerTrapPre2() { // Positif
        // Conditions initiales
        screen.init(10, 5); screen.setNature(2, 3, Cell.TRP);
        // Opération
        screen.triggerTrap(2, 3);
        // Oracle: pas d'exception
    }

    @Test
    public void testOpenDoorPre2() { // Positif
        // Conditions initiales
        screen.init(10, 5); screen.setNature(2, 3, Cell.DOR);
        // Opération
        screen.openDoor(2, 3);
        // Oracle: pas d'exception
    }

    // Transititions

    @Test
    public void testSetNatureTrans() {
        // Conditions initiales
        screen.init(10, 5);
        // Opération
        screen.setNature(2, 3, Cell.PLT);
        // Oracle: vérifié par contrat
    }

    // Les transitions de dig et fill sont maintenant observables !

    @Test
    public void testDigTrans() {
        // Conditions initiales
        screen.init(10, 5); screen.setNature(6, 1, Cell.PLT);
        // Opération
        screen.dig(6, 1);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testFillTrans() {
        // Conditions initiales
        screen.init(10, 5); screen.setNature(4, 2, Cell.HOL);
        // Opération
        screen.fill(4, 2);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testOpenDoorTrans() { // Positif
        // Conditions initiales
        screen.init(10, 5); screen.setNature(4, 3, Cell.DOR);
        // Opération
        screen.openDoor(4, 3);
        // Oracle: vérifié par les contrats
    }

    // Etats remarquables

    @Test
    public void testPlayableState() {
        // Conditions initiales
        screen.init(10, 5);
        for(int i = 0; i < 10; i++) {
            screen.setNature(i, 0, Cell.MTL);
        }
        // Orable: l'écran est jouable
        assertTrue(screen.isPlayable());
    }

    // Paires de transitions

    @Test
    public void testDigFill() {
        // Conditions initiales
        screen.init(10, 5); screen.setNature(5, 3, Cell.PLT);
        // Opérations
        screen.dig(5, 3); screen.fill(5, 3);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testFillDig() {
        // Conditions initiales
        screen.init(10, 5); screen.setNature(5, 3, Cell.HOL);
        // Opérations
        screen.fill(5, 3); screen.dig(5, 3);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testTriggerTrapTrans() {
        // Conditions initiales
        screen.init(10, 5); screen.setNature(5, 1, Cell.TRP);
        // Opération
        screen.triggerTrap(5, 1);
        // Oracle: vérifié par les contrats
    }

    // Scénarios

    @Test
    public void testScenar() {
        screen.init(10, 5);
        for(int i = 0; i < 10; i++) screen.setNature(i, 0, Cell.MTL);
        screen.setNature(5, 3, Cell.PLT); screen.setNature(6, 3, Cell.PLT);
        screen.setNature(7, 3, Cell.PLT); screen.setNature(7, 3, Cell.MTL);

        screen.dig(5, 3); screen.dig(6, 3); screen.fill(5, 3);
    }
}
