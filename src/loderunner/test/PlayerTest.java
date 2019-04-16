package loderunner.test;

import loderunner.contracts.PlayerContract;
import loderunner.impl.PlayerImpl;

public class PlayerTest extends AbstractPlayerTest {

	@Override
	public void beforeTests() {
		setPlayer(new PlayerContract(new PlayerImpl()));
	}
}