package loderunner.services;

import java.util.Set;

public interface Level {
    public EditableScreen getScreen();
    public Coord getPlayerCoord();
    public Set<Coord> getGuardCoords();
    public Set<Coord> getTreasureCoords();
    public Set<Coord> getKeyCoords();
    public Set<PortalPair> getPortals();
}
