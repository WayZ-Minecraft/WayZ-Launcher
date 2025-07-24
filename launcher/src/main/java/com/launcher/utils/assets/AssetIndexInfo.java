package com.launcher.utils.assets;

import com.launcher.utils.minecraft.json.DownloadInfo;

public class AssetIndexInfo extends DownloadInfo {
	
	protected String id;
	protected long totalSize;
	protected boolean known = true;

	public AssetIndexInfo(String id_) {
		this.id = id_;
		this.url = constantURL("https://s3.amazonaws.com/Minecraft.Download/indexes/" + id + ".json");
		this.known = false;
	}

	public long getTotalSize() {
		return this.totalSize;
	}

	public String getId() {
		return this.id;
	}

	public boolean sizeAndHashKnown() {
		return this.known;
	}
}
