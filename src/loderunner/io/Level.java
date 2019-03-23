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
import loderunner.services.Cell;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;

public class Level {
    private EditableScreen screen;
    private Coord playerCoord;
    private Set<Coord> guardCoords;
    private Set<Coord> treasureCoords;

    public Level(File file) throws LevelLoadException {
        screen = new EditableScreenImpl();
        guardCoords = new HashSet<>();
        treasureCoords = new HashSet<>();
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
                    }
                }
                br.read();
            }

            while((line = br.readLine()) != null) {
                String[] coordS = line.substring(1).split("x");
                if(line.charAt(0) == 'P') {
                    playerCoord = new CoordImpl(Integer.parseInt(coordS[0]),
                                                Integer.parseInt(coordS[1]));
                } else if(line.charAt(0) == 'G') {
                    guardCoords.add(new CoordImpl(Integer.parseInt(coordS[0]),
                                                  Integer.parseInt(coordS[1])));
                } else if(line.charAt(0) == 'T') {
                    treasureCoords.add(new CoordImpl(Integer.parseInt(coordS[0]),
                                                     Integer.parseInt(coordS[1])));
                }
            }

            br.close();
        } catch(Exception e) {
            throw new LevelLoadException(file.getName());
        }
    }

    public Level(int w, int h) {
        screen = new EditableScreenImpl();
        screen.init(w, h);

        playerCoord = new CoordImpl(0, 0);
        guardCoords = new HashSet<>();
        treasureCoords = new HashSet<>();
    }

    public EditableScreen getScreen() { return screen; }
    public Coord getPlayerCoord() { return playerCoord; }
    public Set<Coord> getGuardCoords() { return guardCoords; }
    public Set<Coord> getTreasureCoords() { return treasureCoords; }

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
                    }
                }
                bw.write("\n");
            }

            // Player coordinates
            bw.write("P" + playerCoord.getCol() + "x" + playerCoord.getHgt() + "\n");

            // Guard coordinates
            for(Coord g: guardCoords) {
                bw.write("G" + g.getCol() + "x" + g.getHgt() + "\n");
            }

            // Treasure coordinates
            for(Coord t: treasureCoords) {
                bw.write("T" + t.getCol() + "x" + t.getHgt() + "\n");
            }

            bw.close();
        } catch(IOException e) {}
    }
}
