package com.launcher.utils.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Duplicator extends Thread {

	private File source;

	private File dest;

	public Duplicator(File source, File dest) {
		this.source = source;
		this.dest = dest;
	}

	public void run() {
		try {
			startCloning();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startCloning() throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(this.source);
			output = new FileOutputStream(this.dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) output.write(buf, 0, bytesRead);
		} finally {
			input.close();
			output.close();
		}
	}
}