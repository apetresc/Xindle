package org.xindle;

import java.awt.Container;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;

/**
 * Class containing global configuration of the application.
 */
public class UIRoot {
	KindletContext context;
	Container rootContainer;

	KPanel homePanel = null;
	KPanel searchPanel = null;
	KPanel currentPanel = null;

	public UIRoot(KindletContext context) {
		this.context = context;
		this.rootContainer = context.getRootContainer();
	}

	/**
	 * Change the currently displayed panel.
	 * 
	 * @param panel
	 *            Panel to be loaded.
	 * @param action
	 *            Panel to be performed after the loading.
	 * */
	public void setCurrentPanel(KPanel panel, Runnable action) {
		if (currentPanel != null) {
			context.getRootContainer().remove(currentPanel);
		}
		context.getRootContainer().add(panel);
		currentPanel = panel;
		if (action != null) {
			action.run();
		}
	}
}
