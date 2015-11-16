package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Retrieve all images from a given URL.
 */
class FetchImageTask extends AsyncTask<String, Void, Elements> {

    public interface ImageResponse {
        void finishImageResponse(Elements out);
    }

    public ImageResponse delegate = null;
    Elements candidates = new Elements();

    public FetchImageTask(ImageResponse delegate) {
        this.delegate = delegate;
    }

    protected Elements doInBackground(String... in) {
        String url = in[0];
        Document articleRaw;
        try {
            articleRaw = Jsoup.connect(url).get();
            Elements imageSources = articleRaw.select("img[src]");
            for (Element e : imageSources) {
                candidates.add(e);
                Log.e("$$$", e.toString());
            }
        } catch (Exception ex) {
            // Do nothing
        }
        return candidates;
    }

    protected void onPostExecute(Elements result) {
        delegate.finishImageResponse(result);
    }
}