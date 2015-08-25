package com.example.john.voicetubedemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.Element;
import android.text.Html;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by john on 2015/8/23.
 */
public class Utilities {
    static private ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(100);
    static private ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 50, 3000, TimeUnit.MICROSECONDS, workQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    static OnFetchWebContentListener mOnFetchWebContentListener;
    static String protocol = "http://tw.blog.voicetube.com/category/";
    static public SharedPreferences sharedPreferences;
    static final public String DATA = "data";
    static final public String VOICETUBE = "VoiceTube";
    static final public String SKILL = "skill";
    static final public String NEWS = "news";
    static final public String CAREER = "career";
    static final public String SENSE = "sense";
    static final public String HINT = "hint";
    static final public String FIRSTLINK = "firstLink";
    static final public String CONTENT = "content";
    static final public String IMAGE = "image";
    static ArrayList<VoiceTubeItem> fillVoiceTubeItems() {
        ArrayList<VoiceTubeItem> items = new ArrayList<VoiceTubeItem>();
        items.add(new VoiceTubeItem(VOICETUBE));
        items.add(new VoiceTubeItem(SKILL));
        items.add(new VoiceTubeItem(NEWS));
        items.add(new VoiceTubeItem(CAREER));
        items.add(new VoiceTubeItem(SENSE));
        items.add(new VoiceTubeItem(HINT));
        for (int i = 1; i < items.size(); i++) {
            fillVoiceTubeItemFromSharedPreferences(items.get(i));
            fetchFirstLink(items.get(i).name);
        }
        return items;
    }

    static void fillVoiceTubeItemFromSharedPreferences(VoiceTubeItem item) {
        item.firstLink = sharedPreferences.getString(item.name + "_" + FIRSTLINK, null);
        item.firstLinkHtmlContent = sharedPreferences.getString(item.name + "_" + CONTENT, null);
        item.imageUrl = sharedPreferences.getString(item.name + "_" + IMAGE, null);
    }

    static void saveVoiceTubeItemToSharedPreferences(VoiceTubeItem item) {
        sharedPreferences.edit()
                .putString(item.name + "_" + FIRSTLINK, item.firstLink)
                .putString(item.name + "_" + CONTENT, item.firstLinkHtmlContent)
                .putString(item.name + "_" + IMAGE, item.imageUrl)
                .commit();
    }

    static void setOnFetchWebContentListener(OnFetchWebContentListener callback) {
        mOnFetchWebContentListener = callback;
    }

    static void fetchFirstLink(final String name) {
        HttpGet get = new HttpGet(protocol + name);
        HttpExecutor executor = new HttpExecutor(get) {
            @Override
            public void handleResult(HttpResponse response) {
                InputStream inputStream = null;
                if (response.getEntity() != null) {
                    try {
                        inputStream = response.getEntity().getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String result = inputStreamToString(inputStream);
                org.jsoup.nodes.Document doc = Jsoup.parse(result);
                org.jsoup.nodes.Element div = doc.select("div[class=thumb-container]").first();
                org.jsoup.nodes.Element a = div.select("a").first();
                String link = a.attr("href");
                fetchImageUrlHtml(name, link);
            }

            @Override
            public void handleException(Exception e) {

            }
        };
        pool.execute(executor);
    }

    static void fetchImageUrlHtml(final String name, final String link) {
        HttpGet get = new HttpGet(link);
        HttpExecutor executor = new HttpExecutor(get) {
            @Override
            public void handleResult(HttpResponse response) {
                InputStream inputStream = null;
                if (response.getEntity() != null) {
                    try {
                        inputStream = response.getEntity().getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String result = inputStreamToString(inputStream);
                org.jsoup.nodes.Document doc = Jsoup.parse(result);
                org.jsoup.nodes.Element elemContent = doc.select("div[class=content]").first();
                org.jsoup.nodes.Element img = elemContent.select("img").first();
                String content = elemContent.toString();
                String imgUrl = img.attr("src");
                mOnFetchWebContentListener.onFetchWebContentFinish(name, encodeURL(imgUrl), link, content);
            }

            @Override
            public void handleException(Exception e) {

            }
        };
        pool.execute(executor);
    }


    static private String inputStreamToString(InputStream is) {
        String s = "";
        String line = "";

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        try {
            while ((line = rd.readLine()) != null) { s += line; }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return full string
        return s;
    }

    static String encodeURL(String url) {
        if (null != url && url.length() > 0 )
        {
            int endIndex = url.lastIndexOf("/");
            if (endIndex != -1)
            {
                String newstr = url.substring(endIndex + 1); // not forgot to put check if(endIndex != -1)
                try {
                    String encodeUrl = URLEncoder.encode(newstr, "utf-8");
                    return url.substring(0, endIndex + 1) + encodeUrl;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return url;
    }
}
