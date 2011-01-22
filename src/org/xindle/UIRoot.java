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

	AbstractKPanel homePanel = null;
	AbstractKPanel searchPanel = null;
	MoreInfoPanel moreInfoPanel = null;
	DownloadedPanel downloadedPanel = null;

	AbstractKPanel currentPanel = null;

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
	public void setCurrentPanel(AbstractKPanel panel) {
		if (currentPanel != null) {
			Runnable stopAction = currentPanel.onStop();
			if (stopAction != null) {
				stopAction.run();
			}
			context.getRootContainer().remove(currentPanel);
		}
		context.getRootContainer().add(panel);
		currentPanel = panel;
		panel.repaint();
		Runnable action = panel.onStart();
		if (action != null) {
			action.run();
		}
	}
}
