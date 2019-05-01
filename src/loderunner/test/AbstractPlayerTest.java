package loderunner.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.Assert;

import loderunner.contracts.errors.PreconditionError;
import loderunner.impl.CoordImpl;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.EngineImpl;
import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Player;

public abstract class AbstractPlayerTest extends AbstractCharacterTest{
	private Player player;

	public void setPlayer(Player player) {
		super.setCharacter(player);
		this.player = player;
	}

	private EditableScreen createEnvironment() {
		EditableScreen es = new EditableScreenImpl(); es.init(10, 7);
		for(int i = 0; i < 10; i++) es.setNature(i, 0, Cell.MTL);
		for(int i = 0; i < 10; i++) es.setNature(i, 1, Cell.PLT);
    es.setNature(8, 2, Cell.DOR);
		es.setNature(7, 2, Cell.LAD); es.setNature(7, 3, Cell.LAD); es.setNature(7, 4, Cell.LAD);
		es.setNature(5, 4, Cell.PLT); es.setNature(6, 4, Cell.PLT); es.setNature(8, 4, Cell.PLT); es.setNature(9, 4, Cell.PLT);
		es.setNature(1, 4, Cell.HDR); es.setNature(2, 4, Cell.HDR); es.setNature(3, 4, Cell.HDR); es.setNature(4, 4, Cell.HDR);
		return es;
	}

	private Engine createEngine(List<Command> commands) {
		TestCommandProvider cmd = new TestCommandProvider();
		cmd.setCommands(commands);
		Engine eg = new EngineImpl(cmd);
		EditableScreen screen = createEnvironment();
		Coord pCoord = new CoordImpl(9,6);
		Set<Coord> gCoords = new HashSet<Coord>();
		gCoords.add(new CoordImpl(2, 2));
		Set<Coord> tCoords = new HashSet<Coord>();
		tCoords.add(new CoordImpl(4, 5));
		eg.init(screen, pCoord, gCoords, tCoords, new HashSet<>(), new HashSet<>());
		return eg;
	}

	//==============
	// Preconditions
	//==============

	@Test
	public void testInitPre3(){
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Neutral);
		Engine eg = createEngine(cmd);
		//Opération
		player.init(eg.getEnvironment(), eg, 0, 2);
		//Oracle: pas d'exeption
	}

	@Test(expected = PreconditionError.class)
	public void testInitPre4(){
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Neutral);
		Engine eg = createEngine(cmd);
		//Opération
		player.init(eg.getEnvironment(), eg, 0, 0);
		//Oracle: PreconditoinError
	}

    @Test
    public void testTeleportPre1() { // Positif
        // Conditions initiales
        Engine eg = createEngine(new ArrayList<>());
        player.init(eg.getEnvironment(), eg, 0, 2);
        // Opération
        player.teleport(7, 5);
        // Oracle: pas d'exception
    }

    @Test
    public void testTeleportPre2() { // Négatif
        // Conditions initiales
        Engine eg = createEngine(new ArrayList<>());
        player.init(eg.getEnvironment(), eg, 0, 2);
        // Oracle: PreconditionError
        exception.expect(PreconditionError.class);
        // Opération
        player.teleport(7, 2);
    }

	//============
	// Transitions
	//============

	@Test
	public void testStep1() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Neutral);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 0, 3);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player doit tomber en (0,2)
		assert(player.getCol() == 0);
		assert(player.getHgt() == 2);
	}

	@Test
	public void testStep2() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Neutral);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 7, 5);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player sur l'échelle ne doit pas tomber
		assert(player.getCol() == 7);
		assert(player.getHgt() == 5);
	}

	@Test
	public void testStep3() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Neutral);
		cmd.add(Command.Neutral);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 1, 5);
		player.step();
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player ne doit pas tomber du rail en (1,4)
		assert(player.getCol() == 1);
		assert(player.getHgt() == 4);
	}

		@Test
		public void testStep4() {
			//Conditions initiales
			List<Command> cmd = new ArrayList<Command>();
			cmd.add(Command.Neutral);
			Engine eg = createEngine(cmd);
			player.init(eg.getEnvironment(), eg, 2, 3);
			//Opération
			player.step();
			//Oracle: vérifié par contrats + player ne doit pas tomber à cause de garde en (2,2)
			assert(player.getCol() == 2);
			assert(player.getHgt() == 3);
		}

	@Test
	public void testStep5() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Left);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 3, 2);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player s'est déplacé en (2,2)
		assert(player.getCol() == 2);
		assert(player.getHgt() == 2);
	}

	@Test
	public void testStep6() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Right);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 3, 2);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player s'est déplacé en (4,2)
		assert(player.getCol() == 4);
		assert(player.getHgt() == 2);
	}

	@Test
	public void testStep7() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Right);
		cmd.add(Command.Up);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 6, 2);
		player.step();
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player est monté sur l'échelle de (7,2) à (7,3)
		assert(player.getCol() == 7);
		assert(player.getHgt() == 3);
	}

	@Test
	public void testStep8() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Down);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 7, 5);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player a descendu l'échelle de (7,5) à (7,4)
		assert(player.getCol() == 7);
		assert(player.getHgt() == 4);
	}

	@Test
	public void testStep9() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.DigL);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 1, 2);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player a dig la case en bas à gauche
		assert(player.getEnvi().getCellNature(0, 1) == Cell.HOL);
	}

	@Test
	public void testStep10() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.DigL);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 8, 5);
		//Opération
		player.step();
		//Oracle: vérifié par contrats
	}

	@Test
	public void testStep11() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Down);
		cmd.add(Command.DigL);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 7, 5);
		player.step();
		//Opération
		player.step();
		//Oracle: vérifié par contrats
	}

	@Test
	public void testStep12() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.DigR);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 1, 2);
		//Opération
		player.step();
		//Oracle: vérifié par contrats + player a dig la case en bas à droite 
		assert(player.getEnvi().getCellNature(2, 1) == Cell.HOL);
	}

	@Test
	public void testStep13() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.DigR);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 6, 5);
		//Opération
		player.step();
		//Oracle: vérifié par contrats
	}

	@Test
	public void testStep14() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Down);
		cmd.add(Command.DigR);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 7, 5);
		player.step();
		//Opération
		player.step();
		//Oracle: vérifié par contrats
	}

    @Test
    public void testTeleportTrans1() {
        // Conditions initiales
        Engine eg = createEngine(new ArrayList<>());
        player.init(eg.getEnvironment(), eg, 0, 2);
        // Opération
        player.teleport(8, 5);
        // Oracle: vérifié par les contrats
    }

    @Test
    public void testGrabKeyTrans1() {
        // Conditions initiales
        Engine eg = createEngine(new ArrayList<>());
        player.init(eg.getEnvironment(), eg, 0, 2);
        // Opération
        player.grabKey();
        // Oracle: vérifié par les contrats + le joueur à une clé en plus
        Assert.assertEquals(1, player.getNbKeys());
    }

	//==================
	//Etats remarquables
	//==================

	@Test
	public void testUseHandRails() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.Left);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 4, 5);
		player.step(); //Player tombe sur le rail
		//Opération
		player.step();
		//Oracle: player en (3,4)
		assert(player.getCol() == 3);
		assert(player.getHgt() == 4);
	}

    @Test
    public void testOpenDoor() {
        // Etat initial
        List<Command> cmd = new ArrayList<Command>();
        cmd.add(Command.DigL);
        Engine eg = createEngine(cmd);
        eg.getPlayer().grabKey(); eg.getPlayer().teleport(9, 2);
        // Opération
        eg.step();
        // Oracle: vérifié par les contrats + la porte à gauche est ouverte
        Assert.assertEquals(Cell.EMP, eg.getEnvironment().getCellNature(8, 2));
    }

	//========
	//Scénario
	//========

	@Test
	public void testScenar3() {
		//Conditions initiales
		List<Command> cmd = new ArrayList<Command>();
		cmd.add(Command.DigL);
		cmd.add(Command.Left);
		cmd.add(Command.DigR);
		cmd.add(Command.Left);
		cmd.add(Command.Up);
		cmd.add(Command.Up);
		cmd.add(Command.Up);
		cmd.add(Command.Left);
		cmd.add(Command.Left);
		Engine eg = createEngine(cmd);
		player.init(eg.getEnvironment(), eg, 9, 5);
		//Opérations
		player.step(); //DigL
		player.step(); //goLeft
		player.step(); //fall
		player.step(); //fall
		player.step(); //fall
		player.step(); //DigR
		player.step(); //goLeft
		player.step(); //goUp
		player.step(); //goUp
		player.step(); //goUp
		player.step(); //goLeft
		player.step(); //goLeft
		//Oracle
		assert(player.getEnvi().getCellNature(8, 4) == Cell.HOL);
		assert(player.getEnvi().getCellNature(9, 1) == Cell.HOL);
		assert(player.getCol() == 5);
		assert(player.getHgt() == 5);
	}
}
