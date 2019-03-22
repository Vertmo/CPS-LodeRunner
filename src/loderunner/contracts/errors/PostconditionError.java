package loderunner.contracts.errors;

public class PostconditionError extends ContractError {
    private static final long serialVersionUID = 19027497249028406L;

    public PostconditionError(String service, String method, String error) {
        super(service, "Postcondition Error: " + method + ": " + error);
    }
}
