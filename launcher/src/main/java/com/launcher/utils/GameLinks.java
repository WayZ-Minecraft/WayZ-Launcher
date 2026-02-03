package com.launcher.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class GameLinks {

	public final String JSON_NAME;

	public final URL JSON_URL;
	public final URL IGNORE_LIST;
	public final URL DELETE_LIST;
	public final URL CUSTOM_FILES_URL;

	public GameLinks(String baseUrl, String jsonName) throws MalformedURLException {
		this.JSON_NAME = jsonName;

		if(baseUrl == null || baseUrl.isEmpty()) {
			this.JSON_URL = ClassLoader.getSystemClassLoader().getResource("default_settings/" + jsonName);
			this.IGNORE_LIST = ClassLoader.getSystemClassLoader().getResource("default_settings/ignore.cfg");
			this.DELETE_LIST = ClassLoader.getSystemClassLoader().getResource("default_settings/delete.cfg");
			this.CUSTOM_FILES_URL = ClassLoader.getSystemClassLoader().getResource("default_settings/files_" + jsonName.replace(".json", "") + "/");
			return;
		}

		/* Ensure that the url is ending with '/' */
		if(!baseUrl.endsWith("/")) baseUrl += "/";

		this.JSON_URL = new URL(baseUrl + jsonName);
		this.IGNORE_LIST = new URL(baseUrl + "ignore.cfg");
		this.DELETE_LIST = new URL(baseUrl + "delete.cfg");
		this.CUSTOM_FILES_URL = new URL(baseUrl + "files_"+jsonName.replace(".json", "")+"/");
	}

	public String getJsonName() {
		return this.JSON_NAME;
	}

	public URL getJsonUrl() {
		return this.JSON_URL;
	}

	public URL getIgnoreListUrl() {
		return this.IGNORE_LIST;
	}

	public URL getDeleteListUrl() {
		return this.DELETE_LIST;
	}

	public URL getCustomFilesUrl() {
		return this.CUSTOM_FILES_URL;
	}
}
