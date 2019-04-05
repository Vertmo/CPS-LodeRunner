package loderunner.impl;

import java.util.Set;

import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.InCell;
import loderunner.services.Player;

public class PlayerImpl extends CharacterImpl implements Player {
	private Engine engine;

	public Engine getEngine() {
		return engine;
	}

	/**
	 * Initialisation du joueur avec l'environnement, l'engine et la position
	 */
	public void init(Environment e, Engine eg, int x, int y) {
		super.init(e, x, y);
		engine = eg;
	}

	/**
	 * Vérifie si le joueur doit tomber
	 * (s'il se trouve dans le vide sans pouvoir s'accrocher à une échelle ou une rampe)
	 * @return
	 */
	private boolean willfall() {
		Cell down_nat = getEnvi().getCellNature(getCol(), getHgt()-1);
		Cell in = getEnvi().getCellNature(getCol(), getHgt());
		Set<InCell> down_content = getEnvi().getCellContent(getCol(), getHgt()-1);
		boolean perso_present = false;
		for(InCell c : down_content) {
			if (c instanceof loderunner.services.Character) {
				perso_present =true;
				break;
			}
		}
		return !(in == Cell.LAD || in == Cell.HDR) && 
				(down_nat == Cell.HOL || down_nat == Cell.EMP) &&
				!perso_present;
	}

	/**
	 * Traite la commande Dig (gauche ou droite suivant le paramètre)
	 * @param cmd
	 */
	private void instrDig(Command cmd) {
		Cell down_nat = getEnvi().getCellNature(getCol(), getHgt()-1);
		Set<InCell> down_content = getEnvi().getCellContent(getCol(), getHgt()-1);;
		boolean perso_present = false;

		if(getCol() != 0) {
			for(InCell c : down_content) {
				if (c instanceof loderunner.services.Character) {
					perso_present =true;
					break;
				}
			}

			if(perso_present || down_nat == Cell.PLT || down_nat == Cell.MTL || down_nat == Cell.LAD) {
				if(cmd == Command.DigL) {
					if(getEnvi().getCellNature(getCol()-1, getHgt()-1) == Cell.PLT) {
						getEnvi().dig(getCol()-1, getHgt()-1);
					}
				}else {
					if(getEnvi().getCellNature(getCol()+1, getHgt()-1) == Cell.PLT) {
						getEnvi().dig(getCol()+1, getHgt()-1);
					}
				}
			}
		}
	}

	/**
	 * Traite le prochain comportement du joueur
	 */
	public void step() {
		if(willfall()) {
			goDown();
		}else {
			Command cmd = engine.getNextCommand();
			switch(cmd) {
			case DigL :
				instrDig(Command.DigL);
				break;

			case DigR :
				instrDig(Command.DigR);
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
}
