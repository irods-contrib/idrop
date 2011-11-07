
package org.irods.jargon.idrop.finder;

import org.irods.jargon.idrop.desktop.systraygui.viscomponents.*;
import java.text.DateFormat;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
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
    
      public java.awt.Color getBackground(Object o) {
          return null;
      }
      
      public String getDisplayName(Object o) {
          return o.toString();
      }
      
      public java.awt.Color getForeground(Object o) {
          return null;
      }
      
      public javax.swing.Icon getIcon(Object o) {
          return null;
      }
      
      public String getTooltipText(Object o) {
          IRODSNode node = (IRODSNode) o;
          CollectionAndDataObjectListingEntry entry = (CollectionAndDataObjectListingEntry) node.getUserObject();
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
      
      public boolean isHtmlDisplayName(Object o) {
          return false;
      }
   }