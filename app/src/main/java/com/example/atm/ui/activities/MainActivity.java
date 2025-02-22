package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_IS_LOGIN;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.trasaction.GetTransactionRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.databinding.ActivityMainBinding;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupBottomNavigation();
        setListener();
        setSupportActionBar(binding.toolbar);
        executorService = Executors.newSingleThreadExecutor();
        preferencesManager = PreferencesManager.getInstance(this);
        setUpRecyclerView();
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
        binding.recentRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentAdapter(this);
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
        newReportActivityResultLauncher.launch(intent);
    }


    private void onProfileClicked() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void onTransactionsClicked() {
        Intent intent = new Intent(this, MainActivity.class);
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


}