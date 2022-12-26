package se.kth.jarwalli.booksdb.model;

import java.util.ArrayList;

public class Author {
    private String authorId;
    private String fullName;
    private String dateOfBirth;

    public Author(String authorId, String fullName, String dateOfBirth) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public Author(String authorId, String fullName){
        this(authorId, fullName, null);
    }
    public Author(String fullName){
        this(null, fullName, null);
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }



    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
}
