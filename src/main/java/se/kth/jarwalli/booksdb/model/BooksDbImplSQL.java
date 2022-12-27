/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.jarwalli.booksdb.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImplSQL implements BooksDbInterface {

    private ArrayList<Book> result;

    public BooksDbImplSQL() {
        result = new ArrayList<>();
    }

    private Connection con = null;

    /**
     * Connects the user to the MySQL database
     * @param database The name of the database to connect to
     * @return returns the boolean representation if the connection succeeded or not
     * @throws BooksDbException
     */
    @Override
    public boolean connect(String database) throws BooksDbException {
        String user = "UserKTH"; // user name
        String pwd = "mypassword"; // password
        System.out.println(user + ", *********");
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
        } catch (SQLException | ClassNotFoundException e) {
            throw new BooksDbException(e.getMessage(), e);
        }

        return true;
    }

    /**
     * Disconnects the user from the MySQL database
     * @throws BooksDbException
     */
    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (con != null) {
                con.close();
                System.out.println("You have successfully disconnected");
            }
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /**
     * Gets a list of all the authors available in the database to be shown as options in the UI when adding a author to a book
     * @return The list of Authors
     * @throws BooksDbException
     */
    @Override
    public ArrayList<Author> retrieveAllAuthors() throws BooksDbException {
        ArrayList<Author> allAuthors = new ArrayList<>();
        ArrayList<Author> allAuthorsCopy;
        String sql = "SELECT authorId, fullname FROM author";
        try (Statement stmt = con.createStatement()) {
            ResultSet resultset = stmt.executeQuery(sql);

            while (resultset.next()) {
                allAuthors.add(new Author(resultset.getString("authorId"), resultset.getString("fullName")));
            }
            allAuthorsCopy = (ArrayList<Author>) allAuthors.clone();
            allAuthors.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return allAuthorsCopy;
    }

    /**
     * Searches for a book by title in the database
     * @param searchTitle The title of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        ArrayList<Book> tmp;
        String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                + " FROM Book LEFT JOIN wrote ON book.isbn=wrote.isbn LEFT JOIN author ON author.authorId= wrote.authorId"
                + " WHERE title LIKE ? GROUP BY book.isbn";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, "%" + searchTitle + "%");
            ResultSet pResultset = pstmt.executeQuery();
            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }

        return tmp;
    }


    /**
     * Searches for a book by isbn in the database
     * @param searchIsbn The isbn of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBooksByISBN(String searchIsbn) throws BooksDbException {
        ArrayList<Book> tmp = new ArrayList<>();
        String sql = "SELECT  book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                + " FROM Book LEFT JOIN wrote ON book.isbn=wrote.isbn LEFT JOIN author ON author.authorId= wrote.authorId"
                + " WHERE book.isbn LIKE ? GROUP BY book.isbn";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){

            pstmt.setString(1, "%" + searchIsbn + "%");
            ResultSet pResultset = pstmt.executeQuery();
            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }

        return tmp;
    }

    /**
     * Searches for a book by author in the database
     * @param searchAuthor The author of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBookByAuthor(String searchAuthor) throws BooksDbException {
        ArrayList<Book> tmp;
        String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                + " FROM Book LEFT JOIN wrote ON book.isbn=wrote.isbn LEFT JOIN author ON author.authorId= wrote.authorId"
                + " GROUP BY book.isbn HAVING fullname LIKE ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){

            pstmt.setString(1, "%" + searchAuthor + "%");
            ResultSet pResultset = pstmt.executeQuery();

            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return tmp;
    }

    /**
     * Searches for a book by genre in the database
     * @param searchGenre The genre of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBookByGenre(String searchGenre) throws BooksDbException {
        ArrayList<Book> tmp;

        String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                + " FROM Book LEFT JOIN wrote ON book.isbn=wrote.isbn LEFT JOIN author ON author.authorId= wrote.authorId"
                + " GROUP BY book.isbn HAVING genre LIKE ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){

            pstmt.setString(1, "%" + searchGenre + "%");
            ResultSet pResultset = pstmt.executeQuery();

            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return tmp;
    }

    /**
     * Searches for a book by rating in the database
     * @param searchRating The rating of a book to search for
     * @return A list of books to be shown in the UI
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBookByRating(int searchRating) throws BooksDbException {
        ArrayList<Book> tmp;
        String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                + " FROM Book LEFT JOIN wrote ON book.isbn=wrote.isbn LEFT JOIN author ON author.authorId= wrote.authorId"
                + " GROUP BY book.isbn HAVING rating LIKE ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql);){

            pstmt.setString(1, "%" + searchRating + "%");
            ResultSet pResultset = pstmt.executeQuery();

            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return tmp;
    }

    /**
     * Delete a book from the database
     * @param book to be deleted
     * @return returns the boolean representation if the book got deleted or not
     * @throws BooksDbException
     */
    @Override
    public Book deleteBook(Book book) throws BooksDbException {
        String sql = "DELETE FROM Book WHERE isbn = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return book;
    }


    /**
     * Updates the rating of a book
     * @param book Book to be updated
     * @return returns the boolean representation if the update succeeded or not
     * @throws BooksDbException
     */
    @Override
    public Book updateBook(Book book) throws BooksDbException{
        String sql = "UPDATE Book SET rating = ?" + " WHERE isbn = ?;";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, book.getRating());
            pstmt.setString(2, book.getIsbn());
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return book;
    }

    /**
     * Logs in a user
     * @param user the user to be logged in
     * @param pwd the password of the user
     * @param database the database to logg into
     * @return returns the boolean representation if the login succeeded or not
     * @throws BooksDbException
     */

    public void login(String user, String pwd, String database) throws BooksDbException {
        System.out.println(user + ", *********");
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
        } catch (SQLException | ClassNotFoundException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /**
     * Gives the user that is currently logged in
     * @return The user that is currently logged in
     * @throws BooksDbException
     */
    public String retrieveCurrentUser() throws BooksDbException {
        String sql = "SELECT current_user()";
        String tmpUser = "";
        try (Statement stmt = con.createStatement()) {
            ResultSet resultset = stmt.executeQuery(sql);
            while (resultset.next()){
                tmpUser = resultset.getString("current_user()");
            }
            String[] substringOfUser = tmpUser.split("@", 0);
            tmpUser = substringOfUser[0];
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return tmpUser;
    }

    /**
     * adds a review to a book
     * @param review The review to be added
     * @return returns the boolean representation if a review was added or not
     * @throws BooksDbException
     */
    @Override
    public Review addReview(Review review ) throws BooksDbException {
        String sql = "INSERT INTO review VALUES (?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, review.getIsbn());
            pstmt.setString(2, review.getUser());
            pstmt.setString(3, review.getReviewDate());
            pstmt.setString(4, review.getText());
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return review;

    }

    /**
     * Adds a book to the database with a transaction.
     * @param book to be added
     * @param authors The author(s) of the book
     * @param existingAuthorsList A list of the authordId's of the authors
     * @return returns the boolean representation if a new book was added or not
     * @throws BooksDbException
     */
    @Override
    public Book addBook(Book book, ArrayList<String> authors, ArrayList<Author> existingAuthorsList) throws BooksDbException {
        // Step 1: Create Book
        String sql = "INSERT INTO Book VALUES(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            con.setAutoCommit(false);
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPublished());
            pstmt.setString(4, book.getGenre());
            if(book.getRating()==null){
                pstmt.setNull(5, Types.INTEGER);
            }
            else {pstmt.setInt(5, book.getRating());}
            int n = pstmt.executeUpdate();

            // Step 2: Create Author
            String sql2 = "INSERT INTO Author VALUES(NULL, ?, NULL)";
            for (int i = 0; i < authors.size(); i++) {
                pstmt = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, authors.get(i));
                pstmt.executeUpdate();
                ResultSet resultAuthorKey = pstmt.getGeneratedKeys();
                while (resultAuthorKey.next()) {
                    existingAuthorsList.add(new Author((((Integer)resultAuthorKey.getInt(1)).toString()), authors.get(i)));
                }

            }

            // Step 3: Connect Author with Book
            String sql3 = "INSERT INTO Wrote VALUES(?, ?)";
            for (int i = 0; i < existingAuthorsList.size(); i++) {
                pstmt = con.prepareStatement(sql3);
                pstmt.setString(1, existingAuthorsList.get(i).getAuthorId());
                pstmt.setString(2, book.getIsbn());
                pstmt.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new BooksDbException(ex.getMessage(), ex);
                }
            }
            throw new BooksDbException(e.getMessage(), e);

        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    throw new BooksDbException(e.getMessage(), e);
                }

            }
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException(e.getMessage(), e);
            }
        }
        return book;
    }

    /**
     * Creates new book objects of the resulting books of a search
     * @param pResultSet The resulting books of a search
     * @throws SQLException
     */
    private void retrieveBooks(ResultSet pResultSet) throws SQLException {
        while (pResultSet.next()) {
            Book tempBook;
            result.add(tempBook = new Book(pResultSet.getString("ISBN"),
                    pResultSet.getString("title"),
                    pResultSet.getString("datePublished"),
                    pResultSet.getString("genre"),
                    pResultSet.getInt("rating")));
            if(pResultSet.getString("fullname")!=null){
                String[] tempNames = pResultSet.getString("fullname").split(",", 0);
                for (int i = 0; i < tempNames.length; i++) {
                    tempBook.addAuthor(tempNames[i]);
                }
            }
        }
    }
}
