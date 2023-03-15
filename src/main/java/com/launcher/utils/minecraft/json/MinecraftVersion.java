package com.launcher.utils.minecraft.json;

import java.util.List;
import java.util.Map;

import com.launcher.utils.assets.AssetIndexInfo;

/*Bugs*/

public class MinecraftVersion {

	private String id;

	private String inheritsFrom;
	private String minecraftArguments;

	private List<MinecraftLibrary> libraries;

	private String mainClass;

	private String assets;

	private AssetIndexInfo assetIndex;

	private MinecraftClient downloads;

	public Map<ArgumentType, List<Argument>> arguments;

	public MinecraftVersion() {
	}

//	public MinecraftVersion(MinecraftVersion version) {
//		this.id = version.id;
//		if (version.inheritsFrom != null) {
//			this.inheritsFrom = version.inheritsFrom;
//		}
//		if (version.assetIndex != null) {
//			this.assetIndex = version.assetIndex;
//		}
//		if (version.arguments != null) {
//			this.arguments = Maps.newEnumMap(ArgumentType.class);
//			for (Map.Entry<ArgumentType, List<Argument>> entry : version.arguments.entrySet()) {
//				this.arguments.put(entry.getKey(), new ArrayList<Argument>(entry.getValue()));
//			}
//		}
//	    this.minecraftArguments = version.minecraftArguments;
//		this.libraries = version.libraries;
//		this.mainClass = version.mainClass;
//		this.assets = version.assets;
//	}
	
	public String getMinecraftArguments() {
		return this.minecraftArguments;
	}

	public List<MinecraftLibrary> getLibraries() {
		return libraries;
	}

	public MinecraftClient getDownloads() {
		return downloads;
	}

	public String getId() {
		return id;
	}

	public void setId(String idd) {
		this.id = idd;
	}

	public String getInheritsFrom() {
		return inheritsFrom;
	}

	public void setInheritsFrom(String inheritsFrom) {
		this.inheritsFrom = inheritsFrom;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String getAssets() {
		return assets;
	}

	public void setAssets(String assets) {
		this.assets = assets;
	}

	public AssetIndexInfo getAssetIndex() {
		return assetIndex;	
	}
	
	public void setAssetIndex(String s) {
		this.assets = s;
	}

	public Map<ArgumentType, List<Argument>> getArguments() {
		return this.arguments;
	}
}