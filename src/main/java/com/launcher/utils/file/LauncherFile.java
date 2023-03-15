package com.launcher.utils.file;

public class LauncherFile {

	public double size;
	
	public String url;

	public String path;

	public LauncherFile(double size_, String url_, String path_) {
		this.size = size_;
		this.url = url_;
		this.path = path_;
	}

	public double getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}

	public String getPath() {
		return this.path;
	}
}
