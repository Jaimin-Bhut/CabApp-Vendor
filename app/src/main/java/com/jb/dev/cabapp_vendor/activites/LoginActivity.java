package com.jb.dev.cabapp_vendor.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_vendor.R;
import com.jb.dev.cabapp_vendor.helper.Constants;
import com.jb.dev.cabapp_vendor.helper.Helper;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private String email, password;
    TextInputEditText edtEmail, edtPassword;
    MaterialButton btnLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        parent_view = findViewById(android.R.id.content);
        progressBar = findViewById(R.id.progress_circular);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        edtEmail.setText("jaimin@driver.com");
        edtPassword.setText("23456234");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (!Helper.isNetworkAvailable(this)) {
            Helper.showSnackBar(parent_view, R.string.please_turn_on_internet, this);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLogin();
                    }
                }, 1500);
            }
        });
    }

    private void isLogin() {
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        if (email.endsWith("@driver.com")) {
            isSet();
        } else {
            Snackbar.make(parent_view, "Enter valid email", Snackbar.LENGTH_SHORT).show();
        }
    }

    void isSet() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY).whereEqualTo(Constants.DRIVER_EMAIL_KEY, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String p = document.getString(Constants.DRIVER_PASSWORD_KEY);
                                    if (p.equals(password)) {
                                        Log.e("addOnCompleteListener::", "success");
                                        progressBar.setVisibility(View.GONE);
                                        setIsDriverLogin(getApplicationContext(), true, email);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Log.e("addOnCompleteListener::", "Fail");
                                        progressBar.setVisibility(View.GONE);
                                        Snackbar.make(parent_view, getString(R.string.invalid_password), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(parent_view, "Driver not exist", Snackbar.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    public void setIsDriverLogin(Context context, Boolean value, String driver_email) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.IS_DRIVER_EMAIL, value);
        editor.putString("setDriverEmail", driver_email);
        editor.apply();
    }
}