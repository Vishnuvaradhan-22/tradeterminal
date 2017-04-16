package com.tradeterminal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tradeterminal.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailId;
    private EditText etPassword;

    private Button loginButton;

    private Pattern pattern;
    private Matcher matcher;
    private final String email_patters = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUi();
    }

    private void initializeUi(){
        etEmailId = (EditText)findViewById(R.id.et_email_id);
        etPassword = (EditText)findViewById(R.id.et_password);

        pattern = Pattern.compile(email_patters);

        loginButton = (Button)findViewById(R.id.btn_login);
        loginButton.setOnClickListener(loginListener);

    }

    private OnClickListener loginListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            validateUser();
        }
    };

    private void validateUser(){
        String emailId = etEmailId.getText().toString();
        String password = etPassword.getText().toString();

        boolean emailValidationResult = validateEmailInput(emailId);
        boolean passwordValidationResult = validatePassword(password);

        if(emailValidationResult && passwordValidationResult){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateEmailInput(String emailId){
        boolean result = false;
        if(emailId != null){
            matcher = pattern.matcher(emailId);
            if(!matcher.matches()){
                etEmailId.setError("Please enter valid email");
            }
            else
                result = true;

        }
        return result;
    }

    private boolean validatePassword(String password){
        if(password != null)
            return true;
        else
            return false;
    }

}
