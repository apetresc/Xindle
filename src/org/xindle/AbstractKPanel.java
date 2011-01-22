package org.xindle;

import com.amazon.kindle.kindlet.ui.KPanel;

public abstract class AbstractKPanel extends KPanel {

	/** Action to be executed when the KPanel is shown to the screen. */
	public Runnable onStart() {
		return null;
	}

	/** Action to be executed when the KPanel is hidden from the screen. */
	public Runnable onStop() {
		return null;
	}
}
