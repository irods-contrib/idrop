/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author lisa
 */
public class HyperLinkButton extends JButton {
    
    public HyperLinkButton() {
        setup();
    }
    
    public HyperLinkButton(String text) {
        setText(text);
        setup();
    }
    
    // set all of the attributes to make this button look like a hyperlink
    private void setup(){
        Font font = getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setFont(font.deriveFont(attributes));
        setForeground(Color.blue);
        Border b = new EmptyBorder(0,0,0,0);
        setBorder(b);
        
        addMouseListener(new MouseAdapter() {  
            public void mouseEntered(MouseEvent me) {  
                setCursor(new Cursor(Cursor.HAND_CURSOR));  
            }  
            public void mouseExited(MouseEvent me) {  
                setCursor(Cursor.getDefaultCursor());  
            }    
        });
    }
    
}
