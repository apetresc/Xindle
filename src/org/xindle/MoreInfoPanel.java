package org.xindle;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTSelectableLabel;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.net.ConnectivityHandler;
import com.amazon.kindle.kindlet.net.NetworkDisabledDetails;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KTextArea;

public class MoreInfoPanel extends AbstractKPanel {
	private UIRoot root;
	private KWTSelectableLabel downloadButton;
	private DownloadHandler downloadHandler;
	
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

	public MoreInfoPanel(final UIRoot root) {
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
		downloadHandler = new DownloadHandler();
		downloadButton = new KWTSelectableLabel("Download this paper");
		downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.info("About to download: " + result.id);
                root.context.getConnectivity().submitConnectivityRequest(downloadHandler);
            }
		});
		add(downloadButton, gbc);
	}

	   class DownloadHandler implements ConnectivityHandler {

	        public void connected() throws InterruptedException {
	            KindletContext context = root.context;
	            try {
                    URL url = new URL("http://ec2-174-129-215-245.compute-1.amazonaws.com/download?arxiv_id=" + result.id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        int i = in.read();
                        List bytes = new ArrayList();
                        while (i >= 0) {
                            byte b = (byte) i;
                            bytes.add(new Byte(b));
                            i = in.read();
                        }
                        Byte[] response = (Byte[]) bytes.toArray(new Byte[0]);
                        byte[] presponse = new byte[response.length];
                        for (int j = 0; j < presponse.length; j++) {
                            presponse[j] = response[j].byteValue();
                        }
                        String responseString = new String(presponse);
                        logger.info("Got response: " + responseString);
                    } else {
                        logger.warn("Got non-OK response: " + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
	            
	        }

	        public void disabled(NetworkDisabledDetails detail)
	                throws InterruptedException {
	            // do nothing.
	        }
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
