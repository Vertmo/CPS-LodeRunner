package loderunner.impl;

import loderunner.services.Coord;
import loderunner.services.PortalPair;

public class PortalPairImpl implements PortalPair {
    private Coord inCoord;
    private Coord outCoord;

    public PortalPairImpl(Coord inCoord) {
        this.inCoord = inCoord;
    }

    @Override
    public Coord getInPCoord() {
        return inCoord;
    }

    @Override
    public Coord getOutPCoord() {
        return outCoord;
    }

    @Override
    public void setOutPCoord(Coord c) {
        outCoord = c;
    }
}
