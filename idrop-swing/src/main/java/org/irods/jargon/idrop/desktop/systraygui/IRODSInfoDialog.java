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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.irods.jargon.conveyor.core.ConveyorExecutionException;
import org.irods.jargon.conveyor.core.QueueManagerService;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.CatNoAccessException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAOImpl;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
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
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
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

    private CollectionAndDataObjectListingEntry entry;
    private final IRODSFileSystem irodsFileSystem;
    private boolean isFile;
    private final IRODSTree irodsTree;
    private IRODSInfoDialog dialog;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(IRODSTree.class);
    private List<JCheckBox> boxes = new ArrayList<JCheckBox>();

    /**
     * Creates new form IRODSInfoDialog
     *
     * @param parent
     * @param modal
     * @param irodsTree
     */
    public IRODSInfoDialog(final iDrop parent, final boolean modal,
            final IRODSTree irodsTree) {

        super(parent, modal);
        idropGUI = parent;
        irodsAccount = idropGUI.getiDropCore().irodsAccount();
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
        try {
            IRODSOutlineModel irodsFileSystemModel = (IRODSOutlineModel) irodsTree
                    .getModel();
            ListSelectionModel selectionModel = irodsTree.getSelectionModel();
            int idxStart = selectionModel.getMinSelectionIndex();

            IRODSNode selectedNode = (IRODSNode) irodsFileSystemModel
                    .getValueAt(idxStart, 0);
            selectedObjectFullPath = selectedNode.getFullPath();

            CollectionAndDataObjectListAndSearchAO listAndSearchAO = idropGUI
                    .getiDropCore()
                    .getIRODSAccessObjectFactory()
                    .getCollectionAndDataObjectListAndSearchAO(
                            idropGUI.getiDropCore().irodsAccount());
            entry = listAndSearchAO
                    .getCollectionAndDataObjectListingEntryAtGivenAbsolutePath(selectedObjectFullPath);
        } catch (JargonException ex) {
            Logger.getLogger(IRODSInfoDialog.class.getName()).log(Level.SEVERE,
                    null, ex);
            MessageManager.showError(this,
                    "Unable to find entry for given path");
            dispose();

        }

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
        lblInfoObjectName.setText(MiscIRODSUtils.abbreviateFileName(entry
                .getPathOrName()));
        lblInfoObjectName.setToolTipText(entry.getPathOrName());

        lblInfoObjectParent.setText(MiscIRODSUtils.abbreviateFileName(entry
                .getParentPath()));
        lblInfoObjectParent.setToolTipText(entry.getParentPath());

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

                        lblIcon.setIcon(new javax.swing.ImageIcon(
                                getClass()
                                .getResource(
                                        "/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_144_folder_open.png"))); // NOI18N

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
                            lblInfoCollectionObjectPath.setText(MiscIRODSUtils
                                    .abbreviateFileName(collection
                                            .getObjectPath()));
                            lblInfoCollectionObjectPath
                                    .setToolTipText(collection.getObjectPath());

                        } else {
                            lblInfoCollectionObjectPath.setText("");
                            lblInfoCollectionObjectPath
                                    .setToolTipText(collection.getObjectPath());

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
                        lblIcon.setIcon(new javax.swing.ImageIcon(
                                getClass()
                                .getResource(
                                        "/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_036_file.png"))); // NOI18N

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
                            lblInfoObjectDataPath.setText(MiscIRODSUtils
                                    .abbreviateFileName(dataObject
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
                                        entry.getParentPath(),
                                        entry.getPathOrName()));
                    }
                    tableMetadata.setModel(metadataTableModel);
                    tableMetadata.getSelectionModel().addListSelectionListener(
                            dialog);
                    tableMetadata.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent evt) {
                            if (evt.getClickCount() == 2) {
                                Point pnt = evt.getPoint();
                                int row = tableMetadata.rowAtPoint(pnt);
                                showMetadataEditForSelectedRow(row);
                            }
                        }

                    });
                    tableMetadata.validate();

                } catch (IdropException ex) {
                    Logger.getLogger(IRODSInfoDialog.class.getName()).log(
                            Level.SEVERE, null, ex);
                    idropGUI.showIdropException(ex);
                }

                dialog.setCursor(Cursor
                        .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void showMetadataEditForSelectedRow(int row) {
        MetadataTableModel model = (MetadataTableModel) tableMetadata
                .getModel();
        MetaDataAndDomainData metaDataAndDomainData = model.getRow(row);
        AvuData avuData = new AvuData();
        String attr = metaDataAndDomainData.getAvuAttribute();
        avuData.setAttribute(attr);
        String value = metaDataAndDomainData.getAvuValue();
        avuData.setValue(value);
        String unit = metaDataAndDomainData.getAvuUnit();
        avuData.setUnit(unit);

        EditMetaDataDialog editMetaDataDialog = new EditMetaDataDialog(null,
                true, row, selectedObjectFullPath, avuData, isCollection(),
                irodsFileSystem, irodsAccount, model);

        editMetaDataDialog.setLocation((int) dialog.getLocation().getX(),
                (int) dialog.getLocation().getY());
        editMetaDataDialog.setVisible(true);
    }

    private void initPermissionInfo() {
        dialog = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
                    @Override
                    public void mouseClicked(final MouseEvent evt) {
                        if (evt.getClickCount() == 2) {
                            Point pnt = evt.getPoint();
                            int row = tablePermissions.rowAtPoint(pnt);
                            PermissionsTableModel model = (PermissionsTableModel) tablePermissions
                                    .getModel();
                            UserFilePermission userFilePermission = model
                                    .getRow(row);

                            EditPermissionsDialog editPermissionsDialog = new EditPermissionsDialog(
                                    dialog, true, row, selectedObjectFullPath,
                                    userFilePermission, isCollection(),
                                    irodsFileSystem, irodsAccount, model);

                            editPermissionsDialog.setLocation((int) dialog
                                    .getLocation().getX(), (int) dialog
                                    .getLocation().getY());
                            editPermissionsDialog.setVisible(true);
                        }
                    }
                });

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
                log.error("exception checking if collection", ex);
                throw new IdropRuntimeException(ex);
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
        btnEdit.setEnabled(selectedRowCount > 0);
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
        // not implemented
    }

    // end ActionListener Methods
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "serial"})
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        pnlReplication = new javax.swing.JPanel();
        scrollReplicationResources = new javax.swing.JScrollPane();
        pnlReplicationResources = new javax.swing.JPanel();
        pnlReplicaionTools = new javax.swing.JPanel();
        btnReplicate = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pnlSelectedObject = new javax.swing.JPanel();
        lblIcon = new javax.swing.JLabel();
        lblObjectCollection = new javax.swing.JLabel();
        lblParentCollection = new javax.swing.JLabel();
        lblInfoObjectName = new javax.swing.JLabel();
        lblInfoObjectParent = new javax.swing.JLabel();
        tabbedpanelMain = new javax.swing.JTabbedPane();
        pnlInfoTab = new javax.swing.JPanel();
        pnlInfoCards = new javax.swing.JPanel();
        pnlCollectionInfo = new javax.swing.JPanel();
        lblCreatedLabel = new javax.swing.JLabel();
        lblUpdatedLabel = new javax.swing.JLabel();
        lblOwnerLabel = new javax.swing.JLabel();
        lblOwnerZone = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        lblObjectPath = new javax.swing.JLabel();
        lblDescriptionLabel = new javax.swing.JLabel();
        lblInfo1Label = new javax.swing.JLabel();
        lblInfo2Label = new javax.swing.JLabel();
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
        lblSizeLabel = new javax.swing.JLabel();
        lblCreated = new javax.swing.JLabel();
        lblModified = new javax.swing.JLabel();
        lblOwner = new javax.swing.JLabel();
        lblOwnerZoneLabel = new javax.swing.JLabel();
        lblDataPathLabel = new javax.swing.JLabel();
        lblResourceGroup = new javax.swing.JLabel();
        lblChecksum = new javax.swing.JLabel();
        lblResourceLabel = new javax.swing.JLabel();
        lblReplicaNumber = new javax.swing.JLabel();
        lblReplicationStatus = new javax.swing.JLabel();
        lblStatusLabel = new javax.swing.JLabel();
        lblTypeLabel = new javax.swing.JLabel();
        lblVersionLabel = new javax.swing.JLabel();
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
        btnEdit = new javax.swing.JButton();
        pnlPermissionsTab = new javax.swing.JPanel();
        pnlPermissionsTable = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablePermissions = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        btnAddSharePermissions = new javax.swing.JButton();
        btnDeleteSharePermissions = new javax.swing.JButton();
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

        pnlReplication.setEnabled(false);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.title")); // NOI18N
        setName("irodsInfoDialog"); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 750));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlSelectedObject.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));
        pnlSelectedObject.setPreferredSize(new java.awt.Dimension(528, 70));
        pnlSelectedObject.setLayout(new java.awt.GridBagLayout());

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_144_folder_open.png"))); // NOI18N
        lblIcon.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblIcon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSelectedObject.add(lblIcon, gridBagConstraints);

        lblObjectCollection.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblObjectCollection.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblObjectCollection.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlSelectedObject.add(lblObjectCollection, gridBagConstraints);

        lblParentCollection.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblParentCollection.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblParentCollection.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlSelectedObject.add(lblParentCollection, gridBagConstraints);

        lblInfoObjectName.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectName.text")); // NOI18N
        lblInfoObjectName.setName("objectName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlSelectedObject.add(lblInfoObjectName, gridBagConstraints);

        lblInfoObjectParent.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.parentName.text")); // NOI18N
        lblInfoObjectParent.setAutoscrolls(true);
        lblInfoObjectParent.setName("parentName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlSelectedObject.add(lblInfoObjectParent, gridBagConstraints);

        jPanel1.add(pnlSelectedObject, java.awt.BorderLayout.NORTH);

        tabbedpanelMain.setPreferredSize(new java.awt.Dimension(600, 867));
        tabbedpanelMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabbedpanelMainComponentShown(evt);
            }
        });

        pnlInfoTab.setLayout(new java.awt.BorderLayout());

        pnlInfoCards.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfoCards.setLayout(new java.awt.CardLayout());

        pnlCollectionInfo.setPreferredSize(new java.awt.Dimension(515, 500));
        pnlCollectionInfo.setLayout(new java.awt.GridBagLayout());

        lblCreatedLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCreatedLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblCreatedLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        pnlCollectionInfo.add(lblCreatedLabel, gridBagConstraints);

        lblUpdatedLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUpdatedLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblUpdatedLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        pnlCollectionInfo.add(lblUpdatedLabel, gridBagConstraints);

        lblOwnerLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOwnerLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblOwnerLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblOwnerLabel, gridBagConstraints);

        lblOwnerZone.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOwnerZone.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblOwnerZone.text")); // NOI18N
        lblOwnerZone.setName("lblOwnerZone"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblOwnerZone, gridBagConstraints);

        lblType.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblType.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblType.text")); // NOI18N
        lblType.setName("lblType"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblType, gridBagConstraints);

        lblObjectPath.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblObjectPath.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblObjectPath.text")); // NOI18N
        lblObjectPath.setName("lblObjectPath"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblObjectPath, gridBagConstraints);

        lblDescriptionLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescriptionLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblDescriptionLabel.text")); // NOI18N
        lblDescriptionLabel.setName("lblDescriptionLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblDescriptionLabel, gridBagConstraints);

        lblInfo1Label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInfo1Label.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfo1Label.text")); // NOI18N
        lblInfo1Label.setName("lblInfo1Label"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblInfo1Label, gridBagConstraints);

        lblInfo2Label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInfo2Label.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblInfo2Label.text")); // NOI18N
        lblInfo2Label.setName("lblInfo2Label"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlCollectionInfo.add(lblInfo2Label, gridBagConstraints);

        lblInfoCollectionCreatedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.created.text")); // NOI18N
        lblInfoCollectionCreatedDate.setName("created"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionCreatedDate, gridBagConstraints);

        lblInfoCollectionModifiedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.updated.text")); // NOI18N
        lblInfoCollectionModifiedDate.setName("updated"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionModifiedDate, gridBagConstraints);

        lblInfoCollectionOwner.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.owner.text")); // NOI18N
        lblInfoCollectionOwner.setName("owner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionOwner, gridBagConstraints);

        lblInfoCollectionOwnerZone.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.zone.text")); // NOI18N
        lblInfoCollectionOwnerZone.setName("zone"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionOwnerZone, gridBagConstraints);

        lblInfoCollectionType.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.fileTYpe.text")); // NOI18N
        lblInfoCollectionType.setName("fileTYpe"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionType, gridBagConstraints);

        lblInfoCollectionObjectPath.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectPath.text")); // NOI18N
        lblInfoCollectionObjectPath.setName("objectPath"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionObjectPath, gridBagConstraints);

        lblInfoCollectionDescription.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.description.text")); // NOI18N
        lblInfoCollectionDescription.setName("description"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionDescription, gridBagConstraints);

        lblInfoCollectionInfo1.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.info1.text")); // NOI18N
        lblInfoCollectionInfo1.setName("info1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionInfo1, gridBagConstraints);

        lblInfoCollectionInfo2.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.info2.text")); // NOI18N
        lblInfoCollectionInfo2.setName("info2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlCollectionInfo.add(lblInfoCollectionInfo2, gridBagConstraints);

        pnlInfoCards.add(pnlCollectionInfo, "cardCollectionInfo");

        pnlObjectInfo.setPreferredSize(null);
        pnlObjectInfo.setLayout(new java.awt.GridBagLayout());

        lblSizeLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSizeLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblSizeLabel.text")); // NOI18N
        lblSizeLabel.setName("lblSizeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 42;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblSizeLabel, gridBagConstraints);

        lblCreated.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCreated.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCreated.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblCreated.text")); // NOI18N
        lblCreated.setName("lblCreated"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblCreated, gridBagConstraints);

        lblModified.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblModified.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblModified.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblModified.text")); // NOI18N
        lblModified.setName("lblModified"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblModified, gridBagConstraints);

        lblOwner.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblOwner.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOwner.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblOwner.text")); // NOI18N
        lblOwner.setName("lblOwner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblOwner, gridBagConstraints);

        lblOwnerZoneLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblOwnerZoneLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOwnerZoneLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblOwnerZone.text")); // NOI18N
        lblOwnerZoneLabel.setName("lblOwnerZone"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblOwnerZoneLabel, gridBagConstraints);

        lblDataPathLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDataPathLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDataPathLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblDataPath.text")); // NOI18N
        lblDataPathLabel.setName("lblDataPath"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblDataPathLabel, gridBagConstraints);

        lblResourceGroup.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblResourceGroup.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblResourceGroup.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblResourceGroup.text")); // NOI18N
        lblResourceGroup.setName("lblResourceGroup"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblResourceGroup, gridBagConstraints);

        lblChecksum.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblChecksum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChecksum.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblChecksum.text")); // NOI18N
        lblChecksum.setName("lblChecksum"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblChecksum, gridBagConstraints);

        lblResourceLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblResourceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblResourceLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblResource.text")); // NOI18N
        lblResourceLabel.setName("lblResource"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblResourceLabel, gridBagConstraints);

        lblReplicaNumber.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblReplicaNumber.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReplicaNumber.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblReplicaNumber.text")); // NOI18N
        lblReplicaNumber.setName("lblReplicaNumber"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblReplicaNumber, gridBagConstraints);

        lblReplicationStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblReplicationStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReplicationStatus.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblReplicationStatus.text")); // NOI18N
        lblReplicationStatus.setName("lblReplicationStatus"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblReplicationStatus, gridBagConstraints);

        lblStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatusLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblStatusLabel.text")); // NOI18N
        lblStatusLabel.setName("lblStatusLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblStatusLabel, gridBagConstraints);

        lblTypeLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTypeLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblTypeLabel.text")); // NOI18N
        lblTypeLabel.setName("lblTypeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblTypeLabel, gridBagConstraints);

        lblVersionLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblVersionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVersionLabel.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.lblVersionLabel.text")); // NOI18N
        lblVersionLabel.setName("lblVersionLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlObjectInfo.add(lblVersionLabel, gridBagConstraints);

        lblInfoObjectSize.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.size.text")); // NOI18N
        lblInfoObjectSize.setName("size"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectSize, gridBagConstraints);

        lblInfoObjectCreatedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.createdDate.text")); // NOI18N
        lblInfoObjectCreatedDate.setName("createdDate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectCreatedDate, gridBagConstraints);

        lblInfoObjectModifiedDate.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.modifiedDate.text")); // NOI18N
        lblInfoObjectModifiedDate.setName("modifiedDate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectModifiedDate, gridBagConstraints);

        lblInfoObjectOwner.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectOwner.text")); // NOI18N
        lblInfoObjectOwner.setName("objectOwner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectOwner, gridBagConstraints);

        lblInfoObjectOwnerZone.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.ownerZone.text")); // NOI18N
        lblInfoObjectOwnerZone.setName("ownerZone"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectOwnerZone, gridBagConstraints);

        lblInfoObjectDataPath.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.dataPath.text")); // NOI18N
        lblInfoObjectDataPath.setName("dataPath"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectDataPath, gridBagConstraints);

        lblInfoObjectResourceGroup.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.resourceGroup.text")); // NOI18N
        lblInfoObjectResourceGroup.setName("resourceGroup"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectResourceGroup, gridBagConstraints);

        lblInfoObjectChecksum.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.checksum.text")); // NOI18N
        lblInfoObjectChecksum.setName("checksum"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectChecksum, gridBagConstraints);

        lblInfoObjectResource.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.resource.text")); // NOI18N
        lblInfoObjectResource.setName("resource"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectResource, gridBagConstraints);

        lblInfoObjectReplicaNumber.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.replicaNumber.text")); // NOI18N
        lblInfoObjectReplicaNumber.setName("replicaNumber"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectReplicaNumber, gridBagConstraints);

        lblInfoObjectReplicationStatus.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.replicationStatus.text")); // NOI18N
        lblInfoObjectReplicationStatus.setName("replicationStatus"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectReplicationStatus, gridBagConstraints);

        lblInfoObjectStatus.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectStatus.text")); // NOI18N
        lblInfoObjectStatus.setName("objectStatus"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectStatus, gridBagConstraints);

        lblInfoObjectType.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectType.text")); // NOI18N
        lblInfoObjectType.setName("objectType"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectType, gridBagConstraints);

        lblInfoObjectVersion.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.objectVersion.text")); // NOI18N
        lblInfoObjectVersion.setName("objectVersion"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlObjectInfo.add(lblInfoObjectVersion, gridBagConstraints);

        pnlInfoCards.add(pnlObjectInfo, "cardObjectInfo");

        pnlInfoTab.add(pnlInfoCards, java.awt.BorderLayout.CENTER);

        pnlTagsComments.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTagsComments.setLayout(new java.awt.GridBagLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlTagsComments.add(jLabel17, gridBagConstraints);

        txtInfoTags.setColumns(70);
        txtInfoTags.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.tags.text")); // NOI18N
        txtInfoTags.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.txtInfoTags.toolTipText")); // NOI18N
        txtInfoTags.setName("tags"); // NOI18N
        txtInfoTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInfoTagsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlTagsComments.add(txtInfoTags, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlTagsComments.add(jLabel18, gridBagConstraints);

        textareaInfoComments.setColumns(20);
        textareaInfoComments.setRows(5);
        textareaInfoComments.setName("comments"); // NOI18N
        jScrollPane1.setViewportView(textareaInfoComments);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlTagsComments.add(jScrollPane1, gridBagConstraints);

        btnUpdateTagsComments.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnUpdateTagsAndComments.text")); // NOI18N
        btnUpdateTagsComments.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnUpdateTagsComments.toolTipText")); // NOI18N
        btnUpdateTagsComments.setMaximumSize(null);
        btnUpdateTagsComments.setMinimumSize(null);
        btnUpdateTagsComments.setName("btnUpdateTagsAndComments"); // NOI18N
        btnUpdateTagsComments.setPreferredSize(new java.awt.Dimension(180, 37));
        btnUpdateTagsComments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTagsCommentsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlTagsComments.add(btnUpdateTagsComments, gridBagConstraints);

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
        tableMetadata.setName("tblMetadata"); // NOI18N
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
        btnAddMetadata.setPreferredSize(new java.awt.Dimension(90, 37));
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
        btnDeleteMetadata.setName("btnDeleteMetadata"); // NOI18N
        btnDeleteMetadata.setPreferredSize(new java.awt.Dimension(110, 37));
        btnDeleteMetadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteMetadataActionPerformed(evt);
            }
        });
        jPanel10.add(btnDeleteMetadata);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_030_pencil.png"))); // NOI18N
        btnEdit.setMnemonic('E');
        btnEdit.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnEditMetadata.text")); // NOI18N
        btnEdit.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnEditMetadata.toolTipText")); // NOI18N
        btnEdit.setEnabled(false);
        btnEdit.setName("btnEditMetadata"); // NOI18N
        btnEdit.setPreferredSize(new java.awt.Dimension(90, 37));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel10.add(btnEdit);

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
        tablePermissions.setName("tblACL"); // NOI18N
        tablePermissions.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(tablePermissions);

        pnlPermissionsTable.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddSharePermissions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_190_circle_plus.png"))); // NOI18N
        btnAddSharePermissions.setMnemonic('+');
        btnAddSharePermissions.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddAcl.text")); // NOI18N
        btnAddSharePermissions.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnAddAcl.toolTipText")); // NOI18N
        btnAddSharePermissions.setName("btnAddAcl"); // NOI18N
        btnAddSharePermissions.setPreferredSize(new java.awt.Dimension(90, 37));
        btnAddSharePermissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSharePermissionsActionPerformed(evt);
            }
        });
        jPanel7.add(btnAddSharePermissions);

        btnDeleteSharePermissions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_191_circle_minus.png"))); // NOI18N
        btnDeleteSharePermissions.setMnemonic('-');
        btnDeleteSharePermissions.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteAcl.text")); // NOI18N
        btnDeleteSharePermissions.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnDeleteAcl.toolTipText")); // NOI18N
        btnDeleteSharePermissions.setEnabled(false);
        btnDeleteSharePermissions.setName("btnDeleteAcl"); // NOI18N
        btnDeleteSharePermissions.setPreferredSize(new java.awt.Dimension(110, 37));
        btnDeleteSharePermissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSharePermissionsActionPerformed(evt);
            }
        });
        jPanel7.add(btnDeleteSharePermissions);

        pnlPermissionsTable.add(jPanel7, java.awt.BorderLayout.SOUTH);

        pnlPermissionsTab.add(pnlPermissionsTable, java.awt.BorderLayout.CENTER);

        tabbedpanelMain.addTab(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.pnlPermissionsTab.TabConstraints.tabTitle"), pnlPermissionsTab); // NOI18N

        jPanel1.add(tabbedpanelMain, java.awt.BorderLayout.CENTER);

        pnlCloseBtn.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_081_refresh.png"))); // NOI18N
        btnRefresh.setMnemonic('r');
        btnRefresh.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnRefresh.text")); // NOI18N
        btnRefresh.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnRefresh.toolTipText")); // NOI18N
        btnRefresh.setMaximumSize(null);
        btnRefresh.setMinimumSize(null);
        btnRefresh.setName("btnRefresh"); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(110, 37));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnlCloseBtn.add(btnRefresh);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_193_circle_ok.png"))); // NOI18N
        btnClose.setMnemonic('c');
        btnClose.setText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnOk.text")); // NOI18N
        btnClose.setToolTipText(org.openide.util.NbBundle.getMessage(IRODSInfoDialog.class, "IRODSInfoDialog.btnOk.toolTipText")); // NOI18N
        btnClose.setMaximumSize(null);
        btnClose.setMinimumSize(null);
        btnClose.setName("btnOk"); // NOI18N
        btnClose.setPreferredSize(new java.awt.Dimension(90, 37));
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

    private void txtInfoTagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInfoTagsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInfoTagsActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnEditActionPerformed

        int idx = tableMetadata.getSelectionModel().getMinSelectionIndex();
        if (idx == -1) {
            return;
        }

        this.showMetadataEditForSelectedRow(idx);

    }// GEN-LAST:event_btnEditActionPerformed

    private void btnReplicateActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnReplicateActionPerformed
        List<Resource> currentResources;
        int replicatedCount = 0;
        // build a list of the current resources, if a data object, will be used
        // to decide what to replicate
        try {
            currentResources = buildCurrentResourcesList();
        } catch (IdropException ex) {
            Logger.getLogger(IRODSInfoDialog.class.getName()).log(Level.SEVERE,
                    null, ex);
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
                    QueueManagerService qms = idropGUI.getiDropCore()
                            .getConveyorService().getQueueManagerService();
                    Transfer transfer = new Transfer();
                    if (isFile && !foundResource) {
                        log.info("file not yet replicated to resource");

                        replicatedCount++;

                        transfer.setTransferType(TransferType.REPLICATE);
                        transfer.setResourceName(checkBox.getText());
                        transfer.setIrodsAbsolutePath(entry
                                .getFormattedAbsolutePath());

                    } else if (!isFile) {
                        log.info("this is a collection, do the replication");
                        replicatedCount++;

                        transfer.setTransferType(TransferType.REPLICATE);
                        transfer.setIrodsAbsolutePath(entry
                                .getFormattedAbsolutePath());
                        transfer.setResourceName(checkBox.getText());

                    }

                    qms.enqueueTransferOperation(transfer,
                            idropGUI.getIrodsAccount());

                } catch (ConveyorExecutionException ex) {
                    Logger.getLogger(IRODSInfoDialog.class.getName()).log(
                            Level.SEVERE, null, ex);
                    idropGUI.showIdropException(ex);
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

        dispose();

    }// GEN-LAST:event_btnReplicateActionPerformed

    private void tabbedpanelMainComponentShown(
            final java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_tabbedpanelMainComponentShown
        // TODO add your handling code here:
    }// GEN-LAST:event_tabbedpanelMainComponentShown

    private void pnlReplicationComponentShown(
            final java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_pnlReplicationComponentShown
        setUpReplicationData();
    }// GEN-LAST:event_pnlReplicationComponentShown

    private void btnAddMetadataActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddMetadataActionPerformed
        MetadataTableModel model = (MetadataTableModel) tableMetadata
                .getModel();

        AddMetadataDialog addMetadataDialog = new AddMetadataDialog(this, true,
                selectedObjectFullPath, isCollection(), irodsFileSystem,
                irodsAccount, model);

        addMetadataDialog.setLocation((int) this.getLocation().getX(),
                (int) this.getLocation().getY());
        addMetadataDialog.setVisible(true);
    }// GEN-LAST:event_btnAddMetadataActionPerformed

    private void btnDeleteMetadataActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteMetadataActionPerformed

        if ((JOptionPane.showConfirmDialog(this,
                "Are you sure you wish to delete the selected metadata?",
                "Delete Metadata", JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

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
                        "Metadata Deleted Successfully", "Delete Metadata",
                        JOptionPane.PLAIN_MESSAGE);

            } catch (JargonException ex) {
                log.error("metadata delete failed", ex);
                JOptionPane.showMessageDialog(this, "Metadata Delete Failed",
                        "Delete Metadata", JOptionPane.PLAIN_MESSAGE);
            } finally {
                irodsFileSystem.closeAndEatExceptions();
            }

        }
    }// GEN-LAST:event_btnDeleteMetadataActionPerformed

    private void setUpReplicationData() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    IRODSFileService irodsFileService = new IRODSFileService(
                            idropGUI.getIrodsAccount(), idropGUI.getiDropCore()
                            .getIrodsFileSystem());
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
                    Logger.getLogger(IRODSInfoDialog.class.getName()).log(
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
                        idropGUI.getIrodsAccount(), idropGUI.getiDropCore()
                        .getIrodsFileSystem());
                currentResources = irodsFileService.getResourcesForDataObject(
                        entry.getParentPath(), entry.getPathOrName());
            } catch (IdropException ex) {
                Logger.getLogger(IRODSInfoDialog.class.getName()).log(
                        Level.SEVERE, null, ex);
                throw new IdropException(ex);
            }
        } else {
            currentResources = new ArrayList<Resource>();
        }
        return currentResources;
    }

    private void btnUpdateTagsCommentsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateTagsCommentsActionPerformed

        TaggingServiceFactory taggingServiceFactory = null;

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

    private void btnRefreshActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshActionPerformed

        if (tabbedpanelMain.getSelectedComponent() == pnlInfoTab) {
            initializeFileInfo();
        } else if (tabbedpanelMain.getSelectedComponent() == pnlMetadataTab) {
            initMetadataInfo();
        } else { // permissions tab
            initPermissionInfo();
        }
    }// GEN-LAST:event_btnRefreshActionPerformed

    private void btnAddSharePermissionsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddSharePermissionsActionPerformed

        PermissionsTableModel model = (PermissionsTableModel) tablePermissions
                .getModel();

        AddPermissionsDialog addPermissionsDialog = new AddPermissionsDialog(
                this, true, selectedObjectFullPath, isCollection(),
                irodsFileSystem, irodsAccount, model);

        addPermissionsDialog.setLocation((int) this.getLocation().getX(),
                (int) this.getLocation().getY());
        addPermissionsDialog.setVisible(true);

        // UserFilePermission userFilePermission = addPermissionsDialog
        // .getPermissionToAdd();
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

        if ((JOptionPane.showConfirmDialog(this, msg1, "Delete Metadata",
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
                                permission.getUserZone(),
                                selectedObjectFullPath,
                                permission.getUserName(), true);
                    } else {
                        DataObjectAO dataObjectAO = irodsFileSystem
                                .getIRODSAccessObjectFactory().getDataObjectAO(
                                        irodsAccount);
                        dataObjectAO.removeAccessPermissionsForUser(
                                permission.getUserZone(),
                                selectedObjectFullPath,
                                permission.getUserName());
                    }
                }

                // now remove from table
                // have to remove rows in reverse
                for (int i = numRowsSelected - 1; i >= 0; i--) {
                    int selectedRow = selectedRows[i];
                    if (selectedRow >= 0) {
                        model.deleteRow(selectedRow);
                    }
                }

                JOptionPane.showMessageDialog(this, msg2, "Delete Permission",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (JargonException ex) {
                log.error("permission delete failed", ex);
                JOptionPane.showMessageDialog(this, "Permission Delete Failed",
                        "Delete Permission", JOptionPane.PLAIN_MESSAGE);
            } finally {
                irodsFileSystem.closeAndEatExceptions();
            }
        }
    }// GEN-LAST:event_btnDeleteSharePermissionsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMetadata;
    private javax.swing.JButton btnAddSharePermissions;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDeleteMetadata;
    private javax.swing.JButton btnDeleteSharePermissions;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JButton btnUpdateTagsComments;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblChecksum;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblCreatedLabel;
    private javax.swing.JLabel lblDataPathLabel;
    private javax.swing.JLabel lblDescriptionLabel;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblInfo1Label;
    private javax.swing.JLabel lblInfo2Label;
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
    private javax.swing.JLabel lblModified;
    private javax.swing.JLabel lblObjectCollection;
    private javax.swing.JLabel lblObjectPath;
    private javax.swing.JLabel lblOwner;
    private javax.swing.JLabel lblOwnerLabel;
    private javax.swing.JLabel lblOwnerZone;
    private javax.swing.JLabel lblOwnerZoneLabel;
    private javax.swing.JLabel lblParentCollection;
    private javax.swing.JLabel lblReplicaNumber;
    private javax.swing.JLabel lblReplicationStatus;
    private javax.swing.JLabel lblResourceGroup;
    private javax.swing.JLabel lblResourceLabel;
    private javax.swing.JLabel lblSizeLabel;
    private javax.swing.JLabel lblStatusLabel;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblTypeLabel;
    private javax.swing.JLabel lblUpdatedLabel;
    private javax.swing.JLabel lblVersionLabel;
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
