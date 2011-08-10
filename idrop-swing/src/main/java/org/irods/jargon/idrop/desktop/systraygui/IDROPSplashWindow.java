package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class IDROPSplashWindow extends JWindow {

    /**
     *  
     */
    private static final long serialVersionUID = 1L;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(IDROPSplashWindow.class);
    private ImageIcon splashImage = new ImageIcon(IDROPSplashWindow.class.getClassLoader().getResource(
            "org/irods/jargon/idrop/desktop/images/iDrop.png"));
    private JLabel jlblImage = new JLabel();
    private JProgressBar jProgressBar1 = new JProgressBar();
    private iDrop iDrop;

    public IDROPSplashWindow(final iDrop iDrop) {
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
        jProgressBar1.setMaximum(7);
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
}
