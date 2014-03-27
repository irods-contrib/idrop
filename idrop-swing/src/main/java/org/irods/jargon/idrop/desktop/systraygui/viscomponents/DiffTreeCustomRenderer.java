package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.irods.jargon.datautils.tree.FileTreeDiffEntry;
import org.irods.jargon.datautils.tree.FileTreeNode;

/**
 * Custom renderer for the file diff tree
 * 
 * @author Mike
 */
public class DiffTreeCustomRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1425824662091848122L;

	@Override
	public Component getTreeCellRendererComponent(final JTree tree,
			final Object value, final boolean selected, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {

		FileTreeNode fileTreeNode = (FileTreeNode) value;
		FileTreeDiffEntry diffEntry = (FileTreeDiffEntry) fileTreeNode
				.getUserObject();
		StringBuilder sb = new StringBuilder();
		sb.append(diffEntry.getCollectionAndDataObjectListingEntry()
				.getNodeLabelDisplayValue());

		// Allow the original renderer to set up the label
		Component c = super.getTreeCellRendererComponent(tree, value, selected,
				expanded, diffEntry.getCollectionAndDataObjectListingEntry()
						.isDataObject(), row, hasFocus);

		if (diffEntry.isCountAsDiff() && diffEntry.isResolved()) {
			c.setForeground(resolvedForeground);
			sb.append(" :  resolved thru user action");
		} else if (diffEntry.isCountAsDiff()) {
			c.setForeground(diffForeground);
			sb.append(" : ");
			sb.append(diffEntry.getDiffType());
		} else if (diffEntry.getCountOfDiffsInChildren() > 0) {
			c.setForeground(diffChildForeground);
			sb.append(" child diffs:");
			sb.append(diffEntry.getCountOfDiffsInChildren());
		}

		// c.setName(sb.toString());
		setText(sb.toString());

		StringBuilder tt = new StringBuilder();
		tt.append("<html>");
		tt.append(diffEntry.getCollectionAndDataObjectListingEntry()
				.getFormattedAbsolutePath());
		if (diffEntry.isCountAsDiff()) {
			tt.append("<br/><h3>there was a difference here:</h3>");
			tt.append(diffEntry.getDiffType());
		} else if (!fileTreeNode.isLeaf()) {
			tt.append("<br/>there were ");
			tt.append(diffEntry.getCountOfDiffsInChildren());
			tt.append(" diffs in children nodes");
		}
		tt.append("</html>");

		setToolTipText(tt.toString());

		return c;
	}

	private Color diffForeground = Color.RED;
	private Color diffChildForeground = Color.BLUE;
	private Color resolvedForeground = Color.BLACK;
}
