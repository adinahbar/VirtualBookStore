package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

public class BookOfTheStore extends AppCompatActivity {

    //this customer
    Customer user;

    Backend backendFactory = BackendFactory.getInstance();
    private Book bookOfTheStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_of_the_store);

        //thr user
        Intent preIntent = getIntent();
        user = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //the book of the store
        TextView bookOfTheStoreText = (TextView) findViewById(R.id.book_of_the_store_textView);
        try {
            bookOfTheStore = backendFactory.bookOfTheStore();
            String detail = "The book is:\nName: ";
            detail += bookOfTheStore.getBookName()+"\nAuthor: "+bookOfTheStore.getAuthor() +
                    "\nSummary: "+bookOfTheStore.getSummary()+"\nRate: "+
                    String.valueOf(bookOfTheStore.getRateAVR());
            bookOfTheStoreText.setText(detail);
        } catch (Exception e) {
            bookOfTheStoreText.setText("There are no best seller book to watch");
        }
        Button shopBookOfTheStoreButton = (Button)findViewById(R.id.shop_book_of_the_store_button_customer);
        shopBookOfTheStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookOfTheStore != null) {
                    //go to the order activity for this book and customer
                    Intent intent = new Intent(BookOfTheStore.this, BookPage.class);
                    intent.putExtra(ConstValue.BOOK_KEY, bookOfTheStore);//add the book
                    intent.putExtra(ConstValue.CUSTOMER_KEY, user);//add the customer
                    startActivity(intent);
                }
                Toast.makeText(BookOfTheStore.this, "There are no book of the store for now", Toast.LENGTH_LONG).show();
            }
        });
    }

}
