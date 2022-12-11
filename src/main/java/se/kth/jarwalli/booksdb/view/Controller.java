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
        new Thread("onSearchSelectedThread"){
            @Override
            public void run(){
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
                        List<Book> finalResult = result;
                        javafx.application.Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalResult == null || finalResult.isEmpty()) {

                                            booksView.showAlertAndWait(
                                                    "No results found.", INFORMATION);
                                        } else {
                                            booksView.displayBooks(finalResult);
                                        }

                                    }
                                }
                        );
                    } else {
                        javafx.application.Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        booksView.showAlertAndWait("Enter a search string!", WARNING);
                                    }
                                }

                        );
                    }
                } catch (Exception e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Database error.", ERROR);

                                }
                            }
                    );
                }

            }
        }.start();
    }

    void handleConnection() {
        new Thread("handleConnectionThread"){
            @Override
            public void run(){
                try {
                    booksDb.connect("Library");
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Connection Failed!", ERROR);
                                }
                            }
                    );
                }

            }
        }.start();
    }

    void handleDisconnect() {

        System.out.println("innan");
        new Thread("handleDisconnectThread"){
            @Override
            public void run(){
                System.out.println("i run");
                try {
                    booksDb.disconnect();
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Disconnect failed", ERROR);

                                }
                            });
                }
            }
        }.start();
    }


    void handleAddBook(String isbn, String title, String published, String genre, Integer rating, ArrayList<String> authorsToCreate, ArrayList<Integer> existingAuthorIds){
        new Thread ("handleAddBookThread"){
            @Override
            public void run() {
                try {
                    booksDb.addBook(isbn, title, published, genre, rating, authorsToCreate, existingAuthorIds);
                }catch (BooksDbException e){
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Something went wrong and your book has not been added to the database", ERROR);
                                }
                            });
                }
            }
        }.start();
    }



    void handleDeleteBook(String isbn, String title) {
        Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to delete " + title + " from the database?", CONFIRMATION);
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
        new Thread("handleDeleteBookThread"){
            @Override
            public void run(){
                try {
                    booksDb.deleteBook(isbn);
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Something went wrong and your book has not been deleted from the database", ERROR);
                                }
                            });

                }

            }
        }.start();
        booksView.clearBooks();
    }

    void handleUpdateBook(int rating, String isbn) {
        new Thread("handleUpdateBookThread"){
            @Override
            public void run(){
                try {
                    booksDb.updateBook(rating, isbn);
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("An error occurred and your update has not been successful", ERROR);

                                }
                            });
                }
            }
        }.start();
    }


    void handleLogin(String username, String password) {
        new Thread("handleLoginThread"){
            @Override
            public void run(){
                try {
                    booksDb.login(username, password, "Library");
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("An error occurred and you have not been logged in", ERROR);

                                }
                            });
                }
            }
        }.start();
    }

    void retrieveAllAuthors() {
        new Thread("handleRetriveAllAuthorsThread"){

                ArrayList<Author> allAuthors =new ArrayList<>();
            @Override
            public void run(){
                try {
                    allAuthors = booksDb.retrieveAllAuthors();
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.getInsertDialog().showDialog(allAuthors);
                                }
                            }

                    );
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("There has been an error retrieving the authors from the database. You wont be able to choose existing authors. Please contact your database admin to fix the problem", ERROR);

                                }
                            });
                }
            }
        }.start();
    }

}


