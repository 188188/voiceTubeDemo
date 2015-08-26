package com.example.john.voicetubedemo;

/**
 * Created by john on 2015/8/23.
 */
public interface OnFetchWebContentListener {
    void onFetchWebContentFinish(String name, String imageUrl, String firstLink, String firstLinkHtmlContent, int position);
}
