package org.xindle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootCategory;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KindleOrientation;

/**
 * This sample Kindlet demonstrates the use of the KWTCheckbox.
 * 
 * @author Adrian Petrescu
 * 
 */
public class Main extends AbstractKindlet {
	private SearchPanel searchPanel = null;
	private HomePanel homePanel = null;

	private UIRoot root = null;
	Logger logger = Logger.getLogger(Main.class);
	private MoreInfoPanel moreInfoPanel;

	private KMenu makeMenu() {
		KMenu menu = new KMenu();
		KMenuItem home = new KMenuItem("Home");
		KMenuItem my_papers = new KMenuItem("My Papers");
		KMenuItem search = new KMenuItem("Search");
		KMenuItem exit = new KMenuItem("Exit");

		menu.add(home);
		menu.addSeparator();

		menu.add(my_papers);
		menu.add(search);
		menu.addSeparator();

		menu.add(exit);
		// actual logic.
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				root.setCurrentPanel(root.searchPanel);
			}
		});
		home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				root.setCurrentPanel(root.homePanel);
			}
		});
		my_papers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				root.setCurrentPanel(root.downloadedPanel);
			}
		});
		return menu;
	}

	public void create(final KindletContext context) {
		logger.info("Starting application");
		root = new UIRoot(context);

		context.getOrientationController().lockOrientation(
				KindleOrientation.PORTRAIT);

		KMenu menu = makeMenu();

		Util util = new Util(context);
		util.getAllPapers();
		searchPanel = new SearchPanel(root);
		homePanel = new HomePanel(root);
		moreInfoPanel = new MoreInfoPanel(root);
		new DownloadedPanel(root);

		// set the default panel.
		root.setCurrentPanel(homePanel);
		// root.setCurrentPanel(searchPanel);
		context.setMenu(menu);
	}
}
