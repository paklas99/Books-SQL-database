package se.kth.jarwalli.booksdb.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import se.kth.jarwalli.booksdb.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            if (searchFor != null && searchFor.length() > 0) {
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
                    case Genre:
                        result = booksDb.searchBookByGenre(searchFor);
                        break;
                    case Rating:
                        result = booksDb.searchBookByRating(Integer.valueOf(searchFor));
                        break;

                    default:
                        result = new ArrayList<>();
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
            booksView.showAlertAndWait("Database error.", ERROR);
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


    void handleAddBook(String isbn, String title, String published, String genre, Integer rating, ArrayList<String> authorsToCreate){
        try {
            booksDb.addBook(isbn, title, published, genre, rating, authorsToCreate);
        }catch (BooksDbException e){

            // TODO: add alert box
        }
    }

    void handleDeleteBook(String isbn, String title) {
        try {
            Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to delete " + title + " from the database?", CONFIRMATION);
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
            booksDb.deleteBook(isbn);
            booksView.clearBooks();
        } catch (BooksDbException e) {

        }
    }

    void handleRelateBookWithAuthor(String isbn, ArrayList<Integer> authorIds){
        try{
            booksDb.relateBookWithAuthor(isbn, authorIds);
        }catch (BooksDbException e){
            // TODO
        }
    }
    void handleUpdateBook(int rating, String isbn) {
        try {
            /*Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to update " + isbn + " from the database?", CONFIRMATION);
            if (result.isPresent() && result.get()== ButtonType.CANCEL){
                return;
            }*/
            booksDb.updateBook(rating, isbn);
        } catch (BooksDbException e) {

        }
    }

    void handleLogin(String username, String password) {
        try {
            /*Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to update " + isbn + " from the database?", CONFIRMATION);
            if (result.isPresent() && result.get()== ButtonType.CANCEL){
                return;
            }*/
            booksDb.login(username, password, "Library");
        } catch (BooksDbException e) {

        }
    }

    void handleReview(String text){
        try {
            /*Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to update " + isbn + " from the database?", CONFIRMATION);
            if (result.isPresent() && result.get()== ButtonType.CANCEL){
                return;
            }*/
            booksDb.review("test");
        } catch (BooksDbException e) {

        }
    }

    ArrayList<Author> retrieveAllAuthors() {
        ArrayList<Author> allAuthors = null;
        try {
            allAuthors = booksDb.retrieveAllAuthors();
        } catch (BooksDbException e) {
            // TODO
        }
        return allAuthors;
    }

}


