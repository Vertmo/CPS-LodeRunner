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
    private Label hpLabel, bulletsLabel, scoreLabel;
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

        bulletsLabel = new Label("Bullets: ");
        bulletsLabel.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Score: ");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);

        Region reg1 = new Region();
        HBox.setHgrow(reg1, Priority.ALWAYS);

        Region reg2 = new Region();
        HBox.setHgrow(reg2, Priority.ALWAYS);

        HBox hbox = new HBox(hpLabel, reg1, bulletsLabel, reg2, scoreLabel);
        vbox.getChildren().add(hbox);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);

        // Display the stage
        stage.show();

        // Set command provider
        cp = new KeyboardCommandProviderV2(scene);
    }

    public void redrawCanvas(Engine eng) {
        canvas.resize(eng.getEnvironment().getWidth()*LevelCanvas.CELL_W,
                      eng.getEnvironment().getHeight()*LevelCanvas.CELL_W);
        stage.sizeToScene();

        canvas.drawEnvironment(eng);
        canvas.drawKeys(eng.getPlayer().getNbKeys());
    }

    public void redrawHp(int hp) {
        hpLabel.setText("HP: " + hp);
    }

    public void redrawBullets(int bullets) {
        bulletsLabel.setText("Bullets: " + bullets);
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
