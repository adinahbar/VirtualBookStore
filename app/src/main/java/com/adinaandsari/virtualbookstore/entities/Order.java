package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;

/**
 * Created by A&S on 05/11/2015.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * attributes
     */
    private long bookID;
    private long supplierID;
    private long orderID;
    private long customerNumID;
    private int numOfCopies;
    private double totalPrice;
    private boolean paid;
    private static int orderIDCounter = 0;

    /**
     * constructors
     */
    public Order() {
        this.bookID = -1;
        this.supplierID = -1;
        this.customerNumID = -1;
        this.orderID = orderIDCounter++;
        this.numOfCopies = 0;
        totalPrice =0;
        paid = false;
    }
    public Order(Order order){
        setBookID(order.getBookID());
        setSupplierID(order.getOrderID());
        setOrderID(order.getOrderID());
        setCustomerNumID(order.getCustomerNumID());
        setNumOfCopies(order.getNumOfCopies());
        setTotalPrice(order.getTotalPrice());
        setPaid(order.isPaid());
    }
    public Order(long bookID, long supplierID,long customerNumID, int numOfCopies) {
        this.bookID = bookID;
        this.orderID = orderIDCounter++;
        this.supplierID = supplierID;
        this.customerNumID = customerNumID;
        this.numOfCopies = numOfCopies;
        this.totalPrice = 0;
        paid=false;
    }

    /**
     * getter and setter
     */
    public long getOrderID() {
        return orderID;
    }
    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }
    public long getCustomerNumID() {
        return customerNumID;
    }
    public void setCustomerNumID(long customerNumID) {
        this.customerNumID = customerNumID;
    }
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
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public boolean isPaid() {
        return paid;
    }
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public static void setOrderIDCounter(int orderIDCounter) {
        Order.orderIDCounter = orderIDCounter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Order order = (Order)obj;
        return (bookID==order.getBookID() && supplierID==order.getSupplierID() && orderID==order.getOrderID() &&
                customerNumID==order.getCustomerNumID() && numOfCopies==order.getNumOfCopies() &&
                totalPrice== order.getTotalPrice() && paid == order.isPaid());
    }

    @Override
    public String toString() {
        return "Order{" +
                "bookID=" + bookID +
                ", supplierID=" + supplierID +
                ", orderID=" + orderID +
                ", customerNumID=" + customerNumID +
                ", numOfCopies=" + numOfCopies +
                ", totalPrice=" + totalPrice +
                ", paid=" + paid +
                "}\n";
    }
}
