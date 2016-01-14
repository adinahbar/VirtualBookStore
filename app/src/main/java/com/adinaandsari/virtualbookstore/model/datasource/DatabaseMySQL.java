package com.adinaandsari.virtualbookstore.model.datasource;

import android.os.AsyncTask;
import android.util.Log;

import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Language;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.entities.Order;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierAndBook;
import com.adinaandsari.virtualbookstore.entities.SupplierType;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by adina_000 on 10-Jan-16.
 */
public class DatabaseMySQL implements Backend{
    final static String web_url = "http://barchich.vlab.jct.ac.il/";
    ArrayList<Book> books = new ArrayList<>();
    ArrayList<Supplier> suppliers = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();
    ArrayList<Order> orders = new ArrayList<>();
    ArrayList<SupplierAndBook> supplierAndBooks = new ArrayList<>();
    ArrayList<Opinion> opinions = new ArrayList<>();
    Manager managerOfTheStore = new Manager();

    public DatabaseMySQL() {
    }

    //functions of this class
    //function for the url
    private static String GET(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // print result
            return response.toString();
        } else {
            return "";
        }
    }
    private static String POST(String url, Map<String,Object> params) throws IOException {
        //Convert Map<String,Object> into key=value&key=value pairs.
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(postData.toString().getBytes("UTF-8"));
        os.flush();
        os.close();
        // For POST only - END
        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        else return "";
    }
    /**
     * function to add a supplier and book to the list and to the DB
     * @param supplierandbook to add
     * @throws Exception
     */
    public void addSupplierAndBook(final SupplierAndBook supplierandbook) throws Exception {
        supplierAndBooks.add(supplierandbook);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("book_id", supplierandbook.getBookID());
                    _params.put("supplier_id", supplierandbook.getSupplierID());
                    _params.put("num_of_copies", supplierandbook.getNumOfCopies());
                    _params.put("price",supplierandbook.getPrice());
                    try {
                        POST(web_url + "addSupplierAndBook.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to remove the supplier and book
     * @param supplierID
     * @param bookID
     */
    private void removeSupplierAndBook(final long supplierID,final long bookID) {

        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("book_id",bookID);
                    _params.put("supplier_id",supplierID);
                    try {
                        POST(web_url + "removeSupplierAndBook.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get all the supplierandbooks of a specific book
     * @param bookID
     * @return supplier list
     * @throws Exception
     */
    @Override
    public ArrayList<SupplierAndBook> supplierListByBook(long bookID) throws Exception {
        if (supplierAndBooks.size() == 0 ) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<SupplierAndBook> suppliersOfTheBook = new ArrayList<>();
        for (SupplierAndBook supplierAndBook : supplierAndBooks)
        {
            if (supplierAndBook.getBookID() == bookID)
            {
                suppliersOfTheBook.add(supplierAndBook);
            }
        }
        return suppliersOfTheBook;
    }
    /**
     * function to update a supplier and book (num of copies and\or price)
     * @param supplierAndBook for update
     */
    private boolean updateSupplierAndBook (final SupplierAndBook supplierAndBook)
    {
        for (SupplierAndBook s : supplierAndBooks)
        {
            if (s.getBookID() == supplierAndBook.getBookID() && s.getSupplierID()== supplierAndBook.getSupplierID()){
                supplierAndBooks.remove(s);
                supplierAndBooks.add(supplierAndBook);
                try{
                    new AsyncTask< Void,Void,Void>() {
                        @SafeVarargs
                        @Override
                        protected final Void doInBackground(Void... params) {
                            Map<String,Object> _params = new LinkedHashMap<>();
                            _params.put("book_id", supplierAndBook.getBookID());
                            _params.put("supplier_id", supplierAndBook.getSupplierID());
                            _params.put("num_of_copies", supplierAndBook.getNumOfCopies());
                            _params.put("price",supplierAndBook.getPrice());
                            try {
                                POST(web_url + "updateSupplierAndBook.php", _params);
                                Log.d("TEST", "reach the update of supplierAndBook ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false; //problem with the updating
    }
    /**
     * set supplier and book list
     */
    @Override
    public void setSupplierAndBookList() {
        supplierAndBooks.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<SupplierAndBook>>() {
                @Override
                protected  ArrayList<SupplierAndBook> doInBackground(Void... voids) {
                    try {
                        SupplierAndBook tempSupplierAndBook;
                        JSONArray suppliersAndBookArray = new JSONObject(GET(web_url + "allSupplierAndBook.php")).getJSONArray("supplier_and_books");
                        for (int i = 0; i < suppliersAndBookArray.length(); i++) {
                            tempSupplierAndBook = new SupplierAndBook();
                            tempSupplierAndBook.setBookID(suppliersAndBookArray.getJSONObject(i).getInt("book_id"));
                            tempSupplierAndBook.setNumOfCopies(suppliersAndBookArray.getJSONObject(i).getInt("num_of_copies"));
                            tempSupplierAndBook.setSupplierID((long) suppliersAndBookArray.getJSONObject(i).getInt("supplier_id"));
                            tempSupplierAndBook.setPrice((float)suppliersAndBookArray.getJSONObject(i).getInt("price"));
                            supplierAndBooks.add(tempSupplierAndBook);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return supplierAndBooks;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<SupplierAndBook> suppliers) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //book CRUD functions
    /**
     * function to add a book
     * @param book to add
     * @param supplierID of the book's supplier
     * @param privilege of the user
     * @param numOfCopies of this book
     * @param price of the book
     * @throws Exception
     */
    @Override
    public void addBook(final Book book, long userID, long supplierID, Privilege privilege, int numOfCopies, double price) throws Exception {
        if (privilege == Privilege.CUSTOMER) //check that only the manager or the supplier can add a book
            throw new Exception("ERROR: you aren't privileged to add a book");
        if (privilege == Privilege.SUPPLIER) //if the user is a supplier -
            if (userID != supplierID) //check if the supplierID for the new book is not equal to the user ID
                throw new Exception("ERROR: you aren't privileged to add a book of another supplier"); //privilege problem
        findSupplierByID(supplierID);//try to find the supplier of the book
            for (Book bookItem : books) {
                if (bookItem.equals(book)) {
                    //there is a book in the list - check if need to add a supplier and book or not
                    for (SupplierAndBook supplierAndBookItem : supplierAndBooks)
                        if (supplierAndBookItem.getBookID() == book.getBookID() && supplierAndBookItem.getSupplierID() == supplierID)
                            throw new Exception("ERROR: this supplier and book already exist in the system"); //there is a supplier and book for this data
                    //add the supplier and book
                    addSupplierAndBook(new SupplierAndBook(book.getBookID(), numOfCopies, supplierID, price));
                    return;
                }
            }
            //the book doesn't exist
            //add the book and create the supplier and book of it
            books.add(book);
            try{
                new AsyncTask< Void,Void,Void>() {
                    @SafeVarargs
                    @Override
                    protected final Void doInBackground(Void... params) {
                        Map<String,Object> _params = new LinkedHashMap<>();
                        _params.put("book_id",book.getBookID());
                        _params.put("name", book.getBookName());
                        _params.put("auther", book.getAuthor());
                        _params.put("publisher",book.getPublisher());
                        Calendar cal;
                        DateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                        format.format(book.getDatePublished());
                        cal=format.getCalendar();
                        java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
                        _params.put("date_published",sqlDate );
                        _params.put("books_category",book.getBooksCategory().ordinal());
                        _params.put("summary", book.getSummary());
                        _params.put("language", book.getLanguage().ordinal());
                        _params.put("rate_avr", book.getRateAVR());
                        try {
                            POST(web_url + "addBook.php", _params);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        addSupplierAndBook(new SupplierAndBook(book.getBookID(), numOfCopies, supplierID, price));
    }
    /**
     * function to remove a book
     * @param bookID of the book
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeBook(long bookID, long userID, Privilege privilege) throws Exception {
        switch (privilege)
        {
            case CUSTOMER:
                //only the manager or the supplier can remove a book
                throw new Exception("ERROR: you aren't privileged to delete a book");
            case SUPPLIER:
                Book bookToDelete=findBookByID(bookID);//get the book
                boolean flag = false;
                for (Order o :orders)
                {
                    if (o.getOrderID()== userID && o.getBookID()==bookID && o.isPaid()==false)
                    {
                        throw new Exception("ERROR: you can not remove this book because it is in orders of customer");
                    }
                }
                for (SupplierAndBook supplierAndBookItem : supplierAndBooks) //delete the supplier and book with this book
                {
                    if (supplierAndBookItem.getBookID() == bookID && supplierAndBookItem.getSupplierID() == userID ) //check if the supplier delete is own books
                    {
                        supplierAndBooks.remove(supplierAndBookItem);
                        removeSupplierAndBook(supplierAndBookItem.getSupplierID(),supplierAndBookItem.getBookID());
                        flag = true;
                        break;
                    }
                }
                if (!flag)//the supplier try to delete a book of another supplier
                    throw new Exception("ERROR: you aren't privileged to delete others supplier's book");
                flag = false;
                for (SupplierAndBook supplierAndBook : supplierAndBooks)
                {
                    if (supplierAndBook.getBookID() == bookID) //there is more suppliers that provide this book
                        flag = true;
                }
                if (!flag) {
                    for (Opinion o: opinions) //remove the opinions of the book
                    {
                        if (o.getBookID() == bookToDelete.getBookID())
                            removeOpinion(o.getOpinionID(),Privilege.MANAGER);
                    }
                    books.remove(bookToDelete);
                    deleteBook(bookToDelete.getBookID());
                }
                break;
            case MANAGER:
                Book bookForDelete=findBookByID(bookID);//get the book
                for (SupplierAndBook supplierAndBookItem : supplierAndBooks) //delete the supplier and book with this book
                {
                    if (supplierAndBookItem.getBookID() == bookID)
                    {
                        supplierAndBooks.remove(supplierAndBookItem);
                        removeSupplierAndBook(supplierAndBookItem.getSupplierID(),supplierAndBookItem.getBookID());
                    }
                }
                for (Opinion o: opinions) //remove the opinions of the book
                {
                    if (o.getBookID() == bookForDelete.getBookID())
                        removeOpinion(o.getOpinionID(),Privilege.MANAGER);
                }
                books.remove(bookForDelete);
                deleteBook(bookForDelete.getBookID());
                break;
        }
    }
    /**
     * function to delete the book from the database
     * @param bookID
     * @throws Exception
     */
    public void deleteBook(final long bookID) throws Exception {
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("book_id",bookID);
                    try {
                        POST(web_url + "removeBook.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to update a book
     * @param book that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateBook(final Book book, long userID, Privilege privilege) throws Exception {
        switch (privilege)
        {
            case CUSTOMER:
                //check that only the manager or the supplier can update a book
                throw new Exception("ERROR: you aren't privileged to add a book");
            case SUPPLIER:
                Book oldBook=findBookByID(book.getBookID());//get the book by its ID
                boolean flag = false;
                for (SupplierAndBook s : supplierAndBooks)
                    if (s.getBookID() == book.getBookID() && s.getSupplierID() == userID)
                        flag = true;
                if (! flag)//the supplier try to update a book of another supplier
                    throw new Exception("ERROR: you aren't privileged to update others supplier's book");
                //remove the old book form the list and add the updated book to the list
                books.remove(oldBook);
                books.add(book);
                break;
            case MANAGER:
                Book theOldBook=findBookByID(book.getBookID());//get the book by its ID
                //remove the old book form the list and add the updated book to the list
                books.remove(theOldBook);
                books.add(book);
                break;
        }
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("book_id",book.getBookID());
                    _params.put("name", book.getBookName());
                    _params.put("auther", book.getAuthor());
                    _params.put("publisher",book.getPublisher());
                    Calendar cal;
                    DateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                    format.format(book.getDatePublished());
                    cal=format.getCalendar();
                    java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
                    _params.put("date_published", sqlDate);
                    _params.put("books_category",book.getBooksCategory().ordinal());
                    _params.put("summary", book.getSummary());
                    _params.put("language", book.getLanguage().ordinal());
                    _params.put("rate_avr", book.getRateAVR());
                    try {
                        POST(web_url + "updateBook.php", _params);
                        Log.d("TEST", "reach the update of book " );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get the book list
     * @return book list
     * @throws Exception
     */
    @Override
    public ArrayList<Book> getBookList() throws Exception {
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the book list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            listToReturn.add(new Book(bookItem));
        return listToReturn;
    }
    /**
     * function to set the book list from the DB
     */
    @Override
    public void setBooksList() {
        books.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Book>>() {
                @Override
                protected  ArrayList<Book> doInBackground(Void... voids) {
                    try {
                        Book tempBook;
                        int max = 0;
                        JSONArray booksArray = new JSONObject(GET(web_url + "allBooks.php")).getJSONArray("books");
                        for (int i = 0; i < booksArray.length(); i++) {
                            tempBook = new Book();
                            tempBook.setBookID(booksArray.getJSONObject(i).getInt("book_id"));
                            tempBook.setBookName(booksArray.getJSONObject(i).getString("name"));
                            tempBook.setAuthor(booksArray.getJSONObject(i).getString("auther"));
                            tempBook.setPublisher(booksArray.getJSONObject(i).getString("publisher"));
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            tempBook.setDatePublished(df.parse(booksArray.getJSONObject(i).getString("date_published")));
                            tempBook.setBooksCategory
                                    (Category.values()[booksArray.getJSONObject(i).getInt("books_category")]);
                            tempBook.setSummary(booksArray.getJSONObject(i).getString("summary"));
                            tempBook.setLanguage(
                                    Language.values()[booksArray.getJSONObject(i).getInt("language")]);
                            tempBook.setRateAVR(booksArray.getJSONObject(i).getDouble("rate_avr"));
                            books.add(tempBook);
                            if (max < tempBook.getBookID()) {
                                max = tempBook.getBookID();
                            }
                        }
                        Book.setBookIDCounter(max+1);//set the max id for adding the next books
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return books;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Book> books) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    /**
     * find a book by a given ID
     * @param id number of the book
     * @return the book
     * @throws Exception
     */
    @Override
    public Book findBookByID(long id) throws Exception {
        for (Book bookItem : books) {
            if (bookItem.getBookID() == id)
                return bookItem;
        }
        throw new Exception("ERROR: book doesn't exists in the system");
    }

    //supplier CRUD functions
    /**
     * function to add a supplier
     * @param supplier to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addSupplier(final Supplier supplier, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can add a supplier
            throw new Exception("ERROR: you aren't privileged to add a supplier");
        if (suppliers.contains(supplier)) //check if the supplier exist
            throw new Exception("ERROR: this supplier already exist");
        //add this supplier
        suppliers.add(supplier);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("supplier_id",supplier.getNumID());
                    _params.put("name", supplier.getName());
                    _params.put("address", supplier.getAddress());
                    _params.put("gender",supplier.getGender().ordinal());
                    _params.put("phone_number", supplier.getPhoneNumber());
                    _params.put("email_address",supplier.getEmailAddress());
                    _params.put("privilege", supplier.getPrivilege().ordinal());
                    _params.put("customer_service_phone_number", supplier.getCustomerServicePhoneNumber());
                    _params.put("reservations_phone_number", supplier.getReservationsPhoneNumber());
                    _params.put("type", supplier.getType().ordinal());
                    try {
                        POST(web_url + "addSupplier.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to remove a supplier
     * @param supplierID of the supplier
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeSupplier(final long supplierID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can remove a supplier
            throw new Exception("ERROR: you aren't privileged to add a supplier");
        Supplier supplierToDelete = findSupplierByID(supplierID); //try to get the supplier
        ArrayList<SupplierAndBook> supplierAndBooksForSupplier = new ArrayList<>();
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)
        {
            if (supplierAndBookItem.getSupplierID() == supplierID)
            {
                //save the supplier and book items for this supplier
                supplierAndBooksForSupplier.add(supplierAndBookItem);
            }
        }
        Manager manager = getManger();
        //for each supplier and book - check if there is more supplier to those books
        for(SupplierAndBook s : supplierAndBooksForSupplier)
        {
            boolean isTheBookHasMoreSupplier = false;
            for (SupplierAndBook supplierAndBookItem : supplierAndBooks)
            {
                if (supplierAndBookItem.getBookID() == s.getBookID())
                    isTheBookHasMoreSupplier = true;
            }
            if(!isTheBookHasMoreSupplier) //there is no another supplier to this book
                removeBook(s.getBookID(), manager.getNumID() ,privilege); //remove the book (and the supplier and book in this function)

            else {
                supplierAndBooks.remove(s); // remove only the supplier and book
                removeSupplierAndBook(supplierID,s.getBookID());
            }
        }
        suppliers.remove(supplierToDelete); // remove the supplier
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("supplier_id",supplierID);
                    try {
                        POST(web_url + "removeSupplier.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to update the supplier
     * @param supplier that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateSupplier(final Supplier supplier, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a supplier
            throw new Exception("ERROR: you aren't privileged to add a supplier");
        Supplier oldSupplier = findSupplierByID(supplier.getNumID()); //get the old supplier by its ID
        //remove the old supplier and add the updated supplier
        suppliers.remove(oldSupplier);
        suppliers.add(supplier);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("supplier_id",supplier.getNumID());
                    _params.put("name", supplier.getName());
                    _params.put("address", supplier.getAddress());
                    _params.put("gender",supplier.getGender().ordinal());
                    _params.put("phone_number", supplier.getPhoneNumber());
                    _params.put("email_address",supplier.getEmailAddress());
                    _params.put("privilege", supplier.getPrivilege().ordinal());
                    _params.put("customer_service_phone_number", supplier.getCustomerServicePhoneNumber());
                    _params.put("reservations_phone_number", supplier.getReservationsPhoneNumber());
                    _params.put("type", supplier.getType().ordinal());
                    try {
                        POST(web_url + "updateSupplier.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get the supplier list
     * @return supplier list
     * @throws Exception
     */
    @Override
    public ArrayList<Supplier> getSupplierList() throws Exception {
        if (suppliers.size() == 0) //if the list is empty
            throw new Exception("ERROR: the supplier list is empty");
        //to prevent aliasing - create a new list and add new suppliers that are equal to the suppliers in the list supplier
        ArrayList<Supplier> listToReturn = new ArrayList<>();
        for (Supplier supplierItem : suppliers)
            listToReturn.add(new Supplier(supplierItem));
        return listToReturn;
    }
    /**
     * function to set the list
     */
    @Override
    public void setSupplierList() {
        suppliers.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Supplier>>() {
                @Override
                protected  ArrayList<Supplier> doInBackground(Void... voids) {
                    try {
                        Supplier tempSupplier;
                        JSONArray suppliersArray = new JSONObject(GET(web_url + "allSupliers.php")).getJSONArray("suppliers");
                        for (int i = 0; i < suppliersArray.length(); i++) {
                            tempSupplier = new Supplier();
                            tempSupplier.setNumID((long) suppliersArray.getJSONObject(i).getInt("supplier_id"));
                            tempSupplier.setName(suppliersArray.getJSONObject(i).getString("name"));
                            tempSupplier.setAddress(suppliersArray.getJSONObject(i).getString("address"));
                            tempSupplier.setGender(Gender.values()[suppliersArray.getJSONObject(i).getInt("gender")]);
                            tempSupplier.setPhoneNumber(suppliersArray.getJSONObject(i).getString("phone_number"));
                            tempSupplier.setEmailAddress(suppliersArray.getJSONObject(i).getString("email_address"));
                            tempSupplier.setPrivilege(
                                    Privilege.values()[suppliersArray.getJSONObject(i).getInt("privilege")]);
                            tempSupplier.setCustomerServicePhoneNumber(suppliersArray.getJSONObject(i).getString("customer_service_phone_number"));
                            tempSupplier.setReservationsPhoneNumber(suppliersArray.getJSONObject(i).getString("reservations_phone_number"));
                            tempSupplier.setType(
                                    SupplierType.values()[suppliersArray.getJSONObject(i).getInt("type")]);
                            suppliers.add(tempSupplier);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return suppliers;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Supplier> suppliers) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * find a Supplier by a given ID
     * @param id number of the Supplier
     * @return the Supplier
     * @throws Exception
     */
    @Override
    public Supplier findSupplierByID(long id) throws Exception {
        for (Supplier supplierItem : suppliers) {
            if (supplierItem.getNumID() == id)
                return supplierItem;
        }
        throw new Exception("ERROR: book doesn't exists in the system");
    }

    //order CRUD functions
    /**
     * function to add an order
     * @param order to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addOrder(final Order order, Privilege privilege) throws Exception {
        if (privilege == Privilege.SUPPLIER) //check that the supplier can not add an order
            throw new Exception("ERROR: you aren't privileged to add an order");
        //check that all the entities are correct
        findCustomerByID(order.getCustomerNumID());
        findBookByID(order.getBookID());
        findSupplierByID(order.getSupplierID());
        boolean flag = false;
        for (SupplierAndBook s : supplierAndBooks)
        {
            if (s.getSupplierID() == order.getSupplierID() && s.getBookID() == order.getBookID()) {
                //check that there are enough of books for this order and update the amount in the supplier and book
                flag = true;
                if (s.getNumOfCopies() - order.getNumOfCopies() >= 0) {
                    s.setNumOfCopies(s.getNumOfCopies() - order.getNumOfCopies());
                    updateSupplierAndBook (s);
                    //set the order's amount price
                    order.setTotalPrice(order.getNumOfCopies() * s.getPrice());
                    break;
                }
                else
                    throw new Exception("ERROR: there isn't enough copies of this book for your order");
            }
        }
        if (!flag)
            throw new Exception("ERROR: this supplier doesn't provide this book");
        customerVIP(order);
        orders.add(order);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("order_id",order.getOrderID());
                    _params.put("supplier_id", order.getSupplierID());
                    _params.put("book_id", order.getBookID());
                    _params.put("customer_num_id",order.getCustomerNumID());
                    _params.put("num_of_copies", order.getNumOfCopies());
                    _params.put("total_price",order.getTotalPrice());
                    if (order.isPaid())
                        _params.put("paid", 1);
                    else
                        _params.put("paid", 0);
                    try {
                        POST(web_url + "addOrder.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to remove an order
     * @param orderID of the order
     * @param privilege od the user
     * @throws Exception
     */
    @Override
    public void removeOrder(final long orderID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can remove an order
            throw new Exception("ERROR: you aren't privileged to remove an order");
        Order orderToDelete = findOrderByID(orderID);
        if (orderToDelete.isPaid())
            throw new Exception("ERROR: you cannot remove a paid order");
        //update the amount of copies in the supplier and book and remove the order
        for (SupplierAndBook s: supplierAndBooks)
        {
            if (s.getSupplierID()==orderToDelete.getSupplierID() && s.getBookID()==orderToDelete.getBookID())
            {
                s.setNumOfCopies(s.getNumOfCopies()+orderToDelete.getNumOfCopies());
                updateSupplierAndBook(s);
                break;
            }
        }
        orders.remove(orderToDelete);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("order_id",orderID);
                    try {
                        POST(web_url + "removeOrder.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to update an order
     * @param order to update
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateOrder(final Order order, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update an order
            throw new Exception("ERROR: you aren't privileged to update an order");
        Order oldOrder = findOrderByID(order.getOrderID());
        if (oldOrder.getNumOfCopies() != order.getNumOfCopies())
        {
            //need to update the amount of copies in the supplier and book
            for (SupplierAndBook s : supplierAndBooks){
                if (s.getSupplierID()==oldOrder.getSupplierID() && s.getBookID()==oldOrder.getBookID())
                {
                    s.setNumOfCopies(s.getNumOfCopies()+oldOrder.getNumOfCopies()-order.getNumOfCopies());
                    updateSupplierAndBook(s);
                    break;
                }
            }
        }
        //remove the old order and add the updated order
        orders.remove(oldOrder);
        orders.add(order);

        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("order_id",order.getOrderID());
                    _params.put("supplier_id", order.getSupplierID());
                    _params.put("book_id", order.getBookID());
                    _params.put("customer_num_id",order.getCustomerNumID());
                    _params.put("num_of_copies", order.getNumOfCopies());
                    _params.put("total_price",order.getTotalPrice());
                    if (order.isPaid())
                        _params.put("paid", 1);
                    else
                        _params.put("paid", 0);
                    try {
                        POST(web_url + "updateOrder.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get the orders' list
     * @return orders' list
     * @throws Exception
     */
    @Override
    public ArrayList<Order> getOrderList() throws Exception {
        if (orders.size() == 0) //if the list is empty
            throw new Exception("ERROR: the order list is empty");
        //to prevent aliasing - create a new list and add new orders that are equal to the orders in the order's list
        ArrayList<Order> listToReturn = new ArrayList<>();
        for (Order orderItem : orders)
            listToReturn.add(new Order(orderItem));
        return listToReturn;
    }
    /**
     * set orders list
     */
    @Override
    public void setOrderList() {
        orders.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Order>>() {
                @Override
                protected  ArrayList<Order> doInBackground(Void... voids) {
                    try {
                        Order tempOrder;
                        int max = 0;
                        JSONArray ordersArray = new JSONObject(GET(web_url + "allOrders.php")).getJSONArray("orders");
                        for (int i = 0; i < ordersArray.length(); i++) {
                            tempOrder = new Order();
                            tempOrder.setOrderID(ordersArray.getJSONObject(i).getInt("order_id"));
                            tempOrder.setSupplierID((long) ordersArray.getJSONObject(i).getInt("supplier_id"));
                            tempOrder.setBookID(ordersArray.getJSONObject(i).getInt("book_id"));
                            tempOrder.setCustomerNumID((long) ordersArray.getJSONObject(i).getInt("customer_num_id"));
                            tempOrder.setNumOfCopies(ordersArray.getJSONObject(i).getInt("num_of_copies"));
                            tempOrder.setTotalPrice(ordersArray.getJSONObject(i).getDouble("total_price"));
                            tempOrder.setPaid((ordersArray.getJSONObject(i).getInt("paid") != 0));
                            orders.add(tempOrder);
                            if (max < tempOrder.getOrderID())
                            {
                                max = (int)tempOrder.getOrderID();
                            }
                        }
                        Order.setOrderIDCounter(max + 1);//set the max id for adding the next orders
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return orders;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Order> orders) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to find a orderID by its id
     * @param orderID of the Customer
     * @return the order
     * @throws Exception
     */
    @Override
    public Order findOrderByID(long orderID) throws Exception {
        for (Order OrderItem : orders) {
            if (OrderItem.getOrderID() == orderID)
                return OrderItem;
        }
        throw new Exception("ERROR: order doesn't exists in the system");
    }

    //customer CRUD functions
    /**
     * function to add a customer
     * @param customer to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addCustomer(final Customer customer, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a customer
            throw new Exception("ERROR: you aren't privileged to add a customer");
        if (customers.contains(customer)) //check if the customer exist
            throw new Exception("ERROR: this customer already exist");
        //add this customer
        customers.add(customer);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("customer_id",customer.getNumID());
                    _params.put("name", customer.getName());
                    _params.put("address", customer.getAddress());
                    _params.put("gender",customer.getGender().ordinal());
                    _params.put("phone_number", customer.getPhoneNumber());
                    _params.put("email_address", customer.getEmailAddress());
                    _params.put("privilege", customer.getPrivilege().ordinal());
                    Calendar cal;
                    DateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                    format.format(customer.getBirthDay());
                    cal=format.getCalendar();
                    java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
                    _params.put("birthday",sqlDate);
                    _params.put("num_of_credit_card", customer.getNumOfCreditCard());
                    _params.put("status",customer.getStatus().ordinal());
                    if (customer.isVIP())
                        _params.put("vip", 1);
                    else
                        _params.put("vip", 0);
                    try {
                        POST(web_url + "addCustomer.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to remove a customer
     * @param customerID of the customer
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeCustomer(final long customerID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can remove a Customer
            throw new Exception("ERROR: you aren't privileged to remove a customer");
        Customer customerToDelete = findCustomerByID(customerID); //try to get the Customer
        customers.remove(customerToDelete); // remove the Customer
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("customer_id",customerID);
                    try {
                        POST(web_url + "removeCustomer.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to update the customer
     * @param customer that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateCustomer(final Customer customer, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a customer
            throw new Exception("ERROR: you aren't privileged to update a customer");
        Customer oldCustomer = findCustomerByID(customer.getNumID()); //get the old customer by its ID
        //remove the old customer and add the updated customer
        customers.remove(oldCustomer);
        customers.add(customer);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("customer_id",customer.getNumID());
                    _params.put("name", customer.getName());
                    _params.put("address", customer.getAddress());
                    _params.put("gender", customer.getGender().ordinal());
                    _params.put("phone_number", customer.getPhoneNumber());
                    _params.put("email_address", customer.getEmailAddress());
                    _params.put("privilege", customer.getPrivilege().ordinal());
                    Calendar cal;
                    DateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                    format.format(customer.getBirthDay());
                    cal=format.getCalendar();
                    java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
                    _params.put("birthday",sqlDate);
                    _params.put("num_of_credit_card", customer.getNumOfCreditCard());
                    _params.put("status",customer.getStatus().ordinal());
                    if (customer.isVIP())
                        _params.put("vip", 1);
                    else
                        _params.put("vip", 0);
                    try {
                        POST(web_url + "updateCustomer.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get the customers' list
     * @return customers' list
     * @throws Exception
     */
    @Override
    public ArrayList<Customer> getCustomerList(Privilege privilege) throws Exception {
        if (customers.size() == 0) //if the list is empty
            throw new Exception("ERROR: the customer list is empty");
        if (privilege != Privilege.MANAGER)//check that only the manager can get the customers' list
            throw new Exception("ERROR: you aren't privileged to get the customers' list");
        //to prevent aliasing - create a new list and add new customers that are equal to the customers in the customer's list
        ArrayList<Customer> listToReturn = new ArrayList<>();
        for (Customer customerItem : customers)
            listToReturn.add(new Customer(customerItem));
        return listToReturn;
    }
    /**
     * set customer list
     */
    @Override
    public void setCustomerList() {
        customers.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Customer>>() {
                @Override
                protected  ArrayList<Customer> doInBackground(Void... voids) {
                    try {
                        Customer tempCustomer;
                        JSONArray customersArray = new JSONObject(GET(web_url + "allCustomer.php")).getJSONArray("customers");
                        for (int i = 0; i < customersArray.length(); i++) {
                            tempCustomer = new Customer();
                            tempCustomer.setNumID((long) customersArray.getJSONObject(i).getInt("customer_id"));
                            tempCustomer.setName(customersArray.getJSONObject(i).getString("name"));
                            tempCustomer.setAddress(customersArray.getJSONObject(i).getString("address"));
                            tempCustomer.setGender(Gender.values()[customersArray.getJSONObject(i).getInt("gender")]);
                            tempCustomer.setPhoneNumber(customersArray.getJSONObject(i).getString("phone_number"));
                            tempCustomer.setEmailAddress(customersArray.getJSONObject(i).getString("email_address"));
                            tempCustomer.setPrivilege(
                                    Privilege.values()[customersArray.getJSONObject(i).getInt("privilege")]);
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            tempCustomer.setBirthDay(df.parse(customersArray.getJSONObject(i).getString("birthday")));
                            tempCustomer.setNumOfCreditCard(customersArray.getJSONObject(i).getString("num_of_credit_card"));
                            tempCustomer.setStatus(
                                    com.adinaandsari.virtualbookstore.entities.Status.values()
                                            [customersArray.getJSONObject(i).getInt("status")]);
                            tempCustomer.setVIP((customersArray.getJSONObject(i).getInt("vip") != 0));
                            customers.add(tempCustomer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return customers;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Customer> suppliers) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to find a Customer by its id
     * @param customerID of the Customer
     * @return the Customer
     * @throws Exception
     */
    @Override
    public Customer findCustomerByID(long customerID) throws Exception {
        for (Customer CustomerItem : customers) {
            if (CustomerItem.getNumID() == customerID)
                return CustomerItem;
        }
        throw new Exception("ERROR: customer doesn't exists in the system");
    }

    //opinion CRUD functions
    /**
     * function to add an opinion
     * @param opinion to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addOpinion(final Opinion opinion, Privilege privilege) throws Exception {
        if (privilege != Privilege.CUSTOMER) //check that only the customer can add an opinion
            throw new Exception("ERROR: you aren't privileged to add an opinion");
        Book book = findBookByID(opinion.getBookID());//try to get the book
        opinions.add(opinion);//add the opinion to the opinion list
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("opinion_id",opinion.getOpinionID());
                    _params.put("book_id", opinion.getBookID());
                    _params.put("rate", opinion.getRate());
                    _params.put("your_opinion",opinion.getYourOpinion());
                    try {
                        POST(web_url + "addOpinion.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //update the average rate of the book
        updateBookRate(book);
    }
    /**
     * function to remove an opinion
     * @param opinionID of the opinion to delete
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeOpinion(final long opinionID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can remove an opinion
            throw new Exception("ERROR: you aren't privileged to remove an opinion");
        //try to get the opinion and delete it
        Opinion opinionToDelete = findOpinionByID(opinionID);
        Book book = findBookByID(opinionToDelete.getBookID());//try to get the book to update the rate
        opinions.remove(opinionToDelete);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("opinion_id",opinionID);
                    try {
                        POST(web_url + "removeOpinion.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //update the average rate of the book
        updateBookRate(book);
    }
    /**
     * function to update the opinion
     * @param opinion that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateOpinion(final Opinion opinion, Privilege privilege) throws Exception {
        if (privilege != Privilege.CUSTOMER) //check that only the customer can update an opinion
            throw new Exception("ERROR: you aren't privileged to update an opinion");
        Opinion oldOpinion = findOpinionByID(opinion.getOpinionID());//try to get the opinion
        Book book = findBookByID(opinion.getBookID());//try to get the book to update the rate (the id of the book can not be changed
        //remove the old opinion and add the updated opinion
        opinions.remove(oldOpinion);
        opinions.add(opinion);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("opinion_id",opinion.getOpinionID());
                    _params.put("book_id", opinion.getBookID());
                    _params.put("rate", opinion.getRate());
                    _params.put("your_opinion",opinion.getYourOpinion());
                    try {
                        POST(web_url + "updateOpinion.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //update the average rate of the book
        updateBookRate(book);
    }
    /**
     * function to get a list of book's opinions
     * @param bookID of the book
     * @return list of book's opinions
     * @throws Exception
     */
    @Override
    public ArrayList<Opinion> getOpinionListOfBook(long bookID) throws Exception {
        if (opinions.size() == 0) //if the list is empty
            throw new Exception("ERROR: the opinion list is empty");
        //to prevent aliasing - create a new list and add new opinion that are equal to the opinion in the opinion list
        ArrayList<Opinion> listToReturn = new ArrayList<>();
        for (Opinion opinionItem : opinions)
            if (opinionItem.getBookID() == bookID)  //check that the opinions belongs to the book
                listToReturn.add(new Opinion(opinionItem));
        return listToReturn;
    }
    /**
     * set opinion list
     */
    @Override
    public void setOpinionList() {
        opinions.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Opinion>>() {
                @Override
                protected  ArrayList<Opinion> doInBackground(Void... voids) {
                    try {
                        Opinion tempOpinion;
                        int max = 0;
                        JSONArray opinionsArray = new JSONObject(GET(web_url + "allOpinion.php")).getJSONArray("opinions");
                        for (int i = 0; i < opinionsArray.length(); i++) {
                            tempOpinion = new Opinion();
                            tempOpinion.setOpinionID(opinionsArray.getJSONObject(i).getInt("opinion_id"));
                            tempOpinion.setBookID(opinionsArray.getJSONObject(i).getInt("book_id"));
                            tempOpinion.setRate(opinionsArray.getJSONObject(i).getInt("rate"));
                            tempOpinion.setYourOpinion(opinionsArray.getJSONObject(i).getString("your_opinion"));
                            opinions.add(tempOpinion);
                            if (max < tempOpinion.getOpinionID())
                            {
                                max = tempOpinion.getOpinionID();
                            }
                        }
                        Opinion.setOpinionIDCounter(max +1); //set the max id for adding the next opinions
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return opinions;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Opinion> suppliers) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    /**
     * function to find an opinion by its id
     * @param opinionID of the opinion
     * @return the opinion
     * @throws Exception
     */
    @Override
    public Opinion findOpinionByID(long opinionID) throws Exception {
        for (Opinion opinionItem : opinions) {
            if (opinionItem.getOpinionID() == opinionID)
                return opinionItem;
        }
        throw new Exception("ERROR: opinion doesn't exists in the system");
    }

    //manger CRUD functions
    /**
     * function to add a manger for the store
     * @param manager to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addManger(final Manager manager, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to add a manger");
        if (managerOfTheStore.getNumID() != 0) //check that there is no exist manger
            throw new Exception("ERROR: the manger of the store is already exist");
        //set the new manger
        managerOfTheStore = new Manager(manager);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("manager_id",manager.getNumID());
                    _params.put("name", manager.getName());
                    _params.put("address", manager.getAddress());
                    _params.put("gender",manager.getGender().ordinal());
                    _params.put("phone_number", manager.getPhoneNumber());
                    _params.put("email_address",manager.getEmailAddress());
                    _params.put("privilege", manager.getPrivilege().ordinal());
                    _params.put("years_in_company", manager.getYearsInCompany());
                    try {
                        POST(web_url + "addManager.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to remove the manger of the store
     * @param mangerID of the manger
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeManger(final long mangerID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to remove a manger");
        if (mangerID != managerOfTheStore.getNumID()) // check the entered ID
            throw new Exception("ERROR: this manger's ID is not correct");
        //set the data for the manger if the store to be null
        managerOfTheStore = new Manager();
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("manager_id",mangerID);
                    try {
                        POST(web_url + "removeManager.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to update the manger of the store
     * @param manager to update
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateManger(final Manager manager, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to update a manger");
        if (manager.getNumID() != managerOfTheStore.getNumID()) // check the entered ID
            throw new Exception("ERROR: this manger's ID is not correct");
        //set the data of the manger
        managerOfTheStore = new Manager(manager);
        try{
            new AsyncTask< Void,Void,Void>() {
                @SafeVarargs
                @Override
                protected final Void doInBackground(Void... params) {
                    Map<String,Object> _params = new LinkedHashMap<>();
                    _params.put("manager_id",manager.getNumID());
                    _params.put("name", manager.getName());
                    _params.put("address", manager.getAddress());
                    _params.put("gender",manager.getGender().ordinal());
                    _params.put("phone_number", manager.getPhoneNumber());
                    _params.put("email_address",manager.getEmailAddress());
                    _params.put("privilege", manager.getPrivilege().ordinal());
                    _params.put("years_in_company", manager.getYearsInCompany());
                    try {
                        POST(web_url + "updateManager.php", _params);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * function to get the manger
     * @return the manger
     * @throws Exception
     */
    @Override
    public Manager getManger() throws Exception {
        if (managerOfTheStore.getNumID() == 0) //if the manger is not initialize
            throw new Exception("ERROR: the manger of the store wasn't inserted yet");
        return (new Manager(managerOfTheStore)); // to prevent aliasing
    }
    /**
     * set the manager
     */
    @Override
    public void setManager() {
        managerOfTheStore = new Manager();
        try {
            new AsyncTask<Void, Void,  ArrayList<Manager>>() {
                @Override
                protected  ArrayList<Manager> doInBackground(Void... voids) {
                    ArrayList<Manager> tempList = null;
                    try {
                        Manager tempManager;
                        JSONArray ManagerArray = new JSONObject(GET(web_url + "allManager.php")).getJSONArray("manager");
                        for (int i = 0; i < ManagerArray.length(); i++) {
                            tempManager = new Manager();
                            tempManager.setNumID((long) ManagerArray.getJSONObject(i).getInt("manager_id"));
                            tempManager.setName(ManagerArray.getJSONObject(i).getString("name"));
                            tempManager.setAddress(ManagerArray.getJSONObject(i).getString("address"));
                            tempManager.setGender(Gender.values()[ManagerArray.getJSONObject(i).getInt("gender")]);
                            tempManager.setPhoneNumber(ManagerArray.getJSONObject(i).getString("phone_number"));
                            tempManager.setEmailAddress(ManagerArray.getJSONObject(i).getString("email_address"));
                            tempManager.setPrivilege(
                                    Privilege.values()[ManagerArray.getJSONObject(i).getInt("privilege")]);
                            tempManager.setYearsInCompany(ManagerArray.getJSONObject(i).getInt("years_in_company"));
                            managerOfTheStore = tempManager;
                        }
                        tempList = new ArrayList<>();
                        tempList.add(managerOfTheStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return tempList;
                }
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onPostExecute(ArrayList<Manager> suppliers) {

                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //other functions
    /**
     * function to get a books' list by a book category
     * @param category of the books
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByCategory(Category category) throws Exception {
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            if (bookItem.getBooksCategory() == category) //check that the books belong to a specific category
                listToReturn.add(bookItem);
        return listToReturn;
    }
    /**
     * function to get a books' list by a book authors
     * @param authorsName of the books
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByAuthors(String authorsName) throws Exception {
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the book list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            if (bookItem.getAuthor() == authorsName) //check that the books belong to a specific author
                listToReturn.add(bookItem);
        return listToReturn;
    }
    /**
     * function to get a books' list by a dates of publisher
     * @param start date
     * @param end date
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByDate(Date start, Date end) throws Exception {
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            //check that the books' date of publisher is in the time span
            if (bookItem.getDatePublished().before(end) && bookItem.getDatePublished().after(start) )
                listToReturn.add(new Book(bookItem));
        return listToReturn;
    }
    /**
     * function to get all the book of a specific supplier
     * @param supplierID
     * @return book list
     * @throws Exception
     */
    @Override
    public ArrayList<Book> bookListBySupplier(long supplierID) throws Exception {
        if (supplierAndBooks.size() == 0 ) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> books = new ArrayList<>();
        for (SupplierAndBook supplierAndBook : supplierAndBooks)
        {
            if (supplierAndBook.getSupplierID() == supplierID)
            {
                books.add(new Book(findBookByID(supplierAndBook.getBookID())));
            }
        }
        return books;
    }
    /**
     * function to get the list of authors
     * @return authorsName of all the books
     * @throws Exception
     */
    @Override
    public ArrayList<String> authorList() throws Exception {
        if (books.size() == 0)//if the list is empty
            throw new Exception("ERROR: the book list is empty");
        ArrayList<String> authorsName = new ArrayList<>();
        String name;
        boolean exist = false;
        for (Book book : books)
        {
            name = book.getAuthor();
            for (String n : authorsName) //check that it is not exist in the list all ready
            {
                if (n == name) {
                    exist = true;
                    break;
                }
            }
            if (exist == false)
                authorsName.add(name);
        }
        return authorsName;
    }
    /**
     * func to get all the copies of a book by suppliers
     * @param bookID
     * @throws Exception
     * @return
     */
    @Override
    public int getNumOfCopiesOfBook(long bookID) throws Exception {
        int numOfBookCopies=0;
        findBookByID(bookID);
        for (SupplierAndBook s:supplierAndBooks)//loop to go on supplier and book list
        {
            if (s.getBookID() == bookID)
                numOfBookCopies += s.getNumOfCopies();//sum all the copies of this book in all suppliers
        }
        return numOfBookCopies;
    }
    /**
     *  func to calc total price from an order and to set payment
     * @param customerId
     * @throws Exception
     * @return
     */
    @Override
    public ArrayList<Order> finishOrder(long customerId) throws Exception {
        ArrayList<Order> customerOrders=new ArrayList<Order>();
        for (Order o:orders)//loop to go on orders
        {
            if (o.isPaid() != true && o.getCustomerNumID() == customerId) {
                customerOrders.add(o);
            }
        }
        if (customerOrders.size() == 0 ) //if the list is empty
            throw new Exception("ERROR: you have nothing in your cart");
        return customerOrders;
    }
    /**
     * func to get the total price of a customer cart after checking if he's VIP
     * @param certainCustomerOrders
     * @return
     * @throws Exception
     */
    @Override
    public double getCartPayment(ArrayList<Order> certainCustomerOrders) throws Exception {
        double SumToPay = 0;
        for (Order o : certainCustomerOrders)//loop to go on orders
        {
            SumToPay += o.getTotalPrice();
            o.setPaid(true);
            updateOrder(o, Privilege.MANAGER);
        }
        Order helper=certainCustomerOrders.get(0);
        int size=certainCustomerOrders.size();
        if (SumToPay/size>100&&size>10) {//a customer becomes VIP if he has 10 orders in an average of 100 NIS
            Customer c = findCustomerByID(helper.getCustomerNumID());
            //updating the right places
            c.setVIP(true);
            helper.setTotalPrice(helper.getTotalPrice()*0.9);
            updateOrder(helper,Privilege.MANAGER);
            updateCustomer(c,Privilege.MANAGER);
        }

        return SumToPay;
    }
    /**
     * func for supplier to add more copies to a kook he supplies
     * @param bookID
     * @param numOfCopies
     * @param supplierID
     * @param privilege
     * @throws Exception
     */
    @Override
    public void addMoreCopiesToBook(long bookID, int numOfCopies, long supplierID, Privilege privilege) throws Exception {
        boolean flag=false;
        if (privilege == Privilege.CUSTOMER) //check that only the manager or the supplier can add a book
            throw new Exception("ERROR: you aren't privileged to add more copies to a book");
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)//loop to go on supplier and book
            if (supplierAndBookItem.getBookID() ==bookID &&supplierAndBookItem.getSupplierID()==supplierID) //check that this supplier and book exists
            {
                supplierAndBookItem.setNumOfCopies(supplierAndBookItem.getNumOfCopies() + numOfCopies);//adding the copies the supplier wanted to add
                updateSupplierAndBook(supplierAndBookItem);//making the update in the list
                flag=true;
                break;
            }
        if(flag==false)//the supplier and book does'nt exist
            throw new Exception("ERROR:there is no supplier that provides this book");
    }
    /**
     * func to update a customer if he is VIP and to give him 10% discount
     * @param order
     * @throws Exception
     */
    @Override
    public void customerVIP(Order order) throws Exception {

    }
    /**
     * function to update the rate of a book
     * @param book to update
     * @throws Exception
     */
    @Override
    public void updateBookRate(Book book) throws Exception {
        //update the average rate of the book
        int count = 0;
        int rateSumOfBook = 0;
        for (Opinion op : opinions)
        {
            if (op.getBookID() == book.getBookID())
            {
                count++;
                rateSumOfBook += op.getRate();
            }
        }
        if (count == 0)//there are no opinions
        {
            book.setRateAVR(0);
        }
        else
        {
            book.setRateAVR(Math.round(rateSumOfBook / count));
        }
        //update the book
        updateBook(book,getManger().getNumID() , Privilege.MANAGER);
    }
    /**
     *function that return the most ordered book in the store
     * @return book
     * @throws Exception
     */
    @Override
    public Book bookOfTheStore() throws Exception {
        class OrderOfBook { //inner class
            public long orderBookID;
            public int numOfOrder;

            //constructor
            public OrderOfBook(int numOfOrder, long orderBookID) {
                this.numOfOrder = numOfOrder;
                this.orderBookID = orderBookID;
            }
        }
        ArrayList<OrderOfBook> orderOfBookArrayList = new ArrayList<>();
        for (Book book : books) //set all the book id in the list
            orderOfBookArrayList.add(new OrderOfBook(book.getBookID(), 0));
        for (Order o : orders)//loop to go on orders
        {
            for (OrderOfBook orderOfBook : orderOfBookArrayList) { //calculate the num of orders for each book
                if (orderOfBook.orderBookID == o.getBookID())
                    orderOfBook.numOfOrder++;
            }
        }
        if (orderOfBookArrayList.size() == 0) //if there are no order
            throw new Exception("there are no order in the store");
        //get the max ordered book and return it
        int max =orderOfBookArrayList.get(1).numOfOrder;
        long mostOrderBookID = orderOfBookArrayList.get(1).orderBookID;
        for (OrderOfBook orderOfBook : orderOfBookArrayList){
            if (max < orderOfBook.numOfOrder)
            {
                max= orderOfBook.numOfOrder;
                mostOrderBookID = orderOfBook.orderBookID;
            }
        }
        return findBookByID(mostOrderBookID);
    }
    /**
     * function to get the num of book copies of a specific supplier
     * @param bookID
     * @param supplierID
     * @return num of copies
     * @throws Exception
     */
    @Override
    public int getNumOfBookCopiesForSupplier(long bookID, long supplierID) throws Exception {
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)//loop to go on supplier and book
            if (supplierAndBookItem.getBookID() ==bookID &&supplierAndBookItem.getSupplierID()==supplierID) //check that this supplier and book exists
            {
                return supplierAndBookItem.getNumOfCopies();
            }
        //the supplier and book does not exist
        throw new Exception("ERROR:there is no supplier that provides this book");
    }
    /**
     *  function to get the book price of a specific supplier
     * @param bookID
     * @param supplierID
     * @return boo price
     * @throws Exception
     */
    @Override
    public double getPriceOfBookForSupplier(long bookID, long supplierID) throws Exception {
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)//loop to go on supplier and book
            if (supplierAndBookItem.getBookID() ==bookID &&supplierAndBookItem.getSupplierID()==supplierID) //check that this supplier and book exists
            {
                return supplierAndBookItem.getPrice();
            }
        //the supplier and book does not exist
        throw new Exception("ERROR:there is no supplier that provides this book");
    }
    /**
     * function to set the book price of a specific supplier
     * @param bookID
     * @param supplierID
     * @param price
     * @throws Exception
     */
    @Override
    public void setPriceOfBookForSupplier(long bookID, long supplierID, double price) throws Exception {
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)//loop to go on supplier and book
            if (supplierAndBookItem.getBookID() ==bookID &&supplierAndBookItem.getSupplierID()==supplierID) //check that this supplier and book exists
            {
                supplierAndBookItem.setPrice(price);
                updateSupplierAndBook(supplierAndBookItem);
                return;
            }
        //the supplier and book does not exist
        throw new Exception("ERROR:there is no supplier that provides this book");
    }
}
