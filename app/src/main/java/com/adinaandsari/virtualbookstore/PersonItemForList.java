package com.adinaandsari.virtualbookstore;

/**
 * Created by adina_000 on 27-Dec-15.
 */
public class PersonItemForList {
    long personId;
    String name;
    String email;
    String phoneNumber;

    public PersonItemForList(long personId, String name, String email, String phoneNumber) {
        this.personId = personId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public PersonItemForList() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return personId + '\t' +name+'\t'+ phoneNumber +'\t' +email;
    }
}
