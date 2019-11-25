package com.example.dualpaneexample.utilites;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dualpaneexample.R;
import com.example.dualpaneexample.model.Item;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Item> items;
    private OnItemListener mOnItemListener;
    private Context context;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context, List<Item> items, OnItemListener onItemListener) {
        this.context = context;
        this.items = items;
        this.mOnItemListener = onItemListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(view, mOnItemListener);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                ItemViewHolder itemView = (ItemViewHolder) holder;
                itemView.title.setText(item.getTitle());
                break;

            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView image;
        OnItemListener onItemListener;

        public ItemViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            image = itemView.findViewById(R.id.image_row);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
            Log.d("TAg1", "Clicked " + getAdapterPosition());
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void add(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(List<Item> itemList) {
        for (Item item : itemList) {
            items.add(item);
        }
    }

    public void remove(Item item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(item);
            notifyItemRemoved(position);
        }
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Item(501, 5005, "Loading", "dummy", "dummy"));
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = items.size() - 1;
        Item item = getItem(position);
        if (item != null) {
            items.remove(item);
            notifyItemRemoved(position);
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
