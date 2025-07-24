package com.launcher.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.launcher.utils.minecraft.DateTypeAdapter;
import com.launcher.utils.minecraft.LowerCaseEnumTypeAdapterFactory;
import com.launcher.utils.minecraft.json.Argument;

public class JsonUtil {

	public static Gson getGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
		gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		gsonBuilder.registerTypeAdapter(Argument.class, new Argument.Serializer());
		gsonBuilder.enableComplexMapKeySerialization();
		return gsonBuilder.create();
	}

	public static String loadJSON(String inUrl) throws IOException {
		URLConnection url = new URL(inUrl).openConnection();
		url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		url.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(url.getInputStream()));
		String json = new String();
		String inputLine;
		while ((inputLine = in.readLine()) != null) json = json + inputLine;
		return json;
	}

	public static String loadJSONFile(File inUrl) throws IOException {
		URL url = new URL(inUrl.toURI().toString());
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String json = new String();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			json = json + inputLine;
		}
		return json;
	}
}
