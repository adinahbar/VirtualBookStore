package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Language;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.entities.Order;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Status;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierType;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
            Manager manager = new Manager(123456789, "manager", "aa", "00", "@", Gender.MALE,5);
            backendFactory.addManger(manager, Privilege.MANAGER);
            Customer customerToAdd = new Customer(987654321,"customer", "aa", "00", "@", Gender.MALE,
                    new Date(1995,7,8), "", Status.MARRIED);
            backendFactory.addCustomer(customerToAdd, Privilege.MANAGER);
            Supplier supplierToAdd = new Supplier(456789123,"supplier", "aa", "00", "@", Gender.MALE,
                    "555", "55", SupplierType.WRITER);
            backendFactory.addSupplier(supplierToAdd, Privilege.MANAGER);
            Book bookToAdd = new Book("ab","aa", Category.ARTS,new Date(1995,7,8), Language.FRENCH,"aa","very nice");
            Book bookToAdd2 = new Book("ab","aa", Category.ARTS,new Date(1995,7,8), Language.FRENCH,"aa","very nice");
            Book bookToAdd3 = new Book("ab","aa", Category.ARTS,new Date(1995,7,8), Language.FRENCH,"aa","very nice");
            Opinion opinion =new Opinion(5,"yofi",bookToAdd.getBookID());
            backendFactory.addBook(bookToAdd, 456789123,456789123 , Privilege.SUPPLIER,
                    5, 99);
            backendFactory.addBook(bookToAdd2, 456789123,456789123 , Privilege.SUPPLIER,
                    5, 99);
            backendFactory.addBook(bookToAdd3, 456789123,456789123 , Privilege.SUPPLIER,
                    5, 99);
            Opinion opinion2 =new Opinion(5,"yofi2",bookToAdd2.getBookID());
            backendFactory.addOpinion(opinion,Privilege.CUSTOMER);
            backendFactory.addOpinion(opinion2,Privilege.CUSTOMER);
            Order order = new Order(bookToAdd.getBookID(),supplierToAdd.getNumID(),customerToAdd.getNumID(),1);
            order.setPaid(false);
            backendFactory.addOrder(order,Privilege.MANAGER);
        }
        catch (Exception e)
        {

        }

        //log in button
        Button logButton = (Button)findViewById(R.id.logInButton_main);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });

        //sign in button
        Button signButton = (Button)findViewById(R.id.singInButton_main);
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCustomerSingIn.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
