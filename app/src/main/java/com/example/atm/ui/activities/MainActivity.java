package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_TOKEN;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.trasaction.GetTransactionRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ActivityMainBinding;
import com.example.atm.network.impl.UserServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.ui.adapter.RecentAdapter;
import com.example.atm.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private ActivityMainBinding binding;

    private RecentAdapter adapter;
    private List<Transaction> transactions = new ArrayList<>();
    private Executor executorService;
    private UserServiceImpl userService;
    private Dialog dialog;

    private final ActivityResultLauncher<Intent> newReportActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Transaction transaction = (Transaction) result.getData().getSerializableExtra(Constant.KEY_TRANSACTION);
                        adapter.itemInsertedOnTop(transaction);
                        binding.recentRv.smoothScrollToPosition(0);
                    } else {
                        Log.i("new", "failed to find new report");
                    }
                }
            });

    private final ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    getRecentTransactions();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        preferencesManager = PreferencesManager.getInstance(this);
        userService = new UserServiceImpl();
        executorService = Executors.newSingleThreadExecutor();

        setListener();
        setUpRecyclerView();
        setupBottomNavigation();
        getRecentTransactions();

    }

    private void setListener() {
        binding.addBtn.setOnClickListener(v -> openAddDialog(0));
    }


    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.profile_btn) {
                onProfileClicked();
                return true;
            }
            return false;
        });
    }

    private void setUpRecyclerView() {
        adapter = new RecentAdapter(this, new RecentAdapter.RecentAdapterCallback() {
            @Override
            public void onItemClicked(Transaction transaction) {
                goToPreviewScreen(transaction);
            }

        });
        binding.recentRv.setLayoutManager(new LinearLayoutManager(this));
        binding.recentRv.setAdapter(adapter);
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
                preferencesManager.put(PREF_KEY_TOKEN,"");
            } else if (selectedType.equals("CTC")) {
                navigateToAddTransactionForm("CTC");
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToPreviewScreen(Transaction transaction) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("TRANSACTION_AMOUNT", transaction.getAmount());
        intent.putExtra("TRANSACTION_TYPE", transaction.getTransactionType());
        intent.putExtra("IS_PREVIEW", true);
        intent.putExtra(Constant.KEY_TRANSACTION, transaction);
        newReportActivityResultLauncher.launch(intent);
    }

    private void navigateToAddTransactionForm(String transactionType) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("transaction_type", transactionType);
        newReportActivityResultLauncher.launch(intent);
    }


    private void onProfileClicked() {
        Intent intent = new Intent(this, ProfileActivity.class);
        profileActivityResultLauncher.launch(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
        getRecentTransactions();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getRecentTransactions() {
        executorService.execute(
                new GetTransactionRunnable(getApplicationContext(), new ResultListener<List<Transaction>>() {
                    @Override
                    public void onSuccess(List<Transaction> dbTransaction) {
                        runOnUiThread(() -> {
                            transactions.clear();
                            transactions.addAll(dbTransaction);
                            adapter.update(dbTransaction);
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                })
        );
    }

    private void logout() {
        User currentUser = preferencesManager.getObj(PreferencesManager.PREF_KEY_CURRENT_USER, User.class);
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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Logout failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}