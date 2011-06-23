/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * iDropLiteApplet.java
 *
 * Created on Jun 7, 2011, 4:10:11 PM
 */

package org.irods.jargon.idrop.lite;

import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.util.logging.Logger;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;

import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class iDropLiteApplet extends javax.swing.JApplet {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);
    private iDropLiteCore iDropCore = null;
    private IRODSAccount irodsAccount = null;
    //private IRODSTree irodsTree = null;
    protected String host;
    protected Integer port;
    protected String zone;
    protected String user;
    protected String defaultStorageResource;
    protected String tempPswd;
    protected String absPath;
    //protected String sessionID;

    /** Initializes the applet iDropLiteApplet */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    getAppletParams();
                    initComponents();
                    doStartup();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    protected void getAppletParams() {

        // FIX THIS - what to do if parameters do not exist (cannot login message??)
        this.host = getParameter("host");
        this.port = Integer.parseInt(getParameter("port"));
        this.user = getParameter("user");
        this.zone = getParameter("zone");
        this.defaultStorageResource = getParameter("defaultStorageResource");
        this.tempPswd = getParameter("password");
        this.absPath = getParameter("absPath");

    }


    private boolean processLogin() throws NumberFormatException {

        try {
            log.debug("creating account with applet params");
            log.info("host:{}", host);
            log.info("port:{}", port);
            log.info("user:{}", user);
            log.info("zone:{}", zone);
            log.info("resource:{}", defaultStorageResource);
            log.info("absPath:{}", absPath);
        } catch (Exception ex) {
            //Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            showIdropException(ex);
            return true;
        }

        this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);
        // I figure at this point, it's safe to set the preferences...note that we are not caching password
        //iDropCore.getPreferences().put(PREF_LOGIN_HOST, txtHost.getText());
        //iDropCore.getPreferences().put(PREF_LOGIN_ZONE, txtZone.getText());
        //iDropCore.getPreferences().put(PREF_LOGIN_RESOURCE, txtResource.getText());
        //iDropCore.getPreferences().put(PREF_LOGIN_USERNAME, txtUserName.getText());

        IRODSFileSystem irodsFileSystem = null;
        try {
            irodsFileSystem = IRODSFileSystem.instance();
            final UserAO userAO = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
            //final User loggedInUser = userAO.findByName(txtUserName.getText());
            iDropCore.setIrodsAccount(irodsAccount);
        } catch (JargonException ex) {
            if (ex.getMessage().indexOf("Connection refused") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return true;
            } else if (ex.getMessage().indexOf("Connection reset") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return true;
            } else if (ex.getMessage().indexOf("io exception opening socket") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return true;
            } else {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("login error - unable to log in, or invalid user id");
                return true;
            }
        } finally {
            if (irodsFileSystem != null) {
                try {
                    irodsFileSystem.close();
                } catch (JargonException ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }


    protected void doStartup() {
        log.info("initiating startup sequence...");

        log.info("creating idropCore...");
        iDropCore = new iDropLiteCore();

        try {
            iDropCore.setIrodsFileSystem(IRODSFileSystem.instance());
        } catch (JargonException ex) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
        }

        processLogin();
    }

    public void showIdropException(Exception idropException) {
        JOptionPane.showMessageDialog(this, idropException.getMessage(), "iDROP Exception", JOptionPane.WARNING_MESSAGE);
    }

    public void showMessageFromOperation(final String messageFromOperation) {

        final iDropLiteApplet thisIdropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(thisIdropGui, messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        tabIrodsViews = new javax.swing.JTabbedPane();
        pnlIrodsTreeView = new javax.swing.JPanel();
        pnlIrodsTreeToolbar = new javax.swing.JPanel();
        bntRefreshIrodsTree = new javax.swing.JButton();
        pnlTreeMaster = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnlIrodsTreeView.setLayout(new java.awt.BorderLayout());

        bntRefreshIrodsTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bntRefreshIrodsTree.setLabel("Refresh");
        pnlIrodsTreeToolbar.add(bntRefreshIrodsTree);

        pnlIrodsTreeView.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlTreeMaster.setLayout(new java.awt.BorderLayout());
        pnlIrodsTreeView.add(pnlTreeMaster, java.awt.BorderLayout.SOUTH);
        pnlIrodsTreeView.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        tabIrodsViews.addTab("iRODS Tree View", pnlIrodsTreeView);

        org.jdesktop.layout.GroupLayout pnlMainLayout = new org.jdesktop.layout.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(tabIrodsViews, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntRefreshIrodsTree;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JPanel pnlIrodsTreeView;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTreeMaster;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JTabbedPane tabIrodsViews;
    // End of variables declaration//GEN-END:variables

}
