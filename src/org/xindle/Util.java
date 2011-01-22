package org.xindle;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazon.kindle.kindlet.KindletContext;

public class Util {
	private final KindletContext context;
	Logger logger = Logger.getLogger(Util.class);

	public Util(KindletContext context) {
		this.context = context;
	}

	public static String getIdFromUrl(String url) {
		String id = url.substring(url.lastIndexOf('/') + 1);
		if (id.endsWith("v1") || id.endsWith("v2")) {
			id = id.substring(0, id.length() - 2);
		}
		return id;
	}

	public List getAllPapers() {
		File dataDir = new File(context.getHomeDirectory(), "papers");
		File[] dirs = dataDir.listFiles();

		List output = new ArrayList();
		for (int i = 0; i < dirs.length; i++) {
			output.add(new Paper(dirs[i]));
		}
		// logger.info(output);

		return output;
	}
}
