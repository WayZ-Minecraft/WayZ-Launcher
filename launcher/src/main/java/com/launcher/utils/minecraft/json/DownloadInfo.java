package com.launcher.utils.minecraft.json;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DownloadInfo {

	protected String path;

	protected URL url;

	protected String sha1;

	protected int size;

	public DownloadInfo() {}

	public DownloadInfo(DownloadInfo other) {
		this.path = other.path;
		this.url = other.url;
		this.sha1 = other.sha1;
		this.size = other.size;
	}

	public URL getUrl() {
		return this.url;
	}

	public String getSha1() {
		return this.sha1;
	}

	public String getPath() {
		return this.path;
	}

	public int getSize() {
		return this.size;
	}

	public static URI constantURI(String input) {
		try {
			return new URI(input);
		} catch (URISyntaxException e) {
			throw new Error(e);
		}
	}

	public static URL constantURL(String input) {
		try {
			return new URL(input);
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
	}
}