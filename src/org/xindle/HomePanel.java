package org.xindle;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTSelectableLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextArea;
import com.amazon.kindle.kindlet.ui.KTextField;
import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KFontFamilyName;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;

public class HomePanel extends AbstractKPanel {
	KindletUIResources res = KindletUIResources.getInstance();
	final KLabel label = new KLabel("Newest Papers: (loading...)");
	final KButton get_btn = new KButton("Get Papers");
	final KButton browse_btn = new KButton("My Papers");
	final KPanel resultPanel = new KPanel();
	private UIRoot root;
	Logger logger = Logger.getLogger(HomePanel.class);

	public HomePanel(final UIRoot root) {
		this.root = root;
		

		// download and parse the feed
		final String str_titles[] = new String[3];
		final String str_abstracts[] = new String[3];
		final String str_ids[] = new String[3];

		get_btn.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		get_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				root.setCurrentPanel(root.searchPanel);
			}
		});
		browse_btn.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		browse_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				root.setCurrentPanel(root.downloadedPanel);
			}
		});

		label.setFont(res.getFont(KFontFamilyName.SANS_SERIF, 25));

		Runnable run = new Runnable() {
			public void run() {
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder parser = factory.newDocumentBuilder();
					Document document = parser
							.parse("http://export.arxiv.org/rss/math");
					NodeList items = document.getChildNodes().item(0)
							.getChildNodes();
					int item_num = 0;
					for (int n = 0; n < items.getLength() && item_num < 3; n++) {
						Node item = items.item(n);
						if (item.getNodeName().equals("item")) {
							NodeList data = item.getChildNodes();
							for (int m = 0; m < data.getLength(); m++) {
								Node each_data = data.item(m);
								if (each_data.getNodeName().equals("title")) {
									str_titles[item_num] = each_data
											.getTextContent();
								}
								if (each_data.getNodeName().equals(
										"description")) {
									str_abstracts[item_num] = each_data
											.getTextContent()
											.substring(3)
											.substring(
													0,
													each_data.getTextContent()
															.length() - 8)
											.replace('\n', ' ');
								}
								if (each_data.getNodeName().equals(
								"link")) {
							str_ids[item_num] = Util.getIdFromUrl(each_data
									.getTextContent());
						}
							}
							item_num++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						// build new stuff list
						GridBagConstraints gbc = new GridBagConstraints();
						gbc.gridy = 2;
						gbc.gridx = 0;
						gbc.gridwidth = 2;
						gbc.weightx = 1;
						gbc.insets = new Insets(0, 0, 0, 0);
						for (int n = 0; n < 3; n++) {
							// build ui elements
							Result result = new Result(str_titles[n], str_abstracts[n], str_ids[n]);
							addResult(result, false);
						}
						label.setText("Newest Papers:");
						repaint();
					}
				});

			}
		};
		Thread t = new Thread(run);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 1;

		gbc.gridx = 0;
		gbc.insets = new Insets(30, 30, 30, 30);
		add(browse_btn, gbc);

		gbc.gridx = 1;
		add(get_btn, gbc);
		
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		add(label, gbc);

		t.start();
		root.homePanel = this;

		gbc.gridy = 2;
		gbc.ipady = 500;
		add(resultPanel, gbc);
		//this.root.setCurrentPanel(searchPanel);
	}

	/** Add a search result to display. */
	public void addResult(final Result result, boolean focus) {
		KWTSelectableLabel selectable = new KWTSelectableLabel(result.title);
		selectable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// when the item is selected.
				logger.info(result);
				root.setCurrentPanel(root.moreInfoPanel);
				root.moreInfoPanel.setResult(result);
			}
		});
		resultPanel.add(selectable);
		if (focus) {
			selectable.requestFocus();
		}

		String summary;
		// trim abstracts
		if (result.summary != null && result.summary.length() > 250) {
			summary = result.summary.substring(
					0, 250) + "...";
		} else {
			summary = result.summary;
		}
		KTextArea textarea = new KTextArea(summary);
		textarea.setEditable(false);
		textarea.setEnabled(false);
		resultPanel.add(textarea);
	}
	
	public Runnable onStart() {
		return new Runnable() {
			public void run() {
				browse_btn.requestFocus();
				root.rootContainer.repaint();
			}
		};
	}
}
