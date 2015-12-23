package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;

/**
 * Created by adina_000 on 05-Nov-15.
 */
public class Manager extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * attributes
     */
    private int yearsInCompany;

    /**
     * constructors
     */
    public Manager() {
        super();
        this.yearsInCompany = 0;
    }
    public Manager(Manager manager) {
        super(manager);
        setYearsInCompany(manager.getYearsInCompany());
    }
    public Manager(long numID, String name, String address, String phoneNumber, String emailAddress,
                   Gender gender, int yearsInCompany) {
        super(numID, name, address, phoneNumber, emailAddress, gender, Privilege.MANAGER);
        this.yearsInCompany = yearsInCompany;
    }

    /**
     *getter and setter
     */
    public int getYearsInCompany() {
        return yearsInCompany;
    }
    public void setYearsInCompany(int yearsInCompany) {
        this.yearsInCompany = yearsInCompany;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Manager manager = (Manager)obj;
        return (super.equals(obj) && yearsInCompany==manager.getYearsInCompany());
    }

    @Override
    public String toString() {
        return "Manager{" + super.toString()+
                ", yearsInCompany=" + yearsInCompany +
                "}\n";
    }
}
