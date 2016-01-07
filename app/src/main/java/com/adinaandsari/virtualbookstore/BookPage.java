package com.adinaandsari.virtualbookstore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.entities.Order;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierAndBook;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookPage extends AppCompatActivity {

    TextView textViewOfName,textViewOfAuthor,textViewOfPublisher,textViewOfSummary,
            textViewOfDatePublished,textViewOfID,textViewOfNumOfPages,
            textViewOfCategory, textViewOFLanguage, textViewOFRate;
    EditText editTextNumOfPages;
    private Spinner suppliersSpinner ;
    private ImageButton addToCartButton ;
    private Button addOpinionButton,viewOpinionsButton;
    private List<BookItemForList> supplierItemForSpinner = new ArrayList<>();
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
        addOpinionButton = (Button)findViewById(R.id.add_opinion_book_page);
        viewOpinionsButton = (Button)findViewById(R.id.view_opinion_book_page);
    }

    //function to show to book's detail
    void showDetail()throws Exception
    {
        textViewOfName.setText(book.getBookName());
        textViewOfAuthor.setText(book.getAuthor());
        textViewOfPublisher.setText(book.getPublisher());
        textViewOFRate.setText(String.valueOf(book.getRateAVR()));
        textViewOfSummary.setText(book.getSummary());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        textViewOfDatePublished.setText(df.format(book.getDatePublished()));
        textViewOfID.setText(String.valueOf(book.getBookID()));
        textViewOfCategory.setText(book.getBooksCategory().toString());
        textViewOFLanguage.setText(book.getLanguage().toString());
        ArrayList<SupplierAndBook> supplierAndBooks = BackendFactory.getInstance().supplierListByBook(book.getBookID());
        for(SupplierAndBook s : supplierAndBooks)
        {
            Supplier supplier=BackendFactory.getInstance().findSupplierByID(s.getSupplierID());
            supplierItemForSpinner.add(new BookItemForList("\t"+supplier.getName()+"\t",
                    s.getSupplierID(),"\t"+String.valueOf(s.getPrice())+" NIS"));
        }
        ArrayAdapter<BookItemForList> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,supplierItemForSpinner);
        suppliersSpinner.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        //this user
        Intent preIntent = getIntent();
        book = (Book) preIntent.getSerializableExtra(ConstValue.BOOK_KEY);
        customer = (Customer) preIntent.getSerializableExtra(ConstValue.CUSTOMER_KEY);

        //find all the viewers and show the detail
        findView();
        try {
            showDetail();
        }catch (Exception e)
        {
            //print the exception in a toast view
            Toast.makeText(BookPage.this, "Failed to show the book's detail:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        suppliersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
               selectedFromSpinner = (BookItemForList)adapterView.getItemAtPosition(position);
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
       }
        );

        //view opinions list
        viewOpinionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookPage.this, OpinionList.class);//going to previous activity with this supplier details
                intent.putExtra(ConstValue.CUSTOMER_KEY, customer);
                intent.putExtra(ConstValue.BOOK_KEY, book);
                startActivity(intent);
            }
        });

        //add an opinion to the book
        addOpinionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(BookPage.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_opinion_dialog);
                dialog.setTitle("Enter your opinion on this book");
                TextView bookDetail = (TextView)dialog.findViewById(R.id.book_id_of_the_opinion_opinion_dialog);
                final EditText theOpinion = (EditText)dialog.findViewById(R.id.opinion_editText_opinion_dialog);
                final RatingBar rate = (RatingBar) dialog.findViewById(R.id.ratingBar_of_the_opinion_opinion_dialog);
                String text = String.valueOf(book.getBookID());
                text += "\t" + book.getBookName();
                bookDetail.setText(text);
                rate.setRating(3);

                Button addOpinionDialog = (Button)dialog.findViewById(R.id.add_opinion_button_opinion_dialog);
                addOpinionDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int rateForOpinion = (int)Math.round(rate.getRating());
                            String opinionText = theOpinion.getText().toString();
                            Opinion newOpinion = new Opinion(rateForOpinion, opinionText, book.getBookID());
                            Backend backend = BackendFactory.getInstance();
                            backend.addOpinion(newOpinion,customer.getPrivilege());
                            textViewOFRate.setText(String.valueOf(backend.findBookByID(book.getBookID()).getRateAVR()));
                            dialog.dismiss();
                        }catch (Exception e)
                        {
                            //print the exception in a toast view
                            Toast.makeText(BookPage.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        //add to cart button
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values
                    ArrayList<Supplier> suppliers = BackendFactory.getInstance().getSupplierList();
                    for (Supplier s : suppliers) {
                        if (s.getNumID()==selectedFromSpinner.getId())
                        {
                            selectedSupplier=s;
                        }
                    }
                    if (!editTextNumOfPages.getText().toString().equals("")) //add more copies
                    {
                        numOfPages=Integer.parseInt(editTextNumOfPages.getText().toString());
                        if (numOfPages < 0)
                            throw new Exception("ERROR: num of copies must be a positive number");
                    }
                    else{
                        throw new Exception("ERROR: you does not enter the number of copies that you want");
                    }
                    //add the order
                    Order currentOrder = new Order(book.getBookID(), selectedFromSpinner.getId(), customer.getNumID(), numOfPages);
                    BackendFactory.getInstance().addOrder(currentOrder, Privilege.MANAGER);
                    Toast.makeText(BookPage.this, "We add it to your cart!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BookPage.this, CustomerActivity.class);//going to previous activity with this supplier details
                    intent.putExtra(ConstValue.CUSTOMER_KEY, customer);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    //print the exception in a toast view
                    Toast.makeText(BookPage.this, "Failed to show the book's detail:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
            }

            );

        }

    }
