package org.irods.jargon.idrop.desktop.systraygui.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Misc iDrop utilities
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IDropUtils {

    /**
     * Given a file name, return the name up to 100 chars, otherwise, redact and
     * abbreviate
     *
     * @param fileName
     * @return
     */
    public static final String abbreviateFileName(final String fileName) {

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

    /**
     * Create a label with a HTML link
     * @param text
     * @param URL
     * @param toolTip
     * @return 
     */
    public static JLabel linkify(final String text, String URL, String toolTip) {
        URI temp = null;
        try {
            temp = new URI(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final URI uri = temp;
        final JLabel link = new JLabel();
        link.setText("<HTML><FONT color=\"#000099\">" + text + "</FONT></HTML>");
        if (!toolTip.equals("")) {
            link.setToolTipText(toolTip);
        }
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseListener() {
            public void mouseExited(MouseEvent arg0) {
                link.setText("<HTML><FONT color=\"#000099\">" + text + "</FONT></HTML>");
            }

            public void mouseEntered(MouseEvent arg0) {
                link.setText("<HTML><FONT color=\"#000099\"><U>" + text + "</U></FONT></HTML>");
            }

            public void mouseClicked(MouseEvent arg0) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane pane = new JOptionPane("Could not open link.");
                    JDialog dialog = pane.createDialog(new JFrame(), "");
                    dialog.setVisible(true);
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
        return link;
    }
}
