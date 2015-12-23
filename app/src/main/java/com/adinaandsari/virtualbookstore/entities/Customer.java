package com.adinaandsari.virtualbookstore.entities;
import java.io.Serializable;
import java.util.Date;
/**
 * Created by adina_000 on 05-Nov-15.
 */
public class Customer extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * attributes
     */
    private Date birthDay;
    private String numOfCreditCard;
    private Status status;
    private boolean VIP;

    /**
     constructors
     */
    public Customer() {
        super();
        this.birthDay = new Date();
        this.numOfCreditCard = "";
        this.status = Status.SINGLE; //default
        VIP = false;
    }
    public Customer(Customer customer)
    {
        super (customer);
        setBirthDay(customer.getBirthDay());
        setNumOfCreditCard(customer.getNumOfCreditCard());
        setStatus(customer.getStatus());
        setVIP(customer.isVIP());
    }
    public Customer(long numID, String name, String address, String phoneNumber, String emailAddress,
                    Gender gender, Date birthDay, String numOfCreditCard, Status status) {
        super(numID, name, address, phoneNumber, emailAddress, gender,Privilege.CUSTOMER);
        this.birthDay = birthDay;
        this.numOfCreditCard = numOfCreditCard;
        this.status = status;
        this.VIP = false;
    }

    /**
     * getter and setter
     * @return
     */
    public Date getBirthDay() {
        return birthDay;
    }
    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
    public String getNumOfCreditCard() {
        return numOfCreditCard;
    }
    public void setNumOfCreditCard(String numOfCreditCard) {
        this.numOfCreditCard = numOfCreditCard;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public boolean isVIP() {
        return VIP;
    }
    public void setVIP(boolean VIP) {
        this.VIP = VIP;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Customer customer = (Customer)obj;
        return (super.equals(obj)&& birthDay==customer.getBirthDay() && numOfCreditCard==customer.getNumOfCreditCard() &&
                status==customer.getStatus() && VIP==customer.isVIP());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "birthDay=" + birthDay +
                ", numOfCreditCard='" + numOfCreditCard + '\'' +
                ", status=" + status +
                ", VIP=" + VIP +
                "}\n";
    }
}
