package loderunner.contracts.errors;

public class PreconditionError extends ContractError {
    private static final long serialVersionUID = 19027497249028407L;

    public PreconditionError(String service, String method, String error) {
        super(service, "Precondition Error: " + method + ": " + error);
    }
}
