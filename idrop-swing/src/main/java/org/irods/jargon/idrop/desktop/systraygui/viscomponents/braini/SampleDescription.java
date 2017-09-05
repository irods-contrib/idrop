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
    private String experimentId = "";

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public String toString() {
        return "SampleDescription{" + "sampleId=" + sampleId + ", samplePath=" + samplePath + ", experimentId=" + experimentId + '}';
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
