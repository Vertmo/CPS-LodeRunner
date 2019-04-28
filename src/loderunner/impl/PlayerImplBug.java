package loderunner.impl;

import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.InCell;
import loderunner.services.Player;

public class PlayerImplBug extends CharacterImpl implements Player{
	private Engine engine;
    private int nbKeys;

	@Override
	public Engine getEngine() {
		return engine;
	}

	/**
	 * Initialisation du joueur avec l'environnement, l'engine et la position
	 */
	@Override
	public void init(Environment e, Engine eg, int x, int y) {
		super.init(e, x, y);
		engine = eg;
    nbKeys = 0;
	}

	/**
	 * Vérifie si le joueur doit tomber
	 * (s'il se trouve dans le vide sans pouvoir s'accrocher à une échelle ou une rampe)
	 * @return
	 */
	private boolean willFall() {
		Cell cell_below = getEnvi().getCellNature(getCol(), getHgt()-1);
		if(cell_below == Cell.MTL || cell_below == Cell.PLT || cell_below == Cell.LAD ) return false;
		for(InCell ic: getEnvi().getCellContent(getCol(), getHgt()-1)) {
			if(ic instanceof loderunner.services.Character) return false;
		}
		Cell c = getEnvi().getCellNature(getCol(), getHgt());
		if(c == Cell.LAD || c == Cell.HDR) return false;
		return true;
	}

	/**
	 * Traite la commande DigL
	 */
	private void instrDigL() {
		if(getCol() != 0) {
			Cell left_nat = getEnvi().getCellNature(getCol()-1, getHgt());
			boolean left_content_is_empty = getEnvi().getCellContent(getCol()-1, getHgt()).isEmpty();
			if(left_nat != Cell.PLT && left_nat != Cell.MTL && left_content_is_empty &&
					getEnvi().getCellNature(getCol()-1, getHgt()-1) == Cell.PLT) {
				getEnvi().dig(getCol()-1, getHgt()-1);
			}
		}
	}

	/**
	 * Traite la commande DigR
	 */
	private void instrDigR() {
		if(getCol() != getEnvi().getWidth()-1) {
			Cell right_nat = getEnvi().getCellNature(getCol()+1, getHgt());
			boolean right_content_is_empty = getEnvi().getCellContent(getCol()+1, getHgt()).isEmpty();
			if(right_nat != Cell.PLT && right_nat != Cell.MTL && right_content_is_empty &&
					getEnvi().getCellNature(getCol()+1, getHgt()-1) == Cell.PLT) {
				getEnvi().dig(getCol()-1, getHgt()-1);//bug
			}
		}
	}

	/**
	 * Traite le prochain comportement du joueur
	 */
	@Override
	public void step() {
		if(willFall()) {
			goDown();
		}else {
			Command cmd = engine.getNextCommand();
			switch(cmd) {
			case DigL :
				instrDigL();
				break;

			case DigR :
				instrDigR();
				break;

			case Down:
				goDown();
				break;

			case Up :
				goUp();
				break;

			case Left :
				goLeft();
				break;

			case Right :
				goRight();
				break;

			default:
				break;
			}
		}
	}

    @Override
    public void teleport(int x, int y) {
        col = x; hgt = y;
    }

	/**
	 * Vérifie l'égalité de l'instance avec l'instance o
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(this == o) {
			return true;
		}
		if(!(o instanceof Player)) {
			return false;
		}
		Player p = (Player) o;
		return p.getCol() == getCol() && p.getHgt() == p.getHgt() &&
				p.getEngine().equals(getEngine()) && p.getEnvi().equals(getEnvi());
	}

	/**
	 * Clone l'instance courant mais l'environnement et l'engine ne sont pas cloné
	 */
	@Override
	public Player clone() {
		PlayerImpl clone = new PlayerImpl();
		clone.init(getEnvi(), getEngine(), getCol(), getHgt());;
		return clone;
	}

	@Override
	public int getNbKeys() {
		return nbKeys;
	}

	@Override
	public void grabKey() {
      // nbKeys++; // OUPS
	}
}
