package loderunner.test;

import loderunner.contracts.GuardContract;
import loderunner.impl.GuardImpl;

public class GuardTest extends AbstractGuardTest {

	@Override
	public void beforeTests() {
      setGuard(new GuardContract(new GuardImpl()));
	}
}
