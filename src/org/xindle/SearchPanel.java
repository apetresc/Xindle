package org.xindle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextField;
import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KFontFamilyName;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;

public class SearchPanel extends KPanel {
	KTextField searchField;
	KindletUIResources res = KindletUIResources.getInstance();

	public SearchPanel() {
		searchField = new KTextField(20);
		final KButton searchBtn = new KButton("Search");
		final KLabel label = new KLabel("Search:");
		final KPanel resultPanel = new KPanel();

		searchField.setBorder(new KLineBorder(1, true));
		label.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		resultPanel.setLayout(new GridLayout(10, 1));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(label, gbc);
		gbc.gridx = 1;
		add(searchField, gbc);
		gbc.gridx = 2;
		add(searchBtn, gbc);
		// setup search panel.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 3;

		gbc.ipady = 40; // make this component tall
		gbc.weightx = 0.0;

		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resultPanel.add(new KLabel(searchField.getText()));
				searchField.setText("");
				searchField.repaint();
				resultPanel.repaint();
			}
		});
		add(resultPanel, gbc);
	}
}
