package org.xindle;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTSelectableLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.net.ConnectivityHandler;
import com.amazon.kindle.kindlet.net.NetworkDisabledDetails;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextField;
import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KFontFamilyName;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;

public class SearchPanel extends AbstractKPanel {
	KTextField searchField;
	KindletUIResources res = KindletUIResources.getInstance();
	KPanel resultPanel = new KPanel();
	private UIRoot root;
	public static final int DEFAULT_SEARCH_SIZE = 10;

	Logger logger = Logger.getLogger(SearchPanel.class);

	public SearchPanel(UIRoot root) {
		this.root = root;
		searchField = new KTextField(20);
		final KButton searchBtn = new KButton("Search");
		final KLabel label = new KLabel("Search:");

		searchField.setBorder(new KLineBorder(1, true));
		label.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		resultPanel.setLayout(new GridLayout(DEFAULT_SEARCH_SIZE * 2, 1));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		add(label, gbc);
		gbc.gridx = 1;
		gbc.ipadx = 300;
		add(searchField, gbc);
		gbc.gridx = 2;
		add(searchBtn, gbc);
		// setup search panel.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 3;
		gbc.ipady = 300;
		gbc.insets = new Insets(25, 25, 25, 25);
		add(resultPanel, gbc);

		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String searchTerm = searchField.getText();
				searchField.setText("");
				search(searchTerm);
				searchField.repaint();
				resultPanel.repaint();
			}
		});
		root.searchPanel = this;
	}

	public void search(String term) {
		logger.info("Searcing for the term:" + term);
		try {
			KindletContext context = root.context;
			context.getProgressIndicator().setString("Connecting");
			new SearchHandler(term).connected();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static String generateUrl(String searchTerm, int startIndex, int resultSize) {
		String urlstr = "http://export.arxiv.org/api/query?search_query="
				+ searchTerm + "&start=" + startIndex + "&max_results="
				+ resultSize;
		return urlstr;
	}

	public static class Result {
		String title;
		String summary;

		public Result(String title, String summary) {
			this.title = title;
			this.summary = summary;
		}

		public String toString() {
			return title + " " + summary;
		}
	}

	public List searchUrl(String url) {
		List results = new ArrayList();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document document = parser.parse(url);
			NodeList items = document.getChildNodes().item(0).getChildNodes();

			for (int i = 0; i < items.getLength(); i++) {
				Node item = items.item(i);

				if (item.getNodeName().equals("entry")) {
					NodeList childrens = item.getChildNodes();
					String title = "";
					String summary = "";
					for (int j = 0; j < childrens.getLength(); j++) {
						Node childItem = childrens.item(j);
						if (childItem.getNodeName().equals("title")) {
							title = childItem.getTextContent();
						} else if (childItem.getNodeName().equals("summary")) {
							summary = childItem.getTextContent();
						}
					}
					results.add(new Result(title, summary));
				}
			}
			logger.info(results);
			return results;
		} catch (Exception e) {
			logger.error("Error while searchUrl()");
		}
		return results;
	}

	class SearchHandler implements ConnectivityHandler {
		private String term;

		public SearchHandler(String term) {
			this.term = term;
		}

		public void connected() throws InterruptedException {
			KindletContext context = root.context;
			String url = generateUrl(term, 0, DEFAULT_SEARCH_SIZE);
			context.getProgressIndicator().setString("");
			final List results = searchUrl(url);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					resultPanel.removeAll();
					for (int i = 0; i < results.size(); i++) {
						Result item = (Result) results.get(i);
						resultPanel.add(new KWTSelectableLabel(item.title));
						resultPanel.add(new KLabel(item.summary));
					}
				}
			});
		}

		public void disabled(NetworkDisabledDetails detail)
				throws InterruptedException {
		}
	}

	public Runnable onStart() {
		return new Runnable() {
			public void run() {
				searchField.requestFocus();
			}
		};
	}
}
