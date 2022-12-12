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

    /** Gets the authors in a string separated by commas.
     *
     * @return Returns the authors in a string separated by commas.
     */
    public String getAuthors(){
        // DONT TAKE AWAY THIS METHOD! It is used by the PropertyValueFactory at bookPane
        String names = "";
        for(int i=0; i<authors.size(); i++){
            names += authors.get(i).getFullName();
            if((i+1)< authors.size()) names +=", ";
        }
        return names;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getPublished() { return published; }
    public Integer getRating() { return rating; }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

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
