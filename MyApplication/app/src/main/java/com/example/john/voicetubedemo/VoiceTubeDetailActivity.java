package com.example.john.voicetubedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.InputStream;

/**
 * Created by john on 2015/8/25.
 */
public class VoiceTubeDetailActivity extends Activity {
    VoiceTubeItem mVoiceTubeItem = new VoiceTubeItem();
    ImageView voiceTubeImage;
    ListView htmlContentListView;
    HtmlContentAdapter htmlContentAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_voice_tube_detail);

        mVoiceTubeItem.name = getIntent().getExtras().getString("name");
        mVoiceTubeItem.imageUrl = getIntent().getExtras().getString("imageUrl");
        mVoiceTubeItem.firstLink = getIntent().getExtras().getString("firstLink");
        mVoiceTubeItem.firstLinkHtmlContent = getIntent().getExtras().getString("firstLinkHtmlContent");

        voiceTubeImage = (ImageView) findViewById(R.id.voice_tube_image);
        htmlContentListView = (ListView) findViewById(R.id.html_content_list_view);
        htmlContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    int topRowVerticalPosition =
                            (htmlContentListView == null || htmlContentListView.getChildCount() == 0) ?
                                    0 : htmlContentListView.getChildAt(0).getTop();
                    RelativeLayout.LayoutParams voiceTubeImageParams = (RelativeLayout.LayoutParams) voiceTubeImage.getLayoutParams();
                    voiceTubeImageParams.topMargin = topRowVerticalPosition / 2;
                    voiceTubeImage.setLayoutParams(voiceTubeImageParams);
                }
            }
        });
        htmlContentAdapter = new HtmlContentAdapter(this, mVoiceTubeItem.firstLinkHtmlContent);
        htmlContentListView.setAdapter(htmlContentAdapter);
        if (mVoiceTubeItem.imageUrl != null) {
            new DownloadImageTask(voiceTubeImage)
                    .execute(mVoiceTubeItem.imageUrl);
        }
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
            bmImage.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            bmImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            htmlContentAdapter.setBlankViewHeight(bmImage.getMeasuredHeight());
                        }
                    });
            bmImage.setImageBitmap(result);
        }
    }
}
