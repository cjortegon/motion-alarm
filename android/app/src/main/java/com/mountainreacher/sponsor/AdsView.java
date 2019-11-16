package com.mountainreacher.sponsor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AdsView extends View implements OnClickListener {

	private Context context;

	private DownloadImage loading;
	private Intent link;

	private ArrayList<Ad> ads;
	private boolean completeRead, downloadingImage, adProcessRunning;
	private int selectedAd;

	public AdsView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		this.link = new Intent(Intent.ACTION_VIEW);
		this.setOnClickListener(this);
		
		ads = new ArrayList<Ad>();
		completeRead = false;
		downloadingImage = false;
		adProcessRunning = true;
		readFromInternet();
		
		startCheckingThread();
	}
	
	public void startCheckingThread() {
		new Thread(new Runnable() {
			public void run() {
				while(adProcessRunning) {
					try {
						Thread.sleep(1000);
						postInvalidate();
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}

	public void readFromInternet() {
		new Thread(new Runnable() {
			public void run() {

				URLConnection feedUrl = null;
				InputStream in;

				try {
					feedUrl = new URL("https://dl.dropboxusercontent.com/u/13898729/ads/db/motionalarm.txt").openConnection();
				} catch (MalformedURLException e) {
					Log.v("ERROR","MALFORMED URL EXCEPTION");
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					in = feedUrl.getInputStream();
					ads.addAll(buildDb(in));
					completeRead = true;
				}catch(Exception e){}
			}
		}).start();
	}

	private static ArrayList<Ad> buildDb(InputStream is) throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));

		ArrayList<Ad> ads = new ArrayList<Ad>();

		try {
			int links = Integer.valueOf(reader.readLine());
			while(links > 0) {
				int id = Integer.valueOf(reader.readLine());
				String name = reader.readLine();
				int priority = Integer.valueOf(reader.readLine());
				String url = reader.readLine();
				String imgUrl = reader.readLine();
				ads.add(new Ad(id, priority, name, url, imgUrl));
				links --;
			}
		} catch (IOException e) {}
		finally {
			try {
				is.close();
			} catch (IOException e) {}
		}

		return ads;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(adProcessRunning)
			selectOneAd();

		if(completeRead && !downloadingImage) {
			Ad ad = ads.get(selectedAd);
			
			int windowW = getWidth();
			int windowH = getHeight();
			int adW = ad.bitmap.getWidth();
			int adH = ad.bitmap.getHeight();
			
			double wFactor = windowW/(double)adW;
			double hFactor = windowH/(double)adH;
			double factor = (wFactor < hFactor ? wFactor : hFactor);
			adW = (int) (adW*factor);
			adH = (int) (adH*factor);
			
			int centerX = windowW/2;
			int centerY = windowH/2;
			int halfW = adW/2;
			int halfH = adH/2;
			
			Rect src = new Rect(0, 0, ad.bitmap.getWidth(), ad.bitmap.getHeight());
			Rect dst = new Rect(centerX-halfW, centerY-halfH, centerX+halfW, centerY+halfH);
			canvas.drawBitmap(ad.bitmap, src, dst, null);
		}
	}

	public void selectOneAd() {

		if(downloadingImage) {
			if(loading.bitmap != null) {
				ads.get(selectedAd).bitmap = loading.bitmap;
				downloadingImage = false;
				adProcessRunning = false;
			}
		} else if(completeRead) {
			Random ale = new Random();
			selectedAd = ale.nextInt(12345) % ads.size();
			downloadingImage = true;
			loading = new DownloadImage();
			loading.execute(ads.get(selectedAd).imgUrl);
		}
	}

	@Override
	public void onClick(View v) {
		if(completeRead) {
			link.setData(Uri.parse(ads.get(selectedAd).url));
			context.startActivity(link);
		}
	}



}
