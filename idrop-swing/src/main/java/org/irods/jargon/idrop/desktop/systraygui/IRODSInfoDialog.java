/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.CardLayout;
import java.io.FileNotFoundException;
import javax.swing.ListSelectionModel;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAOImpl;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.utils.FieldFormatHelper;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.MetadataTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class IRODSInfoDialog extends javax.swing.JDialog {

    private final iDrop idropGUI;
    private final IRODSAccount irodsAccount;
    private String selectedObjectFullPath;
    private String selectedObjectName;
    private String selectedObjectParent;
    private final IRODSFileSystem irodsFileSystem;
    private final IRODSTree irodsTree;
    public static org.slf4j.Logger log = LoggerFactory.getLogger(IRODSTree.class);
    //private final String fileName;

    /**
     * Creates new form IRODSInfoDialog
     */
//    public IRODSInfoDialog(java.awt.Frame parent, boolean modal) {
//        super(parent, modal);
//        initComponents();
//    }
    public IRODSInfoDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree) {

        super(parent, modal);
        this.idropGUI = parent;
        this.irodsAccount = idropGUI.getiDropCore().getIrodsAccount();
        this.irodsFileSystem = idropGUI.getiDropCore().getIrodsFileSystem();
        this.irodsTree = irodsTree;
        initSelectedObjectName();
        initComponents();
        // perhaps set wait cursor between here ...
        selectInfoCard();
        initializeFileInfo();
        // ... and here
    }

    private void initSelectedObjectName() {

        IRODSFileService irodsFS = null;

        try {
            irodsFS = new IRODSFileService(idropGUI.getiDropCore().getIrodsAccount(),
                    idropGUI.getiDropCore().getIrodsFileSystem());
        } catch (Exception ex) {
            log.error("cannot create irods file service");
            return;
        }

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree.getModel();
        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idxStart = selectionModel.getMinSelectionIndex();

        IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(idxStart, 0);
        selectedObjectFullPath = selectedNode.getFullPath();
        String objectPath[] = selectedObjectFullPath.split("/");
        selectedObjectName = objectPath[objectPath.length - 1];
        IRODSNode pNode = (IRODSNode) selectedNode.getParent();
        selectedObjectParent = pNode.getFullPath();    
    }

    private void selectInfoCard() {

        CardLayout cl = (CardLayout) (pnlInfoCards.getLayout());
        if (isCollection()) {
            lblObjectCollection.setText("Collection:");
            cl.show(pnlInfoCards, "cardCollectionInfo");
        } else {
            lblObjectCollection.setText("Object:");
            cl.show(pnlInfoCards, "cardObjectInfo");
        }

        // also populate header
        if (selectedObjectName != null) {
            lblInfoObjectName.setText(selectedObjectName);
        }
        if (selectedObjectParent != null) {
            lblInfoObjectParent.setText(selectedObjectParent);
        }
    }

    private void initializeFileInfo() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {

                    if (isCollection()) {

                        CollectionAO collectionAO = irodsFileSystem.getIRODSAccessObjectFactory().getCollectionAO(irodsAccount);
                        Collection collection = collectionAO.findByAbsolutePath(selectedObjectFullPath);

                        if (collection.getCreatedAt().toString() != null) {
                            lblInfoCollectionCreatedDate.setText(collection.getCreatedAt().toString());
                        } else {
                            lblInfoCollectionCreatedDate.setText("");
                        }

                        if (collection.getModifiedAt().toString() != null) {
                            lblInfoCollectionModifiedDate.setText(collection.getModifiedAt().toString());
                        } else {
                            lblInfoCollectionModifiedDate.setText("");
                        }

                        if (collection.getCollectionOwnerName() != null) {
                            lblInfoCollectionOwner.setText(collection.getCollectionOwnerName());
                        } else {
                            lblInfoCollectionOwner.setText("");
                        }

                        // don't know how to get collection description
                        lblInfoCollectionDescription.setText("");

                        // don't know how to get collection type
                        lblInfoCollectionType.setText("");

                        if (collection.getCollectionOwnerZone() != null) {
                            lblInfoCollectionOwnerZone.setText(collection.getCollectionOwnerZone());
                        }
                        else {
                            lblInfoCollectionOwnerZone.setText("");
                        }
                        
                        if (collection.getObjectPath() != null) {
                            lblInfoCollectionObjectPath.setText(collection.getObjectPath());
                        }
                        else {
                            lblInfoCollectionObjectPath.setText("");
                        }
                        
                        if (collection.getInfo1() != null) {
                            lblInfoCollectionInfo1.setText(collection.getInfo1());
                        }
                        else {
                            lblInfoCollectionInfo1.setText("");
                        }
                        
                        if (collection.getInfo2() != null) {
                            lblInfoCollectionInfo2.setText(collection.getInfo2());
                        }
                        else {
                            lblInfoCollectionInfo2.setText("");
                        }


                    } else {

                        DataObjectAO dataObjectAO = irodsFileSystem.getIRODSAccessObjectFactory().getDataObjectAO(irodsAccount);
                        DataObject dataObject = dataObjectAO.findByAbsolutePath(selectedObjectFullPath);

                        lblInfoObjectSize.setText(FieldFormatHelper.formatFileLength(dataObject.getDataSize()));
                        lblInfoObjectCreatedDate.setText(dataObject.getCreatedAt().toString());

                    }

                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (JargonException ex) {
                    Exceptions.printStackTrace(ex);
//                } catch (IdropException ex) {
//                    Logger.getLogger(MetadataViewDialog.class.getName()).log(
//                            Level.SEVERE, null, ex);
//                    idropGui.showIdropException(ex);
                }
            }
        });
    }

    private void initMetadataInfo() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                    IRODSFileService irodsFileService = new IRODSFileService(
//                            irodsAccount, idropGui.getiDropCore().getIrodsFileSystem());
//                    MetadataTableModel metadataTableModel;
//
//                    CollectionAndDataObjectListAndSearchAOImpl collectionAndDataObjectListAndSearchAOImpl =
//                            (CollectionAndDataObjectListAndSearchAOImpl)
//                            irodsFileSystem.getIRODSAccessObjectFactory().getCollectionAndDataObjectListAndSearchAO(irodsAccount);
//                    CollectionAndDataObjectListingEntry entry = 
//                            collectionAndDataObjectListAndSearchAOImpl.getCollectionAndDataObjectListingEntryAtGivenAbsolutePath(selectedObjectFullPath);
//                try {
//                    IRODSFileService irodsFileService = new IRODSFileService(
//                            irodsAccount, idropGui.getiDropCore().getIrodsFileSystem());
//                    MetadataTableModel metadataTableModel;
//                   if (collection) {
                //                        lblAbsolutePath.setText(irodsAbsolutePath);
                //                        metadataTableModel = new MetadataTableModel(
                //                                irodsFileService.getMetadataForCollection(irodsAbsolutePath));
                //                    } else {
                //                        lblAbsolutePath.setText(irodsAbsolutePath + "/"
                //                                + fileName);
                //                        metadataTableModel = new MetadataTableModel(
                //                                irodsFileService.getMetadataForDataObject(
                //                                irodsAbsolutePath, fileName));
                //                    }
                //                    tableMetadata.setModel(metadataTableModel);
                //                    tableMetadata.validate();
//                } catch (FileNotFoundException ex) {
//                        Exceptions.printStackTrace(ex);
//                } catch (JargonException ex) {
//                    Exceptions.printStackTrace(ex);
//                } catch (IdropException ex) {
//                    Logger.getLogger(MetadataViewDialog.class.getName()).log(
//                            Level.SEVERE, null, ex);
//                    idropGui.showIdropException(ex);
//                }
            }
        });
    }

    private boolean isCollection() {

        boolean state = false;
        CollectionAndDataObjectListingEntry entry = null;

        //perhaps should throw exception if these vital member variables are null
        if ((selectedObjectFullPath != null)
                && (irodsFileSystem != null)
                && (irodsAccount != null)) {
            CollectionAndDataObjectListAndSearchAOImpl collectionAndDataObjectListAndSearchAOImpl;
            try {
                collectionAndDataObjectListAndSearchAOImpl = (CollectionAndDataObjectListAndSearchAOImpl) irodsFileSystem.getIRODSAccessObjectFactory().getCollectionAndDataObjectListAndSearchAO(irodsAccount);
                entry =
                        collectionAndDataObjectListAndSearchAOImpl.getCollectionAndDataObjectListingEntryAtGivenAbsolutePath(selectedObjectFullPath);
            } catch (JargonException ex) {
                // TODO: respond correctly here
                Exceptions.printStackTrace(ex);
            }

            state = entry.isCollection();
        }

        return state;
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlSelectedObject = new javax.swing.JPanel();
        lblObjectCollection = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblInfoObjectName = new javax.swing.JLabel();
        lblInfoObjectParent = new javax.swing.JLabel();
        tabbedpanelMain = new javax.swing.JTabbedPane();
        pnlInfoTab = new javax.swing.JPanel();
        pnlInfoCards = new javax.swing.JPanel();
        pnlCollectionInfo = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblInfoCollectionCreatedDate = new javax.swing.JLabel();
        lblInfoCollectionModifiedDate = new javax.swing.JLabel();
        lblInfoCollectionOwner = new javax.swing.JLabel();
        lblInfoCollectionOwnerZone = new javax.swing.JLabel();
        lblInfoCollectionType = new javax.swing.JLabel();
        lblInfoCollectionObjectPath = new javax.swing.JLabel();
        lblInfoCollectionDescription = new javax.swing.JLabel();
        lblInfoCollectionInfo1 = new javax.swing.JLabel();
        lblInfoCollectionInfo2 = new javax.swing.JLabel();
        pnlObjectInfo = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblInfoObjectSize = new javax.swing.JLabel();
        lblInfoObjectCreatedDate = new javax.swing.JLabel();
        lblInfoObjectModifiedDate = new javax.swing.JLabel();
        lblInfoObjectOwner = new javax.swing.JLabel();
        lblInfoObjectOwnerZone = new javax.swing.JLabel();
        lblInfoObjectDataPath = new javax.swing.JLabel();
        lblInfoObjectResourceGroup = new javax.swing.JLabel();
        lblInfoObjectChecksum = new javax.swing.JLabel();
        lblInfoObjectResource = new javax.swing.JLabel();
        lblInfoObjectReplicaNumber = new javax.swing.JLabel();
        lblInfoObjectReplicationStatus = new javax.swing.JLabel();
        lblInfoObjectStatus = new javax.swing.JLabel();
        lblInfoObjectType = new javax.swing.JLabel();
        lblInfoObjectVersion = new javax.swing.JLabel();
        pnlTagsComments = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtInfoTags = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaInfoComments = new javax.swing.JTextArea();
        btnUpdateTagsComments = new javax.swing.JButton();
        pnlMetadataTab = new javax.swing.JPanel();
        pnlMetadataTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pnlMetaDataEdit = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        btnMetadataClear = new javax.swing.JButton();
        btnMetadataUpdateCreate = new javax.swing.JButton();
        txtMetadataAttribute = new javax.swing.JTextField();
        txtMetadataValue = new javax.swing.JTextField();
        txtMetadataUnit = new javax.swing.JTextField();
        btnMetadataDelete = new javax.swing.JButton();
        pnlPermissionsTab = new javax.swing.JPanel();
        pnlPermissionsTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        pnlPermissionEdit = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(565, 700));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlSelectedObject.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));
        pnlSelectedObject.setPreferredSize(new java.awt.Dimension(528, 70));

        lblObjectCollection.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblObjectCollection.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblObjectCollection.text")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel2.text")); // NOI18N

        lblInfoObjectName.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectName.text")); // NOI18N

        lblInfoObjectParent.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectParent.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlSelectedObjectLayout = new org.jdesktop.layout.GroupLayout(pnlSelectedObject);
        pnlSelectedObject.setLayout(pnlSelectedObjectLayout);
        pnlSelectedObjectLayout.setHorizontalGroup(
            pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedObjectLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblObjectCollection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblInfoObjectName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .add(lblInfoObjectParent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        pnlSelectedObjectLayout.setVerticalGroup(
            pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedObjectLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblObjectCollection)
                    .add(lblInfoObjectName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedObjectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblInfoObjectParent))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1.add(pnlSelectedObject, java.awt.BorderLayout.PAGE_START);

        pnlInfoTab.setLayout(new java.awt.BorderLayout());

        pnlInfoCards.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfoCards.setPreferredSize(new java.awt.Dimension(545, 640));
        pnlInfoCards.setLayout(new java.awt.CardLayout());

        pnlCollectionInfo.setPreferredSize(new java.awt.Dimension(515, 500));

        jLabel19.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel19.text")); // NOI18N

        jLabel20.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel20.text")); // NOI18N

        jLabel21.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel21.text")); // NOI18N

        jLabel22.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel22.text")); // NOI18N

        jLabel23.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel23.text")); // NOI18N

        jLabel24.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel24.text")); // NOI18N

        jLabel25.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel25.text")); // NOI18N

        jLabel26.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel26.text")); // NOI18N

        jLabel27.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel27.text")); // NOI18N

        lblInfoCollectionCreatedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionCreatedDate.text")); // NOI18N

        lblInfoCollectionModifiedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionModifiedDate.text")); // NOI18N

        lblInfoCollectionOwner.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionOwner.text")); // NOI18N

        lblInfoCollectionOwnerZone.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionOwnerZone.text")); // NOI18N

        lblInfoCollectionType.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionType.text")); // NOI18N

        lblInfoCollectionObjectPath.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionObjectPath.text")); // NOI18N

        lblInfoCollectionDescription.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionDescription.text")); // NOI18N

        lblInfoCollectionInfo1.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionInfo1.text")); // NOI18N

        lblInfoCollectionInfo2.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoCollectionInfo2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlCollectionInfoLayout = new org.jdesktop.layout.GroupLayout(pnlCollectionInfo);
        pnlCollectionInfo.setLayout(pnlCollectionInfoLayout);
        pnlCollectionInfoLayout.setHorizontalGroup(
            pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCollectionInfoLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCollectionInfoLayout.createSequentialGroup()
                        .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel22)
                            .add(jLabel23)
                            .add(jLabel24)
                            .add(jLabel25)
                            .add(jLabel26)
                            .add(jLabel27))
                        .add(24, 24, 24)
                        .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblInfoCollectionOwnerZone, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                            .add(lblInfoCollectionType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoCollectionObjectPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoCollectionDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoCollectionInfo1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoCollectionInfo2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCollectionInfoLayout.createSequentialGroup()
                        .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel19)
                            .add(jLabel20)
                            .add(jLabel21))
                        .add(47, 47, 47)
                        .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(lblInfoCollectionCreatedDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                            .add(lblInfoCollectionModifiedDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoCollectionOwner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlCollectionInfoLayout.setVerticalGroup(
            pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCollectionInfoLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(lblInfoCollectionCreatedDate))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(lblInfoCollectionModifiedDate))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel21)
                    .add(lblInfoCollectionOwner))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(lblInfoCollectionOwnerZone))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(lblInfoCollectionType))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(lblInfoCollectionObjectPath))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(lblInfoCollectionDescription))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(lblInfoCollectionInfo1))
                .add(18, 18, 18)
                .add(pnlCollectionInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel27)
                    .add(lblInfoCollectionInfo2))
                .addContainerGap(324, Short.MAX_VALUE))
        );

        pnlInfoCards.add(pnlCollectionInfo, "cardCollectionInfo");

        pnlObjectInfo.setPreferredSize(new java.awt.Dimension(523, 530));

        jLabel3.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel3.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel5.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel6.text")); // NOI18N

        jLabel7.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel7.text")); // NOI18N

        jLabel8.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel8.text")); // NOI18N

        jLabel9.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel9.text")); // NOI18N

        jLabel10.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel10.text")); // NOI18N

        jLabel11.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel11.text")); // NOI18N

        jLabel12.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel12.text")); // NOI18N

        jLabel13.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel13.text")); // NOI18N

        jLabel14.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel14.text")); // NOI18N

        jLabel15.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel15.text")); // NOI18N

        jLabel16.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel16.text")); // NOI18N

        lblInfoObjectSize.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectSize.text")); // NOI18N

        lblInfoObjectCreatedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectCreatedDate.text")); // NOI18N

        lblInfoObjectModifiedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectModifiedDate.text")); // NOI18N

        lblInfoObjectOwner.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectOwner.text")); // NOI18N

        lblInfoObjectOwnerZone.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectOwnerZone.text")); // NOI18N

        lblInfoObjectDataPath.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectDataPath.text")); // NOI18N

        lblInfoObjectResourceGroup.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectResourceGroup.text")); // NOI18N

        lblInfoObjectChecksum.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectChecksum.text")); // NOI18N

        lblInfoObjectResource.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectResource.text")); // NOI18N

        lblInfoObjectReplicaNumber.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectReplicaNumber.text")); // NOI18N

        lblInfoObjectReplicationStatus.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectReplicationStatus.text")); // NOI18N

        lblInfoObjectStatus.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectStatus.text")); // NOI18N

        lblInfoObjectType.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectType.text")); // NOI18N

        lblInfoObjectVersion.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfoObjectVersion.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlObjectInfoLayout = new org.jdesktop.layout.GroupLayout(pnlObjectInfo);
        pnlObjectInfo.setLayout(pnlObjectInfoLayout);
        pnlObjectInfoLayout.setHorizontalGroup(
            pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlObjectInfoLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlObjectInfoLayout.createSequentialGroup()
                        .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4)
                            .add(jLabel5)
                            .add(jLabel6)
                            .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel12))
                        .add(22, 22, 22)
                        .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(lblInfoObjectResource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectReplicaNumber, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectSize, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                            .add(lblInfoObjectCreatedDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectModifiedDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectOwner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectOwnerZone, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectDataPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectResourceGroup, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectChecksum, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(pnlObjectInfoLayout.createSequentialGroup()
                        .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel14)
                            .add(jLabel15)
                            .add(jLabel16))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblInfoObjectReplicationStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblInfoObjectVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        pnlObjectInfoLayout.setVerticalGroup(
            pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlObjectInfoLayout.createSequentialGroup()
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlObjectInfoLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(lblInfoObjectSize))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlObjectInfoLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel3)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblInfoObjectCreatedDate)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(lblInfoObjectModifiedDate))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblInfoObjectOwner)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(lblInfoObjectOwnerZone))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblInfoObjectDataPath)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblInfoObjectResourceGroup))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblInfoObjectChecksum)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11)
                    .add(lblInfoObjectResource))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(lblInfoObjectReplicaNumber))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(lblInfoObjectReplicationStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(lblInfoObjectStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(lblInfoObjectType))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlObjectInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(lblInfoObjectVersion))
                .addContainerGap(316, Short.MAX_VALUE))
        );

        pnlInfoCards.add(pnlObjectInfo, "cardObjectInfo");

        pnlInfoTab.add(pnlInfoCards, java.awt.BorderLayout.CENTER);

        pnlTagsComments.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel17.text")); // NOI18N

        txtInfoTags.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.txtInfoTags.text")); // NOI18N

        jLabel18.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel18.text")); // NOI18N

        textareaInfoComments.setColumns(20);
        textareaInfoComments.setRows(5);
        jScrollPane1.setViewportView(textareaInfoComments);

        btnUpdateTagsComments.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnUpdateTagsComments.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlTagsCommentsLayout = new org.jdesktop.layout.GroupLayout(pnlTagsComments);
        pnlTagsComments.setLayout(pnlTagsCommentsLayout);
        pnlTagsCommentsLayout.setHorizontalGroup(
            pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTagsCommentsLayout.createSequentialGroup()
                .add(pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTagsCommentsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel17)
                            .add(jLabel18))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 66, Short.MAX_VALUE)
                        .add(pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtInfoTags)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                        .add(6, 6, 6))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTagsCommentsLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(btnUpdateTagsComments)))
                .addContainerGap())
        );
        pnlTagsCommentsLayout.setVerticalGroup(
            pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTagsCommentsLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(txtInfoTags, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlTagsCommentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel18)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnUpdateTagsComments)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlInfoTab.add(pnlTagsComments, java.awt.BorderLayout.SOUTH);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlInfoTab.TabConstraints.tabTitle"), pnlInfoTab); // NOI18N

        pnlMetadataTab.setLayout(new java.awt.BorderLayout());

        pnlMetadataTable.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Attribute", "Value", "Unit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        pnlMetadataTable.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pnlMetadataTab.add(pnlMetadataTable, java.awt.BorderLayout.CENTER);

        pnlMetaDataEdit.setPreferredSize(new java.awt.Dimension(527, 200));

        jLabel28.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel28.text")); // NOI18N

        jLabel29.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel29.text")); // NOI18N

        jLabel30.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel30.text")); // NOI18N

        btnMetadataClear.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnMetadataClear.text")); // NOI18N

        btnMetadataUpdateCreate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnMetadataUpdateCreate.text")); // NOI18N
        btnMetadataUpdateCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMetadataUpdateCreateActionPerformed(evt);
            }
        });

        txtMetadataAttribute.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.txtMetadataAttribute.text")); // NOI18N

        txtMetadataValue.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.txtMetadataValue.text")); // NOI18N

        txtMetadataUnit.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.txtMetadataUnit.text")); // NOI18N

        btnMetadataDelete.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnMetadataDelete.text")); // NOI18N
        btnMetadataDelete.setActionCommand(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnMetadataDelete.actionCommand")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlMetaDataEditLayout = new org.jdesktop.layout.GroupLayout(pnlMetaDataEdit);
        pnlMetaDataEdit.setLayout(pnlMetaDataEditLayout);
        pnlMetaDataEditLayout.setHorizontalGroup(
            pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMetaDataEditLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlMetaDataEditLayout.createSequentialGroup()
                        .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel28)
                            .add(jLabel29)
                            .add(jLabel30))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtMetadataValue)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMetadataAttribute)
                            .add(txtMetadataUnit)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMetaDataEditLayout.createSequentialGroup()
                        .add(0, 296, Short.MAX_VALUE)
                        .add(btnMetadataClear)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnMetadataDelete)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnMetadataUpdateCreate)))
                .addContainerGap())
        );
        pnlMetaDataEditLayout.setVerticalGroup(
            pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMetaDataEditLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel28)
                    .add(txtMetadataAttribute, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(txtMetadataValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel30)
                    .add(txtMetadataUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(23, 23, 23)
                .add(pnlMetaDataEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnMetadataClear)
                    .add(btnMetadataUpdateCreate)
                    .add(btnMetadataDelete))
                .addContainerGap())
        );

        pnlMetadataTab.add(pnlMetaDataEdit, java.awt.BorderLayout.SOUTH);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlMetadataTab.TabConstraints.tabTitle"), pnlMetadataTab); // NOI18N

        pnlPermissionsTab.setLayout(new java.awt.BorderLayout());

        pnlPermissionsTable.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", ""},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "User Name", "Share Permission"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable2);

        pnlPermissionsTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        pnlPermissionsTab.add(pnlPermissionsTable, java.awt.BorderLayout.CENTER);

        pnlPermissionEdit.setPreferredSize(new java.awt.Dimension(527, 200));

        jLabel31.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel31.text")); // NOI18N

        jLabel32.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel32.text")); // NOI18N

        jButton1.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jButton1.text")); // NOI18N

        jButton2.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jButton2.text")); // NOI18N

        jButton3.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jButton3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlPermissionEditLayout = new org.jdesktop.layout.GroupLayout(pnlPermissionEdit);
        pnlPermissionEdit.setLayout(pnlPermissionEditLayout);
        pnlPermissionEditLayout.setHorizontalGroup(
            pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPermissionEditLayout.createSequentialGroup()
                .add(45, 45, 45)
                .add(pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel31)
                    .add(jLabel32))
                .add(32, 32, 32)
                .add(pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jComboBox2, 0, 280, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(pnlPermissionEditLayout.createSequentialGroup()
                .addContainerGap(293, Short.MAX_VALUE)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3)
                .add(19, 19, 19))
        );
        pnlPermissionEditLayout.setVerticalGroup(
            pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPermissionEditLayout.createSequentialGroup()
                .add(45, 45, 45)
                .add(pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(31, 31, 31)
                .add(pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 35, Short.MAX_VALUE)
                .add(pnlPermissionEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2)
                    .add(jButton3))
                .addContainerGap())
        );

        pnlPermissionsTab.add(pnlPermissionEdit, java.awt.BorderLayout.SOUTH);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlPermissionsTab.TabConstraints.tabTitle"), pnlPermissionsTab); // NOI18N

        jPanel1.add(tabbedpanelMain, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMetadataUpdateCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMetadataUpdateCreateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMetadataUpdateCreateActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMetadataClear;
    private javax.swing.JButton btnMetadataDelete;
    private javax.swing.JButton btnMetadataUpdateCreate;
    private javax.swing.JButton btnUpdateTagsComments;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblInfoCollectionCreatedDate;
    private javax.swing.JLabel lblInfoCollectionDescription;
    private javax.swing.JLabel lblInfoCollectionInfo1;
    private javax.swing.JLabel lblInfoCollectionInfo2;
    private javax.swing.JLabel lblInfoCollectionModifiedDate;
    private javax.swing.JLabel lblInfoCollectionObjectPath;
    private javax.swing.JLabel lblInfoCollectionOwner;
    private javax.swing.JLabel lblInfoCollectionOwnerZone;
    private javax.swing.JLabel lblInfoCollectionType;
    private javax.swing.JLabel lblInfoObjectChecksum;
    private javax.swing.JLabel lblInfoObjectCreatedDate;
    private javax.swing.JLabel lblInfoObjectDataPath;
    private javax.swing.JLabel lblInfoObjectModifiedDate;
    private javax.swing.JLabel lblInfoObjectName;
    private javax.swing.JLabel lblInfoObjectOwner;
    private javax.swing.JLabel lblInfoObjectOwnerZone;
    private javax.swing.JLabel lblInfoObjectParent;
    private javax.swing.JLabel lblInfoObjectReplicaNumber;
    private javax.swing.JLabel lblInfoObjectReplicationStatus;
    private javax.swing.JLabel lblInfoObjectResource;
    private javax.swing.JLabel lblInfoObjectResourceGroup;
    private javax.swing.JLabel lblInfoObjectSize;
    private javax.swing.JLabel lblInfoObjectStatus;
    private javax.swing.JLabel lblInfoObjectType;
    private javax.swing.JLabel lblInfoObjectVersion;
    private javax.swing.JLabel lblObjectCollection;
    private javax.swing.JPanel pnlCollectionInfo;
    private javax.swing.JPanel pnlInfoCards;
    private javax.swing.JPanel pnlInfoTab;
    private javax.swing.JPanel pnlMetaDataEdit;
    private javax.swing.JPanel pnlMetadataTab;
    private javax.swing.JPanel pnlMetadataTable;
    private javax.swing.JPanel pnlObjectInfo;
    private javax.swing.JPanel pnlPermissionEdit;
    private javax.swing.JPanel pnlPermissionsTab;
    private javax.swing.JPanel pnlPermissionsTable;
    private javax.swing.JPanel pnlSelectedObject;
    private javax.swing.JPanel pnlTagsComments;
    private javax.swing.JTabbedPane tabbedpanelMain;
    private javax.swing.JTextArea textareaInfoComments;
    private javax.swing.JTextField txtInfoTags;
    private javax.swing.JTextField txtMetadataAttribute;
    private javax.swing.JTextField txtMetadataUnit;
    private javax.swing.JTextField txtMetadataValue;
    // End of variables declaration//GEN-END:variables
}
