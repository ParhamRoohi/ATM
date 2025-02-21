package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_IS_LOGIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.atm.R;
import com.example.atm.databinding.ActivityMainBinding;
import com.example.atm.preferences.PreferencesManager;

public class MainActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setListeners();
        setSupportActionBar(binding.toolbar);

        preferencesManager = PreferencesManager.getInstance(this);
    }


    private void setListeners() {
        binding.profileBtn.setOnClickListener(v -> onProfileClicked());
        binding.transactionsBtn.setOnClickListener(v -> onTransactionsClicked());
    }


    private void onProfileClicked() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onTransactionsClicked() {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        preferencesManager.put(PREF_KEY_IS_LOGIN, false);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}