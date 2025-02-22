package com.example.atm.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.atm.databinding.AcivityProfileBinding;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.atm.R;



public class ProfileActivity  extends AppCompatActivity {

    private AcivityProfileBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = AcivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setUpToolbar();
        setupBottomNavigation();
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24);
        actionBar.setHomeAsUpIndicator(backDrawable);
    }
    private void onProfileClicked() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onTransactionsClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.transactions_btn) {
                onTransactionsClicked();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
