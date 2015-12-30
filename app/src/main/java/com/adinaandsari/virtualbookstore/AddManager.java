package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Gender;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Privilege;
import com.adinaandsari.virtualbookstore.model.backend.Backend;

public class AddManager extends AppCompatActivity {

    private long id;
    private String name ,address , phoneNumber , email;
    private Gender gender;
    private int yearsInCompany;
    EditText editTextOfID,editTextOfName,editTextOfAddress,editTextOfPhoneNumber,editTextOfEmail,editTextOfYearsInCompany;
    private CheckBox male,female;
    Button addButton;

    //function to find view by id for the view in the activity
    void findView()
    {
        editTextOfID = (EditText)findViewById(R.id.id_edit_text_add_manager);
        editTextOfName = (EditText)findViewById(R.id.name_edit_text_add_manager);
        editTextOfAddress = (EditText)findViewById(R.id.address_edit_text_add_manager);
        editTextOfPhoneNumber = (EditText)findViewById(R.id.phone_number_edit_text_add_manager);
        editTextOfEmail = (EditText)findViewById(R.id.email_edit_text_add_manager);
        editTextOfYearsInCompany = (EditText)findViewById(R.id.years_in_company_edit_text_add_manager);
        male = (CheckBox) findViewById(R.id.male_checkBox_add_manager);
        female = (CheckBox) findViewById(R.id.female_checkBox_add_manager);
        addButton = (Button)findViewById(R.id.add_manager_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manager);
        //function to find views by id
        findView();
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
                    yearsInCompany = Integer.valueOf(editTextOfYearsInCompany.getText().toString());
                    if (yearsInCompany < 0) {
                        throw new Exception("ERROR: years in company most be a positive value");
                    }
                    if (male.isChecked()) {
                        gender = Gender.MALE;
                    } else if (female.isChecked()) {
                        gender = Gender.FEMALE;
                    } else
                        throw new Exception("ERROR: you didn't checked any checkbox ");
                    //try to add the manager
                    Backend backendFactory = com.adinaandsari.virtualbookstore.model.datasource.BackendFactory.getInstance();
                    Manager manager = new Manager(id, name, address, phoneNumber, email, gender,yearsInCompany);
                    backendFactory.addManger(manager, Privilege.MANAGER);
                    Toast.makeText(AddManager.this, "The addition has been successful!", Toast.LENGTH_LONG).show();

                    //go back to the activity
                    Intent intent = new Intent(AddManager.this, ManagerActivity.class);
                    intent.putExtra(ConstValue.MANAGER_KEY, manager);//add the specific manager
                    startActivity(intent);

                } catch (Exception e) {
                    //print the exception in a toast view
                    Toast.makeText(AddManager.this, "Failed to add the manager:\n" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //making sure that only one checkbox can be checked
    public void onCheckBoxClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.male_checkBox_add_manager:
                female.setChecked(false);
                break;
            case R.id.female_checkBox_add_manager:
                male.setChecked(false);
        }
    }

}
