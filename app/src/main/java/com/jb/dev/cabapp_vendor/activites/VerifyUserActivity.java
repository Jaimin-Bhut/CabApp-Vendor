package com.jb.dev.cabapp_vendor.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jb.dev.cabapp_vendor.R;

public class VerifyUserActivity extends AppCompatActivity {
    int otp;
    String main;
    EditText editTextOne, editTextTwo, editTextThree, editTextFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        Bundle b = getIntent().getExtras();
        otp = b.getInt("otp");
        editTextOne = findViewById(R.id.verify_et_one);
        editTextTwo = findViewById(R.id.verify_et_two);
        editTextThree = findViewById(R.id.verify_et_three);
        editTextFour = findViewById(R.id.verify_et_four);

        editTextOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextOne.getText().toString().length() == 1) {
                    editTextTwo.requestFocus();
                } else {
                    editTextOne.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextTwo.getText().toString().length() == 1) {
                    editTextThree.requestFocus();
                } else {
                    editTextOne.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextThree.getText().toString().length() == 1) {
                    editTextFour.requestFocus();
                } else {
                    editTextTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextFour.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if (editTextFour.getText().toString().length() == 1) {
                    main = editTextOne.getText().toString() + editTextTwo.getText().toString() + editTextThree.getText().toString() + editTextFour.getText().toString();
                    if (main.equals(String.valueOf(otp))) {
                        Toast.makeText(VerifyUserActivity.this, "VERIFY", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VerifyUserActivity.this, "NOT VERIFY", Toast.LENGTH_LONG).show();
                    }
                } else {
                    editTextThree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextFour.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextFour.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    main = editTextOne.getText().toString() + editTextTwo.getText().toString() + editTextThree.getText().toString() + editTextFour.getText().toString();
                    if (main.equals(String.valueOf(otp))) {
                        Toast.makeText(VerifyUserActivity.this, "VERIFY", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VerifyUserActivity.this, "NOT VERIFY", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        navigateUpTo(new Intent(VerifyUserActivity.this, MainActivity.class));
        this.finish();
    }
}