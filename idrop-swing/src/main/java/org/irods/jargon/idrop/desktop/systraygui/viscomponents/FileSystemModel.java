package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.idrop.exceptions.IdropRuntimeException;

/**
 * Model of an underlying file system for browsing in a tree view
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class FileSystemModel implements TreeModel {

    private File root;
    private List listeners = new ArrayList();

    public FileSystemModel(final File rootDirectory) {
        root = rootDirectory;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
        File directory = (File) parent;
        String[] children = directory.list();
        return new TreeFile(directory, children[index]);
    }

    @Override
    public int getChildCount(final Object parent) {
        File file = (File) parent;
        if (file.isDirectory()) {
            String[] fileList = file.list();
            if (fileList != null) {
                return file.list().length;
            }
        }
        return 0;
    }

    @Override
    public boolean isLeaf(final Object node) {
        if (node instanceof File) {
            File file = (File) node;
            return file.isFile();
        } else {
            throw new IdropRuntimeException("unknown node type");
        }
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        File directory = (File) parent;
        File file = (File) child;
        String[] children = directory.list();
        for (int i = 0; i < children.length; i++) {
            if (file.getName().equals(children[i])) {
                return i;
            }
        }
        return -1;

    }

    @Override
    public void valueForPathChanged(final TreePath path, final Object value) {
        File oldFile = (File) path.getLastPathComponent();
        String fileParentPath = oldFile.getParent();
        String newFileName = (String) value;
        File targetFile = new File(fileParentPath, newFileName);
        oldFile.renameTo(targetFile);
        File parent = new File(fileParentPath);
        int[] changedChildrenIndices = {getIndexOfChild(parent, targetFile)};
        Object[] changedChildren = {targetFile};
        fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices,
                changedChildren);

    }

    private void fireTreeNodesChanged(final TreePath parentPath,
            final int[] indices, final Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices,
                children);
        Iterator iterator = listeners.iterator();
        TreeModelListener listener = null;
        while (iterator.hasNext()) {
            listener = (TreeModelListener) iterator.next();
            listener.treeNodesChanged(event);
        }
    }

    @Override
    public void addTreeModelListener(final TreeModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(final TreeModelListener listener) {
        listeners.remove(listener);
    }

    private class TreeFile extends File {

        public TreeFile(final File parent, final String child) {
            super(parent, child);
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
