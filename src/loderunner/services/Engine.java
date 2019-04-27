package loderunner.services;

import java.util.Set;

public interface Engine {
    /* Observators */

    // const
    public Environment getEnvironment();

    public Player getPlayer();

    // const
    public Set<Guard> getGuards();

    public Set<Item> getTreasures();
    public Set<Hole> getHoles();
    public Status getStatus();
    public int getLevelScore();
    public Command getNextCommand();
    public Command peekNextCommand();
    public boolean isGuardTurn();

    // const
    public Set<PortalPair> getPortals();

    /* Constructors */

    // pre: screen.isPlayable()
    // pre: \forall Coord c \in { pCoord } union gCoords union tCoords
    //        screen.getCellNature(c.getCol(), c.getHgt()) == EMP
    // pre: \forall PortalPair pp \in portals
    //        screen.getCellNature(pp.getInPCoord().getCol(), pp.getInPCoord().getHgt()) == EMP
    //        && screen.getCellNature(pp.getOutPCoord().getCol(), pp.getOutPCoord().getHgt()) == EMP
    // pre: \forall Coord c1 \in gCoords \forall Coord c2 \in gCoords
    //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
    // pre: \forall Coord c1 \in tCoords \forall Coord c2 \in tCoords
    //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
    // pre: \forall Coord c \in gCoords
    //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
    // pre: \forall Coord c \in tCoords
    //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
    // pre: \forall Coord c \in tCoords
    //        screen.getCellNature(c.getCol(). c.getHgt()-1) \in { PLT, MTL }
    //        || c \in gCoords
    // post: getStatus() == Playing
    // post: getLevelScore() == 0
    // post: !isGuardTurn()
    // post: getEnvironment().getWidth() == screen.getWidth()
    //       && getEnvironment().getHeight() == screen.getHeight()
    //       &&\forall x \in [0..screen.getWidth()[ \forall y \in [0..screen.getHeight()[
    //         getEnvironment().getCellNature(x, y) == screen.getCellNature(x, y)
    // post: getPlayer().getCol() == pCoord.getCol() && getPlayer().getHgt() == pCoord.getHgt()
    // post: \forall Coord c \in gCoords \exists Guard g \in getGuards() (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
    //       && \forall Guard g \in getGuards() \exists Coord c \in gCoords (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
    // post: \forall Coord c \in tCoords \exists Item i \in getTreasures() (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
    //       && \forall Item i \in getTreasures() \exists Coord c \in tCoords (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
    // post: \forall PortalPair pp \in portals pp \in getPortals()
    //       && \forall PortalPair pp \in getPortals() pp \in portals
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords, Set<PortalPair> portals);

    /* Invariants */

    // inv: getPlayer() \in getEnvironment().getCellContent(getPlayer().getCol(), getPlayer().getHgt())
    //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
    //           getPlayer() \in getEnvironment().getCellContent(x, y)
    //           => (getPlayer().getCol() == x && getPlayer().getHgt() == y)
    // inv: \forall Guard g \in getGuards() g \in getEnvironment().getCellContent(g.getCol(), g.getHgt())
    //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
    //           \forall Guard g \in getEnvironment().getCellContent(x, y)
    //           => g \in getGuards() && (g.getCol() == x && g.getHgt() == y)
    // inv: \forall Item i \in getTreasures() i \in getEnvironment().getCellContent(i.getCol(), i.getHgt())
    //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
    //           \forall Item i \in getEnvironment().getCellContent(x, y) i.getNature() == Treasure
    //           => i \in getTreasures() && (i.getCol() == x && i.getHgt() == y)
    // inv: \forall Hole h \in getHoles() getEnvironment().getCellNature(h.getCol(), g.getHgt()) == HOL
    //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
    //           getEnvironment().getCellNature(x, y) == HOL
    //           => \exists Hole h \in getHoles() (h.getCol() == x && h.getHgt() == y)

    /* Operators */

    // pre: getStatus() == Playing

    // Gestion des déplacements
    // post: getPlayer() == (getPlayer()@pre).step()
    // post: \exists pp \in getPortals() (pp.getCoordPIn().getCol() == (getPlayer()@pre).getCol() && pp.getCoordPIn().getHgt() == (getPlayer()@pre).getHgt())
    //       => getPlayer() == (getPlayer()@pre).teleport(pp.getCoordPOut().getCol(), pp.getCoordPOut().getHgt())
    // post: \not \exists pp \in getPortals() (pp.getCoordPIn().getCol() == (getPlayer()@pre).getCol() && pp.getCoordPIn().getHgt() == (getPlayer()@pre).getHgt())
    //       => getPlayer() == (getPlayer()@pre).step()
    // post: isGuardTurn()@pre => \forall Guard g: getGuards() g == (g@pre).step()
    //       && !isGuardTurn()@pre => \forall Guard g: getGuards() g == g@pre
    // post: isGuardTurn() = !isGuardTurn()@pre

    // Les gardes peuvent porter des trésors (et les perdent quand ils tombent dans un trou)
    // post: \forall Item t \in getTreasures()@pre
    //         \exists Guard g \in getEnvironment().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
    //           getEnvironment().getCellNature(g.getCol(), g.getHgt()) != HOL
    //         => t.getCol() == g.getCol() && t.getHgt() == g.getHgt()
    // post: \forall Item t \in getTreasures()@pre
    //         \exists Guard g \in getEnvironment().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
    //           getEnvironment().getCellNature(g.getCol(), g.getHgt()) == HOL
    //         => t.getCol() == g.getCol() && t.getHgt() == g.getHgt()+1
    // post: \forall Item t \in getTreasures()@pre
    //         \not \exists Guard g \in getEnvironment().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
    //         && t \in getTreasures()
    //         => t.getCol() == t.getCol()@pre && t.getHgt() = t.getHgt()@pre

    // Le joueur peut récupérer un trésor, ce qui peut amener à la fin de la partie (seulement si le trésor n'est pas porté par un garde)
    // post: \exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
    //       && \not \exists Guard g \in getGuards()@pre (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
    //       => i \notin getTreasures() && getLevelScore() == getLevelScore()@pre+1
    // post: \not exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
    //       => getLevelScore() == getLevelScore()@pre
    // post: \forall Item i \in getTreasures()@pre (i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
    //       => i \in getTreasures()
    // post: getTreasures().isEmpty() => getStatus() == Win

    // Le joueur peut être tué par un garde, mais il gagne si il a attrapé le dernier trésor malgré tout (on est génereux)
    // post: \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
    //       && !getTreasures().isEmpty()
    //       => getStatus() == Loss

    // La partie continue
    // post: \not getTreasures().isEmpty()
    //       && \not \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
    //       && \not \exists Hole h \in getHoles()@pre
    //                 (h.getT() == 15 && h.getCol() == getPlayer().getCol()@pre && h.getHgt() == getPlayer().getHgt()@pre)
    //       => getStatus() == Playing

    // Gestion des trous
    // post: \forall Hole h \in getHoles()
    //       => h \notin getHoles()@pre => h.getT() == 0
    // post: \forall Hole h \in getHoles()@pre h.getT()@pre < 15
    //       => h: getHoles() && h.getT() == h.getT()@pre + 1
    // post: \forall Hole h \in getHoles()@pre h.getT() == 15
    //       => (h \notin getHoles()
    //           && (getPlayer.getCol()@pre == h.getCol() && getPlayer().getHgt()@pre == h.getHgt() => getStatus() == Loss)
    //           && (\exists Guard g \in getEnvironment().getCellContent(h.getCol(), h.getHgt())@pre
    //               => g.getCol() == g.getInitCol() && g.getHgt() == g.getInitHgt()))

    // Gestion des pieges
    // post: getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()-1)@pre == TRP
    //       => getEnvironment().getCellNature(getPlayer().getCol(), getPlayer().getHgt()-1) == EMP
    // post: \forall Guard g: getGuards() getEnvironment().getCellNature(g.getCol(), g.getHgt()-1)@pre == TRP
    //       => getEnvironment().getCellNature(g.getCol(), g.getHgt()-1) == EMP
    public void step();

    public Engine clone();
}
