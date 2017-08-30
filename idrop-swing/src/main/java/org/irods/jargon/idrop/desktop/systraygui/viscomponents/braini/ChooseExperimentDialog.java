/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents.braini;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.AVUQueryOperatorEnum;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.IDROPGui;
import org.irods.jargon.idrop.desktop.systraygui.MessageManager;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mcc
 */
public class ChooseExperimentDialog extends javax.swing.JDialog implements
        ListSelectionListener, ActionListener {

    private IDROPGui idropGui;
    private IDROPCore idropCore;
    private ChooseExperimentDialog dialog = null;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(ChooseExperimentDialog.class);
    private MetaDataAndDomainData selectedData = null;
    private ExperimentDescription selectedExperiment = null;

    /**
     * Creates new form ChooseExperimentDialog
     */
    public ChooseExperimentDialog(java.awt.Frame parent, boolean modal, IDROPCore idropCore) {
        super(parent, modal);
        this.idropGui = idropGui;
        this.idropCore = idropCore;
        initComponents();
        initModel();
    }
  
    private void showExperimentForSelectedRow(int row) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                ExperimentTableModel model = (ExperimentTableModel) tblExperiments.getModel();
                MetaDataAndDomainData data = model.getRow(row);
                dialog.selectedData = data;
                log.info("getting details for:{}", data);
                ExperimentDescription experimentDescription = new ExperimentDescription();
                try {
                    CollectionAO collectionAO = idropCore.getIRODSAccessObjectFactory().getCollectionAO(idropCore.irodsAccount());
                    List<MetaDataAndDomainData> fullMetadata = collectionAO.findMetadataValuesForCollection(data.getDomainObjectUniqueName());
                    experimentDescription.setExperimentPath(data.getDomainObjectUniqueName());
                    
                    for (MetaDataAndDomainData val : fullMetadata) {
                        if (val.getAvuAttribute().equals("ExptId")) {
                            lblExperimentIdValue.setText(val.getAvuValue());
                            experimentDescription.setExperimentId(val.getAvuValue());
                        } else  if (val.getAvuAttribute().equals("Lab/PI")) {
                            lblExperimentPIValue.setText(val.getAvuValue());
                            experimentDescription.setExperimentPi(val.getAvuValue());
                        } else  if (val.getAvuAttribute().equals("ExperimentalPurpose")) {
                            lblExperimentPurposeValue.setText(val.getAvuValue());
                            experimentDescription.setExperimentPurpose(val.getAvuValue());
                        }
                    }
                    
                    dialog.selectedExperiment = experimentDescription;
                    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (JargonException je) {
                    Logger.getLogger(ChooseExperimentDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                } catch (JargonQueryException je) {
                    Logger.getLogger(ChooseExperimentDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                }
            }
        });
    }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblHeader = new javax.swing.JLabel();
        scrollExperiments = new javax.swing.JScrollPane();
        tblExperiments = new javax.swing.JTable();
        pnlExperimentView = new javax.swing.JPanel();
        pnlExperimentDets = new javax.swing.JPanel();
        lblExperimentId = new javax.swing.JLabel();
        lblExperimentIdValue = new javax.swing.JLabel();
        lblExperimentPI = new javax.swing.JLabel();
        lblExperimentPIValue = new javax.swing.JLabel();
        lblExperimentPurpose = new javax.swing.JLabel();
        lblExperimentPurposeValue = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        bntSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblHeader.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblHeader.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblHeader.text")); // NOI18N
        getContentPane().add(lblHeader, java.awt.BorderLayout.NORTH);

        tblExperiments.setAutoCreateRowSorter(true);
        tblExperiments.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.tblExperiments.toolTipText")); // NOI18N
        scrollExperiments.setViewportView(tblExperiments);

        getContentPane().add(scrollExperiments, java.awt.BorderLayout.CENTER);

        pnlExperimentView.setLayout(new java.awt.BorderLayout());

        pnlExperimentDets.setLayout(new java.awt.GridBagLayout());

        lblExperimentId.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblExperimentId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExperimentId.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentId, gridBagConstraints);

        lblExperimentIdValue.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        lblExperimentIdValue.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentIdValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentIdValue, gridBagConstraints);

        lblExperimentPI.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblExperimentPI.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExperimentPI.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentPI.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentPI, gridBagConstraints);

        lblExperimentPIValue.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentPIValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentPIValue, gridBagConstraints);

        lblExperimentPurpose.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblExperimentPurpose.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExperimentPurpose.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentPurpose.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentPurpose, gridBagConstraints);

        lblExperimentPurposeValue.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentPurposeValue.text")); // NOI18N
        lblExperimentPurposeValue.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.lblExperimentPurposeValue.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentPurposeValue, gridBagConstraints);

        pnlExperimentView.add(pnlExperimentDets, java.awt.BorderLayout.CENTER);

        pnlButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_197_remove.png"))); // NOI18N
        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(110, 37));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlButtons.add(btnCancel);

        bntSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        bntSave.setMnemonic('O');
        bntSave.setText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.bntSave.text")); // NOI18N
        bntSave.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseExperimentDialog.class, "ChooseExperimentDialog.bntSave.toolTipText")); // NOI18N
        bntSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bntSave.setMaximumSize(null);
        bntSave.setMinimumSize(null);
        bntSave.setName("btnExit"); // NOI18N
        bntSave.setPreferredSize(new java.awt.Dimension(90, 37));
        bntSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntSaveActionPerformed(evt);
            }
        });
        pnlButtons.add(bntSave);

        pnlExperimentView.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlExperimentView, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.selectedData = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void bntSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSaveActionPerformed

        dispose();
    }//GEN-LAST:event_bntSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntSave;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel lblExperimentId;
    private javax.swing.JLabel lblExperimentIdValue;
    private javax.swing.JLabel lblExperimentPI;
    private javax.swing.JLabel lblExperimentPIValue;
    private javax.swing.JLabel lblExperimentPurpose;
    private javax.swing.JLabel lblExperimentPurposeValue;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlExperimentDets;
    private javax.swing.JPanel pnlExperimentView;
    private javax.swing.JScrollPane scrollExperiments;
    private javax.swing.JTable tblExperiments;
    // End of variables declaration//GEN-END:variables

    private void initModel() {

        dialog = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    CollectionAO collectionAO = idropCore.getIRODSAccessObjectFactory().getCollectionAO(idropCore.irodsAccount());
                    List<AVUQueryElement> query = new ArrayList<>();
                    query.add(AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, AVUQueryOperatorEnum.EQUAL, "ExptId"));
                    List<MetaDataAndDomainData> metadata = collectionAO.findMetadataValuesByMetadataQuery(query);
                    ExperimentTableModel model = new ExperimentTableModel(metadata);
                    tblExperiments.setModel(model);
                    tblExperiments.getSelectionModel().addListSelectionListener(
                            dialog);
                    tblExperiments.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent evt) {
                           
                                Point pnt = evt.getPoint();
                                int row = tblExperiments.rowAtPoint(pnt);
                                showExperimentForSelectedRow(row);
                            
                        }

                    });

                    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (JargonException je) {
                    Logger.getLogger(ChooseExperimentDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                    dispose();
                } catch (JargonQueryException je) {
                    Logger.getLogger(ChooseExperimentDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                    dispose();
                }
            }
        });

    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public MetaDataAndDomainData getSelectedData() {
        return selectedData;
    }

    public ExperimentDescription getSelectedExperiment() {
        return selectedExperiment;
    }

}
