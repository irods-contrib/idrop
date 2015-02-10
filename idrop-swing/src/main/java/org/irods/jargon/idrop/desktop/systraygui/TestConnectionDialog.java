/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.datautils.connectiontester.ConnectionTester;
import org.irods.jargon.datautils.connectiontester.ConnectionTester.TestType;
import org.irods.jargon.datautils.connectiontester.ConnectionTesterConfiguration;
import org.irods.jargon.datautils.connectiontester.ConnectionTesterImpl;
import org.irods.jargon.datautils.connectiontester.ConnectionTestResult;
import org.irods.jargon.datautils.connectiontester.TestResultEntry;
import org.irods.jargon.datautils.connectiontester.TestResultEntry.OperationType;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class TestConnectionDialog extends javax.swing.JDialog {

    private final String portBlockageMsg = "Checking network ports for blockage ...";
    private final String verifyConnectionMsg = "Verifying connection to ";
    private final String verifyLoginMsg = "Verifying login and password ...";
    private final String networkSpeedMsg = "Checking network speed and bandwidth ...";
    private final String verifyDownloadMsg = "Verifying file download ...";
    private final String verifyUploadMsg = "Verifying file upload ...";
    private TestResultEntry savedGetResult = null;
    private TestResultEntry savedPutResult = null;
    private Throwable connectionException = null;
    private IDROPCore idropCore;
    private final ImageIcon okIcon = new ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/dialog-ok-2.png"));
    private final ImageIcon failIcon = new ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/red_X.png"));
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(TestConnectionDialog.class);
    /**
     * Creates new form TestConnectionDialog
     */
    public TestConnectionDialog(final SettingsDialog parent, boolean modal, final IDROPCore idropCore) {
        super(parent, modal);
        this.idropCore = idropCore;
        initComponents();

        new RunTests(this).execute();
    }
    
    private void setLabelIcon(javax.swing.JLabel label, boolean testPassed) {
        
        if (testPassed) {
            label.setIcon(okIcon);
        }
        else {
            label.setIcon(failIcon);
        }
        label.setVisible(true);
    }
    
    private class RunTests extends SwingWorker<Void, Void> {
        
        final JDialog dialog;
        
        public RunTests(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Cursor cursor = dialog.getContentPane().getCursor();
            dialog.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            enableButtons(false);
            
            log.info("running tests");
            final IRODSFileSystem irodsFileSystem = idropCore.getIrodsFileSystem();
            final IRODSAccount irodsAccount = idropCore.irodsAccount();

            boolean portPassed = true;
            boolean hostPassed = true;
            boolean acctPassed = true;
            String irodsHomeDir = irodsAccount.getHomeDirectory();
            String localTempDir = System.getProperty("java.io.tmpdir");

            // host:port and account tests
            lblCheckNetworkPorts.setText(portBlockageMsg);
            lblVerifyConnection.setText(verifyConnectionMsg);
            lblVerifyLogin.setText(verifyLoginMsg);

            log.info("trying connection");
            try {
                idropCore.getIrodsFileSystem().getIRODSAccessObjectFactory()
                         .authenticateIRODSAccount(irodsAccount);
            } catch (AuthenticationException ex) {
                    acctPassed = false;
                    connectionException = ex;
            } catch (JargonException je) {
                    connectionException = je;
                    Throwable cause = je.getCause();
                    if (cause instanceof ConnectException) {
                        portPassed = false;
                    }
                    else
                    if (cause instanceof UnknownHostException) { // je.getCause() == UnknownHostException)
                        hostPassed = false;
                    }
                    else
                    if (cause instanceof InvalidUserException) {
                        acctPassed = false;
                    }
                    else {
                        acctPassed = false;
                    }
            }

            // set status icons for ports and host
            setLabelIcon(lblCheckNetworkPortsIcon, portPassed);
            setLabelIcon(lblVerifyConnectionIcon, hostPassed);
            setLabelIcon(lblVerifyLoginIcon, acctPassed);

            // upload & download tests
            lblFileUpload.setText(verifyUploadMsg);
            lblFileDownload.setText(verifyDownloadMsg);

            // only do the rest if previous test have passed
            if (portPassed & hostPassed & acctPassed) {
            try {
                IRODSFile irodsFile = irodsFileSystem.getIRODSFileFactory(irodsAccount)
                        .instanceIRODSFile(irodsHomeDir);
            } catch (JargonException ex) {
                log.error(ex.toString());
            }

            try {
                File localFile = new File(localTempDir);

                ConnectionTesterConfiguration connectionTesterConfiguration = new ConnectionTesterConfiguration();
                connectionTesterConfiguration.setCleanupOnCompletion(true);
                connectionTesterConfiguration.setIrodsParentDirectory(irodsHomeDir);
                connectionTesterConfiguration.setLocalSourceParentDirectory(localTempDir);

                ConnectionTester connectionTester = new ConnectionTesterImpl(
                        irodsFileSystem.getIRODSAccessObjectFactory(), irodsAccount,
                        connectionTesterConfiguration);
                List<TestType> testTypes = new ArrayList<TestType>();
                testTypes.add(TestType.MEDIUM);
                //testTypes.add(TestType.LARGE);

                ConnectionTestResult actual = connectionTester.runTests(testTypes);
                List<TestResultEntry> results = actual.getTestResults();
                
                lblNetworkSpeed.setText(networkSpeedMsg);
//                TestResultEntry savedGetResult = null;
//                TestResultEntry savedPutResult = null;
                if (results != null) {
                    for (TestResultEntry result : results) {
                        switch(result.getTestType()){
                            case MEDIUM:
                                if (result.getOperationType() == OperationType.GET) {
                                    setLabelIcon(lblFileDownloadIcon, result.isSuccess());
                                    savedGetResult = result;
                                }
                                else
                                if (result.getOperationType() == OperationType.PUT) {
                                    setLabelIcon(lblFileUploadIcon, result.isSuccess());
                                    savedPutResult = result;
                                }
                                break;
                            default:
                                // do nothing for now
                                break;
                        }
                    }
                
                    // populate transfer rates for get (Mbps)
                    StringBuilder summaryText = new StringBuilder();
                    if ((savedGetResult != null) && (savedGetResult.isSuccess())) {
                        setLabelIcon(lblNetworkSpeedIcon, true);
                        float transferRateBytes = (float) savedGetResult.getTransferRateBytesPerSecond();
                        float transferRateMBytes = transferRateBytes/(1024*1024);
                        lblDownloadSpeed.setText("Download speed = " + 
                                                String.valueOf(transferRateMBytes) + 
                                                " Mbps");
                        float estimatedUpload1GbSecs = 0;
                        float estimatedUpload1GbMins = 0;
                        if (transferRateMBytes > 0) {
                            estimatedUpload1GbSecs = 1024/transferRateMBytes;
                            estimatedUpload1GbMins = estimatedUpload1GbSecs/60;
                            summaryText.append("Based on checks, estimated upload time for 1Gb file is ");
                            if (estimatedUpload1GbMins >= 1) {
                                summaryText.append(String.valueOf((int) estimatedUpload1GbMins));
                                summaryText.append(" minutes");
                                int remainingSecs = (int) estimatedUpload1GbSecs % 60;
                                if (remainingSecs >= 1) {
                                    summaryText.append(", ");
                                    summaryText.append(String.valueOf(remainingSecs));
                                    summaryText.append(" seconds");
                                }
                            }
                            else {
                                summaryText.append(", ");
                                    summaryText.append(String.valueOf(estimatedUpload1GbSecs));
                                    summaryText.append(" seconds");    
                            }
                        }
                    }
                    else {
                        setLabelIcon(lblNetworkSpeedIcon, false);
                    }
                    
                    // populate transfer rates for put (Mbps)
                    if ((savedPutResult != null) && (savedPutResult.isSuccess())) {
                        setLabelIcon(lblNetworkSpeedIcon, true);
                        float transferRateBytes = (float) savedPutResult.getTransferRateBytesPerSecond();
                        float transferRateMBytes = transferRateBytes/(1024*1024);
                        lblUploadSpeed.setText("Upload speed = " + 
                                                String.valueOf(transferRateMBytes) +
                                                " Mbps");
                    }
                    
                    lblSummary.setText(summaryText.toString());
                    
                }

            } catch (JargonException ex) {
                Exceptions.printStackTrace(ex);
            }
            }
            else {
                setLabelIcon(lblFileDownloadIcon, false);
                setLabelIcon(lblFileUploadIcon, false);
            }

            dialog.getContentPane().setCursor(cursor);
            enableButtons(true);
            
            return null;
        }
        
    }
    
    private void enableButtons(boolean flag) {
        btnClose.setEnabled(flag);
        btnSendReport.setEnabled(flag);
    }
    
    private String encodeTextForMailTo(String text) {
        
        StringBuilder encodedText = new StringBuilder();
        String newC;
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            newC = String.valueOf(c);
            switch (c) {
                case ' ':
                    newC="%20";
                    break;
                case '\n':
                    newC="%0D%0A";
                    break;
            }
            
            encodedText.append(newC);
        }
        
        return encodedText.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlMain = new javax.swing.JPanel();
        lblCheckNetworkPorts = new javax.swing.JLabel();
        lblVerifyConnection = new javax.swing.JLabel();
        lblVerifyLogin = new javax.swing.JLabel();
        lblFileUpload = new javax.swing.JLabel();
        lblFileDownload = new javax.swing.JLabel();
        lblNetworkSpeed = new javax.swing.JLabel();
        lblCheckNetworkPortsIcon = new javax.swing.JLabel();
        lblUploadSpeed = new javax.swing.JLabel();
        lblDownloadSpeed = new javax.swing.JLabel();
        lblSummary = new javax.swing.JLabel();
        lblVerifyConnectionIcon = new javax.swing.JLabel();
        lblVerifyLoginIcon = new javax.swing.JLabel();
        lblFileUploadIcon = new javax.swing.JLabel();
        lblFileDownloadIcon = new javax.swing.JLabel();
        lblNetworkSpeedIcon = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSendReport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.title")); // NOI18N
        setName("testConnectionDialog"); // NOI18N
        setPreferredSize(new java.awt.Dimension(580, 360));

        pnlTop.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 6, 1));
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 10, 10));
        pnlMain.setPreferredSize(new java.awt.Dimension(490, 284));

        lblCheckNetworkPorts.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblCheckPorts.text_1")); // NOI18N
        lblCheckNetworkPorts.setName("lblCheckPorts"); // NOI18N

        lblVerifyConnection.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblVerifyConnection.text_1")); // NOI18N
        lblVerifyConnection.setName("lblVerifyConnection"); // NOI18N

        lblVerifyLogin.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblVerifyLogin.text_1")); // NOI18N
        lblVerifyLogin.setName("lblVerifyLogin"); // NOI18N

        lblFileUpload.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblFileUpload.text")); // NOI18N
        lblFileUpload.setToolTipText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblFileUpload.toolTipText")); // NOI18N
        lblFileUpload.setName("lblFileUpload"); // NOI18N

        lblFileDownload.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblFileDownload.text")); // NOI18N
        lblFileDownload.setName("lblFileDownload"); // NOI18N

        lblNetworkSpeed.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblNetworkSpeed.text")); // NOI18N
        lblNetworkSpeed.setName("lblNetworkSpeed"); // NOI18N

        lblCheckNetworkPortsIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblCheckNetworkPorts.text")); // NOI18N
        lblCheckNetworkPortsIcon.setName("lblCheckNetworkPorts"); // NOI18N

        lblUploadSpeed.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblUploadSpeed.text")); // NOI18N
        lblUploadSpeed.setName("lblUploadSpeed"); // NOI18N

        lblDownloadSpeed.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblDownloadSpeed.text")); // NOI18N
        lblDownloadSpeed.setName("lblDownloadSpeed"); // NOI18N

        lblSummary.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblSummary.text")); // NOI18N
        lblSummary.setName("lblSummary"); // NOI18N

        lblVerifyConnectionIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.verifyConnectionIcon.text")); // NOI18N
        lblVerifyConnectionIcon.setName("verifyConnectionIcon"); // NOI18N

        lblVerifyLoginIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.verifyLoginIcon.text")); // NOI18N
        lblVerifyLoginIcon.setName("verifyLoginIcon"); // NOI18N

        lblFileUploadIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.verifyFileUploadIcon.text")); // NOI18N
        lblFileUploadIcon.setName("verifyFileUploadIcon"); // NOI18N

        lblFileDownloadIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblFileDownloadIcon.text")); // NOI18N
        lblFileDownloadIcon.setName("lblFileDownloadIcon"); // NOI18N

        lblNetworkSpeedIcon.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.lblNetworkSpeedIcon.text")); // NOI18N
        lblNetworkSpeedIcon.setName("lblNetworkSpeedIcon"); // NOI18N

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVerifyConnection, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCheckNetworkPorts, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVerifyLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFileUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFileDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNetworkSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCheckNetworkPortsIcon)
                    .addComponent(lblVerifyConnectionIcon)
                    .addComponent(lblVerifyLoginIcon)
                    .addComponent(lblFileUploadIcon)
                    .addComponent(lblFileDownloadIcon)
                    .addComponent(lblNetworkSpeedIcon))
                .addContainerGap(353, Short.MAX_VALUE))
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUploadSpeed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDownloadSpeed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCheckNetworkPortsIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCheckNetworkPorts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVerifyConnection)
                    .addComponent(lblVerifyConnectionIcon))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVerifyLogin)
                    .addComponent(lblVerifyLoginIcon))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFileUploadIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFileUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFileDownload)
                    .addComponent(lblFileDownloadIcon))
                .addGap(6, 6, 6)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNetworkSpeed)
                    .addComponent(lblNetworkSpeedIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUploadSpeed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDownloadSpeed)
                .addGap(32, 32, 32)
                .addComponent(lblSummary)
                .addGap(30, 30, 30))
        );

        pnlTop.add(pnlMain, java.awt.BorderLayout.CENTER);

        pnlButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1));
        pnlButtons.setMinimumSize(new java.awt.Dimension(237, 60));
        pnlButtons.setPreferredSize(new java.awt.Dimension(237, 60));
        pnlButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_192_circle_remove.png"))); // NOI18N
        btnClose.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.btnClose.text")); // NOI18N
        btnClose.setMaximumSize(null);
        btnClose.setMinimumSize(new java.awt.Dimension(100, 38));
        btnClose.setName("btnClose"); // NOI18N
        btnClose.setPreferredSize(new java.awt.Dimension(100, 38));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        pnlButtons.add(btnClose);

        btnSendReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_010_envelope.png"))); // NOI18N
        btnSendReport.setText(org.openide.util.NbBundle.getMessage(TestConnectionDialog.class, "TestConnectionDialog.btnSendReport.text")); // NOI18N
        btnSendReport.setMaximumSize(null);
        btnSendReport.setMinimumSize(new java.awt.Dimension(120, 38));
        btnSendReport.setName("btnSendReport"); // NOI18N
        btnSendReport.setPreferredSize(new java.awt.Dimension(150, 38));
        btnSendReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendReportActionPerformed(evt);
            }
        });
        pnlButtons.add(btnSendReport);

        pnlTop.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlTop, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        //TODO: quit any running processes here?
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSendReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendReportActionPerformed
        
        String newline = "%0D%0A";
        
        StringBuilder bodyText = new StringBuilder("TEST%20CONNECTION%20REPORT");
        bodyText.append(newline);
        bodyText.append(newline);
        
        if (connectionException != null) {
            bodyText.append("CONNECTION%20ERRORS:");
            bodyText.append(newline);
            bodyText.append(encodeTextForMailTo(connectionException.toString()));
            bodyText.append(newline);
            bodyText.append(newline);
        }
 
        if (savedPutResult != null) {
            bodyText.append("DETAILS%20OF%20UPLOAD%20TEST:");
            bodyText.append(newline);
            //TODO: check for null here - savedPut and Get
            bodyText.append(encodeTextForMailTo(savedPutResult.toString()));
            bodyText.append(newline);
            if (savedPutResult.getException() != null) {
                bodyText.append("ERRORS:");
                bodyText.append(newline);
                bodyText.append(encodeTextForMailTo(savedPutResult.getException().toString()));
                bodyText.append(newline);
            }
        }

        if (savedGetResult != null) {
            bodyText.append(newline);
            bodyText.append("DETAILS%20OF%20DOWNLOAD%20TEST:");
            bodyText.append(newline);
            bodyText.append(encodeTextForMailTo(savedGetResult.toString()));
            bodyText.append(newline);
            if (savedGetResult.getException() != null) {
                bodyText.append("ERRORS:");
                bodyText.append(newline);
                bodyText.append(encodeTextForMailTo(savedPutResult.getException().toString()));
            }
        }
              
        try {        
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                String emailAddr = idropCore.getIdropConfig().getIdropProperties().getProperty("idrop.admin.email.addr");
                StringBuilder uriText = new StringBuilder("mailto:");
                if (emailAddr != null) {
                    uriText.append(emailAddr);
                }
                uriText.append("?subject=Test%20Connection%20Report&body=");
                uriText.append(bodyText.toString());
                URI uri = new URI(uriText.toString());
                desktop.mail(uri);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnSendReportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSendReport;
    private javax.swing.JLabel lblCheckNetworkPorts;
    private javax.swing.JLabel lblCheckNetworkPortsIcon;
    private javax.swing.JLabel lblDownloadSpeed;
    private javax.swing.JLabel lblFileDownload;
    private javax.swing.JLabel lblFileDownloadIcon;
    private javax.swing.JLabel lblFileUpload;
    private javax.swing.JLabel lblFileUploadIcon;
    private javax.swing.JLabel lblNetworkSpeed;
    private javax.swing.JLabel lblNetworkSpeedIcon;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblUploadSpeed;
    private javax.swing.JLabel lblVerifyConnection;
    private javax.swing.JLabel lblVerifyConnectionIcon;
    private javax.swing.JLabel lblVerifyLogin;
    private javax.swing.JLabel lblVerifyLoginIcon;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTop;
    // End of variables declaration//GEN-END:variables
}
