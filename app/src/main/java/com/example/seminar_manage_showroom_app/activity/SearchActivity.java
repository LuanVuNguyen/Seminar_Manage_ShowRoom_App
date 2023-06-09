package com.example.seminar_manage_showroom_app.activity;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.HttpRfidResponse;
import com.example.seminar_manage_showroom_app.common.Constants;
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
                .replace(R.id.fragmentContainerView, new search_info(this))
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.search_info:
                selectedFragment = new search_info(this);
                break;
            case R.id.search_location:
                if (Constants.CONFIG_DEVICE_NAME == "ATS100-SG UHF Reader")
                {
                    Toast.makeText(this, "Please Connect Toshiba Tec", Toast.LENGTH_SHORT).show();
                }
                else if (Constants.CONFIG_DEVICE_NAME == "Toshiba Tec")
                {
                    selectedFragment = new search_location(this);
                }
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