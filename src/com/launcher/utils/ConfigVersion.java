package com.launcher.utils;

import com.photon.util.TranslationManager;

public class ConfigVersion {

	public String language = TranslationManager.locale_en.getLanguage();
	public String allocatedram = "2";
	public String vmarguments = "-XX:+CMSIncrementalMode";
	
	public ConfigVersion() {}
	
	public ConfigVersion(ConfigVersion o) { setValue(o); }
	
	public void setValue(ConfigVersion o) {
		if(!this.language.equals(o.language)) { this.language = o.language; }
		if(!this.allocatedram.equals(o.allocatedram)) { this.allocatedram = o.allocatedram; }
		if(!this.allocatedram.equals(o.allocatedram)) { this.allocatedram = o.allocatedram; }
		if(!this.vmarguments.equals(o.vmarguments)) { this.vmarguments = o.vmarguments; }
	}

	public String getAllocatedRam() {
		return this.allocatedram;
	}

	public String getVMArguments() {
		return this.vmarguments;
	}
}
