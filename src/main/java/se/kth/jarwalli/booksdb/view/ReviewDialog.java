package se.kth.jarwalli.booksdb.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.kth.jarwalli.booksdb.model.Book;

public class ReviewDialog {

    private Stage stage;
    private Button okButton;
    private String reviewText;

    private String isbn;




    public ReviewDialog(Controller controller) {
        initLoginDialog(controller);
    }

    private void initLoginDialog(Controller controller){
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setTitle("Update Book");
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        okButton = new Button("OK");
        TextField reviewTextField = new TextField();

        //add nodes to gridPane
        gridPane.add(new Label("Enter a review"), 0, 0);
        gridPane.add(reviewTextField, 0,1);
        gridPane.add((okButton),0, 2);

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reviewText = reviewTextField.getCharacters().toString();
                controller.handleReview(isbn, reviewText);
                reviewTextField.clear();
                stage.close();

            }
        });
        stage.setScene(scene);
    }
    public void showReviewDialog(Book tempbook){
        isbn = tempbook.getIsbn();
        stage.showAndWait();
    }
}
