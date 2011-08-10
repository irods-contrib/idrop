/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * iDropLiteApplet.java
 *
 * Created on Jun 7, 2011, 4:10:11 PM
 */

package org.irods.jargon.idrop.lite;

import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.util.logging.Logger;
import javax.swing.JToggleButton;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.datautils.datacache.DataCacheServiceImpl;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;
import org.netbeans.swing.outline.Outline;

import org.slf4j.LoggerFactory;

/**
 *
 * @author lisa
 */
public class iDropLiteApplet extends javax.swing.JApplet implements DropTargetListener, TransferStatusCallbackListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);
    private final Integer defaultLoginMode = -1;
    private iDropLiteCore iDropCore = null;
    private IRODSAccount irodsAccount = null;
    private LocalFileTree fileTree = null;
    private IRODSTree irodsTree = null;
    private Integer mode;
    private String host;
    private Integer port;
    private String zone;
    private String user;
    private String defaultStorageResource;
    private String tempPswd;
    private String absPath;
    IRODSFileSystem irodsFileSystem = null;

    /** Initializes the applet iDropLiteApplet */
    public void init() {
        try {
   
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
               	
                    getAppletParams();         
                    if(doStartup()) {
                    	initComponents();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    protected void getAppletParams() {

    	try {
    		this.mode = Integer.parseInt(getParameter("mode"));
    	} catch (Exception ex) {
            this.mode = defaultLoginMode;
        }
    		
    	try {
    		this.host = getParameter("host");
    		this.port = Integer.parseInt(getParameter("port"));
    		this.user = getParameter("user");
    		this.zone = getParameter("zone");
    		this.defaultStorageResource = getParameter("defaultStorageResource");
    		this.tempPswd = getParameter("password");
    		this.absPath = getParameter("absPath");

            log.debug("creating account with applet params");
            log.info("mode:{}", mode);
            log.info("host:{}", host);
            log.info("port:{}", port);
            log.info("user:{}", user);
            log.info("zone:{}", zone);
            log.info("resource:{}", defaultStorageResource);
            log.info("absPath:{}", absPath);
        } catch (Exception ex) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
            showIdropException(ex);
        }
                
    }
    
    private boolean retrievePermAccount()
    {
    	String pswd = null;
    	
    	DataCacheServiceImpl dataCache = new DataCacheServiceImpl();
    	try {
			dataCache.setIrodsAccessObjectFactory(irodsFileSystem.getIRODSAccessObjectFactory());
		} catch (JargonException e1) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e1);
		}
    	dataCache.setIrodsAccount(irodsAccount);
    	
    	try {
    		log.info("sending user name and key user:{}", user);
			pswd = dataCache.retrieveStringValueFromCache(user, tempPswd);
			irodsFileSystem.closeAndEatExceptions();
			this.irodsAccount = new IRODSAccount(host, port, user, pswd, absPath, zone, defaultStorageResource);
		} catch (JargonException e2) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, e2);
			return false;
		}
		
		return true;
    }
    
    private boolean createPermAccount()
    {
    	this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);
    	
    	return true;
    }


    private boolean processLogin()  {

    	// do different logins depending on which mode is used
    	// 0 - Hard-coded permanent password - just use this password to create and IRODS Account
    	// 1 - Temporary password supplied - use this password to retrieve permanent password from cache file in cacheServiceTempDir
    	
    	switch(this.mode) {
    	
    	case 1:
    		log.info("processLogin: retrieving permanent password...");
    		if(!retrievePermAccount()) {
    			showMessageFromOperation("Temporary Password Mode: login error - unable to log in, or invalid user id");
    			return false;
    		}
    		break;
    	case 0:
    		log.info("processLogin: creating account with provided permanent password...");
    		if(!createPermAccount()) {
    			showMessageFromOperation("Permanent Password Mode: login error - unable to log in, or invalid user id");
    			return false;
    		}
    		break;
    	default:
    		showMessageFromOperation("Unsupported Login Mode");
    		return false;
    	
    	}
    	
        
        try {

            final UserAO userAO = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
            iDropCore.setIrodsAccount(irodsAccount);
            iDropCore.setIrodsFileSystem(irodsFileSystem);
        } catch (JargonException ex) {
            if (ex.getMessage().indexOf("Connection refused") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else if (ex.getMessage().indexOf("Connection reset") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else if (ex.getMessage().indexOf("io exception opening socket") > -1) {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("Cannot connect to the server, is it down?");
                return false;
            } else {
                Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                showMessageFromOperation("login error - unable to log in, or invalid user id");
                return false;
            }
        } finally {
            if (irodsFileSystem != null) {
                try {
                    irodsFileSystem.close();
                } catch (JargonException ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }


    protected boolean doStartup() {

        log.info("initiating startup sequence...");
        
        log.info("creating irods file system instance...");
        try {
			irodsFileSystem = IRODSFileSystem.instance();
		} catch (JargonException ex) {
			Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
		}
        
        log.info("creating temporary irods account...");
        this.irodsAccount = new IRODSAccount(host, port, user, tempPswd, absPath, zone, defaultStorageResource);
        
        log.info("creating idropCore...");
        iDropCore = new iDropLiteCore();

        if(!processLogin()) {
        	return false;
        }

        buildTargetTree();
        
        try {

        	DataTransferOperations dataTransferOperations = irodsFileSystem.getIRODSAccessObjectFactory()
        	.getDataTransferOperations(irodsAccount);
        	iDropCore.setTransferManager(dataTransferOperations);
        }
        catch(JargonException ex) {
        	Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    public void buildTargetTree() {
        log.info("building tree to look at staging resource");
        final iDropLiteApplet gui = this;

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                CollectionAndDataObjectListingEntry root = new CollectionAndDataObjectListingEntry();

                log.info("using root path, no login preset");
                root.setPathOrName("/");

                log.info("building new iRODS tree");
                try {
                    if (irodsTree == null) {
                        irodsTree = new IRODSTree(gui);
                        IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(),
                                getiDropCore().getIrodsFileSystem(), irodsTree);
                        irodsTree.setRefreshingTree(true);
                        //irodsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    }
                    IRODSNode rootNode = new IRODSNode(root, getIrodsAccount(), getiDropCore().getIrodsFileSystem(),
                            irodsTree);

                    IRODSFileSystemModel irodsFileSystemModel = new IRODSFileSystemModel(rootNode, getIrodsAccount());
                    IRODSOutlineModel mdl = new IRODSOutlineModel(gui, irodsFileSystemModel, new IRODSRowModel(), true,
                            "File System");
                    irodsTree.setModel(mdl);

                    /*
                     * IrodsTreeListenerForBuildingInfoPanel treeListener = new
                     * IrodsTreeListenerForBuildingInfoPanel(gui); irodsTree.addTreeExpansionListener(treeListener);
                     * irodsTree.addTreeSelectionListener(treeListener); // preset to display root tree node
                     * irodsTree.setSelectionRow(0);
                     */
                } catch (Exception ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IdropRuntimeException(ex);
                }

                scrollIrodsTree.setViewportView(getTreeStagingResource());
                try {
                    TreePath selectedPath = TreeUtils.buildTreePathForIrodsAbsolutePath(irodsTree, absPath);
                    irodsTree.expandPath(selectedPath);
                    //irodsTree.getSelectionModel().setSelectionInterval(10, 12);
                    Rectangle rect = irodsTree.getPathBounds(selectedPath);
                    scrollIrodsTree.getViewport().scrollRectToVisible(rect);
                }
                catch(IdropException ex) {
                    Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*
                 * TreePath currentPath;
                 *
                 * if (currentPaths != null) { while (currentPaths.hasMoreElements()) { currentPath = (TreePath)
                 * currentPaths.nextElement(); log.debug("expanding tree path:{}", currentPath);
                 * irodsTree.expandPath(currentPath); } }
                 */
                irodsTree.setRefreshingTree(false);

                getiDropCore().getIrodsFileSystem().closeAndEatExceptions(iDropCore.getIrodsAccount());
                DropTarget dt = new DropTarget();
                dt.setComponent(irodsTree.getEditorComponent());
                //dt.addDropTargetListener(this);
            }
        });
    }

    public IRODSAccount getIrodsAccount() {
        synchronized (this) {
            return this.iDropCore.getIrodsAccount();
        }
    }

    public JToggleButton getToggleIrodsDetails() {
        // FIX ME: NEED TO IMPLEMENT
        return new JToggleButton();
    }

    public void showIdropException(Exception idropException) {
        JOptionPane.showMessageDialog(this, idropException.getMessage(), "iDROP Exception", JOptionPane.WARNING_MESSAGE);
    }

    public void showMessageFromOperation(final String messageFromOperation) {

        final iDropLiteApplet thisIdropGui = this;
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(thisIdropGui, messageFromOperation, "iDROP Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    
    @Override
	public void statusCallback(final TransferStatus ts) {
    	
    	this.pbTransferStatus.setMaximum((int) ts.getTotalSize());
    	this.pbTransferStatus.setValue((int) ts.getBytesTransfered());
    	//this.pbTransferStatus.setMaximum((int) ts.getTotalFilesToTransfer());
    	//this.pbTransferStatus.setValue((int) ts.getTotalFilesTransferredSoFar());
    	log.info("transfer status callback to iDropLiteApplet:{}", ts);

    	java.awt.EventQueue.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    			// on initiation, clear and reset the status bar info
    			/*
            	lblTransferFilesCounts.setText("Files: " + ts.getTotalFilesTransferredSoFar() + " / "
                    + ts.getTotalFilesToTransfer());
            	lblTransferByteCounts.setText("Current File (kb):" + (ts.getBytesTransfered() / 1024) + " / "
                    + (ts.getTotalSize() / 1024));
    			 */
    			lblTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
    		}
    	});
		
	}


	@Override
	public void overallStatusCallback(final TransferStatus ts) {
		
		IRODSOutlineModel irodsTreeModel = (IRODSOutlineModel) irodsTree.getModel();
        try {
            irodsTreeModel.notifyCompletionOfOperation(irodsTree, ts);
            // if a get callback on completion, notify the local tree model
            if (ts.getTransferType() == TransferStatus.TransferType.GET
                    && ts.getTransferState() == TransferStatus.TransferState.OVERALL_COMPLETION) {
                ((LocalFileSystemModel) getFileTree().getModel()).notifyCompletionOfOperation(getFileTree(), ts);
            }
        } catch (IdropException ex) {
            Logger.getLogger(iDropLiteApplet.class.getName()).log(Level.SEVERE, null, ex);
            this.showIdropException(ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // on initiation, clear and reset the status bar info
            	/*
                lblTransferType.setText(ts.getTransferType().name());
                lblTransferFilesCounts.setText("Files: " + ts.getTotalFilesTransferredSoFar() + " / "
                        + ts.getTotalFilesToTransfer());
                lblTransferByteCounts.setText("Bytes (kb):" + (ts.getBytesTransfered() / 1024) + " / "
                        + (ts.getTotalSize() / 1024));
                */
            	lblTransferFileName.setText(abbreviateFileName(ts.getSourceFileAbsolutePath()));
            }
        });
		
	}

	
	private final String abbreviateFileName(final String fileName) {

        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }

        StringBuilder sb = new StringBuilder();
        if (fileName.length() < 100) {
            sb.append(fileName);
        } else {
            // gt 100 bytes, redact
            sb.append(fileName.substring(0, 50));
            sb.append(" ... ");
            sb.append(fileName.substring(fileName.length() - 50));
        }

        return sb.toString();

    }


    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new javax.swing.JPanel();
        pnlTools = new javax.swing.JPanel();
        tabIrodsViews = new javax.swing.JTabbedPane();
        pnlIrodsTreeView = new javax.swing.JPanel();
        pnlIrodsTreeToolbar = new javax.swing.JPanel();
        bntRefreshIrodsTree = new javax.swing.JButton();
        pnlTreeMaster = new javax.swing.JPanel();
        scrollIrodsTree = new javax.swing.JScrollPane();
        pnlSearch = new javax.swing.JPanel();
        pnlTransferStatus = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTransferFileName = new javax.swing.JLabel();
        pbTransferStatus = new javax.swing.JProgressBar();

        setPreferredSize(new java.awt.Dimension(600, 400));

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.setMinimumSize(new java.awt.Dimension(200, 200));
        pnlMain.setPreferredSize(new java.awt.Dimension(600, 400));
        pnlMain.setRequestFocusEnabled(false);
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlTools.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        org.jdesktop.layout.GroupLayout pnlToolsLayout = new org.jdesktop.layout.GroupLayout(pnlTools);
        pnlTools.setLayout(pnlToolsLayout);
        pnlToolsLayout.setHorizontalGroup(
            pnlToolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 592, Short.MAX_VALUE)
        );
        pnlToolsLayout.setVerticalGroup(
            pnlToolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        pnlMain.add(pnlTools, java.awt.BorderLayout.NORTH);

        tabIrodsViews.setMinimumSize(new java.awt.Dimension(300, 300));
        tabIrodsViews.setPreferredSize(new java.awt.Dimension(600, 400));
        tabIrodsViews.setRequestFocusEnabled(false);

        pnlIrodsTreeView.setLayout(new java.awt.BorderLayout());

        bntRefreshIrodsTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bntRefreshIrodsTree.setLabel("Refresh");
        pnlIrodsTreeToolbar.add(bntRefreshIrodsTree);
        bntRefreshIrodsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	bntRefreshIrodsTreeActionPerformed(evt);
            }
        });

        pnlIrodsTreeView.add(pnlIrodsTreeToolbar, java.awt.BorderLayout.NORTH);

        pnlTreeMaster.setLayout(new java.awt.BorderLayout());
        pnlIrodsTreeView.add(pnlTreeMaster, java.awt.BorderLayout.SOUTH);
        pnlIrodsTreeView.add(scrollIrodsTree, java.awt.BorderLayout.CENTER);

        tabIrodsViews.addTab("iRODS Tree View", pnlIrodsTreeView);

        pnlSearch.setName("pnlSearch"); // NOI18N

        org.jdesktop.layout.GroupLayout pnlSearchLayout = new org.jdesktop.layout.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 575, Short.MAX_VALUE)
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 271, Short.MAX_VALUE)
        );

        tabIrodsViews.addTab("Search", pnlSearch);

        pnlMain.add(tabIrodsViews, java.awt.BorderLayout.CENTER);

        pnlTransferStatus.setPreferredSize(new java.awt.Dimension(600, 75));
        pnlTransferStatus.setRequestFocusEnabled(false);
        pnlTransferStatus.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel1.setText("Current File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 0, 0);
        pnlTransferStatus.add(jLabel1, gridBagConstraints);

        lblTransferFileName.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        lblTransferFileName.setMaximumSize(new java.awt.Dimension(200, 22));
        lblTransferFileName.setMinimumSize(new java.awt.Dimension(100, 22));
        lblTransferFileName.setPreferredSize(new java.awt.Dimension(200, 22));
        lblTransferFileName.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 15, 0, 0);
        pnlTransferStatus.add(lblTransferFileName, gridBagConstraints);

        pbTransferStatus.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        pbTransferStatus.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pbTransferStatus.setMinimumSize(new java.awt.Dimension(100, 24));
        pbTransferStatus.setPreferredSize(new java.awt.Dimension(300, 24));
        pbTransferStatus.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 40.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 15);
        pnlTransferStatus.add(pbTransferStatus, gridBagConstraints);

        pnlMain.add(pnlTransferStatus, java.awt.BorderLayout.PAGE_END);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>                        


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntRefreshIrodsTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblTransferFileName;
    private javax.swing.JProgressBar pbTransferStatus;
    private javax.swing.JPanel pnlIrodsTreeToolbar;
    private javax.swing.JPanel pnlIrodsTreeView;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTools;
    private javax.swing.JPanel pnlTransferStatus;
    private javax.swing.JPanel pnlTreeMaster;
    private javax.swing.JScrollPane scrollIrodsTree;
    private javax.swing.JTabbedPane tabIrodsViews;
    // End of variables declaration//GEN-END:variables

    public IRODSTree getIrodsTree() {
        return irodsTree;
    }

    public iDropLiteCore getiDropCore() {
        return iDropCore;
    }

    public LocalFileTree getFileTree() {
        return fileTree;
    }

    public Outline getTreeStagingResource() {
        return irodsTree;
    }

    private void bntRefreshIrodsTreeActionPerformed(java.awt.event.ActionEvent evt) {
    	// FIX ME: get current view of irods tree and pass to buildTargetTree
        buildTargetTree();
    }


	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void drop(DropTargetDropEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
