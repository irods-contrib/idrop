/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.utils;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.desktop.systraygui.services.IDROPConfigurationService;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LookAndFeelManager {

    private final IDROPCore idropCore;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LookAndFeelManager.class);

    public LookAndFeelManager(final IDROPCore idropCore) {
        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }
        if (idropCore.getIdropConfig() == null) {
            throw new IllegalArgumentException("idropConfig is null in idropCore");
        }
        if (idropCore.getIdropConfigurationService() == null) {
            throw new IllegalArgumentException("idropConfigurationService in idropCore is null");
        }
        this.idropCore = idropCore;
    }

    public void setLookAndFeel(String lookAndFeelChoice) {
        String lookAndFeel = "";
        if (lookAndFeelChoice == null) {
            lookAndFeelChoice = "System";
        }
        
        log.info("setLookAndFeel to:{}", lookAndFeelChoice);

        if (lookAndFeelChoice != null) {
            try {
                idropCore.getIdropConfigurationService().updateConfig(IDROPConfigurationService.LOOK_AND_FEEL, lookAndFeelChoice);
            } catch (IdropException ex) {
                log.error("unable to update configration for look and feel");
                throw new IdropRuntimeException("unable to set prop for look and feel", ex);
            }
            if (lookAndFeelChoice.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

                //  an alternative way to set the Metal L&F is to replace the 
                // previous line with:
                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";

            } else if (lookAndFeelChoice.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();

            } else if (lookAndFeelChoice.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

            } else if (lookAndFeelChoice.equals("GTK")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

            } else if (lookAndFeelChoice.equals("Nimbus")) {

                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        lookAndFeel = info.getClassName();
                        break;
                    }
                }
            } else {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            if (lookAndFeel.equals("")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

            }


            final String finalLookAndFeel = lookAndFeel;



            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    try {
                        UIManager.setLookAndFeel(finalLookAndFeel);
                    } catch (Exception e) {
                        log.warn("unable to set look and feel to :{}", finalLookAndFeel);
                    }
                }
            });


        }
    }
}
