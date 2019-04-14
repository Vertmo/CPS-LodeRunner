package loderunner.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.ItemImpl;
import loderunner.services.Cell;
import loderunner.services.EditableScreen;
import loderunner.services.Environment;
import loderunner.services.Item;
import loderunner.services.ItemType;

public abstract class AbstractEnvironmentTest extends AbstractScreenTest {
    private Environment screen;

    public void setScreen(Environment screen) {
        super.setScreen(screen);
        this.screen = screen;
    }

    // Preconditions

    @Test
    public void testGetCellContentPre1() { // Positif
        // Conditions initiales
        screen.init(10, 5);
        // Opération
        screen.getCellNature(2, 3);
        // Oracle: pas d'exception
    }

    @Test
    public void testGetCellContentPre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.getCellNature(2, 7);
    }

    @Test
    public void testAddCellContentPre1() { // Positif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.PLT);
        screen.init(es);
        // Opération
        screen.addCellContent(5, 3, new ItemImpl(ItemType.Treasure, 5, 3));
        // Oracle: pas d'exception
    }

    @Test
    public void testAddCellContentPre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.addCellContent(5, 5, new ItemImpl(ItemType.Treasure, 5, 5));
    }

    @Test
    public void testAddCellContentPre3() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.addCellContent(5, 3, new ItemImpl(ItemType.Treasure, 5, 5));
    }

    @Test
    public void testRemoveCellContentPre1() { // Positif
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(2, 2, Cell.PLT);
        screen.init(es);
        Item t = new ItemImpl(ItemType.Treasure, 2, 3);
        screen.addCellContent(2, 3, t);
        // Opération
        screen.removeCellContent(2, 3, t);
        // Oracle: pas d'exception
    }

    @Test
    public void testRemoveCellContentPre2() { // Negatif
        // Conditions initiales
        screen.init(10, 5);
        Item t = new ItemImpl(ItemType.Treasure, 2, 3);
        // Oracle: une exception
        exception.expect(PreconditionError.class);
        // Opération
        screen.removeCellContent(2, 3, t);
    }

    // Transitions

    @Test
    public void testInitTrans1() {
        // Conditions initiales: aucune
        // Opération
        screen.init(2, 5);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testInitTrans2() {
        // Conditions initiales: aucune
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        // Opération
        screen.init(es);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testAddCellContentTrans() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        // Opération
        screen.addCellContent(5, 3, new ItemImpl(ItemType.Treasure, 5, 3));
        // Oracle: vérifié par les contrats
    }

    @Test
    public void testRemoveCellContentTrans() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        Item t = new ItemImpl(ItemType.Treasure, 5, 3);
        screen.addCellContent(5, 3, t);
        // Opération
        screen.removeCellContent(5, 3, t);
        // Oracle: vérifié par les contrats
    }

    // Etats remarquables

    @Test
    public void testItemInCell() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        Item t = new ItemImpl(ItemType.Treasure, 5, 3);
        screen.addCellContent(5, 3, t);
        // Oracle: l'item t est dans la cellule
        assertTrue(screen.getCellContent(5, 3).contains(t));
    }

    // Paires de transitions

    @Test
    public void testAddAdd() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        // Opérations
        screen.addCellContent(5, 3, new ItemImpl(ItemType.Treasure, 5, 3));
        screen.addCellContent(5, 3, new ItemImpl(ItemType.Treasure, 5, 3));
        // Oracle: vérifié par contrat
    }

    @Test
    public void testAddRemove() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        Item t = new ItemImpl(ItemType.Treasure, 5, 3);
        // Opérations
        screen.addCellContent(5, 3, t);
        screen.removeCellContent(5, 3, t);
        // Oracle: vérifié par contrat
    }

    @Test
    public void testRemoveRemove() {
        // Conditions initiales
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(5, 2, Cell.MTL);
        screen.init(es);
        Item t1 = new ItemImpl(ItemType.Treasure, 5, 3);
        Item t2 = new ItemImpl(ItemType.Treasure, 5, 3);
        screen.addCellContent(5, 3, t1);
        screen.addCellContent(5, 3, t2);
        // Opérations
        screen.removeCellContent(5, 3, t1);
        screen.removeCellContent(5, 3, t2);
        // Oracle: vérifié par contrat
    }

    // Scénarios

    @Test
    public void testScenar() {
        EditableScreen es = new EditableScreenImpl(); es.init(10, 5);
        es.setNature(4, 2, Cell.PLT); es.setNature(5, 2, Cell.PLT);
        screen.init(es);

        Item t1 = new ItemImpl(ItemType.Treasure, 5, 3);
        Item t2 = new ItemImpl(ItemType.Treasure, 5, 3);
        Item t3 = new ItemImpl(ItemType.Treasure, 4, 3);

        screen.addCellContent(5, 3, t1);
        screen.addCellContent(5, 3, t2);
        screen.addCellContent(4, 3, t3);

        screen.removeCellContent(4, 3, t3);
        screen.dig(4, 2); screen.fill(4, 2);
        screen.addCellContent(4, 3, t3);

        screen.removeCellContent(5, 3, t2);
        screen.removeCellContent(5, 3, t1);
    }
}
