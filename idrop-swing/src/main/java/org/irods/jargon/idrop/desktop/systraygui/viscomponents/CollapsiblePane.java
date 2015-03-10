/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author lisa
 */
/**
 * The user-triggered collapsible panel containing the component (trigger) in
 * the titled border
 */
public class CollapsiblePane extends JPanel {

    private boolean selected;
    JPanel contentPanel_;
    HeaderPanel headerPanel_;
    final JComponent parent;

    private class HeaderPanel extends JPanel implements MouseListener {

        String text_;
        Font font;
//		BufferedImage open, closed;
        Image open, closed;
        final int OFFSET = 30, PAD = 5;

        public HeaderPanel(String text) {
            addMouseListener(this);
            text_ = text;
            //font = new Font("sans-serif", Font.PLAIN, 12);
            font = new Font("sans-serif", Font.BOLD, 12);
            // setRequestFocusEnabled(true);
            setPreferredSize(new Dimension(200, 20));
            int w = getWidth();
            int h = getHeight();

//			try {
//				Icon openIcon = UIManager.getIcon("Tree.expandedIcon");
//				open = iconToImage(openIcon);
//				Icon closedIcon = UIManager.getIcon("Tree.collapsedIcon");
//				closed = iconToImage(closedIcon);
            ImageIcon openIcon
                    = new javax.swing.ImageIcon(getClass().
                            getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_367_expand.png"));

            open = iconToImage(openIcon);
            ImageIcon closeIcon
                    = new javax.swing.ImageIcon(getClass().
                            getResource("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_368_collapse.png"));
            closed = iconToImage(closeIcon);
//                              open = ImageIO.read(new File("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_367_expand.png"));
//				closed = ImageIO.read(new File("/org/irods/jargon/idrop/desktop/systraygui/images/glyphicons_368_collapse.png"));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            if (selected) {
                g2.drawImage(open, PAD, 0, h, h, this);
            } else {
                g2.drawImage(closed, PAD, 0, h, h, this);
            }
            // Uncomment once you have your own images
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = font.getLineMetrics(text_, frc);
            float height = lm.getAscent() + lm.getDescent();
            float x = OFFSET;
            float y = (h + height) / 2 - lm.getDescent();
            g2.drawString(text_, x, y);
        }

        private Image iconToImage(Icon icon) {
            if (icon instanceof ImageIcon) {
                return ((ImageIcon) icon).getImage();
            } else {
                int w = icon.getIconWidth();
                int h = icon.getIconHeight();
                GraphicsEnvironment ge
                        = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gd = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                BufferedImage image = gc.createCompatibleImage(w, h);
                Graphics2D g = image.createGraphics();
                icon.paintIcon(null, g, 0, 0);
                g.dispose();
                return image;
            }
        }

        public void mouseClicked(MouseEvent e) {
            toggleSelection();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

    }

    public CollapsiblePane(JComponent parent, String text, JPanel panel) {
        super(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 3, 0, 3);
        gbc.weightx = 1.0;
        gbc.fill = gbc.HORIZONTAL;
        gbc.gridwidth = gbc.REMAINDER;

        selected = false;
        headerPanel_ = new HeaderPanel(text);
        headerPanel_.setBorder(new EmptyBorder(0, 0, 0, 0));

        //setBackground(new Color(200, 200, 220));
        contentPanel_ = panel;

        add(headerPanel_, gbc);
        add(contentPanel_, gbc);
        contentPanel_.setVisible(false);

        JLabel padding = new JLabel();
        gbc.weighty = 1.0;
        add(padding, gbc);

        this.parent = parent;

    }

    public void toggleSelection() {
        selected = !selected;

        if (contentPanel_.isShowing()) {
            contentPanel_.setVisible(false);
            parent.revalidate();
            parent.repaint();
        } else {
            contentPanel_.setVisible(true);
            parent.revalidate();
            parent.repaint();
        }
        validate();

        headerPanel_.repaint();
    }

}
