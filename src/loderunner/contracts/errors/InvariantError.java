package loderunner.contracts.errors;

public class InvariantError extends ContractError {
    private static final long serialVersionUID = 19027497249028405L;

    public InvariantError(String service, String error) {
        super(service, "Invariant Error: " + error);
    }
}
