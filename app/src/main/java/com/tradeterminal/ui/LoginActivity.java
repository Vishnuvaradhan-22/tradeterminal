package com.tradeterminal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tradeterminal.R;
import com.tradeterminal.model.User;
import com.tradeterminal.utility.DataLoader;
import com.tradeterminal.utility.WebServiceGateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements DataLoader{

    private EditText etEmailId;
    private EditText etPassword;

    private Button loginButton;

    private Pattern pattern;
    private Matcher matcher;
    private final String email_patters = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private String loginUrl = "http://api.tradeterminal.com.au/api/Login/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUi();
    }

    private void initializeUi(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        String getUrl[] = new String[2];
        getUrl[0] = "get";
        getUrl[1] = this.loginUrl+emailId+"/"+password;
        boolean emailValidationResult = validateEmailInput(emailId);
        boolean passwordValidationResult = validatePassword(password);

        if(emailValidationResult && passwordValidationResult){
            new WebServiceGateway(this).execute(getUrl);
            /**Intent intent = new Intent();
            intent.setClass(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);**/
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

    @Override
    public void loadData(String jsonData) {
        try {
            JSONObject responseJson = new JSONObject(jsonData);
            boolean availability = Boolean.parseBoolean(responseJson.getString("IsSuccess"));
            if(availability){
                JSONObject userObjectJson = new JSONObject(responseJson.getString("ReturnModel"));
                User user = new User();
                user.setUserName(userObjectJson.getString("Username"));
                user.setBussinessId(Integer.parseInt(userObjectJson.getString("BusinessId")));
                user.setFullAddress(userObjectJson.getString("FullAddress"));
                user.setUserId(Long.parseLong(userObjectJson.getString("Id")));
                user.setMobileNumber(userObjectJson.getString("MobilePhone"));
                user.setHomePhone(userObjectJson.getString("HomePhone"));
                user.setActive(Boolean.parseBoolean(userObjectJson.getString("IsActive")));
                user.setDeleted(Boolean.parseBoolean(userObjectJson.getString("IsDeleted")));
                user.setRole(userObjectJson.getString("Role"));
                user.setRoleId(Long.parseLong(userObjectJson.getString("RoleId")));
                user.setToken(userObjectJson.getString("Token"));
                user.setUniqueId(userObjectJson.getString("UniqueId"));
                if(user.isActive()){
                    Intent intent = new Intent();
                    Bundle userData = new Bundle();
                    userData.putSerializable("User",user);
                    intent.setClass(getApplicationContext(),HomeScreenActivity.class);
                    startActivity(intent);
                }
            }
            else{
                JSONArray errorMessage = (JSONArray)responseJson.get("Errors");
                JSONObject errorData = (JSONObject)errorMessage.get(0);
                etEmailId.setText("");
                etPassword.setText("");
                etEmailId.setError("Please check email");
                etPassword.setError("Please check password");
                Toast.makeText(LoginActivity.this,errorData.getString("ErrorMessage"),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            /** TO-DO
            *add feature to reset login page and display error message
             * */
            e.printStackTrace();
        }
    }
}
