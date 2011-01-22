package org.xindle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.kwt.ui.KWTSelectableLabel;

import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextArea;
import com.amazon.kindle.kindlet.ui.KTextField;
import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KFontFamilyName;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;

public class HomePanel extends KPanel {
	KindletUIResources res = KindletUIResources.getInstance();

	public HomePanel() {
		final KButton get_btn = new KButton("Get Papers");
		final KButton browse_btn = new KButton("Browse Papers");
		final KLabel label = new KLabel("Recent Papers:");

		get_btn.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		browse_btn.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		
		String test_abstract = "long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long long abstract is long noooot";
		if(test_abstract.length() > 250){
			test_abstract = test_abstract.substring(0, 250) + "...";
		}
		KWTSelectableLabel new_title1 = new KWTSelectableLabel("click me");
		final KTextArea new_abstract1 = new KTextArea(test_abstract);
		KWTSelectableLabel new_title2 = new KWTSelectableLabel("asdfsadf");
		final KTextArea new_abstract2 = new KTextArea("loooooooooooooooooooooooong");
		KWTSelectableLabel new_title3 = new KWTSelectableLabel(" f ghfghfghf hgf hgf");
		final KTextArea new_abstract3 = new KTextArea("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		new_abstract1.setEditable(false);
		new_abstract2.setEditable(false);
		new_abstract3.setEditable(false);
		new_abstract1.setEnabled(false);
		new_abstract2.setEnabled(false);
		new_abstract3.setEnabled(false);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridy = 0;
		
		gbc.gridx = 0;
		gbc.insets = new Insets(30, 30, 30, 30);
		add(browse_btn, gbc);
		
		gbc.gridx = 1;
		add(get_btn, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		add(label, gbc);
		gbc.gridy = 2;
		add(new_title1, gbc);
		gbc.gridy = 3;
		add(new_abstract1, gbc);
		gbc.gridy = 4;
		add(new_title2, gbc);
		gbc.gridy = 5;
		add(new_abstract2, gbc);
		gbc.gridy = 6;
		add(new_title3, gbc);
		gbc.gridy = 7;
		add(new_abstract3, gbc);
	}
}
