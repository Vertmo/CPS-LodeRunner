package loderunner.io;

public class LevelLoadException extends Exception {
    private static final long serialVersionUID = 19027497249028410L;

    public LevelLoadException(String filename, String msg) {
        super("Loading the level " + filename + " caused the error: " + msg);
    }
}
