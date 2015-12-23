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

import com.adinaandsari.virtualbookstore.entities.Manager;

public class ManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //this manager
    Intent preIntent = getIntent();
    Manager user = (Manager) preIntent.getSerializableExtra(ConstValue.MANAGER_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_manager);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_manager);
        navigationView.setNavigationItemSelectedListener(this);
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
            case R.id.addCustomer:
                intent = new Intent(ManagerActivity.this, AddCustomerSingIn.class);
                break;
            case R.id.removeCustomer:
                intent = new Intent(ManagerActivity.this, RemoveCustomer.class);
                break;
            case R.id.updateCustomer:
                intent = new Intent(ManagerActivity.this, UpdateCustomer.class);
                break;
            case R.id.removeOpinion:
                //intent = new Intent(ManagerActivity.this, .class);
                break;
            case R.id.addManager:
                intent = new Intent(ManagerActivity.this, AddManager.class);
                break;
            case R.id.removeManager:
                //////////////////////////////////////////////
                intent = new Intent(ManagerActivity.this, UpdateManager.class);
                break;
            case R.id.updateManager:
                intent = new Intent(ManagerActivity.this, UpdateManager.class);
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
