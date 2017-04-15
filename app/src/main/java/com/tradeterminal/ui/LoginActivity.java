package com.tradeterminal.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tradeterminal.R;
import com.tradeterminal.utilities.MenuLoader;

public class LoginActivity extends MenuLoader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
