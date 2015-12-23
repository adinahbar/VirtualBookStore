package com.adinaandsari.virtualbookstore.entities;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by adina_000 on 05-Nov-15.
 */
public class Supplier extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * attributes
     */
    private String customerServicePhoneNumber;
    private String reservationsPhoneNumber;
    private SupplierType type;

    /**
     * constructors
     */
    public Supplier() {
        super();
        this.customerServicePhoneNumber = "";
        this.reservationsPhoneNumber = "";
        this.type = SupplierType.SHOP; //default
    }
    public Supplier(Supplier supplier){
        super(supplier);
        setCustomerServicePhoneNumber(supplier.getCustomerServicePhoneNumber());
        setReservationsPhoneNumber(supplier.getReservationsPhoneNumber());
        setType(supplier.getType());
    }
    public Supplier(long numID, String name, String address, String phoneNumber, String emailAddress, Gender gender,
                    String customerServicePhoneNumber, String reservationsPhoneNumber, SupplierType type) {
        super(numID, name, address, phoneNumber, emailAddress, gender , Privilege.SUPPLIER);
        this.customerServicePhoneNumber = customerServicePhoneNumber;
        this.reservationsPhoneNumber = reservationsPhoneNumber;
        this.type = type;
    }

    /**
     *getter and setter
     */
    public String getCustomerServicePhoneNumber() {
        return customerServicePhoneNumber;
    }
    public void setCustomerServicePhoneNumber(String customerServicePhoneNumber) {
        this.customerServicePhoneNumber = customerServicePhoneNumber;
    }
    public String getReservationsPhoneNumber() {
        return reservationsPhoneNumber;
    }
    public void setReservationsPhoneNumber(String reservationsPhoneNumber) {
        this.reservationsPhoneNumber = reservationsPhoneNumber;
    }
    public SupplierType getType() {
        return type;
    }
    public void setType(SupplierType type) {
        this.type = type;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Supplier supplier = (Supplier)obj;
        return ( super.equals(obj) && customerServicePhoneNumber== supplier.getCustomerServicePhoneNumber() &&
                reservationsPhoneNumber== supplier.getReservationsPhoneNumber() &&
                emailAddress== supplier.getEmailAddress() && type== supplier.getType());
    }

    @Override
    public String toString() {
        return "Supplier{" + super.toString() +
                ", customerServicePhoneNumber='" + customerServicePhoneNumber + '\'' +
                ", reservationsPhoneNumber='" + reservationsPhoneNumber + '\'' +
                ", type=" + type +
                "}\n";
    }
}
