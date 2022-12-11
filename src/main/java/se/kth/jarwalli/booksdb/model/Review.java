package se.kth.jarwalli.booksdb.model;

public class Review {
    private String text;
    private String user;
    private String reviewDate;
    private int isbn;


    public Review(String text, String user, String reviewDate, int isbn) {
        this.text = text;
        this.user = user;
        this.reviewDate = reviewDate;
        this.isbn = isbn;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public int getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Review{" +
                "text='" + text + '\'' +
                ", user='" + user + '\'' +
                ", reviewDate='" + reviewDate + '\'' +
                ", isbn=" + isbn +
                '}';
    }
}
