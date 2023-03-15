package com.launcher.utils.assets;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AssetIndex {

	public static final String DEFAULT_ASSET_NAME = "legacy";
	
	private final Map<String, AssetObject> objects;

	private boolean virtual;

	public AssetIndex() {
		this.objects = new LinkedHashMap<String, AssetObject>();
	}

	public Map<String, AssetObject> getObjects() {
		return this.objects;
	}

	public Set<AssetObject> getUniqueObjects() {
		return new HashSet<AssetObject>(this.objects.values());
	}

	public boolean isVirtual() {
		return this.virtual;
	}
}
