package com.launcher.utils.minecraft.json;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.launcher.utils.minecraft.CompatibilityRule;
import com.launcher.utils.minecraft.Substitutor;
import com.photon.util.os.OperatingSystem;


public class MinecraftLibrary {

	private Substitutor SUBSTITUTOR = new Substitutor(new HashMap<String, String>());

	protected String name;

	public List<CompatibilityRule> rules;

	protected Map<OperatingSystem, String> natives;

	protected MinecraftRules extract;

	protected LibraryDownloadInfo downloads;

	private String url;

	private boolean skipped = false;

	public MinecraftLibrary() {
	}

	public MinecraftLibrary(String name) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Library name cannot be null or empty");
		}
		this.name = name;
	}
	
	public boolean appliesToCurrentEnvironment() {
		if (this.rules == null)
			return true;
		CompatibilityRule.Action lastAction = CompatibilityRule.Action.disallow;

		for (CompatibilityRule compatibilityRule : this.rules) {
			CompatibilityRule.Action action = compatibilityRule.getAppliedAction();
			if (action != null)
				lastAction = action;

		}
		return (lastAction == CompatibilityRule.Action.allow);
	}

	public MinecraftLibrary(MinecraftLibrary library) {
		this.name = library.name;
		this.url = library.url;
		if (library.extract != null) {
			this.extract = new MinecraftRules(library.extract);
		}
		if (library.rules != null) {
			this.rules = new ArrayList<CompatibilityRule>();
			for (CompatibilityRule compatibilityRule : library.rules) {
				this.rules.add(new CompatibilityRule(compatibilityRule));
			}
		}
		if (library.natives != null) {
			this.natives = new LinkedHashMap<OperatingSystem, String>();
			for (Map.Entry<OperatingSystem, String> entry : library.getNatives().entrySet()) {
				this.natives.put(entry.getKey(), entry.getValue());
			}
		}
		if (library.downloads != null) {
			this.downloads = new LibraryDownloadInfo(library.downloads);
		}
	}

	public MinecraftLibrary addNative(OperatingSystem operatingSystem, String name) {
		if ((operatingSystem == null) || (!operatingSystem.isSupported())) {
			throw new IllegalArgumentException("Cannot add native for unsupported OS");
		}
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Cannot add native for null or empty name");
		}
		if (this.natives == null) {
			this.natives = new EnumMap<OperatingSystem, String>(OperatingSystem.class);
		}
		this.natives.put(operatingSystem, name);
		return this;
	}

	public Map<OperatingSystem, String> getNatives() {
		return this.natives;
	}

	public boolean hasNatives() {
		return this.natives != null;
	}

	public MinecraftRules getExtractRules() {
		return this.extract;
	}

	public List<CompatibilityRule> getCompatibilityRules() {
		return this.rules;
	}

	public String getName() {
		return this.name;
	}

	public MinecraftLibrary setExtractRules(MinecraftRules rules) {
		this.extract = rules;
		return this;
	}

	public String getArtifactBaseDir() {
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
		}
		String[] parts = this.name.split(":", 3);
		return String.format("%s/%s/%s", new Object[] { parts[0].replaceAll("\\.", "/"), parts[1], parts[2] });
	}

	public String getArtifactPath() {
		return getArtifactPath(null);
	}

	public String getArtifactPath(String classifier) {
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
		}
		return String.format("%s/%s", new Object[] { getArtifactBaseDir(), getArtifactFilename(classifier) });
	}

	public String getArtifactFilename(String classifier) {
		if (this.name == null) {
			throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
		}
		String[] parts = this.name.split(":", 3);
		String result;
		if (classifier == null) {
			result = String.format("%s-%s.jar", new Object[] { parts[1], parts[2] });
		} else {
			result = String.format("%s-%s%s.jar", new Object[] { parts[1], parts[2], "-" + classifier });
		}
		return SUBSTITUTOR.replace(result);
	}

	@Deprecated
	public String getArtifactCustom(String name) {
		String[] split = name.split(":");
		String libName = split[1];
		String libVersion = split[2];
		return libName + "-" + libVersion + ".jar";
	}

	public String getArtifactNatives(String libArg) {
		String[] split = getName().split(":");
		String libName = split[1];
		String libVersion = split[2];
		return libName + "-" + libVersion + "-" + libArg + ".jar";
	}

	public String getPlainName() {
		String[] split = this.name.split(":", 3);
		return split[0] + "." + split[1];
	}

	public boolean isSkipped() {
		return this.skipped;
	}

	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}

	public LibraryDownloadInfo getDownloads() {
		return downloads;
	}

	public void setDownloads(LibraryDownloadInfo downloads) {
		this.downloads = downloads;
	}

	public String toString() {
		return "Library{name='" + this.name + '\'' + ", rules=" + this.rules + ", natives=" + this.natives
				+ ", extract=" + this.extract + '}';
	}
}