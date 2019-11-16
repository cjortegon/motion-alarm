package com.mountainreacher.sponsor;

import android.graphics.Bitmap;

public class Ad {

	public int linkId;
	public int priority;
	public String name;
	public String url;
	public String imgUrl;
	public Bitmap bitmap;
	
	public Ad(int linkId, int priority, String name, String url, String imgUrl) {
		super();
		this.linkId = linkId;
		this.priority = priority;
		this.name = name;
		this.url = url;
		this.imgUrl = imgUrl;
	}
	
}
