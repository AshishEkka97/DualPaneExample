package com.example.dualpaneexample;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dualpaneexample.model.Item;
import com.example.dualpaneexample.utilites.Communicator;
import com.example.dualpaneexample.utilites.PaginationAdapter;
import com.example.dualpaneexample.utilites.PaginationScrollListener;
import com.example.dualpaneexample.utilites.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentA extends Fragment implements PaginationAdapter.OnItemListener {
    private static final String TAG = "Fragment";
    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    private List<Item> items = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_a, container, false);
        setupRecyclerView(rootView);
        return rootView;
    }

    private void setupRecyclerView(View rootView) {
        rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress);
        adapter = new PaginationAdapter(getContext(), items, this);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstPage();
            }
        }, 1000);
    }

    private void loadFirstPage() {
        // fetching dummy data
        List <Item> items = Item.createItems(adapter.getItemCount());
        progressBar.setVisibility(View.GONE);
        adapter.addAll(items);

        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        List<Item> items = Item.createItems(adapter.getItemCount());

        adapter.removeLoadingFooter();
        isLoading = false;

        adapter.addAll(items);

        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
        Communicator communicator = (Communicator) getActivity();
        communicator.displayDetails(items.get(position).getTitle(), items.get(position).getUrl());
    }
}
