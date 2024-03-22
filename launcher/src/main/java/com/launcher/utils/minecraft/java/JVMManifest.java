package com.launcher.utils.minecraft.java;

import java.util.Map;

public class JVMManifest {

	public Map<String, JVMFile> files;

	public JVMManifest(JVMManifest o) {
		this.files = o.files;
	}

	public Map<String, JVMFile> getFiles() {
		return this.files;
	}
}
