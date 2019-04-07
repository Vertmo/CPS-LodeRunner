package loderunner.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import loderunner.services.CommandProvider;
import loderunner.services.Engine;

public class GameWindow {
    private Stage stage;
    private LevelCanvas canvas;
    private Label hpLabel, scoreLabel;
    private CommandProvider cp;

    public GameWindow(Stage stage) {
        // Set the stage
        this.stage = stage;
        stage.setTitle("Loderunner");
        VBox vbox = new VBox();

        // Empty canvas
        canvas = new LevelCanvas();
        vbox.getChildren().add(canvas);
        vbox.getChildren().add(new Separator());

        hpLabel = new Label("HP: ");
        hpLabel.setAlignment(Pos.CENTER_LEFT);

        scoreLabel = new Label("Score: ");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);

        Region centerReg = new Region();
        HBox.setHgrow(centerReg, Priority.ALWAYS);

        HBox hbox = new HBox(hpLabel, centerReg, scoreLabel);
        vbox.getChildren().add(hbox);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);

        // Display the stage
        stage.show();

        // Set command provider
        cp = new KeyboardCommandProvider(scene);
    }

    public void redrawCanvas(Engine eng) {
        canvas.resize(eng.getEnvironment().getWidth()*LevelCanvas.CELL_W,
                      eng.getEnvironment().getHeight()*LevelCanvas.CELL_W);
        stage.sizeToScene();

        canvas.drawEnvironment(eng);
    }

    public void redrawHp(int hp) {
        hpLabel.setText("HP: " + hp);
    }

    public void redrawScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void drawGameOver(int score) {
        scoreLabel.setText("Score: " + score);
        canvas.drawGameOver(score);
    }

    public CommandProvider getCommandProvider() {
        return cp;
    }
}
