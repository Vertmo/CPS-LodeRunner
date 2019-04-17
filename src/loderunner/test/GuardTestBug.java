package loderunner.test;

import loderunner.contracts.GuardContract;
import loderunner.impl.GuardImplBug;

public class GuardTestBug extends AbstractGuardTest {

	@Override
	public void beforeTests() {
      setGuard(new GuardContract(new GuardImplBug()));
	}
}
