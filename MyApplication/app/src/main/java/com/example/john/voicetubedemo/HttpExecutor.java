/**
 * RelayTV Confidential
 * Copyright (c) 2012-2014 LiveRelay, Inc.
 * All Rights Reserved.
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * LiveRelay, Inc. The intellectual and technical concepts contained herein
 * are proprietary to LiveRelay, Inc. and its suppliers and may be covered by
 * U.S. and Foreign Patents, patents in process, and are protected by trade
 * secret or copyright law. Dissemination of this information or reproduction
 * of this material is strictly forbidden unless prior written permission is
 * obtained from LiveRelay, Inc.
 **/
package com.example.john.voicetubedemo;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class HttpExecutor implements Runnable {

	private static final String TAG = "HttpExecutor";
	private HttpUriRequest post;
	public static final int TIMEOUT = 5000; 
	private static DefaultHttpClient mClient =new DefaultHttpClient();

	public HttpExecutor(HttpUriRequest post) {
		this.post = post;
	}

	public void run(){
		try {
			Log.v(TAG, "====== Executing(" + this.hashCode() + "): " + post.getMethod() + " " + post.getURI());
			HttpParams _HttpParams = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(_HttpParams, TIMEOUT);
			mClient = new DefaultHttpClient();
			mClient.setParams(_HttpParams);

			HttpResponse response = mClient.execute(post);
			Log.v(TAG, "Response(" + this.hashCode() + "): " + response.getStatusLine().getStatusCode());
			handleResult(response);
			
		} catch (final Exception e) {
			Log.e(TAG, "Failed(" + this.hashCode() + ") with Exception: ", e);
			onException(e);
		}
	}

	public abstract void handleResult(HttpResponse response);

	public void onException(Exception e) {
		handleException(e);
	};

	public abstract void handleException(Exception e);
};
