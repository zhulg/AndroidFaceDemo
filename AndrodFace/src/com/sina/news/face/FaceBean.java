package com.sina.news.face;

public class FaceBean {

	private String iconName;
	private String key;

	public FaceBean(String Key, String iconName) {
		this.key = Key;
		this.iconName = iconName;
	}

	public String getImageName() {
		return iconName;
	}

	public void setImageName(String imageName) {
		this.iconName = imageName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
