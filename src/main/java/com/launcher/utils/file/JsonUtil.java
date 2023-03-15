package com.launcher.utils.file;

import java.io.BufferedReader;
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
import com.photon.util.ProtectorManager;

public class JsonUtil {

	public static Gson getGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
		gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		gsonBuilder.registerTypeAdapter(Argument.class, new Argument.Serializer());
		gsonBuilder.enableComplexMapKeySerialization();
		gsonBuilder.setPrettyPrinting();
		return gsonBuilder.create();
	}

	public static String loadJSON(String inUrl) throws IOException {
		URLConnection url = new URL(inUrl).openConnection();
		ProtectorManager.addProperties(url, "mojang");
		url.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(url.getInputStream()));
		String json = new String();
		String inputLine;
		while ((inputLine = in.readLine()) != null) json = json + inputLine;
		return json;
	}
}
