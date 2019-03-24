package loderunner.windows;

import java.util.Set;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import loderunner.io.Level;
import loderunner.services.Coord;
import loderunner.services.Screen;

class LevelCanvas extends Canvas {
    public final static int CELL_W = 20;

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

    public void drawCells(Screen s) {
        // TODO du pixel art
        GraphicsContext gc = getGraphicsContext2D();
        for(int x = 0; x < s.getWidth(); x++) {
            for(int y = 0; y < s.getHeight(); y++) {
                switch(s.getCellNature(x, y)) {
                case EMP: gc.setFill(Color.BLACK); break;
                case PLT: gc.setFill(Color.BROWN); break;
                case HOL: gc.setFill(Color.BLACK); break;
                case LAD: gc.setFill(Color.YELLOW); break;
                case HDR: gc.setFill(Color.GREEN); break;
                case MTL: gc.setFill(Color.GRAY); break;
                }
                gc.fillRect(x*CELL_W, (s.getHeight()-1-y)*CELL_W, CELL_W, CELL_W);
            }
        }
    }

    public void drawContents(Screen s, Coord pc, Set<Coord> gCoords, Set<Coord> tCoords) {
        // TODO du pixel art
        GraphicsContext gc = getGraphicsContext2D();
        // Draw player
        gc.setFill(Color.GREEN);
        gc.fillOval(pc.getCol()*CELL_W,
                    (s.getHeight()-1-pc.getHgt())*CELL_W,
                    CELL_W, CELL_W);

        // Draw guards
        gc.setFill(Color.RED);
        for (Coord g: gCoords) {
            gc.fillOval(g.getCol()*CELL_W, (s.getHeight()-1-g.getHgt())*CELL_W,
                        CELL_W, CELL_W);
        }

        // Draw treasures
        for (Coord t: tCoords) {
            if(gCoords.contains(t)) gc.setFill(Color.ORANGE);
            else gc.setFill(Color.GOLD);
            gc.fillOval(t.getCol()*CELL_W, (s.getHeight()-1-t.getHgt())*CELL_W,
                        CELL_W, CELL_W);
        }
    }

    public void drawLevel(Level l) {
        drawCells(l.getScreen());
        drawContents(l.getScreen(), l.getPlayerCoord(), l.getGuardCoords(), l.getTreasureCoords());
    }
}
