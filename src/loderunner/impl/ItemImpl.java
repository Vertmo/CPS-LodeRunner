package loderunner.impl;

import loderunner.services.Item;
import loderunner.services.ItemType;

public class ItemImpl implements Item {
    private static int idCounter = 0;

    private int id;
    private ItemType nature;
    private int col, hgt;

    public ItemImpl(ItemType nature, int col, int hgt) {
        this.nature = nature;
        this.col = col; this.hgt = hgt;
        this.id = idCounter; idCounter++;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ItemType getNature() {
        return nature;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public int getHgt() {
        return hgt;
	}

	@Override
	public void setCol(int col) {
      this.col = col;
	}

	@Override
	public void setHgt(int hgt) {
      this.hgt = hgt;
	}
}
