package com.mountainreacher.sponsor;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
	ImageView layoutimage;
	Bitmap bitmap;

	public DownloadImage(ImageView Image) {
		this.layoutimage = Image;
	}
	
	public DownloadImage() {
	}

	@Override
	protected void onPreExecute() {
		// Not using ProgressBar in this Tutorial
	}

	@Override
	protected Bitmap doInBackground(String... Image_URL) {

		String Downloadimage = Image_URL[0];

		Bitmap bitmap = null;
		try {
			// Download Image from URL
			InputStream input = new java.net.URL(Downloadimage)
					.openStream();
			bitmap = BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			// Error Log
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// Set image into image.xml layout
		if(layoutimage != null)
			layoutimage.setImageBitmap(result);
		bitmap = result;
	}
}
