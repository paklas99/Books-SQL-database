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

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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
        } catch (SQLException | ClassNotFoundException e) {
            throw new BooksDbException(e.getMessage(), e);
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
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Author> retrieveAllAuthors() throws BooksDbException {
        ArrayList<Author> allAuthors = new ArrayList<>();
        ArrayList<Author> allAuthorsCopy;
        String sql = "SELECT authorId, fullname FROM author";
        try (Statement stmt = con.createStatement()) {
            ResultSet resultset = stmt.executeQuery(sql);

            while (resultset.next()) {
                allAuthors.add(new Author(resultset.getInt("authorId"), resultset.getString("fullName")));
            }
            allAuthorsCopy = (ArrayList<Author>) allAuthors.clone();
            allAuthors.clear();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return allAuthorsCopy;
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

    // TODO TRY with resources i alla sökfunktioner
    @Override
    public List<Book> searchBookByAuthor(String author) throws BooksDbException {
        ArrayList<Book> tmp;
        PreparedStatement pstmt = null;
        try {
            String sql = "SELECT book.title, book.isbn, GROUP_CONCAT(author.fullname) AS fullname, book.datePublished, book.rating, book.genre"
                    + " FROM author JOIN book JOIN wrote ON book.isbn = wrote.isbn AND author.authorId = wrote.authorId"
                    + " GROUP BY book.isbn HAVING fullname LIKE ?";
            pstmt = con.prepareStatement(sql);
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
    public boolean deleteBook(String isbn) throws BooksDbException {
        String sql = "DELETE FROM Book WHERE isbn = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return true;
    }

    public boolean relateBookWithAuthor(String isbn, ArrayList<Integer> authorIds) throws BooksDbException {
        String sql = "INSERT INTO wrote VALUES(?, ?)";
        for (Integer i : authorIds) {
            System.out.println(i);
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, i);
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new BooksDbException(e.getMessage(), e);
            }
        }
        return true;
    }

    @Override
    public boolean addBook(String isbn, String title, String datePublished, String genre, Integer rating, ArrayList<String> authors) throws BooksDbException {
        ArrayList<Integer> authorIdList = new ArrayList<>();
        // Step 1: Create Book
        String sql = "INSERT INTO Book VALUES(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            con.setAutoCommit(false);
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, datePublished);
            pstmt.setString(4, genre);
            if(rating==null){
                pstmt.setNull(5, Types.INTEGER);
            }
            else {pstmt.setInt(5, rating);}
            int n = pstmt.executeUpdate();

            // Step 2: Create Author
            String sql2 = "INSERT INTO Author VALUES(NULL, ?, NULL)";
            for (int i = 0; i < authors.size(); i++) {
                pstmt = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, authors.get(i));
                pstmt.executeUpdate();
                ResultSet resultAuthorKey = pstmt.getGeneratedKeys();
                while (resultAuthorKey.next()) {
                    authorIdList.add(resultAuthorKey.getInt(1));
                }
                System.out.println("first");
                System.out.println("lista" + authorIdList);
            }

            // Step 3: Connect Author with Book
            String sql3 = "INSERT INTO Wrote VALUES(?, ?)";
            for (int i = 0; i < authorIdList.size(); i++) {
                pstmt = con.prepareStatement(sql3);
                pstmt.setInt(1, authorIdList.get(i));
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            System.out.println("Catched!");
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Rollback");
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
        return true;
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
            for (int i = 0; i < tempNames.length; i++) {
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
