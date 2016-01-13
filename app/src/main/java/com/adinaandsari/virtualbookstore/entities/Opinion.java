package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;

/**
 * Created by A&S on 05/11/2015.
 */
public class Opinion implements Serializable {
    /**
     * attributes
     */
    private String yourOpinion;
    private int rate;
    private int bookID;
    private int opinionID;
    private static int opinionIDCounter = 0;

    /**
     * constructors
     */
    public Opinion() {
        this.rate = 0;
        this.yourOpinion = "";
        this.bookID = -1;
        this.opinionID = opinionIDCounter++;
    }
    public Opinion(Opinion opinion){
        setYourOpinion(opinion.getYourOpinion());
        setRate(opinion.getRate());
        setBookID(opinion.getBookID());
        setOpinionID(opinion.getOpinionID());
    }
    public Opinion(int rate, String yourOpinion,int bookID ) {
        this.rate = rate;
        this.yourOpinion = yourOpinion;
        this.bookID = bookID;
        this.opinionID = opinionIDCounter++;
    }

    /**
     * getters and setters
     */
    public int getRate() {
        return rate;
    }
    public void setRate(int rate) {
        this.rate = rate;
    }
    public String getYourOpinion() {
        return yourOpinion;
    }
    public void setYourOpinion(String yourOpinion) {
        this.yourOpinion = yourOpinion;
    }
    public int getBookID() {
        return bookID;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }
    public int getOpinionID() {
        return opinionID;
    }
    public void setOpinionID(int opinionID) {
        this.opinionID = opinionID;
    }

    public static void setOpinionIDCounter(int opinionIDCounter) {
        Opinion.opinionIDCounter = opinionIDCounter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Opinion opinion = (Opinion)obj;
        return (yourOpinion==opinion.getYourOpinion() && rate==opinion.getRate()&& bookID==opinion.getBookID()
                && opinionID==opinion.getOpinionID());
    }

    @Override
    public String toString() {
        return "Opinion{" +
                "opinionID=" + opinionID +
                ", bookID=" + bookID +
                ", yourOpinion='" + yourOpinion + '\'' +
                ", rate=" + rate +
                "}\n";
    }
}
