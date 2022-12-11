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

    // TODO TRY with resources i alla sökfunktioner
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



    @Override
    public boolean updateBook(int rating, String isbn) throws BooksDbException{
        String sql = "UPDATE Book SET rating = ?" + " WHERE isbn = ?;";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, rating);
            pstmt.setString(2, isbn);
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return true;
    }

    public boolean login(String user, String pwd, String database) throws BooksDbException {
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

    public String retriveCurrentUser() throws BooksDbException {
        String sql = "SELECT current_user()";
        String tmpUser = "";
        try (Statement stmt = con.createStatement()) {
            ResultSet resultset = stmt.executeQuery(sql);
            while (resultset.next()){
                tmpUser = resultset.getString("current_user()");
            }
            String[] substringOfUser = tmpUser.split("@", 0);
            tmpUser = substringOfUser[0];
            System.out.println(tmpUser);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return tmpUser;
    }

    @Override
    public boolean addReview(String isbn, String username, String date, String review ) throws BooksDbException {
        String sql = "INSERT INTO review VALUES (?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, isbn);
            pstmt.setString(2, username);
            pstmt.setString(3, date);
            pstmt.setString(4, review);
            int n = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
        return true;

    }

    @Override
    public boolean addBook(String isbn, String title, String datePublished, String genre, Integer rating, ArrayList<String> authors, ArrayList<Integer> authorIdList) throws BooksDbException {
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
            System.out.println(pResultSet.getString("fullname"));
            if(pResultSet.getString("fullname")!=null){
                String[] tempNames = pResultSet.getString("fullname").split(",", 0);
                for (int i = 0; i < tempNames.length; i++) {
                    tempBook.addAuthor(tempNames[i]);
                }
            }
        }
    }
}
