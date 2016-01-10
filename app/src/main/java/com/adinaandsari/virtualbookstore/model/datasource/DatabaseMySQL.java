package com.adinaandsari.virtualbookstore.model.datasource;

import android.os.AsyncTask;

import com.adinaandsari.virtualbookstore.ConstValue;
import com.adinaandsari.virtualbookstore.entities.Book;
import com.adinaandsari.virtualbookstore.entities.Category;
import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Language;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Opinion;
import com.adinaandsari.virtualbookstore.entities.Order;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierAndBook;
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
    private boolean updateSupplierAndBook (SupplierAndBook supplierAndBook)///////////////////////////////
    {
        for (SupplierAndBook s : supplierAndBooks)
        {
            if (s.getBookID() == supplierAndBook.getBookID() && s.getSupplierID()== supplierAndBook.getSupplierID()){
                supplierAndBooks.remove(s);
                supplierAndBooks.add(supplierAndBook);
                return true;
            }
        }
        return false; //problem with the updating
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
            //the book doesn't exist
            //add the book and create the supplier and book of it
            books.add(book);
            addSupplierAndBook(new SupplierAndBook(book.getBookID(), numOfCopies, supplierID, price));
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
                        _params.put("date_published", book.getDatePublished());
                        _params.put("books_category",book.getBooksCategory());
                        _params.put("name", book.getSummary());
                        _params.put("language", book.getLanguage());
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
        }
    }

    @Override
    public void removeBook(long bookID, long userID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateBook(Book book, long userID, Privilege privilege) throws Exception {

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
                        JSONArray booksArray = new JSONObject(GET(web_url + "allBooks.php")).getJSONArray("books");
                        for (int i = 0; i < booksArray.length(); i++) {
                            tempBook = new Book();
                            tempBook.setBookID(booksArray.getJSONObject(i).getInt("book_id"));
                            tempBook.setBookName(booksArray.getJSONObject(i).getString("name"));
                            tempBook.setAuthor(booksArray.getJSONObject(i).getString("auther"));
                            tempBook.setPublisher(booksArray.getJSONObject(i).getString("publisher"));
                            DateFormat df = new SimpleDateFormat("yyyy/dd/MM");///dd/yyyy");
                            tempBook.setDatePublished(df.parse(booksArray.getJSONObject(i).getString("date_published")));
                            tempBook.setBooksCategory
                                    (Category.values()[Integer.valueOf(booksArray.getJSONObject(i).getString("books_category"))]);
                            tempBook.setSummary(booksArray.getJSONObject(i).getString("summary"));
                            tempBook.setLanguage(
                                    Language.values()[Integer.valueOf(booksArray.getJSONObject(i).getString("language"))]);
                            tempBook.setRateAVR(Double.valueOf(booksArray.getJSONObject(i).getString("rate_avr")));
                            books.add(tempBook);
                        }
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
                    _params.put("gender",supplier.getGender());
                    _params.put("phone_number", supplier.getPhoneNumber());
                    _params.put("email_address",supplier.getEmailAddress());
                    _params.put("privilege", supplier.getPrivilege());
                    _params.put("customer_service_phone_number", supplier.getCustomerServicePhoneNumber());
                    _params.put("reservation_phone_number", supplier.getReservationsPhoneNumber());
                    _params.put("type", supplier.getType());
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

    @Override
    public void removeSupplier(long supplierID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateSupplier(Supplier supplier, Privilege privilege) throws Exception {

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

    @Override
    public void setSupplierList() {
        suppliers.clear();
        try {
            new AsyncTask<Void, Void,  ArrayList<Supplier>>() {
                @Override
                protected  ArrayList<Supplier> doInBackground(Void... voids) {
                    try {
                        Supplier tempSupplier;
                        JSONArray suppliersArray = new JSONObject(GET(web_url + "allSupliers.php")).getJSONArray("books");
                        for (int i = 0; i < suppliersArray.length(); i++) {
                            tempSupplier = new Supplier();
                            /*
                            tempSupplier.setBookID(booksArray.getJSONObject(i).getInt("book_id"));
                            tempBook.setBookName(booksArray.getJSONObject(i).getString("name"));
                            tempBook.setAuthor(booksArray.getJSONObject(i).getString("auther"));
                            tempBook.setPublisher(booksArray.getJSONObject(i).getString("publisher"));
                            DateFormat df = new SimpleDateFormat("yyyy/dd/MM");///dd/yyyy");
                            tempBook.setDatePublished(df.parse(booksArray.getJSONObject(i).getString("date_published")));
                            tempBook.setBooksCategory
                                    (Category.values()[Integer.valueOf(booksArray.getJSONObject(i).getString("books_category"))]);
                            tempBook.setSummary(booksArray.getJSONObject(i).getString("summary"));
                            tempBook.setLanguage(
                                    Language.values()[Integer.valueOf(booksArray.getJSONObject(i).getString("language"))]);
                            tempBook.setRateAVR(Double.valueOf(booksArray.getJSONObject(i).getString("rate_avr")));
                            */
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

    @Override
    public void addOrder(Order order, Privilege privilege) throws Exception {

    }

    @Override
    public void removeOrder(long orderID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateOrder(Order order, Privilege privilege) throws Exception {

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

    @Override
    public void setOrderList() {

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

    @Override
    public void addCustomer(Customer customer, Privilege privilege) throws Exception {

    }

    @Override
    public void removeCustomer(long customerID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateCustomer(Customer customer, Privilege privilege) throws Exception {

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

    @Override
    public void setCustomerList() {

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

    @Override
    public void addOpinion(Opinion opinion, Privilege privilege) throws Exception {

    }

    @Override
    public void removeOpinion(long opinionID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateOpinion(Opinion opinion, Privilege privilege) throws Exception {

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

    @Override
    public void setOpinionList() {

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
                    _params.put("gender",manager.getGender());
                    _params.put("phone_number", manager.getPhoneNumber());
                    _params.put("email_address",manager.getEmailAddress());
                    _params.put("privilege", manager.getPrivilege());
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

    @Override
    public void removeManger(long mangerID, Privilege privilege) throws Exception {

    }

    @Override
    public void updateManger(Manager manager, Privilege privilege) throws Exception {

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

    @Override
    public void setManager() {

    }

    @Override
    public void setSupplierAndBookList() {

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
