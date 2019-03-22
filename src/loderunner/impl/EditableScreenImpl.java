package loderunner.impl;

import loderunner.services.Cell;
import loderunner.services.EditableScreen;

public class EditableScreenImpl extends ScreenImpl implements EditableScreen {

	@Override
	public boolean isPlayable() {
      for(int x = 0; x < getWidth(); x++) {
          for(int y = 0; y < getHeight(); y++) {
              if(getCellNature(x, y) == Cell.HOL) return false;
          }
      }
      for(int x = 0; x < getWidth(); x++) {
          if(getCellNature(x, 0) != Cell.MTL) return false;
      }
      return true;
	}

	@Override
	public void setNature(int x, int y, Cell c) {
      cells[y][x] = c;
	}
}
