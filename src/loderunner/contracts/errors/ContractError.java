package loderunner.contracts.errors;

public class ContractError extends Error {
    private static final long serialVersionUID = 19027497249028404L;

    public ContractError(String service, String message) {
        super("Service " + service + ": " + message);
    }
}
