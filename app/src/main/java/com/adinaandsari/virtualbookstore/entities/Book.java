package com.adinaandsari.virtualbookstore.entities;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by A&S on 05/11/2015.
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * attributes
     */
    private int bookID;
    private String bookName;
    private String author;
    private String publisher;
    private Date datePublished;
    private Category booksCategory;
    private String summary;
    private Language language;
    private double rateAVR;
    private static int bookIDCounter = 0;

    /**
     * constructors.
     */
    public Book() {
        this.author = "";
        this.bookID = bookIDCounter++;
        this.bookName = "";
        this.datePublished = new Date();
        this.booksCategory = Category.ROMANCE;//default
        this.language = Language.ENGLISH;//default
        this.publisher = "";
        this.rateAVR = 0;
        this.summary = "";
    }
    public Book (Book oldBook) {
        setBookID(oldBook.getBookID());
        setBookName(oldBook.getBookName());
        setAuthor(oldBook.getAuthor());
        setPublisher(oldBook.getPublisher());
        setDatePublished(oldBook.getDatePublished());
        setBooksCategory(oldBook.getBooksCategory());
        setSummary(oldBook.getSummary());
        setLanguage(oldBook.getLanguage());
        setRateAVR(oldBook.getRateAVR());
    }
    public Book(String author, String bookName, Category booksCategory, Date datePublished, Language language,
                 String publisher, String summary) {
        this.author = author;
        this.bookID = bookIDCounter++;
        this.bookName = bookName;
        this.booksCategory = booksCategory;
        this.datePublished = datePublished;
        this.language = language;
        this.publisher = publisher;
        this.rateAVR = 0; //in the beginning the average rate is 0
        this.summary = summary;
    }

    /**
     * getters and setters
     */
    public int getBookID() {
        return bookID;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }
    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public Date getDatePublished() {
        return datePublished;
    }
    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    public Category getBooksCategory() {
        return booksCategory;
    }
    public void setBooksCategory(Category booksCategory) {
        this.booksCategory = booksCategory;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public double getRateAVR() {
        return rateAVR;
    }
    public void setRateAVR(double rate) {
        this.rateAVR = rate;
    }

    public static void setBookIDCounter(int bookIDCounter) {
        Book.bookIDCounter = bookIDCounter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Book book = (Book)obj;
        return (bookID==book.getBookID() && bookName==book.getBookName() && author==book.getAuthor() && publisher==book.getPublisher()&&
                datePublished==book.getDatePublished() && language==book.getLanguage() && booksCategory==book.getBooksCategory()
                && summary==book.getSummary() && rateAVR==book.getRateAVR());
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", bookID=" + bookID +
                ", bookName='" + bookName + '\'' +
                ", publisher='" + publisher + '\'' +
                ", datePublished=" + datePublished +
                ", booksCategory=" + booksCategory +
                ", summary='" + summary + '\'' +
                ", language=" + language +
                ", rateAVR=" + rateAVR +
                "}\n";
    }
}
