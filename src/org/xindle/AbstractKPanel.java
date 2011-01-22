package org.xindle;

import com.amazon.kindle.kindlet.ui.KPanel;

public abstract class AbstractKPanel extends KPanel {

	/** Action to be executed when the KPanel is shown to the screen. */
	public abstract Runnable onStart();
}
