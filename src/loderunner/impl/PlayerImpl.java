package loderunner.impl;

import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;
import loderunner.services.Player;

public class PlayerImpl extends CharacterImpl implements Player{
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
        if(cell_below == Cell.MTL || cell_below == Cell.PLT || cell_below == Cell.LAD || cell_below == Cell.TRP || cell_below == Cell.DOR)
            return false;
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
            if((left_nat == Cell.EMP || left_nat == Cell.HOL) && left_content_is_empty &&
               getEnvi().getCellNature(getCol()-1, getHgt()-1) == Cell.PLT) {
                getEnvi().dig(getCol()-1, getHgt()-1);
            }
            if(left_nat == Cell.DOR && getNbKeys() > 0) {
                nbKeys--;
                getEnvi().openDoor(getCol()-1, getHgt());
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
            if((right_nat == Cell.EMP || right_nat == Cell.HOL) && right_content_is_empty &&
               getEnvi().getCellNature(getCol()+1, getHgt()-1) == Cell.PLT) {
                getEnvi().dig(getCol()+1, getHgt()-1);
            }
            if(right_nat == Cell.DOR && getNbKeys() > 0) {
                nbKeys--;
                getEnvi().openDoor(getCol()+1, getHgt());
            }
        }
    }

    /**
     * Traite la commande ShootL
     */
    private void instrShootL() {
        if(getEngine().getNumberBullets() > 0) {
            for(int j=getCol()-1;j>=0;j--) {
                Cell cell_nat = getEnvi().getCellNature(j, getHgt());
                if(cell_nat == Cell.MTL || cell_nat == Cell.PLT || cell_nat == Cell.DOR || cell_nat == Cell.TRP) {
                    return;
                }else {
                    for(InCell content : getEnvi().getCellContent(j, getHgt())){
                        if(content instanceof Guard) {
                            Guard g = (Guard) content;
                            g.setIsShot(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Traite la commande ShootR
     */
    private void instrShootR() {
        if(getEngine().getNumberBullets() > 0) {
            for(int j=getCol()+1;j<getEnvi().getWidth();j++) {
                Cell cell_nat = getEnvi().getCellNature(j, getHgt());
                if(cell_nat == Cell.MTL || cell_nat == Cell.PLT || cell_nat == Cell.DOR || cell_nat == Cell.TRP) {
                    return;
                }else {
                    for(InCell content : getEnvi().getCellContent(j, getHgt())){
                        if(content instanceof Guard) {
                            Guard g = (Guard) content;
                            g.setIsShot(true);
                            return;
                        }
                    }
                }
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

            case ShootL :
                instrShootL();
                break;

            case ShootR :
                instrShootR();
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
     * Clone l'instance courant mais l'environnement et l'engine ne sont pas cloné
     */
    @Override
    public Player clone() {
        PlayerImpl clone = new PlayerImpl();
        clone.init(getEnvi(), getEngine(), getCol(), getHgt());;
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Player)) return false;
        Player p = (Player) o;
        return p.getCol() == getCol() && p.getHgt() == getHgt();
    }

    @Override
    public int hashCode() {
        return -1; // Pour les gardes ce sera leur id
    }

    @Override
    public int getNbKeys() {
        return nbKeys;
    }

    @Override
    public void grabKey() {
        nbKeys++;
    }
}
