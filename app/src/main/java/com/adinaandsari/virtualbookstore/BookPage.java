package com.adinaandsari.virtualbookstore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Order;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierAndBook;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;
import java.util.List;

public class BookPage extends AppCompatActivity {

    TextView textViewOfName,textViewOfAuthor,textViewOfPublisher,textViewOfSummary,
            textViewOfDatePublished,textViewOfID,textViewOfNumOfPages,
            textViewOfCategory, textViewOFLanguage, textViewOFRate;
    EditText editTextNumOfPages;
    private Spinner suppliersSpinner , opinionsSpinner;
    private ImageButton addToCartButton ;
    private List<BookItemForList> supplierItemForSpinner;
    private BookItemForList selectedFromSpinner;
    private Supplier selectedSupplier;
    private int numOfPages;
    //this users
    Book book ;
    Customer customer ;

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
        textViewOfNumOfPages=(TextView)findViewById(R.id.num_of_copies_text_view_book_page);
        textViewOFRate = (TextView)findViewById(R.id.the_rate_text_view_book_page);
        suppliersSpinner = (Spinner)findViewById(R.id.suppliers_spinner_book_page);
        addToCartButton = (ImageButton)findViewById(R.id.add_to_cart_button);
        editTextNumOfPages=(EditText)findViewById(R.id.num_of_copies_edit_text_book_page);

    }

    //function to show to book's detail
    void showDetail()throws Exception
    {
        numOfPages=Integer.parseInt(editTextNumOfPages.getText().toString());
        textViewOfName.setText(book.getBookName());
        textViewOfAuthor.setText(book.getAuthor());
        textViewOfPublisher.setText(book.getPublisher());
        textViewOFRate.setText(String.valueOf(book.getRateAVR()));
        textViewOfSummary.setText(book.getSummary());
        textViewOfDatePublished.setText(book.getDatePublished().toString());
        textViewOfID.setText(book.getBookID());
        textViewOfCategory.setText(book.getBooksCategory().toString());
        textViewOFLanguage.setText(book.getLanguage().toString());
        ArrayList<SupplierAndBook> supplierAndBooks = BackendFactory.getInstance().supplierListByBook(book.getBookID());
        for(SupplierAndBook s : supplierAndBooks)
        {
            Supplier supplier=BackendFactory.getInstance().findSupplierByID(s.getSupplierID());
            supplierItemForSpinner.add(new BookItemForList(supplier.getName(),s.getSupplierID(),String.valueOf(s.getPrice())));
        }
        ArrayAdapter<BookItemForList> adapter = new ArrayAdapter<BookItemForList>(this, android.R.layout.simple_spinner_item,supplierItemForSpinner);
        suppliersSpinner.setAdapter(adapter);
        suppliersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
            selectedFromSpinner = (BookItemForList)suppliersSpinner.getSelectedItem();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
        }
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this user
        Intent preIntent = getIntent();
        book = (Book) preIntent.getSerializableExtra(ConstValue.BOOK_KEY);
        customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //find all the viewers and show the detail
        findView();
        try {
            showDetail();
            for (Supplier s : BackendFactory.getInstance().getSupplierList()) {
                if (s.getNumID()==selectedFromSpinner.getId())
                {
                    selectedSupplier=s;
                }
            }
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(BookPage.this, "Failed to show the book's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Order currentOrder = new Order(book.getBookID(), selectedSupplier.getNumID(), customer.getNumID(), numOfPages);
                    BackendFactory.getInstance().addOrder(currentOrder, Privilege.MANAGER);
                    Intent intent = new Intent(BookPage.this, CustomerActivity.class);//going to previous activity with this supplier details
                    intent.putExtra(ConstValue.CUSTOMER_KEY, customer);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(BookPage.this, "Failed to show the book's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }

            }
            }

            );

        }

    }
