package com.adinaandsari.virtualbookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adinaandsari.virtualbookstore.entities.Customer;
import com.adinaandsari.virtualbookstore.entities.Manager;
import com.adinaandsari.virtualbookstore.entities.Supplier;
import com.adinaandsari.virtualbookstore.model.datasource.BackendFactory;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //this manager
    Manager user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this manager
        Intent preIntent = getIntent();
        user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);

        //navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_manager);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_manager);
        navigationView.setNavigationItemSelectedListener(this);

        //the name text view
        TextView name = (TextView) findViewById(R.id.name_of_manager_textView_manager_activity);
        String text = "Dear " + user.getName() +
                "\nYou are invited to control all of your customers and suppliers!";
        name.setText(text);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_manager);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.addSupplier:
                intent = new Intent(ManagerActivity.this, AddSupplier.class);
                break;
            case R.id.removeSupplier:
                intent = new Intent(ManagerActivity.this, RemoveSupplier.class);
                break;
            case R.id.updateSupplier:
                intent = new Intent(ManagerActivity.this, UpdateSupplier.class);
                break;
            case R.id.listOfSuppliers:
                try {
                    ArrayList<Supplier> suppliers = BackendFactory.getInstance().getSupplierList();
                    intent = new Intent(ManagerActivity.this, ListOfPerson.class);
                    intent.putExtra(ConstValue.PERSON_KEY, suppliers);
                }catch (Exception e){
                    //print the exception in a toast view
                    Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.removeCustomer:
                intent = new Intent(ManagerActivity.this, RemoveCustomer.class);
                break;
            case R.id.updateCustomer:
                intent = new Intent(ManagerActivity.this, UpdateCustomer.class);
                break;
            case R.id.listOfCustomers:
                try {
                    ArrayList<Customer> customers = BackendFactory.getInstance().getCustomerList(user.getPrivilege());
                    intent = new Intent(ManagerActivity.this, ListOfPerson.class);
                    intent.putExtra(ConstValue.PERSON_KEY, customers);
                }catch (Exception e){
                    //print the exception in a toast view
                    Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.removeOpinion:
                intent = new Intent(ManagerActivity.this, RemoveOpinion.class);
                break;
            case R.id.updateManager:
                intent = new Intent(ManagerActivity.this, UpdateManager.class);
                break;
            case R.id.removeManager:
                intent = new Intent(ManagerActivity.this, RemoveManager.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.putExtra(ConstValue.MANAGER_KEY, user);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_manager);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
