package se.kth.jarwalli.booksdb.view;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import se.kth.jarwalli.booksdb.model.*;

import java.time.LocalDate;
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
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(

                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Database error.", ERROR);
                                    System.out.println(e.getMessage());

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

        new Thread("handleDisconnectThread"){
            @Override
            public void run(){
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


    void handleAddBook(Book book, ArrayList<String> authorsToCreate, ArrayList<Author> existingAuthorsList){
        new Thread ("handleAddBookThread"){
            @Override
            public void run() {
                try {
                    booksDb.addBook(book, authorsToCreate, existingAuthorsList);
                }catch (BooksDbException e){
                    Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Something went wrong and your book has not been added to the database", ERROR);
                                    System.out.println(e.getMessage());
                                }
                            });
                }
            }
        }.start();
    }



    void handleDeleteBook(Book book) {
        Optional<ButtonType> result = booksView.showAlertAndWait("Are you sure you want to delete " + book.getTitle() + " from the database?", CONFIRMATION);
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
        }
        new Thread("handleDeleteBookThread"){
            @Override
            public void run(){
                try {
                    booksDb.deleteBook(book);
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

    void handleUpdateBook(Book book) {
        new Thread("handleUpdateBookThread"){
            @Override
            public void run(){
                try {
                    booksDb.updateBook(book);
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
        boolean successful;
            @Override
            public void run(){
                try {
                    successful = booksDb.login(username, password, "Library");
                    if(!successful){
                        javafx.application.Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        booksView.showAlertAndWait("An error occurred and you have not been logged in", ERROR);

                                    }
                                });
                    }
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
        new Thread("handleRetriveAllAuthorsThread") {

            ArrayList<Author> allAuthors = new ArrayList<>();

            @Override
            public void run() {
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



    void handleReview(String isbn, String text) {
        new Thread("handleReviewThread"){
            @Override
            public void run(){
                try {
                    String username = booksDb.retrieveCurrentUser();
                    booksDb.addReview(new Review(text, username, LocalDate.now().toString(), isbn ));
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Your review was not published. Remember that you can only review once per book", ERROR);

                                }
                            });
                }
            }
        }.start();
    }

}


