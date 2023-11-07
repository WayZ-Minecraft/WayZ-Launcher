package com.launcher.utils;

import java.time.temporal.ChronoUnit;

import com.photon.informations.PhotonUpdaterManager.UpdateChannel;
import com.photon.util.TranslationManager;

public class ConfigVersion {

	public String language = TranslationManager.locale_en.getLanguage();
	public double allocatedram = 2;
	public String vmarguments = "";
	public boolean useCustomSize = false;
	public String screenWidth = "800";
	public String screenHeight = "600";
	public boolean keep_open = false;
	public boolean send_reports = true;
	public boolean autoRAM = false;
	public boolean systemLang = true;
	public EnumLogAutoClearTimer autoClearLogTimer = EnumLogAutoClearTimer.ONE_WEEK;
	public UpdateChannel versionChannel = UpdateChannel.STABLE;
	
	public ConfigVersion() {}
	
	public ConfigVersion(ConfigVersion o) { setValue(o); }
	
	public void setValue(ConfigVersion o) {
		if(!this.language.equals(o.language)) this.language = o.language;
		if(this.allocatedram == o.allocatedram) this.allocatedram = o.allocatedram;
		if(!this.vmarguments.equals(o.vmarguments)) this.vmarguments = o.vmarguments;
		if(this.useCustomSize != o.useCustomSize) this.useCustomSize = o.useCustomSize;
		if(!this.screenWidth.equals(o.screenWidth)) this.screenWidth = o.screenWidth;
		if(!this.screenHeight.equals(o.screenHeight)) this.screenHeight = o.screenHeight;
		if(this.keep_open != o.keep_open) this.keep_open = o.keep_open;
		if(this.send_reports != o.send_reports) this.send_reports = o.send_reports;
		if(this.autoRAM != o.autoRAM) this.autoRAM = o.autoRAM;
		if(this.systemLang != o.systemLang) this.systemLang = o.systemLang;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConfigVersion cfg) {
			final boolean isEqual = this.language.equals(cfg.language) && this.allocatedram == cfg.allocatedram
			&& this.vmarguments.equals(cfg.vmarguments) && this.useCustomSize == cfg.useCustomSize 
			&& this.screenWidth.equals(cfg.screenWidth) && this.screenHeight.equals(cfg.screenHeight)
			&& this.keep_open == cfg.keep_open && this.send_reports == cfg.send_reports && this.autoRAM == cfg.autoRAM && this.systemLang == cfg.systemLang
			;
			return isEqual;
		}
		return false;
	}

	public static enum EnumLogAutoClearTimer {
		ONE_DAY(1, ChronoUnit.DAYS), FIVE_DAYS(5, ChronoUnit.DAYS), ONE_WEEK(1, ChronoUnit.WEEKS), ONE_MONTH(1, ChronoUnit.MONTHS);

		public int unit;
		public ChronoUnit chronoUnit;

		/**
		 * @param unit The number of units to wait before clearing the logs (1, 5, ...)
		 */
		EnumLogAutoClearTimer(int unit, ChronoUnit chronoUnit) {
			this.unit = unit;
			this.chronoUnit = chronoUnit;
		}
	}
}
