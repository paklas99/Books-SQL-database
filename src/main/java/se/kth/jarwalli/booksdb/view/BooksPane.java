package se.kth.jarwalli.booksdb.view;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import se.kth.jarwalli.booksdb.model.Book;
import se.kth.jarwalli.booksdb.model.BooksDbImplMongo;
import se.kth.jarwalli.booksdb.model.BooksDbImplSQL;
import se.kth.jarwalli.booksdb.model.SearchMode;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private InsertDialog insertDialog;
    private UpdateDialog updateDialog;
    private LoginDialog loginDialog;
    private ReviewDialog reviewDialog;
    private MenuBar menuBar;

    public BooksPane(BooksDbImplMongo booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
        insertDialog = new InsertDialog(controller);
        updateDialog = new UpdateDialog(controller);
        loginDialog = new LoginDialog(controller);
        reviewDialog = new ReviewDialog(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    public void clearBooks(){
        booksInTable.clear();
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected Optional<ButtonType> showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }

    private String searchDialogs(String searchMode) {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setContentText("Enter a " + searchMode + " to");
        searchDialog.setTitle("Search by " + searchMode);
        searchDialog.setHeaderText("Search by " + searchMode);
        Optional<String> result = searchDialog.showAndWait();
        if (result.isEmpty()) System.out.println("Hola!");
        if (result.isEmpty()) return null;
        return result.get();
    }


    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);

        addHandlers(controller);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, Date> authorCol = new TableColumn<>("Author");

        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol, authorCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.3));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authors"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }


    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        MenuItem genreItem = new MenuItem("Genre");
        MenuItem ratingItem = new MenuItem("Rating");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem, genreItem, ratingItem);


        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        Menu reviewMenu = new Menu("Review");
        MenuItem review = new MenuItem("review");
        reviewMenu.getItems().addAll(review);

        Menu authenticationMenu = new Menu("Authentication");
        MenuItem login = new MenuItem("login");
        authenticationMenu.getItems().addAll(login);


        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu, authenticationMenu,reviewMenu);
    }

    public InsertDialog getInsertDialog() {
        return insertDialog;
    }

    private void addHandlers(Controller controller) {
        EventHandler<ActionEvent> menuSearchTitleHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    String searchCriteria = searchDialogs("title");
                    controller.onSearchSelected(searchCriteria, SearchMode.Title);
                }
            }
        };
        menuBar.getMenus().get(1).getItems().get(0).addEventHandler(ActionEvent.ACTION, menuSearchTitleHandler);

        EventHandler<ActionEvent> menuSearchIsbnHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    String searchCriteria = searchDialogs("isbn");
                    controller.onSearchSelected(searchCriteria, SearchMode.ISBN);
                }
            }
        };
        menuBar.getMenus().get(1).getItems().get(1).addEventHandler(ActionEvent.ACTION, menuSearchIsbnHandler);

        EventHandler<ActionEvent> menuSearchAuthorHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    String searchCriteria = searchDialogs("author");
                    controller.onSearchSelected(searchCriteria, SearchMode.Author);
                }
            }
        };
        menuBar.getMenus().get(1).getItems().get(2).addEventHandler(ActionEvent.ACTION, menuSearchAuthorHandler);

        EventHandler<ActionEvent> menuSearchGenreHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    String searchCriteria = searchDialogs("genre");
                    controller.onSearchSelected(searchCriteria, SearchMode.Genre);
                }
            }
        };
        menuBar.getMenus().get(1).getItems().get(3).addEventHandler(ActionEvent.ACTION, menuSearchGenreHandler);

        EventHandler<ActionEvent> menuSearchRatingHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    String searchCriteria = searchDialogs("Rating");
                    controller.onSearchSelected(searchCriteria, SearchMode.Rating);
                }
            }
        };
        menuBar.getMenus().get(1).getItems().get(4).addEventHandler(ActionEvent.ACTION, menuSearchRatingHandler);

        EventHandler<ActionEvent> connectHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    controller.handleConnection();

                }
            }
        };
        menuBar.getMenus().get(0).getItems().get(1).addEventHandler(ActionEvent.ACTION, connectHandler);

        EventHandler<ActionEvent> disconnectHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    controller.handleDisconnect();

                }
            }
        };
        menuBar.getMenus().get(0).getItems().get(2).addEventHandler(ActionEvent.ACTION, disconnectHandler);

        EventHandler<ActionEvent> reviewHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    Book tempBook = booksTable.getSelectionModel().getSelectedItem();
                    if(tempBook!=null){

                    }
                    else{
                        showAlertAndWait("You need to select the book you want to remove from the database.", Alert.AlertType.INFORMATION);
                    }

                    reviewDialog.showReviewDialog(tempBook);
                }
            }
        };
        menuBar.getMenus().get(4).getItems().get(0).addEventHandler(ActionEvent.ACTION, reviewHandler);

        EventHandler<ActionEvent> menuAddBookHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    controller.retrieveAllAuthors();
                }
            }
        };
        menuBar.getMenus().get(2).getItems().get(0).addEventHandler(ActionEvent.ACTION, menuAddBookHandler);

        EventHandler<ActionEvent> menuDeleteBookHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    Book tempBook = booksTable.getSelectionModel().getSelectedItem();
                    if(tempBook!=null){
                        controller.handleDeleteBook(tempBook);
                    }
                    else{
                        showAlertAndWait("You need to select the book you want to remove from the database.", Alert.AlertType.INFORMATION);
                    }


                }
            }
        };
        menuBar.getMenus().get(2).getItems().get(1).addEventHandler(ActionEvent.ACTION, menuDeleteBookHandler);

        EventHandler<ActionEvent> menuUpdateBookHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    Book tempBook = booksTable.getSelectionModel().getSelectedItem();
                    if(tempBook!=null){
                        controller.handleUpdateBook(tempBook);
                        updateDialog.showUpdateDialog(tempBook);
                    }
                    else{
                        showAlertAndWait("You need to select the book you want to update from the database.", Alert.AlertType.INFORMATION);
                    }
                }
            }
        };
        menuBar.getMenus().get(2).getItems().get(2).addEventHandler(ActionEvent.ACTION, menuUpdateBookHandler);

        EventHandler<ActionEvent> menuLoginHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    loginDialog.showLoginDialog();
                }
            }
        };
        menuBar.getMenus().get(3).getItems().get(0).addEventHandler(ActionEvent.ACTION, menuLoginHandler);

/*        EventHandler<ActionEvent> addBookHandler = new EventHandler<>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof MenuItem) {
                    insertDialog.showDialog();
                    controller.handleAddBook(insertDialog.getIsbn(), insertDialog.getTitle(), insertDialog.getDatePublished(),
                            insertDialog.getGenre(), insertDialog.getRating(), insertDialog.getAuthors());
                }
            }
        };
        insertDialog.getOkButton().addEventHandler(ActionEvent.ACTION, addBookHandler);*/

    }
}