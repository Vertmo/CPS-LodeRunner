package loderunner.test;

import org.junit.Test;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.EnvironmentImpl;
import loderunner.impl.PlayerImpl;
import loderunner.services.Cell;
import loderunner.services.EditableScreen;
import loderunner.services.Environment;
import loderunner.services.Guard;

public abstract class AbstractGuardTest extends AbstractCharacterTest {
    private Guard guard;

    public void setGuard(Guard guard) {
        super.setCharacter(guard);
        this.guard = guard;
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
