package se.kth.jarwalli.booksdb.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {
    
    private int bookId;
    private String isbn; // should check format
    private String title;
    private Date published;
    private ArrayList<Author> authors;
    private String storyLine = "";
    private String genre;
    private int rating;
    // TODO: 
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(String isbn, String title, Date published, String genre, int rating, String fullName) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating = rating;
        authors = new ArrayList<>();
        authors.add(new Author(fullName));
    }

    public Book(int bookId, String isbn, String title, Date published, String genre, int rating) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating = rating;
        authors = new ArrayList<>();
    }
    
    /*public Book(String isbn, String title, Date published) {
        this(-1, isbn, title, published); 
    }*/




    
    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    public String getStoryLine() { return storyLine; }
    
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }
    
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
