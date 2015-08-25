package com.example.john.voicetubedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by john on 2015/8/23.
 */
public class CategoryAdapter extends BaseAdapter implements OnFetchWebContentListener {
    ArrayList<VoiceTubeItem> items;
    int voiceTubeImageHeight;
    LayoutInflater mInflater;
    Context mContext;
    HashMap<String, ImageView> nameImageViewHashMap = new HashMap<String, ImageView>();
    public CategoryAdapter(Context context) {
        items = Utilities.fillVoiceTubeItems();
        mInflater = LayoutInflater.from(context);
        Utilities.setOnFetchWebContentListener(this);
        mContext = context;
    }

    public void setVoiceTubeImageHeight(int height) {
        voiceTubeImageHeight = height;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            holder = new ItemHolder();
            convertView = mInflater.inflate(R.layout.category_item, parent, false);
            holder.nameText = (TextView) convertView.findViewById(R.id.name);
            holder.backgroundImage = (ImageView) convertView.findViewById(R.id.background);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        holder.nameText.setText(items.get(position).name);
        if (items.get(position).imageUrl != null) {
            Picasso.with(mContext).load(items.get(position).imageUrl).into(holder.backgroundImage);
        } else if (position == 0) {
            RelativeLayout.LayoutParams backgroundImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, voiceTubeImageHeight);
            holder.backgroundImage.setLayoutParams(backgroundImageParams);
        } else {
            nameImageViewHashMap.put(items.get(position).name, holder.backgroundImage);
        }
        return convertView;
    }

    @Override
    public void onFetchWebContentFinish(String name, final String imageUrl, String firstLink, String firstLinkHtmlContent) {
        final ImageView backgroundImage = nameImageViewHashMap.get(name);
        if (backgroundImage != null) {
            ((MainActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(mContext).load(imageUrl).into(backgroundImage);
                }
            });
        }
        for (int i = 1; i < items.size(); i++) {
            if (name.equals(items.get(i).name)) {
                if (!firstLink.equals(items.get(i).firstLink)) {
                    items.get(i).imageUrl = imageUrl;
                    items.get(i).firstLink = firstLink;
                    items.get(i).firstLinkHtmlContent = firstLinkHtmlContent;
                    Utilities.saveVoiceTubeItemToSharedPreferences(items.get(i));
                }
            }
        }
    }

    class ItemHolder {
        TextView nameText;
        ImageView backgroundImage;
    }
}
