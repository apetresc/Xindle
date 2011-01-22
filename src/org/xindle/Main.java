package org.xindle;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KPanel;

/**
 * This sample Kindlet demonstrates the use of the KWTCheckbox.
 * 
 * @author Adrian Petrescu
 * 
 */
public class Main extends AbstractKindlet {
	private KPanel currentPanel = null;
	private KindletContext context = null;
	private SearchPanel searchPanel = new SearchPanel();

	/**
	 * Change the currently displayed panel.
	 * 
	 * @param panel
	 *            Panel to be loaded.
	 * @param action
	 *            Panel to be performed after the loading.
	 * */
	private void setCurrentPanel(KPanel panel, Runnable action) {
		if (currentPanel != null) {
			context.getRootContainer().remove(currentPanel);
		}
		context.getRootContainer().add(panel);
		currentPanel = panel;
		if (action != null) {
			action.run();
		}
	}

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
		this.context = context;
		// set the default panel.
		KMenu menu = makeMenu();
		setCurrentPanel(searchPanel, null);
		context.setMenu(menu);
	}
}
