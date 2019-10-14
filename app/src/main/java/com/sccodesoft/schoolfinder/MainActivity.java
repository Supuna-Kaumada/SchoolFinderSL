package com.sccodesoft.schoolfinder;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef,schoolsRef;
    String currentuserid;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        schoolsRef = FirebaseDatabase.getInstance().getReference().child("Schools");
        currentuserid = mAuth.getCurrentUser().getUid();

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("School Finder");
        getSupportActionBar().setIcon(R.drawable.ic_school_white_24dp);

        mViewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_settings_option)
        {
            SendUserToSetupActivity();
        }
        if(item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            SendUserToLoginActivity();
        }

        return true;
    }


    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(setupIntent);
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
