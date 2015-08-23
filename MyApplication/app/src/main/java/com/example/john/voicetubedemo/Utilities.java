package com.example.john.voicetubedemo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by john on 2015/8/23.
 */
public class Utilities {
    static ArrayList<VoiceTubeItem> fillVoiceTubeItems() {
        ArrayList<VoiceTubeItem> items = new ArrayList<VoiceTubeItem>();
        items.add(new VoiceTubeItem("VoiceTube", null));
        items.add(new VoiceTubeItem("skill", encodeURL("http://tw.blog.voicetube.com/wp-content/uploads/2015/08/niagara-1024x536.jpg")));
        items.add(new VoiceTubeItem("news", encodeURL("http://tw.blog.voicetube.com/wp-content/uploads/2015/08/cover1-1024x533.png")));
        items.add(new VoiceTubeItem("career", encodeURL("http://tw.blog.voicetube.com/wp-content/uploads/2015/08/螢幕快照-2015-08-11-下午8.53.59.png")));
        items.add(new VoiceTubeItem("sense", encodeURL("http://tw.blog.voicetube.com/wp-content/uploads/2015/08/hair.jpg")));
        items.add(new VoiceTubeItem("hint", encodeURL("http://tw.blog.voicetube.com/wp-content/uploads/2015/07/italian-4.jpg")));
        return items;
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
