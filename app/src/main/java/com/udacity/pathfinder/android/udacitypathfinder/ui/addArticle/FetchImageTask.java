package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Retrieve all images from a given URL.
 */
class FetchImageTask extends AsyncTask<String, Void, Document> {

    public interface ImageResponse {
        void finishImageResponse(Document out);
    }

    public ImageResponse delegate = null;

    public FetchImageTask(ImageResponse delegate) {
        this.delegate = delegate;
    }

    protected Document doInBackground(String... in) {
        String url = in[0];
        Document articleRaw;
        try {
            articleRaw = Jsoup.connect(url).get();
            return articleRaw;
        } catch (Exception ex) {
            // Do nothing
            return null;
        }
    }

    protected void onPostExecute(Document result) {
        delegate.finishImageResponse(result);
    }
}