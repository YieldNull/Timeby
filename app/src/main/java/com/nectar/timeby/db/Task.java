package com.nectar.timeby.db;

/**
 * Created by wsw on 2015/7/25.
 */
public class Task {
    private String phoneNumberA;
    private int startTime;
    private int endTime;

    public int getSuccessOrNot() {
        return successOrNot;
    }

    public void setSuccessOrNot(int successOrNot) {
        this.successOrNot = successOrNot;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getFoucusDegree() {
        return foucusDegree;
    }

    public void setFoucusDegree(int foucusDegree) {
        this.foucusDegree = foucusDegree;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getPhoneNumberA() {
        return phoneNumberA;
    }

    public void setPhoneNumberA(String phoneNumberA) {
        this.phoneNumberA = phoneNumberA;
    }

    private String taskContent;
    private int foucusDegree;
    private int efficiency;
    private int successOrNot;
}
