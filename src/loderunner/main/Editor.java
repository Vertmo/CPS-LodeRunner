package loderunner.main;

import javafx.application.Application;
import javafx.stage.Stage;

import loderunner.windows.EditorWindow;

public class Editor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new EditorWindow(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
