/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.irods.jargon.conveyor.core.QueueManagerService;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.CatNoAccessException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAOImpl;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.services.IRODSFileService;
import org.irods.jargon.idrop.desktop.systraygui.utils.FieldFormatHelper;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSOutlineModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSTree;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.MetadataTableModel;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.PermissionsTableModel;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferType;
import org.irods.jargon.usertagging.domain.IRODSTagValue;
import org.irods.jargon.usertagging.tags.FreeTaggingService;
import org.irods.jargon.usertagging.tags.IRODSTaggingService;
import org.irods.jargon.usertagging.tags.TaggingServiceFactory;
import org.irods.jargon.usertagging.tags.TaggingServiceFactoryImpl;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class IRODSInfoDialog extends javax.swing.JDialog implements
        ListSelectionListener, ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -943089734580790199L;
    private final iDrop idropGUI;
    private final IRODSAccount irodsAccount;
    private String selectedObjectFullPath;
    private String selectedObjectName;
    private String selectedObjectParent;
    private final IRODSFileSystem irodsFileSystem;
    private boolean isFile;
    private final IRODSTree irodsTree;
    private IRODSInfoDialog dialog;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(IRODSTree.class);
    private List<JCheckBox> boxes = new ArrayList<JCheckBox>();

    // private final String fileName;
    /**
     * Creates new form IRODSInfoDialog
     */
    // public IRODSInfoDialog(java.awt.Frame parent, boolean modal) {
    // super(parent, modal);
    // initComponents();
    // }
    public IRODSInfoDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree) {

        super(parent, modal);
        idropGUI = parent;
        irodsAccount = idropGUI.getiDropCore().getIrodsAccount();
        irodsFileSystem = idropGUI.getiDropCore().getIrodsFileSystem();
        this.irodsTree = irodsTree;
        initSelectedObjectName();
        initComponents();

        selectInfoCard();

        initializeFileInfo();
        initMetadataInfo();
        initPermissionInfo();

    }

    private void initSelectedObjectName() {

        IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree
                .getModel();
        ListSelectionModel selectionModel = irodsTree.getSelectionModel();
        int idxStart = selectionModel.getMinSelectionIndex();

        IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel.getValueAt(
                idxStart, 0);
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
            lblInfoObjectName.setText(MiscIRODSUtils.abbreviateFileName(selectedObjectName));
            lblInfoObjectName.setToolTipText(selectedObjectName);
        }
        if (selectedObjectParent != null) {
            lblInfoObjectParent.setText(MiscIRODSUtils.abbreviateFileName(selectedObjectParent));
                        lblInfoObjectParent.setToolTipText(selectedObjectParent);

        }
    }

    private void initializeFileInfo() {
        dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {

                    TaggingServiceFactory taggingServiceFactory = new TaggingServiceFactoryImpl(
                            irodsFileSystem.getIRODSAccessObjectFactory());
                    FreeTaggingService freeTaggingService = taggingServiceFactory
                            .instanceFreeTaggingService(irodsAccount);
                    IRODSTaggingService irodsTaggingService = taggingServiceFactory
                            .instanceIrodsTaggingService(irodsAccount);

                    if (isCollection()) {

                        isFile = false;
                        CollectionAO collectionAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getCollectionAO(
                                irodsAccount);
                        Collection collection = collectionAO
                                .findByAbsolutePath(selectedObjectFullPath);

                        if (collection.getCreatedAt().toString() != null) {
                            lblInfoCollectionCreatedDate.setText(collection
                                    .getCreatedAt().toString());
                        } else {
                            lblInfoCollectionCreatedDate.setText("");
                        }

                        if (collection.getModifiedAt().toString() != null) {
                            lblInfoCollectionModifiedDate.setText(collection
                                    .getModifiedAt().toString());
                        } else {
                            lblInfoCollectionModifiedDate.setText("");
                        }

                        if (collection.getCollectionOwnerName() != null) {
                            lblInfoCollectionOwner.setText(collection
                                    .getCollectionOwnerName());
                        } else {
                            lblInfoCollectionOwner.setText("");
                        }

                        if (collection.getComments() != null) {
                            lblInfoCollectionDescription.setText(collection
                                    .getComments());
                        } else {
                            lblInfoCollectionDescription.setText("");
                        }

                        if (collection.getSpecColType() != null) {
                            lblInfoCollectionType.setText(collection
                                    .getSpecColType().name());
                        } else {
                            lblInfoCollectionType.setText("");
                        }

                        if (collection.getCollectionOwnerZone() != null) {
                            lblInfoCollectionOwnerZone.setText(collection
                                    .getCollectionOwnerZone());
                        } else {
                            lblInfoCollectionOwnerZone.setText("");
                        }

                        if (collection.getObjectPath() != null) {
                            lblInfoCollectionObjectPath.setText(MiscIRODSUtils.abbreviateFileName(collection
                                    .getObjectPath()));
                              lblInfoCollectionObjectPath.setToolTipText(collection.getObjectPath());
                           
                        } else {
                            lblInfoCollectionObjectPath.setText("");
                                                          lblInfoCollectionObjectPath.setToolTipText(collection.getObjectPath());

                        }
                        
                        

                        if (collection.getInfo1() != null) {
                            lblInfoCollectionInfo1.setText(collection
                                    .getInfo1());
                        } else {
                            lblInfoCollectionInfo1.setText("");
                        }

                        if (collection.getInfo2() != null) {
                            lblInfoCollectionInfo2.setText(collection
                                    .getInfo2());
                        } else {
                            lblInfoCollectionInfo2.setText("");
                        }

                        // now populate tags and comments for collection
                        txtInfoTags.setText(freeTaggingService
                                .getTagsForCollectionInFreeTagForm(
                                selectedObjectFullPath)
                                .getSpaceDelimitedTagsForDomain());
                        IRODSTagValue irodsTagValue = irodsTaggingService
                                .getDescriptionOnCollectionForLoggedInUser(selectedObjectFullPath);
                        if (irodsTagValue != null) {
                            textareaInfoComments.setText(irodsTagValue
                                    .getTagData());
                        }

                    } else {
                        isFile = true;
                        DataObjectAO dataObjectAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getDataObjectAO(
                                irodsAccount);
                        DataObject dataObject = dataObjectAO
                                .findByAbsolutePath(selectedObjectFullPath);

                        if (dataObject.getDataSize() >= 0) {
                            lblInfoObjectSize.setText(FieldFormatHelper
                                    .formatFileLength(dataObject.getDataSize()));
                        } else {
                            lblInfoObjectSize.setText("");
                        }

                        if (dataObject.getCreatedAt().toString() != null) {
                            lblInfoObjectCreatedDate.setText(dataObject
                                    .getCreatedAt().toString());
                        } else {
                            lblInfoObjectCreatedDate.setText("");
                        }

                        if (dataObject.getUpdatedAt().toString() != null) {
                            lblInfoObjectModifiedDate.setText(dataObject
                                    .getUpdatedAt().toString());
                        } else {
                            lblInfoObjectCreatedDate.setText("");
                        }

                        if (dataObject.getDataOwnerName() != null) {
                            lblInfoObjectOwner.setText(dataObject
                                    .getDataOwnerName());
                        } else {
                            lblInfoObjectOwner.setText("");
                        }

                        if (dataObject.getDataOwnerZone() != null) {
                            lblInfoObjectOwnerZone.setText(dataObject
                                    .getDataOwnerZone());
                        } else {
                            lblInfoObjectOwnerZone.setText("");
                        }

                        if (dataObject.getDataPath() != null) {
                            lblInfoObjectDataPath.setText(MiscIRODSUtils.abbreviateFileName(dataObject
                                    .getDataPath()));
                             lblInfoObjectDataPath.setToolTipText(dataObject
                                    .getDataPath());
                        } else {
                            lblInfoObjectDataPath.setText("");
                             lblInfoObjectDataPath.setToolTipText("");
                        }

                        if (dataObject.getResourceGroupName() != null) {
                            lblInfoObjectResourceGroup.setText(dataObject
                                    .getResourceGroupName());
                        } else {
                            lblInfoObjectResourceGroup.setText("");
                        }

                        if (dataObject.getChecksum() != null) {
                            lblInfoObjectChecksum.setText(dataObject
                                    .getChecksum());
                        } else {
                            lblInfoObjectChecksum.setText("");
                        }

                        if (dataObject.getResourceName() != null) {
                            lblInfoObjectResource.setText(dataObject
                                    .getResourceName());
                        } else {
                            lblInfoObjectResource.setText("");
                        }

                        if (dataObject.getDataReplicationNumber() >= 0) {
                            lblInfoObjectReplicaNumber.setText(Integer
                                    .toString(dataObject
                                    .getDataReplicationNumber()));
                        } else {
                            lblInfoObjectReplicaNumber.setText("");
                        }

                        if (dataObject.getReplicationStatus() != null) {
                            lblInfoObjectReplicationStatus.setText(dataObject
                                    .getReplicationStatus());
                        } else {
                            lblInfoObjectReplicationStatus.setText("");
                        }

                        if (dataObject.getDataStatus() != null) {
                            lblInfoObjectStatus.setText(dataObject
                                    .getDataStatus());
                        } else {
                            lblInfoObjectStatus.setText("");
                        }

                        if (dataObject.getDataTypeName() != null) {
                            lblInfoObjectType.setText(dataObject
                                    .getDataTypeName());
                        } else {
                            lblInfoObjectType.setText("");
                        }

                        if (dataObject.getDataVersion() >= 0) {
                            lblInfoObjectVersion.setText(Integer
                                    .toString(dataObject.getDataVersion()));
                        } else {
                            lblInfoObjectVersion.setText("");
                        }

                        // now populate tags and comments for data object
                        txtInfoTags.setText(freeTaggingService
                                .getTagsForDataObjectInFreeTagForm(
                                selectedObjectFullPath)
                                .getSpaceDelimitedTagsForDomain());
                        IRODSTagValue irodsTagValue = irodsTaggingService
                                .getDescriptionOnDataObjectForLoggedInUser(selectedObjectFullPath);
                        if (irodsTagValue != null) {
                            textareaInfoComments.setText(irodsTagValue
                                    .getTagData());
                        }

                    }

                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (JargonException ex) {
                    Exceptions.printStackTrace(ex);
                }

                dialog.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void initMetadataInfo() {
        dialog = this;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    // set up metadata table and table model
                    IRODSFileService irodsFileService = new IRODSFileService(
                            irodsAccount, irodsFileSystem);
                    MetadataTableModel metadataTableModel;

                    if (isCollection()) {
                        metadataTableModel = new MetadataTableModel(
                                irodsFileService
                                .getMetadataForCollection(selectedObjectFullPath));
                    } else {
                        metadataTableModel = new MetadataTableModel(
                                irodsFileService.getMetadataForDataObject(
                                selectedObjectParent,
                                selectedObjectName));
                    }
                    tableMetadata.setModel(metadataTableModel);
                    tableMetadata.getSelectionModel().addListSelectionListener(
                            dialog);
                    tableMetadata.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent evt) {
                            if (evt.getClickCount() == 2) {
                                Point pnt = evt.getPoint();
                                int row = tableMetadata.rowAtPoint(pnt);
                                MetadataTableModel model = (MetadataTableModel) tableMetadata.getModel();
                                MetaDataAndDomainData metaDataAndDomainData = (MetaDataAndDomainData) model.getRow(row);
                                AvuData avuData = new AvuData();
                                String attr = metaDataAndDomainData.getAvuAttribute();
                                avuData.setAttribute(attr);
                                String value = metaDataAndDomainData.getAvuValue();
                                avuData.setValue(value);
                                String unit = metaDataAndDomainData.getAvuUnit();
                                avuData.setUnit(unit);

                                EditMetaDataDialog editMetaDataDialog = new EditMetaDataDialog(
                                        null,
                                        true,
                                        row,
                                        selectedObjectFullPath,
                                        avuData,
                                        isCollection(),
                                        irodsFileSystem,
                                        irodsAccount,
                                        model);

                                editMetaDataDialog.setLocation(
                                        (int) dialog.getLocation().getX(), (int) dialog.getLocation().getY());
                                editMetaDataDialog.setVisible(true);
                            }
                        }
                    });
                    tableMetadata.validate();

                } catch (IdropException ex) {
                    Logger.getLogger(MetadataViewDialog.class.getName()).log(
                            Level.SEVERE, null, ex);
                    idropGUI.showIdropException(ex);
                }

                dialog.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void initPermissionInfo() {
        dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // this list of ACLs contains permission that are not suppoted?
                // List<FilePermissionEnum> permissions =
                // FilePermissionEnum.listAllValues();
                // for (FilePermissionEnum permission: permissions) {
                // cbPermissionsPermission.addItem(permission.name());
                // }
                // will just do my own for now
                // cbPermissionsPermission.addItem("NONE");
//				javax.swing.JComboBox tableCombo = new javax.swing.JComboBox();
//				tableCombo.addItem("READ");
//				tableCombo.addItem("WRITE");
//				tableCombo.addItem("OWN");

                // set up permission table and table model
                PermissionsTableModel permissionsTableModel = null;
                try {
                    if (isCollection()) {
                        CollectionAO collectionAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getCollectionAO(
                                irodsAccount);
                        permissionsTableModel = new PermissionsTableModel(
                                collectionAO
                                .listPermissionsForCollection(selectedObjectFullPath));
                    } else {
                        DataObjectAO dataObjectAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getDataObjectAO(
                                irodsAccount);
                        permissionsTableModel = new PermissionsTableModel(
                                dataObjectAO
                                .listPermissionsForDataObject(selectedObjectFullPath));
                    }
                } catch (JargonException ex) {
                    Exceptions.printStackTrace(ex);
                }

                tablePermissions.setModel(permissionsTableModel);
                tablePermissions.getSelectionModel().addListSelectionListener(
                        dialog);
                tablePermissions.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() == 2) {
                            Point pnt = evt.getPoint();
                            int row = tablePermissions.rowAtPoint(pnt);
                            PermissionsTableModel model = (PermissionsTableModel) tablePermissions.getModel();
                            UserFilePermission userFilePermission = (UserFilePermission) model.getRow(row);

                            EditPermissionsDialog editPermissionsDialog = new EditPermissionsDialog(
                                    dialog,
                                    true,
                                    row,
                                    selectedObjectFullPath,
                                    userFilePermission,
                                    isCollection(),
                                    irodsFileSystem,
                                    irodsAccount,
                                    model);

                            editPermissionsDialog.setLocation(
                                    (int) dialog.getLocation().getX(), (int) dialog.getLocation().getY());
                            editPermissionsDialog.setVisible(true);
                        }
                    }
                });
//                  TableColumn permissionColumn = tablePermissions
//                        .getColumnModel().getColumn(1);
//                  permissionColumn
//                        .setCellEditor(new DefaultCellEditor(tableCombo));
//                  permissionsTableModel.resetOriginalPermissionList();
                tablePermissions.validate();

                dialog.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private boolean isCollection() {

        boolean state = false;
        CollectionAndDataObjectListingEntry entry = null;

        // perhaps should throw exception if these vital member variables are
        // null
        if ((selectedObjectFullPath != null) && (irodsFileSystem != null)
                && (irodsAccount != null)) {
            CollectionAndDataObjectListAndSearchAOImpl collectionAndDataObjectListAndSearchAOImpl;
            try {
                collectionAndDataObjectListAndSearchAOImpl = (CollectionAndDataObjectListAndSearchAOImpl) irodsFileSystem
                        .getIRODSAccessObjectFactory()
                        .getCollectionAndDataObjectListAndSearchAO(irodsAccount);
                entry = collectionAndDataObjectListAndSearchAOImpl
                        .getCollectionAndDataObjectListingEntryAtGivenAbsolutePath(selectedObjectFullPath);
            } catch (JargonException ex) {
                // TODO: respond correctly here
                Exceptions.printStackTrace(ex);
            }

            state = entry.isCollection();
        }

        return state;
    }

    private void updateMetadataDeleteBtnStatus(final int selectedRowCount) {
        // delete button should only be enabled when there is a tableMetadata
        // selection
        // add all text fields are populated
        btnDeleteMetadata.setEnabled(selectedRowCount > 0);
    }

    private void updatePermissionsDeleteBtnStatus(final int selectedRowCount) {
        // delete button should only be enabled when there is a tableMetadata or
        // tablePermissions selection
        btnDeleteSharePermissions.setEnabled(selectedRowCount > 0);
    }

    // ListSelectionListener methods
    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        int selectedRowCount = 0;

        if (!lse.getValueIsAdjusting()) {
            // determine which table is selected
            // Metadata Table?
            if (lse.getSource() == tableMetadata.getSelectionModel()) {
                selectedRowCount = tableMetadata.getSelectedRowCount();
                updateMetadataDeleteBtnStatus(selectedRowCount);
            } else { // Permissions Table
                selectedRowCount = tablePermissions.getSelectedRowCount();
                updatePermissionsDeleteBtnStatus(selectedRowCount);
            }
        }
    }

    // end ListSelectionListener methods
    // ActionListener Methods
    @Override
    public void actionPerformed(final ActionEvent ae) {
        // if (ae.getSource() == cbPermissionsPermission ||
        // ae.getSource() == cbPermissionsUserName) {
        // updatePermissonsCreateBtnStatus();
        // }
    }

    // end ActionListener Methods
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        tableMetadata = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        btnAddMetadata = new javax.swing.JButton();
        btnDeleteMetadata = new javax.swing.JButton();
        pnlPermissionsTab = new javax.swing.JPanel();
        pnlPermissionsTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablePermissions = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        btnAddSharePermissions = new javax.swing.JButton();
        btnDeleteSharePermissions = new javax.swing.JButton();
        pnlReplication = new javax.swing.JPanel();
        scrollReplicationResources = new javax.swing.JScrollPane();
        pnlReplicationResources = new javax.swing.JPanel();
        pnlReplicaionTools = new javax.swing.JPanel();
        btnReplicate = new javax.swing.JButton();
        pnlCloseBtn = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 10, 10, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 750));
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
                .addContainerGap(54, Short.MAX_VALUE))
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
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(pnlSelectedObject, java.awt.BorderLayout.PAGE_START);

        tabbedpanelMain.setPreferredSize(new java.awt.Dimension(600, 867));
        tabbedpanelMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabbedpanelMainComponentShown(evt);
            }
        });

        pnlInfoTab.setLayout(new java.awt.BorderLayout());

        pnlInfoCards.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfoCards.setPreferredSize(new java.awt.Dimension(555, 640));
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
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pnlInfoCards.add(pnlCollectionInfo, "cardCollectionInfo");

        pnlObjectInfo.setPreferredSize(new java.awt.Dimension(550, 530));

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
                .addContainerGap(54, Short.MAX_VALUE))
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
                .addContainerGap(24, Short.MAX_VALUE))
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
        btnUpdateTagsComments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTagsCommentsActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 83, Short.MAX_VALUE)
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

        tableMetadata.setModel(new javax.swing.table.DefaultTableModel(
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
        tableMetadata.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(tableMetadata);

        pnlMetadataTable.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel10.setPreferredSize(new java.awt.Dimension(568, 44));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_190_circle_plus.png"))); // NOI18N
        btnAddMetadata.setMnemonic('+');
        btnAddMetadata.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddMetadata.text")); // NOI18N
        btnAddMetadata.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddMetadata.toolTipText")); // NOI18N
        btnAddMetadata.setMaximumSize(null);
        btnAddMetadata.setMinimumSize(null);
        btnAddMetadata.setPreferredSize(null);
        btnAddMetadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMetadataActionPerformed(evt);
            }
        });
        jPanel10.add(btnAddMetadata);

        btnDeleteMetadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnDeleteMetadata.setMnemonic('-');
        btnDeleteMetadata.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteMetadata.text")); // NOI18N
        btnDeleteMetadata.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteMetadata.toolTipText")); // NOI18N
        btnDeleteMetadata.setEnabled(false);
        btnDeleteMetadata.setMaximumSize(null);
        btnDeleteMetadata.setMinimumSize(null);
        btnDeleteMetadata.setPreferredSize(null);
        btnDeleteMetadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteMetadataActionPerformed(evt);
            }
        });
        jPanel10.add(btnDeleteMetadata);

        pnlMetadataTable.add(jPanel10, java.awt.BorderLayout.SOUTH);

        pnlMetadataTab.add(pnlMetadataTable, java.awt.BorderLayout.CENTER);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlMetadataTab.TabConstraints.tabTitle"), pnlMetadataTab); // NOI18N

        pnlPermissionsTab.setLayout(new java.awt.BorderLayout());

        pnlPermissionsTable.setLayout(new java.awt.BorderLayout());

        tablePermissions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "User Name", "Share Permission"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablePermissions.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(tablePermissions);

        pnlPermissionsTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.setMinimumSize(null);
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddSharePermissions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_190_circle_plus.png"))); // NOI18N
        btnAddSharePermissions.setMnemonic('+');
        btnAddSharePermissions.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddSharePermissions.text")); // NOI18N
        btnAddSharePermissions.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddSharePermissions.toolTipText")); // NOI18N
        btnAddSharePermissions.setMaximumSize(null);
        btnAddSharePermissions.setMinimumSize(null);
        btnAddSharePermissions.setPreferredSize(null);
        btnAddSharePermissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSharePermissionsActionPerformed(evt);
            }
        });
        jPanel7.add(btnAddSharePermissions);

        btnDeleteSharePermissions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnDeleteSharePermissions.setMnemonic('-');
        btnDeleteSharePermissions.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteSharePermissions.text")); // NOI18N
        btnDeleteSharePermissions.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteSharePermissions.toolTipText")); // NOI18N
        btnDeleteSharePermissions.setEnabled(false);
        btnDeleteSharePermissions.setMaximumSize(null);
        btnDeleteSharePermissions.setMinimumSize(null);
        btnDeleteSharePermissions.setPreferredSize(null);
        btnDeleteSharePermissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSharePermissionsActionPerformed(evt);
            }
        });
        jPanel7.add(btnDeleteSharePermissions);

        pnlPermissionsTable.add(jPanel7, java.awt.BorderLayout.SOUTH);

        pnlPermissionsTab.add(pnlPermissionsTable, java.awt.BorderLayout.CENTER);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlPermissionsTab.TabConstraints.tabTitle"), pnlPermissionsTab); // NOI18N

        pnlReplication.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                pnlReplicationComponentShown(evt);
            }
        });
        pnlReplication.setLayout(new java.awt.BorderLayout());

        pnlReplicationResources.setLayout(new java.awt.GridLayout(0, 1));
        scrollReplicationResources.setViewportView(pnlReplicationResources);

        pnlReplication.add(scrollReplicationResources, java.awt.BorderLayout.CENTER);

        pnlReplicaionTools.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnReplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_464_server_plus.png"))); // NOI18N
        btnReplicate.setMnemonic('p');
        btnReplicate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnReplicate.text")); // NOI18N
        btnReplicate.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnReplicate.toolTipText")); // NOI18N
        btnReplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplicateActionPerformed(evt);
            }
        });
        pnlReplicaionTools.add(btnReplicate);

        pnlReplication.add(pnlReplicaionTools, java.awt.BorderLayout.SOUTH);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlReplication.TabConstraints.tabTitle"), pnlReplication); // NOI18N

        jPanel1.add(tabbedpanelMain, java.awt.BorderLayout.CENTER);

        pnlCloseBtn.setMinimumSize(null);
        pnlCloseBtn.setPreferredSize(null);
        pnlCloseBtn.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_081_refresh.png"))); // NOI18N
        btnRefresh.setMnemonic('r');
        btnRefresh.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnRefresh.text")); // NOI18N
        btnRefresh.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnRefresh.toolTipText")); // NOI18N
        btnRefresh.setMaximumSize(null);
        btnRefresh.setMinimumSize(null);
        btnRefresh.setPreferredSize(null);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnlCloseBtn.add(btnRefresh);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnClose.setMnemonic('c');
        btnClose.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.text")); // NOI18N
        btnClose.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.toolTipText")); // NOI18N
        btnClose.setMaximumSize(null);
        btnClose.setMinimumSize(null);
        btnClose.setName(""); // NOI18N
        btnClose.setPreferredSize(null);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        pnlCloseBtn.add(btnClose);

        jPanel1.add(pnlCloseBtn, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplicateActionPerformed
        List<Resource> currentResources;
        int replicatedCount = 0;
        // build a list of the current resources, if a data object, will be used
        // to decide what to replicate
        try {
            currentResources = buildCurrentResourcesList();
        } catch (IdropException ex) {
            Logger.getLogger(ReplicationDialog.class.getName()).log(
                    Level.SEVERE, null, ex);
            idropGUI.showIdropException(ex);
            return;
        }

        for (JCheckBox checkBox : boxes) {
            if (checkBox.isSelected()) {
                log.info("getting ready to replicate:{}", checkBox.getText());

                boolean foundResource = false;
                for (Resource currentResource : currentResources) {
                    if (currentResource.getName().equals(checkBox.getText())) {
                        foundResource = true;
                        break;
                    }
                }

                try {
                    QueueManagerService qms = this.idropGUI.getiDropCore().getConveyorService().getQueueManagerService();
                    Transfer transfer = new Transfer();
                    if (isFile && !foundResource) {
                        log.info("file not yet replicated to resource");

                        StringBuilder sb = new StringBuilder();
                        sb.append(selectedObjectParent);
                        sb.append("/");
                        sb.append(selectedObjectName);
                        replicatedCount++;



                        transfer.setTransferType(TransferType.REPLICATE);
                        transfer.setResourceName(checkBox.getText());
                        transfer.setIrodsAbsolutePath(sb.toString());

                    } else if (!isFile) {
                        log.info("this is a collection, do the replication");
                        replicatedCount++;



                        transfer.setTransferType(TransferType.REPLICATE);
                        transfer.setIrodsAbsolutePath(selectedObjectParent);
                        transfer.setResourceName(checkBox.getText());

                    }

                    qms.enqueueTransferOperation(transfer, this.idropGUI.getIrodsAccount());


                } catch (Exception ex) {
                    Logger.getLogger(ReplicationDialog.class.getName()).log(
                            Level.SEVERE, null, ex);
                    this.idropGUI.showIdropException(ex);
                    return;
                }

            }
        }

        final int replicationsDone = replicatedCount;
        final iDrop gui = idropGUI;

        // now dispose
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (replicationsDone > 0) {
                    gui.showMessageFromOperation("Replication has been placed into the queue for processing");
                } else {
                    gui.showMessageFromOperation("Nothing to replicate");

                }

            }
        });

        this.dispose();

    }//GEN-LAST:event_btnReplicateActionPerformed

    private void tabbedpanelMainComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabbedpanelMainComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tabbedpanelMainComponentShown

    private void pnlReplicationComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlReplicationComponentShown
        setUpReplicationData();
    }//GEN-LAST:event_pnlReplicationComponentShown

    private void btnAddMetadataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMetadataActionPerformed
        MetadataTableModel model = (MetadataTableModel) tableMetadata
                .getModel();

        AddMetadataDialog addMetadataDialog = new AddMetadataDialog(
                this,
                true,
                selectedObjectFullPath,
                isCollection(),
                irodsFileSystem,
                irodsAccount,
                model);

        addMetadataDialog.setLocation((int) this.getLocation().getX(),
                (int) this.getLocation().getY());
        addMetadataDialog.setVisible(true);
    }//GEN-LAST:event_btnAddMetadataActionPerformed

    private void btnDeleteMetadataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteMetadataActionPerformed

        if ((JOptionPane.showConfirmDialog(this,
                "Are you sure you wish to delete the selected metadata?",
                "Delete Metadata",
                JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

            AvuData avuData;
            CollectionAO collectionAO;
            DataObjectAO dataObjectAO;
            String attr = null;
            String value = null;
            String unit = null;

            // get selected rows to delete in metadata table
            int[] selectedRows = tableMetadata.getSelectedRows();
            int numRowsSelected = selectedRows.length;

            try {
                MetadataTableModel model = (MetadataTableModel) tableMetadata
                        .getModel();

                // first delete permission(s) from iRODS
                for (int selectedRow : selectedRows) {
                    // create AVU data object to delete
                    attr = (String) tableMetadata.getValueAt(selectedRow, 0);
                    value = (String) tableMetadata.getValueAt(selectedRow, 1);
                    unit = (String) tableMetadata.getValueAt(selectedRow, 2);
                    avuData = new AvuData(attr, value, unit);

                    if (isCollection()) {
                        collectionAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getCollectionAO(
                                irodsAccount);
                        collectionAO.deleteAVUMetadata(selectedObjectFullPath,
                                avuData);
                    } else {
                        dataObjectAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getDataObjectAO(
                                irodsAccount);
                        dataObjectAO.deleteAVUMetadata(selectedObjectFullPath,
                                avuData);
                    }
                }

                // have to remove rows in reverse
                for (int i = numRowsSelected - 1; i >= 0; i--) {
                    int selectedRow = selectedRows[i];
                    if (selectedRow >= 0) {
                        model.deleteRow(selectedRow);
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Metadata Deleted Successfully",
                        "Delete Metadata", JOptionPane.PLAIN_MESSAGE);

            } catch (JargonException ex) {
                log.error("metadata delete failed", ex);
                JOptionPane.showMessageDialog(this, "Metadata Delete Failed",
                        "Delete Metadata", JOptionPane.PLAIN_MESSAGE);
            }

        }
    }//GEN-LAST:event_btnDeleteMetadataActionPerformed

    private void setUpReplicationData() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    IRODSFileService irodsFileService = new IRODSFileService(
                            idropGUI.getIrodsAccount(), idropGUI.getiDropCore().getIrodsFileSystem());
                    List<Resource> resources = irodsFileService.getResources();
                    boxes = new ArrayList<JCheckBox>();

                    // if this is a file, list current resources for this data
                    // object
                    List<Resource> currentResources = buildCurrentResourcesList();

                    for (Resource resource : resources) {
                        JCheckBox rescBox = new JCheckBox();
                        rescBox.setText(resource.getName());

                        // if this resource is already replicated, a checkbox is
                        // initialized for that resource

                        for (Resource dataObjectResource : currentResources) {
                            if (dataObjectResource.getName().equals(
                                    resource.getName())) {
                                log.debug(
                                        "resource already replicates data object:{}",
                                        resource);
                                rescBox.setSelected(true);
                                break;
                            }
                        }

                        boxes.add(rescBox);
                    }

                    for (JCheckBox checkBox : boxes) {
                        pnlReplicationResources.add(checkBox);
                    }

                    scrollReplicationResources.validate();

                } catch (IdropException ex) {
                    Logger.getLogger(ReplicationDialog.class.getName()).log(
                            Level.SEVERE, null, ex);
                    idropGUI.showIdropException(ex);
                    return;
                }
            }
        });

    }

    private List<Resource> buildCurrentResourcesList() throws IdropException {
        // if a file, then see if it's already on the resc, otherwise add a
        // replicate
        // if this is a file, list current resources for this data object
        List<Resource> currentResources = null;
        if (isFile) {
            IRODSFileService irodsFileService;
            try {
                irodsFileService = new IRODSFileService(
                        idropGUI.getIrodsAccount(), idropGUI
                        .getiDropCore().getIrodsFileSystem());
                currentResources = irodsFileService.getResourcesForDataObject(
                        selectedObjectParent, selectedObjectName);
            } catch (IdropException ex) {
                Logger.getLogger(ReplicationDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                throw new IdropException(ex);
            }
        } else {
            currentResources = new ArrayList<Resource>();
        }
        return currentResources;
    }

    private void btnMetadataCreateActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMetadataCreateActionPerformed
    }// GEN-LAST:event_btnMetadataCreateActionPerformed

    private void btnUpdateTagsCommentsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateTagsCommentsActionPerformed

        TaggingServiceFactory taggingServiceFactory = null;
        ;
        IRODSTagValue irodsTagValue = null;

        try {
            taggingServiceFactory = new TaggingServiceFactoryImpl(
                    irodsFileSystem.getIRODSAccessObjectFactory());
            FreeTaggingService freeTaggingService = taggingServiceFactory
                    .instanceFreeTaggingService(irodsAccount);
            IRODSTaggingService irodsTaggingService = taggingServiceFactory
                    .instanceIrodsTaggingService(irodsAccount);

            // first update the tags
            String newTagStr = txtInfoTags.getText();
            if (newTagStr != null) { // && !newTagStr.isEmpty()) {

                // now need to diff against existing tags to see what to add and
                // what to delete
                String existingTags = null;
                if (isCollection()) {
                    existingTags = freeTaggingService
                            .getTagsForCollectionInFreeTagForm(
                            selectedObjectFullPath)
                            .getSpaceDelimitedTagsForDomain();
                } else {
                    existingTags = freeTaggingService
                            .getTagsForDataObjectInFreeTagForm(
                            selectedObjectFullPath)
                            .getSpaceDelimitedTagsForDomain();
                }

                List<String> existingTagList = Arrays.asList(existingTags
                        .split(" "));
                List<String> newTagsList = Arrays.asList(newTagStr.split(" +"));

                // find tags to delete and remove them
                Set<String> tagsToDeleteSet = new HashSet<String>(
                        existingTagList);
                tagsToDeleteSet.removeAll(newTagsList);
                String[] tagsToDelete = tagsToDeleteSet.toArray(new String[0]);
                for (String tag : tagsToDelete) {
                    if (tag.length() > 0) {
                        irodsTagValue = new IRODSTagValue(tag,
                                irodsAccount.getUserName());
                        if (isCollection()) {
                            irodsTaggingService.deleteTagFromCollection(
                                    selectedObjectFullPath, irodsTagValue);
                        } else {
                            irodsTaggingService.deleteTagFromDataObject(
                                    selectedObjectFullPath, irodsTagValue);
                        }
                    }
                }

                // find tags to add
                Set<String> tagsToAddSet = new HashSet<String>(newTagsList);
                tagsToAddSet.removeAll(existingTagList);
                String[] tagsToAdd = tagsToAddSet.toArray(new String[0]);

                for (String tag : tagsToAdd) {
                    if (tag.length() > 0) {
                        irodsTagValue = new IRODSTagValue(tag,
                                irodsAccount.getUserName());
                        if (isCollection()) {
                            irodsTaggingService.addTagToCollection(
                                    selectedObjectFullPath, irodsTagValue);
                        } else {
                            irodsTaggingService.addTagToDataObject(
                                    selectedObjectFullPath, irodsTagValue);
                        }
                    }
                }
            }

            // now update comments
            String commentStr = textareaInfoComments.getText();
            if (commentStr != null && !commentStr.isEmpty()) {

                // update comments
                irodsTagValue = new IRODSTagValue(commentStr,
                        irodsAccount.getUserName());
                if (isCollection()) {
                    irodsTaggingService.checkAndUpdateDescriptionOnCollection(
                            selectedObjectFullPath, irodsTagValue);
                } else {
                    irodsTaggingService.checkAndUpdateDescriptionOnDataObject(
                            selectedObjectFullPath, irodsTagValue);
                }
            } else {
                // remove all comments
                if (isCollection()) {
                    irodsTagValue = irodsTaggingService
                            .getDescriptionOnCollectionForLoggedInUser(selectedObjectFullPath);
                    if (irodsTagValue != null) {
                        irodsTaggingService.deleteDescriptionFromCollection(
                                selectedObjectFullPath, irodsTagValue);
                    }
                } else {
                    irodsTagValue = irodsTaggingService
                            .getDescriptionOnDataObjectForLoggedInUser(selectedObjectFullPath);
                    if (irodsTagValue != null) {
                        irodsTaggingService.deleteDescriptionFromDataObject(
                                selectedObjectFullPath, irodsTagValue);
                    }
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Tags and Comments Sucessfully Updated",
                    "Update Tags and Comments", JOptionPane.PLAIN_MESSAGE);

        } catch (CatNoAccessException cna) {
            log.error("no access to collection for tagging", cna);
            JOptionPane.showMessageDialog(this,
                    "Insufficient privilages to update this data");

        } catch (JargonException ex) {
            Exceptions.printStackTrace(ex);
            JOptionPane.showMessageDialog(this,
                    "Update of Tags and Comments Failed");
        }
    }// GEN-LAST:event_btnUpdateTagsCommentsActionPerformed

    private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }// GEN-LAST:event_btnCloseActionPerformed

    private void btnMetadataClearActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMetadataClearActionPerformed
    }// GEN-LAST:event_btnMetadataClearActionPerformed

    private void btnMetadataDeleteActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMetadataDeleteActionPerformed
    }// GEN-LAST:event_btnMetadataDeleteActionPerformed

    private void btnRefreshActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshActionPerformed

        if (tabbedpanelMain.getSelectedComponent() == pnlInfoTab) {
            initializeFileInfo();
        } else if (tabbedpanelMain.getSelectedComponent() == pnlMetadataTab) {
            initMetadataInfo();
        } else { // permissions tab
            initPermissionInfo();
        }
    }// GEN-LAST:event_btnRefreshActionPerformed

    private void btnPermissionsSaveActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPermissionsSaveActionPerformed
    }// GEN-LAST:event_btnPermissionsSaveActionPerformed

    private void btnAddSharePermissionsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddSharePermissionsActionPerformed

        PermissionsTableModel model = (PermissionsTableModel) tablePermissions.getModel();

        AddPermissionsDialog addPermissionsDialog = new AddPermissionsDialog(
                this,
                true,
                selectedObjectFullPath,
                isCollection(),
                irodsFileSystem,
                irodsAccount,
                model);

        addPermissionsDialog.setLocation((int) this.getLocation().getX(),
                (int) this.getLocation().getY());
        addPermissionsDialog.setVisible(true);

//            UserFilePermission userFilePermission = addPermissionsDialog
//                    .getPermissionToAdd();
    }// GEN-LAST:event_btnAddSharePermissionsActionPerformed

    private void btnDeleteSharePermissionsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteSharePermissionsActionPerformed

        int[] selectedRows = tablePermissions.getSelectedRows();
        int numRowsSelected = selectedRows.length;

        String msg1 = "Are you sure you wish to delete the selected permission?";
        String msg2 = "Permission Deleted Successfully";
        if (selectedRows.length > 1) {
            msg1 = "Are you sure you wish to delete the selected permissions?";
            msg2 = "Permissions Deleted Successfully";
        }

        if ((JOptionPane.showConfirmDialog(this,
                msg1,
                "Delete Metadata",
                JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

            PermissionsTableModel model = (PermissionsTableModel) tablePermissions
                    .getModel();
            try {
                // first remove from iRODS
                for (int selectedRow : selectedRows) {

                    UserFilePermission permission = model.getRow(selectedRow);

                    if (isCollection()) {
                        CollectionAO collectionAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getCollectionAO(
                                irodsAccount);
                        collectionAO.removeAccessPermissionForUser(
                                permission.getUserZone(), selectedObjectFullPath,
                                permission.getUserName(), true);
                    } else {
                        DataObjectAO dataObjectAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getDataObjectAO(
                                irodsAccount);
                        dataObjectAO.removeAccessPermissionsForUser(
                                permission.getUserZone(), selectedObjectFullPath,
                                permission.getUserName());
                    }
                }

                // now remove from table
                // have to remove rows in reverse
                for (int i = numRowsSelected - 1; i >= 0; i--) {
                    int selectedRow = selectedRows[i];
                    if (selectedRow >= 0) {
                        //                    PermissionsTableModel model = (PermissionsTableModel) tablePermissions
                        //                            .getModel();
                        model.deleteRow(selectedRow);
                        //                        btnPermissionsSave.setEnabled(true);
                    }
                }

                JOptionPane.showMessageDialog(this,
                        msg2,
                        "Delete Permission", JOptionPane.PLAIN_MESSAGE);
            } catch (JargonException ex) {
                log.error("permission delete failed", ex);
                JOptionPane.showMessageDialog(this, "Permission Delete Failed",
                        "Delete Permission", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }// GEN-LAST:event_btnDeleteSharePermissionsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMetadata;
    private javax.swing.JButton btnAddSharePermissions;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDeleteMetadata;
    private javax.swing.JButton btnDeleteSharePermissions;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JButton btnUpdateTagsComments;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
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
    private javax.swing.JPanel pnlCloseBtn;
    private javax.swing.JPanel pnlCollectionInfo;
    private javax.swing.JPanel pnlInfoCards;
    private javax.swing.JPanel pnlInfoTab;
    private javax.swing.JPanel pnlMetadataTab;
    private javax.swing.JPanel pnlMetadataTable;
    private javax.swing.JPanel pnlObjectInfo;
    private javax.swing.JPanel pnlPermissionsTab;
    private javax.swing.JPanel pnlPermissionsTable;
    private javax.swing.JPanel pnlReplicaionTools;
    private javax.swing.JPanel pnlReplication;
    private javax.swing.JPanel pnlReplicationResources;
    private javax.swing.JPanel pnlSelectedObject;
    private javax.swing.JPanel pnlTagsComments;
    private javax.swing.JScrollPane scrollReplicationResources;
    private javax.swing.JTabbedPane tabbedpanelMain;
    private javax.swing.JTable tableMetadata;
    private javax.swing.JTable tablePermissions;
    private javax.swing.JTextArea textareaInfoComments;
    private javax.swing.JTextField txtInfoTags;
    // End of variables declaration//GEN-END:variables
}
