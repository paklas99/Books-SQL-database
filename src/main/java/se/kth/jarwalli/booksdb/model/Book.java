package se.kth.jarwalli.booksdb.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {
    private String isbn;
    private String title;
    private String published;
    private ArrayList<Author> authors;
    private String genre;
    private Integer rating;
    
    public Book(String isbn, String title, String published, String genre, int rating) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating = rating;
        authors = new ArrayList<>();
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getPublished() { return published; }
    public int getRating() { return rating; }

    public String getGenre(){
        return genre;
    }

    /**
     * Adds a new author to the book
     * @param fullName The name of the author
     */
    public void addAuthor(String fullName){
        authors.add(new Author(fullName));
    }
    
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
