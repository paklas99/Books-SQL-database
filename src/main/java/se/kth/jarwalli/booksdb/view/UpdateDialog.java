package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.kth.jarwalli.booksdb.model.Book;

public class UpdateDialog {
    private Stage stage;
    private int rating;
    private String isbn;
    private Button okButton;
    private ComboBox ratingComboBox;

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

        okButton = new Button("OK");
        ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9));
        ratingComboBox.setPromptText("Update rating");
        TextField storyTextField = new TextField();


        //Add nodes to gridPane
        gridPane.add(new Label("Update the Rating of a book"), 0 ,1);
        gridPane.add(ratingComboBox,0,2);
        gridPane.add(new Label("Update the storyline of a book"), 0 ,3);
        gridPane.add(storyTextField,0,4);
        gridPane.add(okButton, 0 , 5);

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                rating = (int) ratingComboBox.getValue();
                controller.handleUpdateBook(rating, isbn);
                System.out.println(rating);
                stage.close();
                ratingComboBox.getSelectionModel().clearSelection();
                storyTextField.clear();
            }
        });

        stage.setScene(scene);

    }

    public void showUpdateDialog(Book tempBook){
        isbn = tempBook.getIsbn();
        stage.showAndWait();
    }
}
