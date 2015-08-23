package com.example.john.voicetubedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by john on 2015/8/23.
 */
public class CategoryAdapter extends BaseAdapter{
    ArrayList<VoiceTubeItem> items;
    int voiceTubeImageHeight;
    LayoutInflater mInflater;
    public CategoryAdapter(Context context) {
        items = Utilities.fillVoiceTubeItems();
        mInflater = LayoutInflater.from(context);
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
        } else {
            RelativeLayout.LayoutParams backgroundImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, voiceTubeImageHeight);
            holder.backgroundImage.setLayoutParams(backgroundImageParams);
        }

        return convertView;
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
