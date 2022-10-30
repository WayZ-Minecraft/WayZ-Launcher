package com.launcher.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.launcher.utils.GameEngine;

public class FileUtil {

	public static String skipFoldersInExtraction = "META-INF/";

	public static void deleteFakeNatives(File targetDir, GameEngine engine) throws IOException {
		File[] listOfFiles = engine.getGameFolder().getNativesDir().listFiles();
		for (int index = 0; index < listOfFiles.length; index++) {
			if (listOfFiles[index].isFile()) {
				if (listOfFiles[index].getName().endsWith(".dll") || listOfFiles[index].getName().endsWith(".dylib") || listOfFiles[index].getName().endsWith(".so")) continue;
				listOfFiles[index].delete();
			} else deleteFolder(listOfFiles[index]);
		}
	}

	public static void unpackNatives(File targetDir, GameEngine engine) throws IOException {
		File[] listOfFiles = engine.getGameFolder().getNativesCacheDir().listFiles();
		for (int index = 0; index < listOfFiles.length; index++) {
			if (listOfFiles[index].isFile()) {
				ZipFile zip = new ZipFile(listOfFiles[index]);
				try {
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();
						File targetFile = new File(targetDir, entry.getName());
						if (targetFile.getParentFile() != null) {
							targetFile.getParentFile().mkdirs();
						}
						if (!entry.isDirectory()) {
							BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));

							byte[] buffer = new byte[2048];
							FileOutputStream outputStream = new FileOutputStream(targetFile);
							BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
							try {
								int length;
								while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
									bufferedOutputStream.write(buffer, 0, length);
								}
							} finally {
								closeSilently(bufferedOutputStream);
								closeSilently(outputStream);
								closeSilently(inputStream);
							}
						}
					}
				} finally {
					zip.close();
				}
			}
		}
	}


	public static void forceDeleteOnExit(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectoryOnExit(file);
		} else file.deleteOnExit();
	}

	private static void deleteDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) return;
		directory.deleteOnExit();
		if (!isSymlink(directory)) {
			cleanDirectoryOnExit(directory);
		}
	}

	public static boolean isSymlink(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		char WINDOWS_SEPARATOR = '\\';
		if (File.separatorChar == WINDOWS_SEPARATOR) {
			return false;
		}
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
			return false;
		}
		return true;
	}

	private static void cleanDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}
		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}
		File[] files = directory.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of " + directory);
		}
		IOException exception = null;
		for (File file : files) {
			try {
				forceDeleteOnExit(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}
		if (null != exception) {
			throw exception;
		}
	}

	public static Charset getCharset() {
		try {
			return Charset.forName("UTF-8");
		} catch (Exception var1) {
			throw new Error("UTF-8 is not supported", var1);
		}
	}

	public static String getDigest(File file, String algorithm, int hashLength) {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
			byte[] ignored = new byte[65536];
			int read;
			do {
				read = stream.read(ignored);
			} while (read > 0);
			return String.format("%1$0" + hashLength + "x",
					new Object[] { new BigInteger(1, stream.getMessageDigest().digest()) });
		} catch (Exception localException) {
		} finally {
			close(stream);
		}
		return null;
	}

	private static void close(Closeable a) {
		try {
			a.close();
		} catch (Exception var2) {
			var2.printStackTrace();
		}
	}

	public static String getSHA(File file) {
		return getDigest(file, "SHA", 40);
	}

	public static boolean matchSHA1(final File file, final String sha1) {
		try {
			return getSHA(file).equals(sha1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException localIOException) {
			}
		}
	}

	public static void copy(File mc, File local) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(mc);
			output = new FileOutputStream(local);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public static void deleteSomething(String path) {
		Path filePath_1 = Paths.get(path);
		try {
			Files.delete(filePath_1);
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", path);
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	public static String getEtag(String etag) {
		if (etag == null)
			etag = "-";
		else if (etag.startsWith("\"") && etag.endsWith("\""))
			etag = etag.substring(1, etag.length() - 1);

		return etag;
	}

	public static String getMD5(final File file) {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			final byte[] buffer = new byte[65536];

			int read = stream.read(buffer);
			while (read >= 1)
				read = stream.read(buffer);
		} catch (final Exception ignored) {
			return null;
		} finally {
			closeSilently(stream);
		}

		return String.format("%1$032x", new Object[] { new BigInteger(1, stream.getMessageDigest().digest()) });
	}

	public static String readMD5(String url) {
		String result = "";
		try {
			Scanner scan = new Scanner((new URL(url)).openStream(), "UTF-8");
			if (!scan.hasNextLine()) {
				scan.close();
			}
			result = scan.nextLine();
			scan.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
