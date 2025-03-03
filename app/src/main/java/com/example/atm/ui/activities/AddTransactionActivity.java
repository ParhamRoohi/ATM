package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_TOKEN;
import static com.example.atm.ui.activities.LoginActivity.PREF_KEY_CURRENT_USER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.trasaction.InsertTransactionRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ActivityAddTransactionBinding;
import com.example.atm.network.impl.TransactionServiceImpl;
import com.example.atm.network.impl.UserServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.utils.Constant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddTransactionActivity extends AppCompatActivity {
    private ActivityAddTransactionBinding binding;
    private PreferencesManager preferencesManager;
    private Executor executorService;
    private UserServiceImpl userService;
    private Transaction transaction;
    private FloatingActionButton shareBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userService = new UserServiceImpl();
        setUpToolbar();
        executorService = Executors.newSingleThreadExecutor();
        preferencesManager = PreferencesManager.getInstance(this);
        User currentUser = preferencesManager.getObj(PREF_KEY_CURRENT_USER, User.class);
        if (currentUser == null) {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }
        transaction = (Transaction) getIntent().getSerializableExtra(Constant.KEY_TRANSACTION);

        boolean isPreview = getIntent().getBooleanExtra("IS_PREVIEW", false);
        if (isPreview) {
            shareBtn = binding.shareBtn;
            shareBtn.setVisibility(View.VISIBLE);
            loadTransactionForPreview();
        } else {
            String transactionType = getIntent().getStringExtra("transaction_type");
            if (transactionType != null) {
                configureFormForTransactionType(transactionType);
            }
        }
        setListeners(currentUser);
    }


    private void loadTransactionForPreview() {
        String transactionAmount = getIntent().getStringExtra("TRANSACTION_AMOUNT");
        String transactionType = getIntent().getStringExtra("TRANSACTION_TYPE");
        Transaction transaction = (Transaction) getIntent().getSerializableExtra(Constant.KEY_TRANSACTION);

        binding.balanceEt.setText(transactionAmount);
        binding.balanceEt.setEnabled(false);

        configureFormForTransactionType(transactionType);

        binding.originAccountEt.setEnabled(false);
        binding.destinationCardEt.setEnabled(false);
        binding.cvv2Et.setEnabled(false);
        binding.expirationDateEt.setEnabled(false);
        binding.passwordEt.setEnabled(false);
        binding.saveBtn.setVisibility(View.GONE);

        if (transaction != null) {
            binding.originAccountEt.setText(transaction.getCardNumber());
            binding.passwordTv.setVisibility(View.GONE);
            binding.passwordEt.setVisibility(View.GONE);
            binding.balanceEt.setText(String.valueOf(transaction.getAmount()));
            binding.cvv2Et.setText(transaction.getCvv2());
            binding.expirationDateEt.setText(transaction.getExpirationDate().toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureFormForTransactionType(String transactionType) {
        switch (transactionType) {
            case "Withdraw":
                binding.passwordEt.setVisibility(View.VISIBLE);
                binding.originAccountEt.setVisibility(View.GONE);
                binding.destinationCardEt.setVisibility(View.GONE);
                binding.destinationCardTv.setVisibility(View.GONE);
                binding.cvv2Tv.setVisibility(View.GONE);
                binding.expirationDateTv.setVisibility(View.GONE);
                binding.originAccountTv.setVisibility(View.GONE);
                binding.cvv2Et.setVisibility(View.GONE);
                binding.expirationDateEt.setVisibility(View.GONE);
                break;

            case "CTC":
                binding.passwordEt.setVisibility(View.GONE);
                binding.originAccountTv.setVisibility(View.VISIBLE);
                binding.originAccountEt.setVisibility(View.VISIBLE);
                binding.destinationCardTv.setVisibility(View.VISIBLE);
                binding.destinationCardEt.setVisibility(View.VISIBLE);
                binding.cvv2Et.setVisibility(View.VISIBLE);
                binding.passwordTv.setVisibility(View.GONE);
                binding.expirationDateTv.setVisibility(View.VISIBLE);
                binding.expirationDateEt.setVisibility(View.VISIBLE);
                break;

        }

    }

    private void shareTransactionText() {
        if (transaction == null) {
            Toast.makeText(this, "Transaction data not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String amount = String.valueOf(transaction.getAmount());
        String originCard = transaction.getCardNumber();
        String transactionType = transaction.getTransactionType();

        String text = "Transaction Details:\n" +
                "Amount" + amount + "\n" +
                "OriginCard" + originCard + "\n" +
                "TransactionType" + transactionType;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share Transaction"));
    }


    private void setListeners(User currentUser) {
        boolean isPreview = getIntent().getBooleanExtra("IS_PREVIEW", false);
        if (!isPreview) {
            binding.saveBtn.setOnClickListener(v -> onSaveClicked(currentUser));
        } else {
            binding.saveBtn.setVisibility(View.GONE);
            shareBtn.setOnClickListener(v -> shareTransactionText());
        }

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

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onSaveClicked(User currentUser) {
        String transactionType = getIntent().getStringExtra("transaction_type");

        if (!validateInputs(transactionType)) {
            showToastMessage("Please fill all required fields");
            return;
        }

        String accountNumber = binding.originAccountEt.getText().toString().trim();
        String cardNumber = binding.destinationCardEt.getText().toString().trim();
        String cvv2 = binding.cvv2Et.getText().toString().trim();
        String enterBalanceStr = binding.balanceEt.getText().toString().trim();
        String enterPassword = binding.passwordEt.getText().toString().trim();
        String expirationDateStr = binding.expirationDateEt.getText().toString().trim();

        Date transactionDate = new Date();


        Long enterBalance = 0L;
        try {
            enterBalance = Long.parseLong(enterBalanceStr);
        } catch (NumberFormatException e) {
            binding.balanceEt.setError(getString(R.string.error_invalid_balance));
            return;
        }

        Long currentBalance = 0L;
        if (currentUser != null) {
            currentBalance = currentUser.getCurrentBalance();
        }

        if (enterBalance > currentBalance) {
            showToastMessage("Error: Insufficient balance for this transaction");
            return;
        }

        if (transactionType.equals("Withdraw")) {
            String currentPassword = currentUser.getPassword();
            if (!currentPassword.equals(enterPassword)) {
                showToastMessage("Error: Incorrect password");
                return;
            }
        } else if (transactionType.equals("CTC")) {
            if (expirationDateStr.isEmpty()) {
                binding.expirationDateEt.setError("Expiration date is required");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
            dateFormat.setLenient(false);

            try {
                Date expirationDate = dateFormat.parse(expirationDateStr);
                Date currentDate = new Date();

                if (expirationDate.before(currentDate)) {
                    binding.expirationDateEt.setError("Expiration date cannot be in the past");
                    return;
                }
            } catch (ParseException e) {
                binding.expirationDateEt.setError("Invalid date format (expected: yy/MM/dd)");
                return;
            }


            String currentCvv2 = currentUser.getCvv2();
            if (!currentCvv2.equals(cvv2)) {
                showToastMessage("Error: Incorrect CVV2");
                return;
            }
        }

        Long updatedBalance = currentBalance - enterBalance;
        preferencesManager.putObj(PREF_KEY_CURRENT_USER, currentUser);
        Transaction transaction = new Transaction(
                accountNumber,
                cardNumber,
                currentUser.getId(),
                cvv2,
                currentUser.getExpirationDate(),
                currentBalance,
                currentUser.getSessionToken(),
                transactionType,
                transactionDate
        );


        TransactionServiceImpl transactionService = new TransactionServiceImpl();
        transactionService.insertTransaction(transaction, new ResultListener<Transaction>() {
            @Override
            public void onSuccess(Transaction transaction) {

                updateUserBalanceOnServer(currentUser, updatedBalance);

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
            String password = binding.passwordEt.getText().toString().trim();
            return !password.isEmpty();
        } else if ("CTC".equals(transactionType)) {
            String originAccount = binding.originAccountEt.getText().toString().trim();
            String destinationCard = binding.destinationCardEt.getText().toString().trim();
            String cvv2 = binding.cvv2Et.getText().toString().trim();
            String expirationDate = binding.expirationDateEt.getText().toString().trim();

            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
                dateFormat.parse(expirationDate);
            } catch (ParseException e) {
                binding.expirationDateEt.setError(getString(R.string.error_invalid_date_format));
                return false;
            }

            return !originAccount.isEmpty() && !destinationCard.isEmpty() && !cvv2.isEmpty() && !expirationDate.isEmpty();
        }
        return false;
    }

    private void updateUserBalanceOnServer(User currentUser, Long updatedBalance) {
        String sessionToken = preferencesManager.get(PREF_KEY_TOKEN, "");
        if (sessionToken.isEmpty()) {
            Toast.makeText(this, "Session token is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setCurrentBalance(updatedBalance);
        currentUser.setSessionToken(sessionToken);
        userService.updateUserBalance(currentUser, currentUser.getId(), new ResultListener<User>() {
            @Override
            public void onSuccess(User currentUser) {
                runOnUiThread(() -> {
                    Toast.makeText(AddTransactionActivity.this, "Balance updated successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    if (throwable.getMessage().contains("Session token expired")) {
                        Toast.makeText(AddTransactionActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTransactionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Failed to update balance: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
