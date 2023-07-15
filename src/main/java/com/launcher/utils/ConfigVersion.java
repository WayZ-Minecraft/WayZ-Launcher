package com.launcher.utils;

import com.photon.util.TranslationManager;

public class ConfigVersion {

	public String language = TranslationManager.locale_en.getLanguage();
	public String allocatedram = "2";
	public String vmarguments = "";
	public String screenWidth = "800";
	public String screenHeight = "600";
	
	public ConfigVersion() {}
	
	public ConfigVersion(ConfigVersion o) { setValue(o); }
	
	public void setValue(ConfigVersion o) {
		if(!this.language.equals(o.language)) this.language = o.language;
		if(!this.allocatedram.equals(o.allocatedram)) this.allocatedram = o.allocatedram;
		if(!this.vmarguments.equals(o.vmarguments)) this.vmarguments = o.vmarguments;
		if(!this.screenWidth.equals(o.screenWidth)) this.screenWidth = o.screenWidth;
		if(!this.screenHeight.equals(o.screenHeight)) this.screenHeight = o.screenHeight;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConfigVersion cfg) {
			final boolean isEqual = this.language.equals(cfg.language) && this.allocatedram.equals(cfg.allocatedram) 
				&& this.vmarguments.equals(cfg.vmarguments) && this.screenWidth.equals(cfg.screenWidth) && this.screenHeight.equals(cfg.screenHeight);
			return isEqual;
		}
		return false;
	}
}
