package org.xindle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;

public class Paper {
	public static final Logger logger = Logger.getLogger(Paper.class);

	final File paperDir;

	public Paper(File paperDir) {
		this.paperDir = paperDir;
	}

	public int numPages() {
		return this.paperDir.listFiles().length;
	}

	public String getName() {
		File file = new File(this.paperDir, "meta.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			return reader.readLine();
		} catch (IOException e) {
			logger.warn("IOError");
			return "Unknown Name";
		}
		// return this.paperDir.toString();
	}

	public File[] getPages() {
		File[] files = this.paperDir.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String name) {
				return name.endsWith(".png");
			}
		});
		Arrays.sort(files, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return String.valueOf(arg0).compareTo(String.valueOf(arg1));
			}
		});
		/*
		 * for (int i = 0; i < files.length; i++) {
		 * logger.info(files[i].getName()); }
		 */
		return files;
	}
}
