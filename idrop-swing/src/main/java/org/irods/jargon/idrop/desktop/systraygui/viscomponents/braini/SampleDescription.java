/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents.braini;

/**
 *
 * @author mcc
 */
class SampleDescription {
    
     private String sampleId = "";
    private String samplePath = "";

    @Override
    public String toString() {
        return "SampleDescription{" + "sampleId=" + sampleId + ", samplePath=" + samplePath + '}';
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSamplePath() {
        return samplePath;
    }

    public void setSamplePath(String samplePath) {
        this.samplePath = samplePath;
    }
   
    
}
