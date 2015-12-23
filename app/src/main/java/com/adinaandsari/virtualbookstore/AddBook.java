package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Language;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBook extends AppCompatActivity {

    //this user
    Intent preIntent = getIntent();
    Supplier user = (Supplier) preIntent.getSerializableExtra(ConstValue.SUPPLIER_KEY);

    private Spinner categorySpinner , languageSpinner;
    EditText editTextOfName,editTextOfAuthor,editTextOfPublisher,editTextOfSummary,
            editTextOfDatePublished,editTextOfNumCopies,editTextOfPrice;
    private String name ,author , publisher ,summary;
    int numOfCopies;
    double price;
    private Language language;
    private Category category;
    private Date datePublished;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        //find all the viewers
        findView();

        //the category spinner
        String[] categoryList = getResources().getStringArray(R.array.category_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,categoryList);
        categorySpinner.setAdapter(dataAdapter);

        //the category spinner
        String[] LanguageList = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> languageSpinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,LanguageList);
        languageSpinner.setAdapter(languageSpinnerAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             try {
                                                 //get the values and validation check of input
                                                 name = editTextOfName.getText().toString();
                                                 author = editTextOfAuthor.getText().toString();
                                                 publisher = editTextOfPublisher.getText().toString();
                                                 summary = editTextOfSummary.getText().toString();
                                                 numOfCopies =Integer.parseInt(editTextOfNumCopies.getText().toString());
                                                 if (numOfCopies <= 0)
                                                     throw new Exception("ERROR: num of copies must be a positive number");
                                                 price = Double.parseDouble(editTextOfPrice.getText().toString());
                                                 if (price <= 0)
                                                     throw new Exception("ERROR: price must be a positive number");
                                                 DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                                 datePublished = df.parse(editTextOfDatePublished.getText().toString());
                                                 Calendar now = Calendar.getInstance();
                                                 Calendar datePublishedDate = Calendar.getInstance();
                                                 datePublishedDate.setTime(datePublished);
                                                 if (datePublishedDate.before(now) != true) {
                                                     throw new Exception("ERROR: the published date isn't correct");
                                                 }
                                                 language = Language.valueOf(languageSpinner.getSelectedItem().toString().toUpperCase());
                                                 category = Category.valueOf(categorySpinner.getSelectedItem().toString().toUpperCase());

                                                 //try to add a new book
                                                 Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                                                 Book bookToAdd = new Book(author,name,category,datePublished,language,publisher,summary);
                                                 backendFactory.addBook(bookToAdd, user.getNumID(), user.getNumID(), user.getPrivilege(),
                                                         numOfCopies, price);
                                                 Toast.makeText(AddBook.this, "Add the book has been successful!\nThe ID of the book is: " +
                                                         String.valueOf(bookToAdd.getBookID()), Toast.LENGTH_LONG).show();

                                                 //go to the supplier activity
                                                 Intent supplierIntent = new Intent(AddBook.this, SupplierActivity.class);
                                                 supplierIntent.putExtra(ConstValue.SUPPLIER_KEY, user);
                                                 startActivity(supplierIntent);

                                             }
                                             catch (Exception e) {
                                                 //print the exception in a toast view
                                                 Toast.makeText(AddBook.this, "Failed to add the book:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                             }
                                         }
                                     }
        );

    }

    //function to find view by id for the view in the activity
    void findView()
    {
        categorySpinner = (Spinner)findViewById(R.id.category_spinner_add_book);
        languageSpinner = (Spinner)findViewById(R.id.language_spinner_add_book);
        editTextOfAuthor = (EditText)findViewById(R.id.author_edit_text_add_book);
        editTextOfPublisher = (EditText)findViewById(R.id.publisher_book_edit_text_add_book);
        editTextOfSummary = (EditText)findViewById(R.id.summary_edit_text_add_book);
        editTextOfDatePublished = (EditText)findViewById(R.id.published_date_edit_text_add_book);
        editTextOfName = (EditText)findViewById(R.id.book_name_edit_text_add_book);
        editTextOfNumCopies = (EditText)findViewById(R.id.numOfCopies_edit_text_add_book);
        editTextOfPrice = (EditText)findViewById(R.id.book_price_edit_text_add_book);
        addButton = (Button)findViewById(R.id.add_book_button);
    }
}
