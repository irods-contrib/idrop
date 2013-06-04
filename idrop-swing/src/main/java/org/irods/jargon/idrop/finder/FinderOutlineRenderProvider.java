package org.irods.jargon.idrop.finder;

import java.text.DateFormat;

import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.IRODSNode;
import org.netbeans.swing.outline.RenderDataProvider;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class FinderOutlineRenderProvider implements RenderDataProvider {
	private final IRODSFinderTree tree;
	private final DateFormat dateFormat = DateFormat.getDateTimeInstance();

	public FinderOutlineRenderProvider(final IRODSFinderTree tree) {
		this.tree = tree;
	}

	@Override
	public java.awt.Color getBackground(final Object o) {
		return null;
	}

	@Override
	public String getDisplayName(final Object o) {
		return o.toString();
	}

	@Override
	public java.awt.Color getForeground(final Object o) {
		return null;
	}

	@Override
	public javax.swing.Icon getIcon(final Object o) {
		return null;
	}

	@Override
	public String getTooltipText(final Object o) {
		IRODSNode node = (IRODSNode) o;
		CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) node
				.getUserObject();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h3>");
		sb.append(entry.getFormattedAbsolutePath());
		sb.append("</h3>");
		sb.append("<b>size:</b>");
		sb.append(entry.getDataSize());
		sb.append("<br/><b>last mod:</b>");
		sb.append(dateFormat.format(entry.getModifiedAt()));
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	public boolean isHtmlDisplayName(final Object o) {
		return false;
	}
}