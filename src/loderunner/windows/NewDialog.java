package loderunner.windows;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import loderunner.impl.CoordImpl;
import loderunner.services.Coord;

public class NewDialog extends Dialog<Coord> {
    private Coord coord;
    public NewDialog() {
        super();
        setTitle("New Level");
        coord = new CoordImpl(20, 10);

        // Set everything in the dialog box
        setContentText("Choose the size of your new level");
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        TextField width = new TextField();
        width.setPromptText("Width"); width.setText("20");
        width.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        width.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    try {
                        coord.setCol(Integer.parseInt(width.getText()));
                    } catch(NumberFormatException e) { coord.setCol(20); }
                }
            });
        TextField height = new TextField();
        height.setPromptText("Height"); height.setText("10");
        height.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        height.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    try {
                        coord.setHgt(Integer.parseInt(height.getText()));
                    } catch(NumberFormatException e) { coord.setHgt(10); }
                }
            });

        gridPane.add(new Label("Width:"), 0, 0);
        gridPane.add(new Label("Height:"), 0, 1);
        gridPane.add(width, 1, 0);
        gridPane.add(height, 1, 1);

        getDialogPane().setContent(gridPane);

        // Set result converter
        setResultConverter(b -> {
                if(b == ButtonType.OK) return coord;
                else return null;
            });
    }
}
