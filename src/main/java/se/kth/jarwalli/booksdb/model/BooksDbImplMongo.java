package se.kth.jarwalli.booksdb.model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.mongodb.client.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import com.mongodb.MongoException;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;


public class BooksDbImplMongo implements BooksDbInterface {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ArrayList<Book> result;

    private String activeUser;

    public BooksDbImplMongo() {
        result = new ArrayList<>();
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        String uri = "mongodb://UserKTH:mypassword@localhost:27017/Library";

        //String uri = "mongodb://localhost:27017";
        try {
            mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(database);
            //mongoDatabase.runCommand(new Document("authenticate", 1).append("user", user).append("pwd", pwd));
            activeUser = "UserKTH";
            System.out.println("Connected successfully to server.");

        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            mongoClient.close();

        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        try {
            retrieveBooks(regex("title", searchTitle, "i"));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByISBN(String searchIsbn) throws BooksDbException {
        try {
            retrieveBooks(regex("_id", searchIsbn, "i"));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return result;
    }

    @Override
    public List<Book> searchBookByAuthor(String searchAuthor) throws BooksDbException {
        try {
            retrieveBooks(Filters.elemMatch("authors", regex("fullName", searchAuthor, "i")));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return result;
    }

    @Override
    public List<Book> searchBookByGenre(String searchGenre) throws BooksDbException {
        try {
            retrieveBooks(regex("genre", searchGenre, "i"));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return result;
    }

    @Override
    public List<Book> searchBookByRating(int searchRating) throws BooksDbException {
        try {
            retrieveBooks(eq("rating", searchRating));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return result;
    }

    @Override
    public Book addBook(Book book, ArrayList<String> authors, ArrayList<Author> existingAuthorsList) throws BooksDbException {
        ClientSession clientSession = mongoClient.startSession();
        try {
            clientSession.startTransaction();
            List<Document> authorsTotal = new ArrayList<>();
            // Step 1: Create Book
            String[] tempDate = book.getPublished().split("-");
            Calendar calendar = new GregorianCalendar(Integer.parseInt(tempDate[0]), Integer.parseInt(tempDate[1]), Integer.parseInt(tempDate[2]));
            Date date = calendar.getTime();
            Document document = new Document("_id", book.getIsbn())
                    .append("title", book.getTitle())
                    .append("datePublished", date)
                    .append("genre", book.getGenre())
                    .append("rating", book.getRating());
            MongoCollection<Document> collection = mongoDatabase.getCollection("Book");
            collection.insertOne(document);
            MongoCollection<Document> authorCollection = mongoDatabase.getCollection("Author");


            // Step 2: Create new Authors
            for (String s : authors) {
                Document tempAuthor = new Document("fullName", s);
                authorsTotal.add(tempAuthor);
                authorCollection.insertOne(tempAuthor);
            }

            // Step 3: Add existing authors
            for (Author a : existingAuthorsList) {
                authorsTotal.add(new Document("_id", new ObjectId(a.getAuthorId())).append("fullName", a.getFullName()));
            }
            for (Document d : authorsTotal) {
                System.out.println("test " + d.get("_id") + d.get("fullName"));
            }

            Bson filter = Filters.eq("_id", book.getIsbn());
            Bson update = Updates.set("authors", authorsTotal);
            collection.updateOne(filter, update);
            clientSession.commitTransaction();


        } catch (MongoException me) {
            clientSession.abortTransaction();
            throw new BooksDbException(me.getMessage(), me);
        } finally {
            clientSession.close();
        }

        return book;
    }

    @Override
    public Book deleteBook(Book book) throws BooksDbException {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection("Book");
            collection.findOneAndDelete(eq("_id", book.getIsbn()));
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return book;
    }

    @Override
    public ArrayList<Author> retrieveAllAuthors() throws BooksDbException {
        ArrayList<Author> allAuthors = new ArrayList<>();
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection("Author");
            ArrayList<Document> documents = collection.find().into(new ArrayList<>());
            for (Document d : documents) {
                allAuthors.add(new Author(d.get("_id").toString(), d.getString("fullName")));
                System.out.println(d.get("_id").toString() + d.getString("fullName"));
            }
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return (ArrayList<Author>) allAuthors.clone();
    }

    @Override
    public Book updateBook(Book book) throws BooksDbException {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection("Book");
            Bson filter = Filters.eq("_id", book.getIsbn());
            Bson update = Updates.set("rating", book.getRating());
            collection.updateOne(filter, update);
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
        return book;
    }

    @Override
    public void login(String user, String pwd, String database) throws BooksDbException {
        String uri;
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection("Users");
            FindIterable<Document> findIterable = collection.find(eq("_id", user));
            Document document = findIterable.first();
            if (document == null) throw new BooksDbException("User does not exist");
            String checkPwd = document.getString("pwd");
            if (!checkPwd.equals(pwd)) throw new BooksDbException("Wrong password!");
            if (document.getString("privileges").equals("admin")) {
                uri = "mongodb://admin:admin123!@localhost:27017/Library";
            } else uri = "mongodb://UserKTH:mypassword@localhost:27017/Library";
            mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(database);
            activeUser = user;
            System.out.println("Logged in as: " + user);

        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        }
    }

    @Override
    public Review addReview(Review review) throws BooksDbException {
        ClientSession clientSession = mongoClient.startSession();

        try {
            clientSession.startTransaction();
            MongoCollection<Document> collection = mongoDatabase.getCollection("Book");
            MongoCollection<Document> reviewCollection = mongoDatabase.getCollection("Review");

            // Insert new review in review collection
            Document reviewToAdd = new Document("user", review.getUser())
                    .append("review", review.getText())
                    .append("isbn", review.getIsbn());

            reviewCollection.insertOne(reviewToAdd);

            // copy review and user to book
            Document copyToBook = new Document("user", review.getUser())
                    .append("review", review.getText());
            Document update = new Document("$push", new Document("reviews", copyToBook));
            Document filter = new Document("_id", review.getIsbn());
            collection.updateOne(filter, update);
            clientSession.commitTransaction();
        } catch (MongoException me) {
            throw new BooksDbException(me.getMessage(), me);
        } finally {
        clientSession.close();
    }

        return review;
    }

    @Override
    public String retrieveCurrentUser() {
        return activeUser;
    }


    private void retrieveBooks(Bson filter) throws MongoException {
        result.clear();
        MongoCollection<Document> collection = mongoDatabase.getCollection("Book");
        FindIterable findIterable = collection.find(filter);

        try (MongoCursor<Document> cursor = findIterable.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Book tempBook;
                result.add(tempBook = new Book(document.getString("_id"),
                        document.getString("title"),
                        document.getDate("datePublished").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE),
                        document.getString("genre"),
                        document.getInteger("rating")));
                List<Document> authors = (List<Document>) (document.get("authors"));
                if (authors != null) {
                    for (Document author : authors) {
                        tempBook.addAuthor(author.getString("fullName"));
                    }
                }
            }
        }
    }
}

