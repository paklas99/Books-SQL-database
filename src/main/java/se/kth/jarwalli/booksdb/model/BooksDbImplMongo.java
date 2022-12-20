package se.kth.jarwalli.booksdb.model;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.*;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoException;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.singletonList;


public class BooksDbImplMongo implements BooksDbInterface{
    private MongoDatabase mongoDatabase;
    private ArrayList<Book> result;

    public BooksDbImplMongo() {
        result = new ArrayList<>();
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
/*        String user = "UserKTH"; // user name
        String pwd = "mypassword"; // password
        System.out.println(user + ", *********");
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";*/
        String uri = "mongodb://localhost:27017";


        MongoClient mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(database);
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

        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {

    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException, SQLException {
        //System.out.println(mongoDatabase.getName());
        MongoCollection<Document> collection = mongoDatabase.getCollection("Book");

        FindIterable find = collection.find(eq("title", searchTitle));

        retrieveBooks(find);
        return result;
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

    private void retrieveBooks(FindIterable find){
        MongoCursor<Document> cursor = find.iterator();
        while(cursor.hasNext()){
            Book tempBook;
            System.out.println("Test");
            Document doc = cursor.next();



            System.out.println(doc.getString("isbn") +
                    doc.getString("title") +
                    doc.getString("datePublished") +
                    doc.getString("genre") +
                    doc.getInteger("rating"));
            //System.out.println(doc.getString("title") + ", " + doc.getInteger("rating"));
            result.add(new Book(doc.getString("isbn"),
                    doc.getString("title"),
                    doc.getString("datePublished"),
                    doc.getString("genre"),
                    doc.getInteger("rating")));
            System.out.println("r: " + result);

        }

    }

}
