package com.example.atm.ui.activities;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_AGE;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_PHONE_NUMBER;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_PASSWORD;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_USERNAME;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ActivitySignupBinding;
import com.example.atm.network.impl.UserServiceImpl;
import com.example.atm.preferences.PreferencesManager;
import com.example.atm.utils.Validator;


public class SignUpActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private PreferencesManager preferencesManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setListeners();
        setUpToolbar();

        preferencesManager = PreferencesManager.getInstance(this);
    }

    private User createUserObjectFromInputs() {
        String username = binding.usernameEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();
        String accountNumber = binding.accountNumberEt.getText().toString().trim();
        String cardNumber = binding.cardNumberEt.getText().toString().trim();
        String cvv2 = binding.cvv2Et.getText().toString().trim();
        String expire = binding.expireDateEt.getText().toString().trim();
        Double balance = Double.parseDouble(binding.currentBalanceEt.getText().toString().trim());

        int age = 0;
        String phoneNumber = "";

        try {
            age = Integer.parseInt(binding.ageEt.getText().toString().trim());
        } catch (NumberFormatException e) {
            binding.ageEt.setError(getString(R.string.error_format_age));
        }

        try {
            phoneNumber = binding.numberEt.getText().toString().trim();
        } catch (NumberFormatException e) {
            binding.numberEt.setError(getString(R.string.error_format_number));
        }

        return new User(username, password, age, phoneNumber, accountNumber, cardNumber, cvv2,expire,balance);
    }

    private boolean isUserValid() {
        boolean isValid = true;

        String username = binding.usernameEt.getText().toString().trim();
        if (!Validator.isEmailValid(username)) {
            binding.usernameEt.setError(getString(R.string.error_invalid_username));
            isValid = false;
        }

        String password = binding.passwordEt.getText().toString().trim();
        if (!Validator.isPasswordValid(password)) {
            binding.passwordEt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        String confirmPassword = binding.confirmPasswordEt.getText().toString().trim();
        if (!Validator.isPasswordValid(confirmPassword)) {
            binding.confirmPasswordEt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordEt.setError(getString(R.string.error_unique_password));
            isValid = false;
        }

        String ageStr = binding.ageEt.getText().toString().trim();
        if (ageStr.isEmpty()) {
            binding.ageEt.setError(getString(R.string.error_invalid_age));
            isValid = false;
        } else {
            try {
                Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                binding.ageEt.setError(getString(R.string.error_invalid_age));
                isValid = false;
            }
        }
        String numberStr = binding.numberEt.getText().toString().trim();
        if (numberStr.isEmpty()) {
            binding.numberEt.setError(getString(R.string.error_invalid_number));
            isValid = false;
        } else {
            try {
                Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                binding.numberEt.setError(getString(R.string.error_invalid_number));
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveInSharedPreferences() {
        preferencesManager.put(PREF_KEY_USERNAME, user.getUsername());
        preferencesManager.put(PREF_KEY_PASSWORD, user.getPassword());
        preferencesManager.put(PREF_KEY_AGE, user.getAge());
        preferencesManager.put(PREF_KEY_PHONE_NUMBER, user.getPhoneNumber());
    }

    private void removeErrors() {
        binding.usernameEt.setError(null);
        binding.passwordEt.setError(null);
        binding.confirmPasswordEt.setError(null);
    }

    private void signUpUser() {
        removeErrors();
        user = createUserObjectFromInputs();
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.signupUser(user, new ResultListener<User>() {
            @Override
            public void onSuccess(User user) {
                showToastMessage("User signed up successfully");
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage(throwable.getMessage());

            }
        });
    }

    private void setListeners() {
        binding.signUpBtn.setOnClickListener(v -> {
            signUpUser();
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
