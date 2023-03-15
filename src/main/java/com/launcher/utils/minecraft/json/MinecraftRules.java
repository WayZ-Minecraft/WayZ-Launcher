package com.launcher.utils.minecraft.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MinecraftRules {

	private List<String> exclude = new ArrayList<String>();

	public MinecraftRules() {
	}

	public MinecraftRules(String[] exclude) {
		if (exclude != null) {
			Collections.addAll(this.exclude, exclude);
		}
	}

	public MinecraftRules(MinecraftRules rules) {
		Iterator<?> var3 = rules.exclude.iterator();
		while (var3.hasNext()) {
			String exclude = (String) var3.next();
			this.exclude.add(exclude);
		}
	}

	public List<String> getExcludes() {
		return this.exclude;
	}

	public boolean shouldExtract(String path) {
		if (this.exclude == null) {
			return true;
		}
		Iterator<String> var3 = this.exclude.iterator();
		while (var3.hasNext()) {
			String rule = (String) var3.next();
			if (path.startsWith(rule)) {
				return false;
			}
		}
		return true;
	}
}