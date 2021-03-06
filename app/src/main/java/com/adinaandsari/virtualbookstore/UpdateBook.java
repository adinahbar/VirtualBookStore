package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Language;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateBook extends AppCompatActivity {

    //this user
    Supplier user ;

    private Spinner categorySpinner , languageSpinner , idSpinner;
    private EditText editTextOfName,editTextOfAuthor,editTextOfPublisher,editTextOfSummary,editTextOfDatePublished
            ,editTextOfPrice,moreCopies;
    private TextView numOfCopies;
    private String name ,author , publisher ,summary;
    double price;
    private Language language;
    private Category category;
    private Date datePublished;
    private int bookID;
    private Button updateButton;
    private Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this user
        Intent preIntent = getIntent();
        user = (Supplier) preIntent.getSerializableExtra(ConstValue.SUPPLIER_KEY);

        //find all the viewers
        findView();

        //the category spinner
        String[] categoryList = getResources().getStringArray(R.array.category_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categoryList);
        categorySpinner.setAdapter(dataAdapter);

        //the language spinner
        String[] LanguageList = getResources().getStringArray(R.array.language_array);
        ArrayAdapter<String> languageSpinnerAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,LanguageList);
        languageSpinner.setAdapter(languageSpinnerAdapter);

        //try to load the data of the selected book
        try {
            //spinner for the id of the book
            String[] booksID = getBooksID(user.getNumID());
            ArrayAdapter<String> dataIDAdapter = new ArrayAdapter<>
                    (this,R.layout.support_simple_spinner_dropdown_item,booksID);
            idSpinner.setAdapter(dataIDAdapter);
            idSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    enterDetail();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(UpdateBook.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    name = editTextOfName.getText().toString();
                    author = editTextOfAuthor.getText().toString();
                    publisher = editTextOfPublisher.getText().toString();
                    summary = editTextOfSummary.getText().toString();
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
                    bookID = Integer.valueOf(idSpinner.getSelectedItem().toString());
                    if (!moreCopies.getText().toString().equals("")) //add more copies
                    {
                        int numOfMoreCopies = Integer.valueOf(moreCopies.getText().toString());
                        if (numOfMoreCopies < 0)
                            throw new Exception("ERROR: num of copies must be a positive number");
                        backendFactory.addMoreCopiesToBook(bookID,numOfMoreCopies,user.getNumID(),user.getPrivilege());
                    }

                    //try to update the book

                    Book bookToUpdate = new Book(author,name,category,datePublished,language,publisher,summary);
                    bookToUpdate.setBookID(bookID);

                    backendFactory.updateBook(bookToUpdate,user.getNumID(),user.getPrivilege());
                    backendFactory.setPriceOfBookForSupplier(bookToUpdate.getBookID(),user.getNumID(),price);
                    Toast.makeText(UpdateBook.this, "The update has been successful!", Toast.LENGTH_LONG).show();

                    //go to the supplier activity
                    Intent supplierIntent = new Intent(UpdateBook.this, SupplierActivity.class);
                    supplierIntent.putExtra(ConstValue.SUPPLIER_KEY, user);
                    startActivity(supplierIntent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(UpdateBook.this, "Failed to update the book:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //function to find view by id for the view in the activity
    void findView()
    {
        idSpinner = (Spinner)findViewById(R.id.id_spinner_update_book);
        categorySpinner = (Spinner)findViewById(R.id.category_spinner_update_book);
        languageSpinner = (Spinner)findViewById(R.id.language_spinner_update_book);
        editTextOfAuthor = (EditText)findViewById(R.id.author_edit_text_update_book);
        editTextOfPublisher = (EditText)findViewById(R.id.publisher_book_edit_text_update_book);
        editTextOfSummary = (EditText)findViewById(R.id.summary_edit_text_update_book);
        editTextOfDatePublished = (EditText)findViewById(R.id.published_date_edit_text_update_book);
        editTextOfName = (EditText)findViewById(R.id.book_name_edit_text_update_book);
        editTextOfPrice = (EditText)findViewById(R.id.book_price_edit_text_update_book);
        numOfCopies = (TextView)findViewById(R.id.num_of_copies_for_a_book_update_book);
        moreCopies = (EditText)findViewById(R.id.more_copies_edit_text_update_book);
        updateButton = (Button)findViewById(R.id.update_book_button);
    }

    //function to enter the values of the selected supplier by its ID
    void enterDetail()
    {
        try {
            Backend backendFactory = BackendFactory.getInstance();
            Book book = backendFactory.findBookByID(Long.parseLong(idSpinner.getSelectedItem().toString()));
            editTextOfName.setText(book.getBookName());
            editTextOfAuthor.setText(book.getAuthor());
            editTextOfPublisher.setText(book.getPublisher());
            editTextOfSummary.setText(book.getSummary());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            editTextOfDatePublished.setText(df.format(book.getDatePublished()));
            int copies = backendFactory.getNumOfBookCopiesForSupplier(book.getBookID(), user.getNumID());
            numOfCopies.setText(String.valueOf(copies));
            editTextOfPrice.setText(String.valueOf(backendFactory.getPriceOfBookForSupplier(book.getBookID(), user.getNumID())));
            String languageString = book.getLanguage().toString().toLowerCase(Locale.ENGLISH);
            languageSpinner.setSelection(((ArrayAdapter<String>) languageSpinner.getAdapter()).getPosition(languageString));
            String categoryString = book.getBooksCategory().toString().toLowerCase(Locale.ENGLISH);
            categorySpinner.setSelection(((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(categoryString));
        }
        catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(UpdateBook.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //function to return all the books ids
    String[] getBooksID (long supplierID)throws Exception
    {
        ArrayList<Book> bookArrayList = BackendFactory.getInstance().bookListBySupplier(supplierID);
        String[] id = new String[bookArrayList.size()] ;
        int i = 0;
        for (Book b : bookArrayList)
        {
            id[i] =String.valueOf(b.getBookID());
            i++;
        }
        return id;
    }

}
