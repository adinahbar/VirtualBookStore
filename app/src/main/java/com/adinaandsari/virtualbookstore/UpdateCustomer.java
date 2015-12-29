package com.adinaandsari.virtualbookstore;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Status;
import com.adinaandsari.virtualbookstore.model.backend.Backend;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateCustomer extends AppCompatActivity {

    //this user
    Manager user;

    private long id;
    private String name ,address , phoneNumber , email;
    private Status status;
    private Gender gender;
    private Date birthday;
    private String creditCard;
    private boolean vip;
    private Spinner idSpinner , statusSpinner;
    EditText editTextOfName,editTextOfAddress,editTextOfPhoneNumber,editTextOfEmail,editTextOfBirthday,editTextOfCreditCard;
    CheckBox vipCheckBox;
    private CheckBox male,female;
    Button updateButton;

    //function to find view by id for the view in the activity
    void findView()
    {
        editTextOfName = (EditText)findViewById(R.id.name_edit_text_update_customer);
        editTextOfAddress = (EditText)findViewById(R.id.address_edit_text_update_customer);
        editTextOfPhoneNumber = (EditText)findViewById(R.id.phone_number_edit_text_update_customer);
        editTextOfEmail = (EditText)findViewById(R.id.email_edit_text_update_customer);
        editTextOfBirthday = (EditText)findViewById(R.id.birthday_edit_text_update_customer);
        editTextOfCreditCard = (EditText)findViewById(R.id.numOfCreditCard_edit_text_update_customer);
        vipCheckBox = (CheckBox)findViewById(R.id.vip_checkBox_update_customer);
        idSpinner = (Spinner)findViewById(R.id.id_spinner_update_customer);
        statusSpinner = (Spinner)findViewById(R.id.status_spinner_update_customer);
        male = (CheckBox) findViewById(R.id.male_checkBox_update_customer);
        female = (CheckBox) findViewById(R.id.female_checkBox_update_customer);
        updateButton = (Button)findViewById(R.id.update_customer_button_update_customer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer);

        //this user
        Intent preIntent = getIntent();
        user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);

        //function to find views by id
        findView();

        //spinner for the status of the customer
        String[] statusList = getResources().getStringArray(R.array.status_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,statusList);
        statusSpinner.setAdapter(dataAdapter);

        //try to load the data of the selected customer
        try {
            //spinner for the id of the customer
            String[] customersID = getCustomerID();
            ArrayAdapter<String> dataIDAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,customersID);
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
            Toast.makeText(UpdateCustomer.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        //update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    id = Long.parseLong(idSpinner.getSelectedItem().toString());
                    name = editTextOfName.getText().toString();
                    address = editTextOfAddress.getText().toString();
                    phoneNumber = editTextOfPhoneNumber.getText().toString();
                    if (phoneNumber.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: Phone number must contain only digits");
                    email = editTextOfEmail.getText().toString();
                    if (email.contains("@") == false) {
                        throw new Exception("ERROR: your email address is wrong");
                    }
                    status = Status.valueOf(statusSpinner.getSelectedItem().toString().toUpperCase());
                    if (male.isChecked()) {
                        gender = Gender.MALE;
                    } else if (female.isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    birthday = df.parse(editTextOfBirthday.getText().toString());
                    Calendar now = Calendar.getInstance();
                    Calendar birthdayDate = Calendar.getInstance();
                    birthdayDate.setTime(birthday);
                    if (birthdayDate.before(now) != true) {
                        throw new Exception("ERROR: your birthday isn't correct");
                    }
                    creditCard =  editTextOfCreditCard.getText().toString();
                    if (creditCard.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: credit card number must contain only digits");
                    vip = vipCheckBox.isChecked();
                    //try to update the customer
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Customer customerToUpdate = new Customer(id, name, address, phoneNumber, email, gender,
                            birthday, creditCard, status);
                    customerToUpdate.setVIP(vip);
                    backendFactory.updateCustomer(customerToUpdate, user.getPrivilege());
                    Toast.makeText(UpdateCustomer.this, "The update has been successful!", Toast.LENGTH_LONG).show();

                    //go back to the activity
                    Intent intent = new Intent(UpdateCustomer.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, user);
                    startActivity(intent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(UpdateCustomer.this, "Failed to update the customer:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //function to enter the values of the selected customer by its ID
    void enterDetail()
    {
        try {
            Customer customer = BackendFactory.getInstance().findCustomerByID(Long.parseLong(idSpinner.getSelectedItem().toString()));
            editTextOfName.setText(customer.getName());
            editTextOfAddress.setText(customer.getAddress());
            editTextOfPhoneNumber.setText(customer.getPhoneNumber());
            editTextOfEmail.setText(customer.getEmailAddress());
            editTextOfBirthday.setText(customer.getBirthDay().toString());
            editTextOfCreditCard.setText(customer.getNumOfCreditCard());
            vipCheckBox.setChecked(customer.isVIP());
            switch (customer.getGender())
            {
                case MALE:
                    male.setChecked(true);
                    break;
                case FEMALE:
                    female.setChecked(true);
                    break;
            }
            String statusString = customer.getStatus().toString().toLowerCase(Locale.ENGLISH);
            statusSpinner.setSelection(((ArrayAdapter<String>)statusSpinner.getAdapter()).getPosition(statusString));
        }
        catch (Exception e) {
            //print the exception in a toast view
            Toast.makeText(UpdateCustomer.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //function to return all the customers id
    String[] getCustomerID ()throws Exception
    {
        ArrayList<Customer> customerArrayList = BackendFactory.getInstance().getCustomerList(user.getPrivilege());
        String[] id = new String[ customerArrayList.size()] ;
        int i = 0;
        for (Customer c : customerArrayList)
        {
            id[i] =((Long)c.getNumID()).toString();
            i++;
        }
        return id;
    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_update_customer:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_update_customer:
                male.setChecked(false);
        }
    }
}
