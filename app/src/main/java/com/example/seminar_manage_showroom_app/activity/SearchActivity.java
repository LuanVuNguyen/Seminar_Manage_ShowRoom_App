package com.example.seminar_manage_showroom_app.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.seminar_manage_showroom_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SearchActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set the default selected fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new search_info())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.search_info:
                selectedFragment = new search_info();
                break;
            case R.id.search_location:
                selectedFragment = new search_location();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, selectedFragment)
                    .commit();
            return true;
        }
        return false;
    }
}