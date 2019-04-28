package loderunner.windows;

import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import loderunner.impl.CoordImpl;
import loderunner.services.Coord;
import loderunner.services.Engine;
import loderunner.services.Level;
import loderunner.services.PortalPair;
import loderunner.services.Screen;

public class LevelCanvas extends Canvas {
    public static final int CELL_W = 32;

    private static final Image pltImg = new Image("assets/img/plt.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image ladImg = new Image("assets/img/lad.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image hdrImg = new Image("assets/img/hdr.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image mtlImg = new Image("assets/img/mtl.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image trpImg = new Image("assets/img/trp.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image dorImg = new Image("assets/img/dor.png", (double) CELL_W, (double) CELL_W, true, true);

    private static final Image playerImg = new Image("assets/img/player.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image guardImg = new Image("assets/img/guard.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image treasureImg = new Image("assets/img/treasure.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image keyImg = new Image("assets/img/key.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image treaGuardImg = new Image("assets/img/treasureGuard.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image portalInImg = new Image("assets/img/portalIn.png", (double) CELL_W, (double) CELL_W, true, true);
    private static final Image portalOutImg = new Image("assets/img/portalOut.png", (double) CELL_W, (double) CELL_W, true, true);

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void drawCells(Screen s, boolean editorMode) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, s.getWidth()*CELL_W, s.getHeight()*CELL_W);
        for(int x = 0; x < s.getWidth(); x++) {
            for(int y = 0; y < s.getHeight(); y++) {
                switch(s.getCellNature(x, y)) {
                case EMP: break;
                case HOL: break;
                case PLT:
                    gc.drawImage(pltImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    break;
                case LAD:
                    gc.drawImage(ladImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    break;
                case HDR:
                    gc.drawImage(hdrImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    break;
                case MTL:
                    gc.drawImage(mtlImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    break;
                case TRP:
                    if(editorMode) gc.drawImage(trpImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    else gc.drawImage(pltImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                    break;
                case DOR:
                    gc.drawImage(dorImg, x*CELL_W, (s.getHeight()-1-y)*CELL_W);
                }
            }
        }
    }

    public void drawContents(Screen s, Coord pc, Set<Coord> gCoords, Set<Coord> tCoords, Set<Coord> kCoords, Set<PortalPair> portals) {
        GraphicsContext gc = getGraphicsContext2D();

        // Draw player
        gc.drawImage(playerImg, pc.getCol()*CELL_W, (s.getHeight()-1-pc.getHgt())*CELL_W);

        // Draw guards
        gc.setFill(Color.RED);
        for (Coord g: gCoords) {
            gc.drawImage(guardImg, g.getCol()*CELL_W, (s.getHeight()-1-g.getHgt())*CELL_W);
        }

        // Draw treasures
        for (Coord t: tCoords) {
            if(gCoords.contains(t))
                gc.drawImage(treaGuardImg, t.getCol()*CELL_W, (s.getHeight()-1-t.getHgt())*CELL_W);
            else
                gc.drawImage(treasureImg, t.getCol()*CELL_W, (s.getHeight()-1-t.getHgt())*CELL_W);
        }

        // Draw keys
        for (Coord k: kCoords) {
            gc.drawImage(keyImg, k.getCol()*CELL_W, (s.getHeight()-1-k.getHgt())*CELL_W);
        }

        // Draw portals
        for(PortalPair pp: portals) {
            gc.drawImage(portalInImg, pp.getInPCoord().getCol()*CELL_W, (s.getHeight()-1-pp.getInPCoord().getHgt())*CELL_W);
            if(pp.getOutPCoord() != null)
                gc.drawImage(portalOutImg, pp.getOutPCoord().getCol()*CELL_W, (s.getHeight()-1-pp.getOutPCoord().getHgt())*CELL_W);
        }
    }

    public void drawLevel(Level l) {
        drawCells(l.getScreen(), true);
        drawContents(l.getScreen(), l.getPlayerCoord(), l.getGuardCoords(), l.getTreasureCoords(), l.getKeyCoords(), l.getPortals());
    }

    public void drawEnvironment(Engine eng) {
        // Draw environment
        drawCells(eng.getEnvironment(), false);

        // Draw cell contents
        Coord pCoord = new CoordImpl(eng.getPlayer().getCol(), eng.getPlayer().getHgt());
        Set<Coord> gCoords = eng.getGuards().stream()
            .map(g -> new CoordImpl(g.getCol(), g.getHgt()))
            .collect(Collectors.toSet());
        Set<Coord> tCoords = eng.getTreasures().stream()
            .map(t -> new CoordImpl(t.getCol(), t.getHgt()))
            .collect(Collectors.toSet());
        Set<Coord> kCoords = eng.getKeys().stream()
            .map(k -> new CoordImpl(k.getCol(), k.getHgt()))
            .collect(Collectors.toSet());
        drawContents(eng.getEnvironment(), pCoord, gCoords, tCoords, kCoords, eng.getPortals());
    }

    public void drawGameOver(int score) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font(40));
        gc.fillText("Game Over", getWidth()/2, getHeight()/3);
        gc.setFont(new Font(20));
        gc.fillText("Score: " + score, getWidth()/2, 2*getHeight()/3);
    }
}
