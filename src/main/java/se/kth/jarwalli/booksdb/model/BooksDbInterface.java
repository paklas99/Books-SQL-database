package se.kth.jarwalli.booksdb.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database) throws BooksDbException;

    /**
     * Disconnects the user from the MySQL database
     * @throws BooksDbException
     */
    public void disconnect() throws BooksDbException;

    /**
     * Searches for a book by title in the database
     * @param searchTitle The title of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException, SQLException;
    /**
     * Searches for a book by isbn in the database
     * @param searchIsbn The isbn of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    public List<Book> searchBooksByISBN(String searchIsbn) throws BooksDbException;
    /**
     * Searches for a book by author in the database
     * @param searchAuthor The author of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    public List<Book> searchBookByAuthor(String searchAuthor) throws BooksDbException;
    /**
     * Searches for a book by genre in the database
     * @param searchGenre The genre of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    public List<Book> searchBookByGenre(String searchGenre) throws BooksDbException;
    /**
     * Searches for a book by rating in the database
     * @param searchRating The rating of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    public List<Book> searchBookByRating(int searchRating) throws BooksDbException;
    /**
     * Adds a book to the database with a transaction.
     * @param book to be added
     * @param authors The author(s) of the book
     * @param authorIdList A list of the authordId's of the authors
     * @return returns the boolean representation if a new book was added or not
     * @throws BooksDbException
     */
    public Book addBook(Book book, ArrayList<String> authors, ArrayList<Integer> authorIdList) throws BooksDbException;
    /**
     * Delete a book from the database
     * @param book to be deleted
     * @return returns the boolean representation if the book got deleted or not
     * @throws BooksDbException
     */
    public Book deleteBook(Book book) throws BooksDbException;
    /**
     * Gets a list of all the authors available in the database to be shown as options in the UI when adding a author to a book
     * @return The list of Authors
     * @throws BooksDbException
     */
    public ArrayList<Author> retrieveAllAuthors() throws BooksDbException;
    /**
     * Updates the rating of a book
     * @param book Book to be updated
     * @return returns the boolean representation if the update succeeded or not
     * @throws BooksDbException
     */
    public Book updateBook(Book book) throws BooksDbException;
    /**
     * Logs in a user
     * @param user the user to be logged in
     * @param pwd the password of the user
     * @param database the database to logg into
     * @return returns the boolean representation if the login succeeded or not
     * @throws BooksDbException
     */
    public void login(String user, String pwd, String database) throws BooksDbException;
    /**
     * adds a review to a book
     * @param review The review to be added
     * @return returns the boolean representation if a review was added or not
     * @throws BooksDbException
     */
    public Review addReview(Review review ) throws BooksDbException;
    /**
     * Gives the user that is currently logged in
     * @return The user that is currently logged in
     * @throws BooksDbException
     */
    public String retriveCurrentUser() throws BooksDbException;
}
