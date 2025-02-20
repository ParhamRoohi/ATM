package com.example.atm.ui.activities;

import static com.example.atm.ui.activities.LoginActivity.PREF_KEY_CURRENT_USER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.trasaction.InsertTransactionRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ActivityAddTransactionBinding;
import com.example.atm.network.impl.TransactionServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

public class AddTransactionActivity extends AppCompatActivity {
    private ActivityAddTransactionBinding binding;
    private PreferencesManager preferencesManager;
    private Executor executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setUpToolbar();
        setListeners();
        preferencesManager = PreferencesManager.getInstance(this);
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
        binding.saveBtn.setOnClickListener(v -> onSaveClicked());

    }

    private void onAddClicked() {
        openAddDialog(0);
    }

    private void insertTransactionToDb(Transaction transaction) {
        executorService.execute(new InsertTransactionRunnable(getApplicationContext(), transaction, new ResultListener<Transaction>() {
            @Override
            public void onSuccess(Transaction transaction) {
                runOnUiThread(() -> {
                    Toast.makeText(AddTransactionActivity.this, "Transaction saved locally", Toast.LENGTH_SHORT).show();
                    setResult(transaction);
                });
            }

            @Override
            public void onError(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddTransactionActivity.this, "Error saving transaction locally: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }));
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

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onSaveClicked() {
        String transactionType = getIntent().getStringExtra("transaction_type");

        if (!validateInputs(transactionType)) {
            showToastMessage("Please fill all required fields");
            return;
        }

//      String phoneNumber = binding.destinationCardEt.getText().toString().trim();; e
        String accountNumber = binding.originAccountEt.getText().toString().trim();
        ;
        String cardNumber = binding.destinationCardEt.getText().toString().trim();
        String cvv2 = binding.cvv2Et.getText().toString().trim();
        String expirationDateStr = binding.expirationDateEt.getText().toString().trim();
//        Double Balance = preferencesManager.get(PreferencesManager.PREF_KEY_CURRENT_BALANCE, 0.0); ;
//        String sessionToken = "USER_SESSION_TOKEN";

        User currentUser = preferencesManager.getObj(PREF_KEY_CURRENT_USER, User.class);
        Date expirationDate = null;
        Double currentBalance = 0.0;
        if (currentUser != null) {
            currentBalance = currentUser.getCurrentBalance();
        }
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
            expirationDate = dateFormat.parse(expirationDateStr);
        } catch (ParseException e) {
            binding.expirationDateEt.setError(getString(R.string.error_invalid_date_format));
        }
        assert currentUser != null;
        Transaction transaction = new Transaction(
                currentUser.getPhoneNumber(),
                accountNumber,
                cardNumber,
                currentUser.getId(),
                cvv2,
                expirationDate,
                currentBalance,
                currentUser.getSessionToken(),
                transactionType
        );

        TransactionServiceImpl transactionService = new TransactionServiceImpl();
        transactionService.insertTransaction(transaction, new ResultListener<Transaction>() {
            @Override
            public void onSuccess(Transaction transaction) {
//                showToastMessage("Transaction saved successfully");
                insertTransactionToDb(transaction);
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage("Error saving transaction: " + throwable.getMessage());
            }
        });
    }

    private void setResult(Transaction transaction) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_TRANSACTION, transaction);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateInputs(String transactionType) {
        if ("Withdraw".equals(transactionType)) {
            String balance = binding.balanceEt.getText().toString().trim();
            String password = binding.passwordEt.getText().toString().trim();
            return !password.isEmpty()&& !balance.isEmpty();
        } else if ("CTC".equals(transactionType)) {
            String originAccount = binding.originAccountEt.getText().toString().trim();
            String destinationCard = binding.destinationCardEt.getText().toString().trim();
            String cvv2 = binding.cvv2Et.getText().toString().trim();
            String expirationDate = binding.expirationDateEt.getText().toString().trim();

            return !originAccount.isEmpty() && !destinationCard.isEmpty() && !cvv2.isEmpty() && !expirationDate.isEmpty();
        }
        return false;
    }
}
