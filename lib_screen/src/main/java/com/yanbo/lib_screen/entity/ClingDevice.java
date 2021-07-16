package com.yanbo.lib_screen.entity;

import org.fourthline.cling.model.meta.Device;

import java.util.Objects;

/**
 * Created by lzan13 on 2018/3/5.
 */
public class ClingDevice {
    private Device device;
    private boolean isSelected = false;

    public ClingDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }
    public String getDeviceName(){
        return device.getDetails().getFriendlyName();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClingDevice that = (ClingDevice) o;
        return isSelected == that.isSelected &&
                Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, isSelected);
    }
}
