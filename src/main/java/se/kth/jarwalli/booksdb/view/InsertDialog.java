package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import se.kth.jarwalli.booksdb.model.Author;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class InsertDialog{
    private Stage stage;
    Stage authorStage;
    private Scene authorScene;
    private String title;
    private String genre;
    private String isbn;
    private String datePublished;
    private Integer rating;
    private Button okInsertBookButton;
    private Button plusAuthorButton;
    private Button addAuthorButton;
    private Button cancelAuthorButton;
    private ArrayList<ComboBox> extraAuthorComboBoxes;
    private Button okConfirmAuthorsButton;
    private ArrayList<Author> allAuthors;
    private ArrayList<Text> chosenAuthorsTextForDisplay;
    private ArrayList<Integer> authorIdsToReturn;
    private ArrayList<String> authorsToCreateList;

    private ComboBox existingAuthorsComboBox;
    private GridPane authorGridPane;
    private VBox authorVBox;
    private VBox authorListVBox;
    private TextFlow textFlowAuthors;

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
        chosenAuthorsTextForDisplay = new ArrayList<>();
        authorsToCreateList = new ArrayList<>();
        allAuthors = new ArrayList<>();
        authorIdsToReturn = new ArrayList<>();
        extraAuthorComboBoxes = new ArrayList<>();

        // Create nodes for the gridPane dialog:
        TextField titleTextField = new TextField();
        TextField genreTextField = new TextField();
        TextField dateTextField = new TextField();
        TextField isbnTextField = new TextField();

        ComboBox ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ratingComboBox.setPromptText("Choose Rating");
        DatePicker datePicker = new DatePicker();
        ComboBox genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Choose Genre");
        genreComboBox.getItems().addAll(FXCollections.observableArrayList("Drama", "Comedy", "Educational", "Fantasy", "Horror", "Novel", "Scary","Other"));
        okInsertBookButton = new Button("OK");
        addAuthorButton = new Button("Add Author");
        existingAuthorsComboBox = new ComboBox<>();
        extraAuthorComboBoxes.add(existingAuthorsComboBox);
        existingAuthorsComboBox.setEditable(true);
        plusAuthorButton = new Button("+");
        cancelAuthorButton = new Button("Cancel");
        okConfirmAuthorsButton = new Button("Add Authors");






        //Add nodes to gridPane
        gridPane.add(new Label("Book title"), 0,0);
        gridPane.add(titleTextField,1,0);
        gridPane.add(addAuthorButton,1,1);
        gridPane.add(new Label("Genre"), 0,2 );
        gridPane.add(genreComboBox, 1,2);
        gridPane.add(new Label("Rating"), 0,3 );
        gridPane.add(ratingComboBox, 1,3);
        gridPane.add(new Label("Date Published"), 0,4 );
        gridPane.add(datePicker, 1,4);
        gridPane.add(new Label("ISBN"), 0,5 );
        gridPane.add(isbnTextField,1,5);
        gridPane.add(okInsertBookButton, 0,6);



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
                    showAlert(Alert.AlertType.WARNING, "Please enter a date in the required format");
                }
            return null;
            }
        });

        //ratingComboBox.setEditable(true);


        addAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                authorsToCreateList.clear();
                authorIdsToReturn.clear();
                extraAuthorComboBoxes.clear();
                addAuthorDialog();
            }
        });

        okInsertBookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                title = titleTextField.getCharacters().toString();
                genre = (String) genreComboBox.getValue();
                rating = (Integer) ratingComboBox.getValue();
                datePublished = datePicker.getValue().toString();
                isbn = isbnTextField.getCharacters().toString();
                System.out.println(title + genre + datePublished + isbn + rating);
                if(title!=null && genre!=null && datePublished!=null && isbn!=null){
                    controller.handleAddBook(isbn, title, datePublished, genre, rating, authorsToCreateList);
                    controller.handleRelateBookWithAuthor(isbn, authorIdsToReturn);
                    stage.close();
                    titleTextField.clear();
                    genreTextField.clear();
                    datePicker.setValue(null);
                    ratingComboBox.getSelectionModel().clearSelection();
                    genreComboBox.getSelectionModel().clearSelection();
                    dateTextField.clear();
                    isbnTextField.clear();
                }
                else{
                    showAlert(Alert.AlertType.WARNING, "Please fill in all the fields!");
                }


            }
        });

        plusAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ComboBox temp = new ComboBox<>();
                temp.setEditable(true);
                temp.getItems().addAll(FXCollections.observableArrayList(authorListToStringList(allAuthors)));
                authorListVBox.getChildren().add(temp);
                extraAuthorComboBoxes.add(temp);
                authorScene.getWindow().sizeToScene();
            }
        });


        okConfirmAuthorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                for (ComboBox c : extraAuthorComboBoxes){
                    if((String)c.getSelectionModel().getSelectedItem()!="" && c.getSelectionModel().getSelectedItem()!=null){
                        int tempIndex = c.getSelectionModel().getSelectedIndex();
                        System.out.println("adding... : " + c.getSelectionModel().getSelectedItem() + " - " + c.getSelectionModel().getSelectedIndex());
                        if(tempIndex==-1){
                            authorsToCreateList.add((String)c.getSelectionModel().getSelectedItem());
                        }
                        else{
                            authorIdsToReturn.add(allAuthors.get(tempIndex).getAuthorId());
                        }
                    }
                    else System.out.println("empty combobox");
                }

                authorStage.close();

            }
        });

        stage.setScene(scene);

    }

    public void addAuthorDialog(){

        authorStage = new Stage();
        authorStage.initModality(Modality.APPLICATION_MODAL);
        authorGridPane = new GridPane();
        authorVBox = new VBox();

        authorVBox.setPadding(new Insets(8,8,8,8));
        authorScene = new Scene(authorVBox, 400, 300);
        textFlowAuthors = new TextFlow(new Text("Choose existing author from the dropdown menu,or write the name of the new author."));
        authorListVBox = new VBox(5);
        authorListVBox.setPadding(new Insets(5,5,5,5));
        authorListVBox.setPrefHeight(250);
        authorVBox.setAlignment(Pos.CENTER);
        authorStage.setTitle("Add Author");
        authorGridPane.setPadding(new Insets(10, 10, 10, 10));
        authorGridPane.setVgap(5);
        authorGridPane.setHgap(5);
        textFlowAuthors.setTextAlignment(TextAlignment.CENTER);
        textFlowAuthors.autosize();
        textFlowAuthors.setPadding(new Insets(8,8,8,8));


        //Create nodes

        HBox buttonHbox = new HBox();
        buttonHbox.setAlignment(Pos.CENTER);
        buttonHbox.setSpacing(12);

        // Add to Gridpane
        authorVBox.getChildren().add(textFlowAuthors);
        authorVBox.getChildren().add(authorGridPane);
        authorVBox.getChildren().add(buttonHbox);
        authorListVBox.setAlignment(Pos.TOP_CENTER);
        authorGridPane.setAlignment(Pos.CENTER);
        authorGridPane.add(authorListVBox, 0, 0);

        authorListVBox.getChildren().add(existingAuthorsComboBox);
        VBox wrapButton = new VBox(plusAuthorButton);
        wrapButton.setAlignment(Pos.TOP_CENTER);
        wrapButton.setPadding(new Insets(5,5,5,5));
        authorGridPane.add(wrapButton,1,0);
        buttonHbox.getChildren().addAll(cancelAuthorButton, okConfirmAuthorsButton);
        authorStage.setScene(authorScene);
        authorStage.showAndWait();


        // Function

    }
    public void showDialog(ArrayList<Author> allAuthors){

        existingAuthorsComboBox.getItems().addAll(FXCollections.observableArrayList(authorListToStringList(allAuthors)));
        this.allAuthors = allAuthors;
        stage.showAndWait();
    }

    private static ArrayList<String> authorListToStringList(ArrayList<Author> authorList){
        ArrayList<String> stringList = new ArrayList<>();
        for(Author a : authorList){
            stringList.add(a.getFullName());
        }
        return stringList;
    }

    private void showAlert(Alert.AlertType alertType, String msg){
        Alert alert = new Alert(alertType, msg);
        alert.showAndWait();
    }

    public String getTitle() {
        return title;
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

    public Button getOkInsertBookButton() {
        return okInsertBookButton;
    }
}
