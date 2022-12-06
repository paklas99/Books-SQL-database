package se.kth.jarwalli.booksdb.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static java.lang.Integer.parseInt;

public class InsertDialog{


    private Stage stage;

    private String title;
    private String authors;
    private String genre;
    private String isbn;
    private String datePublished;
    private int rating;

    private Button OkButton;




    public InsertDialog(Controller controller) {
        initDialog(controller);
    }


    private void initDialog(Controller controller) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setTitle("Add new Book");
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        TextField titleTextField = new TextField();
        TextField authorTextField = new TextField();
        TextField genreTextField = new TextField();
        TextField ratingTextField = new TextField();
        TextField dateTextField = new TextField();
        TextField isbnTextField = new TextField();
        OkButton = new Button("OK");

        gridPane.add(new Label("Book title"), 0,0);
        gridPane.add(titleTextField,1,0);
        gridPane.add(new Label("Author"), 0,1 );
        gridPane.add(authorTextField,1,1);
        gridPane.add(new Label("Genre"), 0,2 );
        gridPane.add(genreTextField,1,2);
        gridPane.add(new Label("Rating"), 0,3 );
        gridPane.add(ratingTextField,1,3);
        gridPane.add(new Label("Date Published"), 0,4 );
        gridPane.add(dateTextField,1,4);
        gridPane.add(new Label("ISBN"), 0,5 );
        gridPane.add(isbnTextField,1,5);
        gridPane.add(OkButton, 0,6);
        OkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                title = titleTextField.getCharacters().toString();
                authors = authorTextField.getCharacters().toString();
                genre = genreTextField.getCharacters().toString();
                rating = parseInt(ratingTextField.getCharacters().toString());
                datePublished = dateTextField.getCharacters().toString();
                isbn = isbnTextField.getCharacters().toString();
                controller.handleAddBook(isbn, title, datePublished, genre, rating, authors);

            }
        });

        stage.setScene(scene);

    }

    public void showDialog(){
        stage.showAndWait();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getGenre() {
        return genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public int getRating() {
        return rating;
    }

    public Button getOkButton() {
        return OkButton;
    }
}
