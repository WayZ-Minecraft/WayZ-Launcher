package com.launcher.utils;

public class GameLinks {

	public String BASE_URL;
	public String JSON_URL;
	public String JSON_NAME;
	public String IGNORE_LIST;
	public String DELETE_LIST;
	public String CUSTOM_FILES_URL;

	public GameLinks(String baseUrl, String jsonName) {
		if (baseUrl.endsWith("/")) this.BASE_URL = baseUrl;
		else this.BASE_URL = baseUrl + "/";
		this.JSON_URL = this.BASE_URL + jsonName;
		this.JSON_NAME = jsonName;
		this.IGNORE_LIST = this.BASE_URL + "ignore.cfg";
		this.DELETE_LIST = this.BASE_URL + "delete.cfg";
		this.CUSTOM_FILES_URL = this.BASE_URL + "files/";
	}

	public String getBaseUrl() {
		return this.BASE_URL;
	}

	public String getJsonName() {
		return this.JSON_NAME;
	}

	public String getJsonUrl() {
		return this.JSON_URL;
	}

	public String getIgnoreListUrl() {
		return this.IGNORE_LIST;
	}

	public String getDeleteListUrl() {
		return this.DELETE_LIST;
	}

	public String getCustomFilesUrl() {
		return this.CUSTOM_FILES_URL;
	}
}
