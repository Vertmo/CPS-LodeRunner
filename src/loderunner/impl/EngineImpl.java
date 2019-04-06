package loderunner.impl;

import java.util.HashSet;
import java.util.Set;

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

public class EngineImpl implements Engine {
    private CommandProvider cmdProvider;
    private Environment env;
    private Player player;
    private Set<Guard> guards;
    private Set<Item> treasures;
    private Set<Hole> holes;
    private Status status;
    private int levelScore;

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
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords) {
        env = new EnvironmentImpl();
        env.init(screen);
        player = new PlayerImpl();
        player.init(env, this, pCoord.getCol(), pCoord.getHgt());
        env.addCellContent(pCoord.getCol(), pCoord.getHgt(), player);
        levelScore = 0;

        guards = new HashSet<>();
        for(Coord c: gCoords) {
            // TODO les gardes
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
        // Step for everyone
        getPlayer().step();
        for(Guard g: getGuards()) {
            g.step();
            if(g.getCol() == player.getCol() && g.getHgt() == player.getHgt()) status = Status.Loss;
        }

        // Update environment
        for(int x = 0; x < env.getWidth(); x++) {
            for(int y = 0; y < env.getHeight(); y++) {
                for(InCell ic: env.getCellContent(x, y)) {
                    if(ic instanceof Player) env.removeCellContent(x, y, ic);
                    if(ic instanceof Item && ((Item)ic).getNature()==ItemType.Treasure) {
                        if(getPlayer().getCol() == x && getPlayer().getHgt() == y) {
                            env.removeCellContent(x, y, ic);
                            treasures.remove(ic);
                            levelScore++;
                        }
                    }
                    // TODO
                }
            }
        }
        env.addCellContent(getPlayer().getCol(), getPlayer().getHgt(), getPlayer());

        // Update status TODO
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
