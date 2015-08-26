package com.example.john.voicetubedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    ListView category;
    CategoryAdapter categoryAdapter;
    ImageView voiceTubeImage;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isLongPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utilities.sharedPreferences = getSharedPreferences(Utilities.DATA, 0);
        voiceTubeImage = (ImageView) findViewById(R.id.voice_tube_image);
        category = (ListView) findViewById(R.id.category);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        Utilities.mainActivityContext = this;
        categoryAdapter = new CategoryAdapter(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        voiceTubeImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        voiceTubeImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        categoryAdapter.setVoiceTubeImageHeight(voiceTubeImage.getMeasuredHeight());
                        category.setAdapter(categoryAdapter);
                    }
                });
        category.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    int topRowVerticalPosition =
                            (category == null || category.getChildCount() == 0) ?
                                    0 : category.getChildAt(0).getTop();
                    RelativeLayout.LayoutParams voiceTubeImageParams = (RelativeLayout.LayoutParams) voiceTubeImage.getLayoutParams();
                    voiceTubeImageParams.topMargin = topRowVerticalPosition / 2;
                    voiceTubeImage.setLayoutParams(voiceTubeImageParams);
                }
            }
        });
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isLongPress)
                    OnVoiceTubeImageClick((VoiceTubeItem) categoryAdapter.getItem(position));
                isLongPress = false;
            }
        });
        category.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    Utilities.fetchNextLink((VoiceTubeItem) categoryAdapter.getItem(position));
                isLongPress = true;
                return false;
            }
        });
    }

    public void OnVoiceTubeImageClick(VoiceTubeItem item) {
        if (item.firstLinkHtmlContent != null) {
            Intent intent = new Intent(this, VoiceTubeDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", item.name);
            bundle.putString("imageUrl", item.imageUrl);
            bundle.putString("firstLink", item.firstLink);
            bundle.putString("firstLinkHtmlContent", item.firstLinkHtmlContent);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void setRefreshStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public void setRefreshStop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
