package com.example.atm.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.atm.databinding.ActivityAddTransactionBinding;


import com.example.atm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddTransactionActivity extends AppCompatActivity {
   private ActivityAddTransactionBinding binding;


   @Override
   protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

       binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
       View view = binding.getRoot();
       setContentView(view);
       setUpToolbar();
       setListeners();

       String transactionType = getIntent().getStringExtra("transaction_type");

       if (transactionType != null) {
           configureFormForTransactionType(transactionType);
       }
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

    private void configureFormForTransactionType(String transactionType) {
        switch (transactionType) {
            case "Withdraw":
                binding.passwordEt.setVisibility(View.VISIBLE);
                binding.originAccountEt.setVisibility(View.GONE);
                binding.destinationCardEt.setVisibility(View.GONE);
                binding.cvv2Et.setVisibility(View.GONE);
                binding.expirationDateEt.setVisibility(View.GONE);
                break;

            case "CTC":
                binding.passwordEt.setVisibility(View.GONE);
                binding.originAccountEt.setVisibility(View.VISIBLE);
                binding.destinationCardEt.setVisibility(View.VISIBLE);
                binding.cvv2Et.setVisibility(View.VISIBLE);
                binding.expirationDateEt.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListeners() {
        binding.addBtn.setOnClickListener(v -> onAddClicked());
    }
    private void onAddClicked() {
        openAddDialog(0);
    }

private void openAddDialog(int position) {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_transaction_type, null);

    Spinner spinner = dialogView.findViewById(R.id.spinner_transaction_type);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.choose_type_transaction));
    builder.setView(dialogView);

    builder.setPositiveButton(R.string.accept, (dialog, which) -> {
        String selectedType = spinner.getSelectedItem().toString();

        if (selectedType.equals("Withdraw")) {
            navigateToAddTransactionForm("Withdraw");
        } else if (selectedType.equals("CTC")) {
            navigateToAddTransactionForm("CTC");
        }
    });

    builder.setNegativeButton(R.string.cancel, null);

    AlertDialog dialog = builder.create();
    dialog.show();
}
    private void navigateToAddTransactionForm(String transactionType) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("transaction_type", transactionType);
        startActivity(intent);
    }
}
