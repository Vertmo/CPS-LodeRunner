package loderunner.services;

public interface Item extends InCell, Cloneable {
    public int getId();
    public ItemType getNature();
    public int getCol();
    public int getHgt();
    public void setCol(int col);
    public void setHgt(int hgt);
}
