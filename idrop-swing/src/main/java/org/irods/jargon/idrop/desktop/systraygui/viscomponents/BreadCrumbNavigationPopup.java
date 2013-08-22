/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.idrop.desktop.systraygui.iDrop;

/**
 *
 * @author lisa
 */
public class BreadCrumbNavigationPopup extends javax.swing.JPopupMenu {

    /**
     *
     */
    private static final long serialVersionUID = 7164510149337088390L;
    private String breadCrumbPath = null;
    private iDrop idropGUI;
    private String paths[];

    public BreadCrumbNavigationPopup(final iDrop idrop,
            final String breadCrumbPath) {
        super();
        idropGUI = idrop;
        this.breadCrumbPath = breadCrumbPath;
        initComponents();
    }

    private void initComponents() {

        ActionListener popupMenuListener = new PopupNavListener();

        paths = breadCrumbPath.split("/");
        int count = paths.length;
        // ignore the first entry since it just matched the leading slash
        for (int i = 1; i < count; i++) {
            JMenuItem mi = new JMenuItem(paths[i]);
            mi.addActionListener(popupMenuListener);
            this.add(mi);
        }

        // now make toolbar menu item and add as last element in popup menu
        this.add(buildToolbarPanel());
        pack();
    }

    private JPanel buildToolbarPanel() {

        JPanel pnlToolbar = new JPanel();
        javax.swing.JToolBar toolbarIrodsTree = new javax.swing.JToolBar();
        javax.swing.JButton btnGoHomeTargetTree = new javax.swing.JButton();
        javax.swing.JButton btnGoRootTargetTree = new javax.swing.JButton();
        javax.swing.JButton btnSetRootCustomTargetTree = new javax.swing.JButton();

        pnlToolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1,
                1, 1));
        pnlToolbar.setPreferredSize(new java.awt.Dimension(166, 50));
        pnlToolbar.setLayout(new java.awt.GridLayout());

        toolbarIrodsTree.setFloatable(false);
        toolbarIrodsTree.setRollover(true);
        toolbarIrodsTree.setPreferredSize(new java.awt.Dimension(166, 40));

        btnGoHomeTargetTree.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_020_home.png"))); // NOI18N
        btnGoHomeTargetTree.setMnemonic('h');
        btnGoHomeTargetTree.setText(org.openide.util.NbBundle.getMessage(
                BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnGoHomeTargetTree.text"));
        btnGoHomeTargetTree
                .setToolTipText(org.openide.util.NbBundle
                .getMessage(BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnGoHomeTargetTree.toolTipText")); // NOI18N
        btnGoHomeTargetTree.setFocusable(false);
        btnGoHomeTargetTree
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoHomeTargetTree
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoHomeTargetTree
                .addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(
                    final java.awt.event.ActionEvent evt) {
                btnGoHomeTargetTreeActionPerformed(evt);
            }
        });
        toolbarIrodsTree.add(btnGoHomeTargetTree);

        btnGoRootTargetTree.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_213_up_arrow.png"))); // NOI18N
        btnGoRootTargetTree.setMnemonic('t');
        btnGoRootTargetTree.setText(org.openide.util.NbBundle.getMessage(
                BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnGoRootTargetTree.text")); // NOI18N
        btnGoRootTargetTree
                .setToolTipText(org.openide.util.NbBundle
                .getMessage(BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnGoRootTargetTree.toolTipText")); // NOI18N
        btnGoRootTargetTree.setFocusable(false);
        btnGoRootTargetTree
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoRootTargetTree
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoRootTargetTree
                .addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(
                    final java.awt.event.ActionEvent evt) {
                btnGoRootTargetTreeActionPerformed(evt);
            }
        });
        toolbarIrodsTree.add(btnGoRootTargetTree);

        /*
        btnSetRootCustomTargetTree.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_233_direction.png"))); // NOI18N
        btnSetRootCustomTargetTree.setMnemonic('c');
        btnSetRootCustomTargetTree
                .setText(org.openide.util.NbBundle
                .getMessage(BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnSetRootCustomTargetTree.text")); // NOI18N
        btnSetRootCustomTargetTree
                .setToolTipText(org.openide.util.NbBundle
                .getMessage(BreadCrumbNavigationPopup.class,
                "BreadCrumbNavigationPopup.btnSetRootCustomTargetTree.toolTipText")); // NOI18N
        btnSetRootCustomTargetTree.setFocusable(false);
        btnSetRootCustomTargetTree
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetRootCustomTargetTree
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSetRootCustomTargetTree
                .addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(
                    final java.awt.event.ActionEvent evt) {
                btnCustomRootTargetTreeActionPerformed(evt);
            }
        });
        toolbarIrodsTree.add(btnSetRootCustomTargetTree);
        * */

        pnlToolbar.add(toolbarIrodsTree);

        setBackground(btnGoHomeTargetTree.getBackground());

        return pnlToolbar;
    }

    private void btnGoHomeTargetTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {
        // set the root path of the irods tree to root and refresh
        String homeRoot;
        if (idropGUI.getiDropCore().getIrodsAccount().isAnonymousAccount()) {
            // log.info("setting home dir to public");
            homeRoot = MiscIRODSUtils.computePublicDirectory(idropGUI
                    .getiDropCore().getIrodsAccount());
        } else {
            homeRoot = MiscIRODSUtils
                    .computeHomeDirectoryForIRODSAccount(idropGUI
                    .getiDropCore().getIrodsAccount());
        }

        idropGUI.getiDropCore().setBasePath(homeRoot);
        idropGUI.buildTargetTree(false);
        setVisible(false);
    }

    private void btnGoRootTargetTreeActionPerformed(
            final java.awt.event.ActionEvent evt) {
        idropGUI.getiDropCore().setBasePath("/");
        
        idropGUI.buildTargetTree(false);
        setVisible(false);
    }

    private void btnCustomRootTargetTreeActionPerformed(ActionEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Inner class to print information in response to popup events
    class PopupNavListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent ae) {
            String command = ae.getActionCommand();

            // get access to menuitems in popup menu to retrieve rest of path
            JMenuItem mi = (JMenuItem) ae.getSource();
            JPopupMenu pm = (JPopupMenu) mi.getParent();
            int idx = pm.getComponentIndex(mi);

            // now rebuild path
            StringBuilder fullPath = new StringBuilder("/");
            // ignore the first entry since it just matched the leading slash
            for (int i = 1; i <= idx; i++) {
                fullPath.append(paths[i]);
                fullPath.append("/");
            }
            fullPath.append(command);

            // now set tree to selected path
            idropGUI.getiDropCore().setBasePath(fullPath.toString());
            idropGUI.buildTargetTree(false);
        }
    }
}
