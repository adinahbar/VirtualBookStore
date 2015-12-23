package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.entities.Status;
import com.adinaandsari.virtualbookstore.entities.SupplierType;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

public class AddCustomerSingIn extends AppCompatActivity  {

    private CheckBox male,female;
    private long id;
    private String name ,address , phoneNumber , email;
    private Status status;
    private Gender gender;
    private Date birthday;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_sing_in);

        male = (CheckBox) findViewById(R.id.male_checkBox_add_customer);
        female = (CheckBox) findViewById(R.id.female_checkBox_add_customer);

        //spinner for the status of the customer
        spinner = (Spinner) findViewById(R.id.status_spinner_add_customer);
        String[] statusList = getResources().getStringArray(R.array.status_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,statusList);
        spinner.setAdapter(dataAdapter);

        //add button
        Button addButton = (Button)findViewById(R.id.add_customer_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    EditText editTextOfID = (EditText) findViewById(R.id.id_edit_text_add_customer);
                    id = Long.parseLong(editTextOfID.getText().toString());
                    if ((int) (Math.log10(id) + 1) != 9)
                        throw new Exception("ERROR: ID must contain 9 digits");
                    EditText editTextOfName = (EditText) findViewById(R.id.name_edit_text_add_customer);
                    name = editTextOfName.getText().toString();
                    EditText editTextOfAddress = (EditText) findViewById(R.id.address_edit_text_add_customer);
                    address = editTextOfAddress.getText().toString();
                    EditText editTextOfPhoneNumber = (EditText) findViewById(R.id.phone_number_edit_text_add_customer);
                    phoneNumber = editTextOfPhoneNumber.getText().toString();
                    if (phoneNumber.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: Phone number must contain only digits");
                    EditText editTextOfEmail = (EditText) findViewById(R.id.email_edit_text_add_customer);
                    email = editTextOfEmail.getText().toString();
                    if (email.contains("@") == false) {
                        throw new Exception("ERROR: your email address is wrong");
                    }
                    Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner_add_customer);
                    status = Status.valueOf(spinnerStatus.getSelectedItem().toString().toUpperCase());
                    if (((CheckBox) findViewById(R.id.male_checkBox_add_customer)).isChecked()) {
                        gender = Gender.MALE;
                    } else if (((CheckBox) findViewById(R.id.female_checkBox_add_customer)).isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    EditText editTextOfBirthday = (EditText) findViewById(R.id.birthday_edit_text_add_customer);
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    birthday = df.parse(editTextOfBirthday.getText().toString());
                    Calendar now = Calendar.getInstance();
                    Calendar birthdayDate = Calendar.getInstance();
                    birthdayDate.setTime(birthday);
                    if (birthdayDate.before(now) != true) {
                        throw new Exception("ERROR: your birthday isn't correct");
                    }

                    //try to add a new customer
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Customer customerToAdd = new Customer(id, name, address, phoneNumber, email, gender,
                            birthday, "", status);
                    backendFactory.addCustomer(customerToAdd, Privilege.MANAGER);
                    Toast.makeText(AddCustomerSingIn.this, "Sign in has been successful!\nA mail will be sent to you shortly", Toast.LENGTH_LONG).show();

                    //sent an email
                    /*
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, email);
                    i.putExtra(Intent.EXTRA_SUBJECT, "Success sign in to our virtual book store");
                    i.putExtra(Intent.EXTRA_TEXT, "Congratulation! you entered our costumer club.\nyou're welcome to start shopping :)");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(add_customer.this, "There are no email clients installed\nThe mail won't be sent to you", Toast.LENGTH_LONG).show();
                    }
                    */
                    //go to the next activity
                    //go to the customer activity
                    Intent intent = new Intent(AddCustomerSingIn.this, CustomerActivity.class);
                    intent.putExtra(ConstValue.CUSTOMER_KEY, customerToAdd);//add the specific customer
                    startActivity(intent);

                }
                catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(AddCustomerSingIn.this, "Failed to sign in:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_add_customer:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_add_customer:
                male.setChecked(false);
        }
    }

}
