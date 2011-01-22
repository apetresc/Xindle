package org.xindle;

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

	private KMenu makeMenu() {
		KMenu menu = new KMenu();
		KMenuItem home = new KMenuItem("Home");
		KMenuItem search = new KMenuItem("Search");
		KMenuItem history = new KMenuItem("History");
		KMenuItem exit = new KMenuItem("Exit");

		menu.add(home);
		menu.addSeparator();

		menu.add(search);
		menu.add(history);
		menu.addSeparator();

		menu.add(exit);
		return menu;
	}

	public void create(final KindletContext context) {
		UIRoot root = new UIRoot(context);
		
		context.getOrientationController().lockOrientation(KindleOrientation.PORTRAIT);
		
		KMenu menu = makeMenu();

		searchPanel = new SearchPanel(root);

		// set the default panel.
		root.setCurrentPanel(searchPanel, null);
		context.setMenu(menu);
	}
}
