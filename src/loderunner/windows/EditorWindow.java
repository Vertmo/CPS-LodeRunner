package loderunner.windows;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import loderunner.impl.CoordImpl;
import loderunner.impl.PortalPairImpl;
import loderunner.io.LevelIO;
import loderunner.io.LevelLoadException;
import loderunner.services.Cell;
import loderunner.services.Coord;
import loderunner.services.PortalPair;

enum Tool {
    EMP, PLT, LAD, HDR, MTL, TRP, DOR,
    Player, Guard, Treasure, Key, PortalIn, PortalOut
}

public class EditorWindow {
    private Stage stage;
    private Label playableLbl;
    private LevelCanvas canvas;

    private LevelIO level;
    private Tool currentTool;
    private PortalPair currentPortalPair;

    public EditorWindow(Stage stage) {
        // Create default (empty) screen
        level = new LevelIO(20, 10);

        currentTool = Tool.EMP;

        // Set the stage
        this.stage = stage;
        stage.setTitle("Loderunner Maker - <unnamed>");
        VBox vbox = new VBox();

        // At the beginning, the stage is not playable
        playableLbl = new Label("");
        updateIsPlayableLbl();

        // Empty canvas
        canvas = new LevelCanvas();
        canvas.setOnMouseClicked(e -> {
                MouseEvent me = (MouseEvent) e;
                int x = (int) me.getX()/LevelCanvas.CELL_W;
                int y = (int) (level.getScreen().getHeight()-me.getY()/LevelCanvas.CELL_W);
                switch(currentTool) {
                case EMP:
                    level.getScreen().setNature(x, y, Cell.EMP);
                    break;
                case PLT:
                    level.getScreen().setNature(x, y, Cell.PLT);
                    break;
                case MTL:
                    level.getScreen().setNature(x, y, Cell.MTL);
                    break;
                case LAD:
                    level.getScreen().setNature(x, y, Cell.LAD);
                    break;
                case HDR:
                    level.getScreen().setNature(x, y, Cell.HDR);
                    break;
                case TRP:
                    level.getScreen().setNature(x, y, Cell.TRP);
                    break;
                case DOR:
                    level.getScreen().setNature(x, y, Cell.DOR);
                    break;
                case Player:
                    level.getPlayerCoord().setCol(x); level.getPlayerCoord().setHgt(y);
                    break;
                case Guard:
                    Coord gc = new CoordImpl(x, y);
                    if(level.getGuardCoords().contains(gc)) {
                        level.getGuardCoords().remove(gc);
                    } else {
                        level.getGuardCoords().add(gc);
                    }
                    break;
                case Treasure:
                    Coord tc = new CoordImpl(x, y);
                    if(level.getTreasureCoords().contains(tc)) {
                        level.getTreasureCoords().remove(tc);
                    } else {
                        level.getTreasureCoords().add(tc);
                    }
                    break;
                case Key:
                    Coord kc = new CoordImpl(x, y);
                    if(level.getKeyCoords().contains(kc)) {
                        level.getKeyCoords().remove(kc);
                    } else {
                        level.getKeyCoords().add(kc);
                    }
                    break;
                case PortalIn:
                    PortalPair portalOnCellI = null;
                    for(PortalPair pp: level.getPortals()) {
                        if((pp.getInPCoord().getCol() == x && pp.getInPCoord().getHgt() == y) ||
                           (pp.getOutPCoord() != null && pp.getOutPCoord().getCol() == x && pp.getOutPCoord().getHgt() == y))
                            portalOnCellI = pp;
                    }
                    if(portalOnCellI != null) {
                        level.getPortals().remove(portalOnCellI);
                    } else {
                        currentPortalPair = new PortalPairImpl(new CoordImpl(x, y));
                        level.getPortals().add(currentPortalPair);
                        currentTool = Tool.PortalOut;
                    }
                    break;
                case PortalOut:
                    PortalPair portalOnCellO = null;
                    for(PortalPair pp: level.getPortals()) {
                        if((pp.getInPCoord().getCol() == x && pp.getInPCoord().getHgt() == y) ||
                           (pp.getOutPCoord() != null && pp.getOutPCoord().getCol() == x && pp.getOutPCoord().getHgt() == y))
                            portalOnCellO = pp;
                    }
                    if(portalOnCellO != null) {
                        level.getPortals().remove(portalOnCellO);
                    } else {
                        currentPortalPair.setOutPCoord(new CoordImpl(x, y));
                        currentPortalPair = null;
                    }
                    currentTool = Tool.PortalIn;
                    break;
                }
                redrawCanvas();
                updateIsPlayableLbl();
            });

        vbox.getChildren().add(makeMenuBar(stage));
        vbox.getChildren().add(makeToolBar(stage));
        vbox.getChildren().add(canvas);
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(playableLbl);

        stage.setScene(new Scene(vbox));

        // Display the stage
        stage.show();

        redrawCanvas();
    }

    private ToolBar makeMenuBar(Stage stage) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
                File file = new FileChooser().showSaveDialog(stage);
                if(file == null) return;
                level.save(file);
                stage.setTitle("Loderunner Maker - " + file.getName());
            });
        Button openButton = new Button("Open");
        openButton.setOnAction(e -> {
                File file = new FileChooser().showOpenDialog(stage);
                if(file == null) return;
                try {
                    level = new LevelIO(file);
                } catch(LevelLoadException le) {}
                stage.setTitle("Loderunner Maker - " + file.getName());
                redrawCanvas();
                updateIsPlayableLbl();
            });
        Button newButton = new Button("New");
        newButton.setOnAction(e -> {
                NewDialog nd = new NewDialog();
                nd.showAndWait();
                if(nd.getResult() != null) {
                    level = new LevelIO(nd.getResult().getCol(), nd.getResult().getHgt());
                    stage.setTitle("Loderunner Maker - <unnamed>");
                    redrawCanvas();
                    updateIsPlayableLbl();
                }
            });

        return new ToolBar(saveButton, openButton, newButton);
    }

    private ToolBar makeToolBar(Stage stage) {
        Button empButton = new Button("EMP");
        empButton.setOnAction(e -> { currentTool = Tool.EMP;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button pltButton = new Button("PLT");
        pltButton.setOnAction(e -> { currentTool = Tool.PLT;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button ladButton = new Button("LAD");
        ladButton.setOnAction(e -> { currentTool = Tool.LAD;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button hdrButton = new Button("HDR");
        hdrButton.setOnAction(e -> { currentTool = Tool.HDR;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button mtlButton = new Button("MTL");
        mtlButton.setOnAction(e -> { currentTool = Tool.MTL;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button trpButton = new Button("TRP");
        trpButton.setOnAction(e -> { currentTool = Tool.TRP;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button dorButton = new Button("DOR");
        dorButton.setOnAction(e -> { currentTool = Tool.DOR;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });

        Button playerButton = new Button("Player");
        playerButton.setOnAction(e -> { currentTool = Tool.Player;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button guardButton = new Button("Guard");
        guardButton.setOnAction(e -> { currentTool = Tool.Guard;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button treasureButton = new Button("Treasure");
        treasureButton.setOnAction(e -> { currentTool = Tool.Treasure;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });
        Button keyButton = new Button("Key");
        keyButton.setOnAction(e -> { currentTool = Tool.Key;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });

        Button portalButton = new Button("Portal");
        portalButton.setOnAction(e -> { currentTool = Tool.PortalIn;
                if(currentPortalPair != null) level.getPortals().remove(currentPortalPair);
                redrawCanvas(); });

        return new ToolBar(empButton, pltButton, ladButton, hdrButton, mtlButton, trpButton, dorButton,
                           playerButton, guardButton, treasureButton, keyButton, portalButton);
    }

    private void updateIsPlayableLbl() {
        boolean correctContent = true;
        correctContent = correctContent &&
            level.getScreen().getCellNature(level.getPlayerCoord().getCol(), level.getPlayerCoord().getHgt()) == Cell.EMP;
        for(Coord g: level.getGuardCoords()) {
            if(level.getScreen().getCellNature(g.getCol(), g.getHgt()) != Cell.EMP) correctContent = false;
            if(level.getPlayerCoord().getCol() == g.getCol() && level.getPlayerCoord().getHgt() == g.getHgt()) correctContent = false;
        }
        for(Coord t: level.getTreasureCoords()) {
            if(level.getScreen().getCellNature(t.getCol(), t.getHgt()) != Cell.EMP) correctContent = false;
            if(level.getPlayerCoord().getCol() == t.getCol() && level.getPlayerCoord().getHgt() == t.getHgt()) correctContent = false;
            if(level.getScreen().getCellNature(t.getCol(), t.getHgt()-1) != Cell.PLT &&
               level.getScreen().getCellNature(t.getCol(), t.getHgt()-1) != Cell.MTL &&
               level.getScreen().getCellNature(t.getCol(), t.getHgt()-1) != Cell.LAD &&
               level.getScreen().getCellNature(t.getCol(), t.getHgt()-1) != Cell.TRP &&
               !level.getGuardCoords().contains(t)) correctContent = false;
        }
        for(Coord k: level.getKeyCoords()) {
            if(level.getScreen().getCellNature(k.getCol(), k.getHgt()) != Cell.EMP) correctContent = false;
            if(level.getPlayerCoord().getCol() == k.getCol() && level.getPlayerCoord().getHgt() == k.getHgt()) correctContent = false;
            if(level.getScreen().getCellNature(k.getCol(), k.getHgt()-1) != Cell.PLT &&
               level.getScreen().getCellNature(k.getCol(), k.getHgt()-1) != Cell.MTL &&
               level.getScreen().getCellNature(k.getCol(), k.getHgt()-1) != Cell.LAD &&
               level.getScreen().getCellNature(k.getCol(), k.getHgt()-1) != Cell.TRP &&
               !level.getGuardCoords().contains(k)) correctContent = false;
        }

        for(PortalPair pp: level.getPortals()) {
            if(level.getScreen().getCellNature(pp.getInPCoord().getCol(), pp.getInPCoord().getHgt()) != Cell.EMP
               || pp.getOutPCoord() == null || level.getScreen().getCellNature(pp.getOutPCoord().getCol(), pp.getOutPCoord().getHgt()) != Cell.EMP)
                correctContent = false;
        }
        if(correctContent && level.getScreen().isPlayable()) {
            playableLbl.setText("Playable");
            playableLbl.setTextFill(Color.color(0, 1, 0, 1));
        } else {
            playableLbl.setText("Not playable");
            playableLbl.setTextFill(Color.color(1, 0, 0, 1));
        }
    }

    private void redrawCanvas() {
        canvas.resize(level.getScreen().getWidth()*LevelCanvas.CELL_W,
                      level.getScreen().getHeight()*LevelCanvas.CELL_W);
        this.stage.sizeToScene();

        canvas.drawLevel(level);
    }
}
