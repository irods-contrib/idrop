package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingWorker;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.idrop.desktop.systraygui.services.IconManager;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropConfig;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transferengine.TransferManager;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class IDropSplashWindow extends JWindow implements Runnable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(IDropSplashWindow.class);

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
            e.printStackTrace();
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        new LauncherTask().run();
    }

    /**
     * Sets the text of the progress bar and its value
     * 
     * @param msg
     *            The message to be displayed in the progress bar
     * @param theVal
     *            An integer value from 0 to 100
     */
    public void setStatus(String msg, int value) {
        System.out.println(value);
        jProgressBar1.setString(msg);
        jProgressBar1.setValue(value);
    }

    class LauncherTask extends SwingWorker<Void, Void> {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done() {
            super.done();
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected Void doInBackground() throws Exception {

            int count = 0;

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setStatus("Initializing...", ++count);

            try {
                // load the properties
                IdropConfig config = new IdropConfig();
                config.setUpLogging();
                iDrop.getiDropCore().setIdropConfig(config);
                iDrop.getiDropCore().setIconManager(new IconManager(iDrop));
            } catch (IdropException ex) {
                Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                throw new IdropRuntimeException("unable to load iDrop configuration", ex);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setStatus("Logging in...", ++count);

            final LoginDialog loginDialog = new LoginDialog(iDrop);
            Toolkit tk = getToolkit();
            int x = (tk.getScreenSize().width - loginDialog.getWidth()) / 2;
            int y = (tk.getScreenSize().height - loginDialog.getHeight()) / 2;
            loginDialog.setLocation(x, y);
            loginDialog.setVisible(true);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setStatus("Starting DB...", ++count);

            try {
                /*
                 * the transfer manager is the central control for the data transfer queue, as well as the maintainer of
                 * the status of the queue. This app listens to the TransferManager to receive updates about what the
                 * queue is doing.
                 */

                TransferManager transferManager = TransferManager.instanceWithCallbackListenerAndUserLevelDatabase(
                        iDrop, iDrop.getiDropCore().getIdropConfig().isLogSuccessfulTransfers(), iDrop.getiDropCore()
                                .getIdropConfig().getTransferDatabaseName());
                iDrop.getiDropCore().setTransferManager(transferManager);
            } catch (JargonException e1) {
                logger.error(e1.getMessage());
                System.exit(1);
            }

            setStatus("Starting Queue...", ++count);
            QueueThread queueThread = new QueueThread(iDrop);
            queueThread.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dispose();
            return null;
        }
    }

}
