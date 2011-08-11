/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.commons;

import java.awt.Point;
import javax.swing.TransferHandler;
import org.irods.jargon.core.exception.JargonException;

/**
 *
 * @author lisa
 */
public interface IdropClient {

    public IRODSTree getIrodsTree();

    public iDropCoreCommon getiDropCore();

    public int showConfirmDialog(String toString, String string, int YES_NO_OPTION);

    public void showMessageFromOperation(String string);

    public void showIdropException(JargonException ex);

    public void showIdropException(Exception ex);

    public Point getLocation();

    public int getWidth();


    public int getHeight();

    public void showIdropException(IdropException ex);

    public LocalFileTree getFileTree();

    public IRODSTreeTransferHandler getIRODSTreeTransferHandler(String modelType);

    public LocalTreeTransferHandler getLocalTreeTransferHandler();

}
