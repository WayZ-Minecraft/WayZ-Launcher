package com.launcher.utils.minecraft.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryDownloadInfo {

	private DownloadInfo artifact;

	private Map<String, DownloadInfo> classifiers;

	public LibraryDownloadInfo() {
	}

	public LibraryDownloadInfo(LibraryDownloadInfo other) {
		this.artifact = other.artifact;
		if (other.classifiers != null) {
			this.classifiers = new LinkedHashMap<String, DownloadInfo>();
			for (Map.Entry<String, DownloadInfo> entry : other.classifiers.entrySet()) {
				this.classifiers.put(entry.getKey(), new DownloadInfo((DownloadInfo) entry.getValue()));
			}
		}
	}

	public DownloadInfo getDownloadInfo(String classifier) {
		if (classifier == null) {
			return this.artifact;
		}
		return (DownloadInfo) this.classifiers.get(classifier);
	}

	public DownloadInfo getArtifact() {
		return artifact;
	}

	public void setArtifact(DownloadInfo artifact) {
		this.artifact = artifact;
	}

	public Map<String, DownloadInfo> getClassifiers() {
		return classifiers;
	}

	public void setClassifiers(Map<String, DownloadInfo> classifiers) {
		this.classifiers = classifiers;
	}
}