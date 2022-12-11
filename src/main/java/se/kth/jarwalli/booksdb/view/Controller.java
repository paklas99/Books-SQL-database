package se.kth.jarwalli.booksdb.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import se.kth.jarwalli.booksdb.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
            booksView.showAlertAndWait("Connection Failed!", ERROR);
        }
    }

    void handleDisconnect() {
        try {
            booksDb.disconnect();
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("Disconnect failed", ERROR);
        }
    }


    void handleAddBook(String isbn, String title, String published, String genre, Integer rating, ArrayList<String> authorsToCreate, ArrayList<Integer> existingAuthorIds){
        Thread t1 = new Thread ("handleAddBookThread"){
            @Override
            public void run() {
                System.out.println("NU KÖRS ADDBOOk Thread!!!" + currentThread().getName());
                try {
                    booksDb.addBook(isbn, title, published, genre, rating, authorsToCreate, existingAuthorIds);
                    TimeUnit.SECONDS.sleep(5);
                }catch (BooksDbException e){

                    // TODO: add alert box
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        t1.start();
        try{
            t1.join();
        } catch (InterruptedException e) {
        }
        System.out.println("Finito med tråd add book");
    }

    void handleDeleteBook(String isbn, String title) {
        try {
            Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to delete " + title + " from the database?", ERROR);
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
            booksDb.deleteBook(isbn);
            booksView.clearBooks();
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("Something went wrong and your book has not been deleted from the database", ERROR);
        }
    }


    void handleUpdateBook(int rating, String isbn) {
        try {
            booksDb.updateBook(rating, isbn);
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("An error occurred and your update has not been successful", ERROR);
        }
    }

    ArrayList<Author> retrieveAllAuthors() {
        ArrayList<Author> allAuthors = null;
        try {
            allAuthors = booksDb.retrieveAllAuthors();
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("There has been an error retrieving the authors from the database. You wont be able to choose existing authors. Please contact your database admin to fix the problem", ERROR);
        }
        return allAuthors;
    }

}


