package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_AGE;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_NUMBER;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_PASSWORD;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_USERNAME;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.user.InsertUserRunnable;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ActivityLoginBinding;
import com.example.atm.network.impl.UserServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.utils.Validator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    public static final String PREF_KEY_CURRENT_USER = "current_user";
    private ProgressDialog dialog;
    private UserServiceImpl userService;
    private ActivityLoginBinding binding;
    private PreferencesManager preferencesManager;

    private ExecutorService executorService;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setListeners();
        executorService = Executors.newSingleThreadExecutor();

        preferencesManager = PreferencesManager.getInstance(this);
        userService = new UserServiceImpl();
    }

    private void onSignUpClicked() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void onLoginClicked() {
        removeErrors();

        String username = binding.usernameEt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

        if (isInputValid(username, password)) {
            loginIfAccountExist(username, password);
        }
    }

    private void removeErrors() {
        binding.usernameEt.setError(null);
        binding.passwordEdt.setError(null);
    }

    private boolean isInputValid(String username, String password) {
        boolean isValid = true;

        if (!Validator.isEmailValid(username)) {
            binding.usernameEt.setError(getString(R.string.error_invalid_username));
            isValid = false;
        }

        if (!Validator.isPasswordValid(password)) {
            binding.passwordEdt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        return isValid;
    }

    private void loginIfAccountExist(String username, String password) {
        showDialog();

        int savedAge = preferencesManager.get(PREF_KEY_AGE, 0);
        Number savedNumber = preferencesManager.get(PREF_KEY_NUMBER, 0);

        if (!isInputValid(username, password)) {
            return;
        }
        User inputUser = new User(username, password, savedAge, savedNumber);
        userService.loginUser(inputUser, new ResultListener<User>() {
            @Override
            public void onSuccess(User user) {
                insertUserToDb(user);
                preferencesManager.get(PREF_KEY_USERNAME, username);
                preferencesManager.get(PREF_KEY_PASSWORD, password);
            }

            @Override
            public void onError(Throwable throwable) {
                showLoginError();
            }
        });
    }

    private void login(User user) {
        dialog.dismiss();
        preferencesManager.put(PREF_KEY_IS_LOGIN, true);
        preferencesManager.putObj(PREF_KEY_CURRENT_USER, user);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoginError() {
        dialog.dismiss();
        Toast.makeText(this, R.string.error_invalid_login, Toast.LENGTH_SHORT).show();

    }

    private void showDialog() {
        String dialogTitle = getString(R.string.title_dialog);
        String dialogMessage = getString(R.string.title_dialog_message);
        dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);
    }

    private void setListeners() {
        binding.signUpBtn.setOnClickListener(v -> onSignUpClicked());
        binding.signInBtn.setOnClickListener(v -> onLoginClicked());
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void insertUserToDb(User inputUser) {
        executorService.execute(new InsertUserRunnable(this, inputUser, new ResultListener<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> login(user));
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> showToastMessage(throwable.getMessage()));

            }
        }));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
