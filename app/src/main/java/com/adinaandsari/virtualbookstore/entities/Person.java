package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;

/**
 * Created by adina_000 on 05-Nov-15.
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * attributes
     */
    protected long numID;
    protected String name;
    protected String address;
    protected Gender gender;
    protected String phoneNumber;
    protected String emailAddress;
    protected Privilege privilege;

    /**
     * constructors
     */
    public Person() {
        this.numID = 0;
        this.name = "";
        this.address = "";
        this.phoneNumber = "";
        this.emailAddress = "";
        this.gender = Gender.MALE; //default
        this.privilege = Privilege.CUSTOMER; //default
    }
    public Person(Person person)
    {
        setNumID(person.getNumID());
        setName(person.getName());
        setAddress(person.getAddress());
        setGender(person.getGender());
        setPhoneNumber(person.getPhoneNumber());
        setEmailAddress(person.getEmailAddress());
        setPrivilege(person.getPrivilege());
    }
    public Person(long numID, String name, String address, String phoneNumber, String emailAddress, Gender gender, Privilege privilege) {
        this.numID = numID;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.gender = gender;
        this.privilege = privilege;
    }

    /**
     * getter and setter
     */
    public long getNumID() {
        return numID;
    }
    public void setNumID(long numID) {
        this.numID = numID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public Privilege getPrivilege() {
        return privilege;
    }
    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Person person = (Person)obj;
        return (numID==person.getNumID() && name==person.getName() && address==person.getAddress() && gender==person.getGender() &&
                phoneNumber==person.getPhoneNumber() && emailAddress==person.getEmailAddress() && privilege==person.getPrivilege());
    }

    @Override
    public String toString() {
        return  " numID=" + numID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\''+
                ", privilege='" + privilege + '\'';
    }
}
