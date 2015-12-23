package com.adinaandsari.virtualbookstore.model.datasource;
import com.adinaandsari.virtualbookstore.entities.*;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adina_000 on 09-Nov-15.
 */
public class DatabaseList implements Backend {

    ArrayList<Book> books = new ArrayList<>();
    ArrayList<Supplier> suppliers = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();
    ArrayList<Order> orders = new ArrayList<>();
    ArrayList<SupplierAndBook> supplierAndBooks = new ArrayList<>();
    ArrayList<Opinion> opinions = new ArrayList<>();
    Manager managerOfTheStore = new Manager();

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
    public void addBook(Book book ,long userID ,long supplierID, Privilege privilege, int numOfCopies, double price) throws Exception {
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
                supplierAndBooks.add(new SupplierAndBook(book.getBookID(), numOfCopies, supplierID, price));
                return;
            }
        }
        //the book doesn't exist
        //add the book and create the supplier and book of it
        books.add(book);
        supplierAndBooks.add(new SupplierAndBook(book.getBookID(),numOfCopies,supplierID,price));
    }
    /**
     * function to remove a book
     * @param bookID of the book
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeBook(long bookID, long userID ,Privilege privilege) throws Exception {
        switch (privilege)
        {
            case CUSTOMER:
                //only the manager or the supplier can remove a book
                throw new Exception("ERROR: you aren't privileged to delete a book");
            case SUPPLIER:
                Book bookToDelete=findBookByID(bookID);//get the book
                boolean flag = false;
                for (SupplierAndBook supplierAndBookItem : supplierAndBooks) //delete the supplier and book with this book
                {
                    if (supplierAndBookItem.getBookID() == bookID && supplierAndBookItem.getSupplierID() == userID ) //check if the supplier delete is own books
                    {
                        supplierAndBooks.remove(supplierAndBookItem);
                        flag = true;
                    }
                }
                if (!flag)//the supplier try to delete a book of another supplier
                    throw new Exception("ERROR: you aren't privileged to delete others supplier's book");
                books.remove(bookToDelete);
                break;
            case MANAGER:
                Book bookForDelete=findBookByID(bookID);//get the book
                for (SupplierAndBook supplierAndBookItem : supplierAndBooks) //delete the supplier and book with this book
                {
                    if (supplierAndBookItem.getBookID() == bookID)
                    {
                        supplierAndBooks.remove(supplierAndBookItem);
                    }
                }
                books.remove(bookForDelete);
                break;
        }


    }
    /**
     * function to update a book
     * @param book that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateBook(Book book,long userID  ,Privilege privilege) throws Exception {
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

    //supplier CRUD functions
    /**
     * function to add a supplier
     * @param supplier to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addSupplier(Supplier supplier , Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can add a supplier
            throw new Exception("ERROR: you aren't privileged to add a supplier");
        if (suppliers.contains(supplier)) //check if the supplier exist
            throw new Exception("ERROR: this supplier already exist");
        //add this supplier
        suppliers.add(supplier);
    }
    /**
     * function to remove a supplier
     * @param supplierID of the supplier
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeSupplier(long supplierID , Privilege privilege) throws Exception {
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
            else
                supplierAndBooks.remove(s); // remove only the supplier and book
        }
        suppliers.remove(supplierToDelete); // remove the supplier
    }
    /**
     * function to update the supplier
     * @param supplier that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateSupplier(Supplier supplier , Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a supplier
            throw new Exception("ERROR: you aren't privileged to add a supplier");
        Supplier oldSupplier = findSupplierByID(supplier.getNumID()); //get the old supplier by its ID
        //remove the old supplier and add the updated supplier
        suppliers.remove(oldSupplier);
        suppliers.add(supplier);
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

    //order CRUD functions

    /**
     * function to add an order
     * @param order to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addOrder(Order order, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can add an order
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
    }
    /**
     * function to remove an order
     * @param orderID of the order
     * @param privilege od the user
     * @throws Exception
     */
    @Override
    public void removeOrder(long orderID, Privilege privilege) throws Exception {
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
    }
    /**
     * fucntion to update an order
     * @param order to update
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateOrder(Order order, Privilege privilege) throws Exception {
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

    //customer CRUD functions
    /**
     * function to add a customer
     * @param customer to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addCustomer(Customer customer ,Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a customer
            throw new Exception("ERROR: you aren't privileged to add a customer");
        if (customers.contains(customer)) //check if the customer exist
            throw new Exception("ERROR: this customer already exist");
        //add this customer
        customers.add(customer);
    }
    /**
     * function to remove a customer
     * @param customerID of the customer
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeCustomer(long customerID ,Privilege privilege) throws Exception {
        {
            if (privilege != Privilege.MANAGER) //check that only the manager can remove a Customer
                throw new Exception("ERROR: you aren't privileged to add a customer");
            Customer customerToDelete = findCustomerByID(customerID); //try to get the Customer
            suppliers.remove(customerToDelete); // remove the Customer
        }

    }
    /**
     * function to update the customer
     * @param customer that has been updated
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateCustomer(Customer customer ,Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check that only the manager can update a customer
            throw new Exception("ERROR: you aren't privileged to update a customer");
        Customer oldCustomer = findCustomerByID(customer.getNumID()); //get the old customer by its ID
        //remove the old customer and add the updated customer
        customers.remove(oldCustomer);
        customers.add(customer);
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

    //opinion CRUD functions
    /**
     * function to add an opinion
     * @param opinion to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addOpinion(Opinion opinion ,Privilege privilege) throws Exception{
        if (privilege != Privilege.CUSTOMER) //check that only the customer can add an opinion
            throw new Exception("ERROR: you aren't privileged to add an opinion");
        Book book = findBookByID(opinion.getBookID());//try to get the book
        opinions.add(opinion);//add the opinion to the opinion list
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
    public void removeOpinion(long opinionID,Privilege privilege) throws Exception {
        if (privilege != Privilege.CUSTOMER) //check that only the customer can remove an opinion
            throw new Exception("ERROR: you aren't privileged to remove an opinion");
        //try to get the opinion and delete it
        Opinion opinionToDelete = findOpinionByID(opinionID);
        Book book = findBookByID(opinionToDelete.getBookID());//try to get the book to update the rate
        opinions.remove(opinionToDelete);
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
    public void updateOpinion(Opinion opinion,Privilege privilege) throws Exception {
        if (privilege != Privilege.CUSTOMER) //check that only the customer can update an opinion
            throw new Exception("ERROR: you aren't privileged to update an opinion");
        Opinion oldOpinion = findOpinionByID(opinion.getOpinionID());//try to get the opinion
        Book book = findBookByID(opinion.getBookID());//try to get the book to update the rate (the id of the book can not be changed
        //remove the old opinion and add the updated opinion
        opinions.remove(oldOpinion);
        opinions.add(opinion);
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
    public ArrayList<Opinion> getOpinionListOfBook(long bookID)  throws Exception {
        if (opinions.size() == 0) //if the list is empty
            throw new Exception("ERROR: the opinion list is empty");
        //to prevent aliasing - create a new list and add new opinion that are equal to the opinion in the opinion list
        ArrayList<Opinion> listToReturn = new ArrayList<>();
        for (Opinion opinionItem : opinions)
            if (opinionItem.getBookID() == bookID)  //check that the opinions belongs to the book
                listToReturn.add(new Opinion(opinionItem));
        return listToReturn;
    }

    //manger CRUD functions
    /**
     * function to add a manger for the store
     * @param manager to add
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void addManger(Manager manager, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to add a manger");
        if (managerOfTheStore.getNumID() != 0) //check that there is no exist manger
            throw new Exception("ERROR: the manger of the store is already exist");
        //set the new manger
        managerOfTheStore = new Manager(manager);
    }
    /**
     * function to remove the manger of the store
     * @param mangerID of the manger
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void removeManger(long mangerID, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to remove a manger");
        if (mangerID != managerOfTheStore.getNumID()) // check the entered ID
            throw new Exception("ERROR: this manger's ID is not correct");
        //set the data for the manger if the store to be null
        managerOfTheStore = new Manager();
    }
    /**
     * function to update the manger of the store
     * @param manager to update
     * @param privilege of the user
     * @throws Exception
     */
    @Override
    public void updateManger(Manager manager, Privilege privilege) throws Exception {
        if (privilege != Privilege.MANAGER) //check the privilege
            throw new Exception("ERROR: you aren't privileged to update a manger");
        if (manager.getNumID() != managerOfTheStore.getNumID()) // check the entered ID
            throw new Exception("ERROR: this manger's ID is not correct");
        //set the data of the manger
        managerOfTheStore = new Manager(manager);
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

    //functions to find object
    /**
     * find a Supplier by a given ID
     * @param id number of the Supplier
     * @return the Supplier
     * @throws Exception
     */
    public Supplier findSupplierByID(long id) throws Exception {
        for (Supplier supplierItem : suppliers) {
            if (supplierItem.getNumID() == id)
                return supplierItem;
        }
        throw new Exception("ERROR: book doesn't exists in the system");
    }
    /**
     * find a book by a given ID
     * @param id number of the book
     * @return the book
     * @throws Exception
     */
    public Book findBookByID(long id) throws Exception {
        for (Book bookItem : books) {
            if (bookItem.getBookID() == id)
                return bookItem;
        }
        throw new Exception("ERROR: book doesn't exists in the system");
    }
    /**
     * function to find an opinion by its id
     * @param opinionID of the opinion
     * @return the opinion
     * @throws Exception
     */
    public Opinion findOpinionByID (long opinionID)throws Exception {
        for (Opinion opinionItem : opinions) {
            if (opinionItem.getOpinionID() == opinionID)
                return opinionItem;
        }
        throw new Exception("ERROR: opinion doesn't exists in the system");
    }
    /**
     * function to find a Customer by its id
     * @param customerID of the Customer
     * @return the Customer
     * @throws Exception
     */
    public Customer findCustomerByID(long customerID)throws Exception {
        for (Customer CustomerItem : customers) {
            if (CustomerItem.getNumID() == customerID)
                return CustomerItem;
        }
        throw new Exception("ERROR: customer doesn't exists in the system");
    }
    /**
     * function to find a orderID by its id
     * @param orderID of the Customer
     * @return the order
     * @throws Exception
     */
    public Order findOrderByID(long orderID)throws Exception {
        for (Order OrderItem : orders) {
            if (OrderItem.getOrderID() == orderID)
                return OrderItem;
        }
        throw new Exception("ERROR: order doesn't exists in the system");
    }

    //other functions
    /**
     * function to get a books' list by a book category
     * @param category of the books
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByCategory(Category category) throws Exception{
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            if (bookItem.getBooksCategory() == category) //check that the books belong to a specific category
                listToReturn.add(new Book(bookItem));
        return listToReturn;
    }
    /**
     * function to get a books' list by a book authors
     * @param authorsName of the books
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByAuthors(String authorsName)throws Exception {
        if (books.size() == 0) //if the list is empty
            throw new Exception("ERROR: the book list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Book> listToReturn = new ArrayList<>();
        for (Book bookItem : books)
            if (bookItem.getAuthor() == authorsName) //check that the books belong to a specific author
                listToReturn.add(new Book(bookItem));
        return listToReturn;
    }
    /**
     * function to get the list of authors
     * @return authorsName of all the books
     * @throws Exception
     */
    @Override
    public ArrayList<String> authorList () throws Exception{
        if (books.size() == 0)//if the list is empty
            throw new Exception("ERROR: the book list is empty");
        ArrayList<String> authorsName = new ArrayList<>();
        String name;
        boolean exist = false;
        for (Book book : books)
        {
            name = book.getBookName();
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
     * function to get a books' list by a dates of publisher
     * @param start date
     * @param end date
     * @return books' list
     */
    @Override
    public ArrayList<Book> bookListSortedByDate(Date start, Date end) throws Exception{
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
    public ArrayList<Book> bookListBySupplier (long supplierID)throws Exception{
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
     * function to get all the suppliers of a specific book
     * @param bookID
     * @return supplier list
     * @throws Exception
     */
    public ArrayList<Supplier> supplierListByBook (long bookID)throws Exception{
        if (supplierAndBooks.size() == 0 ) //if the list is empty
            throw new Exception("ERROR: the list is empty");
        //to prevent aliasing - create a new list and add new books that are equal to the books in the list book
        ArrayList<Supplier> suppliersOfTheBook = new ArrayList<>();
        for (SupplierAndBook supplierAndBook : supplierAndBooks)
        {
            if (supplierAndBook.getBookID() == bookID)
            {
                suppliersOfTheBook.add(new Supplier(findSupplierByID(supplierAndBook.getSupplierID())));
            }
        }
        return suppliersOfTheBook;
    }
    /**
     * function to update the rate of a book
     * @param book to update
     * @throws Exception
     */
    @Override
    public void updateBookRate (Book book)throws Exception
    {
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
        book.setRateAVR(rateSumOfBook / count);
        //update the book
        updateBook(book,getManger().getNumID() , Privilege.MANAGER);
    }
    /**
     * func to get all the copies of a book by suppliers
     * @param bookID
     * @throws Exception
     * @return
     */
    @Override
    public int getNumOfCopiesOfBook (long bookID)throws Exception
    {
        int numOfBookCopies=0;
        findBookByID(bookID);//

        for (SupplierAndBook s:supplierAndBooks)//loop to go on supplier and book list
        {
            if (s.getBookID() == bookID)
                numOfBookCopies += s.getNumOfCopies();//sum all the copies of this book in all suppliers

        }
        return numOfBookCopies;
    }
    /**
     *  func to calc total price from an order and to set payment
     * @param CustomerOrders
     * @throws Exception
     * @return
     */
    @Override
    public double finishOrder (ArrayList<Order> CustomerOrders)throws Exception
    {
        double SumToPay=0;
        for (Order o:CustomerOrders)//loop to go on orders
        {
            SumToPay+=o.getTotalPrice();
            o.setPaid(true);
            updateOrder(o,Privilege.MANAGER);

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
    public void addMoreCopiesToBook (long bookID , int numOfCopies, long supplierID , Privilege privilege) throws Exception
    {
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
     * function to get the num of book copies of a specific supplier
     * @param bookID
     * @param supplierID
     * @return num of copies
     * @throws Exception
     */
    public int getNumOfBookCopiesForSupplier (long bookID ,long supplierID )throws Exception
    {
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
    public double getPriceOfBookForSupplier (long bookID ,long supplierID )throws Exception
    {
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
    public void setPriceOfBookForSupplier (long bookID ,long supplierID ,double price)throws Exception
    {
        for (SupplierAndBook supplierAndBookItem : supplierAndBooks)//loop to go on supplier and book
            if (supplierAndBookItem.getBookID() ==bookID &&supplierAndBookItem.getSupplierID()==supplierID) //check that this supplier and book exists
            {
               supplierAndBookItem.setPrice(price);
                break;
            }
        //the supplier and book does not exist
        throw new Exception("ERROR:there is no supplier that provides this book");
    }
    /**
     * func to update a customer if he is VIP and to give him 10% discount
     * @param order
     * @throws Exception
     */
    @Override
    public void customerVIP (Order order)throws Exception
    {
        int counter=0;
        double totalPrice=0;
        for (Order o : orders)//loop to go on orders
        {
            if (o.getCustomerNumID()==order.getCustomerNumID()) //if this order belong to this customer
            {
                counter++;
                totalPrice+=o.getTotalPrice();
            }
        }
        if (totalPrice/counter>100&&counter>10) {//a customer becomes VIP if he has 10 orders in an average of 100 NIS
            Customer c = findCustomerByID(order.getCustomerNumID());
            //updating the right places
            c.setVIP(true);
            order.setTotalPrice(order.getTotalPrice()*0.9);
            updateOrder(order,Privilege.MANAGER);
            updateCustomer(c,Privilege.MANAGER);
        }


    }
    /**
     *function that return the most ordered book in the store
     * @return book
     * @throws Exception
     */
    @Override
    public Book bookOfTheStore ()throws Exception {
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
        int max =0;
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

    //functions of this class
    /**
     * function to update a supplier and book (num of copies and\or price)
     * @param supplierAndBook for update
     */
    private boolean updateSupplierAndBook (SupplierAndBook supplierAndBook)
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

}

