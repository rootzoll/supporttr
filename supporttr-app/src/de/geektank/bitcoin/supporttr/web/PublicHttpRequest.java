package de.geektank.bitcoin.supporttr.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;


public class PublicHttpRequest extends AsyncTask<String, Integer, String>{

	Context context;
	ResultListener listener;
	
	public interface ResultListener {
		void onResult(String result);
		void onError();
	}
	
	public PublicHttpRequest(final Context context, final ResultListener resultListener) {
		this.context = context;
		this.listener = resultListener;
		
	}
	
	private static String GET(String url) {
		
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			inputStream = httpResponse.getEntity().getContent();
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null) result += line;
		inputStream.close();
		return result;
	}
		
	public boolean isConnected() {
		
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) 
			return true;
		else
			return false;
	}

	@Override
	protected String doInBackground(String... params) {
		return GET(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		listener.onResult(result);
	}

	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		listener.onError();
	}
	
	
		
}
