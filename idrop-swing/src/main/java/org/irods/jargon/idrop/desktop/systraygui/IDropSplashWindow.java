package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingWorker;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.engine.TransferManager;
import org.irods.jargon.transfer.engine.TransferManagerImpl;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class IDropSplashWindow extends JWindow {

    /**
     *  
     */
    private static final long serialVersionUID = 1L;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(IDropSplashWindow.class);
    private ImageIcon splashImage = new ImageIcon(IDropSplashWindow.class.getClassLoader().getResource(
            "org/irods/jargon/idrop/desktop/images/iDrop.png"));
    private JLabel jlblImage = new JLabel();
    private JProgressBar jProgressBar1 = new JProgressBar();
    private iDrop iDrop;

    public IDropSplashWindow(iDrop iDrop) {
        super(iDrop);
        this.iDrop = iDrop;
        try {
            init();
        } catch (Exception e) {
            log.error("error initializing iDrop splash window", e);
            throw new IdropRuntimeException(e);
        }
    }

    /**
     * This method stores all initialization commands for the window.
     */
    private void init() throws Exception {
        // jlblImage.setText("jLabel1");
        jlblImage.setIcon(splashImage);
        jProgressBar1.setMinimum(1);
        jProgressBar1.setMaximum(4);
        jProgressBar1.setStringPainted(true);
        this.getContentPane().add(jlblImage, BorderLayout.CENTER);
        this.getContentPane().add(jProgressBar1, BorderLayout.SOUTH);

        Toolkit tk = this.getToolkit();
        int width = 420;
        int height = 315;
        int x = (tk.getScreenSize().width - width) / 2;
        int y = (tk.getScreenSize().height - height) / 2;
        this.setLocation(x, y);
        this.setSize(width, height);
        this.setAlwaysOnTop(false);
        this.setVisible(true);
        this.pack();
    }

   

    /**
     * Sets the text of the progress bar and its value
     * 
     * @param msg
     *            The message to be displayed in the progress bar
     * @param theVal
     *            An integer value from 0 to 100
     */
    public void setStatus(final String msg, final int value) {
        
         java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                jProgressBar1.setString(msg);
        jProgressBar1.setValue(value);
        
            }
        });
       
    }

  
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.SwingWorker#doInBackground()
         */
    /*
        @Override
        protected Void doInBackground() throws Exception {

           
            int count = 0;

           

            try {
                // load the properties
                IdropConfig config = new IdropConfig();
                config.setUpLogging();
                iDrop.getiDropCore().setIdropConfig(config);
                iDrop.getiDropCore().setIconManager(new IconManager(iDrop));
            } catch (IdropException ex) {
                log.error(ex.getMessage());
                MessageManager.showError(IDropSplashWindow.this, ex.getMessage(), "Failed to load iDrop configuration");
                throw new IdropRuntimeException("ex");
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.error("interrupted exception on startup", e);
                throw new IdropRuntimeException(e);
            }

            log.info("logging in in splash background thread");
            setStatus("Logging in...", ++count);

            final LoginDialog loginDialog = new LoginDialog(iDrop);
            Toolkit tk = getToolkit();
            int x = (tk.getScreenSize().width - loginDialog.getWidth()) / 2;
            int y = (tk.getScreenSize().height - loginDialog.getHeight()) / 2;
            loginDialog.setLocation(x, y);
            IDropSplashWindow.this.toBack();
            loginDialog.setAlwaysOnTop(true);
            loginDialog.toFront();
            loginDialog.setVisible(true);

            IDropSplashWindow.this.toFront();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.error("interrupted exception on startup", e);
                throw new IdropRuntimeException(e);
            }

            log.info("starting db in splash background thread");
            setStatus("Starting DB...", ++count);

            try {
               
                TransferManager transferManager = new TransferManagerImpl(iDrop.getiDropCore().getIrodsFileSystem(), iDrop, iDrop.getiDropCore().getIdropConfig().isLogSuccessfulTransfers());
                iDrop.getiDropCore().setTransferManager(transferManager);
            } catch (JargonException e) {
                log.error("jargon exception on startup", e);
                throw new IdropRuntimeException(e);
            }

           
            List<LocalIRODSTransfer> currentQueue = iDrop.getiDropCore().getTransferManager().getCurrentQueue();

            if (!currentQueue.isEmpty()) {
                int result = JOptionPane.showConfirmDialog((Component) null, "Transfers are waiting to process, restart transfer?",
                        "iDrop Transfers in Progress", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION) {
                    iDrop.getiDropCore().getTransferManager().pause();
                }
            }

            log.info("starting transfer mgr queue");
            setStatus("Starting Queue...", ++count);

            QueueThread queueThread = new QueueThread(iDrop);
            queueThread.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("interrupted exception on startup", e);
                throw new IdropRuntimeException(e);
            }

            dispose();
            return null;
        }
    }
*/
}
