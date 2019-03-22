package loderunner.services;

import java.util.Set;

public interface Engine {
    /* Observators */

    // const
    public Environment getEnvironment();

    // const
    public Player getPlayer();

    // const
    public Set<Guard> getGuards();

    public Set<Item> getTreasures();
    public Set<Hole> getHoles();
    public Status getStatus();
    public Command getNextCommand();

    /* Constructors */

    // pre: screen.isPlayable()
    // pre: \forall Coord c \in { pCoord } union gCoords union tCoords
    //        getEnvironment().getCellNature(c.getCol(), c.getHgt()) == EMP
    // pre: \forall Coord c1 \in { pCoord } union gCoords union tCoords
    //        \forall Coord c2 \in { pCoord } union gCoords union tCoords
    //          (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
    // post: getStatus() == Playing
    // post: getEnvironment().getWidth() == screen.getWidth()
    //       && getEnvironment().getHeight() == screen.getHeight()
    //       &&\forall x \in [0..screen.getWidth()[ \forall y \in [0..screen.getHeight()[
    //         getEnvironment().getCellNature(x, y) == screen.getCellNature(x, y)
    // post: getPlayer().getCol() == pCoord.getCol() && getPlayer().getHgt() == pCoord.getHgt()
    // post: \forall Coord c \in gCoords \exists Guard g \in getGuards() (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
    //       && \forall Guard g \in getGuards() \exists Coord c \in gCoords (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
    // post: \forall Coord c \in tCoords \exists Item i \in getTreasures() (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
    //       && \forall Item i \in getTreasures() \exists Coord c \in tCoords (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
    void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords);

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
    // inv: \forall Hole h \in getHoles() h getEnvironment().getCellNature(h.getCol(), g.getHgt()) == HOL
    //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
    //           getEnvironment().getCellNature(x, y) == HOL
    //           => \exists Hole h \in getHoles() (h.getCol() == x && h.getHgt() == y)

    /* Operators */

    // pre: getStatus() == Playing
    // post: \exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
    //       => i \notin getTreasures()
    // post: \forall Item i \in getTreasures()@pre (i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
    //       => i \in getTreasures()
    // post: getTreasures().isEmpty() => getStatus() == Win
    // post: \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
    //       => getStatus() == Loss
    // post: \forall Hole h \in getHoles()
    //       => h \notin getHoles()@pre => h.getT() == 0
    // post: \forall Hole h \in getHoles()@pre h1.getT()@pre < 15
    //       => h: getHoles() && h.getT() == h.getT()@pre + 1
    // post: \forall Hole h \in getHoles()@pre h.getT() == 15
    //       => (h \notin getHoles()
    //           && (getPlayer.getCol()@pre == h.getCol() && getPlayer()@pre.getHgt() == h.getHgt() => getStatus() == Loss)
    //           && (\exists Guard g \in getEnvironment().getCellContent(h.getCol(), h.getHgt())@pre
    //               => g.getCol() == g.getInitCol() && g.getHgt() == g.getInitHgt()))
    // post: \not getTreasures().isEmpty()
    //       && \not \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
    //       && \not \exists Hole h \in getHoles()@pre
    //                 (h.getT() == 15 && h.getCol() == getPlayer().getCol()@pre && h.getHgt() == getPlayer().getHgt()@pre)
    //       => getStatus() == Playing
    void step();
}
