package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.entities.SupplierType;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

import java.util.Locale;

public class AddSupplier extends AppCompatActivity {
    private CheckBox male,female;
    private long id;
    private String name ,address , phoneNumber , email , customerServiceNum,reservationNum;
    private Gender gender;
    private SupplierType supplierType;
    private Spinner supplierTypeSpinner;
    EditText editTextOfID,editTextOfName,editTextOfAddress,editTextOfPhoneNumber,editTextOfEmail,editTextOfCustomerServiceNum,editTextOfReservationNum;
    Button addButton;

    //this user
    Intent preIntent = getIntent();
    Manager user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);

    //function to find view by id for the view in the activity
    void findView()
    {
        editTextOfID = (EditText)findViewById(R.id.id_edit_text_add_supplier);
        editTextOfName = (EditText)findViewById(R.id.name_edit_text_add_supplier);
        editTextOfAddress = (EditText)findViewById(R.id.address_edit_text_add_supplier);
        editTextOfPhoneNumber = (EditText)findViewById(R.id.phone_number_edit_text_add_supplier);
        editTextOfEmail = (EditText)findViewById(R.id.email_edit_text_add_supplier);
        editTextOfCustomerServiceNum = (EditText)findViewById(R.id.customer_service_num_edit_text_add_supplier);
        editTextOfReservationNum = (EditText)findViewById(R.id.reservation_num_edit_text_add_supplier);
        supplierTypeSpinner = (Spinner)findViewById(R.id.type_spinner_add_supplier);
        male = (CheckBox) findViewById(R.id.male_checkBox_add_supplier);
        female = (CheckBox) findViewById(R.id.female_checkBox_add_supplier);
        addButton = (Button)findViewById(R.id.add_supplier_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//find view
        findView();
        //spinner for the type of the supplier
        String[] typeList = getResources().getStringArray(R.array.supplier_type_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,typeList);
        supplierTypeSpinner.setAdapter(dataAdapter);
        //add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get the values and validation check of input
                    id = Long.parseLong(editTextOfID.getText().toString());
                    if ((int) (Math.log10(id) + 1) != 9)
                        throw new Exception("ERROR: ID must contain 9 digits");
                    name = editTextOfName.getText().toString();
                    address = editTextOfAddress.getText().toString();
                    phoneNumber = editTextOfPhoneNumber.getText().toString();
                    if (phoneNumber.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: Phone number must contain only digits");
                    email = editTextOfEmail.getText().toString();
                    if (email.contains("@") == false) {
                        throw new Exception("ERROR: your email address is wrong");
                    }
                    customerServiceNum = editTextOfCustomerServiceNum.getText().toString();
                    if (customerServiceNum.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR: customer reservation number must contain only digits");
                    reservationNum = editTextOfReservationNum.getText().toString();
                    if (reservationNum.contains("[a-zA-Z]+") != false)
                        throw new Exception("ERROR:reservation number must contain only digits");
                    supplierType = SupplierType.valueOf(supplierTypeSpinner.getSelectedItem().toString().toUpperCase());
                    if (male.isChecked()) {
                        gender = Gender.MALE;
                    } else if (female.isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    //try to update the supplier
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Supplier supplierToAdd = new Supplier(id, name, address, phoneNumber, email, gender,
                            customerServiceNum, reservationNum, supplierType);
                    backendFactory.updateSupplier(supplierToAdd, user.getPrivilege());
                    Toast.makeText(AddSupplier.this, "The addition has been successful!", Toast.LENGTH_LONG).show();

                    //go back to the activity
                    Intent intent = new Intent(AddSupplier.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, user);
                    startActivity(intent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(AddSupplier.this, "Failed to add the supplier:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_add_supplier:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_add_supplier:
                male.setChecked(false);
        }
    }


}
