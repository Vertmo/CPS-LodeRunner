package loderunner.services;

public interface Item extends InCell {
    public int getId();
    public ItemType getNature();
    public int getCol();
    public int getHgt();
}
