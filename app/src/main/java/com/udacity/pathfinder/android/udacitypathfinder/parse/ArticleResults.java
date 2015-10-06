package com.udacity.pathfinder.android.udacitypathfinder.parse;

import java.util.List;

// Class for GSON
public class ArticleResults {
    private List<Item> items;
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
}
