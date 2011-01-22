package org.xindle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Result;

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

	public SearchPanel(UIRoot root) {
		this.root = root;
		searchField = new KTextField(20);
		final KButton searchBtn = new KButton("Search");
		final KLabel label = new KLabel("Search:");

		searchField.setBorder(new KLineBorder(1, true));
		label.setFont(res.getFont(KFontFamilyName.MONOSPACE, 30));
		resultPanel.setLayout(new GridLayout(40, 1));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
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

		gbc.weighty = 1.0;
		gbc.weightx = 0.0;

		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String searchTerm = searchField.getText();
				resultPanel.add(new KLabel(searchTerm));
				searchField.setText("");
				search(searchTerm);
				searchField.repaint();
				resultPanel.repaint();
			}
		});
		add(resultPanel, gbc);
		root.searchPanel = this;
	}

	public void search(String term) {
		try {
			KindletContext context = root.context;
			context.getProgressIndicator().setString("Connecting");
			new SearchHandler().connected();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class SearchHandler implements ConnectivityHandler {
		public void connected() throws InterruptedException {
			KindletContext context = root.context;
			try {
				String urlstr = "http://google.com";
				URL url;
				url = new URL(urlstr);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					resultPanel.add(new KLabel(reader.readLine()));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			context.getProgressIndicator().setString("");
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
