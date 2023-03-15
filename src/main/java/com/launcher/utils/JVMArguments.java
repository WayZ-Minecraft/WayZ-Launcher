package com.launcher.utils;

import java.util.ArrayList;
import java.util.List;

public class JVMArguments {

	private List<String> jvmArguments;

	public JVMArguments(String[] args) {
		this.jvmArguments = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			this.jvmArguments.add(args[i]);
		}
	}

	public List<String> getJVMArguments() {
		return this.jvmArguments;
	}
}
