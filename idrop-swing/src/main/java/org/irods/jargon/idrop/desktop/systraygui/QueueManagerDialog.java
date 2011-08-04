/*
 * QueueManagerDialog.java
 *
 * Created on Jun 23, 2010, 9:34:05 AM
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.irods.jargon.idrop.desktop.systraygui.services.RefreshQueueManagerTimerTask;
import org.irods.jargon.idrop.desktop.systraygui.utils.IDropUtils;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.QueueManagerDetailTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.QueueManagerMasterTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.irods.jargon.transfer.dao.domain.LocalIRODSTransfer;
import org.irods.jargon.transfer.dao.domain.TransferState;
import org.irods.jargon.transfer.dao.domain.TransferStatus;
import org.irods.jargon.transfer.engine.TransferManager;
import org.slf4j.LoggerFactory;

/**
 * Manage transfer queue
 * @author mikeconway
 */
public class QueueManagerDialog extends javax.swing.JDialog implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    public enum ViewType {

        RECENT, ERROR, WARNING, CURRENT
    }
    private final TransferManager transferManager;
    private ViewType viewType = null;
    private iDrop iDropParent = null;
    private RefreshQueueManagerTimerTask refreshQueueManagerTimerTask = null;
    private Timer refreshQueueTimer = null;
    private LocalIRODSTransfer selectedMasterTableObject = null;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(QueueManagerDialog.class);
    private DateFormat dateFormat = DateFormat.getDateTimeInstance();

    private int showResubmitConfirm(LocalIRODSTransfer selectedTransfer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to resubmit this transfer? \n ");
        sb.append(selectedTransfer.toString());

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(), "Resubmit Confirmaiton", JOptionPane.YES_NO_OPTION);

        return n;
    }

    private int showCancelConfirm(LocalIRODSTransfer selectedTransfer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to cancel this transfer? \n ");
        sb.append(selectedTransfer.toString());

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(), "Cancel Confirmaiton", JOptionPane.YES_NO_OPTION);

        return n;
    }

    public synchronized ViewType getViewType() {
        return viewType;
    }

    public synchronized void setViewType(ViewType viewType) {
        if (viewType != this.getViewType()) {
            selectedMasterTableObject = null;
        }
        this.viewType = viewType;
    }

    /** Creates new form QueueManagerDialog */
    public QueueManagerDialog(final iDrop iDropParent, final TransferManager transferManager, final ViewType viewType)
            throws IdropException {
        super((JFrame) null, true);

        if (transferManager == null) {
            throw new IdropException("null transferManager");
        }

        if (viewType == null) {
            throw new IdropException("null viewType");
        }

        this.transferManager = transferManager;
        this.viewType = viewType;
        this.iDropParent = iDropParent;

        initComponents();
        btnDeleteSelected.setEnabled(false);
        btnCancelSelected.setEnabled(false);
        btnResubmitSelected.setEnabled(false);
        btnRestartSelected.setEnabled(false);
        jTableMaster.setModel(new QueueManagerMasterTableModel(new ArrayList<LocalIRODSTransfer>()));
        jTableMaster.getSelectionModel().addListSelectionListener(this);
        tabDetails.setVisible(false);
       
        refreshTableView(viewType);
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int height = screenSize.height;
            int width = screenSize.width;
            screenSize.setSize(width*(0.90), height*(0.80));
            int newheight = screenSize.height;
            int newwidth = screenSize.width;

             //Then I put some print statements in the code
            System.out.println("height="+height);
            System.out.println("width="+width);
            System.out.println("0.80*height="+(height*0.80));
            System.out.println("0.90*width="+(width*0.90));
            System.out.println("newheight="+newheight);
            System.out.println("newwidth="+newwidth);

            this.setSize(newwidth, newheight);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnGroupDetailsDisplay = new javax.swing.ButtonGroup();
        pnlProgress = new javax.swing.JPanel();
        lblTransferred = new javax.swing.JLabel();
        lblCountSoFar = new javax.swing.JLabel();
        lblTransferredOutOf = new javax.swing.JLabel();
        lblCountOutOf = new javax.swing.JLabel();
        progressBarQueueDetails = new javax.swing.JProgressBar();
        pnlCenter = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlTop = new javax.swing.JPanel();
        pnlToolbar = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();
        toolbarQueueManagement = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPurgeAll = new javax.swing.JButton();
        btnPurgeSuccessful = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnDeleteSelected = new javax.swing.JButton();
        btnCancelSelected = new javax.swing.JButton();
        btnRestartSelected = new javax.swing.JButton();
        btnResubmitSelected = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnRefreshView = new javax.swing.JButton();
        toggleAutoRefresh = new javax.swing.JToggleButton();
        jScrollPaneMaster = new javax.swing.JScrollPane();
        jTableMaster = new javax.swing.JTable();
        tabDetails = new javax.swing.JTabbedPane();
        pnlTransferDetailsSummary = new javax.swing.JPanel();
        pnlTransferInfo = new javax.swing.JPanel();
        pnlTransferInfoBasicStats = new javax.swing.JPanel();
        lblTransferTypeLabel = new javax.swing.JLabel();
        lblTransferType = new javax.swing.JLabel();
        lblTransferStatusLabel = new javax.swing.JLabel();
        lblTransferStatus = new javax.swing.JLabel();
        lblProcessingStateLabel = new javax.swing.JLabel();
        lblProcessingState = new javax.swing.JLabel();
        btnSynchronization = new javax.swing.JButton();
        lblHostLabel = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        lblZoneLabel = new javax.swing.JLabel();
        lblZone = new javax.swing.JLabel();
        lblTransferStartLabel = new javax.swing.JLabel();
        lblTransferStart = new javax.swing.JLabel();
        lblTransferEndLabel = new javax.swing.JLabel();
        lblTransferEnd = new javax.swing.JLabel();
        lblSourceLabel = new javax.swing.JLabel();
        lblSource = new javax.swing.JLabel();
        lblTargetLabel = new javax.swing.JLabel();
        lblTarget = new javax.swing.JLabel();
        lblLastPathLabel = new javax.swing.JLabel();
        lblLastPath = new javax.swing.JLabel();
        lblErrorMessageLabel = new javax.swing.JLabel();
        lblErrorMessage = new javax.swing.JLabel();
        pnlTransferProgress = new javax.swing.JPanel();
        pnlTransferOverview = new javax.swing.JPanel();
        pnlTransferStatus = new javax.swing.JPanel();
        pnlTransferType = new javax.swing.JPanel();
        lblTransferTypeLabel1 = new javax.swing.JLabel();
        lblTransferType1 = new javax.swing.JLabel();
        pnlTransferFileCounts = new javax.swing.JPanel();
        lblTransferFilesCounts = new javax.swing.JLabel();
        pnlTransferFileInfo = new javax.swing.JPanel();
        lblCurrentFileLabel = new javax.swing.JLabel();
        lblCurrentFile = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        transferStatusProgressBar = new javax.swing.JProgressBar();
        pnlTransferDetailsTable = new javax.swing.JPanel();
        pnlViewRadio = new javax.swing.JPanel();
        radioShowAll = new javax.swing.JRadioButton();
        radioShowError = new javax.swing.JRadioButton();
        jScrollPaneDetails = new javax.swing.JScrollPane();
        jTableDetails = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuView = new javax.swing.JMenu();
        jMenuCurrent = new javax.swing.JMenuItem();
        jMenuRecent = new javax.swing.JMenuItem();
        jMenuError = new javax.swing.JMenuItem();
        jMenuWarning = new javax.swing.JMenuItem();

        pnlProgress.setBackground(javax.swing.UIManager.getDefaults().getColor("TabbedPane.shadow"));

        lblTransferred.setText("Transferred ");

        lblCountSoFar.setText("0");

        lblTransferredOutOf.setText("out of ");

        lblCountOutOf.setText("0");

        org.jdesktop.layout.GroupLayout pnlProgressLayout = new org.jdesktop.layout.GroupLayout(pnlProgress);
        pnlProgress.setLayout(pnlProgressLayout);
        pnlProgressLayout.setHorizontalGroup(
            pnlProgressLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlProgressLayout.createSequentialGroup()
                .addContainerGap(79, Short.MAX_VALUE)
                .add(lblTransferred)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCountSoFar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(27, 27, 27)
                .add(lblTransferredOutOf)
                .add(18, 18, 18)
                .add(lblCountOutOf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(648, 648, 648))
            .add(pnlProgressLayout.createSequentialGroup()
                .addContainerGap()
                .add(progressBarQueueDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 901, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlProgressLayout.setVerticalGroup(
            pnlProgressLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlProgressLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlProgressLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTransferred)
                    .add(lblTransferredOutOf)
                    .add(lblCountOutOf)
                    .add(lblCountSoFar))
                .add(18, 18, 18)
                .add(progressBarQueueDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("iDrop - Transfer Manager");
        setBounds(new java.awt.Rectangle(0, 22, 0, 0));

        pnlCenter.setLayout(new java.awt.GridLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlTop.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlToolbar.setLayout(new java.awt.GridBagLayout());

        lblHeader.setText("Most Recent iDrop Transfers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 978;
        pnlToolbar.add(lblHeader, gridBagConstraints);

        toolbarQueueManagement.setRollover(true);
        toolbarQueueManagement.setMinimumSize(null);
        toolbarQueueManagement.setPreferredSize(null);
        toolbarQueueManagement.setRequestFocusEnabled(false);
        toolbarQueueManagement.add(jSeparator2);

        btnPurgeAll.setText("Purge All");
        btnPurgeAll.setToolTipText("Purge all complete and enqueued transfers");
        btnPurgeAll.setFocusable(false);
        btnPurgeAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPurgeAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPurgeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurgeAllActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnPurgeAll);

        btnPurgeSuccessful.setText("Purge Successful");
        btnPurgeSuccessful.setToolTipText("Purge all completed, successful transfers");
        btnPurgeSuccessful.setFocusable(false);
        btnPurgeSuccessful.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPurgeSuccessful.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPurgeSuccessful.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurgeSuccessfulActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnPurgeSuccessful);
        toolbarQueueManagement.add(jSeparator1);

        btnDeleteSelected.setText("Delete Selected");
        btnDeleteSelected.setToolTipText("Delete the selected transfer");
        btnDeleteSelected.setFocusable(false);
        btnDeleteSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSelectedActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnDeleteSelected);

        btnCancelSelected.setText("Cancel Selected");
        btnCancelSelected.setToolTipText("Cancel the selected transfer");
        btnCancelSelected.setFocusable(false);
        btnCancelSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnCancelSelected);

        btnRestartSelected.setText("Restart selected transfer");
        btnRestartSelected.setToolTipText("Restart the selected transfer from the current checkpoint");
        btnRestartSelected.setFocusable(false);
        btnRestartSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRestartSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRestartSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartSelectedActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnRestartSelected);

        btnResubmitSelected.setText("Resubmit Selected");
        btnResubmitSelected.setToolTipText("Resubmit the selected transfer with no restart");
        btnResubmitSelected.setFocusable(false);
        btnResubmitSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResubmitSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResubmitSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResubmitSelectedActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnResubmitSelected);
        toolbarQueueManagement.add(jSeparator3);

        btnRefreshView.setText("Refresh View");
        btnRefreshView.setFocusable(false);
        btnRefreshView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshViewActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(btnRefreshView);

        toggleAutoRefresh.setText("Auto Refresh View");
        toggleAutoRefresh.setFocusable(false);
        toggleAutoRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleAutoRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleAutoRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAutoRefreshActionPerformed(evt);
            }
        });
        toolbarQueueManagement.add(toggleAutoRefresh);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 11, 0);
        pnlToolbar.add(toolbarQueueManagement, gridBagConstraints);

        pnlTop.add(pnlToolbar, java.awt.BorderLayout.NORTH);

        jScrollPaneMaster.setPreferredSize(null);
        jScrollPaneMaster.setRequestFocusEnabled(false);

        jTableMaster.setAutoCreateRowSorter(true);
        jTableMaster.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableMaster.setMaximumSize(null);
        jTableMaster.setMinimumSize(null);
        jTableMaster.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneMaster.setViewportView(jTableMaster);

        pnlTop.add(jScrollPaneMaster, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(pnlTop);

        tabDetails.setPreferredSize(null);

        pnlTransferDetailsSummary.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlTransferDetailsSummary.setLayout(new java.awt.BorderLayout());

        pnlTransferInfo.setMinimumSize(null);
        pnlTransferInfo.setPreferredSize(null);
        pnlTransferInfo.setRequestFocusEnabled(false);
        pnlTransferInfo.setLayout(new java.awt.GridLayout());

        pnlTransferInfoBasicStats.setFocusable(false);
        pnlTransferInfoBasicStats.setMinimumSize(null);
        pnlTransferInfoBasicStats.setPreferredSize(null);
        pnlTransferInfoBasicStats.setLayout(new java.awt.GridBagLayout());

        lblTransferTypeLabel.setText("Transfer Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferInfoBasicStats.add(lblTransferTypeLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferInfoBasicStats.add(lblTransferType, gridBagConstraints);

        lblTransferStatusLabel.setText("Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlTransferInfoBasicStats.add(lblTransferStatusLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferInfoBasicStats.add(lblTransferStatus, gridBagConstraints);

        lblProcessingStateLabel.setText("Processing State:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        pnlTransferInfoBasicStats.add(lblProcessingStateLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlTransferInfoBasicStats.add(lblProcessingState, gridBagConstraints);

        btnSynchronization.setText("Synchronization");
        btnSynchronization.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        pnlTransferInfoBasicStats.add(btnSynchronization, gridBagConstraints);

        lblHostLabel.setText("Host:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblHostLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblHost, gridBagConstraints);

        lblZoneLabel.setText("Zone:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlTransferInfoBasicStats.add(lblZoneLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblZone, gridBagConstraints);

        lblTransferStartLabel.setText("Start:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTransferStartLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTransferStart, gridBagConstraints);

        lblTransferEndLabel.setText("End:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTransferEndLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTransferEnd, gridBagConstraints);

        lblSourceLabel.setText("Source Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblSourceLabel, gridBagConstraints);

        lblSource.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSource.setMaximumSize(null);
        lblSource.setMinimumSize(new java.awt.Dimension(300, 18));
        lblSource.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblSource, gridBagConstraints);

        lblTargetLabel.setText("Target Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTargetLabel, gridBagConstraints);

        lblTarget.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTarget.setMinimumSize(new java.awt.Dimension(300, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblTarget, gridBagConstraints);

        lblLastPathLabel.setText("Last Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblLastPathLabel, gridBagConstraints);

        lblLastPath.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLastPath.setMinimumSize(new java.awt.Dimension(300, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblLastPath, gridBagConstraints);

        lblErrorMessageLabel.setText("Error Message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblErrorMessageLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlTransferInfoBasicStats.add(lblErrorMessage, gridBagConstraints);

        pnlTransferInfo.add(pnlTransferInfoBasicStats);

        pnlTransferDetailsSummary.add(pnlTransferInfo, java.awt.BorderLayout.CENTER);

        pnlTransferProgress.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlTransferProgress.setPreferredSize(new java.awt.Dimension(200, 100));
        pnlTransferProgress.setRequestFocusEnabled(false);
        pnlTransferProgress.setLayout(new java.awt.GridBagLayout());

        pnlTransferOverview.setLayout(new java.awt.GridBagLayout());

        lblTransferTypeLabel1.setText("Transfer Type:");
        lblTransferTypeLabel1.setMinimumSize(null);
        pnlTransferType.add(lblTransferTypeLabel1);

        lblTransferType1.setText(" ");
        lblTransferType1.setMaximumSize(null);
        lblTransferType1.setMinimumSize(null);
        pnlTransferType.add(lblTransferType1);

        pnlTransferStatus.add(pnlTransferType);

        lblTransferFilesCounts.setText("Files: /");
        lblTransferFilesCounts.setMinimumSize(null);
        pnlTransferFileCounts.add(lblTransferFilesCounts);

        pnlTransferStatus.add(pnlTransferFileCounts);

        pnlTransferOverview.add(pnlTransferStatus, new java.awt.GridBagConstraints());

        pnlTransferFileInfo.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlTransferFileInfo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblCurrentFileLabel.setText("Current File:");
        pnlTransferFileInfo.add(lblCurrentFileLabel);
        pnlTransferFileInfo.add(lblCurrentFile);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        pnlTransferOverview.add(pnlTransferFileInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 941;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 21, 0, 21);
        pnlTransferProgress.add(pnlTransferOverview, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        transferStatusProgressBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        transferStatusProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 844;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 5, 0);
        jPanel1.add(transferStatusProgressBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlTransferProgress.add(jPanel1, gridBagConstraints);

        pnlTransferDetailsSummary.add(pnlTransferProgress, java.awt.BorderLayout.SOUTH);

        tabDetails.addTab("TransferSummary", null, pnlTransferDetailsSummary, "Summary information about the selected transfer");

        pnlTransferDetailsTable.setMinimumSize(null);
        pnlTransferDetailsTable.setPreferredSize(null);
        pnlTransferDetailsTable.setRequestFocusEnabled(false);
        pnlTransferDetailsTable.setLayout(new java.awt.BorderLayout());

        pnlViewRadio.setMinimumSize(null);
        pnlViewRadio.setPreferredSize(null);

        btnGroupDetailsDisplay.add(radioShowAll);
        radioShowAll.setText("Show all items");
        radioShowAll.setToolTipText("List all transfer items in detail");
        radioShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioShowAllActionPerformed(evt);
            }
        });
        pnlViewRadio.add(radioShowAll);

        btnGroupDetailsDisplay.add(radioShowError);
        radioShowError.setSelected(true);
        radioShowError.setText("Show error items only");
        radioShowError.setToolTipText("Show only transfer items that were in error");
        radioShowError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioShowErrorActionPerformed(evt);
            }
        });
        pnlViewRadio.add(radioShowError);

        pnlTransferDetailsTable.add(pnlViewRadio, java.awt.BorderLayout.NORTH);

        jScrollPaneDetails.setMinimumSize(null);
        jScrollPaneDetails.setPreferredSize(null);
        jScrollPaneDetails.setRequestFocusEnabled(false);

        jTableDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableDetails.setAutoCreateRowSorter(true);
        jTableDetails.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneDetails.setViewportView(jTableDetails);

        pnlTransferDetailsTable.add(jScrollPaneDetails, java.awt.BorderLayout.CENTER);

        tabDetails.addTab("Transfer Details", pnlTransferDetailsTable);

        jSplitPane1.setBottomComponent(tabDetails);

        pnlCenter.add(jSplitPane1);

        getContentPane().add(pnlCenter, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenuView.setText("View");

        jMenuCurrent.setMnemonic('C');
        jMenuCurrent.setText("Current");
        jMenuCurrent.setToolTipText("Show the queue of transfers waiting to process");
        jMenuCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCurrentActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuCurrent);

        jMenuRecent.setMnemonic('R');
        jMenuRecent.setText("Recent");
        jMenuRecent.setToolTipText("Show the last transfers in the queue");
        jMenuRecent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuRecentActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuRecent);

        jMenuError.setMnemonic('E');
        jMenuError.setText("Error");
        jMenuError.setToolTipText("Show transfers that had an error");
        jMenuError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuErrorActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuError);

        jMenuWarning.setMnemonic('W');
        jMenuWarning.setText("Warning");
        jMenuWarning.setToolTipText("Show transfers that had a warning.");
        jMenuWarning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuWarningActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuWarning);

        jMenuBar1.add(jMenuView);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radioShowAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_radioShowAllActionPerformed
        if (radioShowAll.isSelected()) {
            adjustDetails();
        }
    }// GEN-LAST:event_radioShowAllActionPerformed

    private void radioShowErrorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_radioShowErrorActionPerformed
        if (radioShowError.isSelected()) {
            adjustDetails();
        }
    }// GEN-LAST:event_radioShowErrorActionPerformed

    private void jMenuCurrentActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuCurrentActionPerformed
        refreshTableView(QueueManagerDialog.ViewType.CURRENT);

    }// GEN-LAST:event_jMenuCurrentActionPerformed

    private void jMenuRecentActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuRecentActionPerformed
        refreshTableView(QueueManagerDialog.ViewType.RECENT);

    }// GEN-LAST:event_jMenuRecentActionPerformed

    private void jMenuErrorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuErrorActionPerformed
        refreshTableView(QueueManagerDialog.ViewType.ERROR);

    }// GEN-LAST:event_jMenuErrorActionPerformed

    private void jMenuWarningActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuWarningActionPerformed
        refreshTableView(QueueManagerDialog.ViewType.WARNING);
    }// GEN-LAST:event_jMenuWarningActionPerformed

    private void btnPurgeAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPurgeAllActionPerformed
        try {
            iDropParent.getiDropCore().getTransferManager().purgeAllTransfers();
            refreshTableView(viewType);
            resetDisplayFieldsAndStatus();
        } catch (Exception ex) {
            Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
            iDropParent.showIdropException(ex);
        }
    }// GEN-LAST:event_btnPurgeAllActionPerformed

    private void btnPurgeSuccessfulActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPurgeSuccessfulActionPerformed
        try {
            iDropParent.getiDropCore().getTransferManager().purgeSuccessfulTransfers();
            refreshTableView(viewType);
            resetDisplayFieldsAndStatus();

        } catch (Exception ex) {
            Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
            iDropParent.showIdropException(ex);
        }
    }// GEN-LAST:event_btnPurgeSuccessfulActionPerformed

    private void btnDeleteSelectedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteSelectedActionPerformed
        // TODO add your handling code here:
        if (jTableMaster.getSelectedRow() > -1) {
            log.info("no row selected");
            return;
        }

        // get the transfer at the selected row
        QueueManagerMasterTableModel tableModel = (QueueManagerMasterTableModel) jTableMaster.getModel();
        LocalIRODSTransfer selectedTransfer = tableModel.getTransferAtRow(jTableMaster.getSelectedRow());

        int dialogReturn = showDeleteConfirm(selectedTransfer);

        if (dialogReturn == JOptionPane.YES_OPTION) {
        }

    }// GEN-LAST:event_btnDeleteSelectedActionPerformed

    private void btnRestartSelectedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRestartSelectedActionPerformed

        if (jTableMaster.getSelectedRow() == -1) {
            log.info("restart, no transfer selected");
            return;
        }

        // get the transfer at the selected row
        QueueManagerMasterTableModel tableModel = (QueueManagerMasterTableModel) jTableMaster.getModel();
        LocalIRODSTransfer selectedTransfer = tableModel.getTransferAtRow(jTableMaster.getSelectedRow());

        int dialogReturn = showRestartConfirm(selectedTransfer);

        if (dialogReturn == JOptionPane.YES_OPTION) {
            try {
                transferManager.restartTransfer(selectedTransfer);
            } catch (Exception ex) {
                Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
                iDropParent.showIdropException(ex);
            }

            refreshTableView(this.getViewType());
        }

    }// GEN-LAST:event_btnRestartSelectedActionPerformed

    private void btnResubmitSelectedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnResubmitSelectedActionPerformed

        if (jTableMaster.getSelectedRow() == -1) {
            log.info("resubmit, no transfer selected");
            return;
        }

        // get the transfer at the selected row
        QueueManagerMasterTableModel tableModel = (QueueManagerMasterTableModel) jTableMaster.getModel();
        LocalIRODSTransfer selectedTransfer = tableModel.getTransferAtRow(jTableMaster.getSelectedRow());

        int dialogReturn = showResubmitConfirm(selectedTransfer);

        if (dialogReturn == JOptionPane.YES_OPTION) {
            try {
                transferManager.resubmitTransfer(selectedTransfer);
            } catch (Exception ex) {
                Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
                iDropParent.showIdropException(ex);
            }

            refreshTableView(this.getViewType());

        }

    }// GEN-LAST:event_btnResubmitSelectedActionPerformed

    private void btnCancelSelectedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelSelectedActionPerformed

        if (jTableMaster.getSelectedRow() == -1) {
            log.info("cancel, no transfer selected");
            return;
        }

        // get the transfer at the selected row
        QueueManagerMasterTableModel tableModel = (QueueManagerMasterTableModel) jTableMaster.getModel();
        LocalIRODSTransfer selectedTransfer = tableModel.getTransferAtRow(jTableMaster.getSelectedRow());

        int dialogReturn = showCancelConfirm(selectedTransfer);

        if (dialogReturn == JOptionPane.YES_OPTION) {
            try {
                transferManager.cancelTransfer(selectedTransfer);
                refreshTableView(this.getViewType());
            } catch (Exception ex) {
                Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
                iDropParent.showIdropException(ex);
            }

            refreshTableView(this.getViewType());

        }

    }// GEN-LAST:event_btnCancelSelectedActionPerformed

    private void btnRefreshViewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshViewActionPerformed
        refreshTableView(this.getViewType());
    }// GEN-LAST:event_btnRefreshViewActionPerformed

    private void toggleAutoRefreshActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleAutoRefreshActionPerformed

        if (toggleAutoRefresh.isSelected()) {
            // launch auto refresh thread

            log.info("creating timer for queue manager dialog refresh");
            refreshQueueTimer = new Timer();
            try {
                refreshQueueManagerTimerTask = RefreshQueueManagerTimerTask.instance(this);
                refreshQueueTimer.scheduleAtFixedRate(refreshQueueManagerTimerTask, 0, 10000);
            } catch (IdropException ex) {
                Logger.getLogger(iDrop.class.getName()).log(Level.SEVERE, null, ex);
                iDropParent.showIdropException(ex);
                return;
            }
        } else {
            // cancel auto refresh thread
            if (refreshQueueTimer != null) {
                log.info("refreshQueueTimer is not null, cancel current refresh timer");
                refreshQueueTimer.cancel();
                refreshQueueManagerTimerTask = null;
                refreshQueueTimer = null;
            }
        }
    }// GEN-LAST:event_toggleAutoRefreshActionPerformed

    /**
     * Refresh the data in the queue table from the database. This method will automatically maintain the view on
     * whatever the currently selected transfer is. If that transfer is not in the view, it will select the first
     * transfer depicted.
     * 
     * @param viewType
     */
    public final void refreshTableView(final ViewType viewType) {

        final QueueManagerDialog queueManagerDialog = this;

        final QueueManagerMasterTableModel masterTableModel = (QueueManagerMasterTableModel) jTableMaster.getModel();
        int selectedRow = jTableMaster.getSelectedRow();

        if (masterTableModel.getRowCount() > 0) {
            if (selectedRow == -1) {
                selectedRow = 0;
            }

            selectedMasterTableObject = (LocalIRODSTransfer) masterTableModel.getTransferAtRow(jTableMaster.convertRowIndexToModel(selectedRow));

        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                queueManagerDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    tabDetails.setVisible(true);
                    updateDetailsWIthSelectedTable();
                } catch (Exception e) {
                    log.error("exceptoin updating table", e);
                    MessageManager.showError(queueManagerDialog, e.getMessage(), MessageManager.TITLE_MESSAGE);
                } finally {
                    queueManagerDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

            }

            private void updateDetailsWIthSelectedTable() throws Exception {
                log.info("refreshing master table for view type:{}", viewType);

                if (viewType == null) {
                    throw new IdropRuntimeException("null viewType");
                }

                List<LocalIRODSTransfer> transferQueue = null;

                if (viewType == ViewType.CURRENT) {
                    transferQueue = transferManager.getCurrentQueue();
                    queueManagerDialog.getLblHeader().setText("Current transfer queue");
                } else if (viewType == ViewType.RECENT) {
                    transferQueue = transferManager.getRecentQueue();
                    queueManagerDialog.getLblHeader().setText("Recent transfer activity");
                } else if (viewType == ViewType.ERROR) {
                    transferQueue = transferManager.getErrorQueue();
                    queueManagerDialog.getLblHeader().setText("Transfer activities with errors");
                } else if (viewType == ViewType.WARNING) {
                    transferQueue = transferManager.getWarningQueue();
                    queueManagerDialog.getLblHeader().setText("Transfer activities with warnings");
                }

                queueManagerDialog.setViewType(viewType);

                if (transferQueue != null) {
                    jTableMaster.setModel(new QueueManagerMasterTableModel(transferQueue));
                    int matchingRowForSelected = -1;

                    if (selectedMasterTableObject != null) {
                        // previously selected table, refresh display, first, selecting same row

                        LocalIRODSTransfer transfer;
                        for (int i = 0; i < jTableMaster.getModel().getRowCount(); i++) {
                            transfer = masterTableModel.getTransferAtRow(i);
                            if (transfer.getId() == selectedMasterTableObject.getId()) {
                                matchingRowForSelected = i;
                                break;
                            }
                        }

                        if (matchingRowForSelected != -1) {
                            int selectedRowIndex = jTableMaster.convertRowIndexToView(matchingRowForSelected);
                            if (selectedRowIndex != -1) {
                                jTableMaster.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
                            }
                        }
                    } else {
                        jTableDetails.setVisible(false);
                    }
                }
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelSelected;
    private javax.swing.JButton btnDeleteSelected;
    private javax.swing.ButtonGroup btnGroupDetailsDisplay;
    private javax.swing.JButton btnPurgeAll;
    private javax.swing.JButton btnPurgeSuccessful;
    private javax.swing.JButton btnRefreshView;
    private javax.swing.JButton btnRestartSelected;
    private javax.swing.JButton btnResubmitSelected;
    private javax.swing.JButton btnSynchronization;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuCurrent;
    private javax.swing.JMenuItem jMenuError;
    private javax.swing.JMenuItem jMenuRecent;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JMenuItem jMenuWarning;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPaneDetails;
    private javax.swing.JScrollPane jScrollPaneMaster;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableDetails;
    private javax.swing.JTable jTableMaster;
    private javax.swing.JLabel lblCountOutOf;
    private javax.swing.JLabel lblCountSoFar;
    private javax.swing.JLabel lblCurrentFile;
    private javax.swing.JLabel lblCurrentFileLabel;
    private javax.swing.JLabel lblErrorMessage;
    private javax.swing.JLabel lblErrorMessageLabel;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHostLabel;
    private javax.swing.JLabel lblLastPath;
    private javax.swing.JLabel lblLastPathLabel;
    private javax.swing.JLabel lblProcessingState;
    private javax.swing.JLabel lblProcessingStateLabel;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblSourceLabel;
    private javax.swing.JLabel lblTarget;
    private javax.swing.JLabel lblTargetLabel;
    private javax.swing.JLabel lblTransferEnd;
    private javax.swing.JLabel lblTransferEndLabel;
    private javax.swing.JLabel lblTransferFilesCounts;
    private javax.swing.JLabel lblTransferStart;
    private javax.swing.JLabel lblTransferStartLabel;
    private javax.swing.JLabel lblTransferStatus;
    private javax.swing.JLabel lblTransferStatusLabel;
    private javax.swing.JLabel lblTransferType;
    private javax.swing.JLabel lblTransferType1;
    private javax.swing.JLabel lblTransferTypeLabel;
    private javax.swing.JLabel lblTransferTypeLabel1;
    private javax.swing.JLabel lblTransferred;
    private javax.swing.JLabel lblTransferredOutOf;
    private javax.swing.JLabel lblZone;
    private javax.swing.JLabel lblZoneLabel;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlProgress;
    private javax.swing.JPanel pnlToolbar;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTransferDetailsSummary;
    private javax.swing.JPanel pnlTransferDetailsTable;
    private javax.swing.JPanel pnlTransferFileCounts;
    private javax.swing.JPanel pnlTransferFileInfo;
    private javax.swing.JPanel pnlTransferInfo;
    private javax.swing.JPanel pnlTransferInfoBasicStats;
    private javax.swing.JPanel pnlTransferOverview;
    private javax.swing.JPanel pnlTransferProgress;
    private javax.swing.JPanel pnlTransferStatus;
    private javax.swing.JPanel pnlTransferType;
    private javax.swing.JPanel pnlViewRadio;
    private javax.swing.JProgressBar progressBarQueueDetails;
    private javax.swing.JRadioButton radioShowAll;
    private javax.swing.JRadioButton radioShowError;
    private javax.swing.JTabbedPane tabDetails;
    private javax.swing.JToggleButton toggleAutoRefresh;
    private javax.swing.JToolBar toolbarQueueManagement;
    private javax.swing.JProgressBar transferStatusProgressBar;
    // End of variables declaration//GEN-END:variables

    public JLabel getLblHeader() {
        return lblHeader;
    }

    public void setLblHeader(JLabel lblHeader) {
        this.lblHeader = lblHeader;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (e.getValueIsAdjusting()) {
            return;
        }

        if (e.getFirstIndex() == -1 || e.getLastIndex() == -1 || jTableMaster.getSelectedRow() == -1) {
            return;
        }

        adjustDetails();
        pnlTransferInfo.setVisible(true);

    }

    private void adjustDetails() {
        final LocalIRODSTransfer localIRODSTransfer = ((QueueManagerMasterTableModel) jTableMaster.getModel()).getTransferAtRow(jTableMaster.convertRowIndexToModel(jTableMaster.getSelectedRow()));
        log.info("selected transfer:{}", localIRODSTransfer);
        final boolean showAll = radioShowAll.isSelected();

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                // initialize panel info
                pnlCenter.setVisible(true);
                lblTransferType.setText(localIRODSTransfer.getTransferType().toString());
                lblTransferStatus.setText(localIRODSTransfer.getTransferStatus().toString());

                // colorize transfer status
                if (localIRODSTransfer.getTransferStatus() == TransferStatus.ERROR) {
                    lblTransferStatus.setForeground(Color.RED);
                } else if (localIRODSTransfer.getTransferStatus() == TransferStatus.WARNING) {
                    lblTransferStatus.setForeground(Color.CYAN);
                } else {
                    lblTransferStatus.setForeground(Color.GREEN);
                }

                lblProcessingState.setText(localIRODSTransfer.getTransferState().toString());
                if (localIRODSTransfer.getTransferState() == TransferState.PROCESSING) {
                    lblProcessingState.setForeground(Color.GREEN);
                } else if (localIRODSTransfer.getTransferState() == TransferState.PAUSED) {
                    lblProcessingState.setForeground(Color.cyan);
                } else if (localIRODSTransfer.getTransferState() == TransferState.ENQUEUED) {
                    lblProcessingState.setForeground(Color.BLUE);
                } else if (localIRODSTransfer.getTransferState() == TransferState.CANCELLED) {
                    lblProcessingState.setForeground(Color.RED);
                } else {
                    lblProcessingState.setForeground(Color.BLACK);
                }

                if (localIRODSTransfer.getSynchronization() != null) {
                    btnSynchronization.setEnabled(true);
                } else {
                    btnSynchronization.setEnabled(false);
                }

                lblHost.setText(localIRODSTransfer.getTransferHost());
                lblZone.setText(localIRODSTransfer.getTransferZone());

                if (localIRODSTransfer.getTransferStart() != null) {
                    lblTransferStart.setText(dateFormat.format(localIRODSTransfer.getTransferStart()));
                }

                if (localIRODSTransfer.getTransferEnd() != null) {
                    lblTransferEnd.setText(dateFormat.format(localIRODSTransfer.getTransferEnd()));
                }

                lblLastPath.setText(IDropUtils.abbreviateFileName(localIRODSTransfer.getLastSuccessfulPath()));
                lblErrorMessage.setText(localIRODSTransfer.getGlobalException());

                String source = null;
                String target = null;


                // set source and target properly based on activity (put, get, etc)
                switch (localIRODSTransfer.getTransferType()) {
                    case GET:
                        source = localIRODSTransfer.getIrodsAbsolutePath();
                        target = localIRODSTransfer.getLocalAbsolutePath();
                        break;
                    case PUT:
                        source = localIRODSTransfer.getLocalAbsolutePath();
                        target = localIRODSTransfer.getIrodsAbsolutePath();
                        break;
                    case REPLICATE:
                        source = localIRODSTransfer.getIrodsAbsolutePath();
                        target = "";
                        break;
                    case COPY:
                        source = localIRODSTransfer.getIrodsAbsolutePath();
                        target = localIRODSTransfer.getIrodsAbsolutePath();
                        break;
                    case SYNCH:
                        source = localIRODSTransfer.getIrodsAbsolutePath();
                        target = localIRODSTransfer.getIrodsAbsolutePath();
                        break;
                    default:
                        log.error("unable to build details for transfer with transfer type of:{}",
                                localIRODSTransfer.getTransferType());
                        iDropParent.showIdropException(new IdropException("unable to build details for this transfer type"));
                        break;
                }

                lblSource.setText(IDropUtils.abbreviateFileName(source));
                lblTarget.setText(IDropUtils.abbreviateFileName(target));

                lblTransferType1.setText(localIRODSTransfer.getTransferType().toString());


                lblCountSoFar.setText(String.valueOf(localIRODSTransfer.getTotalFilesTransferredSoFar()));
                lblCountOutOf.setText(String.valueOf(localIRODSTransfer.getTotalFilesCount()));
                lblTransferFilesCounts.setText("Files: " + localIRODSTransfer.getTotalFilesTransferredSoFar() + " / " + localIRODSTransfer.getTotalFilesCount());
                transferStatusProgressBar.setMinimum(0);
                transferStatusProgressBar.setMaximum(localIRODSTransfer.getTotalFilesCount());
                transferStatusProgressBar.setValue(localIRODSTransfer.getTotalFilesTransferredSoFar());
                // initialize the detail values via hibernate (they are lazily loaded)
                log.info("get the details based on the selected option");

                try {
                    if (showAll) {
                        log.info("showing all transfers based on radio selection");

                        jTableDetails.setModel(new QueueManagerDetailTableModel(transferManager.getAllTransferItemsForTransfer(localIRODSTransfer.getId())));
                    } else {
                        jTableDetails.setModel(new QueueManagerDetailTableModel(transferManager.getErrorTransferItemsForTransfer(localIRODSTransfer.getId())));
                    }

                } catch (Exception ex) {
                    Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
                    iDropParent.showIdropException(ex);
                    return;
                }

                switch (localIRODSTransfer.getTransferState()) {
                    case PROCESSING:
                        btnDeleteSelected.setEnabled(false);
                        btnRestartSelected.setEnabled(false);
                        btnResubmitSelected.setEnabled(false);
                        btnCancelSelected.setEnabled(true);
                        break;
                    case COMPLETE:
                    case CANCELLED:
                        btnDeleteSelected.setEnabled(true);
                        btnRestartSelected.setEnabled(true);
                        btnResubmitSelected.setEnabled(true);
                        btnCancelSelected.setEnabled(false);
                        break;
                    case ENQUEUED:
                        btnDeleteSelected.setEnabled(false);
                        btnCancelSelected.setEnabled(true);
                        btnResubmitSelected.setEnabled(false);
                        btnRestartSelected.setEnabled(false);
                        break;
                    case IDLE:
                    case PAUSED:
                    default:
                        btnDeleteSelected.setEnabled(false);
                        btnCancelSelected.setEnabled(false);
                        btnResubmitSelected.setEnabled(false);
                        btnRestartSelected.setEnabled(false);
                        break;
                }

                jTableDetails.setVisible(true);
            }
        });
    }

    public int showDeleteConfirm(final LocalIRODSTransfer localIRODSTransfer) {

        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to delete this transfer? \n ");
        sb.append(localIRODSTransfer.toString());

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(), "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        return n;
    }

    public int showRestartConfirm(final LocalIRODSTransfer localIRODSTransfer) {

        StringBuilder sb = new StringBuilder();
        sb.append("Would you like to retart this transfer? \n ");
        sb.append(localIRODSTransfer.toString());

        // default icon, custom title
        int n = JOptionPane.showConfirmDialog(this, sb.toString(), "Restart Confirmation", JOptionPane.YES_NO_OPTION);

        return n;
    }

    private void resetDisplayFieldsAndStatus() {
        try {
            iDropParent.getiDropCore().getTransferManager().resetStatus();
        } catch (Exception ex) {
            Logger.getLogger(QueueManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
            // log and continue...not useful to user
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                /*
                lblTransferType.setText("");
                lblTransferDate.setText("");
                lblTransferStatus.setText("");
                txtSourcePath.setText("");
                txtTargetPath.setText("");
                txtAreaErrorMessage.setText("");
                txtLastGoodPath.setText("");
                 * */
            }
        });
    }
}
