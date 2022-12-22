package se.kth.jarwalli.booksdb.model;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.client.*;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoException;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.singletonList;


public class BooksDbImplMongo implements BooksDbInterface {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ArrayList<Book> result;

    public BooksDbImplMongo() {
        result = new ArrayList<>();
    }

    @Override
    public boolean connect(String database) throws BooksDbException {

        String uri = "mongodb://localhost:27017";
        try {
            mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(database);
            System.out.println("Connected successfully to server.");
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
        mongoClient.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        //System.out.println(mongoDatabase.getName());
        result.clear();
        MongoCollection<Document> collection = mongoDatabase.getCollection("Book");

        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.regex("title", searchTitle, "i")),
                Aggregates.lookup("Author", "author", "_id", "author")
        );

        AggregateIterable<Document> aggregateIterable = collection.aggregate(pipeline);


        retrieveBooks(aggregateIterable);
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

    private void retrieveBooks(AggregateIterable<Document> aggregateIterable) {
        for (Document document : aggregateIterable) {
            Book tempBook;
            document.get("author");
            result.add(tempBook = new Book(document.getString("isbn"),
                    document.getString("title"),
                    document.getString("datePublished"),
                    // IF WANTING TO FORMAT DATE:
                    // document.getDate("datePublished").toInstant().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    document.getString("genre"),
                    document.getInteger("rating")));
            List<Document> authors = (List<Document>) (document.get("author"));
            for (Document author : authors) {
                tempBook.addAuthor(author.getString("fullName"));
            }
        }
    }
}