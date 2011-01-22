package org.xindle;

import java.io.File;
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

	public List getAllPapers() {
		File dataDir = new File(context.getHomeDirectory(), "papers");
		File[] dirs = dataDir.listFiles();

		List output = new ArrayList();
		for (int i = 0; i < dirs.length; i++) {
			output.add(new Paper(dirs[i]));
		}
		logger.info(output);

		return output;
	}
}
