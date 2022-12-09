package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateDialog {
    private Stage stage;
    private String storyLine;
    private int rating;
    private Button OkButton;

    public UpdateDialog(Controller controller){
        initDialog(controller);
    }

    private void initDialog(Controller controller){
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setTitle("Update Book");
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        ComboBox ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9));
        ratingComboBox.setPromptText("Update rating");

        //Add nodes to gridPane
        gridPane.add(new Label("Rating"), 0 ,1);
        gridPane.add(OkButton, 0 , 1);

    }


    public void showUpdateDialog(){
        stage.showAndWait();
    }
}
