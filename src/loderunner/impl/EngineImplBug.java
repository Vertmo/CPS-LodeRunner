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
import loderunner.services.Status;

public class EngineImplBug implements Engine {
    private CommandProvider cmdProvider;
    private Environment env;
    private Player player;
    private Set<Guard> guards;
    private Set<Item> treasures;
    private Set<Hole> holes;
    private Status status;
    private int levelScore;

    public EngineImplBug(CommandProvider cmdProvider) {
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
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords) {
        env = new EnvironmentImpl();
        env.init(screen);
        player = new PlayerImpl();
        player.init(env, this, pCoord.getCol(), pCoord.getHgt());
        env.addCellContent(pCoord.getCol(), pCoord.getHgt(), player);
        levelScore = 0;

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
    }

    @Override
    public void step() {
        // Step for player
        env.removeCellContent(getPlayer().getCol(), getPlayer().getHgt(), getPlayer());
        getPlayer().step();
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
            g.step();
            env.addCellContent(g.getCol(), g.getHgt(), g);
            if(g.getCol() == player.getCol() && g.getHgt() == player.getHgt() && !treasures.isEmpty()) status = Status.Loss;

            if(transported != null) {
                if(env.getCellNature(g.getCol(), g.getHgt()) != Cell.HOL) {
                    transported.setCol(g.getCol()); transported.setHgt(g.getCol()); // OUPS
                    env.addCellContent(g.getCol(), g.getHgt(), transported);
                } else {
                    transported.setCol(g.getCol()); transported.setHgt(g.getHgt()+1);
                    env.addCellContent(g.getCol(), g.getHgt()+1, transported);
                }
            }
        }

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
                // toRemove = t; // OUPS
                levelScore++;
            }
        }
        if(toRemove != null) treasures.remove(toRemove);
        if(treasures.isEmpty()) status = Status.Win;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Engine)) return false;
        Engine e = (Engine) o;
        return e.getEnvironment().equals(getEnvironment())
            && e.getPlayer().equals(getPlayer())
            && e.getGuards().equals(getGuards())
            && e.getTreasures().equals(getTreasures())
            && e.getHoles().equals(getHoles())
            && e.getStatus().equals(getStatus());
    }

    @Override
    public Engine clone() {
        EngineImplBug ei = new EngineImplBug(cmdProvider);
        ei.env = env;
        ei.player = player;
        ei.guards = new HashSet<>(guards);
        ei.treasures = new HashSet<>(treasures);
        ei.holes = new HashSet<>(holes);
        ei.status = status;
        return ei;
    }
}
