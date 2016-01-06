package com.adinaandsari.virtualbookstore;

/**
 * Created by adina_000 on 06-Jan-16.
 */
public class OpinionItemForList {
    String theOpinion;
    int rate;
    int opinionId;

    @Override
    public String toString()  {return opinionId + '\t' +rate+'\t'+ theOpinion; }

    public OpinionItemForList() {
    }

    public OpinionItemForList(String theOpinion, int rate, int opinionId) {
        this.theOpinion = theOpinion;
        this.rate = rate;
        this.opinionId = opinionId;
    }

    public String getTheOpinion() {
        return theOpinion;
    }

    public void setTheOpinion(String theOpinion) {
        this.theOpinion = theOpinion;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(int opinionId) {
        this.opinionId = opinionId;
    }
}
