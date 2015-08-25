package com.example.john.voicetubedemo;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by john on 2015/8/25.
 */
public class HtmlContentAdapter extends BaseAdapter {
    Context mContext;
    View blankView;
    TextView htmlContentText;
    public void setBlankViewHeight(int height) {
        blankView.setMinimumHeight(height);
    }
    public HtmlContentAdapter(Context context, String content) {
        mContext = context;
        blankView = new View(context);
        htmlContentText = new TextView(context);
        htmlContentText.setText(Html.fromHtml(content));
        htmlContentText.setBackgroundColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return blankView;
        }
        return htmlContentText;
    }
}
