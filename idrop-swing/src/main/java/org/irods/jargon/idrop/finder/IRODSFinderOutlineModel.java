/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.finder;

import org.irods.jargon.idrop.desktop.systraygui.viscomponents.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatus.TransferState;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;
import org.irods.jargon.idrop.desktop.systraygui.utils.TreeUtils;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.RowModel;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFinderOutlineModel extends DefaultOutlineModel {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(IRODSFinderOutlineModel.class);
    private final IRODSFileSystemModel treeModel;

    public IRODSFileSystemModel getTreeModel() {
        return treeModel;
    }

    public IRODSFinderOutlineModel(final TreeModel tm,
            final TableModel tm1, final boolean bln, final String string) {
        super(tm, tm1, bln, string);
        this.treeModel = (IRODSFileSystemModel) tm;
    }

    public IRODSFinderOutlineModel(final TreeModel tm,
            final RowModel rm, final boolean bln, final String string) {
        super(tm, rm, bln, string);
        this.treeModel = (IRODSFileSystemModel) tm;
    }

}
