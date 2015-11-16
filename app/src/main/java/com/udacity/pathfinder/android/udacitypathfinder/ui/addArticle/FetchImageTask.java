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

    protected Elements doInBackground(String... in) throws IllegalArgumentException {
        String url = in[0];
        Document articleRaw;
        try {
            articleRaw = Jsoup.connect(url).get();
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            for (Element imageUrl : articleRaw.select("url")) {
                Log.e("$$$", imageUrl.data() + imageUrl.toString());
                candidates.add(imageUrl);
            }
            return candidates;
        } catch (Exception ex) {
            Log.e("$$$", ex.getMessage());
            throw new IllegalArgumentException("Invalid URL");
        }
    }

    protected void onPostExecute(Elements result) {
        delegate.finishImageResponse(result);
    }
}