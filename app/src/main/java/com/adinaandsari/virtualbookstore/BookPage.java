package com.adinaandsari.virtualbookstore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class BookPage extends AppCompatActivity {

    TextView textViewOfName,textViewOfAuthor,textViewOfPublisher,textViewOfSummary,
            textViewOfDatePublished,textViewOfID,
            textViewOfCategory, textViewOFLanguage, textViewOFRate;
    private Spinner suppliersSpinner , opinionsSpinner;
    private Button orderButton , addOpinionButton ;

    //this user
    Intent preIntent = getIntent();
    Book book = (Book) preIntent.getSerializableExtra(ConstValue.BOOK_KEY);
    Customer customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

    //function to find view by id for the view in the activity
    void findView()
    {
        textViewOfName = (TextView)findViewById(R.id.the_book_name_text_view_book_page);
        textViewOfAuthor = (TextView)findViewById(R.id.the_author_text_view_book_page);
        textViewOfPublisher = (TextView)findViewById(R.id.the_publisher_book_text_view_book_page);
        textViewOfSummary = (TextView)findViewById(R.id.the_summary_text_view_book_page);
        textViewOfDatePublished = (TextView)findViewById(R.id.the_published_date_text_view_book_page);
        textViewOfID = (TextView)findViewById(R.id.the_id_book_text_view_book_page);
        textViewOfCategory = (TextView)findViewById(R.id.the_category_text_view_book_page);
        textViewOFLanguage = (TextView)findViewById(R.id.the_language_text_view_book_page);
        textViewOFRate = (TextView)findViewById(R.id.the_rate_text_view_book_page);
        suppliersSpinner = (Spinner)findViewById(R.id.suppliers_spinner_book_page);
        opinionsSpinner = (Spinner)findViewById(R.id.opinions_spinner_book_page);
        orderButton = (Button)findViewById(R.id.order_book_button_book_page);
        addOpinionButton = (Button)findViewById(R.id.add_opinion_button_book_page);
    }

    //function to show to book's detail
    void showDetail()throws Exception
    {
        textViewOfName.setText(book.getBookName());
        textViewOfAuthor.setText(book.getAuthor());
        textViewOfPublisher.setText(book.getPublisher());
        textViewOFRate.setText(String.valueOf(book.getRateAVR()));
        textViewOfSummary.setText(book.getSummary());
        textViewOfDatePublished.setText(book.getDatePublished().toString());
        textViewOfID.setText(book.getBookID());
        textViewOfCategory.setText(book.getBooksCategory().toString());
        textViewOFLanguage.setText(book.getLanguage().toString());
        ArrayList<Supplier> suppliers = BackendFactory.getInstance().supplierListByBook(book.getBookID());
        /*
        String[] names = new String[suppliers.size()];
        int i=0;
        for (Supplier s:suppliers)
        {
            names[i] = s.getName();
            i++;
        }
        suppliersSpinner;
        opinionsSpinner;
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //find all the viewers and show the detail
        findView();
        try {
            showDetail();
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(BookPage.this, "Failed to show the book's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addOpinionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

}
