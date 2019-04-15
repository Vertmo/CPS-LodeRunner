package loderunner.test;

import org.junit.Test;

import loderunner.services.Player;

public abstract class AbstractPlayerTest extends AbstractCharacterTest{
	private Player player;

	public void setPlayer(Player player) {
		super.setCharacter(player);
		this.player = player;
	}

	// Precondition

	@Test
	public void testInitPre1(){

	}
}
