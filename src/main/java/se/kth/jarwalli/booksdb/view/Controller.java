package se.kth.jarwalli.booksdb.view;

import se.kth.jarwalli.booksdb.model.Book;
import se.kth.jarwalli.booksdb.model.BooksDbException;
import se.kth.jarwalli.booksdb.model.BooksDbInterface;
import se.kth.jarwalli.booksdb.model.SearchMode;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        result = booksDb.searchBooksByISBN(searchFor);
                        break;
                    case Author:
                        result = booksDb.searchBookByAuthor(searchFor);
                        break;
                    default:
                        result= new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
    void handleConnection() {
        try {
            booksDb.connect("Library");
        } catch (BooksDbException e) {
            System.out.println("caught!");
            // TODO: add alert box
        }
    }

    void handleDisconnect() {
        try {
            booksDb.disconnect();
        } catch (BooksDbException e) {
            // TODO: Add alert box
        }
    }

    void handleAddBook(String isbn, String title, String published, String genre, int rating, String authors){
        booksDb.addBook(isbn, title, published, genre, rating, authors);
    }
}


