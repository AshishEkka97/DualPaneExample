package com.example.dualpaneexample;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {


    public FragmentB() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b, container, false);
    }

    public void displayDetails(String title, String url) {
        TextView titleText = getView().findViewById(R.id.txvTitle);
        titleText.setText(title);
        ImageView imageView = getView().findViewById(R.id.bigImage);
        Glide.with(getView()).load(url).into(imageView);
    }

}
