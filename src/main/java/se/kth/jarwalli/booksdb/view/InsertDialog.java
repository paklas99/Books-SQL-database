package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import se.kth.jarwalli.booksdb.model.Author;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
    private Button addAuthorButton;
    private ArrayList<Author> allAuthors;
    private ComboBox authorsComboBox;




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

        // Create nodes for the gridPane dialog:
        TextField titleTextField = new TextField();
        TextField authorTextField = new TextField();
        TextField genreTextField = new TextField();
        TextField ratingTextField = new TextField();
        TextField dateTextField = new TextField();
        TextField isbnTextField = new TextField();
        OkButton = new Button("OK");
        addAuthorButton = new Button("Add Author");
        authorsComboBox = new ComboBox<>();
        ComboBox ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ratingComboBox.setPromptText("Choose Rating");
        DatePicker datePicker = new DatePicker();



        //Add nodes to gridPane
        gridPane.add(new Label("Book title"), 0,0);
        gridPane.add(titleTextField,1,0);
        gridPane.add(new Label("Author"), 0,1 );
        gridPane.add(authorTextField,1,1);
        gridPane.add(new Label("Genre"), 0,2 );
        gridPane.add(genreTextField,1,2);
        gridPane.add(new Label("Rating"), 0,3 );
        gridPane.add(ratingComboBox, 1,3);
        gridPane.add(new Label("Date Published"), 0,4 );
        gridPane.add(dateTextField,1,4);
        gridPane.add(new Label("ISBN"), 0,5 );
        gridPane.add(isbnTextField,1,5);
        gridPane.add(OkButton, 0,6);
        //gridPane.add(datePicker, 0,9);


        // Functionality:

        // The following block catches an error that can happen if the datepicker gets a wrong date.
        StringConverter<LocalDate> defaultConverter = datePicker.getConverter();
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate value) {
                return defaultConverter.toString(value);
            }

            @Override public LocalDate fromString(String text) {
                try {
                    return defaultConverter.fromString(text);
                } catch (DateTimeParseException ex) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a date in the required format");
                    alert.showAndWait();
                }
            return null;
            }
        });

        //ratingComboBox.setEditable(true);

/*
        addAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TextField t = new TextField();
                extraAuthors.add(t);
                gridPane.add(t,0,7+extraAuthors.size());
                scene.getWindow().sizeToScene();
                System.out.println(ratingComboBox.getValue());
            }
        });*/

        OkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                title = titleTextField.getCharacters().toString();
                authors = authorTextField.getCharacters().toString();
                genre = genreTextField.getCharacters().toString();
                // TODO check for NULLPointer Exception, when no rating is selected.
                rating = (int) ratingComboBox.getValue();
                datePublished = dateTextField.getCharacters().toString();
                isbn = isbnTextField.getCharacters().toString();
                controller.handleAddBook(isbn, title, datePublished, genre, rating, authors);
                stage.close();
                titleTextField.clear();
                authorTextField.clear();
                genreTextField.clear();
                // Clear combo box??? ratingComboBox;
                dateTextField.clear();
                isbnTextField.clear();

            }
        });

        stage.setScene(scene);

    }

    public void showDialog(ArrayList<String> allAuthorsString){
        authorsComboBox.getItems().addAll(FXCollections.observableArrayList(allAuthorsString));
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
