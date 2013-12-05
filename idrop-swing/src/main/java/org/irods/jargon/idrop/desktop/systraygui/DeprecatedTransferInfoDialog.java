/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.openide.util.Exceptions;

/**
 *
 * @author lisa stillwell - RENCI
 */
public class DeprecatedTransferInfoDialog extends javax.swing.JDialog {
    
    private final Transfer transfer;
    private List<TransferAttempt> transferAttempts = null;
    private final IDROPCore idropCore;
    private String fileDetailsTitle;

    /**
     * Creates new form TransferInfoDialog
     */  
    public DeprecatedTransferInfoDialog(javax.swing.JDialog parent, Transfer transfer, IDROPCore idropCore) {
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
        
        lblTransferFrom.setText(MiscIRODSUtils.abbreviateFileName(fromPath));
        lblTransferFrom.setToolTipText(fromPath);
        lblTransferTo.setText(MiscIRODSUtils.abbreviateFileName(toPath));
        lblTransferTo.setToolTipText(toPath);
        
        // also build string for later use
        StringBuilder sb = new StringBuilder();
        sb.append(transfer.getTransferType().toString());
        sb.append( " Transfer from ");
        sb.append(fromPath);
        sb.append(" to ");
        sb.append(toPath);
        fileDetailsTitle = sb.toString();
        
        // initialize transfer attempts
        try {
            Transfer transferWithChildren = idropCore.getConveyorService().getQueueManagerService().initializeGivenTransferByLoadingChildren(transfer);
            transferAttempts = transferWithChildren.getTransferAttempts();

        } catch (ConveyorExecutionException ex) {
            Exceptions.printStackTrace(ex); // FIXME: do the right thing here
        }
    }
    
    private void populateTransferAttempts() {
        final DeprecatedTransferInfoDialog tid = this;
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                // change panel's tile border to plural version if more than 1 attempt
                int numAttempts = transferAttempts.size();
                if (numAttempts > 1) {
                    TitledBorder border = (TitledBorder)jPanel1.getBorder();
                    border.setTitle("Transfer Attempts");
                    jPanel1.repaint();
                }
                int origHeight = pnlScrollableTransferAttempts.getHeight();
                        
                // compose panel for each attempt and add to grid bag layout
                java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 0.5;
                gridBagConstraints.weighty = 0.5;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                
                int count = 1;
                for (TransferAttempt attempt: transferAttempts) {
                    gridBagConstraints.gridy = count-1;
                    pnlScrollableTransferAttempts.add(createAttemptPanel(attempt, count), gridBagConstraints);
                    count++;
                }
                
                pnlScrollableTransferAttempts.setPreferredSize(
                        new Dimension(pnlScrollableTransferAttempts.getWidth(), origHeight*(count-1)));
                pnlScrollableTransferAttempts.validate();
                pnlScrollableTransferAttempts.repaint();
                pack();
                tid.validate();
                tid.repaint();
            }
        });
    }
    
    private javax.swing.JPanel createAttemptPanel(TransferAttempt attempt, int count) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM, d, yyyy : h:mm:ss a");
        
        //javax.swing.JPanel pnlAttemptInstance = new javax.swing.JPanel();
        int idx = count-1;
        javax.swing.JPanel pnlAttemptInstance = new javax.swing.JPanel();
        pnlAttemptInstance.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Attempt #" + String.valueOf(count)));
        pnlAttemptInstance.setPreferredSize(new java.awt.Dimension(340, 585));
        pnlAttemptInstance.setLayout(new java.awt.BorderLayout());
        
        javax.swing.JPanel pnlAttemptInfo = new javax.swing.JPanel();
        pnlAttemptInfo.setPreferredSize(new java.awt.Dimension(330, 58));
        pnlAttemptInfo.setLayout(new java.awt.GridBagLayout());
        
        javax.swing.JLabel label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.idLabel.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.startLabel.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.endLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        if (attempt.getAttemptEnd() != null) {
            label.setText(dateFormat.format(attempt.getAttemptEnd()));
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAttemptInfo.add(label, gridBagConstraints);

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.statusLabel.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.lastPathLabel.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.totalFilesLabel.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.totalSoFarLabel.text")); // NOI18N
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
        pnlAttemptErrors.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.pnlAttemptErrors.border.title"))); // NOI18N
        pnlAttemptErrors.setPreferredSize(new java.awt.Dimension(340, 250));
        pnlAttemptErrors.setLayout(new java.awt.GridBagLayout());

        label = new javax.swing.JLabel();
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.errorsLabel.text")); // NOI18N
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
        textArea.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.exceptionLabel.text")); // NOI18N
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
        textArea.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
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
        label.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.exceptionDetailsLabel.text")); // NOI18N
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
        textArea.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "TransferInfoDialog.NA.text")); // NOI18N
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
        
        return pnlAttemptInstance;
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
        pnlTop = new javax.swing.JPanel();
        toolBatTop = new javax.swing.JToolBar();
        btnShowFiles = new javax.swing.JButton();
        pnlTransferInfo = new javax.swing.JPanel();
        lblTransferType = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTransferFrom = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTransferTo = new javax.swing.JLabel();
        pnlTransferAttempt = new javax.swing.JPanel();
        pnlTransferAttemptInfo = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollableTransferAttempts = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.title")); // NOI18N

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 4, 8, 4));
        pnlMain.setPreferredSize(new java.awt.Dimension(530, 630));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTop.setLayout(new java.awt.BorderLayout());

        toolBatTop.setRollover(true);

        btnShowFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_195_circle_info.png"))); // NOI18N
        btnShowFiles.setMnemonic('i');
        btnShowFiles.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.btnShowFiles.text")); // NOI18N
        btnShowFiles.setFocusable(false);
        btnShowFiles.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowFiles.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnShowFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowFilesActionPerformed(evt);
            }
        });
        toolBatTop.add(btnShowFiles);

        pnlTop.add(toolBatTop, java.awt.BorderLayout.NORTH);

        pnlTransferInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 0, 6, 0));
        pnlTransferInfo.setLayout(new java.awt.GridBagLayout());

        lblTransferType.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.lblTransferType.text")); // NOI18N
        pnlTransferInfo.add(lblTransferType, new java.awt.GridBagConstraints());

        jLabel4.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferInfo.add(jLabel4, gridBagConstraints);

        lblTransferFrom.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.lblTransferFrom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferInfo.add(lblTransferFrom, gridBagConstraints);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferInfo.add(jLabel6, gridBagConstraints);

        lblTransferTo.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.lblTransferTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferInfo.add(lblTransferTo, gridBagConstraints);

        pnlTop.add(pnlTransferInfo, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlTransferAttempt.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 2, 6, 2));
        pnlTransferAttempt.setPreferredSize(new java.awt.Dimension(520, 630));
        pnlTransferAttempt.setRequestFocusEnabled(false);
        pnlTransferAttempt.setLayout(new java.awt.BorderLayout());

        pnlTransferAttemptInfo.setPreferredSize(new java.awt.Dimension(350, 600));
        pnlTransferAttemptInfo.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.jPanel1.border.title"))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(350, 600));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 460));

        pnlScrollableTransferAttempts.setPreferredSize(new java.awt.Dimension(340, 460));
        pnlScrollableTransferAttempts.setLayout(new java.awt.GridBagLayout());
        jScrollPane1.setViewportView(pnlScrollableTransferAttempts);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlTransferAttemptInfo.add(jPanel1, java.awt.BorderLayout.CENTER);

        pnlTransferAttempt.add(pnlTransferAttemptInfo, java.awt.BorderLayout.CENTER);

        pnlButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_198_ok.png"))); // NOI18N
        btnClose.setMnemonic('o');
        btnClose.setText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.btnClose.text")); // NOI18N
        btnClose.setToolTipText(org.openide.util.NbBundle.getMessage(DeprecatedTransferInfoDialog.class, "DeprecatedTransferInfoDialog.btnClose.toolTipText")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        pnlButtons.add(btnClose);

        pnlTransferAttempt.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlTransferAttempt, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnShowFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowFilesActionPerformed
        TransferFileListDialog transferFileListDialog = new TransferFileListDialog(
                    this, transfer.getTransferAttempts().get(transfer.getTransferAttempts().size() - 1).getId(), idropCore);
        Toolkit tk = getToolkit();
        int x = (tk.getScreenSize().width - transferFileListDialog.getWidth()) / 2;
        int y = (tk.getScreenSize().height - transferFileListDialog.getHeight()) / 2;
        transferFileListDialog.setLocation(x, y);
        transferFileListDialog.setTitle(this.fileDetailsTitle);
        transferFileListDialog.setModal(true);  
        transferFileListDialog.setVisible(true);
    }//GEN-LAST:event_btnShowFilesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnShowFiles;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTransferFrom;
    private javax.swing.JLabel lblTransferTo;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlScrollableTransferAttempts;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTransferAttempt;
    private javax.swing.JPanel pnlTransferAttemptInfo;
    private javax.swing.JPanel pnlTransferInfo;
    private javax.swing.JToolBar toolBatTop;
    // End of variables declaration//GEN-END:variables
}
