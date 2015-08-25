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
    HashMap<String, ImageView> nameImageViewHashMap = new HashMap<String, ImageView>();
    public CategoryAdapter(Context context) {
        items = Utilities.fillVoiceTubeItems();
        mInflater = LayoutInflater.from(context);
        Utilities.setOnFetchWebContentListener(this);
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
            new DownloadImageTask(holder.backgroundImage)
                    .execute(items.get(position).imageUrl);
        } else if (position == 0) {
            RelativeLayout.LayoutParams backgroundImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, voiceTubeImageHeight);
            holder.backgroundImage.setLayoutParams(backgroundImageParams);
        } else {
            nameImageViewHashMap.put(items.get(position).name, holder.backgroundImage);
        }
        return convertView;
    }

    @Override
    public void onFetchWebContentFinish(String name, String imageUrl, String firstLink, String firstLinkHtmlContent) {
        ImageView backgroundImage = nameImageViewHashMap.get(name);
        if (backgroundImage != null) {
            new DownloadImageTask(backgroundImage)
                    .execute(imageUrl);
        }
        for (int i = 1; i < items.size(); i++) {
            if (name.equals(items.get(i).name)) {
                items.get(i).imageUrl = imageUrl;
                items.get(i).firstLink = firstLink;
                items.get(i).firstLinkHtmlContent = firstLinkHtmlContent;
            }
        }
    }

    class ItemHolder {
        TextView nameText;
        ImageView backgroundImage;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
