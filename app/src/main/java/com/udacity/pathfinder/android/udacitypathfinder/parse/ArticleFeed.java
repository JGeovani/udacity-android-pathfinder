package com.udacity.pathfinder.android.udacitypathfinder.parse;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;

import java.util.HashMap;
import java.util.Map;

public class ArticleFeed {
    private ArticleResults articleFeed;
    public boolean completed = false;
    String TAG = this.getClass().getSimpleName();
    /**
     *     :TODO Create a callback function to notify when data has completed from parse background task
     *     Example of current null error:
     *              ArticleFeed articleFeed = new ArticleFeed();
     *              articleFeed.getArticles();
     *              ArticleResults articleResults = articleFeed.getArticleData();
     *              Will return null as background task is not completed --> articleResults.getItems().size();
     *     getArticles returns JSON Array of articles items
     */


    public void getArticles() {
        ParseCloud.callFunctionInBackground("allArticles", new HashMap<String, Object>(), new FunctionCallback<Map<String, Object>>() {
            public void done(Map<String, Object> mapObject, com.parse.ParseException e) {
                if (e == null) {
                    try {
                        ArticleResults dataList = new Gson().fromJson(mapObject.get("articles").toString(), ArticleResults.class);
                        Log.v(TAG, "GSON Size:" + dataList.getItems().size() + " test --> " + dataList.getItems().get(0).getTitle());
                        setArticleData(dataList);
                    } catch (JsonSyntaxException err) {
                        Log.e(TAG, "There was an error :" + String.valueOf(err));
                    }
                } else {
                    System.out.println("There was an error :" + String.valueOf(e));

                }
            }
        });
    }

    private void setArticleData(ArticleResults data){
        this.articleFeed = data;
        completed = true;
       Log.v(TAG,"Article List count:" + articleFeed.getItems().size());
    }

    public ArticleResults getArticleData(){
        return articleFeed;
    }

}
