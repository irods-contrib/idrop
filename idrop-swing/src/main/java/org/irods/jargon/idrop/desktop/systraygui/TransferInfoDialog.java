/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.border.TitledBorder;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.openide.util.Exceptions;

/**
 *
 * @author lisa
 */
public class TransferInfoDialog extends javax.swing.JDialog {
    
    private final Transfer transfer;
    private List<TransferAttempt> transferAttempts = null;
    private final IDROPCore idropCore;

    /**
     * Creates new form TransferInfoDialog
     */  
    public TransferInfoDialog(javax.swing.JDialog parent, Transfer transfer, IDROPCore idropCore) {
        super(parent, true);
        initComponents();
        this.transfer = transfer;
        this.idropCore = idropCore;
        initTransferInfo();
        populateTransferAttempts();
    }
    
    private void initTransferInfo() {
        String fromPath = null;
        String toPath = null;
        lblTransferType.setText(transfer.getTransferType().toString());
        if (transfer.getTransferType() == TransferType.GET) {
            fromPath = transfer.getIrodsAbsolutePath();
            toPath = transfer.getLocalAbsolutePath();
        }
        else if (transfer.getTransferType() == TransferType.PUT) {
            fromPath = transfer.getLocalAbsolutePath();
            toPath = transfer.getIrodsAbsolutePath();
        }
        
        lblTransferFrom.setText(fromPath);
        lblTransferTo.setText(toPath);
        
        // initialize transfer attempts
        try {
            Transfer transferWithChildren = idropCore.getConveyorService().getQueueManagerService().initializeGivenTransferByLoadingChildren(transfer);
            transferAttempts = transferWithChildren.getTransferAttempts();

        } catch (ConveyorExecutionException ex) {
            Exceptions.printStackTrace(ex); // FIXME: do the right thing here
        }
    }
    
    private void populateTransferAttempts() {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            
//            @Override
//            public void run() {
                // change panel's tile border to plural version if more than 1 attempt
//                int numAttempts = transferAttempts.size();
//                if (numAttempts > 1) {
//                    TitledBorder border = (TitledBorder)jPanel1.getBorder();
//                    border.setTitle("Transfer Attempts");
//                    jPanel1.repaint();
//                }
                // compose panel for each attempt and add to grid bag layout
                int count = 1;
                for (TransferAttempt attempt: transferAttempts) {
                    addAttemptToDialog(attempt, count);
                    count++;
                }
                
                pnlScrollableTransferAttempts.validate();
                pnlScrollableTransferAttempts.repaint();
                pack();
                this.validate();
                this.repaint();
//            }
//        });
    }
    
    private void addAttemptToDialog(TransferAttempt attempt, int count) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM, d, yyyy : h:mm:ss a");
        
        javax.swing.JPanel pnlAttemptInstance = new javax.swing.JPanel();
        pnlAttemptInstance.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Attempt #" + String.valueOf(count)));
        pnlAttemptInstance.setPreferredSize(new java.awt.Dimension(340, 585));
        pnlAttemptInstance.setLayout(new java.awt.BorderLayout());
        
        javax.swing.JPanel pnlAttemptInfo = new javax.swing.JPanel();
        pnlAttemptInfo.setPreferredSize(new java.awt.Dimension(330, 58));
        pnlAttemptInfo.setLayout(new java.awt.GridBagLayout());
        
        javax.swing.JLabel label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.idLabel.text")); // NOI18N
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);
        
        label = new javax.swing.JLabel();
        label.setText(transfer.getId().toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        pnlAttemptInfo.add(label, gridBagConstraints);
        
        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.startLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(dateFormat.format(attempt.getAttemptStart()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.endLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(dateFormat.format(attempt.getAttemptEnd()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.statusLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(attempt.getAttemptStatus().toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lastPathLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(attempt.getLastSuccessfulPath());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);
        
        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.totalFilesLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(String.valueOf(attempt.getTotalFilesCount()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.totalSoFarLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(String.valueOf(attempt.getTotalFilesTransferredSoFar()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        pnlAttemptInstance.add(pnlAttemptInfo, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel pnlAttemptErrors = new javax.swing.JPanel();
        pnlAttemptErrors.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.pnlAttemptErrors.border.title"))); // NOI18N
        pnlAttemptErrors.setPreferredSize(new java.awt.Dimension(340, 250));
        pnlAttemptErrors.setLayout(new java.awt.GridBagLayout());

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.errorsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptErrors.add(label, gridBagConstraints);

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        scrollPane.setMinimumSize(new java.awt.Dimension(23, 65));

        javax.swing.JTextArea textArea = new javax.swing.JTextArea();
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
        scrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 8);
        pnlAttemptErrors.add(scrollPane, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.exceptionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptErrors.add(label, gridBagConstraints);

        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setMinimumSize(new java.awt.Dimension(23, 65));

        textArea = new javax.swing.JTextArea();
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
        textArea.setMinimumSize(new java.awt.Dimension(0, 60));
        scrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 8);
        pnlAttemptErrors.add(scrollPane, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.exceptionDetailsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlAttemptErrors.add(label, gridBagConstraints);

        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setMinimumSize(new java.awt.Dimension(23, 65));

        textArea = new javax.swing.JTextArea();
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
        scrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlAttemptErrors.add(scrollPane, gridBagConstraints);

        pnlAttemptInstance.add(pnlAttemptErrors, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        pnlScrollableTransferAttempts.add(pnlAttemptInstance, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        pnlScrollableTransferAttempts.add(pnlAttemptInstance, gridBagConstraints);
        
//        pnlScrollableTransferAttempts.validate();
//        pnlScrollableTransferAttempts.repaint();
//        pack();
//        this.validate();
//        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new javax.swing.JPanel();
        pnlTransferInfo = new javax.swing.JPanel();
        lblTransferType = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTransferFrom = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTransferTo = new javax.swing.JLabel();
        pnlTransferAttempt = new javax.swing.JPanel();
        pnlTransferAttemptInfo = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollableTransferAttempts = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(540, 600));

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 4, 8, 4));
        pnlMain.setPreferredSize(new java.awt.Dimension(530, 600));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTransferInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 6, 0));
        pnlTransferInfo.setPreferredSize(new java.awt.Dimension(172, 50));

        lblTransferType.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferType.text")); // NOI18N
        pnlTransferInfo.add(lblTransferType);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel4.text")); // NOI18N
        pnlTransferInfo.add(jLabel4);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel5.text")); // NOI18N
        pnlTransferInfo.add(jLabel5);

        lblTransferFrom.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferFrom.text")); // NOI18N
        pnlTransferInfo.add(lblTransferFrom);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel6.text")); // NOI18N
        pnlTransferInfo.add(jLabel6);

        lblTransferTo.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferTo.text")); // NOI18N
        pnlTransferInfo.add(lblTransferTo);

        pnlMain.add(pnlTransferInfo, java.awt.BorderLayout.NORTH);

        pnlTransferAttempt.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 2, 6, 2));
        pnlTransferAttempt.setPreferredSize(new java.awt.Dimension(520, 600));
        pnlTransferAttempt.setRequestFocusEnabled(false);
        pnlTransferAttempt.setLayout(new java.awt.BorderLayout());

        pnlTransferAttemptInfo.setPreferredSize(new java.awt.Dimension(350, 600));
        pnlTransferAttemptInfo.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jPanel1.border.title"))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(350, 600));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 680));

        pnlScrollableTransferAttempts.setPreferredSize(new java.awt.Dimension(340, 58));
        pnlScrollableTransferAttempts.setLayout(new java.awt.GridBagLayout());
        jScrollPane1.setViewportView(pnlScrollableTransferAttempts);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlTransferAttemptInfo.add(jPanel1, java.awt.BorderLayout.CENTER);

        pnlTransferAttempt.add(pnlTransferAttemptInfo, java.awt.BorderLayout.CENTER);

        pnlButtons.setPreferredSize(new java.awt.Dimension(595, 35));
        pnlButtons.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 50));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 35, Short.MAX_VALUE)
        );

        pnlButtons.add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel3.setPreferredSize(new java.awt.Dimension(100, 112));

        btnClose.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel3.add(btnClose);

        pnlButtons.add(jPanel3, java.awt.BorderLayout.EAST);

        pnlTransferAttempt.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlTransferAttempt, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTransferFrom;
    private javax.swing.JLabel lblTransferTo;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlScrollableTransferAttempts;
    private javax.swing.JPanel pnlTransferAttempt;
    private javax.swing.JPanel pnlTransferAttemptInfo;
    private javax.swing.JPanel pnlTransferInfo;
    // End of variables declaration//GEN-END:variables

    
    //////////////////////
//    private void initComponents() {
//        java.awt.GridBagConstraints gridBagConstraints;
//
//        pnlMain = new javax.swing.JPanel();
//        pnlTransferInfo = new javax.swing.JPanel();
//        lblTransferType = new javax.swing.JLabel();
//        jLabel4 = new javax.swing.JLabel();
//        jLabel5 = new javax.swing.JLabel();
//        lblTransferFrom = new javax.swing.JLabel();
//        jLabel6 = new javax.swing.JLabel();
//        lblTransferTo = new javax.swing.JLabel();
//        pnlTransferAttempt = new javax.swing.JPanel();
//        pnlTransferAttemptInfo = new javax.swing.JPanel();
//        jPanel1 = new javax.swing.JPanel();
//        jScrollPane1 = new javax.swing.JScrollPane();
//        pnlScrollableTransferAttempts = new javax.swing.JPanel();
//        pnlSample = new javax.swing.JPanel();
//        pnlAttemptInfo = new javax.swing.JPanel();
//        jLabel1 = new javax.swing.JLabel();
//        lblAttemptId = new javax.swing.JLabel();
//        jLabel2 = new javax.swing.JLabel();
//        lblStartTime = new javax.swing.JLabel();
//        jLabel3 = new javax.swing.JLabel();
//        lblEndTime = new javax.swing.JLabel();
//        jLabel7 = new javax.swing.JLabel();
//        lblStatus = new javax.swing.JLabel();
//        jLabel11 = new javax.swing.JLabel();
//        lblLastPath = new javax.swing.JLabel();
//        jLabel12 = new javax.swing.JLabel();
//        lblTotalFiles = new javax.swing.JLabel();
//        jLabel13 = new javax.swing.JLabel();
//        lblTotalTransferred = new javax.swing.JLabel();
//        pnlAttemptErrors = new javax.swing.JPanel();
//        jLabel14 = new javax.swing.JLabel();
//        jScrollPane5 = new javax.swing.JScrollPane();
//        jTextArea3 = new javax.swing.JTextArea();
//        jLabel8 = new javax.swing.JLabel();
//        jScrollPane2 = new javax.swing.JScrollPane();
//        jTextArea1 = new javax.swing.JTextArea();
//        jLabel9 = new javax.swing.JLabel();
//        jScrollPane3 = new javax.swing.JScrollPane();
//        jTextArea2 = new javax.swing.JTextArea();
//        pnlButtons = new javax.swing.JPanel();
//        jPanel2 = new javax.swing.JPanel();
//        jPanel3 = new javax.swing.JPanel();
//        btnClose = new javax.swing.JButton();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//        setTitle(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.title")); // NOI18N
//        setPreferredSize(new java.awt.Dimension(540, 600));
//
//        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 4, 8, 4));
//        pnlMain.setPreferredSize(new java.awt.Dimension(530, 600));
//        pnlMain.setLayout(new java.awt.BorderLayout());
//
//        pnlTransferInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 6, 0));
//        pnlTransferInfo.setPreferredSize(new java.awt.Dimension(172, 50));
//
//        lblTransferType.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferType.text")); // NOI18N
//        pnlTransferInfo.add(lblTransferType);
//
//        jLabel4.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel4.text")); // NOI18N
//        pnlTransferInfo.add(jLabel4);
//
//        jLabel5.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel5.text")); // NOI18N
//        pnlTransferInfo.add(jLabel5);
//
//        lblTransferFrom.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferFrom.text")); // NOI18N
//        pnlTransferInfo.add(lblTransferFrom);
//
//        jLabel6.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel6.text")); // NOI18N
//        pnlTransferInfo.add(jLabel6);
//
//        lblTransferTo.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTransferTo.text")); // NOI18N
//        pnlTransferInfo.add(lblTransferTo);
//
//        pnlMain.add(pnlTransferInfo, java.awt.BorderLayout.NORTH);
//
//        pnlTransferAttempt.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 2, 6, 2));
//        pnlTransferAttempt.setPreferredSize(new java.awt.Dimension(520, 600));
//        pnlTransferAttempt.setRequestFocusEnabled(false);
//        pnlTransferAttempt.setLayout(new java.awt.BorderLayout());
//
//        pnlTransferAttemptInfo.setPreferredSize(new java.awt.Dimension(350, 600));
//        pnlTransferAttemptInfo.setLayout(new java.awt.BorderLayout());
//
//        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jPanel1.border.title"))); // NOI18N
//        jPanel1.setPreferredSize(new java.awt.Dimension(350, 600));
//        jPanel1.setLayout(new java.awt.BorderLayout());
//
//        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 680));
//
//        pnlScrollableTransferAttempts.setPreferredSize(new java.awt.Dimension(340, 58));
//        pnlScrollableTransferAttempts.setLayout(new java.awt.GridBagLayout());
//
//        pnlSample.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.pnlSample.border.title"))); // NOI18N
//        pnlSample.setPreferredSize(new java.awt.Dimension(340, 585));
//        pnlSample.setLayout(new java.awt.BorderLayout());
//
//        pnlAttemptInfo.setPreferredSize(new java.awt.Dimension(330, 58));
//        pnlAttemptInfo.setLayout(new java.awt.GridBagLayout());
//
//        jLabel1.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel1.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel1, gridBagConstraints);
//
//        lblAttemptId.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblAttemptId.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        gridBagConstraints.weightx = 0.5;
//        pnlAttemptInfo.add(lblAttemptId, gridBagConstraints);
//
//        jLabel2.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel2.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel2, gridBagConstraints);
//
//        lblStartTime.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblStartTime.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblStartTime, gridBagConstraints);
//
//        jLabel3.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel3.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel3, gridBagConstraints);
//
//        lblEndTime.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblEndTime.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblEndTime, gridBagConstraints);
//
//        jLabel7.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel7.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 3;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel7, gridBagConstraints);
//
//        lblStatus.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblStatus.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 3;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblStatus, gridBagConstraints);
//
//        jLabel11.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel11.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 6;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
//        pnlAttemptInfo.add(jLabel11, gridBagConstraints);
//
//        lblLastPath.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblLastPath.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 6;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblLastPath, gridBagConstraints);
//
//        jLabel12.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel12.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel12, gridBagConstraints);
//
//        lblTotalFiles.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTotalFiles.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblTotalFiles, gridBagConstraints);
//
//        jLabel13.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel13.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 5;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptInfo.add(jLabel13, gridBagConstraints);
//
//        lblTotalTransferred.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.lblTotalTransferred.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 5;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        pnlAttemptInfo.add(lblTotalTransferred, gridBagConstraints);
//
//        pnlSample.add(pnlAttemptInfo, java.awt.BorderLayout.CENTER);
//
//        pnlAttemptErrors.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.pnlAttemptErrors.border.title"))); // NOI18N
//        pnlAttemptErrors.setPreferredSize(new java.awt.Dimension(340, 250));
//        pnlAttemptErrors.setLayout(new java.awt.GridBagLayout());
//
//        jLabel14.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel14.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptErrors.add(jLabel14, gridBagConstraints);
//
//        jScrollPane5.setMinimumSize(new java.awt.Dimension(23, 65));
//
//        jTextArea3.setEditable(false);
//        jTextArea3.setColumns(20);
//        jTextArea3.setRows(5);
//        jTextArea3.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jTextArea3.text")); // NOI18N
//        jScrollPane5.setViewportView(jTextArea3);
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridheight = 2;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        gridBagConstraints.weightx = 0.05;
//        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 8);
//        pnlAttemptErrors.add(jScrollPane5, gridBagConstraints);
//
//        jLabel8.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel8.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        pnlAttemptErrors.add(jLabel8, gridBagConstraints);
//
//        jScrollPane2.setMinimumSize(new java.awt.Dimension(23, 65));
//
//        jTextArea1.setEditable(false);
//        jTextArea1.setColumns(20);
//        jTextArea1.setRows(5);
//        jTextArea1.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jTextArea1.text")); // NOI18N
//        jTextArea1.setMinimumSize(new java.awt.Dimension(0, 60));
//        jScrollPane2.setViewportView(jTextArea1);
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.gridheight = 2;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 8);
//        pnlAttemptErrors.add(jScrollPane2, gridBagConstraints);
//
//        jLabel9.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jLabel9.text")); // NOI18N
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.ipadx = 6;
//        gridBagConstraints.ipady = 6;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
//        pnlAttemptErrors.add(jLabel9, gridBagConstraints);
//
//        jScrollPane3.setMinimumSize(new java.awt.Dimension(23, 65));
//
//        jTextArea2.setEditable(false);
//        jTextArea2.setColumns(20);
//        jTextArea2.setRows(5);
//        jTextArea2.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.jTextArea2.text")); // NOI18N
//        jScrollPane3.setViewportView(jTextArea2);
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.gridheight = 2;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
//        pnlAttemptErrors.add(jScrollPane3, gridBagConstraints);
//
//        pnlSample.add(pnlAttemptErrors, java.awt.BorderLayout.SOUTH);
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
//        gridBagConstraints.weightx = 0.5;
//        gridBagConstraints.weighty = 0.5;
//        pnlScrollableTransferAttempts.add(pnlSample, gridBagConstraints);
//
//        jScrollPane1.setViewportView(pnlScrollableTransferAttempts);
//
//        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);
//
//        pnlTransferAttemptInfo.add(jPanel1, java.awt.BorderLayout.CENTER);
//
//        pnlTransferAttempt.add(pnlTransferAttemptInfo, java.awt.BorderLayout.CENTER);
//
//        pnlButtons.setPreferredSize(new java.awt.Dimension(595, 35));
//        pnlButtons.setLayout(new java.awt.BorderLayout());
//
//        jPanel2.setPreferredSize(new java.awt.Dimension(400, 50));
//
//        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
//        jPanel2.setLayout(jPanel2Layout);
//        jPanel2Layout.setHorizontalGroup(
//            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//            .add(0, 400, Short.MAX_VALUE)
//        );
//        jPanel2Layout.setVerticalGroup(
//            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//            .add(0, 35, Short.MAX_VALUE)
//        );
//
//        pnlButtons.add(jPanel2, java.awt.BorderLayout.WEST);
//
//        jPanel3.setPreferredSize(new java.awt.Dimension(100, 112));
//
//        btnClose.setText(org.openide.util.NbBundle.getMessage(TransferInfoDialog.class, "TransferInfoDialog.btnClose.text")); // NOI18N
//        btnClose.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                btnCloseActionPerformed(evt);
//            }
//        });
//        jPanel3.add(btnClose);
//
//        pnlButtons.add(jPanel3, java.awt.BorderLayout.EAST);
//
//        pnlTransferAttempt.add(pnlButtons, java.awt.BorderLayout.SOUTH);
//
//        pnlMain.add(pnlTransferAttempt, java.awt.BorderLayout.CENTER);
//
//        getContentPane().add(pnlMain, java.awt.BorderLayout.PAGE_END);
//
//        pack();
//    }// </editor-fold>
//
//    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
//        this.dispose();
//    }
//
//    // Variables declaration - do not modify
//    private javax.swing.JButton btnClose;
//    private javax.swing.JLabel jLabel1;
//    private javax.swing.JLabel jLabel11;
//    private javax.swing.JLabel jLabel12;
//    private javax.swing.JLabel jLabel13;
//    private javax.swing.JLabel jLabel14;
//    private javax.swing.JLabel jLabel2;
//    private javax.swing.JLabel jLabel3;
//    private javax.swing.JLabel jLabel4;
//    private javax.swing.JLabel jLabel5;
//    private javax.swing.JLabel jLabel6;
//    private javax.swing.JLabel jLabel7;
//    private javax.swing.JLabel jLabel8;
//    private javax.swing.JLabel jLabel9;
//    private javax.swing.JPanel jPanel1;
//    private javax.swing.JPanel jPanel2;
//    private javax.swing.JPanel jPanel3;
//    private javax.swing.JScrollPane jScrollPane1;
//    private javax.swing.JScrollPane jScrollPane2;
//    private javax.swing.JScrollPane jScrollPane3;
//    private javax.swing.JScrollPane jScrollPane5;
//    private javax.swing.JTextArea jTextArea1;
//    private javax.swing.JTextArea jTextArea2;
//    private javax.swing.JTextArea jTextArea3;
//    private javax.swing.JLabel lblAttemptId;
//    private javax.swing.JLabel lblEndTime;
//    private javax.swing.JLabel lblLastPath;
//    private javax.swing.JLabel lblStartTime;
//    private javax.swing.JLabel lblStatus;
//    private javax.swing.JLabel lblTotalFiles;
//    private javax.swing.JLabel lblTotalTransferred;
//    private javax.swing.JLabel lblTransferFrom;
//    private javax.swing.JLabel lblTransferTo;
//    private javax.swing.JLabel lblTransferType;
//    private javax.swing.JPanel pnlAttemptErrors;
//    private javax.swing.JPanel pnlAttemptInfo;
//    private javax.swing.JPanel pnlButtons;
//    private javax.swing.JPanel pnlMain;
//    private javax.swing.JPanel pnlSample;
//    private javax.swing.JPanel pnlScrollableTransferAttempts;
//    private javax.swing.JPanel pnlTransferAttempt;
//    private javax.swing.JPanel pnlTransferAttemptInfo;
//    private javax.swing.JPanel pnlTransferInfo;
    // End of variables declaration
    //////////////////////



}
