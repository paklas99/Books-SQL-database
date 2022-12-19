package se.kth.jarwalli.booksdb.model;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoException;


public class BooksDbImplMongo implements BooksDbInterface{
    @Override
    public boolean connect(String database) throws BooksDbException {
/*        String user = "UserKTH"; // user name
        String pwd = "mypassword"; // password
        System.out.println(user + ", *********");
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";*/
        String uri = "mongodb://localhost:27017";



        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            try {
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = mongoDatabase.runCommand(command);
                System.out.println("Connected successfully to server.");
            } catch (MongoException me) {
                System.err.println("An error occurred while attempting to run a command: " + me);

            }
/*
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);*/
/*            System.out.println("Connected!");
        } catch (SQLException | ClassNotFoundException e) {
            throw new BooksDbException(e.getMessage(), e);
        }*/
        }

        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {

    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException, SQLException {
        return null;
    }

    @Override
    public List<Book> searchBooksByISBN(String searchIsbn) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBookByAuthor(String searchAuthor) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBookByGenre(String searchGenre) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBookByRating(int searchRating) throws BooksDbException {
        return null;
    }

    @Override
    public Book addBook(Book book, ArrayList<String> authors, ArrayList<Integer> authorIdList) throws BooksDbException {
        return null;
    }

    @Override
    public Book deleteBook(Book book) throws BooksDbException {
        return null;
    }

    @Override
    public ArrayList<Author> retrieveAllAuthors() throws BooksDbException {
        return null;
    }

    @Override
    public Book updateBook(Book book) throws BooksDbException {
        return null;
    }

    @Override
    public void login(String user, String pwd, String database) throws BooksDbException {

    }

    @Override
    public Review addReview(Review review) throws BooksDbException {
        return null;
    }

    @Override
    public String retrieveCurrentUser() throws BooksDbException {
        return null;
    }
}
