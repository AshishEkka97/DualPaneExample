package com.example.dualpaneexample;

import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
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
import com.example.dualpaneexample.utilites.NetworkUtils;
import com.example.dualpaneexample.utilites.PaginationAdapter;
import com.example.dualpaneexample.utilites.PaginationScrollListener;
import com.example.dualpaneexample.utilites.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FragmentA extends Fragment implements PaginationAdapter.OnItemListener {
    private static final String TAG = "Fragment";
    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    SQLiteHelper dbHelper;

    private static final int PAGE_START = 0;
    private static final int ITEMS_PER_PAGE = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
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
        dbHelper = new SQLiteHelper(getContext());
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
                loadNextPage();
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

        if (dbHelper.checkDatabase()) {
            TOTAL_PAGES = (int) dbHelper.numberOfRows() / ITEMS_PER_PAGE - 1;
            loadFirstPage();
        } else {
            fetchFromAPI();
        }
    }

    public void fetchFromAPI() {
        URL photosURL = NetworkUtils.buildUrl();
        new APIFetch().execute(photosURL);
    }

    private void loadFirstPage() {
        // fetching dummy data
        List <Item> items = loadRows();
        progressBar.setVisibility(View.GONE);
        adapter.addAll(items);

        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    private List<Item> loadRows() {
        List <Item> items = new ArrayList<>();
        Cursor cursor = dbHelper.getData(ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int albumId = cursor.getInt(1);
            String title = cursor.getString(2);
            String url = cursor.getString(3);
            String thumbUrl = cursor.getString(4);
            items.add(new Item(albumId,id, title,url, thumbUrl));
        }
        return items;
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        List <Item> items = loadRows();

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

    List<Item> getItemsFromJSON(String jsonString) {
        List<Item> items = new ArrayList<>();
        try {
            JSONArray photos = new JSONArray(jsonString);
            for (int i = 0; i < photos.length(); i++) {
                JSONObject photo = photos.getJSONObject(i);
                int id = photo.getInt("id");
                int albumId = photo.getInt("albumId");
                String title = photo.getString("title");
                String url = photo.getString("url");
                String thumbnailUrl = photo.getString("thumbnailUrl");
                items.add(new Item(albumId,id, title, url, thumbnailUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public class APIFetch extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL apiUrl = urls[0];
            String photos = null;
            try {
                photos = NetworkUtils.getResponseFromHttpUrl(apiUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if (s != null && !s.equals("")) {
                Toast.makeText(getContext(), "Fetched", Toast.LENGTH_LONG).show();
                new SQLiteDump().execute(getItemsFromJSON(s));
            }
        }

    }

    public class SQLiteDump extends AsyncTask<List<Item>, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(List<Item>... lists) {
            try {
                dbHelper.addImages(lists[0]);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (true) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Saved to db", Toast.LENGTH_LONG).show();
                TOTAL_PAGES = (int) dbHelper.numberOfRows() / ITEMS_PER_PAGE - 1;
                loadFirstPage();
            }
        }
    }
}
