package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class RemoveBook extends AppCompatActivity {

    //this supplier is entering the activity
    Intent preIntent = getIntent();
    Supplier user = (Supplier) preIntent.getSerializableExtra(ConstValue.SUPPLIER_KEY);
    private Spinner removeBookIdSpinner;
    private Long idToRemove;
    private Button removeBookButton;
    void showDetail()throws Exception
    {
        ArrayList<Book> books = BackendFactory.getInstance().getBookList();
        String[] ids = new String[books.size()];
        int i=0;
        for (Book b:books)
        {
            ids[i] = String.valueOf(b.getBookID());
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,ids);
        removeBookIdSpinner.setAdapter(dataAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        removeBookIdSpinner = (Spinner)findViewById(R.id.select_supplier_to_remove__by_ID_spinner);
        removeBookButton = (Button)findViewById(R.id.remove_book_button);
        try {
            showDetail();
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(RemoveBook.this, "Failed to show the book's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        removeBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    idToRemove = Long.valueOf(removeBookIdSpinner.getSelectedItem().toString());//getting selected id
                    BackendFactory.getInstance().removeBook(idToRemove, user.getNumID(), user.getPrivilege());//removing selected book
                    Toast.makeText(RemoveBook.this, "the selected book was removed successfully:\n" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RemoveBook.this, SupplierActivity.class);//going to previous activity with this supplier details
                    intent.putExtra(ConstValue.SUPPLIER_KEY, user);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(RemoveBook.this, "Failed to remove book:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }


        });

    }

}
