package com.zjy.irissqlxposed.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zjy.irissqlxposed.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().add(R.id.container_packages, FindDexFragment.newInstance(), FindDexFragment.TAG)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preference:
                new AlertDialog.Builder(this).setMessage(R.string.use_wizard)
                        .setPositiveButton("知道了", null)
                        .show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
