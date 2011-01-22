package org.xindle;

import java.io.File;
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
		return this.paperDir.toString();
	}

	public File[] getPages() {
		File[] files = this.paperDir.listFiles();
		Arrays.sort(files, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return String.valueOf(arg0).compareTo(String.valueOf(arg1));
			}
		});
		/*
		for (int i = 0; i < files.length; i++) {
			logger.info(files[i].getName());
		}
		*/
		return files;
	}
}
