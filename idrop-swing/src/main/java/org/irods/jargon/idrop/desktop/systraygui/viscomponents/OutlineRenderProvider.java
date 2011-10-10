
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.netbeans.swing.outline.RenderDataProvider;

/**   
 * @author Mike Conway - DICE (www.irods.org)
 */
public class OutlineRenderProvider implements RenderDataProvider {
    private final IRODSTree tree;
    
    public OutlineRenderProvider(final IRODSTree tree) {
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
          return entry.getFormattedAbsolutePath();
      }
      
      public boolean isHtmlDisplayName(Object o) {
          return false;
      }
   }