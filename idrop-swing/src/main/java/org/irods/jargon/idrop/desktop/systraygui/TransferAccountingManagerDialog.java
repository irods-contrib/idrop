/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.irods.jargon.conveyor.core.ConveyorBusyException;
import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.idrop.desktop.systraygui.utils.TransferInformationMessageBuilder;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.DashboardAttempt;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.DashboardLayoutService;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferAttemptTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferDashboardLayout;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferManagerTable;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.TransferManagerTableModel;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.irods.jargon.transfer.dao.domain.TransferStateEnum;
import org.irods.jargon.transfer.dao.domain.TransferStatusEnum;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
// public class TransferManagerDialog extends javax.swing.JDialog implements
// ActionListener {
public class TransferAccountingManagerDialog extends javax.swing.JDialog
        implements ListSelectionListener {

    /**
     *
     */
    private static final long serialVersionUID = 6768064190203607302L;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(TransferAccountingManagerDialog.class);
    private Transfer selectedTableObject = null;
    private final iDrop idropGui;
    private final IDROPCore idropCore;
    private MyPanel myPanel;
    private Timer autoRefreshTimer;

    /**
     * Creates new form TransferManagerDialog
     *
     * @param parent
     * @throws org.irods.jargon.conveyor.core.ConveyorExecutionException
     */
    public TransferAccountingManagerDialog(final iDrop parent)
            throws ConveyorExecutionException {
        super(parent, false);
        initComponents();

        idropGui = parent;
        idropCore = parent.getiDropCore();

        initTransferTable();

        ListSelectionModel listSelectionModel = jTableAttempts
                .getSelectionModel();
        listSelectionModel
                .addListSelectionListener(new SharedListSelectionHandler(this));
    }

    public final void refreshTableView() {

        final TransferAccountingManagerDialog tmd = this;

        log.info("refreshing transfer table");

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                tmd.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    int matchingRowForSelected = -1;
                    List<Transfer> transfers = idropCore.getConveyorService()
                            .getQueueManagerService().listAllTransfersInQueue();

                    TransferManagerTableModel model = (TransferManagerTableModel) tblTransfers
                            .getModel();
                    model.setTransfers(transfers);
                    model.fireTableDataChanged();
                    tblTransfers.revalidate();

                    if (selectedTableObject != null) {
                        // previously selected table, refresh display, first,
                        // selecting same row

                        Transfer transfer = null;
                        for (int i = 0; i < tblTransfers.getModel()
                                .getRowCount(); i++) {
                            transfer = model.getTransferAtRow(i);
                            if (transfer.getId().longValue() == selectedTableObject
                                    .getId().longValue()) {
                                matchingRowForSelected = i;
                                break;
                            }
                        }

                        if (matchingRowForSelected != -1) {

                            displayTransferInfo(transfer);

                            int selectedRowIndex = tblTransfers
                                    .convertRowIndexToView(matchingRowForSelected);
                            if (selectedRowIndex != -1) {
                                tblTransfers.setRowSelectionInterval(
                                        selectedRowIndex, selectedRowIndex);
                            }
                        }
                    }
                } catch (ConveyorExecutionException ex) {
                    log.error("exception updating transfer table", ex);
                    MessageManager.showError(tmd, ex.getMessage(),
                            MessageManager.TITLE_MESSAGE);
                } finally {
                    tmd.setCursor(Cursor
                            .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

            }
        });

    }

    private void initTransferTable() throws ConveyorExecutionException {

        List<Transfer> transfers = idropCore.getConveyorService()
                .getQueueManagerService().listAllTransfersInQueue();

        tblTransfers.setModel(new TransferManagerTableModel(idropCore,
                transfers));

        // make more room for summary column
        tblTransfers.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblTransfers.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblTransfers.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblTransfers.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblTransfers.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblTransfers.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(6).setPreferredWidth(150);
        tblTransfers.getColumnModel().getColumn(7).setPreferredWidth(280);

        tblTransfers.getSelectionModel().addListSelectionListener(this);

        tblTransfers.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent me) {

                int selected = tblTransfers.getSelectedRow();

                if (selected == -1) {
                    return;
                }

                TransferManagerTableModel tableModel = (TransferManagerTableModel) tblTransfers
                        .getModel();
                Transfer transfer = tableModel.getTransferAtRow(selected);
                log.info("selected transfer:{}", transfer);

                displayTransferInfo(transfer);

            }

            @Override
            public void mousePressed(final MouseEvent me) {
            }

            @Override
            public void mouseReleased(final MouseEvent me) {
            }

            @Override
            public void mouseEntered(final MouseEvent me) {
            }

            @Override
            public void mouseExited(final MouseEvent me) {
            }
        });

    }

    private void enableTransferSpecificButtons() {
        boolean isRowSelected = (tblTransfers.getSelectedRow() != -1);

        // enable delete, restart, resubmit buttons if row is selected and
        // transfer status is not PROCESSING
        btnRemoveSelected
                .setEnabled(isRowSelected
                        && (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnRestartSelected
                .setEnabled(isRowSelected
                        && (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnResubmitSelected
                .setEnabled(isRowSelected
                        && (selectedTableObject.getTransferState() != TransferStateEnum.PROCESSING));
        btnCancel.setEnabled(isRowSelected);
    }

    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {

            // save selected row transfer object
            int selectedRow = tblTransfers.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = tblTransfers.convertRowIndexToModel(selectedRow);
                TransferManagerTableModel model = (TransferManagerTableModel) tblTransfers
                        .getModel();
                selectedTableObject = model.getTransferAtRow(selectedRow);
            }

            // enable appropriate buttons
            enableTransferSpecificButtons();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        toolBarTop = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnPurgeAll = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnPurgeSuccessful = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jSeparator2 = new javax.swing.JToolBar.Separator();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnRemoveSelected = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnCancel = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnRestartSelected = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnResubmitSelected = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jSeparator3 = new javax.swing.JToolBar.Separator();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnRefresh = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnAutoRefresh = new javax.swing.JToggleButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        splitMasterdetail = new javax.swing.JSplitPane();
        pnlTable = new javax.swing.JPanel();
        pnlTransferDetails = new javax.swing.JPanel();
        lblTransferDetails = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransfers = new TransferManagerTable();
        transferTabs = new javax.swing.JTabbedPane();
        pnlAttemptDashboardTab = new javax.swing.JPanel();
        pnlDashboardDetails = new javax.swing.JPanel();
        lblTransferAttemptDetails = new javax.swing.JLabel();
        pnlDashboard = new javax.swing.JPanel();
        pnlTransferAttemptsContainer = new javax.swing.JPanel();
        scrollPaneAttempts = new javax.swing.JScrollPane();
        jTableAttempts = new javax.swing.JTable();
        pnlBottom = new javax.swing.JPanel();
        bntClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.title")); // NOI18N
        setName("transferAccountingManagerDialog"); // NOI18N
        setPreferredSize(new java.awt.Dimension(900, 800));

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 4, 4, 4));
        pnlMain.setLayout(new java.awt.BorderLayout());

        toolBarTop.setFloatable(false);
        toolBarTop.setRollover(true);
        toolBarTop.add(filler1);
        toolBarTop.add(filler2);

        btnPurgeAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_207_remove_2.png"))); // NOI18N
        btnPurgeAll.setMnemonic('a');
        btnPurgeAll.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnPurgeAll.text")); // NOI18N
        btnPurgeAll.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnPurgeAll.toolTipText")); // NOI18N
        btnPurgeAll.setBorder(null);
        btnPurgeAll.setFocusable(false);
        btnPurgeAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPurgeAll.setName("btnPurgeAll"); // NOI18N
        btnPurgeAll.setPreferredSize(new java.awt.Dimension(80, 80));
        btnPurgeAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPurgeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurgeAllActionPerformed(evt);
            }
        });
        toolBarTop.add(btnPurgeAll);
        toolBarTop.add(filler3);

        btnPurgeSuccessful.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_207_remove_2.png"))); // NOI18N
        btnPurgeSuccessful.setMnemonic('x');
        btnPurgeSuccessful.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnPurgeSuccessful.text")); // NOI18N
        btnPurgeSuccessful.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnPurgeSuccessful.toolTipText")); // NOI18N
        btnPurgeSuccessful.setBorder(null);
        btnPurgeSuccessful.setFocusable(false);
        btnPurgeSuccessful.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPurgeSuccessful.setName("btnPurgeSuccessful"); // NOI18N
        btnPurgeSuccessful.setPreferredSize(new java.awt.Dimension(120, 80));
        btnPurgeSuccessful.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPurgeSuccessful.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurgeSuccessfulActionPerformed(evt);
            }
        });
        toolBarTop.add(btnPurgeSuccessful);
        toolBarTop.add(filler4);
        toolBarTop.add(jSeparator2);
        toolBarTop.add(filler5);

        btnRemoveSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnRemoveSelected.setMnemonic('d');
        btnRemoveSelected.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnDelete.text")); // NOI18N
        btnRemoveSelected.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnDelete.toolTipText")); // NOI18N
        btnRemoveSelected.setBorder(null);
        btnRemoveSelected.setEnabled(false);
        btnRemoveSelected.setFocusable(false);
        btnRemoveSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveSelected.setName("btnDelete"); // NOI18N
        btnRemoveSelected.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRemoveSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSelectedActionPerformed(evt);
            }
        });
        toolBarTop.add(btnRemoveSelected);
        toolBarTop.add(filler6);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_175_stop.png"))); // NOI18N
        btnCancel.setMnemonic('l');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.setBorder(null);
        btnCancel.setEnabled(false);
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        toolBarTop.add(btnCancel);
        toolBarTop.add(filler7);

        btnRestartSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_085_repeat.png"))); // NOI18N
        btnRestartSelected.setMnemonic('t');
        btnRestartSelected.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnRestart.text")); // NOI18N
        btnRestartSelected.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnRestart.toolTipText")); // NOI18N
        btnRestartSelected.setBorder(null);
        btnRestartSelected.setEnabled(false);
        btnRestartSelected.setFocusable(false);
        btnRestartSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRestartSelected.setName("btnRestart"); // NOI18N
        btnRestartSelected.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRestartSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRestartSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartSelectedActionPerformed(evt);
            }
        });
        toolBarTop.add(btnRestartSelected);
        toolBarTop.add(filler8);

        btnResubmitSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_434_redo.png"))); // NOI18N
        btnResubmitSelected.setMnemonic('b');
        btnResubmitSelected.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnResubmit.text")); // NOI18N
        btnResubmitSelected.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnResubmit.toolTipText")); // NOI18N
        btnResubmitSelected.setBorder(null);
        btnResubmitSelected.setEnabled(false);
        btnResubmitSelected.setFocusable(false);
        btnResubmitSelected.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResubmitSelected.setName("btnResubmit"); // NOI18N
        btnResubmitSelected.setPreferredSize(new java.awt.Dimension(80, 80));
        btnResubmitSelected.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResubmitSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResubmitSelectedActionPerformed(evt);
            }
        });
        toolBarTop.add(btnResubmitSelected);
        toolBarTop.add(filler9);
        toolBarTop.add(jSeparator3);
        toolBarTop.add(filler10);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_081_refresh.png"))); // NOI18N
        btnRefresh.setMnemonic('f');
        btnRefresh.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnRefresh.text")); // NOI18N
        btnRefresh.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnRefresh.toolTipText")); // NOI18N
        btnRefresh.setBorder(null);
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setName("btnRefresh"); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(80, 80));
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        toolBarTop.add(btnRefresh);
        toolBarTop.add(filler11);

        btnAutoRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_055_stopwatch.png"))); // NOI18N
        btnAutoRefresh.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnAutoRefresh.text")); // NOI18N
        btnAutoRefresh.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.btnAutoRefresh.toolTipText")); // NOI18N
        btnAutoRefresh.setFocusable(false);
        btnAutoRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAutoRefresh.setName("btnAutoRefresh"); // NOI18N
        btnAutoRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAutoRefresh.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnAutoRefreshItemStateChanged(evt);
            }
        });
        toolBarTop.add(btnAutoRefresh);
        toolBarTop.add(filler13);

        pnlMain.add(toolBarTop, java.awt.BorderLayout.NORTH);

        splitMasterdetail.setDividerLocation(200);
        splitMasterdetail.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlTable.setLayout(new java.awt.BorderLayout());

        lblTransferDetails.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.transferDescription.text")); // NOI18N
        lblTransferDetails.setName("transferDescription"); // NOI18N
        pnlTransferDetails.add(lblTransferDetails);

        pnlTable.add(pnlTransferDetails, java.awt.BorderLayout.NORTH);

        tblTransfers.setAutoCreateRowSorter(true);
        tblTransfers.setName("transferTable"); // NOI18N
        tblTransfers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblTransfers);

        pnlTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        splitMasterdetail.setLeftComponent(pnlTable);

        transferTabs.setName("tabTransfers"); // NOI18N

        pnlAttemptDashboardTab.setLayout(new java.awt.BorderLayout());

        lblTransferAttemptDetails.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.lblTransferAttemptDetails.text")); // NOI18N
        pnlDashboardDetails.add(lblTransferAttemptDetails);

        pnlAttemptDashboardTab.add(pnlDashboardDetails, java.awt.BorderLayout.NORTH);

        pnlDashboard.setName("transferGraph"); // NOI18N
        pnlDashboard.setPreferredSize(new java.awt.Dimension(700, 400));
        pnlDashboard.setLayout(new java.awt.GridLayout(1, 0));
        pnlAttemptDashboardTab.add(pnlDashboard, java.awt.BorderLayout.CENTER);

        transferTabs.addTab(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.pnlAttemptDashboardTab.TabConstraints.tabTitle"), pnlAttemptDashboardTab); // NOI18N

        pnlTransferAttemptsContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                pnlTransferAttemptsContainerComponentShown(evt);
            }
        });
        pnlTransferAttemptsContainer.setLayout(new java.awt.GridLayout(1, 0));

        jTableAttempts.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableAttempts.setName("attemptLog"); // NOI18N
        jTableAttempts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneAttempts.setViewportView(jTableAttempts);

        pnlTransferAttemptsContainer.add(scrollPaneAttempts);

        transferTabs.addTab(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.pnlTransferAttemptsContainer.TabConstraints.tabTitle"), pnlTransferAttemptsContainer); // NOI18N

        splitMasterdetail.setRightComponent(transferTabs);

        pnlMain.add(splitMasterdetail, java.awt.BorderLayout.CENTER);

        pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        bntClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        bntClose.setMnemonic('l');
        bntClose.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.bntClose.text")); // NOI18N
        bntClose.setToolTipText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.bntClose.toolTipText")); // NOI18N
        bntClose.setFocusable(false);
        bntClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bntClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bntClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCloseActionPerformed(evt);
            }
        });
        pnlBottom.add(bntClose);

        pnlMain.add(pnlBottom, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pnlTransferAttemptsContainerComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlTransferAttemptsContainerComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlTransferAttemptsContainerComponentShown

    private void btnAutoRefreshItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnAutoRefreshItemStateChanged

        // turn on/off auto refresh
        if (btnAutoRefresh.isSelected()) {

            autoRefreshTimer = new Timer();
            autoRefreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshTableView();

                }
            }, 0, 10 * 1000);

        } else {

            if (autoRefreshTimer != null) {
                autoRefreshTimer.cancel();
                autoRefreshTimer = null;
            }
        }

    }//GEN-LAST:event_btnAutoRefreshItemStateChanged

    private void bntCloseActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bntCloseActionPerformed
        idropGui.closeTransferManagerDialog();
        dispose();
    }// GEN-LAST:event_bntCloseActionPerformed

    private void btnPurgeAllActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPurgeAllActionPerformed
        try {
            idropCore.getConveyorService().getQueueManagerService()
                    .purgeAllFromQueue();
            resetDetails();
        } catch (ConveyorBusyException ex) {
            log.error("exception purging all from transfer table", ex);
            MessageManager
                    .showError(
                            this,
                            "Transfer Queue Manager is currently busy. Please try again later.",
                            MessageManager.TITLE_MESSAGE);
        } catch (ConveyorExecutionException ex) {
            log.error("exception updating transfer table", ex);
            MessageManager.showError(this, ex.getMessage(),
                    MessageManager.TITLE_MESSAGE);
        }
        refreshTableView();
    }// GEN-LAST:event_btnPurgeAllActionPerformed

    private void btnRefreshActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshActionPerformed
        refreshTableView();
    }// GEN-LAST:event_btnRefreshActionPerformed

    private void btnRemoveSelectedActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRemoveSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore.getConveyorService().getQueueManagerService()
                        .deleteTransferFromQueue(selectedTableObject);
                resetDetails();
            } catch (ConveyorBusyException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ConveyorExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            refreshTableView();
        }
    }// GEN-LAST:event_btnRemoveSelectedActionPerformed

    private void btnRestartSelectedActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRestartSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore
                        .getConveyorService()
                        .getQueueManagerService()
                        .enqueueRestartOfTransferOperation(
                                selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error restarting transfer: {}", ex.getMessage());
                MessageManager
                        .showError(
                                this,
                                "Transfer Queue Manager is currently busy. Please try again later.",
                                MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error restarting transfer. Transfer may have already completed.";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg,
                        MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }// GEN-LAST:event_btnRestartSelectedActionPerformed

    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore.getConveyorService().getQueueManagerService()
                        .cancelTransfer(selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error restarting transfer: {}", ex.getMessage());
                MessageManager
                        .showError(
                                this,
                                "Transfer Queue Manager is currently busy. Please try again later.",
                                MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error cancelling transfer. ";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg,
                        MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }// GEN-LAST:event_btnCancelActionPerformed

    private void btnResubmitSelectedActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnResubmitSelectedActionPerformed
        if (selectedTableObject != null) {
            try {
                idropCore
                        .getConveyorService()
                        .getQueueManagerService()
                        .enqueueResubmitOfTransferOperation(
                                selectedTableObject.getId());
            } catch (ConveyorBusyException ex) {
                log.error("Error resubmitting transfer: {}", ex.getMessage());
                MessageManager
                        .showError(
                                this,
                                "Transfer Queue Manager is currently busy. Please try again later.",
                                MessageManager.TITLE_MESSAGE);
            } catch (ConveyorExecutionException ex) {
                String msg = "Error resubmitting transfer. Transfer may have already completed.";
                log.error(msg + " {}", ex.getMessage());
                MessageManager.showError(this, msg,
                        MessageManager.TITLE_MESSAGE);
            }
            refreshTableView();
        }
    }// GEN-LAST:event_btnResubmitSelectedActionPerformed

    private void btnPurgeSuccessfulActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPurgeSuccessfulActionPerformed
        try {
            idropCore.getConveyorService().getQueueManagerService()
                    .purgeSuccessfulFromQueue();
            resetDetails();
        } catch (ConveyorBusyException ex) {
            log.error("exception purging all from transfer table", ex);
            MessageManager
                    .showError(
                            this,
                            "Transfer Queue Manager is currently busy. Please try again later.",
                            MessageManager.TITLE_MESSAGE);
        } catch (ConveyorExecutionException ex) {
            log.error("exception updating transfer table", ex);
            MessageManager.showError(this, ex.getMessage(),
                    MessageManager.TITLE_MESSAGE);
        }
        refreshTableView();
    }// GEN-LAST:event_btnPurgeSuccessfulActionPerformed

    public void setTransferAttemptDetails(final String details) {
        final TransferAccountingManagerDialog dialog = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.lblTransferAttemptDetails.setText(details);
            }
        });

    }

    public JTable getJTableAttempts() {
        return jTableAttempts;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntClose;
    private javax.swing.JToggleButton btnAutoRefresh;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPurgeAll;
    private javax.swing.JButton btnPurgeSuccessful;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRemoveSelected;
    private javax.swing.JButton btnRestartSelected;
    private javax.swing.JButton btnResubmitSelected;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTable jTableAttempts;
    private javax.swing.JLabel lblTransferAttemptDetails;
    private javax.swing.JLabel lblTransferDetails;
    private javax.swing.JPanel pnlAttemptDashboardTab;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlDashboard;
    private javax.swing.JPanel pnlDashboardDetails;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTransferAttemptsContainer;
    private javax.swing.JPanel pnlTransferDetails;
    private javax.swing.JScrollPane scrollPaneAttempts;
    private javax.swing.JSplitPane splitMasterdetail;
    private javax.swing.JTable tblTransfers;
    private javax.swing.JToolBar toolBarTop;
    private javax.swing.JTabbedPane transferTabs;
    // End of variables declaration//GEN-END:variables

    private void displayTransferInfo(Transfer transfer) throws HeadlessException {
        lblTransferDetails.setText(TransferInformationMessageBuilder
                .buildTransferSummary(transfer));
        lblTransferAttemptDetails.setText(org.openide.util.NbBundle.getMessage(TransferAccountingManagerDialog.class, "TransferAccountingManagerDialog.lblTransferAttemptDetails.text")); // NOI18N
        buildDashboardForTransfer(transfer);

    }

    private void resetDetails() {

        final TransferAccountingManagerDialog dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnlDashboard.removeAll();
                TransferAttemptTableModel transferAttemptTableModel = new TransferAttemptTableModel(
                        null);
                jTableAttempts.setModel(transferAttemptTableModel);
                transferAttemptTableModel.fireTableDataChanged();

            }
        });
    }

    private void buildDashboardForTransfer(final Transfer transfer) {

        final TransferAccountingManagerDialog dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                TransferAttemptTableModel transferAttemptTableModel = new TransferAttemptTableModel(
                        transfer);
                jTableAttempts.setModel(transferAttemptTableModel);

                if (myPanel != null) {
                    pnlDashboard.removeAll();
                }
                myPanel = new MyPanel(transfer, dialog);
                myPanel.setSize(500, 300);
                myPanel.setBackground(Color.WHITE);
                pnlDashboard.add(myPanel);

                dialog.repaint();
            }
        });
    }

    public void showTransferAttemptDetailsDialog(
            final TransferAttempt transferAttempt) {
        log.info("showing transfser attempt:{}", transferAttempt);
        if (transferAttempt == null) {
            throw new IllegalArgumentException("null transferAttempt");
        }

        TransferFileListDialog transferFileListDialog = new TransferFileListDialog(
                this, transferAttempt, idropCore);
        transferFileListDialog.setVisible(true);
    }
}

class MyPanel extends JPanel implements MouseListener, MouseMotionListener {

    /**
     *
     */
    private static final long serialVersionUID = -8512815806313201986L;
    private final Transfer transfer;
    private final List<AttemptRectangle> rectangles = new ArrayList<AttemptRectangle>();
    private final TransferAccountingManagerDialog transferAccountingManagerDialog;

    public MyPanel(final Transfer transfer,
            final TransferAccountingManagerDialog transferAccountingManagerDialog) {
        this.transfer = transfer;
        this.transferAccountingManagerDialog = transferAccountingManagerDialog;
        setBackground(Color.white);
        addMouseMotionListener(this);
        addMouseListener(this);

    }

    @Override
    protected void paintComponent(final Graphics g) {
        TransferDashboardLayout layout = DashboardLayoutService
                .layoutDashboard(transfer);

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        g2.clearRect(0, 0, width, height);
        int nextX = 0;
        int nextY = 0;

        int gap = layout.getDashboardAttempts().size() * 5;

        width = width - gap;

        for (DashboardAttempt attempt : layout.getDashboardAttempts()) {
            // set fill grey for skipped
            g2.setColor(Color.BLUE);

            int widthThisBar = Math.round(width
                    * (attempt.getPercentWidth() / 100));

            if (widthThisBar == 0) {
                widthThisBar = 50;
            } else if (widthThisBar > 200) {
                widthThisBar = 200;
            }

            int heightSkipped = 0;
            int heightTransferred = 0;
            int heightError = 0;
            nextY = height;

            if (attempt.getPercentHeightSkipped() > 0) {

                heightSkipped = Math.round(height
                        * (attempt.getPercentHeightSkipped() / 100));

                if (heightSkipped == 0) {
                    heightSkipped = 2;
                }

                Rectangle skippedRectangle = new Rectangle(nextX, nextY
                        - heightSkipped, widthThisBar, heightSkipped);
                AttemptRectangle attemptRectangle = new AttemptRectangle();

                attemptRectangle.setShape(skippedRectangle);
                attemptRectangle.setDashboardAttempt(attempt);
                attemptRectangle.setType(AttemptRectangle.Type.SKIPPED);
                rectangles.add(attemptRectangle);

                g2.fill(skippedRectangle);

                nextY -= heightSkipped;
            }

            if (attempt.getPercentHeightTransferred() > 0) {
                g2.setColor(Color.GREEN);
                heightTransferred = Math.round(height
                        * (attempt.getPercentHeightTransferred() / 100));

                if (heightTransferred == 0) {
                    heightTransferred = 2;
                }

                Rectangle transferredRectangle = new Rectangle(nextX, nextY
                        - heightTransferred, widthThisBar, heightTransferred);
                AttemptRectangle attemptRectangle = new AttemptRectangle();

                attemptRectangle.setShape(transferredRectangle);
                attemptRectangle.setDashboardAttempt(attempt);
                attemptRectangle.setType(AttemptRectangle.Type.TRANSFERRED);
                rectangles.add(attemptRectangle);

                g2.fill(transferredRectangle);

                nextY -= heightTransferred;

            }

            if (attempt.getPercentHeightError() > 0
                    || attempt.getTransferAttempt().getAttemptStatus() == TransferStatusEnum.ERROR) {

                g2.setColor(Color.RED);
                heightError = Math.round(height
                        * (attempt.getPercentHeightError() / 100));

                if (heightError == 0) {
                    heightError = 10;
                }

                Rectangle errorRectangle = new Rectangle(nextX, nextY
                        - heightError, widthThisBar, heightError);
                AttemptRectangle attemptRectangle = new AttemptRectangle();

                attemptRectangle.setShape(errorRectangle);
                attemptRectangle.setDashboardAttempt(attempt);
                attemptRectangle.setType(AttemptRectangle.Type.ERROR);
                rectangles.add(attemptRectangle);

                g2.fill(errorRectangle);
            }

            nextX += widthThisBar + 5;

        }
    }

    @Override
    public void mouseClicked(final MouseEvent me) {

        if (rectangles == null) {
            return;
        }

        // log.info("point entered:{}", me.getPoint());
        for (AttemptRectangle attemptRectangle : rectangles) {
            if (attemptRectangle.contains(me.getPoint())) {
                transferAccountingManagerDialog
                        .showTransferAttemptDetailsDialog(attemptRectangle
                                .getDashboardAttempt().getTransferAttempt());
                break;
            }
        }
    }

    @Override
    public void mousePressed(final MouseEvent me) {
    }

    @Override
    public void mouseReleased(final MouseEvent me) {
    }

    @Override
    public void mouseEntered(final MouseEvent me) {
    }

    @Override
    public void mouseExited(final MouseEvent me) {
    }

    @Override
    public void mouseDragged(final MouseEvent me) {
    }

    @Override
    public void mouseMoved(final MouseEvent me) {
        if (rectangles == null) {
            return;
        }

        // log.info("point entered:{}", me.getPoint());
        for (AttemptRectangle attemptRectangle : rectangles) {
            if (attemptRectangle.contains(me.getPoint())) {
                // log.info("contains the rectangle for:{}", attemptRectangle);

                TransferInformationMessageBuilder.AttemptType attemptType;

                if (attemptRectangle.getType() == AttemptRectangle.Type.ERROR) {
                    attemptType = TransferInformationMessageBuilder.AttemptType.ERROR;
                } else if (attemptRectangle.getType() == AttemptRectangle.Type.SKIPPED) {
                    attemptType = TransferInformationMessageBuilder.AttemptType.SKIPPED;
                } else {
                    attemptType = TransferInformationMessageBuilder.AttemptType.TRANSFERRED;
                }

                String msg = TransferInformationMessageBuilder
                        .buildTransferAttemptSummary(attemptRectangle
                                .getDashboardAttempt().getTransferAttempt(),
                                attemptType);

                transferAccountingManagerDialog.setTransferAttemptDetails(msg);

                break;
            }
        }
    }
}

class AttemptRectangle {

    public enum Type {

        TRANSFERRED, SKIPPED, ERROR
    }
    private Rectangle2D shape;
    private DashboardAttempt dashboardAttempt;
    private Type type;

    public Rectangle2D getShape() {
        return shape;
    }

    public void setShape(final Rectangle2D shape) {
        this.shape = shape;
    }

    public DashboardAttempt getDashboardAttempt() {
        return dashboardAttempt;
    }

    public void setDashboardAttempt(final DashboardAttempt dashboardAttempt) {
        this.dashboardAttempt = dashboardAttempt;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public boolean contains(final Point point) {

        boolean contains = false;
        if (shape.contains(point.x, point.y)) {
            contains = true;
        }

        return contains;

    }
}

class SharedListSelectionHandler implements ListSelectionListener {

    private final TransferAccountingManagerDialog transferAccountingManagerDialog;

    public SharedListSelectionHandler(
            final TransferAccountingManagerDialog transferAccountingManagerDialog) {
        this.transferAccountingManagerDialog = transferAccountingManagerDialog;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        TransferAttemptTableModel tm = (TransferAttemptTableModel) transferAccountingManagerDialog
                .getJTableAttempts().getModel();

        if (lsm.isSelectionEmpty()) {
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    TransferAttempt transferAttempt = tm
                            .getTransferAttemptAtRow(i);

                    transferAccountingManagerDialog
                            .showTransferAttemptDetailsDialog(transferAttempt);

                }
            }
        }
    }
}
