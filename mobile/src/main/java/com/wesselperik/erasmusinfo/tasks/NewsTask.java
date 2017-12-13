package com.wesselperik.erasmusinfo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.wesselperik.erasmusinfo.adapters.NewsAdapter;
import com.wesselperik.erasmusinfo.models.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wesselperik on 13/12/2017.
 */

public class NewsTask extends AsyncTask<String, Void, ArrayList<News>> {

    private NewsAdapter.OnNewsLoaded listener;

    public NewsTask(NewsAdapter.OnNewsLoaded listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<News> doInBackground(String... params) {
        ArrayList<News> items = new ArrayList<>();
        Document doc = null;

        if (params.length == 0) {
            try {
                doc = Jsoup.connect("https://het-erasmus.nl/nieuws").get();

                Elements newsItems = doc.getElementsByClass("news-list-item");
                for (Element newsItem : newsItems) {
                    Element item = newsItem.getElementsByClass("news-list-item-right").get(0);
                    String title = item.getElementsByTag("a").get(1).text();
                    String shortText = item.getElementsByClass("bodytext").get(0).text();
                    String category = item.getElementsByTag("a").get(0).text();
                    String date = item.getElementsByClass("news-list-date").get(0).text().replace(".", " ");
                    String url = item.getElementsByTag("a").get(0).attr("href");
                    News news = new News(title, shortText, category, date, url);
                    items.add(news);
                    Log.d("News item", news.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                doc = Jsoup.connect("https://het-erasmus.nl/" + params[0]).get();

                Element item = doc.getElementsByClass("news-single-item").get(0);
                String title = item.getElementsByTag("h1").get(0).text();
                String shortText = item.getElementsByTag("h2").get(0).getElementsByClass("bodytext").get(0).text();
                String category = item.getElementsByClass("news-list-category").get(0).text();
                String date = item.getElementsByClass("news-single-date").get(0).text().replace(".", " ");
                String url = item.baseUri();
                News news = new News(title, shortText, category, date, url);
                items.add(news);
                Log.d("News single item", news.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (listener != null) listener.onNewsLoaded(items);
        return items;
    }
}
