package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;

/**
 * Created by A&S on 05/11/2015.
 */
public class SupplierAndBook implements Serializable {
    /**
     * attributes
     */
    private long bookID;
    private long supplierID;
    private int numOfCopies;
    private double price;

    /**
     * constructors
     */
    public SupplierAndBook() {
        this.bookID = -1;
        this.numOfCopies = 0;
        this.supplierID = -1;
        this.price = 0;
    }
    public SupplierAndBook(SupplierAndBook supplierAndBook){
        setBookID(supplierAndBook.getBookID());
        setSupplierID(supplierAndBook.getSupplierID());
        setNumOfCopies(supplierAndBook.getNumOfCopies());
        setPrice(supplierAndBook.getPrice());
    }
    public SupplierAndBook(long bookID, int numOfCopies, long supplierID ,double price) {
        this.bookID = bookID;
        this.numOfCopies = numOfCopies;
        this.supplierID = supplierID;
        this.price = price;
    }

    /**
     * get and set functions
     */

    public long getBookID() {
        return bookID;
    }
    public void setBookID(long bookID) {
        this.bookID = bookID;
    }
    public long getSupplierID() {
        return supplierID;
    }
    public void setSupplierID(long supplierID) {
        this.supplierID = supplierID;
    }
    public int getNumOfCopies() {
        return numOfCopies;
    }
    public void setNumOfCopies(int numOfCopies) {
        this.numOfCopies = numOfCopies;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        SupplierAndBook supplierAndBook = (SupplierAndBook)obj;
        return (supplierID==supplierAndBook.getSupplierID() && bookID==supplierAndBook.getBookID() &&
        numOfCopies==supplierAndBook.getNumOfCopies()&& price==supplierAndBook.getPrice());
    }

    @Override
    public String toString() {
        return "SupplierAndBook{" +
                "bookID=" + bookID +
                ", supplierID=" + supplierID +
                ", numOfCopies=" + numOfCopies +
                ", price=" + price +
                "}\n";
    }
}
