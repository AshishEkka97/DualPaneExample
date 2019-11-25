package com.example.dualpaneexample.model;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private int albumId;
    private int id;
    private String title;
    private String url;
    private String thumbUrl;

    public Item(int albumId, int id, String title, String url, String thumbUrl) {
        this.albumId = albumId;
        this.id = id;
        this.title = title;
        this.url = url;
        this.thumbUrl = thumbUrl;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public static List<Item> createItems(int itemCount) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item(itemCount, itemCount, "Item no. " + itemCount, "url", "thumbUrl");
            items.add(item);
        }
        return items;
    }
}

