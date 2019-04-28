package loderunner.contracts;

import java.util.HashSet;
import java.util.Set;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.decorators.EngineDecorator;
import loderunner.services.Cell;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Guard;
import loderunner.services.Hole;
import loderunner.services.InCell;
import loderunner.services.Item;
import loderunner.services.ItemType;
import loderunner.services.Player;
import loderunner.services.PortalPair;
import loderunner.services.Status;

public class EngineContract extends EngineDecorator {

    public EngineContract(Engine delegate) {
        super(delegate);
    }

    public void checkInvariant() {
        // inv: getPlayer() \in getEnvironment().getCellContent(getPlayer().getCol(), getPlayer().getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           getPlayer() \in getEnvironment().getCellContent(x, y)
        //           => (getPlayer().getCol() == x && getPlayer().getHgt() == y)
        if(!getEnvironment().getCellContent(getPlayer().getCol(), getPlayer().getHgt()).contains(getPlayer()))
            throw new InvariantError("Engine", "The player is not in the correct cell");
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                for(InCell ic: getEnvironment().getCellContent(x, y)) {
                    if(ic instanceof Player &&
                       (x != getPlayer().getCol() || y != getPlayer().getHgt()))
                        throw new InvariantError("Engine", "The player is not in the correct cell");
                }
            }
        }
        // inv: \forall Guard g \in getGuards() g \in getEnvironment().getCellContent(g.getCol(), g.getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           \forall Guard g \in getEnvironment().getCellContent(x, y)
        //           => g \in getGuards() && (g.getCol() == x && g.getHgt() == y)
        for(Guard g: getGuards()) {
            if(!getEnvironment().getCellContent(g.getCol(), g.getHgt()).contains(g))
                throw new InvariantError("Engine", "A guard is not in the correct cell");
        }
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                for(InCell ic: getEnvironment().getCellContent(x, y)) {
                    if(ic instanceof Guard) {
                        Guard g = (Guard) ic;
                        if(!getGuards().contains(g) || g.getCol() != x || g.getHgt() != y)
                            throw new InvariantError("Engine", "A guard is not in the correct cell");
                    }
                }
            }
        }
        // inv: \forall Item i \in getTreasures() i \in getEnvironment().getCellContent(i.getCol(), i.getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           \forall Item i \in getEnvironment().getCellContent(x, y) i.getNature() == Treasure
        //           => i \in getTreasures() && (i.getCol() == x && i.getHgt() == y)
        for(Item i: getTreasures()) {
            if(!getEnvironment().getCellContent(i.getCol(), i.getHgt()).contains(i))
                throw new InvariantError("Engine", "A treasure is not in the correct cell");
        }
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                for(InCell ic: getEnvironment().getCellContent(x, y)) {
                    if(ic instanceof Item && ((Item)ic).getNature()==ItemType.Treasure) {
                        Item t = (Item) ic;
                        if(!getTreasures().contains(t) || t.getCol() != x || t.getHgt() != y)
                            throw new InvariantError("Engine", "A treasure is not in the correct cell");
                    }
                }
            }
        }
        // inv: \forall Item i \in getKeys() i \in getEnvironment().getCellContent(i.getCol(), i.getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           \forall Item i \in getEnvironment().getCellContent(x, y) i.getNature() == Key
        //           => i \in getKeys() && (i.getCol() == x && i.getHgt() == y)
        for(Item i: getKeys()) {
            if(!getEnvironment().getCellContent(i.getCol(), i.getHgt()).contains(i))
                throw new InvariantError("Engine", "A treasure is not in the correct cell");
        }
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                for(InCell ic: getEnvironment().getCellContent(x, y)) {
                    if(ic instanceof Item && ((Item)ic).getNature()==ItemType.Key) {
                        Item k = (Item) ic;
                        if(!getKeys().contains(k) || k.getCol() != x || k.getHgt() != y)
                            throw new InvariantError("Engine", "A treasure is not in the correct cell");
                    }
                }
            }
        }
        // inv: \forall Hole h \in getHoles() getEnvironment().getCellNature(h.getCol(), g.getHgt()) == HOL
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           getEnvironment().getCellNature(x, y) == HOL
        //           => \exists Hole h \in getHoles() (h.getCol() == x && h.getHgt() == y)
        for(Hole h: getHoles()) {
            if(getEnvironment().getCellNature(h.getCol(), h.getHgt()) != Cell.HOL)
                throw new InvariantError("Engine", "A cell is not hol as it should be");
        }
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                if(getEnvironment().getCellNature(x, y) == Cell.HOL) {
                    boolean found = false;
                    for(Hole h: getHoles()) {
                        if(h.getCol() == x && h.getHgt() == y) found = true;
                    }
                    if(!found) throw new InvariantError("Engine", "A hole is missing in the list");
                }
            }
        }
    }

    @Override
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords, Set<Coord> kCoords, Set<PortalPair> portals) {
        // pre: screen.isPlayable()
        if(!screen.isPlayable())
            throw new PreconditionError("Engine", "init", "screen.isPlayable()");
        // pre: \forall Coord c \in { pCoord } union gCoords union tCoords union kCoords
        //        screen.getCellNature(c.getCol(), c.getHgt()) == EMP
        if(screen.getCellNature(pCoord.getCol(), pCoord.getHgt()) != Cell.EMP)
            throw new PreconditionError("Engine", "init", "The player is not on an empty cell");
        for(Coord c: gCoords) {
            if(screen.getCellNature(c.getCol(), c.getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "A guard is not on an empty cell");
        }
        for(Coord c: tCoords) {
            if(screen.getCellNature(c.getCol(), c.getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "A treasure is not on an empty cell");
        }
        for(Coord c: kCoords) {
            if(screen.getCellNature(c.getCol(), c.getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "A key is not on an empty cell");
        }
        // pre: \forall Coord c1 \in gCoords \forall Coord c2 \in gCoords
        //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
        for(Coord c1: gCoords) {
            for(Coord c2: gCoords) {
                if(c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt() && c1 != c2)
                    throw new PreconditionError("Engine", "init", "Two guards are on the same cell");
            }
        }
        // pre: \forall Coord c1 \in tCoords \forall Coord c2 \in tCoords
        //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
        for(Coord c1: tCoords) {
            for(Coord c2: tCoords) {
                if(c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt() && c1 != c2)
                    throw new PreconditionError("Engine", "init", "Two treasures are on the same cell");
            }
        }
        // pre: \forall Coord c1 \in kCoords \forall Coord c2 \in kCoords
        //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
        for(Coord c1: kCoords) {
            for(Coord c2: kCoords) {
                if(c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt() && c1 != c2)
                    throw new PreconditionError("Engine", "init", "Two keys are on the same cell");
            }
        }
        // pre: \forall Coord c \in gCoords
        //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
        for(Coord c: gCoords) {
            if(c.getCol() == pCoord.getCol() && c.getHgt() == pCoord.getHgt())
                throw new PreconditionError("Engine", "init", "A guard is on the same cell as the player");
        }
        // pre: \forall Coord c \in tCoords
        //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
        for(Coord c: tCoords) {
            if(c.getCol() == pCoord.getCol() && c.getHgt() == pCoord.getHgt())
                throw new PreconditionError("Engine", "init", "A treasure is on the same cell as the player");
        }
        // pre: \forall Coord c \in kCoords
        //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
        for(Coord c: kCoords) {
            if(c.getCol() == pCoord.getCol() && c.getHgt() == pCoord.getHgt())
                throw new PreconditionError("Engine", "init", "A key is on the same cell as the player");
        }

        // pre: \forall PortalPair pp \in portals
        //        screen.getCellNature(pp.getInPCoord().getCol(), pp.getInPCoord().getHgt()) == EMP
        //        && screen.getCellNature(pp.getOutPCoord().getCol(), pp.getOutPCoord().getHgt()) == EMP
        for(PortalPair pp: portals) {
            if(screen.getCellNature(pp.getInPCoord().getCol(), pp.getInPCoord().getHgt()) != Cell.EMP
               || screen.getCellNature(pp.getOutPCoord().getCol(), pp.getOutPCoord().getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "Every portal should be on an empty cell");
        }

        // run
        super.init(screen, pCoord, gCoords, tCoords, kCoords, portals);

        // post-invariant
        checkInvariant();

        // post: getStatus() == Playing
        if(getStatus() != Status.Playing)
            throw new PostconditionError("Engine", "init", "getStatus() == Playing");
        // post: getLevelScore() == 0
        if(getLevelScore() != 0)
            throw new PostconditionError("Engine", "init", "getLevelScore() == 0");
        // post: !isGuardTurn()
        if(isGuardTurn())
            throw new PostconditionError("Engine", "init", "!isGuardTurn()");
        // post: getEnvironment().getWidth() == screen.getWidth()
        //       && getEnvironment().getHeight() == screen.getHeight()
        //       &&\forall x \in [0..screen.getWidth()[ \forall y \in [0..screen.getHeight()[
        //         getEnvironment().getCellNature(x, y) == screen.getCellNature(x, y)
        if(getEnvironment().getWidth() != screen.getWidth())
            throw new PostconditionError("Engine", "init", "The width is not the same");
        if(getEnvironment().getHeight() != screen.getHeight())
            throw new PostconditionError("Engine", "init", "The height is not the same");
        for(int x = 0; x < screen.getWidth(); x++) {
            for(int y = 0; y < screen.getHeight(); y++) {
                if(getEnvironment().getCellNature(x, y) != screen.getCellNature(x, y))
                    throw new PostconditionError("Engine", "init", "The content of the environment is not the same");
            }
        }
        // post: getPlayer().getCol() == pCoord.getCol() && getPlayer().getHgt() == pCoord.getHgt()
        if(getPlayer().getCol() != pCoord.getCol() || getPlayer().getHgt() != pCoord.getHgt())
            throw new PostconditionError("Engine", "init", "The coords of the player are not the same");
        // post: \forall Coord c \in gCoords \exists Guard g \in getGuards() (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
        //       && \forall Guard g \in getGuards() \exists Coord c \in gCoords (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
        for(Coord c: gCoords) {
            boolean found = false;
            for(Guard g: getGuards()) {
                if(c.getCol() == g.getCol() && c.getHgt() == g.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "A guard is missing");
        }
        for(Guard g: getGuards()) {
            boolean found = false;
            for(Coord c: gCoords) {
                if(c.getCol() == g.getCol() && c.getHgt() == g.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "There are too much guards");
        }
        // post: \forall Coord c \in tCoords \exists Item i \in getTreasures() (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        //       && \forall Item i \in getTreasures() \exists Coord c \in tCoords (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        for(Coord c: tCoords) {
            boolean found = false;
            for(Item t: getTreasures()) {
                if(c.getCol() == t.getCol() && c.getHgt() == t.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "A treasure is missing");
        }
        for(Item t: getTreasures()) {
            boolean found = false;
            for(Coord c: tCoords) {
                if(c.getCol() == t.getCol() && c.getHgt() == t.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "There are too much treasures");
        }
        // post: \forall Coord c \in kCoords \exists Item i \in getKeys() (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        //       && \forall Item i \in getKeys() \exists Coord c \in kCoords (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        for(Coord c: kCoords) {
            boolean found = false;
            for(Item k: getKeys()) {
                if(c.getCol() == k.getCol() && c.getHgt() == k.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "A key is missing");
        }
        for(Item k: getKeys()) {
            boolean found = false;
            for(Coord c: kCoords) {
                if(c.getCol() == k.getCol() && c.getHgt() == k.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "There are too much keys");
        }
        // post: \forall PortalPair pp \in portals pp \in getPortals()
        //       && \forall PortalPair pp \in getPortals() pp \in portals
        for(PortalPair pp: portals) {
            if(!getPortals().contains(pp)) throw new PostconditionError("Engine", "init", "A portal is missing");
        }
        for(PortalPair pp: getPortals()) {
            if(!portals.contains(pp)) throw new PostconditionError("Engine", "init", "There are too much portals");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void step() {
        // pre: getStatus() == Playing
        if(getStatus() != Status.Playing)
            throw new PreconditionError("Engine", "Screen", "getStatus() == Playing");

        // pre-invariant
        checkInvariant();

        // captures
        Set<Item> treasures_pre = new HashSet<>(getTreasures());
        int levelScore_pre = getLevelScore();
        Set<Hole> holes_pre = new HashSet<>();
        for(Hole h: getHoles()) holes_pre.add(h.clone());
        int playerCol_pre = getPlayer().getCol();
        int playerHgt_pre = getPlayer().getHgt();
        Set<InCell>[][] cellContent_pre = new Set[getEnvironment().getWidth()][getEnvironment().getHeight()];
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                cellContent_pre[x][y] = new HashSet<>(getEnvironment().getCellContent(x, y));
            }
        }
        boolean guardTurn_pre = isGuardTurn();
        Cell[][] cellNature_pre = new Cell[getEnvironment().getWidth()][getEnvironment().getHeight()];
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) cellNature_pre[x][y] = getEnvironment().getCellNature(x, y);
        }

        // run
        super.step();

        // post-invariant
        checkInvariant();

        // post: \exists pp \in getPortals() (pp.getCoordPIn().getCol() == (getPlayer()@pre).getCol() && pp.getCoordPIn().getHgt() == (getPlayer()@pre).getHgt())
        //       => getPlayer() == (getPlayer()@pre).teleport(pp.getCoordPOut().getCol(), pp.getCoordPOut().getHgt())
        // post: \not \exists pp \in getPortals() (pp.getCoordPIn().getCol() == (getPlayer()@pre).getCol() && pp.getCoordPIn().getHgt() == (getPlayer()@pre).getHgt())
        //       => getPlayer() == (getPlayer()@pre).step()
        // Il n'est pas raisonnable de chercher à tester cela (ça voudrait dire cloner l'état entier)
        // post: isGuardTurn()@pre => \forall Guard g: getGuards() g == (g@pre).step()
        //       && !isGuardTurn()@pre => \forall Guard g: getGuards() g == g@pre
        // Il n'est pas raisonnable de chercher à tester cela (idem)
        // post: isGuardTurn() == !isGuardTurn()@pre
        if(isGuardTurn() == guardTurn_pre)
            throw new PostconditionError("Engine", "step", "isGuardTurn() == !isGuardTurn()@pre");

        // post: \forall Item t \in getTreasures()@pre
        //         \exists Guard g \in getEnvi().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //           getEnvi().getCellNature(g.getCol(), g.getHgt()) != HOL
        //         => t.getCol() == g.getCol() && t.getHgt() == g.getHgt()
        for(Item i: treasures_pre) {
            for(InCell ic: cellContent_pre[i.getCol()][i.getHgt()]) {
                if(ic instanceof Guard) {
                    Guard g = (Guard) ic;
                    if(getEnvironment().getCellNature(g.getCol(), g.getHgt()) != Cell.HOL &&
                       (i.getCol() != g.getCol() || i.getHgt() != g.getHgt()))
                        throw new PostconditionError("Engine", "step", "The treasure has not moved with the guard");
                }
            }
        }
        // post: \forall Item t \in getTreasures()@pre
        //         \exists Guard g \in getEnvironment().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //           getEnvironment().getCellNature(g.getCol(), g.getHgt()) == HOL
        //         => t.getCol() == g.getCol() && t.getHgt() == g.getHgt()+1
        for(Item i: treasures_pre) {
            for(InCell ic: cellContent_pre[i.getCol()][i.getHgt()]) {
                if(ic instanceof Guard) {
                    Guard g = (Guard) ic;
                    if(getEnvironment().getCellNature(g.getCol(), g.getHgt()) == Cell.HOL &&
                       (i.getCol() != g.getCol() || i.getHgt() != g.getHgt()+1))
                        throw new PostconditionError("Engine", "step", "The treasure has not fallen above the guard");
                }
            }
        }
        // post: \forall Item t \in getTreasures()@pre
        //         \not \exists Guard g \in getEnvironment().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //         && t \in getTreasures()
        //         => t.getCol() == t.getCol()@pre && t.getHgt() == t.getHgt()@pre
        for(Item i: treasures_pre) {
            boolean found = false;
            for(InCell ic: cellContent_pre[i.getCol()][i.getHgt()]) {
                if(ic instanceof Guard) found = true;
            }
            if(!found) {
                for(Item i2: getTreasures()) {
                    if(i.getId() == i2.getId() &&
                       (i.getCol() != i2.getCol() || i.getHgt() != i2.getHgt()))
                        throw new PostconditionError("Engine", "step", "The treasure has moved while not supposed to");
                }
            }
        }
        // post: \exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
        //       && \not \exists Guard g \in getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
        //       => i \notin getTreasures() && getLevelScore() == getLevelScore()@pre+1
        boolean guard_found = false;
        for(Guard g: getGuards()) {
            if(g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt()) guard_found = true;
        }
        for(Item i: treasures_pre) {
            if(i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt() && !guard_found
               && (getTreasures().contains(i) || getLevelScore() != levelScore_pre + 1))
                throw new PostconditionError("Engine", "step", "The treasure should have been taken by the player");
        }
        // post: \not exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
        //       => getLevelScore() == getLevelScore()@pre
        boolean found = false;
        for(Item i: treasures_pre) {
            if(i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt()) found = true;
        }
        if(!found && getLevelScore() != levelScore_pre)
            throw new PostconditionError("Engine", "step", "The score was updated when it should not have been");
        // post: \forall Item i \in getTreasures()@pre (i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
        //       => i \in getTreasures()
        for(Item i: treasures_pre)
            if((i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
               && !getTreasures().contains(i))
                throw new PostconditionError("Engine", "step", "The treasure should not have been taken by the player");
        // post: getTreasures().isEmpty() => getStatus() == Win
        if(getTreasures().isEmpty() && getStatus() != Status.Win)
            throw new PostconditionError("Engine", "step", "getTreasures().isEmpty() => getStatus() == Win");
        // post: \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
        //       && !getTreasures().isEmpty()
        //       => getStatus() == Loss
        for(Guard g: getGuards()) {
            if(g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt()
               && !getTreasures().isEmpty()
               && getStatus() != Status.Loss)
                throw new PostconditionError("Engine", "step", "The player should have lost");
        }
        // post: \not getTreasures().isEmpty()
        //       && \not \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
        //       && \not \exists Hole h \in getHoles()@pre
        //                 (h.getT() == 15 && h.getCol() == getPlayer().getCol()@pre && h.getHgt() == getPlayer().getHgt()@pre)
        //       => getStatus() == Playing
        boolean playing = true;
        if(getTreasures().isEmpty()) playing = false;
        for(Guard g: getGuards()) {
            if(g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt()) playing = false;
        }
        for(Hole h: holes_pre) {
            if(h.getT() == 15 && h.getCol() == playerCol_pre && h.getHgt() == playerHgt_pre) playing = false;
        }
        if(playing && getStatus() != Status.Playing)
            throw new PostconditionError("Engine", "step", "The game is not supposed to be over");
        // post: \forall Hole h \in getHoles()
        //       => h \notin getHoles()@pre => h.getT() == 0
        for(Hole h: getHoles()) {
            if(!holes_pre.contains(h) && h.getT() != 0)
                throw new PostconditionError("Engine", "step", "The new hole doesn't have the correct t");
        }
        // post: \forall Hole h \in getHoles()@pre h.getT()@pre < 15
        //       => h: getHoles() && h.getT() == h.getT()@pre + 1
        for(Hole h: holes_pre) {
            if(h.getT() < 15) {
                Hole foundH = null;
                for(Hole h1: getHoles()) {
                    if(h1.equals(h)) foundH = h1;
                }
                if(foundH == null || foundH.getT() != h.getT()+1)
                    throw new PostconditionError("Engine", "step", "The t of a hole hasn't increased");
            }
        }
        // post: \forall Hole h \in getHoles()@pre h.getT() == 15
        //       => (h \notin getHoles()
        //           && (getPlayer.getCol()@pre == h.getCol() && getPlayer().getHgt()@pre == h.getHgt() => getStatus() == Loss)
        //           && (\exists Guard g \in getEnvironment().getCellContent(h.getCol(), h.getHgt())@pre
        //               => g.getCol() == g.getInitCol() && g.getHgt() == g.getInitHgt()))
        for(Hole h: holes_pre) {
            if(h.getT() == 15) {
                if(getHoles().contains(h))
                    throw new PostconditionError("Engine", "step", "The hole should have been refilled");
                if(h.getCol() == playerCol_pre && h.getHgt() == playerHgt_pre && getStatus() != Status.Loss)
                    throw new PostconditionError("Engine", "step", "The player should have been crushed by the hole");
                for(InCell ic: cellContent_pre[h.getCol()][h.getHgt()]) {
                    if(ic instanceof Guard) {
                        Guard g = (Guard) ic;
                        for(Guard g2: getGuards()) {
                            if(g2.getId() == g.getId() && (g2.getCol() != g2.getInitCol() || g2.getHgt() != g2.getInitHgt()))
                                throw new PostconditionError("Engine", "step", "The guard should have been crushed by the hole"+g2.getCol());
                        }
                    }
                }
            }
        }

        // post: getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()-1)@pre == TRP
        //       => getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()-1) == EMP
        if(cellNature_pre[getPlayer().getCol()][getPlayer().getHgt()] == Cell.TRP
           && getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()) != Cell.EMP)
            throw new PostconditionError("Engine", "step", "The trap underneath the player should have triggered");
        // post: \forall Guard g: getGuards() getEnvironment().getCellNature(g.getCol(), g.getHgt()-1)@pre == TRP
        //       => getEnvironment().getCellNature(g.getCol(), g.getHgt()-1) == EMP
        for(Guard g: getGuards()) {
            if(cellNature_pre[g.getCol()][g.getHgt()] == Cell.TRP
               && getEnvironment().getCellNature(g.getCol(), g.getHgt()) != Cell.EMP)
                throw new PostconditionError("Engine", "step", "The trap underneath the guard should have triggered");
        }
    }

    @Override
    public Engine clone() {
        return new EngineContract((Engine)getDelegate().clone());
    }
}
