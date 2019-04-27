package loderunner.impl;

import java.util.HashSet;
import java.util.Set;

import loderunner.services.Cell;
import loderunner.services.Command;
import loderunner.services.CommandProvider;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.Hole;
import loderunner.services.InCell;
import loderunner.services.Item;
import loderunner.services.ItemType;
import loderunner.services.Player;
import loderunner.services.PortalPair;
import loderunner.services.Status;

public class EngineImpl implements Engine {
    private CommandProvider cmdProvider;
    private Environment env;
    private Player player;
    private Set<Guard> guards;
    private Set<Item> treasures;
    private Set<Hole> holes;
    private Status status;
    private int levelScore;
    private boolean guardTurn;
    private Set<PortalPair> portals;

    public EngineImpl(CommandProvider cmdProvider) {
        this.cmdProvider = cmdProvider;
    }

    @Override
    public Environment getEnvironment() {
        return env;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Set<Guard> getGuards() {
        return guards;
    }

    @Override
    public Set<Item> getTreasures() {
        return treasures;
    }

    @Override
    public Set<Hole> getHoles() {
        return holes;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public int getLevelScore() {
        return levelScore;
    }

    @Override
    public Command getNextCommand() {
        return cmdProvider.getNextCommand();
    }

    @Override
    public Command peekNextCommand() {
        return cmdProvider.peekNextCommand();
    }

    @Override
    public boolean isGuardTurn() {
        return guardTurn;
    }

    @Override
    public Set<PortalPair> getPortals() {
        return portals;
    }

    @Override
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords, Set<PortalPair> portals) {
        env = new EnvironmentImpl();
        env.init(screen);
        player = new PlayerImpl();
        player.init(env, this, pCoord.getCol(), pCoord.getHgt());
        env.addCellContent(pCoord.getCol(), pCoord.getHgt(), player);
        levelScore = 0;
        this.portals = new HashSet<>(portals);

        guards = new HashSet<>();
        for(Coord c: gCoords) {
            Guard g = new GuardImpl();
            g.init(env, player, c.getCol(), c.getHgt());
            guards.add(g);
            env.addCellContent(c.getCol(), c.getHgt(), g);
        }

        treasures = new HashSet<>();
        for(Coord c: tCoords) {
            Item t = new ItemImpl(ItemType.Treasure, c.getCol(), c.getHgt());
            treasures.add(t);
            env.addCellContent(c.getCol(), c.getHgt(), t);
        }

        holes = new HashSet<>();
        status = Status.Playing;
        guardTurn = false;
    }

    @Override
    public void step() {
        // Step for player
        env.removeCellContent(getPlayer().getCol(), getPlayer().getHgt(), getPlayer());
        PortalPair portal = null;
        for(PortalPair pp: getPortals()) {
            if(pp.getInPCoord().getCol() == getPlayer().getCol() && pp.getInPCoord().getHgt() == getPlayer().getHgt())
                portal = pp;
        }
        if(portal != null) {
            getPlayer().teleport(portal.getOutPCoord().getCol(), portal.getOutPCoord().getHgt());
        } else {
            getPlayer().step();
        }
        env.addCellContent(getPlayer().getCol(), getPlayer().getHgt(), getPlayer());

        // Step for guards
        for(Guard g: getGuards()) {
            Item transported = null;
            for(InCell ic: env.getCellContent(g.getCol(), g.getHgt())) {
                if(ic instanceof Item && ((Item) ic).getNature() == ItemType.Treasure) {
                    env.removeCellContent(g.getCol(), g.getHgt(), ic);
                    transported = (Item)ic;
                    break;
                }
            }
            env.removeCellContent(g.getCol(), g.getHgt(), g);
            if(guardTurn) g.step();
            env.addCellContent(g.getCol(), g.getHgt(), g);
            if(g.getCol() == player.getCol() && g.getHgt() == player.getHgt() && !treasures.isEmpty()) status = Status.Loss;

            if(transported != null) {
                if(env.getCellNature(g.getCol(), g.getHgt()) != Cell.HOL) {
                    transported.setCol(g.getCol()); transported.setHgt(g.getHgt());
                    env.addCellContent(g.getCol(), g.getHgt(), transported);
                } else {
                    transported.setCol(g.getCol()); transported.setHgt(g.getHgt()+1);
                    env.addCellContent(g.getCol(), g.getHgt()+1, transported);
                }
            }
        }
        guardTurn = !guardTurn;

        // Update holes
        Set<Hole> newHoles = new HashSet<>(holes);
        for(Hole h: holes) {
            if(h.getT() == 15) {
                newHoles.remove(h);
                env.fill(h.getCol(), h.getHgt());
                if(getPlayer().getCol() == h.getCol() && getPlayer().getHgt() == h.getHgt())
                    status = Status.Loss;
                for(InCell ic: env.getCellContent(h.getCol(), h.getHgt())) {
                    if(ic instanceof Guard) {
                        Guard g = (Guard) ic;
                        env.removeCellContent(g.getCol(), g.getHgt(), g);
                        g.init(g.getEnvi(), g.getTarget(), g.getInitCol(), g.getInitHgt());
                        env.addCellContent(g.getCol(), g.getHgt(), g);
                    }
                }
            }
        }
        holes = newHoles;
        for(Hole h: holes) h.incT();

        // Create eventual new holes
        for(int x = 0; x < env.getWidth(); x++) {
            for(int y = 0; y < env.getHeight(); y++) {
                if(env.getCellNature(x, y) == Cell.HOL) holes.add(new HoleImpl(x, y));
            }
        }

        // Update items if grabbed by the player
        boolean guard_found = false;
        for(Guard g: guards) {
            if(g.getCol() == player.getCol() && g.getHgt() == player.getHgt()) guard_found = true;
        }
        Item toRemove = null;
        for(Item t: getTreasures()) {
            if(getPlayer().getCol() == t.getCol() && getPlayer().getHgt() == t.getHgt() && !guard_found) {
                env.removeCellContent(t.getCol(), t.getHgt(), t);
                toRemove = t;
                levelScore++;
            }
        }
        if(toRemove != null) treasures.remove(toRemove);
        if(treasures.isEmpty()) status = Status.Win;

        // Trigger traps
        if(getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()-1) == Cell.TRP) {
            getEnvironment().triggerTrap(getPlayer().getCol(), getPlayer().getHgt()-1);
        }
        for(Guard g: getGuards()) {
            if(getEnvironment().getCellNature(g.getCol(), g.getHgt()-1) == Cell.TRP) {
                getEnvironment().triggerTrap(g.getCol(), g.getHgt()-1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Engine)) return false;
        Engine e = (Engine) o;
        // Les égalités d'ensemble c'est compliqué...
        for(Guard g1: getGuards()) {
            boolean guard_found = false;
            for(Guard g2: e.getGuards()) {
                if(g1.equals(g2)) guard_found = true;
            }
            if(!guard_found) return false;
        }
        for(Guard g1: e.getGuards()) {
            boolean guard_found = false;
            for(Guard g2: getGuards()) {
                if(g1.equals(g2)) guard_found = true;
            }
            if(!guard_found) return false;
        }
        for(Item t1: getTreasures()) {
            boolean treasure_found = false;
            for(Item t2: e.getTreasures()) {
                if(t1.equals(t2)) treasure_found = true;
            }
            if(!treasure_found) return false;
        }
        for(Item t1: e.getTreasures()) {
            boolean treasure_found = false;
            for(Item t2: getTreasures()) {
                if(t1.equals(t2)) treasure_found = true;
            }
            if(!treasure_found) return false;
        }
        return e.getPlayer().equals(getPlayer())
            && e.getHoles().equals(getHoles())
            && e.getStatus().equals(getStatus());
    }

    @Override
    public Engine clone() {
        EngineImpl ei = new EngineImpl(cmdProvider);
        ei.env = env;
        ei.player = player;
        ei.guards = new HashSet<>(guards);
        ei.treasures = new HashSet<>(treasures);
        ei.holes = new HashSet<>(holes);
        ei.status = status;
        return ei;
	}

}
