package loderunner.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Screen;

public abstract class AbstractScreenTest {
    private Screen screen;

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Before
    public abstract void beforeTests();

    @After
    public final void afterTests() {
        screen = null;
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // Preconditions

    @Test
    public void testGetCellNaturePre1() { // Positif
        // Conditions initiales
        screen.init(10, 5);
        // Opération
        screen.getCellNature(2, 3);
        // Oracle: pas d'exception
    }

    @Test
    public void testGetCellNaturePre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.getCellNature(-2, 3);
    }

    @Test
    public void testGetCellNaturePre3() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.getCellNature(2, 5);
    }

    @Test
    public void testInitPre1() { // Positif
        // Conditions initiales: aucune
        // Opération
        screen.init(10, 5);
        // Oracle: pas d'exception
    }

    @Test
    public void testInitPre2() { // Negatif
        // Conditions initiales: aucune
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.init(0, 5);
    }

    @Test
    public void testInitPre3() { // Negatif
        // Conditions initiales: aucune
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.init(10, -5);
    }

    // Les cas positifs de fill et dig ne sont pas observables, puisqu'on ne peut pas set les cellules

    @Test
    public void testDigPre1() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.dig(2, 6);
    }

    @Test
    public void testDigPre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.dig(2, 3);
    }

    @Test
    public void testFillPre1() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.dig(-2, 3);
    }

    @Test
    public void testFillPre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.dig(2, 3);
    }

    @Test
    public void testTriggerTrapPre1() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.triggerTrap(2, 3);
    }

    // Transitions

    @Test
    public void testInitTrans() {
        // Conditions initiales: aucune
        // Opération
        screen.init(10, 5);
        // Oracle: vérifié par contrat
    }

    // Les transitions dig et fill ne sont pas non plus observables.
    // Il n'y a donc pas réellemnt d'états remarquables, de paires de
    // transitions ou de scénarios observables
}
