package org.xindle;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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
                        byte[] response = new byte[bytes.size()];
                        for (int j = 0; j < response.length; j++) {
                            response[j] = ((Byte) bytes.get(j)).byteValue();
                        }
                        String responseString = new String(response);
                        logger.info("Got response: " + responseString);
                        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials("AKIAIZ3F54ERWCUWT62Q", "lWkVNNwbxXWWhCK9uoUgWFmYIHoU87HLSM8MGR69"));
                        String s3Prefix = responseString.substring(responseString.lastIndexOf('/', responseString.lastIndexOf('/') - 1) + 1, responseString.lastIndexOf('/'));
                        logger.info("Requesting S3 prefix: " + s3Prefix);
                        List pages = s3.listObjects("xindle-docs", s3Prefix).getObjectSummaries();
                        Iterator pagesIt = pages.iterator();
                        int p = 0;
                        File dataDir = new File(context.getHomeDirectory(), "papers");
                        if (!dataDir.exists()) { dataDir.mkdir(); }
                        File docDir = new File(dataDir, result.id);
                        if (!docDir.exists()) { docDir.mkdir(); }
                        while (pagesIt.hasNext()) {
                            S3Object page = s3.getObject(new GetObjectRequest("xindle-docs", ((S3ObjectSummary) pagesIt.next()).getKey()));
                            File dataFile = new File(docDir, result.id + "-page-"+p+".png");
                            InputStream pageData = page.getObjectContent();
                            FileOutputStream pageDataOut = new FileOutputStream(dataFile);
                            i = pageData.read();
                            while (i >= 0) {
                                pageDataOut.write(i);
                                i = pageData.read();
                            }
                            pageData.close();
                            p++;
                        }
                        File metaFile = new File(docDir, "meta.txt");
                        PrintWriter metaDataOut = new PrintWriter(new FileWriter(metaFile));
                        metaDataOut.print(result.title + '\n');
                        metaDataOut.print(result.summary + '\n');
                        metaDataOut.close();
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
