package com.adinaandsari.virtualbookstore;

/**
 * Created by adina_000 on 23-Dec-15.
 */
public class BookItemForList {
    String name;
    long id;
    String author;

    public BookItemForList(String name, long id, String author) {
        this.name = name;
        this.id = id;
        this.author = author;
    }

    public BookItemForList() {
    }

    @Override
    public String toString() {
        return id + '\t' +name+'\t'+ author ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
