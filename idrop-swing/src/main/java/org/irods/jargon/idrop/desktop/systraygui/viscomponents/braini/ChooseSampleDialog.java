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
public class ChooseSampleDialog extends javax.swing.JDialog implements
        ListSelectionListener, ActionListener {

    private IDROPGui idropGui;
    private IDROPCore idropCore;
    private ChooseSampleDialog dialog = null;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(ChooseSampleDialog.class);
    private MetaDataAndDomainData selectedData = null;
    private ExperimentDescription selectedExperiment = null;
    private SampleDescription selectedSample = null;

    /**
     * Creates new form ChooseExperimentDialog
     */
    public ChooseSampleDialog(java.awt.Frame parent, boolean modal, IDROPCore idropCore, ExperimentDescription experimentDescription) {
        super(parent, modal);
        this.idropCore = idropCore;
        initComponents();
        initModel();
        this.selectedExperiment = experimentDescription;
    }
  
    private void showSampleForSelectedRow(int row) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                SampleTableModel model = (SampleTableModel) tblSamples.getModel();
                MetaDataAndDomainData data = model.getRow(row);
                dialog.selectedData = data;
                log.info("getting details for:{}", data);
                SampleDescription sampleDescription = new SampleDescription();
                try {
                    CollectionAO collectionAO = idropCore.getIRODSAccessObjectFactory().getCollectionAO(idropCore.irodsAccount());
                    List<MetaDataAndDomainData> fullMetadata = collectionAO.findMetadataValuesForCollection(data.getDomainObjectUniqueName());
                    sampleDescription.setSamplePath(data.getDomainObjectUniqueName());
                    sampleDescription.setExperimentId(dialog.getSelectedExperiment().getExperimentId());
                    lblSamplePathValue.setText(sampleDescription.getSamplePath());
                    
                    for (MetaDataAndDomainData val : fullMetadata) {
                        if (val.getAvuAttribute().equals("ParentExptId")) {
                            lblExperimentIdValue.setText(val.getAvuValue());
                            sampleDescription.setSampleId(val.getAvuValue());
                        } else  if (val.getAvuAttribute().equals("SampleId")) {
                            lblSampleIdValue.setText(val.getAvuValue());
                           sampleDescription.setSampleId(val.getAvuValue());
                        }
                    }
                    
                    dialog.selectedSample = sampleDescription;
                    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (JargonException je) {
                    Logger.getLogger(ChooseSampleDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                } catch (JargonQueryException je) {
                    Logger.getLogger(ChooseSampleDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                }
            }
        });
    }
    
     public SampleDescription getSelectedSample() {
        return selectedSample;
    }

    public void setSelectedSample(SampleDescription selectedSample) {
        this.selectedSample = selectedSample;
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
        tblSamples = new javax.swing.JTable();
        pnlExperimentView = new javax.swing.JPanel();
        pnlExperimentDets = new javax.swing.JPanel();
        lblExperimentId = new javax.swing.JLabel();
        lblExperimentIdValue = new javax.swing.JLabel();
        lblSampleId = new javax.swing.JLabel();
        lblSampleIdValue = new javax.swing.JLabel();
        lblSamplePath = new javax.swing.JLabel();
        lblSamplePathValue = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        bntSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblHeader.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblHeader.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblHeader.text")); // NOI18N
        getContentPane().add(lblHeader, java.awt.BorderLayout.NORTH);

        tblSamples.setAutoCreateRowSorter(true);
        tblSamples.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.tblSamples.toolTipText")); // NOI18N
        scrollExperiments.setViewportView(tblSamples);

        getContentPane().add(scrollExperiments, java.awt.BorderLayout.CENTER);

        pnlExperimentView.setLayout(new java.awt.BorderLayout());

        pnlExperimentDets.setLayout(new java.awt.GridBagLayout());

        lblExperimentId.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblExperimentId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExperimentId.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblExperimentId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentId, gridBagConstraints);

        lblExperimentIdValue.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        lblExperimentIdValue.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblExperimentIdValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblExperimentIdValue, gridBagConstraints);

        lblSampleId.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblSampleId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSampleId.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblSampleId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblSampleId, gridBagConstraints);

        lblSampleIdValue.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblSampleIdValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblSampleIdValue, gridBagConstraints);

        lblSamplePath.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblSamplePath.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSamplePath.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblSamplePath.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblSamplePath, gridBagConstraints);

        lblSamplePathValue.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblSamplePathValue.text")); // NOI18N
        lblSamplePathValue.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.lblSamplePathValue.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlExperimentDets.add(lblSamplePathValue, gridBagConstraints);

        pnlExperimentView.add(pnlExperimentDets, java.awt.BorderLayout.CENTER);

        pnlButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_197_remove.png"))); // NOI18N
        btnCancel.setMnemonic('c');
        btnCancel.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.btnCancel.toolTipText")); // NOI18N
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
        bntSave.setText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.bntSave.text")); // NOI18N
        bntSave.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseSampleDialog.class, "ChooseSampleDialog.bntSave.toolTipText")); // NOI18N
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
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblSampleId;
    private javax.swing.JLabel lblSampleIdValue;
    private javax.swing.JLabel lblSamplePath;
    private javax.swing.JLabel lblSamplePathValue;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlExperimentDets;
    private javax.swing.JPanel pnlExperimentView;
    private javax.swing.JScrollPane scrollExperiments;
    private javax.swing.JTable tblSamples;
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
                    query.add(AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, AVUQueryOperatorEnum.EQUAL, "SampleId"));
                    List<MetaDataAndDomainData> metadata = collectionAO.findMetadataValuesByMetadataQuery(query);
                    SampleTableModel model = new SampleTableModel(metadata, dialog.getSelectedExperiment());
                    tblSamples.setModel(model);
                    tblSamples.getSelectionModel().addListSelectionListener(
                            dialog);
                    tblSamples.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent evt) {
                           
                                Point pnt = evt.getPoint();
                                int row = tblSamples.rowAtPoint(pnt);
                                showSampleForSelectedRow(row);
                            
                        }

                    });

                    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (JargonException je) {
                    Logger.getLogger(ChooseSampleDialog.class.getName()).log(Level.SEVERE,
                            null, je);
                    MessageManager.showError(dialog,
                            je.getMessage());
                    dispose();
                } catch (JargonQueryException je) {
                    Logger.getLogger(ChooseSampleDialog.class.getName()).log(Level.SEVERE,
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
       
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }

    public MetaDataAndDomainData getSelectedData() {
        return selectedData;
    }

    public ExperimentDescription getSelectedExperiment() {
        return selectedExperiment;
    }

}
