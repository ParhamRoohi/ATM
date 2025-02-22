package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_CURRENT_USER;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static com.example.atm.preferences.PreferencesManager.preferencesManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.trasaction.DeleteTransactionRunnable;
import com.example.atm.data.db.runnables.trasaction.GetTransactionByUserIdRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
import com.example.atm.databinding.AcivityProfileBinding;
import com.example.atm.network.impl.TransactionServiceImpl;
import com.example.atm.network.impl.UserServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.ui.adapter.TransactionAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ProfileActivity extends AppCompatActivity {
    private ExecutorService executorService;
    private TransactionAdapter transactionAdapter;
    private AcivityProfileBinding binding;
    private TransactionServiceImpl transactionService;

    private UserServiceImpl userService;
    private User user;
    private Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = AcivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        executorService = Executors.newSingleThreadExecutor();
        transactionService = new TransactionServiceImpl();
        userService = new UserServiceImpl();
        preferencesManager = PreferencesManager.getInstance(this);


        setSupportActionBar(binding.toolbar);
        setProfile();
        setUpRecyclerView();
        getTransactionByUserId();

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

    private void setProfile() {
        user = preferencesManager.getObj(PREF_KEY_CURRENT_USER, User.class);
        binding.profileTv.setText(user.toString());
    }


    private void setUpRecyclerView() {

        transactionAdapter = new TransactionAdapter(this, new TransactionAdapter.TransactionAdapterCallback() {
            @Override
            public void onItemLongClicked(Transaction transaction) {
                openDeleteDialog(transaction);
            }
        });
        binding.transactionRv.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionRv.setAdapter(transactionAdapter);
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

    private void getTransactionByUserId() {
        executorService.execute(
                new GetTransactionByUserIdRunnable(getApplicationContext(), user.getId(),
                        new ResultListener<List<Transaction>>() {
                            @Override
                            public void onSuccess(List<Transaction> dbTransaction) {
                                runOnUiThread(() -> {
                                    transactionAdapter.update(dbTransaction);
                                });
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProfileActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                });
                            }
                        })
        );
    }

    private void openDeleteDialog(Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_report));
        builder.setMessage(getString(R.string.are_you_sure));
        builder.setPositiveButton(R.string.yes, (dialog, which) -> deleteTransaction(transaction));
        builder.setNegativeButton(R.string.no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTransaction(Transaction transaction) {
        transactionService.deleteTransaction(transaction, new ResultListener<Transaction>() {
            @Override
            public void onSuccess(Transaction transaction) {
                executorService.execute(
                        new DeleteTransactionRunnable(getApplicationContext(), transaction, new ResultListener<Transaction>() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                runOnUiThread(() -> {
                                    transactionAdapter.remove(transaction);
                                    Toast.makeText(ProfileActivity.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProfileActivity.this, "Failed to delete transaction: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        })
                );
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void logout() {
        User currentUser = preferencesManager.getObj(PREF_KEY_CURRENT_USER, User.class);
        if (currentUser == null) {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getId();

        userService.logout(userId, new ResultListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                runOnUiThread(() -> {
                    preferencesManager.put(PREF_KEY_IS_LOGIN, false);
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Logout failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

}
