/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.jarwalli.booksdb.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private ArrayList<Book> result;

    public BooksDbImpl() {
        //books = Arrays.asList(DATA);
        result = new ArrayList<>();
    }

    private Connection con = null;

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
        } catch (SQLException | ClassNotFoundException e){
            throw new BooksDbException(e.getMessage(),e);
        }

        return true;
    }


    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (con != null) {
                con.close();
                System.out.println("You have successfully disconnected");
            }
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(),e);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        ArrayList<Book> tmp = new ArrayList<>();
        try {
            String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                        + " FROM author JOIN book JOIN wrote ON book.isbn = wrote.isbn AND author.authorId = wrote.authorId"
                        + " WHERE title LIKE ? GROUP BY book.isbn";
            PreparedStatement pstmt = con.prepareStatement(sql);
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

    @Override
    public List<Book> searchBooksByISBN(String isbn) throws BooksDbException {
        ArrayList<Book> tmp = new ArrayList<>();
        try {
            String sql = "SELECT  book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                        + " FROM author JOIN book JOIN wrote ON book.isbn = wrote.isbn AND author.authorId = wrote.authorId"
                        + " WHERE wrote.isbn LIKE ? GROUP BY book.isbn";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + isbn + "%");
            ResultSet pResultset = pstmt.executeQuery();
            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }

        return tmp;
    }

    @Override
    public List<Book> searchBookByAuthor(String author) throws BooksDbException {
        ArrayList<Book> tmp;
        try {
            String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                        + " FROM author JOIN book JOIN wrote ON book.isbn = wrote.isbn AND author.authorId = wrote.authorId"
                        + " GROUP BY book.isbn HAVING fullname LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + author + "%");
            ResultSet pResultset = pstmt.executeQuery();

            retrieveBooks(pResultset);

            tmp = (ArrayList<Book>) result.clone();
            result.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }

        return tmp;
    }

    @Override
    public Book addBook(String isbn, String title, String datePublished, String genre, int rating, String authors) {
        Book bookToAdd;
        result.add(bookToAdd = new Book(isbn, title, datePublished, genre, rating));
        return bookToAdd;
    }

    private void retrieveBooks(ResultSet pResultSet) throws SQLException {
        //System.out.println(pResultSet.getString("fullname"));
        while (pResultSet.next()) {
            Book tempBook;
            result.add(tempBook = new Book(pResultSet.getString("ISBN"),
                    pResultSet.getString("title"),
                    pResultSet.getString("datePublished"),
                    pResultSet.getString("genre"),
                    pResultSet.getInt("rating")));
            String[] tempNames = pResultSet.getString("fullname").split(",", 0);
            for(int i=0; i<tempNames.length; i++){
                tempBook.addAuthor(tempNames[i]);
            }
        }
    }

    /*private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1)),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1)),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1)),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1)),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1)),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1)),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1)),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1)),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1)),
    };*/
}
