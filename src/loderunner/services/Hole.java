package loderunner.services;

public interface Hole extends Cloneable {
    public int getCol();
    public int getHgt();
    public int getT();
    public void incT();

    public Hole clone();
}
