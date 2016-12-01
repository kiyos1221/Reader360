package com.a360ground.epubreader360.EpubManipulation.Model;

/**
 * Created by Kiyos Solomon on 11/2/2016.
 */
public class Spine {
    boolean isLinear;
    String resourceId;

    public boolean isLinear() {
        return isLinear;
    }

    public void setLinear(boolean linear) {
        isLinear = linear;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
