package com.tradeterminal.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tradeterminal.R;

/**
 * Created by Vish on 14/04/2017.
 */
public class MenuLoader extends AppCompatActivity {
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.loggedin_activity,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.trade_terminal_logo) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
