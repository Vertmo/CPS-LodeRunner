package loderunner.test;

import loderunner.contracts.PlayerContract;
import loderunner.impl.PlayerImplBug;

public class PlayerTestBug extends AbstractPlayerTest {

	@Override
	public void beforeTests() {
		setPlayer(new PlayerContract(new PlayerImplBug()));
	}
}