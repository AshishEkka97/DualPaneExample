package com.example.dualpaneexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dualpaneexample.utilites.Communicator;

public class MainActivity extends AppCompatActivity implements Communicator {

    private boolean mIsDualPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fragmentBView = findViewById(R.id.fragmentB);
        mIsDualPane = fragmentBView != null && fragmentBView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void displayDetails(String title, String url) {
        if (mIsDualPane) {
            FragmentB fragmentB = (FragmentB) getSupportFragmentManager().findFragmentById(R.id.fragmentB);
            fragmentB.displayDetails(title, url);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }
}
