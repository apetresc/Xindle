package org.xindle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTSelectableLabel;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KTextArea;

public class MoreInfoPanel extends AbstractKPanel {
	private UIRoot root;
	KLabel titleLabel = new KLabel("TITLES COMES HERE.");
	KLabelMultiline summaryArea = new KLabelMultiline("SUMMARY COMES HERE.");
	Logger logger = Logger.getLogger(MoreInfoPanel.class);
	KeyEventDispatcher eventDispatcher = new KeyEventDispatcher() {
		public boolean dispatchKeyEvent(KeyEvent evt) {
			logger.info("KeyCode:" + evt.getKeyCode());
			if (evt.getKeyCode() == KindleKeyCodes.VK_FIVE_WAY_LEFT) {
				// on "left" key, move back to the search results.
				logger.info("Unloading");
				root.setCurrentPanel(root.searchPanel);
				return true;
			}
			return false;
		}
	};
	private Result result;

	public MoreInfoPanel(UIRoot root) {
		this.root = root;
		root.moreInfoPanel = this;

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		add(titleLabel, gbc);
		gbc.gridy++;
		add(summaryArea, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		add(new KWTSelectableLabel("Download this paper."), gbc);
	}

	public Runnable onStart() {
		return new Runnable() {
			public void run() {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.addKeyEventDispatcher(eventDispatcher);
				root.rootContainer.repaint();
				root.currentPanel.requestFocus();
			}
		};
	}

	public Runnable onStop() {
		return new Runnable() {
			public void run() {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.removeKeyEventDispatcher(eventDispatcher);
			}
		};
	}

	public void setResult(Result result) {
		this.result = result;
		titleLabel.setText("Paper:" + result.title);
		summaryArea.setText("Summary:" + result.summary);
	}
}
