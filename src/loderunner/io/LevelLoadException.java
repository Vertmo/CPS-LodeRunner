package loderunner.io;

public class LevelLoadException extends Exception {
    private static final long serialVersionUID = 19027497249028410L;

    public LevelLoadException(String filename) {
        super("The file " + filename + " does not contain a valid level syntax");
    }
}
