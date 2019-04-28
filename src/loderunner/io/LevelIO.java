package loderunner.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import loderunner.impl.CoordImpl;
import loderunner.impl.EditableScreenImpl;
import loderunner.impl.PortalPairImpl;
import loderunner.services.Cell;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Level;
import loderunner.services.PortalPair;

public class LevelIO implements Level {
    private EditableScreen screen;
    private Coord playerCoord;
    private Set<Coord> guardCoords;
    private Set<Coord> treasureCoords;
    private Set<Coord> keyCoords;
    private Set<PortalPair> portals;

    public LevelIO(File file) throws LevelLoadException {
        screen = new EditableScreenImpl();
        guardCoords = new HashSet<>();
        treasureCoords = new HashSet<>();
        keyCoords = new HashSet<>();
        portals = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            String[] sizeS = line.split("x");

            screen.init(Integer.parseInt(sizeS[0]),
                        Integer.parseInt(sizeS[1]));

            for(int y = screen.getHeight()-1; y >= 0; y--) {
                for(int x = 0; x < screen.getWidth(); x++) {
                    switch(br.read()) {
                    case 'E': screen.setNature(x, y, Cell.EMP); break;
                    case 'P': screen.setNature(x, y, Cell.PLT); break;
                    case 'H': screen.setNature(x, y, Cell.HOL); break;
                    case 'L': screen.setNature(x, y, Cell.LAD); break;
                    case 'R': screen.setNature(x, y, Cell.HDR); break;
                    case 'M': screen.setNature(x, y, Cell.MTL); break;
                    case 'T': screen.setNature(x, y, Cell.TRP); break;
                    case 'D': screen.setNature(x, y, Cell.DOR); break;
                    }
                }
                br.read();
            }

            while((line = br.readLine()) != null) {
                if(line.charAt(0) == 'P') {
                    String[] coordS = line.substring(1).split("x");
                    playerCoord = new CoordImpl(Integer.parseInt(coordS[0]),
                                                Integer.parseInt(coordS[1]));
                } else if(line.charAt(0) == 'G') {
                    String[] coordS = line.substring(1).split("x");
                    guardCoords.add(new CoordImpl(Integer.parseInt(coordS[0]),
                                                  Integer.parseInt(coordS[1])));
                } else if(line.charAt(0) == 'T') {
                    String[] coordS = line.substring(1).split("x");
                    treasureCoords.add(new CoordImpl(Integer.parseInt(coordS[0]),
                                                     Integer.parseInt(coordS[1])));
                } else if(line.charAt(0) == 'K') {
                    String[] coordS = line.substring(1).split("x");
                    keyCoords.add(new CoordImpl(Integer.parseInt(coordS[0]),
                                                     Integer.parseInt(coordS[1])));
                } else if(line.charAt(0) == 'W') {
                    String[] coordS = line.substring(1).split("/");
                    String[] coordInS = coordS[0].split("x");
                    CoordImpl coordIn = new CoordImpl(Integer.parseInt(coordInS[0]),
                                                      Integer.parseInt(coordInS[1]));
                    String[] coordOutS = coordS[1].split("x");
                    CoordImpl coordOut = new CoordImpl(Integer.parseInt(coordOutS[0]),
                                                       Integer.parseInt(coordOutS[1]));
                    PortalPair pp = new PortalPairImpl(coordIn); pp.setOutPCoord(coordOut);
                    portals.add(pp);
                }
            }

            br.close();
        } catch(Exception e) {
            throw new LevelLoadException(file.getName(), e.getMessage());
        }
    }

    public LevelIO(int w, int h) {
        screen = new EditableScreenImpl();
        screen.init(w, h);

        for(int x = 0; x < w; x++) {
            screen.setNature(x, 0, Cell.MTL);
        }

        playerCoord = new CoordImpl(0, 1);
        guardCoords = new HashSet<>();
        treasureCoords = new HashSet<>();
        keyCoords = new HashSet<>();
        portals = new HashSet<>();
    }

    public EditableScreen getScreen() { return screen; }
    public Coord getPlayerCoord() { return playerCoord; }
    public Set<Coord> getGuardCoords() { return guardCoords; }
    public Set<Coord> getTreasureCoords() { return treasureCoords; }
    public Set<Coord> getKeyCoords() { return keyCoords; }
    public Set<PortalPair> getPortals() { return portals; }

    public void save(File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            // Write size
            bw.write(screen.getWidth() + "x" + screen.getHeight() + "\n");

            // Write cell nature
            for(int y = screen.getHeight()-1; y >= 0; y--) {
                for(int x = 0; x < screen.getWidth(); x++) {
                    switch(screen.getCellNature(x, y)) {
                    case EMP: bw.write("E"); break;
                    case PLT: bw.write("P"); break;
                    case HOL: bw.write("H"); break;
                    case LAD: bw.write("L"); break;
                    case HDR: bw.write("R"); break;
                    case MTL: bw.write("M"); break;
                    case TRP: bw.write("T"); break;
                    case DOR: bw.write("D"); break;
                    }
                }
                bw.write("\n");
            }

            // Player coordinates
            bw.write("P" + playerCoord.toString() + "\n");

            // Guard coordinates
            for(Coord g: guardCoords) {
                bw.write("G" + g.toString() + "\n");
            }

            // Treasure coordinates
            for(Coord t: treasureCoords) {
                bw.write("T" + t.toString() + "\n");
            }

            // Key coordinates
            for(Coord k: keyCoords) {
                bw.write("K" + k.toString() + "\n");
            }

            // Portal pairs coordinates
            for(PortalPair p: portals) {
                if(p.getOutPCoord() != null)
                    bw.write("W" + p.getInPCoord().toString() + "/" + p.getOutPCoord().toString() + "\n");
            }

            bw.close();
        } catch(IOException e) {}
    }
}
